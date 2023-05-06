CREATE TABLE `seasonal_selection`
(
    `id`     BIGINT       NOT NULL,
    `name`   VARCHAR(255) NOT NULL,
    `closed` BOOLEAN      NOT NULL,
    CONSTRAINT `PK_SEASONAL_SELECTION` PRIMARY KEY (`id`),
    CONSTRAINT `UK_SEASONAL_SELECTION_NAME` UNIQUE (`name`)
);

CREATE TABLE `seasonal_voter`
(
    `seasonal_selection_id` BIGINT NOT NULL,
    `user_id`               BIGINT NOT NULL,
    `amount`                INT    NOT NULL,
    CONSTRAINT `PK_SEASONAL_VOTER` PRIMARY KEY (`seasonal_selection_id`, `user_id`),
    CONSTRAINT `FK_SVOTER_SEASONAL_SELECTION` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`),
    CONSTRAINT `FK_SVOTER_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `seasonal_vote`
(
    `user_id`  BIGINT NOT NULL,
    `anime_id` BIGINT NOT NULL,
    CONSTRAINT `PK_SEASONAL_VOTE` PRIMARY KEY (`user_id`, `anime_id`),
    CONSTRAINT `FK_SVOTE_ANIME` FOREIGN KEY (`anime_id`) REFERENCES `anime` (`id`),
    CONSTRAINT `FK_SVOTE_USER` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
);

CREATE TABLE `seasonal_selection_animes`
(
    `seasonal_selection_id` BIGINT NOT NULL,
    `animes_id`             BIGINT NOT NULL,
    CONSTRAINT `PK_SEASONAL_SELECTION_ANIMES` PRIMARY KEY (`seasonal_selection_id`, `animes_id`),
    CONSTRAINT `FK_SSA_ANIME` FOREIGN KEY (`animes_id`) REFERENCES `anime` (`id`),
    CONSTRAINT `FK_SSA_SS` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`)
);

CREATE TABLE `seasonal_selection_votes`
(
    `seasonal_selection_id` BIGINT NOT NULL,
    `votes_anime_id`        BIGINT NOT NULL,
    `votes_user_id`         BIGINT NOT NULL,
    CONSTRAINT `PK_SEASONAL_SELECTION_VOTES` PRIMARY KEY (`seasonal_selection_id`, `votes_anime_id`, `votes_user_id`),
    CONSTRAINT `FK_SSVOTES_SS` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`),
    CONSTRAINT `FK_SSVOTES_SVOTE` FOREIGN KEY (`votes_user_id`, `votes_anime_id`) REFERENCES `seasonal_vote` (`user_id`, `anime_id`)
);

CREATE TABLE `seasonal_selection_voters`
(
    `seasonal_selection_id`        BIGINT NOT NULL,
    `voters_seasonal_selection_id` BIGINT NOT NULL,
    `voters_user_id`               BIGINT NOT NULL,
    CONSTRAINT `PK_SEASONAL_SELECTION_VOTERS` PRIMARY KEY (`seasonal_selection_id`, `voters_seasonal_selection_id`,
                                                           `voters_user_id`),
    CONSTRAINT `FK_SSVOTERS_SS` FOREIGN KEY (`seasonal_selection_id`) REFERENCES `seasonal_selection` (`id`),
    CONSTRAINT `FK_SSVOTERS_SVOTER` FOREIGN KEY (`voters_seasonal_selection_id`, `voters_user_id`) REFERENCES `seasonal_voter` (`seasonal_selection_id`, `user_id`)
);
