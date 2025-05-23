DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(50)                    NOT NULL,
    email         VARCHAR(50) UNIQUE             NOT NULL,
    password      VARCHAR(255),
    is_verify     BOOLEAN          DEFAULT FALSE NOT NULL,
    profile_image TEXT,
    created_at    TIMESTAMPTZ      DEFAULT NOW(),
    updated_at    TIMESTAMPTZ
);

DROP TABLE IF EXISTS user_accounts;
CREATE TABLE IF NOT EXISTS user_accounts
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    provider    VARCHAR(255) NOT NULL UNIQUE,
    provider_id VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS verification_token;
CREATE TABLE IF NOT EXISTS verification_token
(
    email     VARCHAR(255) PRIMARY KEY,
    token     TEXT,
    expire_at TIMESTAMP
);

DROP TABLE IF EXISTS otp_tokens;
CREATE TABLE IF NOT EXISTS otp_tokens
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    hash_otp    TEXT      NOT NULL,
    expire_date TIMESTAMP NOT NULL,
    user_id     UUID      NOT NULL,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS projects;
CREATE TABLE IF NOT EXISTS projects
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    project_owner_id UUID         NOT NULL,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMPTZ  NULL, -- For Soft Deletes,
    CONSTRAINT fk_project_owner FOREIGN KEY (project_owner_id) REFERENCES users (user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS prject_collaborators;
CREATE TABLE IF NOT EXISTS project_collaborators
(
    project_collaborator_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id              UUID NOT NULL,
    user_id                 UUID NOT NULL,
    CONSTRAINT fk_project FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS collections;
CREATE TABLE IF NOT EXISTS collections
(
    id         UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name       VARCHAR(255) NOT NULL DEFAULT 'new collection',
    project_id UUID         NOT NULL,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMPTZ  NULL,
    CONSTRAINT fk_collections_projects FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- create a request table
CREATE TYPE http_method AS ENUM ('GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS', 'HEAD', 'TRACE');

DROP TABLE IF EXISTS requests;
CREATE TABLE requests
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(255) NOT NULL,
    collection_id UUID         NOT NULL,
    method        http_method  NOT NULL DEFAULT 'GET',
    details       JSONB        NOT NULL,
    created_at    TIMESTAMP    NOT NULL,
    updated_at    TIMESTAMP    NOT NULL,
    CONSTRAINT fk_collection_id FOREIGN KEY (collection_id) REFERENCES collections (id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- create environment variable
DROP TABLE IF EXISTS variables;
CREATE TABLE variables
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    project_id UUID NOT NULL,
    key         VARCHAR(255) NOT NULL,
    value      TEXT,
    enabled    BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);



