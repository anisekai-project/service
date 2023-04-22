-- First, rename the column
ALTER TABLE `user` CHANGE COLUMN `banned` `active` BOOLEAN NOT NULL;

-- Then invert the value (meaning change)
UPDATE `user` SET `active` = NOT active;
