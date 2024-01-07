DROP TABLE if exists roles CASCADE ;
DROP TABLE if exists users CASCADE ;
drop table if exists verification_tokens CASCADE ;
drop table if exists reset_tokens CASCADE ;
drop table if exists users_roles CASCADE ;
drop table if exists tasks CASCADE ;

create sequence if not exists auto_increment_sequence start with 1;

CREATE TABLE if not exists roles
(
    id        UUID NOT NULL,
    role_name VARCHAR(255) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);

CREATE TABLE if not exists users
(
    id                      UUID        NOT NULL,
    first_name              VARCHAR(20) NOT NULL,
    last_name               VARCHAR(20) NOT NULL,
    email                   VARCHAR(45) NOT NULL,
    password                VARCHAR(64) NOT NULL,
    enabled                 BOOLEAN,
    picture_url             VARCHAR(255),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

CREATE TABLE if not exists verification_tokens
(
    id                      UUID        NOT NULL,
    token_val               VARCHAR(64) NOT NULL,
    expiry_date             TIMESTAMP    NOT NULL,
    user_id                 UUID        NOT NULL,
    CONSTRAINT pk_verification_tokens PRIMARY KEY (id),
    CONSTRAINT fk_user_verification FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE if not exists reset_tokens
(
    id                      UUID        NOT NULL,
    token_val               VARCHAR(64) NOT NULL,
    expiry_date             TIMESTAMP    NOT NULL,
    user_id                 UUID        NOT NULL,
    CONSTRAINT pk_reset_tokens PRIMARY KEY (id),
    CONSTRAINT fk_user_reset FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE if not exists users_roles
(
    role_id UUID NOT NULL,
    user_id UUID NOT NULL,
    CONSTRAINT pk_users_roles PRIMARY KEY (role_id, user_id)
);


ALTER TABLE roles
    drop constraint if exists uc_roles_role_name;

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_role_name UNIQUE (role_name);

ALTER TABLE users
    drop CONSTRAINT if exists uc_users_email;

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users_roles
    drop CONSTRAINT if exists fk_userol_on_role;

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    drop CONSTRAINT if exists fk_userol_on_user;

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

CREATE TABLE if not exists tasks
(
    id                   UUID         NOT NULL,
    project_name         VARCHAR(255) NOT NULL,
    description          VARCHAR(255)          NOT NULL,
    task_code            VARCHAR(2048),
    user_id              UUID         NOT NULL,
    assigned_designer_id UUID         NOT NULL,
    task_number          BIGINT       default nextval('auto_increment_sequence'),
    task_status          SMALLINT,
    CONSTRAINT pk_tasks PRIMARY KEY (id)
);

ALTER TABLE tasks
    drop CONSTRAINT if exists uc_tasks_task_number;

ALTER TABLE tasks
    ADD CONSTRAINT uc_tasks_task_number UNIQUE (task_number);

ALTER TABLE users_roles
    drop CONSTRAINT if exists FK_TASKS_ON_USER;

ALTER TABLE tasks
    ADD CONSTRAINT FK_TASKS_ON_USER FOREIGN KEY (user_id) REFERENCES users (id);
