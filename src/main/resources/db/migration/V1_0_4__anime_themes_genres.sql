ALTER TABLE `anime`
    DROP COLUMN `tags`;
ALTER TABLE `anime`
    ADD COLUMN `genres` VARCHAR(255) NULL AFTER `synopsis`;
ALTER TABLE `anime`
    ADD COLUMN `themes` VARCHAR(255) NULL AFTER `genres`;

DROP TABLE `scheduled_event`;

CREATE TABLE `anime_night`
(
    `id`       BIGINT NOT NULL,
    `anime_id` BIGINT NULL,
    `amount`   BIGINT NOT NULL,
    CONSTRAINT `pk_animenight` PRIMARY KEY (`id`)
);

ALTER TABLE `anime_night`
    ADD CONSTRAINT `FK_ANIMENIGHT_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);
