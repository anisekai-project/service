ALTER TABLE `torrent_file`
    DROP FOREIGN KEY `FK_TORRENTFILE_ON_TORRENT`;
ALTER TABLE `torrent_file`
    DROP PRIMARY KEY;
ALTER TABLE `torrent`
    DROP PRIMARY KEY;

ALTER TABLE `torrent`
    CHANGE `id` `hash` VARCHAR(40);
ALTER TABLE `torrent_file`
    CHANGE `torrent_id` `torrent_hash` VARCHAR(40);

ALTER TABLE `torrent`
    ADD COLUMN `id` BINARY(16) FIRST;
ALTER TABLE `torrent_file`
    ADD COLUMN `torrent_id` BINARY(16) FIRST;

UPDATE `torrent`
SET `id` = CAST(`UUID_v7`() AS BINARY)
WHERE TRUE
ORDER BY `created_at`;

UPDATE `torrent_file` `tf`
SET `torrent_id` = (SELECT `id` FROM `torrent` AS `t` WHERE `tf`.`torrent_hash` = `t`.`hash`)
WHERE TRUE;

ALTER TABLE `torrent`
    ADD PRIMARY KEY (`id`);
ALTER TABLE `torrent`
    ADD CONSTRAINT `uc_torrent_hash` UNIQUE (`hash`);

ALTER TABLE `torrent_file`
    DROP COLUMN `torrent_hash`;
ALTER TABLE `torrent_file`
    ADD CONSTRAINT `FK_TORRENTFILE_ON_TORRENT` FOREIGN KEY (`torrent_id`) REFERENCES `torrent` (`id`);
ALTER TABLE `torrent_file`
    ADD PRIMARY KEY (`torrent_id`, `index`)
