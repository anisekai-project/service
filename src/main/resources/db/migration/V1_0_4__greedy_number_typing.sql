
ALTER TABLE `broadcast`
    MODIFY `episode_count` INT;

ALTER TABLE `anime`
    MODIFY `episode_duration` INT;

ALTER TABLE `broadcast`
    MODIFY `first_episode` INT;

ALTER TABLE `anime`
    MODIFY `total` INT;

ALTER TABLE `anime`
    MODIFY `watched` INT;
