ALTER TABLE `anime_night`
    ADD `image_url` VARCHAR(255) NULL;

ALTER TABLE `anime_night`
    ADD `start_time` DATETIME NOT NULL;

ALTER TABLE `anime_night`
    ADD `end_time` DATETIME NOT NULL;
