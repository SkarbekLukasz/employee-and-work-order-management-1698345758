DROP TABLE if exists roles CASCADE ;
DROP TABLE if exists users CASCADE ;
drop table if exists verification_tokens CASCADE ;
drop table if exists reset_tokens CASCADE ;
drop table if exists users_roles CASCADE ;

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
    picture_url             varchar(255),
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