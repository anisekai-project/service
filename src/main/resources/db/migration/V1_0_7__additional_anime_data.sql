ALTER TABLE `anime`
    ADD COLUMN `image` VARCHAR(255) AFTER `link`;
ALTER TABLE `anime`
    ADD COLUMN `episode_duration` BIGINT NOT NULL DEFAULT 24 AFTER `total`;
