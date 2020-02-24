package edu.cnm.deepdive.nasaapod.service;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class GoogleSignInRepository {

  private static Application context;

  private GoogleSignInClient client;

  private MutableLiveData<GoogleSignInAccount> account;

  private MutableLiveData<Throwable> throwable;

  //   Constructor.  REM MLD is not exposed to the user.  Rather we expose live data
  private GoogleSignInRepository() {
    account = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    GoogleSignInOptions options = new GoogleSignInOptions.Builder()
        .requestEmail()
        .requestId() // Long key that is not required
        .requestProfile()
        //  .requestIdToken(clientId) for server hosting connection not android
        .build();
    client = GoogleSignIn.getClient(context, options);
  }

//  Setter for context
  public static void setContext(Application context) {
    GoogleSignInRepository.context = context;
  }

  public static GoogleSignInRepository getInstance() {
    return InstanceHolder.INSTANCE;
  }

  //  Getters for Account and Throwable - Delete MUTABLE
  public LiveData<GoogleSignInAccount> getAccount() {
    return account;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }
  public Task<GoogleSignInAccount> refresh() {
    return client.silentSignIn()
        .addOnSuccessListener(this::update)
        .addOnFailureListener(this::update);
  }

  public void startSignIn(Activity activity, int requestCode) {
   update((GoogleSignInAccount) null);
   Intent intent = client.getSignInIntent();
   activity.startActivityForResult(intent, requestCode);
  }
  
  public Task<GoogleSignInAccount> completeSignIn(Intent data) {
    Task<GoogleSignInAccount> task = null;
    try {
      task = GoogleSignIn.getSignedInAccountFromIntent(data);
      account.setValue(task.getResult(ApiException.class));
    } catch (ApiException e) {
      update(e);
    }
    return task;
  }

  public Task<Void> signOut() {
    return client.signOut()
        .addOnCompleteListener((account) -> update((GoogleSignInAccount) null));


  }

  // Update exception
  private void update(GoogleSignInAccount account) {
    this.account.setValue(account);
    this.throwable.setValue(null);

  }
  //  Update account
  private void update(Throwable throwable) {
    this.throwable.setValue(throwable);
    this.account.setValue(null);
  }

  //  Private constructor
  private static class InstanceHolder {
    private static final GoogleSignInRepository INSTANCE = new GoogleSignInRepository();
  }
}
