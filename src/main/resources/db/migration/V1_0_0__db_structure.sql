CREATE TABLE `anime`
(
    `id`               BIGINT AUTO_INCREMENT NOT NULL,
    `group`            VARCHAR(255)          NOT NULL,
    `order`            TINYINT               NOT NULL,
    `title`            VARCHAR(255)          NOT NULL,
    `list`             VARCHAR(255)          NOT NULL,
    `synopsis`         TEXT                  NULL,
    `tags`             VARCHAR(255)          NULL,
    `thumbnail_url`    VARCHAR(255)          NULL,
    `url`              VARCHAR(255)          NOT NULL,
    `title_regex`      VARCHAR(255)          NULL,
    `watched`          BIGINT                NULL,
    `total`            BIGINT                NULL,
    `episode_duration` BIGINT                NULL,
    `added_by_id`      BIGINT                NOT NULL,
    `anilist_id`       BIGINT                NULL,
    `announcement_id`  BIGINT                NULL,
    `created_at`       DATETIME              NOT NULL,
    `updated_at`       DATETIME              NOT NULL,
    CONSTRAINT `pk_anime` PRIMARY KEY (`id`)
);

CREATE TABLE `broadcast`
(
    `id`              BIGINT AUTO_INCREMENT NOT NULL,
    `watch_target_id` BIGINT                NOT NULL,
    `starting_at`     DATETIME              NOT NULL,
    `event_id`        BIGINT                NULL,
    `status`          VARCHAR(255)          NOT NULL,
    `episode_count`   BIGINT                NOT NULL,
    `first_episode`   BIGINT                NOT NULL,
    `skip_enabled`    BIT(1)                NOT NULL,
    `created_at`      DATETIME              NOT NULL,
    `updated_at`      DATETIME              NOT NULL,
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
    `level`      TINYINT  NOT NULL,
    `created_at` DATETIME NOT NULL,
    `updated_at` DATETIME NOT NULL,
    CONSTRAINT `pk_interest` PRIMARY KEY (`user_id`, `anime_id`)
);

CREATE TABLE `selection`
(
    `id`         BIGINT AUTO_INCREMENT NOT NULL,
    `season`     VARCHAR(255)          NOT NULL,
    `year`       INT                   NOT NULL,
    `status`     VARCHAR(255)          NOT NULL,
    `created_at` DATETIME              NOT NULL,
    `updated_at` DATETIME              NOT NULL,
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
    `id`            BIGINT AUTO_INCREMENT NOT NULL,
    `factory_name`  VARCHAR(255)          NOT NULL,
    `name`          VARCHAR(255)          NOT NULL,
    `status`        VARCHAR(255)          NOT NULL,
    `priority`      TINYINT               NOT NULL,
    `arguments`     LONGTEXT              NULL,
    `failure_count` TINYINT               NOT NULL,
    `started_at`    DATETIME              NULL,
    `completed_at`  DATETIME              NULL,
    `created_at`    DATETIME              NOT NULL,
    `updated_at`    DATETIME              NOT NULL,
    CONSTRAINT `pk_task` PRIMARY KEY (`id`)
);

CREATE TABLE `torrent`
(
    `id`                 VARCHAR(255) NOT NULL,
    `name`               VARCHAR(255) NOT NULL,
    `status`             VARCHAR(255) NOT NULL,
    `progress`           DOUBLE       NOT NULL,
    `link`               VARCHAR(255) NOT NULL,
    `download_directory` VARCHAR(255) NOT NULL,
    `priority`           TINYINT      NOT NULL,
    `created_at`         DATETIME     NOT NULL,
    `updated_at`         DATETIME     NOT NULL,
    CONSTRAINT `pk_torrent` PRIMARY KEY (`id`)
);

CREATE TABLE `torrent_file`
(
    `torrent_id` VARCHAR(255) NOT NULL,
    `index`      INT          NOT NULL,
    `episode_id` BIGINT       NOT NULL,
    `name`       VARCHAR(255) NOT NULL,
    `created_at` DATETIME     NOT NULL,
    `updated_at` DATETIME     NOT NULL,
    CONSTRAINT `pk_torrentfile` PRIMARY KEY (`torrent_id`, `index`)
);

CREATE TABLE `track`
(
    `id`         BIGINT AUTO_INCREMENT NOT NULL,
    `episode_id` BIGINT                NOT NULL,
    `name`       VARCHAR(255)          NOT NULL,
    `label`      VARCHAR(255)          NULL,
    `codec`      VARCHAR(255)          NOT NULL,
    `forced`     BIT(1)                NOT NULL,
    `language`   VARCHAR(255)          NULL,
    `created_at` DATETIME              NOT NULL,
    `updated_at` DATETIME              NOT NULL,
    CONSTRAINT `pk_track` PRIMARY KEY (`id`)
);

CREATE TABLE `user`
(
    `id`            BIGINT       NOT NULL,
    `username`      VARCHAR(255) NOT NULL,
    `nickname`      VARCHAR(255) NOT NULL,
    `avatar_url`    VARCHAR(255) NULL,
    `emote`         VARCHAR(255) NULL,
    `active`        BIT(1)       NOT NULL,
    `administrator` BIT(1)       NOT NULL,
    `guest`         BIT(1)       NOT NULL,
    `api_key`       VARCHAR(255) NULL,
    `created_at`    DATETIME     NOT NULL,
    `updated_at`    DATETIME     NOT NULL,
    CONSTRAINT `pk_user` PRIMARY KEY (`id`)
);

CREATE TABLE `voter`
(
    `selection_id` BIGINT   NOT NULL,
    `user_id`      BIGINT   NOT NULL,
    `amount`       SMALLINT NOT NULL,
    `created_at`   DATETIME NOT NULL,
    `updated_at`   DATETIME NOT NULL,
    CONSTRAINT `pk_voter` PRIMARY KEY (`selection_id`, `user_id`)
);

CREATE TABLE `voter_votes`
(
    `voter_selection_id` BIGINT NOT NULL,
    `voter_user_id`      BIGINT NOT NULL,
    `votes_id`           BIGINT NOT NULL,
    CONSTRAINT `pk_voter_votes` PRIMARY KEY (`voter_selection_id`, `voter_user_id`, `votes_id`)
);

CREATE TABLE `watchlist`
(
    `id`         VARCHAR(255) NOT NULL,
    `message_id` BIGINT       NULL,
    `created_at` DATETIME     NOT NULL,
    `updated_at` DATETIME     NOT NULL,
    CONSTRAINT `pk_watchlist` PRIMARY KEY (`id`)
);

ALTER TABLE `anime`
    ADD CONSTRAINT `uc_anime_title` UNIQUE (`title`);

ALTER TABLE `voter_votes`
    ADD CONSTRAINT `uc_voter_votes_votes` UNIQUE (`votes_id`);

ALTER TABLE `anime`
    ADD CONSTRAINT `FK_ANIME_ON_ADDEDBY` FOREIGN KEY (`added_by_id`) REFERENCES `user` (`id`);

ALTER TABLE `broadcast`
    ADD CONSTRAINT `FK_BROADCAST_ON_WATCHTARGET` FOREIGN KEY (`watch_target_id`) REFERENCES `anime` (`id`);

ALTER TABLE `episode`
    ADD CONSTRAINT `FK_EPISODE_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);

ALTER TABLE `interest`
    ADD CONSTRAINT `FK_INTEREST_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);

ALTER TABLE `interest`
    ADD CONSTRAINT `FK_INTEREST_ON_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`);

ALTER TABLE `torrent_file`
    ADD CONSTRAINT `FK_TORRENTFILE_ON_EPISODE` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`);

ALTER TABLE `torrent_file`
    ADD CONSTRAINT `FK_TORRENTFILE_ON_TORRENT` FOREIGN KEY (`torrent_id`) REFERENCES `torrent` (`id`);

ALTER TABLE `track`
    ADD CONSTRAINT `FK_TRACK_ON_EPISODE` FOREIGN KEY (`episode_id`) REFERENCES `episode` (`id`);

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
    ADD CONSTRAINT `fk_votvot_on_voter` FOREIGN KEY (`voter_selection_id`, `voter_user_id`) REFERENCES `voter` (`selection_id`, `user_id`);
