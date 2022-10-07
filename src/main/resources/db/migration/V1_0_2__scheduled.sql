CREATE TABLE `scheduled_event`
(
    `id`               INT AUTO_INCREMENT NOT NULL,
    `event_start_at`   DATETIME           NOT NULL,
    `anime_id`         BIGINT             NULL,
    `description`      VARCHAR(255)       NOT NULL,
    `last_episode`     INT                NOT NULL,
    `state`            VARCHAR(255)       NOT NULL,
    `notified`         BIT(1)             NOT NULL,
    CONSTRAINT `PK_SCHEDULED_EVENT` PRIMARY KEY (`id`),
    CONSTRAINT `UK_SCHEDULED_EVENT_START_AT` UNIQUE (`event_start_at`),
    CONSTRAINT `FK_SCHEDULED_EVENT_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`)
);

ALTER TABLE `watchlist` DROP CONSTRAINT `UK_WATCHLIST_MESSAGE`;

ALTER TABLE `watchlist`
    ADD COLUMN `state` VARCHAR(255) NOT NULL DEFAULT 'DONE';

ALTER TABLE `watchlist`
    MODIFY COLUMN `message_id` BIGINT NULL;

ALTER TABLE `interest`
    MODIFY COLUMN `level` VARCHAR(255) NOT NULL;


