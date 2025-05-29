CREATE TYPE execution_trigger_type_enum AS ENUM ('SINGLE_TEST_CASE', 'REQUEST_TEST_CASES', 'FOLDER_TEST_CASES', 'COLLECTION_TEST_CASES', 'PROJECT_TEST_CASES', 'SELECTED_TEST_CASES', 'SINGLE_REQUEST', 'SELECTED_REQUESTS');
CREATE TYPE execution_status_enum AS ENUM ('STARTED', 'RUNNING', 'COMPLETED', 'FAILED', 'ABORTED', 'PENDING', 'EXECUTING', 'PASSED', 'ERROR', 'SKIPPED');


DROP TABLE IF EXISTS users;
CREATE TABLE users
(
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name          VARCHAR(50)                    NOT NULL,
    email         VARCHAR(50) UNIQUE             NOT NULL,
    password      VARCHAR(255),
    is_verify     BOOLEAN          DEFAULT FALSE NOT NULL,
    profile_image TEXT,
    created_at    TIMESTAMP        DEFAULT NOW(),
    updated_at    TIMESTAMP
);


DROP TABLE IF EXISTS user_accounts;
CREATE TABLE IF NOT EXISTS user_accounts
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id     UUID         NOT NULL,
    provider    VARCHAR(255) NOT NULL,
    provider_id VARCHAR(255) NOT NULL UNIQUE,
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
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
    CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS projects;
CREATE TABLE IF NOT EXISTS projects
(
    id               UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    name             VARCHAR(255) NOT NULL,
    description      TEXT,
    project_owner_id UUID         NOT NULL,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at       TIMESTAMP    NULL, -- For Soft Deletes,
    CONSTRAINT fk_project_owner FOREIGN KEY (project_owner_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE
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
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP    NULL,
    CONSTRAINT fk_collections_projects FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS requests;
CREATE TABLE requests
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
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
    project_id UUID         NOT NULL,
    key        VARCHAR(255) NOT NULL,
    value      TEXT,
    enabled    BOOLEAN          DEFAULT TRUE,
    created_at TIMESTAMP    NOT NULL,
    updated_at TIMESTAMP    NOT NULL
);

DROP TABLE IF EXISTS data_types;
CREATE TABLE data_types
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name       TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

DROP TABLE IF EXISTS test_cases;
CREATE TABLE test_cases
(
    id            UUID PRIMARY KEY      DEFAULT gen_random_uuid(),
    project_id    UUID,
    data_type_id  UUID         NOT NULL,
    name          VARCHAR(255) NOT NULL,
    value         TEXT,
    is_predefined BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES projects (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_data_type_id FOREIGN KEY (data_type_id) REFERENCES data_types (id) ON DELETE CASCADE ON UPDATE CASCADE
);


CREATE TABLE IF NOT EXISTS execution_batches
(
    batch_id          BIGSERIAL PRIMARY KEY,
    projectId         UUID                        NOT NULL,
    user_id           UUID                        NULL,     -- Changed to UUID to match users.user_id
    trigger_type      execution_trigger_type_enum NOT NULL, -- Using ENUM
    trigger_source_id UUID,
    start_timestamp   TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_timestamp     TIMESTAMP                   NULL,
    overall_status    execution_status_enum       NOT NULL, -- Using ENUM
    created_at        TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at        TIMESTAMP                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_execbatches_project FOREIGN KEY (projectId) REFERENCES projects (id) ON DELETE CASCADE,
    CONSTRAINT fk_execbatches_user FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS execution_results
(
    result_id                   UUID PRIMARY KEY,
    batch_id                    UUID                  NOT NULL,
    request_id                  UUID                  NOT NULL,
    test_case_id                UUID                  NULL,
    isExpectedSuccess           BOOLEAN               NOT NULL DEFAULT FALSE,
    request_definition_snapshot JSONB                 NOT NULL,
    execution_order             INTEGER               NULL,
    start_timestamp             TIMESTAMPTZ           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_timestamp               TIMESTAMPTZ           NULL,
    status                      execution_status_enum NOT NULL, -- Using ENUM
    request_sent_details        JSONB                 NULL,
    response_status_code        INTEGER               NULL,
    response_headers            JSONB                 NULL,
    response_body               TEXT                  NULL,
    response_size_bytes         BIGINT                NULL,
    duration_ms                 INTEGER               NULL,
    assertion_results           JSONB                 NULL,
    created_at                  TIMESTAMPTZ           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_execresults_batch FOREIGN KEY (batch_id) REFERENCES execution_batches (batch_id) ON DELETE CASCADE,
    CONSTRAINT fk_execresults_request FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE RESTRICT,
    CONSTRAINT fk_execresults_testcase FOREIGN KEY (test_case_id) REFERENCES test_cases (id) ON DELETE SET NULL
);

DO
$$
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'application_context_type_enum') THEN
            CREATE TYPE application_context_type_enum AS ENUM (
                'BODY_FIELD',
                'QUERY_PARAM',
                'PATH_VARIABLE'
                );
        END IF;
    END
$$;


DROP TABLE IF EXISTS request_test_cases CASCADE;
CREATE TABLE IF NOT EXISTS request_test_cases
(
    id                  UUID PRIMARY KEY                       DEFAULT gen_random_uuid(),
    request_id          UUID                          NOT NULL,
    test_case_id        UUID                          NOT NULL,
    application_context application_context_type_enum NOT NULL,
    target_field_path   TEXT                          NULL,
    is_expected_success BOOLEAN                       NOT NULL DEFAULT TRUE,
    created_at          TIMESTAMPTZ                   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ                   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_request_test_cases_request FOREIGN KEY (request_id) REFERENCES requests (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_request_test_cases_test_case FOREIGN KEY (test_case_id) REFERENCES test_cases (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT uq_request_test_case_link_context_field UNIQUE (request_id, test_case_id, application_context, target_field_path)
);







