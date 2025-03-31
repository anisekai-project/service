CREATE TABLE `anime`
(
    `id`               BIGINT AUTO_INCREMENT                                                                                                                                          NOT NULL,
    `title`            VARCHAR(255)                                                                                                                                                   NOT NULL,
    `status`           ENUM ('WATCHED','WATCHING','SIMULCAST','SIMULCAST_AVAILABLE','DOWNLOADED','DOWNLOADING','NOT_DOWNLOADED','NO_SOURCE','UNAVAILABLE','CANCELLED','STORAGE_ONLY') NOT NULL,
    `synopsis`         VARCHAR(255)                                                                                                                                                   NULL,
    `tags`             VARCHAR(255)                                                                                                                                                   NULL,
    `thumbnail`        VARCHAR(255)                                                                                                                                                   NULL,
    `nautiljon_url`    VARCHAR(255)                                                                                                                                                   NOT NULL,
    `title_regex`      VARCHAR(255)                                                                                                                                                   NULL,
    `watched`          BIGINT                                                                                                                                                         NOT NULL,
    `total`            BIGINT                                                                                                                                                         NOT NULL,
    `episode_duration` BIGINT                                                                                                                                                         NULL,
    `added_by_id`      BIGINT                                                                                                                                                         NOT NULL,
    `anilist_id`       BIGINT                                                                                                                                                         NULL,
    `announcement_id`  BIGINT                                                                                                                                                         NULL,
    `created_at`       DATETIME                                                                                                                                                       NOT NULL,
    `updated_at`       DATETIME                                                                                                                                                       NOT NULL,
    CONSTRAINT `pk_anime` PRIMARY KEY (`id`),
    CONSTRAINT `uc_anime_title` UNIQUE (`title`)
);

CREATE TABLE `broadcast`
(
    `id`              BIGINT AUTO_INCREMENT                                        NOT NULL,
    `watch_target_id` BIGINT                                                       NOT NULL,
    `event_id`        BIGINT                                                       NULL,
    `status`          ENUM ('UNKNOWN','SCHEDULED','ACTIVE','COMPLETED','CANCELED') NOT NULL,
    `episode_count`   BIGINT                                                       NOT NULL,
    `first_episode`   BIGINT                                                       NOT NULL,
    `starting_at`     DATETIME                                                     NOT NULL,
    `skip_enabled`    BIT(1)                                                       NOT NULL,
    `do_progress`     BIT(1)                                                       NOT NULL,
    `created_at`      DATETIME                                                     NOT NULL,
    `updated_at`      DATETIME                                                     NOT NULL,
    CONSTRAINT `pk_broadcast` PRIMARY KEY (`id`)
);

CREATE TABLE `episode`
(
    `id`         BIGINT AUTO_INCREMENT NOT NULL,
    `anime_id`   BIGINT                NOT NULL,
    `number`     INT                   NOT NULL,
    `created_at` DATETIME              NOT NULL,
    `updated_at` DATETIME              NOT NULL,
    CONSTRAINT `pk_episode` PRIMARY KEY (`id`)
);

CREATE TABLE `interest`
(
    `user_id`    BIGINT   NOT NULL,
    `anime_id`   BIGINT   NOT NULL,
    `level`      BIGINT   NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    CONSTRAINT `pk_interest` PRIMARY KEY (`user_id`, `anime_id`)
);

CREATE TABLE `media`
(
    `id`         BIGINT AUTO_INCREMENT NOT NULL,
    `episode_id` BIGINT                NOT NULL,
    `name`       VARCHAR(255)          NOT NULL,
    `meta`       VARCHAR(255)          NULL,
    `created_at` DATETIME              NOT NULL,
    `updated_at` DATETIME              NOT NULL,
    CONSTRAINT `pk_media` PRIMARY KEY (`id`)
);

CREATE TABLE `selection`
(
    `id`         BIGINT AUTO_INCREMENT                  NOT NULL,
    `season`     VARCHAR(255)                           NOT NULL,
    `year`       INT                                    NOT NULL,
    `status`     ENUM ('OPEN', 'CLOSED', 'AUTO_CLOSED') NOT NULL,
    `created_at` DATETIME                               NOT NULL,
    `updated_at` DATETIME                               NOT NULL,
    CONSTRAINT `pk_selection` PRIMARY KEY (`id`)
);

CREATE TABLE `selection_animes`
(
    `selection_id` BIGINT NOT NULL,
    `animes_id`    BIGINT NOT NULL,
    CONSTRAINT `pk_selection_animes` PRIMARY KEY (`selection_id`, `animes_id`)
);

CREATE TABLE `setting`
(
    `id`         VARCHAR(255) NOT NULL,
    `value`      VARCHAR(255) NULL,
    `created_at` DATETIME     NOT NULL,
    `updated_at` DATETIME     NOT NULL,
    CONSTRAINT `pk_setting` PRIMARY KEY (`id`)
);

CREATE TABLE `task`
(
    `id`            BIGINT AUTO_INCREMENT                                              NOT NULL,
    `factory`       VARCHAR(255)                                                       NOT NULL,
    `name`          VARCHAR(255)                                                       NOT NULL,
    `state`         ENUM ('SCHEDULED', 'EXECUTING', 'FAILED', 'SUCCEEDED', 'CANCELED') NOT NULL,
    `priority`      BIGINT                                                             NOT NULL,
    `arguments`     LONGTEXT                                                           NULL,
    `failure_count` BIGINT                                                             NOT NULL,
    `started_at`    DATETIME                                                           NULL,
    `completed_at`  DATETIME                                                           NULL,
    `created_at`    DATETIME                                                           NOT NULL,
    `updated_at`    DATETIME                                                           NOT NULL,
    CONSTRAINT `pk_task` PRIMARY KEY (`id`)
);

CREATE TABLE `torrent`
(
    `id`                 VARCHAR(255)                                                                                                          NOT NULL,
    `name`               VARCHAR(255)                                                                                                          NOT NULL,
    `episode_id`         BIGINT                                                                                                                NULL,
    `status`             ENUM ('UNKNOWN', 'STOPPED', 'VERIFY_QUEUED', 'VERIFYING', 'DOWNLOAD_QUEUED', 'DOWNLOADING', 'SEED_QUEUED', 'SEEDING') NOT NULL,
    `progress`           DOUBLE                                                                                                                NOT NULL,
    `link`               VARCHAR(255)                                                                                                          NOT NULL,
    `download_directory` VARCHAR(255)                                                                                                          NOT NULL,
    `file_name`          VARCHAR(255)                                                                                                          NOT NULL,
    `priority`           INT                                                                                                                   NOT NULL,
    `created_at`         DATETIME                                                                                                              NOT NULL,
    `updated_at`         DATETIME                                                                                                              NOT NULL,
    CONSTRAINT `pk_torrent` PRIMARY KEY (`id`)
);

CREATE TABLE `track`
(
    `id`         BIGINT AUTO_INCREMENT               NOT NULL,
    `media_id`   BIGINT                              NOT NULL,
    `name`       VARCHAR(255)                        NOT NULL,
    `type`       ENUM ('AUDIO', 'VIDEO', 'SUBTITLE') NOT NULL,
    `created_at` DATETIME                            NOT NULL,
    `updated_at` DATETIME                            NOT NULL,
    CONSTRAINT `pk_track` PRIMARY KEY (`id`)
);

CREATE TABLE `user`
(
    `id`             BIGINT       NOT NULL,
    `username`       VARCHAR(255) NOT NULL,
    `emote`          VARCHAR(255) NULL,
    `active`         BIT(1)       NOT NULL,
    `administrator`  BIT(1)       NOT NULL,
    `website_access` BIT(1)       NOT NULL,
    `key`            VARCHAR(255) NULL,
    `created_at`     DATETIME     NOT NULL,
    `updated_at`     DATETIME     NOT NULL,
    CONSTRAINT `pk_user` PRIMARY KEY (`id`)
);

CREATE TABLE `voter`
(
    `id`           BIGINT AUTO_INCREMENT NOT NULL,
    `selection_id` BIGINT                NOT NULL,
    `user_id`      BIGINT                NOT NULL,
    `amount`       BIGINT                NOT NULL,
    `created_at`   DATETIME              NOT NULL,
    `updated_at`   DATETIME              NOT NULL,
    CONSTRAINT `pk_voter` PRIMARY KEY (`id`)
);

CREATE TABLE `voter_votes`
(
    `voter_id` BIGINT NOT NULL,
    `votes_id` BIGINT NOT NULL,
    CONSTRAINT `pk_voter_votes` PRIMARY KEY (`voter_id`, `votes_id`),
    CONSTRAINT `uc_voter_votes_votes` UNIQUE (`votes_id`)
);

CREATE TABLE `watchlist`
(
    `id`         ENUM ('WATCHED','WATCHING','SIMULCAST','SIMULCAST_AVAILABLE','DOWNLOADED','DOWNLOADING','NOT_DOWNLOADED','NO_SOURCE','UNAVAILABLE','CANCELLED','STORAGE_ONLY') NOT NULL,
    `message_id` BIGINT                                                                                                                                                         NULL,
    `order`      INT                                                                                                                                                            NOT NULL,
    `created_at` DATETIME                                                                                                                                                       NOT NULL,
    `updated_at` DATETIME                                                                                                                                                       NOT NULL,
    CONSTRAINT `pk_watchlist` PRIMARY KEY (`id`)
);

ALTER TABLE `anime`
    ADD CONSTRAINT `FK_ANIME_ON_ADDED_BY` FOREIGN KEY (`added_by_id`) REFERENCES `user` (`id`);

ALTER TABLE `broadcast`
    ADD CONSTRAINT `FK_BROADCAST_ON_WATCH_TARGET` FOREIGN KEY (`watch_target_id`) REFERENCES `anime` (`id`);

ALTER TABLE `episode`
    ADD CONSTRAINT `FK_EPISODE_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);

ALTER TABLE `interest`
    ADD CONSTRAINT `FK_INTEREST_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);

ALTER TABLE `interest`
    ADD CONSTRAINT `FK_INTEREST_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `media`
    ADD CONSTRAINT `FK_MEDIA_ON_EPISODE` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`);

ALTER TABLE `torrent`
    ADD CONSTRAINT `FK_TORRENT_ON_EPISODE` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`);

ALTER TABLE `track`
    ADD CONSTRAINT `FK_TRACK_ON_MEDIA` FOREIGN KEY (`media_id`) REFERENCES `media` (`id`);

ALTER TABLE `voter`
    ADD CONSTRAINT `FK_VOTER_ON_SELECTION` FOREIGN KEY (`selection_id`) REFERENCES `selection` (`id`);

ALTER TABLE `voter`
    ADD CONSTRAINT `FK_VOTER_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `selection_animes`
    ADD CONSTRAINT `fk_selani_on_anime` FOREIGN KEY (`animes_id`) REFERENCES `anime` (`id`);

ALTER TABLE `selection_animes`
    ADD CONSTRAINT `fk_selani_on_selection` FOREIGN KEY (`selection_id`) REFERENCES `selection` (`id`);

ALTER TABLE `voter_votes`
    ADD CONSTRAINT `fk_votvot_on_anime` FOREIGN KEY (`votes_id`) REFERENCES `anime` (`id`);

ALTER TABLE `voter_votes`
    ADD CONSTRAINT `fk_votvot_on_voter` FOREIGN KEY (`voter_id`) REFERENCES `voter` (`id`);
