CREATE TABLE IF NOT EXISTS `Apod`
(
    `apod_id`     INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    `date`        INTEGER                           NOT NULL,
    `title`       TEXT                              NOT NULL COLLATE NOCASE,
    `description` TEXT                              NOT NULL,
    `copyright`   TEXT,
    `media_type`  INTEGER                           NOT NULL,
    `url`         TEXT                              NOT NULL,
    `hd_url`      TEXT
);












