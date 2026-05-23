CREATE TABLE blog_posts (
    id         VARCHAR(36)  NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    author     VARCHAR(36)  NOT NULL,
    created_at DATETIME(6)  NOT NULL,
    updated_at DATETIME(6),
    PRIMARY KEY (id)
);

CREATE TABLE comments (
    id           VARCHAR(36) NOT NULL,
    content      TEXT        NOT NULL,
    author       VARCHAR(36) NOT NULL,
    created_at   DATETIME(6) NOT NULL,
    updated_at   DATETIME(6),
    blog_post_id VARCHAR(36) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_comments_blog_post FOREIGN KEY (blog_post_id) REFERENCES blog_posts (id)
);
