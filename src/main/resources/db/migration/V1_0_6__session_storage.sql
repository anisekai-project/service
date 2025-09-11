CREATE TABLE `session_token`
(
    `id`         BINARY(16)   NOT NULL,
    `owner_id`   BIGINT       NOT NULL,
    `type`       VARCHAR(255) NOT NULL,
    `expires_at` DATETIME     NOT NULL,
    `revoked_at` DATETIME     NULL,
    `created_at` DATETIME     NOT NULL,
    `updated_at` DATETIME     NOT NULL,
    CONSTRAINT `pk_sessiontoken` PRIMARY KEY (`id`)
);

ALTER TABLE `session_token`
    ADD CONSTRAINT `FK_SESSIONTOKEN_ON_OWNER` FOREIGN KEY (`owner_id`) REFERENCES `user` (`id`);
