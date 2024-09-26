-- -> Reflecting computed
ALTER TABLE broadcast DROP COLUMN last_episode;

-- -> Reflecting new interface names
ALTER TABLE broadcast RENAME COLUMN amount TO episode_count;
ALTER TABLE broadcast RENAME COLUMN start_date_time TO starting_at;
ALTER TABLE broadcast RENAME COLUMN end_date_time TO ending_at; -- Read-only in the code, just to visually see the data in database.