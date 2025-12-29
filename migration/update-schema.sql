ALTER TABLE users
DROP
FOREIGN KEY fk_users_roles;

ALTER TABLE roles
    ADD roles BIGINT NULL;

ALTER TABLE users_roles
    ADD PRIMARY KEY (role_id, user_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users_roles
    ADD CONSTRAINT uc_users_roles_role UNIQUE (role_id);

ALTER TABLE roles
    ADD CONSTRAINT FK_ROLES_ON_ROLES FOREIGN KEY (roles) REFERENCES users (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
DROP
COLUMN roles_id;

ALTER TABLE roles
DROP
COLUMN name;

ALTER TABLE roles
    ADD name SMALLINT NOT NULL;

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);
ALTER TABLE users
    DROP FOREIGN KEY fk_users_roles;

ALTER TABLE roles
    ADD roles BIGINT NULL;

ALTER TABLE users_roles
    ADD PRIMARY KEY (role_id, user_id);

ALTER TABLE users
    ADD CONSTRAINT uc_users_email UNIQUE (email);

ALTER TABLE users_roles
    ADD CONSTRAINT uc_users_roles_role UNIQUE (role_id);

ALTER TABLE roles
    ADD CONSTRAINT FK_ROLES_ON_ROLES FOREIGN KEY (roles) REFERENCES users (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES roles (id);

ALTER TABLE users_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

ALTER TABLE users
    DROP COLUMN roles_id;

ALTER TABLE roles
    DROP COLUMN name;

ALTER TABLE roles
    ADD name SMALLINT NOT NULL;

ALTER TABLE roles
    ADD CONSTRAINT uc_roles_name UNIQUE (name);