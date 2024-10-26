CREATE TABLE `task`
(
    `id`            BIGINT       NOT NULL,
    `factory`       VARCHAR(255) NOT NULL,
    `name`          VARCHAR(255) NOT NULL,
    `state`         VARCHAR(255) NOT NULL,
    `arguments`     LONGTEXT     NULL,
    `failure_count` BIGINT       NOT NULL,
    `started_at`    DATETIME     NULL,
    `completed_at`  DATETIME     NULL,
    `created_at`    DATETIME     NOT NULL,
    `updated_at`    DATETIME     NOT NULL,
    CONSTRAINT `PK_TASK` PRIMARY KEY (`id`)
);
