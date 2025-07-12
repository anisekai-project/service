-- Nuke few tables !
DELETE
FROM `track`
WHERE TRUE;

UPDATE `episode`
SET `ready` = FALSE
WHERE TRUE;

ALTER TABLE `track`
    AUTO_INCREMENT 1;

-- Apply modifications
ALTER TABLE `track`
    ADD `dispositions` INT NOT NULL DEFAULT 0 AFTER `codec`;

ALTER TABLE `track`
    DROP COLUMN `forced`;

ALTER TABLE `track`
    DROP COLUMN `label`;

-- This was supposed to be in the previous migration, oops.
ALTER TABLE `broadcast`
    MODIFY `episode_count` INT NOT NULL;

ALTER TABLE `broadcast`
    MODIFY `first_episode` INT NOT NULL;
