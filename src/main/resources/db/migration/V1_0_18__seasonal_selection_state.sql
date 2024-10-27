ALTER TABLE `seasonal_selection`
    ADD `state` VARCHAR(255) NULL;

UPDATE `seasonal_selection`
SET `state` = 'CLOSED'
WHERE `closed` = TRUE;

UPDATE `seasonal_selection`
SET `state` = 'OPENED'
WHERE `closed` = TRUE;

ALTER TABLE `seasonal_selection`
    DROP COLUMN `closed`;

ALTER TABLE `seasonal_selection`
    CHANGE COLUMN `state` `state` VARCHAR(255) NOT NULL;
