CREATE TABLE `user`
(
    `id`            BIGINT       NOT NULL,
    `username`      VARCHAR(255) NOT NULL,
    `discriminator` VARCHAR(255) NOT NULL,
    `emote`         VARCHAR(255) NULL,
    CONSTRAINT `PK_USER` PRIMARY KEY (`id`),
    CONSTRAINT `UK_USER_DISCORD` UNIQUE (`username`, `discriminator`)
);

CREATE TABLE `anime`
(
    `id`               BIGINT AUTO_INCREMENT NOT NULL,
    `name`             VARCHAR(255)          NOT NULL,
    `status`           VARCHAR(30)           NOT NULL,
    `added_by_id`      BIGINT                NULL,
    `link`             VARCHAR(255)          NULL,
    `watched`          BIGINT                NOT NULL,
    `total`            BIGINT                NOT NULL,
    `announce_message` BIGINT                NULL,
    `added_at`         DATETIME              NOT NULL,
    CONSTRAINT `PK_ANIME` PRIMARY KEY (`id`),
    CONSTRAINT `UK_ANIME_NAME` UNIQUE (`name`),
    CONSTRAINT `UK_ANIME_LINK` UNIQUE (`link`),
    CONSTRAINT `FK_ANIME_ADDED_BY` FOREIGN KEY (`added_by_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `interest`
(
    `anime_id` BIGINT       NOT NULL,
    `user_id`  BIGINT       NOT NULL,
    `level`    VARCHAR(255) NULL,
    CONSTRAINT `PK_INTEREST` PRIMARY KEY (`anime_id`, `user_id`),
    CONSTRAINT `FK_INTEREST_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`),
    CONSTRAINT `FK_INTEREST_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `watchlist`
(
    `status`     VARCHAR(30) NOT NULL,
    `message_id` BIGINT      NOT NULL,
    CONSTRAINT `PK_WATCHLIST` PRIMARY KEY (`status`),
    CONSTRAINT `UK_WATCHLIST_MESSAGE` UNIQUE (`message_id`)
)
