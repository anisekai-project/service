DROP TABLE IF EXISTS `anime_night`;

CREATE TABLE `anime_night`
(
    `id`              BIGINT AUTO_INCREMENT NOT NULL,
    `event_id`        BIGINT                NULL,
    `anime_id`        BIGINT                NOT NULL,
    `amount`          BIGINT                NOT NULL,
    `first_episode`   BIGINT                NOT NULL,
    `last_episode`    BIGINT                NOT NULL,
    `status`          VARCHAR(255)          NULL,
    `image_url`       VARCHAR(255)          NULL,
    `start_date_time` DATETIME              NOT NULL,
    `end_date_time`   DATETIME              NOT NULL,
    CONSTRAINT `pk_animenight` PRIMARY KEY (`id`)
);

ALTER TABLE `anime_night`
    ADD CONSTRAINT `FK_ANIMENIGHT_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);
