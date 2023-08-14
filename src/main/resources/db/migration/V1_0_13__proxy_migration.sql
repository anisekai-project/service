ALTER TABLE `anime`
    CHANGE `added_at` `created_at` datetime not null;
ALTER TABLE `anime`
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT NOW();

RENAME TABLE `anime_night` TO `broadcast`;
ALTER TABLE `broadcast`
    ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT NOW();
ALTER TABLE `broadcast`
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT NOW();

ALTER TABLE `interest`
    ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT NOW();
ALTER TABLE `interest`
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT NOW();

ALTER TABLE `torrent`
    ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT NOW();
ALTER TABLE `torrent`
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT NOW();

ALTER TABLE `user`
    ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT NOW();
ALTER TABLE `user`
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT NOW();

ALTER TABLE `watchlist`
    ADD COLUMN `order` INT NOT NULL DEFAULT 0;
ALTER TABLE `watchlist`
    ADD COLUMN `created_at` DATETIME NOT NULL DEFAULT NOW();
ALTER TABLE `watchlist`
    ADD COLUMN `updated_at` DATETIME NOT NULL DEFAULT NOW();

ALTER TABLE `watchlist`
    DROP COLUMN `state`;

UPDATE `watchlist`
SET `order` = 1
WHERE status = 'WATCHING';
UPDATE `watchlist`
SET `order` = 2
WHERE status = 'SIMULCAST';
UPDATE `watchlist`
SET `order` = 3
WHERE status = 'SIMULCAST_AVAILABLE';
UPDATE `watchlist`
SET `order` = 4
WHERE status = 'DOWNLOADED';
UPDATE `watchlist`
SET `order` = 5
WHERE status = 'DOWNLOADING';
UPDATE `watchlist`
SET `order` = 6
WHERE status = 'NOT_DOWNLOADED';
UPDATE `watchlist`
SET `order` = 7
WHERE status = 'NO_SOURCE';
UPDATE `watchlist`
SET `order` = 8
WHERE status = 'UNAVAILABLE';

DROP TABLE seasonal_selection_animes;
DROP TABLE seasonal_voter;
DROP TABLE seasonal_vote;
DROP TABLE seasonal_selection;

CREATE TABLE `seasonal_selection`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `name`       VARCHAR(255) NOT NULL,
    `closed`     BOOLEAN      NOT NULL,
    `created_at` DATETIME     NOT NULL DEFAULT NOW(),
    `updated_at` DATETIME     NOT NULL DEFAULT NOW(),
    CONSTRAINT `PK_SEASONAL_SELECTION` PRIMARY KEY (`id`),
    CONSTRAINT `UK_SEASONAL_SELECTION_NAME` UNIQUE (`name`)
);

CREATE TABLE `seasonal_voter`
(
    `seasonal_selection_id` BIGINT   NOT NULL,
    `user_id`               BIGINT   NOT NULL,
    `amount`                INT      NOT NULL,
    `created_at`            DATETIME NOT NULL DEFAULT NOW(),
    `updated_at`            DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT `PK_SEASONAL_VOTER` PRIMARY KEY (`seasonal_selection_id`, `user_id`),
    CONSTRAINT `FK_SVOTER_SEASONAL_SELECTION` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`),
    CONSTRAINT `FK_SVOTER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `seasonal_vote`
(
    `seasonal_selection_id` BIGINT   NOT NULL,
    `user_id`               BIGINT   NOT NULL,
    `anime_id`              BIGINT   NOT NULL,
    `created_at`            DATETIME NOT NULL DEFAULT NOW(),
    `updated_at`            DATETIME NOT NULL DEFAULT NOW(),
    CONSTRAINT `PK_SEASONAL_VOTE` PRIMARY KEY (`seasonal_selection_id`, `user_id`, `anime_id`),
    CONSTRAINT `FK_SVOTE_SS` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`),
    CONSTRAINT `FK_SVOTE_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`),
    CONSTRAINT `FK_SVOTE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `seasonal_selection_animes`
(
    `seasonal_selection_id` BIGINT NOT NULL,
    `animes_id`             BIGINT NOT NULL,
    CONSTRAINT `PK_SEASONAL_SELECTION_ANIMES` PRIMARY KEY (`seasonal_selection_id`, `animes_id`),
    CONSTRAINT `FK_SSA_ANIME` FOREIGN KEY (`animes_id`) REFERENCES `anime` (`id`),
    CONSTRAINT `FK_SSA_SS` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`)
);

