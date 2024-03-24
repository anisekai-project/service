-- Amount of votes per user
CREATE OR REPLACE VIEW `user_vote` AS
SELECT `u`.`id`,
       COUNT(*) AS `vote`
FROM `user` `u`,
     `interest` `i`,
     `anime` `a`
WHERE `u`.`id` = `i`.`user_id`
  AND `a`.`id` = `i`.`anime_id`
  AND `i`.`level` != 'NEUTRAL'
  AND `a`.`status` IN ('SIMULCAST_AVAILABLE', 'DOWNLOADED')
  AND `u`.`active` = TRUE
GROUP BY `u`.`id`;

-- Vote power per user (% of vote)
CREATE OR REPLACE VIEW `vote_power` AS
SELECT `id`, `vote` / (SELECT SUM(`vote`) FROM `user_vote`) AS `vote_power`
FROM `user_vote` `uv`;

-- Vote score per anime (sum of all vote power)
CREATE OR REPLACE VIEW anime_vote AS
SELECT a.`id`, ROUND(SUM(IF(i.`level` = 'INTERESTED', vp.`vote_power`, vp.`vote_power` * -1)) * 100, 2) score
FROM `anime` `a`,
     `interest` `i`,
     `vote_power` `vp`
WHERE a.`id` = i.`anime_id`
  AND `i`.`level` != 'NEUTRAL'
  AND `a`.`status` IN ('SIMULCAST_AVAILABLE', 'DOWNLOADED')
  AND i.`user_id` = vp.`id`
GROUP BY a.`id`;
