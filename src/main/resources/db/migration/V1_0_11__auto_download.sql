CREATE TABLE `setting`
(
    `key`   VARCHAR(255) NOT NULL,
    `value` VARCHAR(255) NULL,
    CONSTRAINT `PK_SETTING` PRIMARY KEY (`key`)
);

ALTER TABLE `anime`
    ADD `rss_match` VARCHAR(255) NULL AFTER `announce_message`;

ALTER TABLE `anime`
    ADD `disk_path` VARCHAR(255) NULL AFTER `rss_match`;

CREATE TABLE `torrent`
(
    `id`           INT          NOT NULL,
    `anime_id`     BIGINT       NOT NULL,
    `link`         VARCHAR(255) NOT NULL,
    `name`         VARCHAR(255) NOT NULL,
    `status`       VARCHAR(255) NOT NULL,
    `download_dir` VARCHAR(255) NULL,
    `percent_done` DOUBLE       NOT NULL,
    `info_hash`    VARCHAR(255) NOT NULL,
    CONSTRAINT `pk_torrent` PRIMARY KEY (`id`)
);

ALTER TABLE `torrent`
    ADD CONSTRAINT `FK_TORRENT_ON_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`);
