DROP TABLE IF EXISTS "public"."admin_event_entity";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."admin_event_entity" (
    "id" varchar(36) NOT NULL,
    "admin_event_time" int8,
    "realm_id" varchar(255),
    "operation_type" varchar(255),
    "auth_realm_id" varchar(255),
    "auth_client_id" varchar(255),
    "auth_user_id" varchar(255),
    "ip_address" varchar(255),
    "resource_path" varchar(2550),
    "representation" text,
    "error" varchar(255),
    "resource_type" varchar(64),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."associated_policy";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."associated_policy" (
    "policy_id" varchar(36) NOT NULL,
    "associated_policy_id" varchar(36) NOT NULL,
    PRIMARY KEY ("policy_id","associated_policy_id")
);

DROP TABLE IF EXISTS "public"."authentication_execution";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."authentication_execution" (
    "id" varchar(36) NOT NULL,
    "alias" varchar(255),
    "authenticator" varchar(36),
    "realm_id" varchar(36),
    "flow_id" varchar(36),
    "requirement" int4,
    "priority" int4,
    "authenticator_flow" bool NOT NULL DEFAULT false,
    "auth_flow_id" varchar(36),
    "auth_config" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."authentication_flow";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."authentication_flow" (
    "id" varchar(36) NOT NULL,
    "alias" varchar(255),
    "description" varchar(255),
    "realm_id" varchar(36),
    "provider_id" varchar(36) NOT NULL DEFAULT 'basic-flow'::character varying,
    "top_level" bool NOT NULL DEFAULT false,
    "built_in" bool NOT NULL DEFAULT false,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."authenticator_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."authenticator_config" (
    "id" varchar(36) NOT NULL,
    "alias" varchar(255),
    "realm_id" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."authenticator_config_entry";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."authenticator_config_entry" (
    "authenticator_id" varchar(36) NOT NULL,
    "value" text,
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("authenticator_id","name")
);

DROP TABLE IF EXISTS "public"."broker_link";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."broker_link" (
    "identity_provider" varchar(255) NOT NULL,
    "storage_provider_id" varchar(255),
    "realm_id" varchar(36) NOT NULL,
    "broker_user_id" varchar(255),
    "broker_username" varchar(255),
    "token" text,
    "user_id" varchar(255) NOT NULL,
    PRIMARY KEY ("identity_provider","user_id")
);

DROP TABLE IF EXISTS "public"."client";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client" (
    "id" varchar(36) NOT NULL,
    "enabled" bool NOT NULL DEFAULT false,
    "full_scope_allowed" bool NOT NULL DEFAULT false,
    "client_id" varchar(255),
    "not_before" int4,
    "public_client" bool NOT NULL DEFAULT false,
    "secret" varchar(255),
    "base_url" varchar(255),
    "bearer_only" bool NOT NULL DEFAULT false,
    "management_url" varchar(255),
    "surrogate_auth_required" bool NOT NULL DEFAULT false,
    "realm_id" varchar(36),
    "protocol" varchar(255),
    "node_rereg_timeout" int4 DEFAULT 0,
    "frontchannel_logout" bool NOT NULL DEFAULT false,
    "consent_required" bool NOT NULL DEFAULT false,
    "name" varchar(255),
    "service_accounts_enabled" bool NOT NULL DEFAULT false,
    "client_authenticator_type" varchar(255),
    "root_url" varchar(255),
    "description" varchar(255),
    "registration_token" varchar(255),
    "standard_flow_enabled" bool NOT NULL DEFAULT true,
    "implicit_flow_enabled" bool NOT NULL DEFAULT false,
    "direct_access_grants_enabled" bool NOT NULL DEFAULT false,
    "always_display_in_console" bool NOT NULL DEFAULT false,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."client_attributes";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_attributes" (
    "client_id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "value" text,
    PRIMARY KEY ("client_id","name")
);

DROP TABLE IF EXISTS "public"."client_auth_flow_bindings";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_auth_flow_bindings" (
    "client_id" varchar(36) NOT NULL,
    "flow_id" varchar(36),
    "binding_name" varchar(255) NOT NULL,
    PRIMARY KEY ("client_id","binding_name")
);

DROP TABLE IF EXISTS "public"."client_initial_access";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_initial_access" (
    "id" varchar(36) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "timestamp" int4,
    "expiration" int4,
    "count" int4,
    "remaining_count" int4,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."client_node_registrations";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_node_registrations" (
    "client_id" varchar(36) NOT NULL,
    "value" int4,
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("client_id","name")
);

DROP TABLE IF EXISTS "public"."client_scope";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_scope" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255),
    "realm_id" varchar(36),
    "description" varchar(255),
    "protocol" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."client_scope_attributes";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_scope_attributes" (
    "scope_id" varchar(36) NOT NULL,
    "value" varchar(2048),
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("scope_id","name")
);

DROP TABLE IF EXISTS "public"."client_scope_client";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_scope_client" (
    "client_id" varchar(255) NOT NULL,
    "scope_id" varchar(255) NOT NULL,
    "default_scope" bool NOT NULL DEFAULT false,
    PRIMARY KEY ("client_id","scope_id")
);

DROP TABLE IF EXISTS "public"."client_scope_role_mapping";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_scope_role_mapping" (
    "scope_id" varchar(36) NOT NULL,
    "role_id" varchar(36) NOT NULL,
    PRIMARY KEY ("scope_id","role_id")
);

DROP TABLE IF EXISTS "public"."client_session";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_session" (
    "id" varchar(36) NOT NULL,
    "client_id" varchar(36),
    "redirect_uri" varchar(255),
    "state" varchar(255),
    "timestamp" int4,
    "session_id" varchar(36),
    "auth_method" varchar(255),
    "realm_id" varchar(255),
    "auth_user_id" varchar(36),
    "current_action" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."client_session_auth_status";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_session_auth_status" (
    "authenticator" varchar(36) NOT NULL,
    "status" int4,
    "client_session" varchar(36) NOT NULL,
    PRIMARY KEY ("client_session","authenticator")
);

DROP TABLE IF EXISTS "public"."client_session_note";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_session_note" (
    "name" varchar(255) NOT NULL,
    "value" varchar(255),
    "client_session" varchar(36) NOT NULL,
    PRIMARY KEY ("client_session","name")
);

DROP TABLE IF EXISTS "public"."client_session_prot_mapper";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_session_prot_mapper" (
    "protocol_mapper_id" varchar(36) NOT NULL,
    "client_session" varchar(36) NOT NULL,
    PRIMARY KEY ("client_session","protocol_mapper_id")
);

DROP TABLE IF EXISTS "public"."client_session_role";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_session_role" (
    "role_id" varchar(255) NOT NULL,
    "client_session" varchar(36) NOT NULL,
    PRIMARY KEY ("client_session","role_id")
);

DROP TABLE IF EXISTS "public"."client_user_session_note";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."client_user_session_note" (
    "name" varchar(255) NOT NULL,
    "value" varchar(2048),
    "client_session" varchar(36) NOT NULL,
    PRIMARY KEY ("client_session","name")
);

DROP TABLE IF EXISTS "public"."component";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."component" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255),
    "parent_id" varchar(36),
    "provider_id" varchar(36),
    "provider_type" varchar(255),
    "realm_id" varchar(36),
    "sub_type" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."component_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."component_config" (
    "id" varchar(36) NOT NULL,
    "component_id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "value" text,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."composite_role";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."composite_role" (
    "composite" varchar(36) NOT NULL,
    "child_role" varchar(36) NOT NULL,
    PRIMARY KEY ("composite","child_role")
);

DROP TABLE IF EXISTS "public"."credential";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."credential" (
    "id" varchar(36) NOT NULL,
    "salt" bytea,
    "type" varchar(255),
    "user_id" varchar(36),
    "created_date" int8,
    "user_label" varchar(255),
    "secret_data" text,
    "credential_data" text,
    "priority" int4,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."databasechangelog";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."databasechangelog" (
    "id" varchar(255) NOT NULL,
    "author" varchar(255) NOT NULL,
    "filename" varchar(255) NOT NULL,
    "dateexecuted" timestamp NOT NULL,
    "orderexecuted" int4 NOT NULL,
    "exectype" varchar(10) NOT NULL,
    "md5sum" varchar(35),
    "description" varchar(255),
    "comments" varchar(255),
    "tag" varchar(255),
    "liquibase" varchar(20),
    "contexts" varchar(255),
    "labels" varchar(255),
    "deployment_id" varchar(10)
);

DROP TABLE IF EXISTS "public"."databasechangeloglock";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."databasechangeloglock" (
    "id" int4 NOT NULL,
    "locked" bool NOT NULL,
    "lockgranted" timestamp,
    "lockedby" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."default_client_scope";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."default_client_scope" (
    "realm_id" varchar(36) NOT NULL,
    "scope_id" varchar(36) NOT NULL,
    "default_scope" bool NOT NULL DEFAULT false,
    PRIMARY KEY ("realm_id","scope_id")
);

DROP TABLE IF EXISTS "public"."event_entity";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."event_entity" (
    "id" varchar(36) NOT NULL,
    "client_id" varchar(255),
    "details_json" varchar(2550),
    "error" varchar(255),
    "ip_address" varchar(255),
    "realm_id" varchar(255),
    "session_id" varchar(255),
    "event_time" int8,
    "type" varchar(255),
    "user_id" varchar(255),
    "details_json_long_value" text,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."fed_user_attribute";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_attribute" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "storage_provider_id" varchar(36),
    "value" varchar(2024),
    "long_value_hash" bytea,
    "long_value_hash_lower_case" bytea,
    "long_value" text,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."fed_user_consent";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_consent" (
    "id" varchar(36) NOT NULL,
    "client_id" varchar(255),
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "storage_provider_id" varchar(36),
    "created_date" int8,
    "last_updated_date" int8,
    "client_storage_provider" varchar(36),
    "external_client_id" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."fed_user_consent_cl_scope";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_consent_cl_scope" (
    "user_consent_id" varchar(36) NOT NULL,
    "scope_id" varchar(36) NOT NULL,
    PRIMARY KEY ("user_consent_id","scope_id")
);

DROP TABLE IF EXISTS "public"."fed_user_credential";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_credential" (
    "id" varchar(36) NOT NULL,
    "salt" bytea,
    "type" varchar(255),
    "created_date" int8,
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "storage_provider_id" varchar(36),
    "user_label" varchar(255),
    "secret_data" text,
    "credential_data" text,
    "priority" int4,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."fed_user_group_membership";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_group_membership" (
    "group_id" varchar(36) NOT NULL,
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "storage_provider_id" varchar(36),
    PRIMARY KEY ("group_id","user_id")
);

DROP TABLE IF EXISTS "public"."fed_user_required_action";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_required_action" (
    "required_action" varchar(255) NOT NULL DEFAULT ' '::character varying,
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "storage_provider_id" varchar(36),
    PRIMARY KEY ("required_action","user_id")
);

DROP TABLE IF EXISTS "public"."fed_user_role_mapping";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."fed_user_role_mapping" (
    "role_id" varchar(36) NOT NULL,
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "storage_provider_id" varchar(36),
    PRIMARY KEY ("role_id","user_id")
);

DROP TABLE IF EXISTS "public"."federated_identity";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."federated_identity" (
    "identity_provider" varchar(255) NOT NULL,
    "realm_id" varchar(36),
    "federated_user_id" varchar(255),
    "federated_username" varchar(255),
    "token" text,
    "user_id" varchar(36) NOT NULL,
    PRIMARY KEY ("identity_provider","user_id")
);

DROP TABLE IF EXISTS "public"."federated_user";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."federated_user" (
    "id" varchar(255) NOT NULL,
    "storage_provider_id" varchar(255),
    "realm_id" varchar(36) NOT NULL,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."group_attribute";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."group_attribute" (
    "id" varchar(36) NOT NULL DEFAULT 'sybase-needs-something-here'::character varying,
    "name" varchar(255) NOT NULL,
    "value" varchar(255),
    "group_id" varchar(36) NOT NULL,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."group_role_mapping";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."group_role_mapping" (
    "role_id" varchar(36) NOT NULL,
    "group_id" varchar(36) NOT NULL,
    PRIMARY KEY ("role_id","group_id")
);

DROP TABLE IF EXISTS "public"."identity_provider";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."identity_provider" (
    "internal_id" varchar(36) NOT NULL,
    "enabled" bool NOT NULL DEFAULT false,
    "provider_alias" varchar(255),
    "provider_id" varchar(255),
    "store_token" bool NOT NULL DEFAULT false,
    "authenticate_by_default" bool NOT NULL DEFAULT false,
    "realm_id" varchar(36),
    "add_token_role" bool NOT NULL DEFAULT true,
    "trust_email" bool NOT NULL DEFAULT false,
    "first_broker_login_flow_id" varchar(36),
    "post_broker_login_flow_id" varchar(36),
    "provider_display_name" varchar(255),
    "link_only" bool NOT NULL DEFAULT false,
    PRIMARY KEY ("internal_id")
);

DROP TABLE IF EXISTS "public"."identity_provider_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."identity_provider_config" (
    "identity_provider_id" varchar(36) NOT NULL,
    "value" text,
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("identity_provider_id","name")
);

DROP TABLE IF EXISTS "public"."identity_provider_mapper";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."identity_provider_mapper" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "idp_alias" varchar(255) NOT NULL,
    "idp_mapper_name" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."idp_mapper_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."idp_mapper_config" (
    "idp_mapper_id" varchar(36) NOT NULL,
    "value" text,
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("idp_mapper_id","name")
);

DROP TABLE IF EXISTS "public"."keycloak_group";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."keycloak_group" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255),
    "parent_group" varchar(36) NOT NULL,
    "realm_id" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."keycloak_role";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."keycloak_role" (
    "id" varchar(36) NOT NULL,
    "client_realm_constraint" varchar(255),
    "client_role" bool NOT NULL DEFAULT false,
    "description" varchar(255),
    "name" varchar(255),
    "realm_id" varchar(255),
    "client" varchar(36),
    "realm" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."migration_model";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."migration_model" (
    "id" varchar(36) NOT NULL,
    "version" varchar(36),
    "update_time" int8 NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."offline_client_session";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."offline_client_session" (
    "user_session_id" varchar(36) NOT NULL,
    "client_id" varchar(255) NOT NULL,
    "offline_flag" varchar(4) NOT NULL,
    "timestamp" int4,
    "data" text,
    "client_storage_provider" varchar(36) NOT NULL DEFAULT 'local'::character varying,
    "external_client_id" varchar(255) NOT NULL DEFAULT 'local'::character varying,
    PRIMARY KEY ("user_session_id","client_id","client_storage_provider","external_client_id","offline_flag")
);

DROP TABLE IF EXISTS "public"."offline_user_session";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."offline_user_session" (
    "user_session_id" varchar(36) NOT NULL,
    "user_id" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "created_on" int4 NOT NULL,
    "offline_flag" varchar(4) NOT NULL,
    "data" text,
    "last_session_refresh" int4 NOT NULL DEFAULT 0,
    PRIMARY KEY ("user_session_id","offline_flag")
);

DROP TABLE IF EXISTS "public"."policy_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."policy_config" (
    "policy_id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "value" text,
    PRIMARY KEY ("policy_id","name")
);

DROP TABLE IF EXISTS "public"."protocol_mapper";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."protocol_mapper" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "protocol" varchar(255) NOT NULL,
    "protocol_mapper_name" varchar(255) NOT NULL,
    "client_id" varchar(36),
    "client_scope_id" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."protocol_mapper_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."protocol_mapper_config" (
    "protocol_mapper_id" varchar(36) NOT NULL,
    "value" text,
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("protocol_mapper_id","name")
);

DROP TABLE IF EXISTS "public"."realm";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm" (
    "id" varchar(36) NOT NULL,
    "access_code_lifespan" int4,
    "user_action_lifespan" int4,
    "access_token_lifespan" int4,
    "account_theme" varchar(255),
    "admin_theme" varchar(255),
    "email_theme" varchar(255),
    "enabled" bool NOT NULL DEFAULT false,
    "events_enabled" bool NOT NULL DEFAULT false,
    "events_expiration" int8,
    "login_theme" varchar(255),
    "name" varchar(255),
    "not_before" int4,
    "password_policy" varchar(2550),
    "registration_allowed" bool NOT NULL DEFAULT false,
    "remember_me" bool NOT NULL DEFAULT false,
    "reset_password_allowed" bool NOT NULL DEFAULT false,
    "social" bool NOT NULL DEFAULT false,
    "ssl_required" varchar(255),
    "sso_idle_timeout" int4,
    "sso_max_lifespan" int4,
    "update_profile_on_soc_login" bool NOT NULL DEFAULT false,
    "verify_email" bool NOT NULL DEFAULT false,
    "master_admin_client" varchar(36),
    "login_lifespan" int4,
    "internationalization_enabled" bool NOT NULL DEFAULT false,
    "default_locale" varchar(255),
    "reg_email_as_username" bool NOT NULL DEFAULT false,
    "admin_events_enabled" bool NOT NULL DEFAULT false,
    "admin_events_details_enabled" bool NOT NULL DEFAULT false,
    "edit_username_allowed" bool NOT NULL DEFAULT false,
    "otp_policy_counter" int4 DEFAULT 0,
    "otp_policy_window" int4 DEFAULT 1,
    "otp_policy_period" int4 DEFAULT 30,
    "otp_policy_digits" int4 DEFAULT 6,
    "otp_policy_alg" varchar(36) DEFAULT 'HmacSHA1'::character varying,
    "otp_policy_type" varchar(36) DEFAULT 'totp'::character varying,
    "browser_flow" varchar(36),
    "registration_flow" varchar(36),
    "direct_grant_flow" varchar(36),
    "reset_credentials_flow" varchar(36),
    "client_auth_flow" varchar(36),
    "offline_session_idle_timeout" int4 DEFAULT 0,
    "revoke_refresh_token" bool NOT NULL DEFAULT false,
    "access_token_life_implicit" int4 DEFAULT 0,
    "login_with_email_allowed" bool NOT NULL DEFAULT true,
    "duplicate_emails_allowed" bool NOT NULL DEFAULT false,
    "docker_auth_flow" varchar(36),
    "refresh_token_max_reuse" int4 DEFAULT 0,
    "allow_user_managed_access" bool NOT NULL DEFAULT false,
    "sso_max_lifespan_remember_me" int4 NOT NULL DEFAULT 0,
    "sso_idle_timeout_remember_me" int4 NOT NULL DEFAULT 0,
    "default_role" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."realm_attribute";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_attribute" (
    "name" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    "value" text,
    PRIMARY KEY ("name","realm_id")
);

DROP TABLE IF EXISTS "public"."realm_default_groups";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_default_groups" (
    "realm_id" varchar(36) NOT NULL,
    "group_id" varchar(36) NOT NULL,
    PRIMARY KEY ("realm_id","group_id")
);

DROP TABLE IF EXISTS "public"."realm_enabled_event_types";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_enabled_event_types" (
    "realm_id" varchar(36) NOT NULL,
    "value" varchar(255) NOT NULL,
    PRIMARY KEY ("realm_id","value")
);

DROP TABLE IF EXISTS "public"."realm_events_listeners";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_events_listeners" (
    "realm_id" varchar(36) NOT NULL,
    "value" varchar(255) NOT NULL,
    PRIMARY KEY ("realm_id","value")
);

DROP TABLE IF EXISTS "public"."realm_localizations";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_localizations" (
    "realm_id" varchar(255) NOT NULL,
    "locale" varchar(255) NOT NULL,
    "texts" text NOT NULL,
    PRIMARY KEY ("realm_id","locale")
);

DROP TABLE IF EXISTS "public"."realm_required_credential";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_required_credential" (
    "type" varchar(255) NOT NULL,
    "form_label" varchar(255),
    "input" bool NOT NULL DEFAULT false,
    "secret" bool NOT NULL DEFAULT false,
    "realm_id" varchar(36) NOT NULL,
    PRIMARY KEY ("realm_id","type")
);

DROP TABLE IF EXISTS "public"."realm_smtp_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_smtp_config" (
    "realm_id" varchar(36) NOT NULL,
    "value" varchar(255),
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("realm_id","name")
);

DROP TABLE IF EXISTS "public"."realm_supported_locales";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."realm_supported_locales" (
    "realm_id" varchar(36) NOT NULL,
    "value" varchar(255) NOT NULL,
    PRIMARY KEY ("realm_id","value")
);

DROP TABLE IF EXISTS "public"."redirect_uris";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."redirect_uris" (
    "client_id" varchar(36) NOT NULL,
    "value" varchar(255) NOT NULL,
    PRIMARY KEY ("client_id","value")
);

DROP TABLE IF EXISTS "public"."required_action_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."required_action_config" (
    "required_action_id" varchar(36) NOT NULL,
    "value" text,
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("required_action_id","name")
);

DROP TABLE IF EXISTS "public"."required_action_provider";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."required_action_provider" (
    "id" varchar(36) NOT NULL,
    "alias" varchar(255),
    "name" varchar(255),
    "realm_id" varchar(36),
    "enabled" bool NOT NULL DEFAULT false,
    "default_action" bool NOT NULL DEFAULT false,
    "provider_id" varchar(255),
    "priority" int4,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_attribute";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_attribute" (
    "id" varchar(36) NOT NULL DEFAULT 'sybase-needs-something-here'::character varying,
    "name" varchar(255) NOT NULL,
    "value" varchar(255),
    "resource_id" varchar(36) NOT NULL,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_policy";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_policy" (
    "resource_id" varchar(36) NOT NULL,
    "policy_id" varchar(36) NOT NULL,
    PRIMARY KEY ("resource_id","policy_id")
);

DROP TABLE IF EXISTS "public"."resource_scope";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_scope" (
    "resource_id" varchar(36) NOT NULL,
    "scope_id" varchar(36) NOT NULL,
    PRIMARY KEY ("resource_id","scope_id")
);

DROP TABLE IF EXISTS "public"."resource_server";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_server" (
    "id" varchar(36) NOT NULL,
    "allow_rs_remote_mgmt" bool NOT NULL DEFAULT false,
    "policy_enforce_mode" int2 NOT NULL,
    "decision_strategy" int2 NOT NULL DEFAULT 1,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_server_perm_ticket";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_server_perm_ticket" (
    "id" varchar(36) NOT NULL,
    "owner" varchar(255) NOT NULL,
    "requester" varchar(255) NOT NULL,
    "created_timestamp" int8 NOT NULL,
    "granted_timestamp" int8,
    "resource_id" varchar(36) NOT NULL,
    "scope_id" varchar(36),
    "resource_server_id" varchar(36) NOT NULL,
    "policy_id" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_server_policy";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_server_policy" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "description" varchar(255),
    "type" varchar(255) NOT NULL,
    "decision_strategy" int2,
    "logic" int2,
    "resource_server_id" varchar(36) NOT NULL,
    "owner" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_server_resource";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_server_resource" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "type" varchar(255),
    "icon_uri" varchar(255),
    "owner" varchar(255) NOT NULL,
    "resource_server_id" varchar(36) NOT NULL,
    "owner_managed_access" bool NOT NULL DEFAULT false,
    "display_name" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_server_scope";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_server_scope" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "icon_uri" varchar(255),
    "resource_server_id" varchar(36) NOT NULL,
    "display_name" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."resource_uris";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."resource_uris" (
    "resource_id" varchar(36) NOT NULL,
    "value" varchar(255) NOT NULL,
    PRIMARY KEY ("resource_id","value")
);

DROP TABLE IF EXISTS "public"."role_attribute";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."role_attribute" (
    "id" varchar(36) NOT NULL,
    "role_id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "value" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."scope_mapping";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."scope_mapping" (
    "client_id" varchar(36) NOT NULL,
    "role_id" varchar(36) NOT NULL,
    PRIMARY KEY ("client_id","role_id")
);

DROP TABLE IF EXISTS "public"."scope_policy";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."scope_policy" (
    "scope_id" varchar(36) NOT NULL,
    "policy_id" varchar(36) NOT NULL,
    PRIMARY KEY ("scope_id","policy_id")
);

DROP TABLE IF EXISTS "public"."user_attribute";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_attribute" (
    "name" varchar(255) NOT NULL,
    "value" varchar(255),
    "user_id" varchar(36) NOT NULL,
    "id" varchar(36) NOT NULL DEFAULT 'sybase-needs-something-here'::character varying,
    "long_value_hash" bytea,
    "long_value_hash_lower_case" bytea,
    "long_value" text,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."user_consent";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_consent" (
    "id" varchar(36) NOT NULL,
    "client_id" varchar(255),
    "user_id" varchar(36) NOT NULL,
    "created_date" int8,
    "last_updated_date" int8,
    "client_storage_provider" varchar(36),
    "external_client_id" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."user_consent_client_scope";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_consent_client_scope" (
    "user_consent_id" varchar(36) NOT NULL,
    "scope_id" varchar(36) NOT NULL,
    PRIMARY KEY ("user_consent_id","scope_id")
);

DROP TABLE IF EXISTS "public"."user_entity";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_entity" (
    "id" varchar(36) NOT NULL,
    "email" varchar(255),
    "email_constraint" varchar(255),
    "email_verified" bool NOT NULL DEFAULT false,
    "enabled" bool NOT NULL DEFAULT false,
    "federation_link" varchar(255),
    "first_name" varchar(255),
    "last_name" varchar(255),
    "realm_id" varchar(255),
    "username" varchar(255),
    "created_timestamp" int8,
    "service_account_client_link" varchar(255),
    "not_before" int4 NOT NULL DEFAULT 0,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."user_federation_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_federation_config" (
    "user_federation_provider_id" varchar(36) NOT NULL,
    "value" varchar(255),
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("user_federation_provider_id","name")
);

DROP TABLE IF EXISTS "public"."user_federation_mapper";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_federation_mapper" (
    "id" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "federation_provider_id" varchar(36) NOT NULL,
    "federation_mapper_type" varchar(255) NOT NULL,
    "realm_id" varchar(36) NOT NULL,
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."user_federation_mapper_config";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_federation_mapper_config" (
    "user_federation_mapper_id" varchar(36) NOT NULL,
    "value" varchar(255),
    "name" varchar(255) NOT NULL,
    PRIMARY KEY ("user_federation_mapper_id","name")
);

DROP TABLE IF EXISTS "public"."user_federation_provider";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_federation_provider" (
    "id" varchar(36) NOT NULL,
    "changed_sync_period" int4,
    "display_name" varchar(255),
    "full_sync_period" int4,
    "last_sync" int4,
    "priority" int4,
    "provider_name" varchar(255),
    "realm_id" varchar(36),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."user_group_membership";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_group_membership" (
    "group_id" varchar(36) NOT NULL,
    "user_id" varchar(36) NOT NULL,
    PRIMARY KEY ("group_id","user_id")
);

DROP TABLE IF EXISTS "public"."user_required_action";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_required_action" (
    "user_id" varchar(36) NOT NULL,
    "required_action" varchar(255) NOT NULL DEFAULT ' '::character varying,
    PRIMARY KEY ("required_action","user_id")
);

DROP TABLE IF EXISTS "public"."user_role_mapping";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_role_mapping" (
    "role_id" varchar(255) NOT NULL,
    "user_id" varchar(36) NOT NULL,
    PRIMARY KEY ("role_id","user_id")
);

DROP TABLE IF EXISTS "public"."user_session";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_session" (
    "id" varchar(36) NOT NULL,
    "auth_method" varchar(255),
    "ip_address" varchar(255),
    "last_session_refresh" int4,
    "login_username" varchar(255),
    "realm_id" varchar(255),
    "remember_me" bool NOT NULL DEFAULT false,
    "started" int4,
    "user_id" varchar(255),
    "user_session_state" int4,
    "broker_session_id" varchar(255),
    "broker_user_id" varchar(255),
    PRIMARY KEY ("id")
);

DROP TABLE IF EXISTS "public"."user_session_note";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."user_session_note" (
    "user_session" varchar(36) NOT NULL,
    "name" varchar(255) NOT NULL,
    "value" varchar(2048),
    PRIMARY KEY ("user_session","name")
);

DROP TABLE IF EXISTS "public"."username_login_failure";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."username_login_failure" (
    "realm_id" varchar(36) NOT NULL,
    "username" varchar(255) NOT NULL,
    "failed_login_not_before" int4,
    "last_failure" int8,
    "last_ip_failure" varchar(255),
    "num_failures" int4,
    PRIMARY KEY ("realm_id","username")
);

DROP TABLE IF EXISTS "public"."web_origins";
-- This script only contains the table creation statements and does not fully represent the table in the database. Do not use it as a backup.

-- Table Definition
CREATE TABLE "public"."web_origins" (
    "client_id" varchar(36) NOT NULL,
    "value" varchar(255) NOT NULL,
    PRIMARY KEY ("client_id","value")
);

INSERT INTO "public"."authentication_execution" ("id", "alias", "authenticator", "realm_id", "flow_id", "requirement", "priority", "authenticator_flow", "auth_flow_id", "auth_config") VALUES
('010f2b5c-4706-40a1-be9e-8fef45975e7f', NULL, 'client-secret-jwt', '5f22ffdd-e26c-45a8-80c1-382f149facab', '761dcf0d-bd44-405e-8e2e-744ce7f7c5e1', 2, 30, 'f', NULL, NULL),
('03c12b90-789f-4ff8-b767-db98f8a4d291', NULL, 'docker-http-basic-authenticator', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f3583716-db08-48ea-b8a6-ed6dd10f9461', 0, 10, 'f', NULL, NULL),
('06cc4a90-4a2c-4b53-8616-1ed170cc675b', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', 'fe9841ce-0852-4eca-85b9-c46e13385dcb', 2, 20, 't', '5e04cdc5-46b4-4ac3-b593-7a80c13d74b0', NULL),
('0702c941-6968-4582-96d6-bb2876843093', NULL, 'direct-grant-validate-otp', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '7cf3af6b-d6ac-4619-aa1f-3a9d9fdb5b34', 0, 20, 'f', NULL, NULL),
('087bc9fb-b4e8-4522-8fdb-3f85b90daeb9', NULL, 'conditional-user-configured', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '0da6d6e0-d5d0-4925-8000-4042dec7e00d', 0, 10, 'f', NULL, NULL),
('08f65a06-1e9f-4626-8942-a293dc36ae0d', NULL, 'http-basic-authenticator', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'e1f099c8-1a5a-4f81-b14a-327eb39bbd3c', 0, 10, 'f', NULL, NULL),
('0925f3be-41a3-48db-b946-5241c9c8a1b5', NULL, 'registration-recaptcha-action', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'd4797e3f-f3c4-40dc-a485-e51722f22058', 3, 60, 'f', NULL, NULL),
('0a410b78-f63b-45ca-87de-1f63f142b5bc', NULL, 'direct-grant-validate-otp', '5f22ffdd-e26c-45a8-80c1-382f149facab', '16d7be19-dcb0-41a1-b165-4f57ea082c6b', 0, 20, 'f', NULL, NULL),
('0c8b0186-cfe8-4115-ac4e-582ceb3988e6', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', '5e04cdc5-46b4-4ac3-b593-7a80c13d74b0', 1, 20, 't', '0b5b0bd0-cd4f-430e-97bc-e03a5ddda5c7', NULL),
('0dc1a297-29c9-4fcc-9cd4-312f7feb1162', NULL, 'http-basic-authenticator', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a6c03e8d-da77-4233-a5fb-a00a725b58f6', 0, 10, 'f', NULL, NULL),
('0dfbc133-3b8f-4628-babb-eb41e1f0a539', NULL, 'reset-credentials-choose-user', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a1b857d5-9e63-4fec-9875-3b9fe4678176', 0, 10, 'f', NULL, NULL),
('1189accb-5b89-42bf-8ebb-2ab2e27bf531', NULL, 'auth-otp-form', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'b4f03f43-4109-47ac-9b28-b75118bfc1df', 0, 20, 'f', NULL, NULL),
('179a589b-97b2-43c3-af19-47b2b3b01d5b', NULL, 'registration-password-action', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'd4797e3f-f3c4-40dc-a485-e51722f22058', 0, 50, 'f', NULL, NULL),
('1a301599-fa03-4f45-b631-bcbe91434cc9', NULL, 'auth-otp-form', '5f22ffdd-e26c-45a8-80c1-382f149facab', '3c00fc11-c562-425b-97d4-25567a9a305a', 0, 20, 'f', NULL, NULL),
('22ef28a3-1054-4ddf-bb53-3428a8f0c0e5', NULL, 'conditional-user-configured', '5f22ffdd-e26c-45a8-80c1-382f149facab', '3c00fc11-c562-425b-97d4-25567a9a305a', 0, 10, 'f', NULL, NULL),
('2912cf21-2d8a-4aa9-bed1-c4a44a4b030c', NULL, 'conditional-user-configured', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'ed64e746-9f51-4eec-96dc-aad2dfc4ea64', 0, 10, 'f', NULL, NULL),
('2c64b757-55a0-48f7-9807-52224f271c02', NULL, 'registration-terms-and-conditions', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4385a249-c491-495f-a011-061d7eb944bc', 3, 70, 'f', NULL, NULL),
('31a757e1-42b4-4717-9dd0-3e76c3bdea60', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '24d0b0fc-1390-4645-8051-60cb448acf93', 0, 20, 't', '1e76bc37-4d8f-4a5c-99f4-95b208a0df15', NULL),
('34662ccd-f84a-4075-97d8-4869d53c42b7', NULL, 'identity-provider-redirector', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'e2e271a6-ca14-4572-b3c7-c48dedc6fb08', 2, 25, 'f', NULL, NULL),
('373725f1-ffe9-46e9-9d0c-fb96877000c4', NULL, 'registration-user-creation', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4385a249-c491-495f-a011-061d7eb944bc', 0, 20, 'f', NULL, NULL),
('37b38007-fc65-4b6b-a008-024aa8995b63', NULL, 'conditional-user-configured', '5f22ffdd-e26c-45a8-80c1-382f149facab', '16d7be19-dcb0-41a1-b165-4f57ea082c6b', 0, 10, 'f', NULL, NULL),
('3906e2fd-5b16-4792-9051-860f1efd8595', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd47b7b71-3b3e-402d-a1a3-148550fc5ebb', 1, 20, 't', 'ff2b8116-21ce-49db-94dd-2c5abf746b8b', NULL),
('3a56658f-26c1-4098-b456-07540a41c01f', NULL, 'conditional-user-configured', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'ff2b8116-21ce-49db-94dd-2c5abf746b8b', 0, 10, 'f', NULL, NULL),
('3ae8afb1-f755-41e0-aad6-b71e4a21e978', NULL, 'direct-grant-validate-password', '5f22ffdd-e26c-45a8-80c1-382f149facab', '8f960c80-811b-4ad2-b3cf-b28ecba73619', 0, 20, 'f', NULL, NULL),
('3b0785f0-52b8-4cca-94a7-b6a87a016bd8', NULL, 'conditional-user-configured', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '7cf3af6b-d6ac-4619-aa1f-3a9d9fdb5b34', 0, 10, 'f', NULL, NULL),
('3d499d2c-2f53-4432-b0e7-ae434c0e1f43', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '2ab1bbad-7868-43e9-818e-e3e13e30ac78', 1, 40, 't', '0da6d6e0-d5d0-4925-8000-4042dec7e00d', NULL),
('3d5b82c5-c13a-482f-b7c6-881065424eb1', NULL, 'registration-terms-and-conditions', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'd4797e3f-f3c4-40dc-a485-e51722f22058', 3, 70, 'f', NULL, NULL),
('4191df48-d72d-40b1-aa49-51e89e485ba7', NULL, 'idp-review-profile', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '24d0b0fc-1390-4645-8051-60cb448acf93', 0, 10, 'f', NULL, '5a200504-d3cd-414a-aee9-fb425e69397b'),
('439346e8-58e2-43bc-b3e5-31b0b450023e', NULL, 'auth-otp-form', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'ff2b8116-21ce-49db-94dd-2c5abf746b8b', 0, 20, 'f', NULL, NULL),
('47b88ee3-9dea-4d62-a13b-c25d6060d6c0', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', '98708ad2-9682-43ac-b942-0ce6a5330fbe', 2, 20, 't', '7832b11d-e540-4149-ad16-94b1c9f89452', NULL),
('48f234b9-278d-4854-827d-181a7455fad1', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', '7832b11d-e540-4149-ad16-94b1c9f89452', 0, 20, 't', 'fe9841ce-0852-4eca-85b9-c46e13385dcb', NULL),
('4c19e833-0d5c-4397-b95f-d1708ad62179', NULL, 'idp-confirm-link', '5f22ffdd-e26c-45a8-80c1-382f149facab', '7832b11d-e540-4149-ad16-94b1c9f89452', 0, 10, 'f', NULL, NULL),
('54fb4bf4-d8fe-4851-a1af-0e7b727aab46', NULL, 'client-jwt', '5f22ffdd-e26c-45a8-80c1-382f149facab', '761dcf0d-bd44-405e-8e2e-744ce7f7c5e1', 2, 20, 'f', NULL, NULL),
('5579868a-75de-4dc8-8828-bcfb62d2abe0', NULL, 'idp-create-user-if-unique', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1e76bc37-4d8f-4a5c-99f4-95b208a0df15', 2, 10, 'f', NULL, '00477eee-9e1a-40a8-8711-049927828e19'),
('55ec77d1-553c-458a-9144-b5840c27e92f', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a1b857d5-9e63-4fec-9875-3b9fe4678176', 1, 40, 't', 'ed64e746-9f51-4eec-96dc-aad2dfc4ea64', NULL),
('56dd079a-387d-479a-be2b-fe0e7dd8f391', NULL, 'idp-username-password-form', '5f22ffdd-e26c-45a8-80c1-382f149facab', '5e04cdc5-46b4-4ac3-b593-7a80c13d74b0', 0, 10, 'f', NULL, NULL),
('5ea8d5c8-d6fd-4c29-ae56-03b68c6ace00', NULL, 'auth-username-password-form', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '9557f5bb-11d1-47a6-890b-0a8f254e33cc', 0, 10, 'f', NULL, NULL),
('60779add-6fa5-4ec7-a34f-58b9f20319f1', NULL, 'auth-spnego', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'e2e271a6-ca14-4572-b3c7-c48dedc6fb08', 3, 20, 'f', NULL, NULL),
('60ded654-6dc6-459a-aa20-25bbd959fc3d', NULL, 'registration-recaptcha-action', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4385a249-c491-495f-a011-061d7eb944bc', 3, 60, 'f', NULL, NULL),
('630ba7ad-47dc-444a-81e4-fb6531812f26', NULL, 'reset-credential-email', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '2ab1bbad-7868-43e9-818e-e3e13e30ac78', 0, 20, 'f', NULL, NULL),
('69d3bdc9-c822-41d4-8303-ee35611b2678', NULL, 'direct-grant-validate-username', '5f22ffdd-e26c-45a8-80c1-382f149facab', '8f960c80-811b-4ad2-b3cf-b28ecba73619', 0, 10, 'f', NULL, NULL),
('6a1d528a-200b-4224-8f23-f824a6224a53', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4cf88c06-b446-49e9-b478-18ae676aec57', 1, 30, 't', '7cf3af6b-d6ac-4619-aa1f-3a9d9fdb5b34', NULL),
('77cdd30b-e272-4d1f-8f53-50a40b1eac07', NULL, 'registration-user-creation', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'd4797e3f-f3c4-40dc-a485-e51722f22058', 0, 20, 'f', NULL, NULL),
('7cd2e48d-e73d-4c32-a7f5-59a501e77e5f', NULL, 'client-secret', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd061b399-f617-4e31-b021-71151b9cc7e8', 2, 10, 'f', NULL, NULL),
('80a3dcfb-dc74-4bda-a9c6-2f1052bec152', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '979e30c1-a2ee-4c47-a0ce-d3b94ed73bfa', 2, 20, 't', 'd47b7b71-3b3e-402d-a1a3-148550fc5ebb', NULL),
('84068806-5182-473e-b99c-520be0a4f290', NULL, 'idp-create-user-if-unique', '5f22ffdd-e26c-45a8-80c1-382f149facab', '98708ad2-9682-43ac-b942-0ce6a5330fbe', 2, 10, 'f', NULL, 'a8c161a0-bff3-4402-9d54-d12894e271d8'),
('84ff63c9-b0a9-4afc-8af5-5bc8868e7c1d', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', '8f960c80-811b-4ad2-b3cf-b28ecba73619', 1, 30, 't', '16d7be19-dcb0-41a1-b165-4f57ea082c6b', NULL),
('85ecaf3a-e7cd-4f55-9a66-bedb9f2e77b1', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', '811fe0f4-f25d-4ef3-a294-9f944cfb0398', 2, 30, 't', '262d0cab-87d3-4ef3-866b-6fcfa3965052', NULL),
('89250b67-58e3-414d-b079-3beec1779ca8', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '37696545-14fe-4996-b7ac-69a4ca8577cd', 0, 20, 't', '979e30c1-a2ee-4c47-a0ce-d3b94ed73bfa', NULL),
('98be0e01-ffbd-4bb1-91f6-106320aed7ee', NULL, 'reset-credentials-choose-user', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '2ab1bbad-7868-43e9-818e-e3e13e30ac78', 0, 10, 'f', NULL, NULL),
('9a804bba-fd3d-4417-a648-0cae26b3114e', NULL, 'reset-password', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a1b857d5-9e63-4fec-9875-3b9fe4678176', 0, 30, 'f', NULL, NULL),
('a9d7ecc0-482b-4d33-a65c-29cfec90843d', NULL, 'idp-review-profile', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'cbed1e10-0430-4e63-9298-b9ce56f96198', 0, 10, 'f', NULL, '5eeee9a1-52ae-4d03-9937-c365bcd85634'),
('b0c3659c-8bde-4f21-9904-88c3aa013227', NULL, 'idp-email-verification', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '979e30c1-a2ee-4c47-a0ce-d3b94ed73bfa', 2, 10, 'f', NULL, NULL),
('b2bd1b7f-034b-4ced-9276-d55711894a53', NULL, 'auth-username-password-form', '5f22ffdd-e26c-45a8-80c1-382f149facab', '262d0cab-87d3-4ef3-866b-6fcfa3965052', 0, 10, 'f', NULL, NULL),
('b3599e2e-6a26-4f1f-831f-093037b446a7', NULL, 'reset-password', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '2ab1bbad-7868-43e9-818e-e3e13e30ac78', 0, 30, 'f', NULL, NULL),
('b59978b4-3dea-4822-ac0d-19d52b1e0fcd', NULL, 'idp-confirm-link', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '37696545-14fe-4996-b7ac-69a4ca8577cd', 0, 10, 'f', NULL, NULL),
('ba95c46b-43f0-4bba-bc73-c68a4bf811c7', NULL, 'identity-provider-redirector', '5f22ffdd-e26c-45a8-80c1-382f149facab', '811fe0f4-f25d-4ef3-a294-9f944cfb0398', 2, 25, 'f', NULL, NULL),
('bc91899c-fef1-4a47-b1fa-6a142ee5f2fc', NULL, 'reset-otp', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'ed64e746-9f51-4eec-96dc-aad2dfc4ea64', 0, 20, 'f', NULL, NULL),
('be9f5688-aa9b-4734-86f1-8931a2140c3f', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', '262d0cab-87d3-4ef3-866b-6fcfa3965052', 1, 20, 't', '3c00fc11-c562-425b-97d4-25567a9a305a', NULL),
('c1e6a116-9e9c-43bd-9636-59f3b33bdcf1', NULL, 'auth-spnego', '5f22ffdd-e26c-45a8-80c1-382f149facab', '811fe0f4-f25d-4ef3-a294-9f944cfb0398', 3, 20, 'f', NULL, NULL),
('c507ab16-826c-4422-8320-e6f59db8ac3b', NULL, 'registration-page-form', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'e1554650-f0de-437a-b820-ad569fb07513', 0, 10, 't', 'd4797e3f-f3c4-40dc-a485-e51722f22058', NULL),
('c9a5b739-b819-4550-a661-d3e1c6bb748b', NULL, 'reset-otp', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '0da6d6e0-d5d0-4925-8000-4042dec7e00d', 0, 20, 'f', NULL, NULL),
('c9f67f71-db6c-4f91-a9ec-f3eace8d2caa', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '9557f5bb-11d1-47a6-890b-0a8f254e33cc', 1, 20, 't', 'b4f03f43-4109-47ac-9b28-b75118bfc1df', NULL),
('d1c49d1a-5f35-4dc6-bc3a-61df74a6e188', NULL, 'client-secret-jwt', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd061b399-f617-4e31-b021-71151b9cc7e8', 2, 30, 'f', NULL, NULL),
('d23f74da-98a7-4860-bf9f-d5d23e73e9aa', NULL, 'registration-page-form', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1c3df4bc-935c-4c14-8e45-e0a6d682dcda', 0, 10, 't', '4385a249-c491-495f-a011-061d7eb944bc', NULL),
('d7cccf7a-7a5c-4bca-b1d9-5151c316e0b3', NULL, 'docker-http-basic-authenticator', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'b18a61a0-d71b-4a8b-9496-6544e0b29caa', 0, 10, 'f', NULL, NULL),
('d88e1473-f334-4dbd-b57a-c959f45c8020', NULL, 'reset-credential-email', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a1b857d5-9e63-4fec-9875-3b9fe4678176', 0, 20, 'f', NULL, NULL),
('db71af7f-a731-4931-9582-c6dbfc738fa1', NULL, 'conditional-user-configured', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'b4f03f43-4109-47ac-9b28-b75118bfc1df', 0, 10, 'f', NULL, NULL),
('dd8ada08-e497-4c77-9de3-270f4acf86c4', NULL, 'auth-cookie', '5f22ffdd-e26c-45a8-80c1-382f149facab', '811fe0f4-f25d-4ef3-a294-9f944cfb0398', 2, 10, 'f', NULL, NULL),
('dedc13bc-e4dc-4434-a744-9cee8ddf7c28', NULL, 'idp-username-password-form', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd47b7b71-3b3e-402d-a1a3-148550fc5ebb', 0, 10, 'f', NULL, NULL),
('df6d75c3-7be1-49e8-bb1f-a48de28aa823', NULL, 'conditional-user-configured', '5f22ffdd-e26c-45a8-80c1-382f149facab', '0b5b0bd0-cd4f-430e-97bc-e03a5ddda5c7', 0, 10, 'f', NULL, NULL),
('e29aeab1-76ea-451c-9c87-7a2d98cdf71d', NULL, 'direct-grant-validate-username', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4cf88c06-b446-49e9-b478-18ae676aec57', 0, 10, 'f', NULL, NULL),
('e8adb7d4-a638-49b4-810b-6cdf173b2a28', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1e76bc37-4d8f-4a5c-99f4-95b208a0df15', 2, 20, 't', '37696545-14fe-4996-b7ac-69a4ca8577cd', NULL),
('eadc6cb0-7e21-4b31-82c4-cc3f840a159f', NULL, 'client-jwt', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd061b399-f617-4e31-b021-71151b9cc7e8', 2, 20, 'f', NULL, NULL),
('ed702525-31fd-409b-ac5a-6f208b5aca0d', NULL, 'client-secret', '5f22ffdd-e26c-45a8-80c1-382f149facab', '761dcf0d-bd44-405e-8e2e-744ce7f7c5e1', 2, 10, 'f', NULL, NULL),
('f10b2c76-cf80-4738-ad52-1b87f879d953', NULL, 'client-x509', '5f22ffdd-e26c-45a8-80c1-382f149facab', '761dcf0d-bd44-405e-8e2e-744ce7f7c5e1', 2, 40, 'f', NULL, NULL),
('f26c6eed-afe9-49e8-bb19-fe74d11de00a', NULL, 'registration-password-action', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4385a249-c491-495f-a011-061d7eb944bc', 0, 50, 'f', NULL, NULL),
('f2a622ed-83ea-4360-bb2b-e0f8bd4fdeb6', NULL, 'auth-cookie', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'e2e271a6-ca14-4572-b3c7-c48dedc6fb08', 2, 10, 'f', NULL, NULL),
('f39639c5-6e34-4c0b-99e3-15ed9ed74f98', NULL, 'auth-otp-form', '5f22ffdd-e26c-45a8-80c1-382f149facab', '0b5b0bd0-cd4f-430e-97bc-e03a5ddda5c7', 0, 20, 'f', NULL, NULL),
('f4639dd3-de37-4cd3-9131-6e0452b999a9', NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'e2e271a6-ca14-4572-b3c7-c48dedc6fb08', 2, 30, 't', '9557f5bb-11d1-47a6-890b-0a8f254e33cc', NULL),
('f488b68e-09f8-4351-a0c8-bc6619561ead', NULL, NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', 'cbed1e10-0430-4e63-9298-b9ce56f96198', 0, 20, 't', '98708ad2-9682-43ac-b942-0ce6a5330fbe', NULL),
('f4f0de6d-8230-4cbe-9785-cb8b65392a6c', NULL, 'direct-grant-validate-password', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '4cf88c06-b446-49e9-b478-18ae676aec57', 0, 20, 'f', NULL, NULL),
('f67394bc-2228-42b7-b3d2-a08d1a94c3d4', NULL, 'client-x509', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd061b399-f617-4e31-b021-71151b9cc7e8', 2, 40, 'f', NULL, NULL),
('ffe4f541-c098-4145-b6aa-6155051b8e42', NULL, 'idp-email-verification', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'fe9841ce-0852-4eca-85b9-c46e13385dcb', 2, 10, 'f', NULL, NULL);

INSERT INTO "public"."authentication_flow" ("id", "alias", "description", "realm_id", "provider_id", "top_level", "built_in") VALUES
('0b5b0bd0-cd4f-430e-97bc-e03a5ddda5c7', 'First broker login - Conditional OTP', 'Flow to determine if the OTP is required for the authentication', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('0da6d6e0-d5d0-4925-8000-4042dec7e00d', 'Reset - Conditional OTP', 'Flow to determine if the OTP should be reset or not. Set to REQUIRED to force.', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('16d7be19-dcb0-41a1-b165-4f57ea082c6b', 'Direct Grant - Conditional OTP', 'Flow to determine if the OTP is required for the authentication', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('1c3df4bc-935c-4c14-8e45-e0a6d682dcda', 'registration', 'registration flow', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('1e76bc37-4d8f-4a5c-99f4-95b208a0df15', 'User creation or linking', 'Flow for the existing/non-existing user alternatives', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('24d0b0fc-1390-4645-8051-60cb448acf93', 'first broker login', 'Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('262d0cab-87d3-4ef3-866b-6fcfa3965052', 'forms', 'Username, password, otp and other auth forms.', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('2ab1bbad-7868-43e9-818e-e3e13e30ac78', 'reset credentials', 'Reset credentials for a user if they forgot their password or something', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('37696545-14fe-4996-b7ac-69a4ca8577cd', 'Handle Existing Account', 'Handle what to do if there is existing account with same email/username like authenticated identity provider', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('3c00fc11-c562-425b-97d4-25567a9a305a', 'Browser - Conditional OTP', 'Flow to determine if the OTP is required for the authentication', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('4385a249-c491-495f-a011-061d7eb944bc', 'registration form', 'registration form', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'form-flow', 'f', 't'),
('4cf88c06-b446-49e9-b478-18ae676aec57', 'direct grant', 'OpenID Connect Resource Owner Grant', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('5e04cdc5-46b4-4ac3-b593-7a80c13d74b0', 'Verify Existing Account by Re-authentication', 'Reauthentication of existing account', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('761dcf0d-bd44-405e-8e2e-744ce7f7c5e1', 'clients', 'Base authentication for clients', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'client-flow', 't', 't'),
('7832b11d-e540-4149-ad16-94b1c9f89452', 'Handle Existing Account', 'Handle what to do if there is existing account with same email/username like authenticated identity provider', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('7cf3af6b-d6ac-4619-aa1f-3a9d9fdb5b34', 'Direct Grant - Conditional OTP', 'Flow to determine if the OTP is required for the authentication', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('811fe0f4-f25d-4ef3-a294-9f944cfb0398', 'browser', 'browser based authentication', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('8f960c80-811b-4ad2-b3cf-b28ecba73619', 'direct grant', 'OpenID Connect Resource Owner Grant', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('9557f5bb-11d1-47a6-890b-0a8f254e33cc', 'forms', 'Username, password, otp and other auth forms.', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('979e30c1-a2ee-4c47-a0ce-d3b94ed73bfa', 'Account verification options', 'Method with which to verity the existing account', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('98708ad2-9682-43ac-b942-0ce6a5330fbe', 'User creation or linking', 'Flow for the existing/non-existing user alternatives', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('a1b857d5-9e63-4fec-9875-3b9fe4678176', 'reset credentials', 'Reset credentials for a user if they forgot their password or something', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('a6c03e8d-da77-4233-a5fb-a00a725b58f6', 'saml ecp', 'SAML ECP Profile Authentication Flow', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('b18a61a0-d71b-4a8b-9496-6544e0b29caa', 'docker auth', 'Used by Docker clients to authenticate against the IDP', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('b4f03f43-4109-47ac-9b28-b75118bfc1df', 'Browser - Conditional OTP', 'Flow to determine if the OTP is required for the authentication', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('cbed1e10-0430-4e63-9298-b9ce56f96198', 'first broker login', 'Actions taken after first broker login with identity provider account, which is not yet linked to any Keycloak account', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('d061b399-f617-4e31-b021-71151b9cc7e8', 'clients', 'Base authentication for clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'client-flow', 't', 't'),
('d4797e3f-f3c4-40dc-a485-e51722f22058', 'registration form', 'registration form', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'form-flow', 'f', 't'),
('d47b7b71-3b3e-402d-a1a3-148550fc5ebb', 'Verify Existing Account by Re-authentication', 'Reauthentication of existing account', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't'),
('e1554650-f0de-437a-b820-ad569fb07513', 'registration', 'registration flow', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 't', 't'),
('e1f099c8-1a5a-4f81-b14a-327eb39bbd3c', 'saml ecp', 'SAML ECP Profile Authentication Flow', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('e2e271a6-ca14-4572-b3c7-c48dedc6fb08', 'browser', 'browser based authentication', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('ed64e746-9f51-4eec-96dc-aad2dfc4ea64', 'Reset - Conditional OTP', 'Flow to determine if the OTP should be reset or not. Set to REQUIRED to force.', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('f3583716-db08-48ea-b8a6-ed6dd10f9461', 'docker auth', 'Used by Docker clients to authenticate against the IDP', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 't', 't'),
('fe9841ce-0852-4eca-85b9-c46e13385dcb', 'Account verification options', 'Method with which to verity the existing account', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'basic-flow', 'f', 't'),
('ff2b8116-21ce-49db-94dd-2c5abf746b8b', 'First broker login - Conditional OTP', 'Flow to determine if the OTP is required for the authentication', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'basic-flow', 'f', 't');

INSERT INTO "public"."authenticator_config" ("id", "alias", "realm_id") VALUES
('00477eee-9e1a-40a8-8711-049927828e19', 'create unique user config', 'b8300271-af6f-4da4-9541-bc4ee4f0779f'),
('5a200504-d3cd-414a-aee9-fb425e69397b', 'review profile config', 'b8300271-af6f-4da4-9541-bc4ee4f0779f'),
('5eeee9a1-52ae-4d03-9937-c365bcd85634', 'review profile config', '5f22ffdd-e26c-45a8-80c1-382f149facab'),
('a8c161a0-bff3-4402-9d54-d12894e271d8', 'create unique user config', '5f22ffdd-e26c-45a8-80c1-382f149facab');

INSERT INTO "public"."authenticator_config_entry" ("authenticator_id", "value", "name") VALUES
('00477eee-9e1a-40a8-8711-049927828e19', 'false', 'require.password.update.after.registration'),
('5a200504-d3cd-414a-aee9-fb425e69397b', 'missing', 'update.profile.on.first.login'),
('5eeee9a1-52ae-4d03-9937-c365bcd85634', 'missing', 'update.profile.on.first.login'),
('a8c161a0-bff3-4402-9d54-d12894e271d8', 'false', 'require.password.update.after.registration');

INSERT INTO "public"."client" ("id", "enabled", "full_scope_allowed", "client_id", "not_before", "public_client", "secret", "base_url", "bearer_only", "management_url", "surrogate_auth_required", "realm_id", "protocol", "node_rereg_timeout", "frontchannel_logout", "consent_required", "name", "service_accounts_enabled", "client_authenticator_type", "root_url", "description", "registration_token", "standard_flow_enabled", "implicit_flow_enabled", "direct_access_grants_enabled", "always_display_in_console") VALUES
('0cbca4c2-c802-473b-b7d2-15875ed6a364', 't', 'f', 'security-admin-console', 0, 't', NULL, '/admin/master/console/', 'f', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'openid-connect', 0, 'f', 'f', '${client_security-admin-console}', 'f', 'client-secret', '${authAdminUrl}', NULL, NULL, 't', 'f', 'f', 'f'),
('1042da59-0a62-4804-88cc-ee594466da90', 't', 'f', 'admin-cli', 0, 't', NULL, NULL, 'f', NULL, 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', 0, 'f', 'f', '${client_admin-cli}', 'f', 'client-secret', NULL, NULL, NULL, 'f', 'f', 't', 'f'),
('1acd0f60-85ea-485a-933a-16a294f49529', 't', 'f', 'account', 0, 't', NULL, '/realms/master/account/', 'f', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'openid-connect', 0, 'f', 'f', '${client_account}', 'f', 'client-secret', '${authBaseUrl}', NULL, NULL, 't', 'f', 'f', 'f'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', 't', 'f', 'admin-cli', 0, 't', NULL, NULL, 'f', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'openid-connect', 0, 'f', 'f', '${client_admin-cli}', 'f', 'client-secret', NULL, NULL, NULL, 'f', 'f', 't', 'f'),
('3d1829e6-92da-4617-bb14-77c94e568f64', 't', 'f', 'security-admin-console', 0, 't', NULL, '/admin/avenirs/console/', 'f', NULL, 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', 0, 'f', 'f', '${client_security-admin-console}', 'f', 'client-secret', '${authAdminUrl}', NULL, NULL, 't', 'f', 'f', 'f'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 't', 'f', 'account-console', 0, 't', NULL, '/realms/master/account/', 'f', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'openid-connect', 0, 'f', 'f', '${client_account-console}', 'f', 'client-secret', '${authBaseUrl}', NULL, NULL, 't', 'f', 'f', 'f'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 't', 'f', 'account-console', 0, 't', NULL, '/realms/avenirs/account/', 'f', NULL, 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', 0, 'f', 'f', '${client_account-console}', 'f', 'client-secret', '${authBaseUrl}', NULL, NULL, 't', 'f', 'f', 'f'),
('79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', 'f', 'avenirs-realm', 0, 'f', NULL, NULL, 't', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, 0, 'f', 'f', 'avenirs Realm', 'f', 'client-secret', NULL, NULL, NULL, 't', 'f', 'f', 'f'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 't', 't', 'mps-cli', 0, 'f', 'anfoYhY7PNxIhgEthcWdbq2P4XPJ90Aw', 'http://localhost:5001', 'f', 'http://localhost:5001', 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', -1, 't', 'f', 'mps-cli', 'f', 'client-secret', 'http://localhost:5001', 'mps-cli', NULL, 't', 'f', 'f', 'f'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', 't', 'f', 'realm-management', 0, 'f', NULL, NULL, 't', NULL, 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', 0, 'f', 'f', '${client_realm-management}', 'f', 'client-secret', NULL, NULL, NULL, 't', 'f', 'f', 'f'),
('bf1482b1-f598-4da3-982e-146e190880e1', 't', 'f', 'master-realm', 0, 'f', NULL, NULL, 't', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, 0, 'f', 'f', 'master Realm', 'f', 'client-secret', NULL, NULL, NULL, 't', 'f', 'f', 'f'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', 't', 'f', 'account', 0, 't', NULL, '/realms/avenirs/account/', 'f', NULL, 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', 0, 'f', 'f', '${client_account}', 'f', 'client-secret', '${authBaseUrl}', NULL, NULL, 't', 'f', 'f', 'f'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', 't', 'f', 'broker', 0, 'f', NULL, NULL, 't', NULL, 'f', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'openid-connect', 0, 'f', 'f', '${client_broker}', 'f', 'client-secret', NULL, NULL, NULL, 't', 'f', 'f', 'f'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', 't', 'f', 'broker', 0, 'f', NULL, NULL, 't', NULL, 'f', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'openid-connect', 0, 'f', 'f', '${client_broker}', 'f', 'client-secret', NULL, NULL, NULL, 't', 'f', 'f', 'f');

INSERT INTO "public"."client_attributes" ("client_id", "name", "value") VALUES
('0cbca4c2-c802-473b-b7d2-15875ed6a364', 'pkce.code.challenge.method', 'S256'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', 'post.logout.redirect.uris', '+'),
('1acd0f60-85ea-485a-933a-16a294f49529', 'post.logout.redirect.uris', '+'),
('3d1829e6-92da-4617-bb14-77c94e568f64', 'pkce.code.challenge.method', 'S256'),
('3d1829e6-92da-4617-bb14-77c94e568f64', 'post.logout.redirect.uris', '+'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 'pkce.code.challenge.method', 'S256'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 'post.logout.redirect.uris', '+'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 'pkce.code.challenge.method', 'S256'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 'post.logout.redirect.uris', '+'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'backchannel.logout.revoke.offline.tokens', 'false'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'backchannel.logout.session.required', 'true'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'client.secret.creation.time', '1721292270'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'display.on.consent.screen', 'false'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'oauth2.device.authorization.grant.enabled', 'false'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'oidc.ciba.grant.enabled', 'false'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'post.logout.redirect.uris', 'http://localhost:5001##https://www.monprojetsup.fr'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', 'post.logout.redirect.uris', '+');

INSERT INTO "public"."client_scope" ("id", "name", "realm_id", "description", "protocol") VALUES
('0f53a958-9a66-4148-9d6e-2ee8c9b3e49c', 'role_list', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'SAML role list', 'saml'),
('16fb9da1-1d10-47a6-a62e-4c9bad36c267', 'acr', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect scope for add acr (authentication context class reference) to the token', 'openid-connect'),
('1a671fb4-828d-4617-a071-239196ed6493', 'microprofile-jwt', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'Microprofile - JWT built-in scope', 'openid-connect'),
('26884290-b7d8-4952-b224-90a6ea20de6c', 'microprofile-jwt', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'Microprofile - JWT built-in scope', 'openid-connect'),
('284ff9bc-10a5-4cc4-9c20-154258f94211', 'address', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect built-in scope: address', 'openid-connect'),
('35489f79-ed93-44f2-9c13-e007406b8b43', 'offline_access', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect built-in scope: offline_access', 'openid-connect'),
('44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 'roles', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect scope for add user roles to the access token', 'openid-connect'),
('5fae990e-ff8e-4389-91db-3f48f3ff11d5', 'web-origins', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect scope for add allowed web origins to the access token', 'openid-connect'),
('78863cd8-8bb4-4296-9700-83618b5d1726', 'phone', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect built-in scope: phone', 'openid-connect'),
('7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'offline_access', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect built-in scope: offline_access', 'openid-connect'),
('820ed402-fc77-4525-a04c-bb4b9b86a8ba', 'web-origins', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect scope for add allowed web origins to the access token', 'openid-connect'),
('82e5650b-0e47-4554-892a-be40598874b8', 'profile', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect built-in scope: profile', 'openid-connect'),
('858023ed-373a-4766-9b08-e0a00cb8e3b3', 'email', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect built-in scope: email', 'openid-connect'),
('a40b0daf-520e-491f-bd6e-21f22088fa6e', 'roles', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect scope for add user roles to the access token', 'openid-connect'),
('a550d1b2-19a6-4952-877c-9d720393c8e0', 'role_list', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'SAML role list', 'saml'),
('a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 'profile', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect built-in scope: profile', 'openid-connect'),
('b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'phone', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect built-in scope: phone', 'openid-connect'),
('b62b91e2-e4ca-4c34-94e6-4d60300386f7', 'email', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect built-in scope: email', 'openid-connect'),
('d763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'address', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'OpenID Connect built-in scope: address', 'openid-connect'),
('d9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 'acr', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'OpenID Connect scope for add acr (authentication context class reference) to the token', 'openid-connect');

INSERT INTO "public"."client_scope_attributes" ("scope_id", "value", "name") VALUES
('0f53a958-9a66-4148-9d6e-2ee8c9b3e49c', '${samlRoleListScopeConsentText}', 'consent.screen.text'),
('0f53a958-9a66-4148-9d6e-2ee8c9b3e49c', 'true', 'display.on.consent.screen'),
('16fb9da1-1d10-47a6-a62e-4c9bad36c267', 'false', 'display.on.consent.screen'),
('16fb9da1-1d10-47a6-a62e-4c9bad36c267', 'false', 'include.in.token.scope'),
('1a671fb4-828d-4617-a071-239196ed6493', 'false', 'display.on.consent.screen'),
('1a671fb4-828d-4617-a071-239196ed6493', 'true', 'include.in.token.scope'),
('26884290-b7d8-4952-b224-90a6ea20de6c', 'false', 'display.on.consent.screen'),
('26884290-b7d8-4952-b224-90a6ea20de6c', 'true', 'include.in.token.scope'),
('284ff9bc-10a5-4cc4-9c20-154258f94211', '${addressScopeConsentText}', 'consent.screen.text'),
('284ff9bc-10a5-4cc4-9c20-154258f94211', 'true', 'display.on.consent.screen'),
('284ff9bc-10a5-4cc4-9c20-154258f94211', 'true', 'include.in.token.scope'),
('35489f79-ed93-44f2-9c13-e007406b8b43', '${offlineAccessScopeConsentText}', 'consent.screen.text'),
('35489f79-ed93-44f2-9c13-e007406b8b43', 'true', 'display.on.consent.screen'),
('44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', '${rolesScopeConsentText}', 'consent.screen.text'),
('44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 'true', 'display.on.consent.screen'),
('44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 'false', 'include.in.token.scope'),
('5fae990e-ff8e-4389-91db-3f48f3ff11d5', '', 'consent.screen.text'),
('5fae990e-ff8e-4389-91db-3f48f3ff11d5', 'false', 'display.on.consent.screen'),
('5fae990e-ff8e-4389-91db-3f48f3ff11d5', 'false', 'include.in.token.scope'),
('78863cd8-8bb4-4296-9700-83618b5d1726', '${phoneScopeConsentText}', 'consent.screen.text'),
('78863cd8-8bb4-4296-9700-83618b5d1726', 'true', 'display.on.consent.screen'),
('78863cd8-8bb4-4296-9700-83618b5d1726', 'true', 'include.in.token.scope'),
('7a613d18-ae4c-4613-b6f5-9e33c4915b92', '${offlineAccessScopeConsentText}', 'consent.screen.text'),
('7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'true', 'display.on.consent.screen'),
('820ed402-fc77-4525-a04c-bb4b9b86a8ba', '', 'consent.screen.text'),
('820ed402-fc77-4525-a04c-bb4b9b86a8ba', 'false', 'display.on.consent.screen'),
('820ed402-fc77-4525-a04c-bb4b9b86a8ba', 'false', 'include.in.token.scope'),
('82e5650b-0e47-4554-892a-be40598874b8', '${profileScopeConsentText}', 'consent.screen.text'),
('82e5650b-0e47-4554-892a-be40598874b8', 'true', 'display.on.consent.screen'),
('82e5650b-0e47-4554-892a-be40598874b8', 'true', 'include.in.token.scope'),
('858023ed-373a-4766-9b08-e0a00cb8e3b3', '${emailScopeConsentText}', 'consent.screen.text'),
('858023ed-373a-4766-9b08-e0a00cb8e3b3', 'true', 'display.on.consent.screen'),
('858023ed-373a-4766-9b08-e0a00cb8e3b3', 'true', 'include.in.token.scope'),
('a40b0daf-520e-491f-bd6e-21f22088fa6e', '${rolesScopeConsentText}', 'consent.screen.text'),
('a40b0daf-520e-491f-bd6e-21f22088fa6e', 'true', 'display.on.consent.screen'),
('a40b0daf-520e-491f-bd6e-21f22088fa6e', 'false', 'include.in.token.scope'),
('a550d1b2-19a6-4952-877c-9d720393c8e0', '${samlRoleListScopeConsentText}', 'consent.screen.text'),
('a550d1b2-19a6-4952-877c-9d720393c8e0', 'true', 'display.on.consent.screen'),
('a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', '${profileScopeConsentText}', 'consent.screen.text'),
('a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 'true', 'display.on.consent.screen'),
('a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 'true', 'include.in.token.scope'),
('b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', '${phoneScopeConsentText}', 'consent.screen.text'),
('b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'true', 'display.on.consent.screen'),
('b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'true', 'include.in.token.scope'),
('b62b91e2-e4ca-4c34-94e6-4d60300386f7', '${emailScopeConsentText}', 'consent.screen.text'),
('b62b91e2-e4ca-4c34-94e6-4d60300386f7', 'true', 'display.on.consent.screen'),
('b62b91e2-e4ca-4c34-94e6-4d60300386f7', 'true', 'include.in.token.scope'),
('d763d8f2-5e16-4c96-91c8-dd70d65a2f14', '${addressScopeConsentText}', 'consent.screen.text'),
('d763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'true', 'display.on.consent.screen'),
('d763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'true', 'include.in.token.scope'),
('d9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 'false', 'display.on.consent.screen'),
('d9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 'false', 'include.in.token.scope');

INSERT INTO "public"."client_scope_client" ("client_id", "scope_id", "default_scope") VALUES
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('0cbca4c2-c802-473b-b7d2-15875ed6a364', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f'),
('1042da59-0a62-4804-88cc-ee594466da90', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('1042da59-0a62-4804-88cc-ee594466da90', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('1042da59-0a62-4804-88cc-ee594466da90', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('1042da59-0a62-4804-88cc-ee594466da90', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('1042da59-0a62-4804-88cc-ee594466da90', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('1042da59-0a62-4804-88cc-ee594466da90', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('1042da59-0a62-4804-88cc-ee594466da90', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('1042da59-0a62-4804-88cc-ee594466da90', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('1042da59-0a62-4804-88cc-ee594466da90', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('1acd0f60-85ea-485a-933a-16a294f49529', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('1acd0f60-85ea-485a-933a-16a294f49529', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('1acd0f60-85ea-485a-933a-16a294f49529', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('1acd0f60-85ea-485a-933a-16a294f49529', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('1acd0f60-85ea-485a-933a-16a294f49529', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('1acd0f60-85ea-485a-933a-16a294f49529', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('1acd0f60-85ea-485a-933a-16a294f49529', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('1acd0f60-85ea-485a-933a-16a294f49529', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('1acd0f60-85ea-485a-933a-16a294f49529', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('1cc336ce-54ea-47f7-9bd7-2da066122b7f', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('3d1829e6-92da-4617-bb14-77c94e568f64', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('3d1829e6-92da-4617-bb14-77c94e568f64', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('3d1829e6-92da-4617-bb14-77c94e568f64', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('a9819d56-3d39-4dc4-865f-deecc78b7477', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('bf1482b1-f598-4da3-982e-146e190880e1', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('bf1482b1-f598-4da3-982e-146e190880e1', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('bf1482b1-f598-4da3-982e-146e190880e1', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('bf1482b1-f598-4da3-982e-146e190880e1', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('bf1482b1-f598-4da3-982e-146e190880e1', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('bf1482b1-f598-4da3-982e-146e190880e1', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('bf1482b1-f598-4da3-982e-146e190880e1', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('bf1482b1-f598-4da3-982e-146e190880e1', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('bf1482b1-f598-4da3-982e-146e190880e1', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('cc87bab5-4e4e-4c37-91c3-212ef60fae17', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't');

INSERT INTO "public"."client_scope_role_mapping" ("scope_id", "role_id") VALUES
('35489f79-ed93-44f2-9c13-e007406b8b43', '3cc61388-e5d5-4b6c-b980-6bc82a3e38c5'),
('7a613d18-ae4c-4613-b6f5-9e33c4915b92', '24df1b04-4f42-4ba4-9cd5-0fac5a474b0b');

INSERT INTO "public"."component" ("id", "name", "parent_id", "provider_id", "provider_type", "realm_id", "sub_type") VALUES
('097278af-2aa9-44b5-994c-e3c5c45766e4', 'aes-generated', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'aes-generated', 'org.keycloak.keys.KeyProvider', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL),
('0e0d8b16-9d93-4a23-a20d-8a3c767616cd', 'Allowed Client Scopes', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'allowed-client-templates', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'anonymous'),
('23ecf0eb-a828-461b-9bde-e791b23aee81', 'Max Clients Limit', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'max-clients', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'anonymous'),
('280dc286-1f74-48dc-a84e-67986be41f71', 'Allowed Protocol Mapper Types', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'allowed-protocol-mappers', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'authenticated'),
('281c3c67-1e8e-40b2-944e-5dafdf10fa24', 'hmac-generated-hs512', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'hmac-generated', 'org.keycloak.keys.KeyProvider', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL),
('32799757-7c40-4b89-984d-8f14744629d7', 'aes-generated', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'aes-generated', 'org.keycloak.keys.KeyProvider', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL),
('3604963e-3710-4061-926c-78c80d3a8072', 'Allowed Protocol Mapper Types', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'allowed-protocol-mappers', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'anonymous'),
('376ead5a-8bab-4733-babf-2864c7863f08', 'Consent Required', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'consent-required', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'anonymous'),
('42e4674c-c360-4c4d-bb87-adb6119aa6da', 'Full Scope Disabled', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'scope', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'anonymous'),
('4bbd43f6-67fc-419c-9c37-831ce8867a01', 'Allowed Client Scopes', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'allowed-client-templates', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'authenticated'),
('549e6f95-1530-4691-ad00-80412e20017f', 'Allowed Client Scopes', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'allowed-client-templates', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'authenticated'),
('68ddf526-c0be-4549-8c25-8d53fe233f4c', 'Trusted Hosts', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'trusted-hosts', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'anonymous'),
('79553a18-f224-4e18-ab10-1b972145d4ab', 'Allowed Protocol Mapper Types', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'allowed-protocol-mappers', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'authenticated'),
('8380c3af-97be-4b44-9eeb-a4d49b65e6f1', 'hmac-generated-hs512', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'hmac-generated', 'org.keycloak.keys.KeyProvider', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL),
('885e0475-71d5-406e-9bba-d422e4bef783', NULL, '5f22ffdd-e26c-45a8-80c1-382f149facab', 'declarative-user-profile', 'org.keycloak.userprofile.UserProfileProvider', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL),
('8f48bfcf-edc8-4927-bcb5-7d2a4536cbbc', 'Max Clients Limit', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'max-clients', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'anonymous'),
('8fa31cba-ab87-43ab-8a68-3894967968f4', 'Trusted Hosts', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'trusted-hosts', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'anonymous'),
('ad2fc437-0bcd-4f8b-8535-19ff86909d85', 'rsa-generated', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'rsa-generated', 'org.keycloak.keys.KeyProvider', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL),
('b319777c-3c9d-41d9-ab70-ef276ea02389', 'rsa-enc-generated', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'rsa-enc-generated', 'org.keycloak.keys.KeyProvider', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL),
('b71586f1-db75-4b6c-bad2-2542018d6a9d', NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'declarative-user-profile', 'org.keycloak.userprofile.UserProfileProvider', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL),
('bcd003b3-c854-4384-b216-1ae80fe3631a', 'Full Scope Disabled', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'scope', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'anonymous'),
('cdbf913a-a131-4efb-8195-f722f682ddb9', 'Consent Required', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'consent-required', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'anonymous'),
('cf7ad600-c4e2-49b5-b9b5-9620ca804f45', 'Allowed Client Scopes', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'allowed-client-templates', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'anonymous'),
('d06f064b-336a-43b0-9ecd-e3ef6a76ce17', 'rsa-generated', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'rsa-generated', 'org.keycloak.keys.KeyProvider', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL),
('dd7b287f-8e0f-4e3d-9f73-a6a88c2d0e5d', 'rsa-enc-generated', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'rsa-enc-generated', 'org.keycloak.keys.KeyProvider', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL),
('e0d3edbd-4e76-44b8-a739-e22b58750e41', 'Allowed Protocol Mapper Types', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'allowed-protocol-mappers', 'org.keycloak.services.clientregistration.policy.ClientRegistrationPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'anonymous');

INSERT INTO "public"."component_config" ("id", "component_id", "name", "value") VALUES
('029e89eb-0d76-44e6-b228-8595945a0b1b', '8fa31cba-ab87-43ab-8a68-3894967968f4', 'client-uris-must-match', 'true'),
('02d810f0-136b-4d86-9057-144bdd89635e', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'oidc-full-name-mapper'),
('115cf6db-3659-4743-a35c-eac60f0ed342', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'saml-role-list-mapper'),
('156d10dc-cfec-40f3-98f9-fbd7f5db1a33', 'b319777c-3c9d-41d9-ab70-ef276ea02389', 'privateKey', 'MIIEpAIBAAKCAQEAyypSB8R1UP58RpS7fvnemOjo4A/pzxzsn3KwS6wYwBwRTE1ODXJ3pXfEnsPr3QfnMXLnGYowx21hoqltlj9O2WFK/jAVOtx41hpVY2y5cWYPYQtO2BHmPxVwO36W9Rp34Zo51UytSNjAZyZSC2YGZm26YuZGUf26w7BF0Md4m79F/PeqZcRO/D/mfw05sgbV7XHKKJgEiC8dce/x2OUGtPcKRz74BnjRsU//Z8+CqfMrRHUPULND1OLSTdN2HGP+Etpvm6hWNVmntEh6L67gz8UqiN3qhAI7J8SmdoRRai4G6SSbMsQMoXnM9E1Gh79Mfcmyf3PU12izU3gaI1BJ6wIDAQABAoIBAAw7jhjdoVqxF/Z/+hIZJWf7jhn/m0pXKYAoPakwwYS/q+L2PJ7ep4mU/oe4sDjswuTdUEDfazR2x4wtu2VyhmLcUtGfP4f9wmDR0RZdc4T6ai6jqamk2kIuHkXWx2wWneU9jgHjzgM22c7cJdB94iDEtpZB2zYQmvAxauK5P+/QpuOLBTnNZzbw+LA8vbcmqJ2vk/YMKuzh7MNLaSlX/g8pYXuQLWrSJEQsWUjLMpVaRCEWE052ND9i4BHDTkXbDuiy9kJyKCKM7NElDONP+D8BoahgIkfeHhkldTOQo+VvBT1lNSABaEe3JMCHXc4MsL6cMt0JNE3ok9OWFbKZHfECgYEA+58yYGsCYLCK9I4IJjKmVAfXss45nL1kQUAirKZIAUZgnUJLL67Whi04z3lid9t2U9G6Ez9THHHyhIoHKjdJOeYHUZNoIL4hfYcFBBgsKZ1UHJQG1LweIcp0w2dQ0VybxE/Tfot+Z7tmCogIaPG3oKj4O+jSj2ZnrMSy7PGY/p8CgYEAzrNIawNHxMC7EUNRwzA0Rvef8/SBYBGU6A11ycFD66qRtob8SFepnsvNpzqPE9p/3AgPr95qOnyOgZ/EglC2Sfb8ciZTgRClliAqzm3em8GQzPNL8uCl0gyBbfUAabsem5TgUvDOg3rPWZrXl9FLWZ05K0CZqKEVrF6jW5najTUCgYB0iXHaiBUs1Pc2dsW9cz3ns04qSSsTtf/F3L9yePzmYkMC8p5B8lb3A+DI7q41VtaB0aO9oFmM1hV9XzQcUEjA4RHIrV3PtF9sdOlU0SE7ENvQ9JoJUysdeVgE3Efo/1xBWrtYY6DiyD9lZ0WF3VWvujJmBNnogoRH1z4LUYYLfwKBgQCJLYa0Lpskwtvjmw/aQFWRe6mWGAD8pu5yCuzPFwpijvLhdjhcMoGJt6wmfcCS5oHQnzD6ANvzMNLKyCN8cOBpuhbUEwd8MnYSagq+sT/5kr7spdMTv2+NNWt4dA7wHJU1n9o4cUQ9EfYme5L1WJtvP96/C8JunWlq2ewEUNkwuQKBgQDrDLxByILVVSgKklXzLYxJGz/Z2YezIsrzv2qij68xFuvFJuDCoZ6VAkrsYYFM/pYdXxnSlh+z7fNAWSBdmkFOD4o3e1bmuX5vb3gjuzZgKMUj2B17+xyB+G2I8sxKFYxKqPxPb8jGm/Vf1oPmM3RHsJCsOs4bTTX/nnQSlNyg6A=='),
('17aff1ff-ba28-427c-ba2b-4d39e5484070', 'b319777c-3c9d-41d9-ab70-ef276ea02389', 'keyUse', 'ENC'),
('191311b5-ee94-478a-a490-9318d8981b77', '32799757-7c40-4b89-984d-8f14744629d7', 'secret', 'oiJA3qzJvfKxFzOp2NbMyA'),
('1a1c86d5-d649-4211-9d0b-387c85fbf68e', 'ad2fc437-0bcd-4f8b-8535-19ff86909d85', 'keyUse', 'SIG'),
('1cfb74fb-c1f1-4ce1-9fbf-67ca39cfe00a', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'saml-user-attribute-mapper'),
('2004a180-583d-44ba-ae63-3ed5ebb605b6', '281c3c67-1e8e-40b2-944e-5dafdf10fa24', 'priority', '100'),
('20a6aa5d-453c-4121-b2d8-b863bfb98bc8', 'dd7b287f-8e0f-4e3d-9f73-a6a88c2d0e5d', 'certificate', 'MIICnTCCAYUCBgGQxQDfrzANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDDAdhdmVuaXJzMB4XDTI0MDcxODA4Mzk0N1oXDTM0MDcxODA4NDEyN1owEjEQMA4GA1UEAwwHYXZlbmlyczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAL35MKB3inHbvyCssDCbIYt6Ug7WL1DXGkKs9hdSgSpBucVWU+iGu8BLRcCOuGEQrLrYAFtst9GThv//MBgLCbzS7aDUdBfvnIlU/0LX/gh7GlxUxsDrcwBgImfEywxyGf0mpbB8TcahJHP5rfe5LpxlowuRYMvPa5BzkV7DzqwxghDflCGC6CuAwebCuZna7KaV3M9ezVFt6ACJDJw6L5J+vGz6PeQC4l3UYYKjzRS3PkvhSmUljUdrCtt3Crx7+0vnajRoDaSlRT5UfelJ3tWUrjBsRNPCCB0O1Q9r0p8V+UnzlvIfAwj3BYzSEWsha9mLC+8p6dVhUu2aUVpEKEECAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAONODmppTeaJaJ50v1uethE0ZkGr5tamneifTtwBI18dWfAFj1svUPzNKJXHrK8cHtOsNVUJQo7GafCCO3IxRaw8plLUzzqqLunhOc3qozIajdV0gELQlUluVDb6wrjEDrvLovIwn2Yz5Fy2aA/TpSurJWzyfBYApkayqrBSumDoTVz9gvDcxbIvZiSs44yUF+6qAvyTvfwuA21qj39Ofk6eZLLDjlnyt5MNIiICmQB9flbif9NGKQvqYaeaq/9Gbb0OUEfK3vjPS0EdusUMI3CbQ6uU6LvI0Wd/FiFzwDBqgYOvTtN1sdKfB0Wr8NmFK9NntmUGs6sIQcsDoXRNc/w=='),
('22462191-2046-42b2-a5df-01938218efe3', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'oidc-usermodel-property-mapper'),
('22b295ea-8577-4dea-9dd6-bc1dbbcf67ab', '549e6f95-1530-4691-ad00-80412e20017f', 'allow-default-scopes', 'true'),
('23c05d73-8fea-4d26-ae62-76875e3e64f3', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'oidc-usermodel-property-mapper'),
('31d2e6d5-1ce3-4b5e-ac0e-feb08571f956', 'd06f064b-336a-43b0-9ecd-e3ef6a76ce17', 'keyUse', 'SIG'),
('34c35d10-356e-4d25-b05e-275bb96dc8e2', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'saml-user-attribute-mapper'),
('3653219d-cd61-4500-85cb-67e8d3d075ec', '097278af-2aa9-44b5-994c-e3c5c45766e4', 'secret', 'dxGTWouQ-6Jj62S8n-kkHg'),
('3904dbb3-c277-4dfa-a575-4e4ffb5513a0', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'saml-user-property-mapper'),
('39939a78-96a4-481e-8c04-beff9301006c', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'saml-user-property-mapper'),
('3b5d66a0-667d-49db-8051-4cf41409893c', '8380c3af-97be-4b44-9eeb-a4d49b65e6f1', 'priority', '100'),
('3ce23db5-aa5f-452f-8aeb-2db1dff0eeed', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'oidc-address-mapper'),
('46315ce3-8852-4300-8aac-0a5c961bb9d1', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'oidc-address-mapper'),
('4779db26-60f7-4262-966f-d462084c2b7b', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'saml-role-list-mapper'),
('5203a1d0-0b55-4c2e-9a17-2ad28ed26ee4', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'oidc-full-name-mapper'),
('52a64988-0f88-4e5d-890c-a3bf9c4285da', '68ddf526-c0be-4549-8c25-8d53fe233f4c', 'client-uris-must-match', 'true'),
('53cb2d7a-9091-4afb-afba-737fcb101fdf', '8fa31cba-ab87-43ab-8a68-3894967968f4', 'host-sending-registration-request-must-match', 'true'),
('55165179-8b73-42c2-8a73-4e1fce2bc709', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'saml-user-attribute-mapper'),
('5ebb80d6-d097-4d1b-8d4c-f8b876f9fb88', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'oidc-full-name-mapper'),
('60c6e03c-ad95-40e4-808b-7cf390e349f8', 'd06f064b-336a-43b0-9ecd-e3ef6a76ce17', 'privateKey', 'MIIEpAIBAAKCAQEAzIJDlYMBvYUN4mAxKqAqgZEiClXmVNvaYFCVJg7suTSbimnzOgk1FlC0DhrEiTFSREcIC3D6lzxJbzULFVDd0D5JRIufCU3Cz5BGTTZ4eOztP6qxwAmYxmb12sy1UUbtV5lLzZL6vzRdcUSwAl25lJApnIvAG6Zo105Bl/SdMWAwsIN+1/+JqZM0cGlmqTSJF+kcWeZWOFKTO5u1u6zXXgIHCyBMk5yISeBrxcM3tZhMgnAzHsB8A2vOsz10f9NMwSJ+4eIYhoiQTr5c1YFFWg2QOkK6CpZ6d/MiMgrcd+LC/35OYeOgEhyX6sUSNbHo1hFexm+WNr0v+dCSN1KMvQIDAQABAoIBAALq288gSzrP0QV49P1EQ374L08MRSjSPxYEfZreongM56gh6qHvZjC544ECVunpyHWSuYBnnZzhM7SmAiZicjD0O9mCEA7TAL5dyEb13zeYZ6pdbhjiJNS1vM2g1yMARZwBjZWH7ShOSq6g5Hg61o4nlVRh9lJeqBxkMHwxYDmx/KXriCjtHy6vs3Gmu9mTk/rk1vCTf1IUguXMz3YpdTH/4PIVfuJhxSSNrjeg0R+HD2jzzVDE+VhBpLQs6BH8n6jIG7AqN9Z8JDzCkZgY7S7cbksT21piMZHi9oe1CvB6iQf15fneyHWbyqj7iMLiIhyzQAOHFLDV8pDdrvajzDkCgYEA5QkT4JXLNZvWlVkLMtnhncVkDFU0IG7LHl0Bk/22+qGlPXM0mw02vuYTOEMSFjGK2CVEqoLUCdolJ9hK9zEUXWzyZ9WR7cXUXzuDzxDZYFKqgsnNNotJip1m6k8A7ZRGv1O8yLrbbIosJoC0anHicfcXrmOebBBVXZorpN3Mi2UCgYEA5JX56/GrY1N6o/ggYwzii7iFnDE1lM4GVUWjh6JH6Skj9ja1GP6V7UNbQ1puS5zf24ujh+erhOCxnNeKNd6V9WbTYxKi/0YPeRgejMOrQ3D1nmEyso0vJhgOpgH17Uk0O8KAnvnQmAHnSahxbhRja5lB0/PNYqvbA2p12pKoYnkCgYEAxLyqdWVWm+MUTU+infioo+SVzz41O5LQKXHM1DnBPsMxdHqMeo+8MfjiE5cRZz0BPi+tLyaYSyo2dmaF5pYjHylhhC3hXzRq+67cxzZ68xGvZA9ClpLJBie06poYevu+VcTxSh+wMijySdSOpUzULf8JOYRa5gqVuYSf7suTUz0CgYEArHOleGb72VSqirfwnymiZKzSSDMr7Fa+mmiOZg59WB+MnP1LBU9Uhwqw+JBp86uni3e+6RJT6UynUt+dyuRhmCF6Sx1NUW9mzwHuUl5wf9HhzBoiaGhxcLjnnECQa/M1b/xekxF/WJ7fAkgNQ3IoS431BtXBVra25c3UmhDY7mkCgYAz5YcexG3UcJApvwaslmnjKe7qHZz2plOnZLFw/umtEqDG1SoYUf65gHaPST8vqdG8FpFnVu7tks3m4vV7uG0CloWxl+spmRcCiMvkpTj75IkS1bE8ME5oxqJ4ddefHzBMzK+qKG8HD3gtvQK6iNBKq2JaOh/7Ex2j11+FodeLiA=='),
('62b6f36e-ce50-4499-9327-bfdefba00b25', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'oidc-full-name-mapper'),
('64755c8f-1068-444c-92e2-4cc6068ae056', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'oidc-sha256-pairwise-sub-mapper'),
('653f8f14-7801-4f47-91d1-c8d62a2e5af8', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'oidc-usermodel-property-mapper'),
('67d7792a-9bcf-47a9-af52-d369f4b03cfe', 'ad2fc437-0bcd-4f8b-8535-19ff86909d85', 'priority', '100'),
('68668973-5081-4cb8-b821-3392864163f6', '097278af-2aa9-44b5-994c-e3c5c45766e4', 'kid', 'b71147ac-8c32-443e-9502-fd4440557c57'),
('692e4cab-af07-444d-884d-b45b9593c02c', 'dd7b287f-8e0f-4e3d-9f73-a6a88c2d0e5d', 'privateKey', 'MIIEowIBAAKCAQEAvfkwoHeKcdu/IKywMJshi3pSDtYvUNcaQqz2F1KBKkG5xVZT6Ia7wEtFwI64YRCsutgAW2y30ZOG//8wGAsJvNLtoNR0F++ciVT/Qtf+CHsaXFTGwOtzAGAiZ8TLDHIZ/SalsHxNxqEkc/mt97kunGWjC5Fgy89rkHORXsPOrDGCEN+UIYLoK4DB5sK5mdrsppXcz17NUW3oAIkMnDovkn68bPo95ALiXdRhgqPNFLc+S+FKZSWNR2sK23cKvHv7S+dqNGgNpKVFPlR96Une1ZSuMGxE08IIHQ7VD2vSnxX5SfOW8h8DCPcFjNIRayFr2YsL7ynp1WFS7ZpRWkQoQQIDAQABAoIBAAIZrkqmSMIPsyEW6uBtGD9IJ9OhKoT0Hoa06/czC1mJWeOtNVw2vA+IB6YGlnREiikk7+Ej8zOnAN3b5BvuV09z9Wl59SwXxlJxcNPVqbJexg2JNXn/axHofi9JLr0dHsP4b/seogkZYuv0ZPTTjSKKQUKVttniiMBGmGueOshsfy6blLEzanHVXeovi7XCU6cIa0bsPaKmnPL5eYqUaaHYcGJMg8zBPKfe79YSM16AyJC2ud+mgwwa5BghNmtELVQqeGRHz5Hpf7d2yBvVFqRyjBMYskAt1uyQGKYIcKYi40jTLnHAH1CLh68GrxZipQCAd2mUKtfIGIH9T3dvqGkCgYEA+5SndvfGpC8hyWSTiYrtYsuObMfXeLcOQDqdb952eHO1/JwiiBMLqqvpdAQRJFmkzEfCwnLSuhmiNUjEhaXZFheELVNx7mOZGeDC+iTyaQRvP5lJ2jTYJgWqdke/BzKU848joFy/SW2485UOuL8lwnCehfcarHC0E3bknQuXzp0CgYEAwU99q3kNPbDyLg1tg5GDApLqc1X1S/kspgXQk/7BXhB/M8usQMMXS3dRMrG1Qghb1AbLtO6MVWSR8PKVowiNXlFG/9fnZQOL5O18X2HiuOMQHoyXpzYNozNVAj+SCBKhnT0CUkfqY8k6bsOE7zffYKVgHG4AyxosL5HciZA3XPUCgYEAn4f2YZRhXD+e9yxV+kQkoB9gY06eLig2WzhQGV5CVcNaRaNkqlEZdtKoqS7jB9/ohrmCoPda9TPIDm3kYLN4Y2/qGyvs9TQnI6fJPAItzbZILVYw5LPZ1NKG85YvZosGtSOxfcESc6emPp/ow/JCKXhsrZqySWL2bdlYwtVVIGkCgYAQTv2CLudAEiKMYhOgKvGLkQt4iYL1UuNCanErWy3DGs8wdnlkyVC4zCsshLOPC5d7rssWy51JQv/Q/RZEt979WwwLX5KlAgSWXrbEcUSoZnegrCnLMJM79oOwed5/EdtSoagFew26N2rXl2UqrWMKC0mtfFZSzDV4Ge0qKeGR1QKBgBoZ+tNRdM6ePIAI5g/iGLJTj/2QPLGXuAizXmpRNk/kxoyCuQNSa0Pooc+FMtwFmgS6yQGpmhEzXbIkMf3F4EwyCtEIGjr6Fbyx059QzaYjG66tEB5gATjnyV7efzAERvDg67gHQQWJL3tXyjRi6n6q9AqWxZlgExIBpjDufOfR'),
('6d0be90f-5b34-4b80-ae04-667aeda85c56', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'oidc-usermodel-attribute-mapper'),
('7018fa6e-9495-463c-850e-73093719e145', 'd06f064b-336a-43b0-9ecd-e3ef6a76ce17', 'certificate', 'MIICnTCCAYUCBgGQxQDfiTANBgkqhkiG9w0BAQsFADASMRAwDgYDVQQDDAdhdmVuaXJzMB4XDTI0MDcxODA4Mzk0N1oXDTM0MDcxODA4NDEyN1owEjEQMA4GA1UEAwwHYXZlbmlyczCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAMyCQ5WDAb2FDeJgMSqgKoGRIgpV5lTb2mBQlSYO7Lk0m4pp8zoJNRZQtA4axIkxUkRHCAtw+pc8SW81CxVQ3dA+SUSLnwlNws+QRk02eHjs7T+qscAJmMZm9drMtVFG7VeZS82S+r80XXFEsAJduZSQKZyLwBumaNdOQZf0nTFgMLCDftf/iamTNHBpZqk0iRfpHFnmVjhSkzubtbus114CBwsgTJOciEnga8XDN7WYTIJwMx7AfANrzrM9dH/TTMEifuHiGIaIkE6+XNWBRVoNkDpCugqWenfzIjIK3Hfiwv9+TmHjoBIcl+rFEjWx6NYRXsZvlja9L/nQkjdSjL0CAwEAATANBgkqhkiG9w0BAQsFAAOCAQEAc92Ci3LjE0+zVLkIFgHip1LumNRtRvEsMXNEDwjGKMtg9zvoQJ2fsZIworfWZ3XJyDxyAgF/0IKALVP2nTNwuASPOxxOUHsk1+rfWQzleOdTD/DHX+f5kChiYyoept+f1lNYkK27BPqHlORjngdsl+xxfGkAR73od8S4L9E4URPh0vuPRgvnr5aXUWZev03P76hvEV0Qvm0zWjDtWZciPxh7J2LpfCb4FIJFC2/eHxE/JWv1oHayDQ/xZve0OnblAK10IaY0YWMht8JYxnbrFrDqNhG7m2ramPf4v9ef0Z9ufiA2CIJ5aLghf/qW0G5DhjhVwxh1qLw+v7+jgRcXWQ=='),
('708d8f52-d904-44ea-81f4-bd55e0fb7156', 'd06f064b-336a-43b0-9ecd-e3ef6a76ce17', 'priority', '100'),
('7b203c8d-d5cf-4a10-9065-a9ab2a459711', 'dd7b287f-8e0f-4e3d-9f73-a6a88c2d0e5d', 'keyUse', 'ENC'),
('7fa8fb4f-8725-46fe-b97f-abc6af4c5ce3', '68ddf526-c0be-4549-8c25-8d53fe233f4c', 'host-sending-registration-request-must-match', 'true'),
('832d74dd-1515-4d33-8e79-29216a4ac788', '097278af-2aa9-44b5-994c-e3c5c45766e4', 'priority', '100'),
('836f5036-1b8f-4dfa-a4f3-14b812f32cde', '4bbd43f6-67fc-419c-9c37-831ce8867a01', 'allow-default-scopes', 'true'),
('83ea6e99-6ff9-40fb-b684-1b157c7f2a15', 'cf7ad600-c4e2-49b5-b9b5-9620ca804f45', 'allow-default-scopes', 'true'),
('840c209d-b929-431a-916d-ebcbd879f531', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'saml-role-list-mapper'),
('88fa758d-42d1-4eb8-967f-1fa0b55a8925', '281c3c67-1e8e-40b2-944e-5dafdf10fa24', 'kid', 'b05fbbcf-b69f-4f17-88db-dee47526666c'),
('89f78d7e-e294-4756-9cda-8e284d915a64', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'saml-role-list-mapper'),
('90153de2-be59-428e-96ac-c34ef4b1bfe7', '8380c3af-97be-4b44-9eeb-a4d49b65e6f1', 'secret', 'qVdPFnZhTDrusosvB44o_jgAlpFrEegJiYOmFkvaukLNrOj-cHwrUsYNFhPnuOvhWaNHvQdP3cn8agp_U85GF2r3XttX1o_QXF0e5by0WepPQ02mKQhU8qWgbvpmqUmxciK8oVnCW5nJVBlFKcrgaJGY2yaBB84MKl6ARtsCkaU'),
('9622289e-38c2-4ae1-9843-6f716708158d', 'dd7b287f-8e0f-4e3d-9f73-a6a88c2d0e5d', 'priority', '100'),
('973c8943-5b93-476c-8c82-e2a1c1d428cd', '0e0d8b16-9d93-4a23-a20d-8a3c767616cd', 'allow-default-scopes', 'true'),
('9944555d-d8bc-4294-938b-7cbcccc6f4d6', '8380c3af-97be-4b44-9eeb-a4d49b65e6f1', 'kid', '7ed40ae6-1102-4c6e-b22f-057591227474'),
('9ac9832d-b38f-4534-9d67-b8f1b012856f', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'oidc-usermodel-property-mapper'),
('9dfabbd7-1976-4d9d-bc4e-34768e52b812', '885e0475-71d5-406e-9bba-d422e4bef783', 'kc.user.profile.config', '{"attributes":[{"name":"username","displayName":"${username}","validations":{"length":{"min":3,"max":255},"username-prohibited-characters":{},"up-username-not-idn-homograph":{}},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"email","displayName":"${email}","validations":{"email":{},"length":{"max":255}},"required":{"roles":["user"]},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"firstName","displayName":"${firstName}","validations":{"length":{"max":255},"person-name-prohibited-characters":{}},"required":{"roles":["user"]},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"lastName","displayName":"${lastName}","validations":{"length":{"max":255},"person-name-prohibited-characters":{}},"required":{"roles":["user"]},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"profile","displayName":"profile","validations":{},"annotations":{},"required":{"roles":["admin","user"]},"permissions":{"view":["admin","user"],"edit":["admin"]},"multivalued":false}],"groups":[{"name":"user-metadata","displayHeader":"User metadata","displayDescription":"Attributes, which refer to user metadata"}]}'),
('a8db18c5-cdf9-4818-8c5d-156e9c7a8f38', '23ecf0eb-a828-461b-9bde-e791b23aee81', 'max-clients', '200'),
('ad212502-6717-448f-96d7-f8a16bb00897', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'saml-user-property-mapper'),
('ae296f9e-ddeb-4a9b-a5ee-3a9d1d627d3e', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'oidc-sha256-pairwise-sub-mapper'),
('b84cd663-4c75-4393-8fa8-19df617092e2', '8f48bfcf-edc8-4927-bcb5-7d2a4536cbbc', 'max-clients', '200'),
('bc0ad885-d89f-42ae-b57d-791a9f62af2b', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'oidc-usermodel-attribute-mapper'),
('c05ccde9-cfe3-492e-abd2-c7936ba448c9', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'oidc-usermodel-attribute-mapper'),
('c1d8449a-7e77-4027-bcbe-bba326522cd6', 'ad2fc437-0bcd-4f8b-8535-19ff86909d85', 'privateKey', 'MIIEoQIBAAKCAQEA9ru9gxyx6rtmR6eGyUFkmXrLkLA5dXTzE3BcuDXeIwQGBBtPxGnLm8rTtETHMAWMVbVxh0Nh6wmly8T7T0TgiGGb33bb67YIH2PFdbGO4d7HJYkEhJ78xKT3rKwMep8gc2QePF43kbLKOA13yyh7zCMV39pJ6Pk3EB858jXb/xElCYg+Yg0VtWGNPCvU9d8WWRPdj3Dqc1zjSO02qUZPewrZeOtgoKtP7Qi4PDccPGWbnC61ydD6rmVRART88efTw5/zCJkPiFTT+vIP6HS20i/pkeQ89xjfHOqlfuJDjl3NVo/vzvN7PronP6bi1cEp2MnuHponpNdFzjuD62Y0KwIDAQABAoH/My1Xbo3R8yse8leAhAryuUlyVkPQ2nKBLAcIzfmRmeTu9B5PuiEEZ2XEUIZZ/bWPTJ48bh7YsJsC4lrfxasDtHlXW4KYtMuNFvW0f2g7VGst44GhuxOljYqqO0BBVmERpt2dZ+gjBPv3KQ+iFjTPeNUBZ7n5AQ0Wq4409rubmWWmUiuUYK4cutowA9FjpFvXpUZ6zXtBD7SN0rs9K/2Z8pqPS7Up2tTv3MXjFB3Z4vpH4Al0vmLVDKpzeEAPfe4c0XLD+7jTOdhDJNMQH43BLD1QdRfIaenqm6eAB6IjMRfX/R0rJ2lS0JB7oIKpbdV938Z3O5xn/J3Dc4zV7nVNAoGBAP2Qe8FOfvQBRjTw7sYG85XC1zRPy4s22cS+PB/TyyyBB5O26OwfMf1JsmhhrOkJgXy4OBSjjX42VBlEtvMRIDqCEcXRQ2Dvd+J2wGdNxqhfvhWxH7OwY8jJuFstzoIzyVNnLOIkxIizPGGvTy0Iq3d8mvpAIrQmByOwmqKl+U6dAoGBAPkadZXqSbiOMPSnxuvusEcx3333COUObN5ItpMz1PArpTm1JlPnOMKD87kv2w0Yt+x/HgMcayKgF3uKtDC5xiF1z9JtZBrXM0w1MSTCId6XHQnjoC8W/rRaFknpQyji3ozTJYydQ04FCo8ghodUGH1KBcf1PO162Q0PGXhCj+9nAoGBAJUdu/mxfO1oNm555iGlAujmvYJxDsWfCzAto9QfzJMuilMvNQlvSwmmpnnGP1aIlZLQD4LrsBEySADYLTMCtk5SIbuQ1iwSiBExPvNBhPuN2o6NLJSSvgvdvUI5zQE7DUG0ImwXrVCq/25/F3PnblcFe2qB/yasWnR9rmLKcrPtAoGAI9+Zm3NYFonzpugaj5OaM0aNZviSgVood4KFOIG4YrYGX18lG+QqmmiiSsMHisVKh3Zu/gezrhLcDuCUvoeibuMmgKlcRzlCm/D1GNQUSJf+iJQNV3q6R4LJvu8mytaRMjqeJ7fmH3A+awpGFWsY88IbPwakNZcBdw37nwQG8nUCgYAwFuNIEl+q96Uq6Fvc5zdQg9glB9clMDf85yOXeKcnrBCEfrvvvJOcL/VDZxlsTOVjUbjgxF3y4W6hLnR8x9Lrq14hljU3d6tNrEBNZEjZBAqvOwbfBfggVpxBBV6XAOlxWordIOBC0xubzN1FnFtfSE5SbNBtk6gQ+ZmdE00uWw=='),
('c22f988a-10e5-4a9e-9cda-67e32c01cfeb', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'saml-user-attribute-mapper'),
('c521938c-bc3e-4c33-b0ed-f976a4a28950', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'oidc-usermodel-attribute-mapper'),
('c5f2cb54-4d10-426e-9a7d-57ff61ffa984', '32799757-7c40-4b89-984d-8f14744629d7', 'priority', '100'),
('c6613fb4-1afb-4c29-a4a9-bb294b02c68e', 'e0d3edbd-4e76-44b8-a739-e22b58750e41', 'allowed-protocol-mapper-types', 'oidc-sha256-pairwise-sub-mapper'),
('c675af97-75e3-4c5e-8f4c-92a2c31a7124', '32799757-7c40-4b89-984d-8f14744629d7', 'kid', 'b9957ad3-6fc1-4a00-b59f-6af55b9d6a9c'),
('ca795aa0-bd5e-4307-9e1b-62333d0f2de9', 'dd7b287f-8e0f-4e3d-9f73-a6a88c2d0e5d', 'algorithm', 'RSA-OAEP'),
('cb2cad03-830f-4d0f-af57-ffdb12e57997', '281c3c67-1e8e-40b2-944e-5dafdf10fa24', 'algorithm', 'HS512'),
('d07205d0-3a1f-4260-acb9-9a788619dbdb', '281c3c67-1e8e-40b2-944e-5dafdf10fa24', 'secret', 'rxo84f9rv9KLeuC8HqxbkQ8LNm_3so11CE2Q96w-lF70Fomrc3BDWNdzoDouO9w-7aFihnSNYdfvtZoqu8q_KYla3BbtASNUKWVRWnHJ5UOM4LqSNA7K2j4GAaTfYdPjgBDmz6FEZ9UXTcKxuPJsfZbl-0Wyx1a2im3pFuSX1t0'),
('d350a5a5-7129-4fe6-9226-8ca928ee0941', 'b319777c-3c9d-41d9-ab70-ef276ea02389', 'certificate', 'MIICmzCCAYMCBgGQxQCAsDANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDDAZtYXN0ZXIwHhcNMjQwNzE4MDgzOTIyWhcNMzQwNzE4MDg0MTAyWjARMQ8wDQYDVQQDDAZtYXN0ZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQDLKlIHxHVQ/nxGlLt++d6Y6OjgD+nPHOyfcrBLrBjAHBFMTU4Ncneld8Sew+vdB+cxcucZijDHbWGiqW2WP07ZYUr+MBU63HjWGlVjbLlxZg9hC07YEeY/FXA7fpb1GnfhmjnVTK1I2MBnJlILZgZmbbpi5kZR/brDsEXQx3ibv0X896plxE78P+Z/DTmyBtXtccoomASILx1x7/HY5Qa09wpHPvgGeNGxT/9nz4Kp8ytEdQ9Qs0PU4tJN03YcY/4S2m+bqFY1Wae0SHovruDPxSqI3eqEAjsnxKZ2hFFqLgbpJJsyxAyhecz0TUaHv0x9ybJ/c9TXaLNTeBojUEnrAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAMKp+Cw5ntqBzfxpE1Js6zxFi9c3Jb1txdcv+SWeOrhWC5yWZ6ymSjpMFs1R3fPcH1uj60rBwCDKrMtI/5EdFyf6Hl4iJCE7TB7as7YXoqOBDmG3p+kBAUWV7ki5vUhGhI+AldMEfAMpMrKxaa46RUPqRaiMjtsqn0qXXysf2lBStnv1ahfZ5U81L3R//S115g9oDqey/ctxaBDyuo4T/B2xzQlGg2mKRY9+CGmqE9lYF7sKXaqjUfTjLlJl7fyH2GyEpJ5E4Qf9u5s5JZ9zoKLyqh+atZUBKgnaexiM6SYkZTOOHLa1iUmxftD3+9Ut8OCSu4i9NaTLlid1uzvHbms='),
('d3a74c84-c689-4514-ae6a-f2ee44f00888', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'oidc-sha256-pairwise-sub-mapper'),
('da20c09a-6b07-403b-896b-02bbad8c0c5d', 'ad2fc437-0bcd-4f8b-8535-19ff86909d85', 'certificate', 'MIICmzCCAYMCBgGQxQCAQjANBgkqhkiG9w0BAQsFADARMQ8wDQYDVQQDDAZtYXN0ZXIwHhcNMjQwNzE4MDgzOTIyWhcNMzQwNzE4MDg0MTAyWjARMQ8wDQYDVQQDDAZtYXN0ZXIwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQD2u72DHLHqu2ZHp4bJQWSZesuQsDl1dPMTcFy4Nd4jBAYEG0/EacubytO0RMcwBYxVtXGHQ2HrCaXLxPtPROCIYZvfdtvrtggfY8V1sY7h3scliQSEnvzEpPesrAx6nyBzZB48XjeRsso4DXfLKHvMIxXf2kno+TcQHznyNdv/ESUJiD5iDRW1YY08K9T13xZZE92PcOpzXONI7TapRk97Ctl462Cgq0/tCLg8Nxw8ZZucLrXJ0PquZVEBFPzx59PDn/MImQ+IVNP68g/odLbSL+mR5Dz3GN8c6qV+4kOOXc1Wj+/O83s+uic/puLVwSnYye4emiek10XOO4PrZjQrAgMBAAEwDQYJKoZIhvcNAQELBQADggEBAKHBbuUBAaxTgyZzB/7/YvJpB8Tw+6C7k6eh48zpLhLkWKwkPbtU40hxZdeQ6sZlUfi4rSYkSHS5dYg+ME/+fLwFCllae0ysFqBgv2VbQpEmT7EAW3KvT8jbDZ1yfoiUgC0xcdxzB2ePYXzVrrNAOo0H0SdFflR+hzp9OYZtG/vhM+VVd+IuoJdlC54yjYuMWgJ5gjeWuJBUijTV9rIQ2cX90kSA8zvYOJCxeTXGSn+8Sk6QVWxw8gsDyWp/44zXULrCfv6uJZ5+emqMt+46/4Pzvq6bomI9vHzF/8w/WsnCo8/ATDcZ3m+uNr9v+fWtEUy2BKUlF0/+O6dWTn7EgkA='),
('e0abcc5c-c57e-419e-adc4-d4a02b184bce', '280dc286-1f74-48dc-a84e-67986be41f71', 'allowed-protocol-mapper-types', 'saml-user-property-mapper'),
('e7e71a7e-3d25-4498-918a-01e73828e096', 'b319777c-3c9d-41d9-ab70-ef276ea02389', 'algorithm', 'RSA-OAEP'),
('ec4bea14-e41e-4721-9535-a3a815dfc0ad', '79553a18-f224-4e18-ab10-1b972145d4ab', 'allowed-protocol-mapper-types', 'oidc-address-mapper'),
('f9a73cc6-d14c-465c-a003-ee41f5942638', 'b71586f1-db75-4b6c-bad2-2542018d6a9d', 'kc.user.profile.config', '{"attributes":[{"name":"username","displayName":"${username}","validations":{"length":{"min":3,"max":255},"username-prohibited-characters":{},"up-username-not-idn-homograph":{}},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"email","displayName":"${email}","validations":{"email":{},"length":{"max":255}},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"firstName","displayName":"${firstName}","validations":{"length":{"max":255},"person-name-prohibited-characters":{}},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false},{"name":"lastName","displayName":"${lastName}","validations":{"length":{"max":255},"person-name-prohibited-characters":{}},"permissions":{"view":["admin","user"],"edit":["admin","user"]},"multivalued":false}],"groups":[{"name":"user-metadata","displayHeader":"User metadata","displayDescription":"Attributes, which refer to user metadata"}]}'),
('fa9ff648-ed30-45cf-953c-fc7bfb48de6a', 'b319777c-3c9d-41d9-ab70-ef276ea02389', 'priority', '100'),
('fbb874e9-0b7b-4110-878d-bfcaaf7c57a5', '3604963e-3710-4061-926c-78c80d3a8072', 'allowed-protocol-mapper-types', 'oidc-address-mapper'),
('fbcd6231-90e2-4de3-b8d5-cb32cdde8cf8', '8380c3af-97be-4b44-9eeb-a4d49b65e6f1', 'algorithm', 'HS512');

INSERT INTO "public"."composite_role" ("composite", "child_role") VALUES
('251031da-6462-4f04-8bf6-d2a8ca146ad9', '7b8ee897-a4b2-4ca7-ba21-d6d70f571c10'),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '24df1b04-4f42-4ba4-9cd5-0fac5a474b0b'),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '251031da-6462-4f04-8bf6-d2a8ca146ad9'),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '4ff3ecbc-ed33-46e6-bcfc-75ff7cabafd8'),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '7cac211a-5046-48da-b0cb-ba12673a606b'),
('585d6263-5768-4aea-805c-3991a50562b6', 'c407072b-cf62-41d9-b727-33002583c30d'),
('5e577bdf-7d1a-45b9-a281-9b511ccf7f6c', '646e98b8-c472-4740-b4a1-3143bc80f64a'),
('5e577bdf-7d1a-45b9-a281-9b511ccf7f6c', '97a8daf9-bb0f-4762-8b47-20662bbcb5a8'),
('6226b6de-a765-4b3f-ae99-48468a3287d6', 'b9147ff5-ff49-43da-acc6-db13538d3707'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '1d5abdb1-d9ec-4c3a-b63b-62d6bc447348'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '2440c6dc-07b2-4b2f-bb5e-cb709f347a49'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '385e08a8-357b-4b06-adb6-58fab59f8d06'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '3f8fbb4f-b052-4804-a2a1-c30b1fab54d5'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '42488b72-58b0-46f5-b63d-9b45d6bb23d0'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '447d13a1-0273-4c89-ba22-ed2b5c32ec72'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '585d6263-5768-4aea-805c-3991a50562b6'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '5931fab6-4ed3-48b7-a97d-fd40a336350d'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '618261c1-13aa-41d8-a463-ffc3379f38ac'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '6f9e3ddc-1d60-4330-8bee-191f68d29dfb'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '732c2c4f-d34f-466e-8390-ffd034c7c7c0'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '74e0a0f1-3f93-45f0-9b39-9a1999919006'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '84705d7b-87a3-44a8-8e2e-d3e859a648f8'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', '98b45923-7952-4505-adb1-af6e7b122c15'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', 'c407072b-cf62-41d9-b727-33002583c30d'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', 'c70489d3-2936-485f-a7eb-32b3ce8145df'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', 'e3cd5ce0-7759-4000-8626-3813c8a33703'),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', 'fe7bd900-a0eb-4ebf-814e-f06f807151af'),
('732c2c4f-d34f-466e-8390-ffd034c7c7c0', '42488b72-58b0-46f5-b63d-9b45d6bb23d0'),
('732c2c4f-d34f-466e-8390-ffd034c7c7c0', '447d13a1-0273-4c89-ba22-ed2b5c32ec72'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '0b879c2f-ded7-45df-bf59-adcecef77cd0'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '1edb31c8-fc3c-4483-90de-1dc75d81c522'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '28055d79-f37d-45af-894f-a8a0430e968d'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '2c1261e2-f28f-473e-92e8-dea6a74b5a1d'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '3a52e281-3104-42c0-aeb3-b132feeeb452'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '40cb5431-5c12-48d9-b52c-7302ad80bdfc'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '46829747-e692-4f67-a157-d075f7a97d72'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '4741d45f-6128-41d3-abf0-534d847074b2'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '4c176de3-3d7c-40f3-92b5-a1ccf21610fa'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '4d316a8f-f9ca-4c26-a78a-4d0ff5a2548e'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '4f309be6-e6ca-4040-9753-eef7f37b4798'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '56d3f041-62d2-4190-86a6-bedb3791013b'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '5be6a567-f4c6-4d39-935b-ac692f759f8e'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '5c836613-2cb2-453a-bfa4-4c2bcb08fb75'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '5e577bdf-7d1a-45b9-a281-9b511ccf7f6c'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '646e98b8-c472-4740-b4a1-3143bc80f64a'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '72f6fc94-b66c-40d8-b861-16244b59232d'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '781df7d9-4c66-449e-9cf9-5178dab9d955'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '78c444f5-22ad-4563-afe2-a9bdc641ef85'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '814d5d22-d0c5-4f89-80a6-cbf18261b834'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '83aefa93-4004-40e1-be65-88f21e1e1523'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '8b8eac30-7aec-45ca-896e-4920dd355854'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '97a8daf9-bb0f-4762-8b47-20662bbcb5a8'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '9e36f61e-7e38-4110-bf09-3d99253617b0'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'a407e863-8cc3-4234-92dd-5733c7d13501'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'a6509e2f-e380-432f-b249-8cd8862f69fb'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'a8922e31-e6cf-4d72-b741-c0bace915424'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'a96fa59b-c15b-4074-9334-a0c3af830086'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'ac37c5f0-5006-4ed3-b457-fd0354c0114e'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'b765e80e-655c-404d-a65e-6b82295a6977'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'cd60378e-8e42-4ae8-bac6-d9424c59fd1e'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'cfb8a860-12a3-48df-a53a-3a9af7970193'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'd11656b6-80e3-4586-b997-b8ce1da0321a'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'e4465ad3-d92c-4895-86e2-6d9d59a6d39a'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'e545fc06-5f3b-4949-9282-124f94206594'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'e76578b0-9441-4020-abe6-ac4f524ee305'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'f0997a92-0e74-49bc-9e63-073b0e7eed6a'),
('83aefa93-4004-40e1-be65-88f21e1e1523', '1edb31c8-fc3c-4483-90de-1dc75d81c522'),
('a8922e31-e6cf-4d72-b741-c0bace915424', '9e36f61e-7e38-4110-bf09-3d99253617b0'),
('abea39fb-e472-45bc-aaf0-69d902107670', 'de4c2d8b-5490-48cd-a866-fbd96025d1c5'),
('ca61f9e8-b6d4-4f27-bf46-5803219408ca', '7ba90225-e3bd-49cb-b275-8821da5fd8b2'),
('d11656b6-80e3-4586-b997-b8ce1da0321a', 'a96fa59b-c15b-4074-9334-a0c3af830086'),
('d11656b6-80e3-4586-b997-b8ce1da0321a', 'f0997a92-0e74-49bc-9e63-073b0e7eed6a'),
('e497a841-53af-4eb6-863d-5623aa30a330', '3cc61388-e5d5-4b6c-b980-6bc82a3e38c5'),
('e497a841-53af-4eb6-863d-5623aa30a330', '3e56333e-32dd-4bf1-a99e-32613119b705'),
('e497a841-53af-4eb6-863d-5623aa30a330', '4fafdfb9-1842-419a-ad6a-66d17d68a475'),
('e497a841-53af-4eb6-863d-5623aa30a330', 'ca61f9e8-b6d4-4f27-bf46-5803219408ca');

INSERT INTO "public"."credential" ("id", "salt", "type", "user_id", "created_date", "user_label", "secret_data", "credential_data", "priority") VALUES
('4cb44bf3-0612-48f6-a0dc-db2275a74e35', NULL, 'password', '32a56da2-08b8-4f5d-9b24-02727ceebde0', 1721292220721, 'My password', '{"value":"FXmx9fTOTVI3UGrSKo+kc1NHi1AG6JoxOCc/S3Y+3STQEvW29rEaMuD2IBhBnhFXzIYuq0/j2gBKeNQR5JSNEA==","salt":"gCLoZcDRNaDN0RAJRR+NFA==","additionalParameters":{}}', '{"hashIterations":210000,"algorithm":"pbkdf2-sha512","additionalParameters":{}}', 10),
('5b34ed3c-9562-45dd-bbca-c58e936932f3', NULL, 'password', '32fa1821-1eb3-4df9-98a5-24f307bec772', 1721292063500, NULL, '{"value":"8/oGAlUjQn3KmTxdojgugmMYWDGpZ9vzcThL7qenVeacXo+yxeReJLyWEXuZ6kjJOdJAigPDxUc8L9fiV99zgw==","salt":"ByUEsXvnC0rY1AtDsSVEQQ==","additionalParameters":{}}', '{"hashIterations":210000,"algorithm":"pbkdf2-sha512","additionalParameters":{}}', 10),
('b0ed58c3-43dd-4b9a-b5c1-f97a29f9c5c7', NULL, 'password', '44b414b4-223e-4782-ab2b-1819161405e7', 1721292180374, 'My password', '{"value":"td5L/AGnQ5fmmPQcpzvdxCpPjr11Ebf0iLJKf0TWJQZ9wOymMUP+97jYCdxFC/ySF+gSazYe3iLG0odQ7AqXvw==","salt":"xaag5ytqtlbrpn1LoZI34Q==","additionalParameters":{}}', '{"hashIterations":210000,"algorithm":"pbkdf2-sha512","additionalParameters":{}}', 10);

INSERT INTO "public"."databasechangelog" ("id", "author", "filename", "dateexecuted", "orderexecuted", "exectype", "md5sum", "description", "comments", "tag", "liquibase", "contexts", "labels", "deployment_id") VALUES
('1.0.0.Final-KEYCLOAK-5461', 'sthorger@redhat.com', 'META-INF/jpa-changelog-1.0.0.Final.xml', '2024-07-18 08:41:00.999671', 1, 'EXECUTED', '9:6f1016664e21e16d26517a4418f5e3df', 'createTable tableName=APPLICATION_DEFAULT_ROLES; createTable tableName=CLIENT; createTable tableName=CLIENT_SESSION; createTable tableName=CLIENT_SESSION_ROLE; createTable tableName=COMPOSITE_ROLE; createTable tableName=CREDENTIAL; createTable tab...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.0.0.Final-KEYCLOAK-5461', 'sthorger@redhat.com', 'META-INF/db2-jpa-changelog-1.0.0.Final.xml', '2024-07-18 08:41:01.010203', 2, 'MARK_RAN', '9:828775b1596a07d1200ba1d49e5e3941', 'createTable tableName=APPLICATION_DEFAULT_ROLES; createTable tableName=CLIENT; createTable tableName=CLIENT_SESSION; createTable tableName=CLIENT_SESSION_ROLE; createTable tableName=COMPOSITE_ROLE; createTable tableName=CREDENTIAL; createTable tab...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.1.0.Beta1', 'sthorger@redhat.com', 'META-INF/jpa-changelog-1.1.0.Beta1.xml', '2024-07-18 08:41:01.044168', 3, 'EXECUTED', '9:5f090e44a7d595883c1fb61f4b41fd38', 'delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION; createTable tableName=CLIENT_ATTRIBUTES; createTable tableName=CLIENT_SESSION_NOTE; createTable tableName=APP_NODE_REGISTRATIONS; addColumn table...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.1.0.Final', 'sthorger@redhat.com', 'META-INF/jpa-changelog-1.1.0.Final.xml', '2024-07-18 08:41:01.046899', 4, 'EXECUTED', '9:c07e577387a3d2c04d1adc9aaad8730e', 'renameColumn newColumnName=EVENT_TIME, oldColumnName=TIME, tableName=EVENT_ENTITY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.2.0.Beta1', 'psilva@redhat.com', 'META-INF/jpa-changelog-1.2.0.Beta1.xml', '2024-07-18 08:41:01.110969', 5, 'EXECUTED', '9:b68ce996c655922dbcd2fe6b6ae72686', 'delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION; createTable tableName=PROTOCOL_MAPPER; createTable tableName=PROTOCOL_MAPPER_CONFIG; createTable tableName=...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.2.0.Beta1', 'psilva@redhat.com', 'META-INF/db2-jpa-changelog-1.2.0.Beta1.xml', '2024-07-18 08:41:01.113838', 6, 'MARK_RAN', '9:543b5c9989f024fe35c6f6c5a97de88e', 'delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION; createTable tableName=PROTOCOL_MAPPER; createTable tableName=PROTOCOL_MAPPER_CONFIG; createTable tableName=...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.2.0.RC1', 'bburke@redhat.com', 'META-INF/jpa-changelog-1.2.0.CR1.xml', '2024-07-18 08:41:01.170578', 7, 'EXECUTED', '9:765afebbe21cf5bbca048e632df38336', 'delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete tableName=USER_SESSION; createTable tableName=MIGRATION_MODEL; createTable tableName=IDENTITY_P...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.2.0.RC1', 'bburke@redhat.com', 'META-INF/db2-jpa-changelog-1.2.0.CR1.xml', '2024-07-18 08:41:01.174', 8, 'MARK_RAN', '9:db4a145ba11a6fdaefb397f6dbf829a1', 'delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete tableName=USER_SESSION; createTable tableName=MIGRATION_MODEL; createTable tableName=IDENTITY_P...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.2.0.Final', 'keycloak', 'META-INF/jpa-changelog-1.2.0.Final.xml', '2024-07-18 08:41:01.177208', 9, 'EXECUTED', '9:9d05c7be10cdb873f8bcb41bc3a8ab23', 'update tableName=CLIENT; update tableName=CLIENT; update tableName=CLIENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.3.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-1.3.0.xml', '2024-07-18 08:41:01.230949', 10, 'EXECUTED', '9:18593702353128d53111f9b1ff0b82b8', 'delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete tableName=USER_SESSION; createTable tableName=ADMI...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.4.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-1.4.0.xml', '2024-07-18 08:41:01.26689', 11, 'EXECUTED', '9:6122efe5f090e41a85c0f1c9e52cbb62', 'delete tableName=CLIENT_SESSION_AUTH_STATUS; delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete table...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.4.0', 'bburke@redhat.com', 'META-INF/db2-jpa-changelog-1.4.0.xml', '2024-07-18 08:41:01.268909', 12, 'MARK_RAN', '9:e1ff28bf7568451453f844c5d54bb0b5', 'delete tableName=CLIENT_SESSION_AUTH_STATUS; delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete table...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.5.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-1.5.0.xml', '2024-07-18 08:41:01.278877', 13, 'EXECUTED', '9:7af32cd8957fbc069f796b61217483fd', 'delete tableName=CLIENT_SESSION_AUTH_STATUS; delete tableName=CLIENT_SESSION_ROLE; delete tableName=CLIENT_SESSION_PROT_MAPPER; delete tableName=CLIENT_SESSION_NOTE; delete tableName=CLIENT_SESSION; delete tableName=USER_SESSION_NOTE; delete table...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.6.1_from15', 'mposolda@redhat.com', 'META-INF/jpa-changelog-1.6.1.xml', '2024-07-18 08:41:01.291441', 14, 'EXECUTED', '9:6005e15e84714cd83226bf7879f54190', 'addColumn tableName=REALM; addColumn tableName=KEYCLOAK_ROLE; addColumn tableName=CLIENT; createTable tableName=OFFLINE_USER_SESSION; createTable tableName=OFFLINE_CLIENT_SESSION; addPrimaryKey constraintName=CONSTRAINT_OFFL_US_SES_PK2, tableName=...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.6.1_from16-pre', 'mposolda@redhat.com', 'META-INF/jpa-changelog-1.6.1.xml', '2024-07-18 08:41:01.292466', 15, 'MARK_RAN', '9:bf656f5a2b055d07f314431cae76f06c', 'delete tableName=OFFLINE_CLIENT_SESSION; delete tableName=OFFLINE_USER_SESSION', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.6.1_from16', 'mposolda@redhat.com', 'META-INF/jpa-changelog-1.6.1.xml', '2024-07-18 08:41:01.294635', 16, 'MARK_RAN', '9:f8dadc9284440469dcf71e25ca6ab99b', 'dropPrimaryKey constraintName=CONSTRAINT_OFFLINE_US_SES_PK, tableName=OFFLINE_USER_SESSION; dropPrimaryKey constraintName=CONSTRAINT_OFFLINE_CL_SES_PK, tableName=OFFLINE_CLIENT_SESSION; addColumn tableName=OFFLINE_USER_SESSION; update tableName=OF...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.6.1', 'mposolda@redhat.com', 'META-INF/jpa-changelog-1.6.1.xml', '2024-07-18 08:41:01.295883', 17, 'EXECUTED', '9:d41d8cd98f00b204e9800998ecf8427e', 'empty', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.7.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-1.7.0.xml', '2024-07-18 08:41:01.323817', 18, 'EXECUTED', '9:3368ff0be4c2855ee2dd9ca813b38d8e', 'createTable tableName=KEYCLOAK_GROUP; createTable tableName=GROUP_ROLE_MAPPING; createTable tableName=GROUP_ATTRIBUTE; createTable tableName=USER_GROUP_MEMBERSHIP; createTable tableName=REALM_DEFAULT_GROUPS; addColumn tableName=IDENTITY_PROVIDER; ...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.8.0', 'mposolda@redhat.com', 'META-INF/jpa-changelog-1.8.0.xml', '2024-07-18 08:41:01.353738', 19, 'EXECUTED', '9:8ac2fb5dd030b24c0570a763ed75ed20', 'addColumn tableName=IDENTITY_PROVIDER; createTable tableName=CLIENT_TEMPLATE; createTable tableName=CLIENT_TEMPLATE_ATTRIBUTES; createTable tableName=TEMPLATE_SCOPE_MAPPING; dropNotNullConstraint columnName=CLIENT_ID, tableName=PROTOCOL_MAPPER; ad...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.8.0-2', 'keycloak', 'META-INF/jpa-changelog-1.8.0.xml', '2024-07-18 08:41:01.357606', 20, 'EXECUTED', '9:f91ddca9b19743db60e3057679810e6c', 'dropDefaultValue columnName=ALGORITHM, tableName=CREDENTIAL; update tableName=CREDENTIAL', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('24.0.0-9758-2', 'keycloak', 'META-INF/jpa-changelog-24.0.0.xml', '2024-07-18 08:41:01.936456', 119, 'EXECUTED', '9:bf0fdee10afdf597a987adbf291db7b2', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.8.0', 'mposolda@redhat.com', 'META-INF/db2-jpa-changelog-1.8.0.xml', '2024-07-18 08:41:01.359229', 21, 'MARK_RAN', '9:831e82914316dc8a57dc09d755f23c51', 'addColumn tableName=IDENTITY_PROVIDER; createTable tableName=CLIENT_TEMPLATE; createTable tableName=CLIENT_TEMPLATE_ATTRIBUTES; createTable tableName=TEMPLATE_SCOPE_MAPPING; dropNotNullConstraint columnName=CLIENT_ID, tableName=PROTOCOL_MAPPER; ad...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.8.0-2', 'keycloak', 'META-INF/db2-jpa-changelog-1.8.0.xml', '2024-07-18 08:41:01.361362', 22, 'MARK_RAN', '9:f91ddca9b19743db60e3057679810e6c', 'dropDefaultValue columnName=ALGORITHM, tableName=CREDENTIAL; update tableName=CREDENTIAL', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.9.0', 'mposolda@redhat.com', 'META-INF/jpa-changelog-1.9.0.xml', '2024-07-18 08:41:01.374989', 23, 'EXECUTED', '9:bc3d0f9e823a69dc21e23e94c7a94bb1', 'update tableName=REALM; update tableName=REALM; update tableName=REALM; update tableName=REALM; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=REALM; update tableName=REALM; customChange; dr...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.9.1', 'keycloak', 'META-INF/jpa-changelog-1.9.1.xml', '2024-07-18 08:41:01.377689', 24, 'EXECUTED', '9:c9999da42f543575ab790e76439a2679', 'modifyDataType columnName=PRIVATE_KEY, tableName=REALM; modifyDataType columnName=PUBLIC_KEY, tableName=REALM; modifyDataType columnName=CERTIFICATE, tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.9.1', 'keycloak', 'META-INF/db2-jpa-changelog-1.9.1.xml', '2024-07-18 08:41:01.378547', 25, 'MARK_RAN', '9:0d6c65c6f58732d81569e77b10ba301d', 'modifyDataType columnName=PRIVATE_KEY, tableName=REALM; modifyDataType columnName=CERTIFICATE, tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('1.9.2', 'keycloak', 'META-INF/jpa-changelog-1.9.2.xml', '2024-07-18 08:41:01.394089', 26, 'EXECUTED', '9:fc576660fc016ae53d2d4778d84d86d0', 'createIndex indexName=IDX_USER_EMAIL, tableName=USER_ENTITY; createIndex indexName=IDX_USER_ROLE_MAPPING, tableName=USER_ROLE_MAPPING; createIndex indexName=IDX_USER_GROUP_MAPPING, tableName=USER_GROUP_MEMBERSHIP; createIndex indexName=IDX_USER_CO...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-2.0.0', 'psilva@redhat.com', 'META-INF/jpa-changelog-authz-2.0.0.xml', '2024-07-18 08:41:01.434258', 27, 'EXECUTED', '9:43ed6b0da89ff77206289e87eaa9c024', 'createTable tableName=RESOURCE_SERVER; addPrimaryKey constraintName=CONSTRAINT_FARS, tableName=RESOURCE_SERVER; addUniqueConstraint constraintName=UK_AU8TT6T700S9V50BU18WS5HA6, tableName=RESOURCE_SERVER; createTable tableName=RESOURCE_SERVER_RESOU...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-2.5.1', 'psilva@redhat.com', 'META-INF/jpa-changelog-authz-2.5.1.xml', '2024-07-18 08:41:01.436876', 28, 'EXECUTED', '9:44bae577f551b3738740281eceb4ea70', 'update tableName=RESOURCE_SERVER_POLICY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.1.0-KEYCLOAK-5461', 'bburke@redhat.com', 'META-INF/jpa-changelog-2.1.0.xml', '2024-07-18 08:41:01.472799', 29, 'EXECUTED', '9:bd88e1f833df0420b01e114533aee5e8', 'createTable tableName=BROKER_LINK; createTable tableName=FED_USER_ATTRIBUTE; createTable tableName=FED_USER_CONSENT; createTable tableName=FED_USER_CONSENT_ROLE; createTable tableName=FED_USER_CONSENT_PROT_MAPPER; createTable tableName=FED_USER_CR...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.2.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-2.2.0.xml', '2024-07-18 08:41:01.481414', 30, 'EXECUTED', '9:a7022af5267f019d020edfe316ef4371', 'addColumn tableName=ADMIN_EVENT_ENTITY; createTable tableName=CREDENTIAL_ATTRIBUTE; createTable tableName=FED_CREDENTIAL_ATTRIBUTE; modifyDataType columnName=VALUE, tableName=CREDENTIAL; addForeignKeyConstraint baseTableName=FED_CREDENTIAL_ATTRIBU...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.3.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-2.3.0.xml', '2024-07-18 08:41:01.493101', 31, 'EXECUTED', '9:fc155c394040654d6a79227e56f5e25a', 'createTable tableName=FEDERATED_USER; addPrimaryKey constraintName=CONSTR_FEDERATED_USER, tableName=FEDERATED_USER; dropDefaultValue columnName=TOTP, tableName=USER_ENTITY; dropColumn columnName=TOTP, tableName=USER_ENTITY; addColumn tableName=IDE...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.4.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-2.4.0.xml', '2024-07-18 08:41:01.496169', 32, 'EXECUTED', '9:eac4ffb2a14795e5dc7b426063e54d88', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.5.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-2.5.0.xml', '2024-07-18 08:41:01.49993', 33, 'EXECUTED', '9:54937c05672568c4c64fc9524c1e9462', 'customChange; modifyDataType columnName=USER_ID, tableName=OFFLINE_USER_SESSION', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.5.0-unicode-oracle', 'hmlnarik@redhat.com', 'META-INF/jpa-changelog-2.5.0.xml', '2024-07-18 08:41:01.501378', 34, 'MARK_RAN', '9:3a32bace77c84d7678d035a7f5a8084e', 'modifyDataType columnName=DESCRIPTION, tableName=AUTHENTICATION_FLOW; modifyDataType columnName=DESCRIPTION, tableName=CLIENT_TEMPLATE; modifyDataType columnName=DESCRIPTION, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=DESCRIPTION,...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.5.0-unicode-other-dbs', 'hmlnarik@redhat.com', 'META-INF/jpa-changelog-2.5.0.xml', '2024-07-18 08:41:01.51525', 35, 'EXECUTED', '9:33d72168746f81f98ae3a1e8e0ca3554', 'modifyDataType columnName=DESCRIPTION, tableName=AUTHENTICATION_FLOW; modifyDataType columnName=DESCRIPTION, tableName=CLIENT_TEMPLATE; modifyDataType columnName=DESCRIPTION, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=DESCRIPTION,...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.5.0-duplicate-email-support', 'slawomir@dabek.name', 'META-INF/jpa-changelog-2.5.0.xml', '2024-07-18 08:41:01.517613', 36, 'EXECUTED', '9:61b6d3d7a4c0e0024b0c839da283da0c', 'addColumn tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.5.0-unique-group-names', 'hmlnarik@redhat.com', 'META-INF/jpa-changelog-2.5.0.xml', '2024-07-18 08:41:01.52043', 37, 'EXECUTED', '9:8dcac7bdf7378e7d823cdfddebf72fda', 'addUniqueConstraint constraintName=SIBLING_NAMES, tableName=KEYCLOAK_GROUP', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('2.5.1', 'bburke@redhat.com', 'META-INF/jpa-changelog-2.5.1.xml', '2024-07-18 08:41:01.521817', 38, 'EXECUTED', '9:a2b870802540cb3faa72098db5388af3', 'addColumn tableName=FED_USER_CONSENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.0.0', 'bburke@redhat.com', 'META-INF/jpa-changelog-3.0.0.xml', '2024-07-18 08:41:01.523277', 39, 'EXECUTED', '9:132a67499ba24bcc54fb5cbdcfe7e4c0', 'addColumn tableName=IDENTITY_PROVIDER', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.2.0-fix', 'keycloak', 'META-INF/jpa-changelog-3.2.0.xml', '2024-07-18 08:41:01.523864', 40, 'MARK_RAN', '9:938f894c032f5430f2b0fafb1a243462', 'addNotNullConstraint columnName=REALM_ID, tableName=CLIENT_INITIAL_ACCESS', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.2.0-fix-with-keycloak-5416', 'keycloak', 'META-INF/jpa-changelog-3.2.0.xml', '2024-07-18 08:41:01.524709', 41, 'MARK_RAN', '9:845c332ff1874dc5d35974b0babf3006', 'dropIndex indexName=IDX_CLIENT_INIT_ACC_REALM, tableName=CLIENT_INITIAL_ACCESS; addNotNullConstraint columnName=REALM_ID, tableName=CLIENT_INITIAL_ACCESS; createIndex indexName=IDX_CLIENT_INIT_ACC_REALM, tableName=CLIENT_INITIAL_ACCESS', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.2.0-fix-offline-sessions', 'hmlnarik', 'META-INF/jpa-changelog-3.2.0.xml', '2024-07-18 08:41:01.527683', 42, 'EXECUTED', '9:fc86359c079781adc577c5a217e4d04c', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.2.0-fixed', 'keycloak', 'META-INF/jpa-changelog-3.2.0.xml', '2024-07-18 08:41:01.587141', 43, 'EXECUTED', '9:59a64800e3c0d09b825f8a3b444fa8f4', 'addColumn tableName=REALM; dropPrimaryKey constraintName=CONSTRAINT_OFFL_CL_SES_PK2, tableName=OFFLINE_CLIENT_SESSION; dropColumn columnName=CLIENT_SESSION_ID, tableName=OFFLINE_CLIENT_SESSION; addPrimaryKey constraintName=CONSTRAINT_OFFL_CL_SES_P...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.3.0', 'keycloak', 'META-INF/jpa-changelog-3.3.0.xml', '2024-07-18 08:41:01.589493', 44, 'EXECUTED', '9:d48d6da5c6ccf667807f633fe489ce88', 'addColumn tableName=USER_ENTITY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-3.4.0.CR1-resource-server-pk-change-part1', 'glavoie@gmail.com', 'META-INF/jpa-changelog-authz-3.4.0.CR1.xml', '2024-07-18 08:41:01.591901', 45, 'EXECUTED', '9:dde36f7973e80d71fceee683bc5d2951', 'addColumn tableName=RESOURCE_SERVER_POLICY; addColumn tableName=RESOURCE_SERVER_RESOURCE; addColumn tableName=RESOURCE_SERVER_SCOPE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-3.4.0.CR1-resource-server-pk-change-part2-KEYCLOAK-6095', 'hmlnarik@redhat.com', 'META-INF/jpa-changelog-authz-3.4.0.CR1.xml', '2024-07-18 08:41:01.596616', 46, 'EXECUTED', '9:b855e9b0a406b34fa323235a0cf4f640', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-3.4.0.CR1-resource-server-pk-change-part3-fixed', 'glavoie@gmail.com', 'META-INF/jpa-changelog-authz-3.4.0.CR1.xml', '2024-07-18 08:41:01.597405', 47, 'MARK_RAN', '9:51abbacd7b416c50c4421a8cabf7927e', 'dropIndex indexName=IDX_RES_SERV_POL_RES_SERV, tableName=RESOURCE_SERVER_POLICY; dropIndex indexName=IDX_RES_SRV_RES_RES_SRV, tableName=RESOURCE_SERVER_RESOURCE; dropIndex indexName=IDX_RES_SRV_SCOPE_RES_SRV, tableName=RESOURCE_SERVER_SCOPE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-3.4.0.CR1-resource-server-pk-change-part3-fixed-nodropindex', 'glavoie@gmail.com', 'META-INF/jpa-changelog-authz-3.4.0.CR1.xml', '2024-07-18 08:41:01.61726', 48, 'EXECUTED', '9:bdc99e567b3398bac83263d375aad143', 'addNotNullConstraint columnName=RESOURCE_SERVER_CLIENT_ID, tableName=RESOURCE_SERVER_POLICY; addNotNullConstraint columnName=RESOURCE_SERVER_CLIENT_ID, tableName=RESOURCE_SERVER_RESOURCE; addNotNullConstraint columnName=RESOURCE_SERVER_CLIENT_ID, ...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authn-3.4.0.CR1-refresh-token-max-reuse', 'glavoie@gmail.com', 'META-INF/jpa-changelog-authz-3.4.0.CR1.xml', '2024-07-18 08:41:01.620656', 49, 'EXECUTED', '9:d198654156881c46bfba39abd7769e69', 'addColumn tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.4.0', 'keycloak', 'META-INF/jpa-changelog-3.4.0.xml', '2024-07-18 08:41:01.639737', 50, 'EXECUTED', '9:cfdd8736332ccdd72c5256ccb42335db', 'addPrimaryKey constraintName=CONSTRAINT_REALM_DEFAULT_ROLES, tableName=REALM_DEFAULT_ROLES; addPrimaryKey constraintName=CONSTRAINT_COMPOSITE_ROLE, tableName=COMPOSITE_ROLE; addPrimaryKey constraintName=CONSTR_REALM_DEFAULT_GROUPS, tableName=REALM...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.4.0-KEYCLOAK-5230', 'hmlnarik@redhat.com', 'META-INF/jpa-changelog-3.4.0.xml', '2024-07-18 08:41:01.653687', 51, 'EXECUTED', '9:7c84de3d9bd84d7f077607c1a4dcb714', 'createIndex indexName=IDX_FU_ATTRIBUTE, tableName=FED_USER_ATTRIBUTE; createIndex indexName=IDX_FU_CONSENT, tableName=FED_USER_CONSENT; createIndex indexName=IDX_FU_CONSENT_RU, tableName=FED_USER_CONSENT; createIndex indexName=IDX_FU_CREDENTIAL, t...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.4.1', 'psilva@redhat.com', 'META-INF/jpa-changelog-3.4.1.xml', '2024-07-18 08:41:01.655005', 52, 'EXECUTED', '9:5a6bb36cbefb6a9d6928452c0852af2d', 'modifyDataType columnName=VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.4.2', 'keycloak', 'META-INF/jpa-changelog-3.4.2.xml', '2024-07-18 08:41:01.65614', 53, 'EXECUTED', '9:8f23e334dbc59f82e0a328373ca6ced0', 'update tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('3.4.2-KEYCLOAK-5172', 'mkanis@redhat.com', 'META-INF/jpa-changelog-3.4.2.xml', '2024-07-18 08:41:01.65723', 54, 'EXECUTED', '9:9156214268f09d970cdf0e1564d866af', 'update tableName=CLIENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.0.0-KEYCLOAK-6335', 'bburke@redhat.com', 'META-INF/jpa-changelog-4.0.0.xml', '2024-07-18 08:41:01.660142', 55, 'EXECUTED', '9:db806613b1ed154826c02610b7dbdf74', 'createTable tableName=CLIENT_AUTH_FLOW_BINDINGS; addPrimaryKey constraintName=C_CLI_FLOW_BIND, tableName=CLIENT_AUTH_FLOW_BINDINGS', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.0.0-CLEANUP-UNUSED-TABLE', 'bburke@redhat.com', 'META-INF/jpa-changelog-4.0.0.xml', '2024-07-18 08:41:01.663635', 56, 'EXECUTED', '9:229a041fb72d5beac76bb94a5fa709de', 'dropTable tableName=CLIENT_IDENTITY_PROV_MAPPING', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.0.0-KEYCLOAK-6228', 'bburke@redhat.com', 'META-INF/jpa-changelog-4.0.0.xml', '2024-07-18 08:41:01.675038', 57, 'EXECUTED', '9:079899dade9c1e683f26b2aa9ca6ff04', 'dropUniqueConstraint constraintName=UK_JKUWUVD56ONTGSUHOGM8UEWRT, tableName=USER_CONSENT; dropNotNullConstraint columnName=CLIENT_ID, tableName=USER_CONSENT; addColumn tableName=USER_CONSENT; addUniqueConstraint constraintName=UK_JKUWUVD56ONTGSUHO...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.0.0-KEYCLOAK-5579-fixed', 'mposolda@redhat.com', 'META-INF/jpa-changelog-4.0.0.xml', '2024-07-18 08:41:01.7221', 58, 'EXECUTED', '9:139b79bcbbfe903bb1c2d2a4dbf001d9', 'dropForeignKeyConstraint baseTableName=CLIENT_TEMPLATE_ATTRIBUTES, constraintName=FK_CL_TEMPL_ATTR_TEMPL; renameTable newTableName=CLIENT_SCOPE_ATTRIBUTES, oldTableName=CLIENT_TEMPLATE_ATTRIBUTES; renameColumn newColumnName=SCOPE_ID, oldColumnName...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-4.0.0.CR1', 'psilva@redhat.com', 'META-INF/jpa-changelog-authz-4.0.0.CR1.xml', '2024-07-18 08:41:01.735709', 59, 'EXECUTED', '9:b55738ad889860c625ba2bf483495a04', 'createTable tableName=RESOURCE_SERVER_PERM_TICKET; addPrimaryKey constraintName=CONSTRAINT_FAPMT, tableName=RESOURCE_SERVER_PERM_TICKET; addForeignKeyConstraint baseTableName=RESOURCE_SERVER_PERM_TICKET, constraintName=FK_FRSRHO213XCX4WNKOG82SSPMT...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-4.0.0.Beta3', 'psilva@redhat.com', 'META-INF/jpa-changelog-authz-4.0.0.Beta3.xml', '2024-07-18 08:41:01.738753', 60, 'EXECUTED', '9:e0057eac39aa8fc8e09ac6cfa4ae15fe', 'addColumn tableName=RESOURCE_SERVER_POLICY; addColumn tableName=RESOURCE_SERVER_PERM_TICKET; addForeignKeyConstraint baseTableName=RESOURCE_SERVER_PERM_TICKET, constraintName=FK_FRSRPO2128CX4WNKOG82SSRFY, referencedTableName=RESOURCE_SERVER_POLICY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-4.2.0.Final', 'mhajas@redhat.com', 'META-INF/jpa-changelog-authz-4.2.0.Final.xml', '2024-07-18 08:41:01.744947', 61, 'EXECUTED', '9:42a33806f3a0443fe0e7feeec821326c', 'createTable tableName=RESOURCE_URIS; addForeignKeyConstraint baseTableName=RESOURCE_URIS, constraintName=FK_RESOURCE_SERVER_URIS, referencedTableName=RESOURCE_SERVER_RESOURCE; customChange; dropColumn columnName=URI, tableName=RESOURCE_SERVER_RESO...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-4.2.0.Final-KEYCLOAK-9944', 'hmlnarik@redhat.com', 'META-INF/jpa-changelog-authz-4.2.0.Final.xml', '2024-07-18 08:41:01.747712', 62, 'EXECUTED', '9:9968206fca46eecc1f51db9c024bfe56', 'addPrimaryKey constraintName=CONSTRAINT_RESOUR_URIS_PK, tableName=RESOURCE_URIS', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.2.0-KEYCLOAK-6313', 'wadahiro@gmail.com', 'META-INF/jpa-changelog-4.2.0.xml', '2024-07-18 08:41:01.749395', 63, 'EXECUTED', '9:92143a6daea0a3f3b8f598c97ce55c3d', 'addColumn tableName=REQUIRED_ACTION_PROVIDER', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.3.0-KEYCLOAK-7984', 'wadahiro@gmail.com', 'META-INF/jpa-changelog-4.3.0.xml', '2024-07-18 08:41:01.7513', 64, 'EXECUTED', '9:82bab26a27195d889fb0429003b18f40', 'update tableName=REQUIRED_ACTION_PROVIDER', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.6.0-KEYCLOAK-7950', 'psilva@redhat.com', 'META-INF/jpa-changelog-4.6.0.xml', '2024-07-18 08:41:01.752815', 65, 'EXECUTED', '9:e590c88ddc0b38b0ae4249bbfcb5abc3', 'update tableName=RESOURCE_SERVER_RESOURCE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.6.0-KEYCLOAK-8377', 'keycloak', 'META-INF/jpa-changelog-4.6.0.xml', '2024-07-18 08:41:01.759168', 66, 'EXECUTED', '9:5c1f475536118dbdc38d5d7977950cc0', 'createTable tableName=ROLE_ATTRIBUTE; addPrimaryKey constraintName=CONSTRAINT_ROLE_ATTRIBUTE_PK, tableName=ROLE_ATTRIBUTE; addForeignKeyConstraint baseTableName=ROLE_ATTRIBUTE, constraintName=FK_ROLE_ATTRIBUTE_ID, referencedTableName=KEYCLOAK_ROLE...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.6.0-KEYCLOAK-8555', 'gideonray@gmail.com', 'META-INF/jpa-changelog-4.6.0.xml', '2024-07-18 08:41:01.761608', 67, 'EXECUTED', '9:e7c9f5f9c4d67ccbbcc215440c718a17', 'createIndex indexName=IDX_COMPONENT_PROVIDER_TYPE, tableName=COMPONENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.7.0-KEYCLOAK-1267', 'sguilhen@redhat.com', 'META-INF/jpa-changelog-4.7.0.xml', '2024-07-18 08:41:01.763697', 68, 'EXECUTED', '9:88e0bfdda924690d6f4e430c53447dd5', 'addColumn tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.7.0-KEYCLOAK-7275', 'keycloak', 'META-INF/jpa-changelog-4.7.0.xml', '2024-07-18 08:41:01.772816', 69, 'EXECUTED', '9:f53177f137e1c46b6a88c59ec1cb5218', 'renameColumn newColumnName=CREATED_ON, oldColumnName=LAST_SESSION_REFRESH, tableName=OFFLINE_USER_SESSION; addNotNullConstraint columnName=CREATED_ON, tableName=OFFLINE_USER_SESSION; addColumn tableName=OFFLINE_USER_SESSION; customChange; createIn...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('4.8.0-KEYCLOAK-8835', 'sguilhen@redhat.com', 'META-INF/jpa-changelog-4.8.0.xml', '2024-07-18 08:41:01.779486', 70, 'EXECUTED', '9:a74d33da4dc42a37ec27121580d1459f', 'addNotNullConstraint columnName=SSO_MAX_LIFESPAN_REMEMBER_ME, tableName=REALM; addNotNullConstraint columnName=SSO_IDLE_TIMEOUT_REMEMBER_ME, tableName=REALM', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('authz-7.0.0-KEYCLOAK-10443', 'psilva@redhat.com', 'META-INF/jpa-changelog-authz-7.0.0.xml', '2024-07-18 08:41:01.781735', 71, 'EXECUTED', '9:fd4ade7b90c3b67fae0bfcfcb42dfb5f', 'addColumn tableName=RESOURCE_SERVER', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('8.0.0-adding-credential-columns', 'keycloak', 'META-INF/jpa-changelog-8.0.0.xml', '2024-07-18 08:41:01.78465', 72, 'EXECUTED', '9:aa072ad090bbba210d8f18781b8cebf4', 'addColumn tableName=CREDENTIAL; addColumn tableName=FED_USER_CREDENTIAL', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('8.0.0-updating-credential-data-not-oracle-fixed', 'keycloak', 'META-INF/jpa-changelog-8.0.0.xml', '2024-07-18 08:41:01.788352', 73, 'EXECUTED', '9:1ae6be29bab7c2aa376f6983b932be37', 'update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('8.0.0-updating-credential-data-oracle-fixed', 'keycloak', 'META-INF/jpa-changelog-8.0.0.xml', '2024-07-18 08:41:01.789577', 74, 'MARK_RAN', '9:14706f286953fc9a25286dbd8fb30d97', 'update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL; update tableName=FED_USER_CREDENTIAL', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('8.0.0-credential-cleanup-fixed', 'keycloak', 'META-INF/jpa-changelog-8.0.0.xml', '2024-07-18 08:41:01.801121', 75, 'EXECUTED', '9:2b9cc12779be32c5b40e2e67711a218b', 'dropDefaultValue columnName=COUNTER, tableName=CREDENTIAL; dropDefaultValue columnName=DIGITS, tableName=CREDENTIAL; dropDefaultValue columnName=PERIOD, tableName=CREDENTIAL; dropDefaultValue columnName=ALGORITHM, tableName=CREDENTIAL; dropColumn ...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('8.0.0-resource-tag-support', 'keycloak', 'META-INF/jpa-changelog-8.0.0.xml', '2024-07-18 08:41:01.80417', 76, 'EXECUTED', '9:91fa186ce7a5af127a2d7a91ee083cc5', 'addColumn tableName=MIGRATION_MODEL; createIndex indexName=IDX_UPDATE_TIME, tableName=MIGRATION_MODEL', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.0-always-display-client', 'keycloak', 'META-INF/jpa-changelog-9.0.0.xml', '2024-07-18 08:41:01.805618', 77, 'EXECUTED', '9:6335e5c94e83a2639ccd68dd24e2e5ad', 'addColumn tableName=CLIENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.0-drop-constraints-for-column-increase', 'keycloak', 'META-INF/jpa-changelog-9.0.0.xml', '2024-07-18 08:41:01.806223', 78, 'MARK_RAN', '9:6bdb5658951e028bfe16fa0a8228b530', 'dropUniqueConstraint constraintName=UK_FRSR6T700S9V50BU18WS5PMT, tableName=RESOURCE_SERVER_PERM_TICKET; dropUniqueConstraint constraintName=UK_FRSR6T700S9V50BU18WS5HA6, tableName=RESOURCE_SERVER_RESOURCE; dropPrimaryKey constraintName=CONSTRAINT_O...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.0-increase-column-size-federated-fk', 'keycloak', 'META-INF/jpa-changelog-9.0.0.xml', '2024-07-18 08:41:01.815865', 79, 'EXECUTED', '9:d5bc15a64117ccad481ce8792d4c608f', 'modifyDataType columnName=CLIENT_ID, tableName=FED_USER_CONSENT; modifyDataType columnName=CLIENT_REALM_CONSTRAINT, tableName=KEYCLOAK_ROLE; modifyDataType columnName=OWNER, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=CLIENT_ID, ta...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.0-recreate-constraints-after-column-increase', 'keycloak', 'META-INF/jpa-changelog-9.0.0.xml', '2024-07-18 08:41:01.816722', 80, 'MARK_RAN', '9:077cba51999515f4d3e7ad5619ab592c', 'addNotNullConstraint columnName=CLIENT_ID, tableName=OFFLINE_CLIENT_SESSION; addNotNullConstraint columnName=OWNER, tableName=RESOURCE_SERVER_PERM_TICKET; addNotNullConstraint columnName=REQUESTER, tableName=RESOURCE_SERVER_PERM_TICKET; addNotNull...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.1-add-index-to-client.client_id', 'keycloak', 'META-INF/jpa-changelog-9.0.1.xml', '2024-07-18 08:41:01.819377', 81, 'EXECUTED', '9:be969f08a163bf47c6b9e9ead8ac2afb', 'createIndex indexName=IDX_CLIENT_ID, tableName=CLIENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.1-KEYCLOAK-12579-drop-constraints', 'keycloak', 'META-INF/jpa-changelog-9.0.1.xml', '2024-07-18 08:41:01.820308', 82, 'MARK_RAN', '9:6d3bb4408ba5a72f39bd8a0b301ec6e3', 'dropUniqueConstraint constraintName=SIBLING_NAMES, tableName=KEYCLOAK_GROUP', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.1-KEYCLOAK-12579-add-not-null-constraint', 'keycloak', 'META-INF/jpa-changelog-9.0.1.xml', '2024-07-18 08:41:01.823209', 83, 'EXECUTED', '9:966bda61e46bebf3cc39518fbed52fa7', 'addNotNullConstraint columnName=PARENT_GROUP, tableName=KEYCLOAK_GROUP', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.1-KEYCLOAK-12579-recreate-constraints', 'keycloak', 'META-INF/jpa-changelog-9.0.1.xml', '2024-07-18 08:41:01.823856', 84, 'MARK_RAN', '9:8dcac7bdf7378e7d823cdfddebf72fda', 'addUniqueConstraint constraintName=SIBLING_NAMES, tableName=KEYCLOAK_GROUP', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('9.0.1-add-index-to-events', 'keycloak', 'META-INF/jpa-changelog-9.0.1.xml', '2024-07-18 08:41:01.826214', 85, 'EXECUTED', '9:7d93d602352a30c0c317e6a609b56599', 'createIndex indexName=IDX_EVENT_TIME, tableName=EVENT_ENTITY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('map-remove-ri', 'keycloak', 'META-INF/jpa-changelog-11.0.0.xml', '2024-07-18 08:41:01.827921', 86, 'EXECUTED', '9:71c5969e6cdd8d7b6f47cebc86d37627', 'dropForeignKeyConstraint baseTableName=REALM, constraintName=FK_TRAF444KK6QRKMS7N56AIWQ5Y; dropForeignKeyConstraint baseTableName=KEYCLOAK_ROLE, constraintName=FK_KJHO5LE2C0RAL09FL8CM9WFW9', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('map-remove-ri', 'keycloak', 'META-INF/jpa-changelog-12.0.0.xml', '2024-07-18 08:41:01.830313', 87, 'EXECUTED', '9:a9ba7d47f065f041b7da856a81762021', 'dropForeignKeyConstraint baseTableName=REALM_DEFAULT_GROUPS, constraintName=FK_DEF_GROUPS_GROUP; dropForeignKeyConstraint baseTableName=REALM_DEFAULT_ROLES, constraintName=FK_H4WPD7W4HSOOLNI3H0SW7BTJE; dropForeignKeyConstraint baseTableName=CLIENT...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('12.1.0-add-realm-localization-table', 'keycloak', 'META-INF/jpa-changelog-12.0.0.xml', '2024-07-18 08:41:01.835043', 88, 'EXECUTED', '9:fffabce2bc01e1a8f5110d5278500065', 'createTable tableName=REALM_LOCALIZATIONS; addPrimaryKey tableName=REALM_LOCALIZATIONS', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('default-roles', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.838396', 89, 'EXECUTED', '9:fa8a5b5445e3857f4b010bafb5009957', 'addColumn tableName=REALM; customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('default-roles-cleanup', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.8436', 90, 'EXECUTED', '9:67ac3241df9a8582d591c5ed87125f39', 'dropTable tableName=REALM_DEFAULT_ROLES; dropTable tableName=CLIENT_DEFAULT_ROLES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('13.0.0-KEYCLOAK-16844', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.846025', 91, 'EXECUTED', '9:ad1194d66c937e3ffc82386c050ba089', 'createIndex indexName=IDX_OFFLINE_USS_PRELOAD, tableName=OFFLINE_USER_SESSION', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('map-remove-ri-13.0.0', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.848559', 92, 'EXECUTED', '9:d9be619d94af5a2f5d07b9f003543b91', 'dropForeignKeyConstraint baseTableName=DEFAULT_CLIENT_SCOPE, constraintName=FK_R_DEF_CLI_SCOPE_SCOPE; dropForeignKeyConstraint baseTableName=CLIENT_SCOPE_CLIENT, constraintName=FK_C_CLI_SCOPE_SCOPE; dropForeignKeyConstraint baseTableName=CLIENT_SC...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('13.0.0-KEYCLOAK-17992-drop-constraints', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.849145', 93, 'MARK_RAN', '9:544d201116a0fcc5a5da0925fbbc3bde', 'dropPrimaryKey constraintName=C_CLI_SCOPE_BIND, tableName=CLIENT_SCOPE_CLIENT; dropIndex indexName=IDX_CLSCOPE_CL, tableName=CLIENT_SCOPE_CLIENT; dropIndex indexName=IDX_CL_CLSCOPE, tableName=CLIENT_SCOPE_CLIENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('13.0.0-increase-column-size-federated', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.852884', 94, 'EXECUTED', '9:43c0c1055b6761b4b3e89de76d612ccf', 'modifyDataType columnName=CLIENT_ID, tableName=CLIENT_SCOPE_CLIENT; modifyDataType columnName=SCOPE_ID, tableName=CLIENT_SCOPE_CLIENT', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('13.0.0-KEYCLOAK-17992-recreate-constraints', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.853725', 95, 'MARK_RAN', '9:8bd711fd0330f4fe980494ca43ab1139', 'addNotNullConstraint columnName=CLIENT_ID, tableName=CLIENT_SCOPE_CLIENT; addNotNullConstraint columnName=SCOPE_ID, tableName=CLIENT_SCOPE_CLIENT; addPrimaryKey constraintName=C_CLI_SCOPE_BIND, tableName=CLIENT_SCOPE_CLIENT; createIndex indexName=...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('json-string-accomodation-fixed', 'keycloak', 'META-INF/jpa-changelog-13.0.0.xml', '2024-07-18 08:41:01.856989', 96, 'EXECUTED', '9:e07d2bc0970c348bb06fb63b1f82ddbf', 'addColumn tableName=REALM_ATTRIBUTE; update tableName=REALM_ATTRIBUTE; dropColumn columnName=VALUE, tableName=REALM_ATTRIBUTE; renameColumn newColumnName=VALUE, oldColumnName=VALUE_NEW, tableName=REALM_ATTRIBUTE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('14.0.0-KEYCLOAK-11019', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.861454', 97, 'EXECUTED', '9:24fb8611e97f29989bea412aa38d12b7', 'createIndex indexName=IDX_OFFLINE_CSS_PRELOAD, tableName=OFFLINE_CLIENT_SESSION; createIndex indexName=IDX_OFFLINE_USS_BY_USER, tableName=OFFLINE_USER_SESSION; createIndex indexName=IDX_OFFLINE_USS_BY_USERSESS, tableName=OFFLINE_USER_SESSION', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('14.0.0-KEYCLOAK-18286', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.862175', 98, 'MARK_RAN', '9:259f89014ce2506ee84740cbf7163aa7', 'createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('14.0.0-KEYCLOAK-18286-revert', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.871025', 99, 'MARK_RAN', '9:04baaf56c116ed19951cbc2cca584022', 'dropIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('14.0.0-KEYCLOAK-18286-supported-dbs', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.873948', 100, 'EXECUTED', '9:60ca84a0f8c94ec8c3504a5a3bc88ee8', 'createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('14.0.0-KEYCLOAK-18286-unsupported-dbs', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.874619', 101, 'MARK_RAN', '9:d3d977031d431db16e2c181ce49d73e9', 'createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('KEYCLOAK-17267-add-index-to-user-attributes', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.877671', 102, 'EXECUTED', '9:0b305d8d1277f3a89a0a53a659ad274c', 'createIndex indexName=IDX_USER_ATTRIBUTE_NAME, tableName=USER_ATTRIBUTE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('KEYCLOAK-18146-add-saml-art-binding-identifier', 'keycloak', 'META-INF/jpa-changelog-14.0.0.xml', '2024-07-18 08:41:01.880717', 103, 'EXECUTED', '9:2c374ad2cdfe20e2905a84c8fac48460', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('15.0.0-KEYCLOAK-18467', 'keycloak', 'META-INF/jpa-changelog-15.0.0.xml', '2024-07-18 08:41:01.883887', 104, 'EXECUTED', '9:47a760639ac597360a8219f5b768b4de', 'addColumn tableName=REALM_LOCALIZATIONS; update tableName=REALM_LOCALIZATIONS; dropColumn columnName=TEXTS, tableName=REALM_LOCALIZATIONS; renameColumn newColumnName=TEXTS, oldColumnName=TEXTS_NEW, tableName=REALM_LOCALIZATIONS; addNotNullConstrai...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('17.0.0-9562', 'keycloak', 'META-INF/jpa-changelog-17.0.0.xml', '2024-07-18 08:41:01.886746', 105, 'EXECUTED', '9:a6272f0576727dd8cad2522335f5d99e', 'createIndex indexName=IDX_USER_SERVICE_ACCOUNT, tableName=USER_ENTITY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('18.0.0-10625-IDX_ADMIN_EVENT_TIME', 'keycloak', 'META-INF/jpa-changelog-18.0.0.xml', '2024-07-18 08:41:01.888863', 106, 'EXECUTED', '9:015479dbd691d9cc8669282f4828c41d', 'createIndex indexName=IDX_ADMIN_EVENT_TIME, tableName=ADMIN_EVENT_ENTITY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('19.0.0-10135', 'keycloak', 'META-INF/jpa-changelog-19.0.0.xml', '2024-07-18 08:41:01.891502', 107, 'EXECUTED', '9:9518e495fdd22f78ad6425cc30630221', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('20.0.0-12964-supported-dbs', 'keycloak', 'META-INF/jpa-changelog-20.0.0.xml', '2024-07-18 08:41:01.894149', 108, 'EXECUTED', '9:e5f243877199fd96bcc842f27a1656ac', 'createIndex indexName=IDX_GROUP_ATT_BY_NAME_VALUE, tableName=GROUP_ATTRIBUTE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('20.0.0-12964-unsupported-dbs', 'keycloak', 'META-INF/jpa-changelog-20.0.0.xml', '2024-07-18 08:41:01.89472', 109, 'MARK_RAN', '9:1a6fcaa85e20bdeae0a9ce49b41946a5', 'createIndex indexName=IDX_GROUP_ATT_BY_NAME_VALUE, tableName=GROUP_ATTRIBUTE', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('client-attributes-string-accomodation-fixed', 'keycloak', 'META-INF/jpa-changelog-20.0.0.xml', '2024-07-18 08:41:01.898179', 110, 'EXECUTED', '9:3f332e13e90739ed0c35b0b25b7822ca', 'addColumn tableName=CLIENT_ATTRIBUTES; update tableName=CLIENT_ATTRIBUTES; dropColumn columnName=VALUE, tableName=CLIENT_ATTRIBUTES; renameColumn newColumnName=VALUE, oldColumnName=VALUE_NEW, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('21.0.2-17277', 'keycloak', 'META-INF/jpa-changelog-21.0.2.xml', '2024-07-18 08:41:01.900645', 111, 'EXECUTED', '9:7ee1f7a3fb8f5588f171fb9a6ab623c0', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('21.1.0-19404', 'keycloak', 'META-INF/jpa-changelog-21.1.0.xml', '2024-07-18 08:41:01.916902', 112, 'EXECUTED', '9:3d7e830b52f33676b9d64f7f2b2ea634', 'modifyDataType columnName=DECISION_STRATEGY, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=LOGIC, tableName=RESOURCE_SERVER_POLICY; modifyDataType columnName=POLICY_ENFORCE_MODE, tableName=RESOURCE_SERVER', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('21.1.0-19404-2', 'keycloak', 'META-INF/jpa-changelog-21.1.0.xml', '2024-07-18 08:41:01.918245', 113, 'MARK_RAN', '9:627d032e3ef2c06c0e1f73d2ae25c26c', 'addColumn tableName=RESOURCE_SERVER_POLICY; update tableName=RESOURCE_SERVER_POLICY; dropColumn columnName=DECISION_STRATEGY, tableName=RESOURCE_SERVER_POLICY; renameColumn newColumnName=DECISION_STRATEGY, oldColumnName=DECISION_STRATEGY_NEW, tabl...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('22.0.0-17484-updated', 'keycloak', 'META-INF/jpa-changelog-22.0.0.xml', '2024-07-18 08:41:01.921638', 114, 'EXECUTED', '9:90af0bfd30cafc17b9f4d6eccd92b8b3', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('22.0.5-24031', 'keycloak', 'META-INF/jpa-changelog-22.0.0.xml', '2024-07-18 08:41:01.922523', 115, 'MARK_RAN', '9:a60d2d7b315ec2d3eba9e2f145f9df28', 'customChange', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('23.0.0-12062', 'keycloak', 'META-INF/jpa-changelog-23.0.0.xml', '2024-07-18 08:41:01.925415', 116, 'EXECUTED', '9:2168fbe728fec46ae9baf15bf80927b8', 'addColumn tableName=COMPONENT_CONFIG; update tableName=COMPONENT_CONFIG; dropColumn columnName=VALUE, tableName=COMPONENT_CONFIG; renameColumn newColumnName=VALUE, oldColumnName=VALUE_NEW, tableName=COMPONENT_CONFIG', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('23.0.0-17258', 'keycloak', 'META-INF/jpa-changelog-23.0.0.xml', '2024-07-18 08:41:01.926958', 117, 'EXECUTED', '9:36506d679a83bbfda85a27ea1864dca8', 'addColumn tableName=EVENT_ENTITY', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('24.0.0-9758', 'keycloak', 'META-INF/jpa-changelog-24.0.0.xml', '2024-07-18 08:41:01.933774', 118, 'EXECUTED', '9:502c557a5189f600f0f445a9b49ebbce', 'addColumn tableName=USER_ATTRIBUTE; addColumn tableName=FED_USER_ATTRIBUTE; createIndex indexName=USER_ATTR_LONG_VALUES, tableName=USER_ATTRIBUTE; createIndex indexName=FED_USER_ATTR_LONG_VALUES, tableName=FED_USER_ATTRIBUTE; createIndex indexName...', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('24.0.0-26618-drop-index-if-present', 'keycloak', 'META-INF/jpa-changelog-24.0.0.xml', '2024-07-18 08:41:01.938849', 120, 'MARK_RAN', '9:04baaf56c116ed19951cbc2cca584022', 'dropIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726'),
('24.0.0-26618-reindex', 'keycloak', 'META-INF/jpa-changelog-24.0.0.xml', '2024-07-18 08:41:01.941925', 121, 'EXECUTED', '9:08707c0f0db1cef6b352db03a60edc7f', 'createIndex indexName=IDX_CLIENT_ATT_BY_NAME_VALUE, tableName=CLIENT_ATTRIBUTES', '', NULL, '4.25.1', NULL, NULL, '1292060726');

INSERT INTO "public"."databasechangeloglock" ("id", "locked", "lockgranted", "lockedby") VALUES
(1, 'f', NULL, NULL),
(1000, 'f', NULL, NULL),
(1001, 'f', NULL, NULL);

INSERT INTO "public"."default_client_scope" ("realm_id", "scope_id", "default_scope") VALUES
('5f22ffdd-e26c-45a8-80c1-382f149facab', '0f53a958-9a66-4148-9d6e-2ee8c9b3e49c', 't'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', '26884290-b7d8-4952-b224-90a6ea20de6c', 'f'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', '284ff9bc-10a5-4cc4-9c20-154258f94211', 'f'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', '78863cd8-8bb4-4296-9700-83618b5d1726', 'f'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', '7a613d18-ae4c-4613-b6f5-9e33c4915b92', 'f'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', '820ed402-fc77-4525-a04c-bb4b9b86a8ba', 't'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', '82e5650b-0e47-4554-892a-be40598874b8', 't'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', 'a40b0daf-520e-491f-bd6e-21f22088fa6e', 't'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', 'b62b91e2-e4ca-4c34-94e6-4d60300386f7', 't'),
('5f22ffdd-e26c-45a8-80c1-382f149facab', 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', '16fb9da1-1d10-47a6-a62e-4c9bad36c267', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', '1a671fb4-828d-4617-a071-239196ed6493', 'f'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', '35489f79-ed93-44f2-9c13-e007406b8b43', 'f'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', '5fae990e-ff8e-4389-91db-3f48f3ff11d5', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', '858023ed-373a-4766-9b08-e0a00cb8e3b3', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', 'a550d1b2-19a6-4952-877c-9d720393c8e0', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef', 't'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9', 'f'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14', 'f');

INSERT INTO "public"."keycloak_role" ("id", "client_realm_constraint", "client_role", "description", "name", "realm_id", "client", "realm") VALUES
('0b879c2f-ded7-45df-bf59-adcecef77cd0', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_view-authorization}', 'view-authorization', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('0d650979-c418-4631-ae99-1601e8e555b5', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_delete-account}', 'delete-account', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('1d5abdb1-d9ec-4c3a-b63b-62d6bc447348', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_view-realm}', 'view-realm', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('1edb31c8-fc3c-4483-90de-1dc75d81c522', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_query-clients}', 'query-clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('2440c6dc-07b2-4b2f-bb5e-cb709f347a49', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_manage-events}', 'manage-events', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('24df1b04-4f42-4ba4-9cd5-0fac5a474b0b', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'f', '${role_offline-access}', 'offline_access', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL, NULL),
('251031da-6462-4f04-8bf6-d2a8ca146ad9', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_manage-account}', 'manage-account', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('28055d79-f37d-45af-894f-a8a0430e968d', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_view-identity-providers}', 'view-identity-providers', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('2c1261e2-f28f-473e-92e8-dea6a74b5a1d', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', '${role_create-realm}', 'create-realm', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, NULL),
('385e08a8-357b-4b06-adb6-58fab59f8d06', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_manage-authorization}', 'manage-authorization', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('3a52e281-3104-42c0-aeb3-b132feeeb452', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_manage-events}', 'manage-events', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('3cc61388-e5d5-4b6c-b980-6bc82a3e38c5', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', '${role_offline-access}', 'offline_access', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, NULL),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'f', '${role_default-roles}', 'default-roles-avenirs', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL, NULL),
('3e56333e-32dd-4bf1-a99e-32613119b705', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_view-profile}', 'view-profile', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('3f8fbb4f-b052-4804-a2a1-c30b1fab54d5', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_manage-identity-providers}', 'manage-identity-providers', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('40cb5431-5c12-48d9-b52c-7302ad80bdfc', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_manage-clients}', 'manage-clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('42488b72-58b0-46f5-b63d-9b45d6bb23d0', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_query-groups}', 'query-groups', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('447d13a1-0273-4c89-ba22-ed2b5c32ec72', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_query-users}', 'query-users', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('46829747-e692-4f67-a157-d075f7a97d72', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_query-realms}', 'query-realms', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('4741d45f-6128-41d3-abf0-534d847074b2', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_create-client}', 'create-client', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('4c176de3-3d7c-40f3-92b5-a1ccf21610fa', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_view-events}', 'view-events', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('4d316a8f-f9ca-4c26-a78a-4d0ff5a2548e', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_manage-identity-providers}', 'manage-identity-providers', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('4f309be6-e6ca-4040-9753-eef7f37b4798', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_manage-clients}', 'manage-clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('4fafdfb9-1842-419a-ad6a-66d17d68a475', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', '${role_uma_authorization}', 'uma_authorization', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, NULL),
('4ff3ecbc-ed33-46e6-bcfc-75ff7cabafd8', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_view-profile}', 'view-profile', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('56d3f041-62d2-4190-86a6-bedb3791013b', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_manage-realm}', 'manage-realm', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('585d6263-5768-4aea-805c-3991a50562b6', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_view-clients}', 'view-clients', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('5931fab6-4ed3-48b7-a97d-fd40a336350d', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_impersonation}', 'impersonation', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('5be6a567-f4c6-4d39-935b-ac692f759f8e', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_manage-users}', 'manage-users', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('5c074436-8387-44ac-846e-51c2d39ef212', 'cc87bab5-4e4e-4c37-91c3-212ef60fae17', 't', '${role_read-token}', 'read-token', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'cc87bab5-4e4e-4c37-91c3-212ef60fae17', NULL),
('5c836613-2cb2-453a-bfa4-4c2bcb08fb75', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_manage-identity-providers}', 'manage-identity-providers', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('5e577bdf-7d1a-45b9-a281-9b511ccf7f6c', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_view-users}', 'view-users', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('618261c1-13aa-41d8-a463-ffc3379f38ac', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_manage-users}', 'manage-users', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('6226b6de-a765-4b3f-ae99-48468a3287d6', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_manage-consent}', 'manage-consent', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('62a149f5-89a3-49d5-8aea-c9b92436e6f2', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_realm-admin}', 'realm-admin', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('646e98b8-c472-4740-b4a1-3143bc80f64a', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_query-users}', 'query-users', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('6f9e3ddc-1d60-4330-8bee-191f68d29dfb', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_view-events}', 'view-events', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('72f6fc94-b66c-40d8-b861-16244b59232d', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_view-events}', 'view-events', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('732c2c4f-d34f-466e-8390-ffd034c7c7c0', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_view-users}', 'view-users', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('74e0a0f1-3f93-45f0-9b39-9a1999919006', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_query-realms}', 'query-realms', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', '${role_admin}', 'admin', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, NULL),
('781df7d9-4c66-449e-9cf9-5178dab9d955', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_view-authorization}', 'view-authorization', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('78c444f5-22ad-4563-afe2-a9bdc641ef85', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_query-realms}', 'query-realms', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('7b8ee897-a4b2-4ca7-ba21-d6d70f571c10', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_manage-account-links}', 'manage-account-links', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('7ba90225-e3bd-49cb-b275-8821da5fd8b2', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_manage-account-links}', 'manage-account-links', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('7cac211a-5046-48da-b0cb-ba12673a606b', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'f', '${role_uma_authorization}', 'uma_authorization', '5f22ffdd-e26c-45a8-80c1-382f149facab', NULL, NULL),
('814d5d22-d0c5-4f89-80a6-cbf18261b834', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_impersonation}', 'impersonation', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('83aefa93-4004-40e1-be65-88f21e1e1523', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_view-clients}', 'view-clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('84705d7b-87a3-44a8-8e2e-d3e859a648f8', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_view-identity-providers}', 'view-identity-providers', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('8966b737-6fe9-47ed-ae19-05f7c19eed1f', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_view-groups}', 'view-groups', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('8b8eac30-7aec-45ca-896e-4920dd355854', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_manage-authorization}', 'manage-authorization', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('97a8daf9-bb0f-4762-8b47-20662bbcb5a8', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_query-groups}', 'query-groups', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('98b45923-7952-4505-adb1-af6e7b122c15', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_create-client}', 'create-client', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('9e36f61e-7e38-4110-bf09-3d99253617b0', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_query-clients}', 'query-clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('a407e863-8cc3-4234-92dd-5733c7d13501', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_view-realm}', 'view-realm', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('a6509e2f-e380-432f-b249-8cd8862f69fb', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_manage-realm}', 'manage-realm', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('a8922e31-e6cf-4d72-b741-c0bace915424', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_view-clients}', 'view-clients', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('a96fa59b-c15b-4074-9334-a0c3af830086', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_query-users}', 'query-users', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('abea39fb-e472-45bc-aaf0-69d902107670', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_manage-consent}', 'manage-consent', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('ac37c5f0-5006-4ed3-b457-fd0354c0114e', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_impersonation}', 'impersonation', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('b765e80e-655c-404d-a65e-6b82295a6977', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_view-realm}', 'view-realm', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('b9147ff5-ff49-43da-acc6-db13538d3707', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_view-consent}', 'view-consent', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('c407072b-cf62-41d9-b727-33002583c30d', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_query-clients}', 'query-clients', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('c70489d3-2936-485f-a7eb-32b3ce8145df', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_manage-realm}', 'manage-realm', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('ca61f9e8-b6d4-4f27-bf46-5803219408ca', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_manage-account}', 'manage-account', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('cd60378e-8e42-4ae8-bac6-d9424c59fd1e', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_create-client}', 'create-client', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('cfb8a860-12a3-48df-a53a-3a9af7970193', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_manage-authorization}', 'manage-authorization', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('d11656b6-80e3-4586-b997-b8ce1da0321a', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_view-users}', 'view-users', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('d4d2543d-60d5-4615-97ff-3e325662a8f1', 'f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', 't', '${role_read-token}', 'read-token', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'f26c5799-eb0f-4d7e-95e5-70f4bad7eaf4', NULL),
('de4c2d8b-5490-48cd-a866-fbd96025d1c5', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_view-consent}', 'view-consent', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('e3cd5ce0-7759-4000-8626-3813c8a33703', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_view-authorization}', 'view-authorization', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL),
('e4465ad3-d92c-4895-86e2-6d9d59a6d39a', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_view-identity-providers}', 'view-identity-providers', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('e497a841-53af-4eb6-863d-5623aa30a330', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', '${role_default-roles}', 'default-roles-master', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', NULL, NULL),
('e545fc06-5f3b-4949-9282-124f94206594', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 't', '${role_manage-events}', 'manage-events', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', NULL),
('e76578b0-9441-4020-abe6-ac4f524ee305', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_manage-users}', 'manage-users', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('f0997a92-0e74-49bc-9e63-073b0e7eed6a', 'bf1482b1-f598-4da3-982e-146e190880e1', 't', '${role_query-groups}', 'query-groups', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'bf1482b1-f598-4da3-982e-146e190880e1', NULL),
('f8ff2639-bd80-429d-b766-455e024f8926', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_view-applications}', 'view-applications', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('fb325292-64b7-47d6-9e5b-ba85664b3c7d', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_view-applications}', 'view-applications', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('fc479e5f-7cca-4376-a63b-27c28c802855', '1acd0f60-85ea-485a-933a-16a294f49529', 't', '${role_delete-account}', 'delete-account', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1acd0f60-85ea-485a-933a-16a294f49529', NULL),
('fd324272-4528-46f6-b03c-81e0bfad1623', 'c7207b89-d86c-425d-a252-510cb2bc7e39', 't', '${role_view-groups}', 'view-groups', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'c7207b89-d86c-425d-a252-510cb2bc7e39', NULL),
('fe7bd900-a0eb-4ebf-814e-f06f807151af', 'a9819d56-3d39-4dc4-865f-deecc78b7477', 't', '${role_manage-clients}', 'manage-clients', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'a9819d56-3d39-4dc4-865f-deecc78b7477', NULL);

INSERT INTO "public"."migration_model" ("id", "version", "update_time") VALUES
('qcz7u', '24.0.1', 1721292062);

INSERT INTO "public"."protocol_mapper" ("id", "name", "protocol", "protocol_mapper_name", "client_id", "client_scope_id") VALUES
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'phone number verified', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'locale', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'upn', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '1a671fb4-828d-4617-a071-239196ed6493'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'groups', 'openid-connect', 'oidc-usermodel-realm-role-mapper', NULL, '1a671fb4-828d-4617-a071-239196ed6493'),
('1b3fcc93-94dc-472b-a36d-4696fa7692d7', 'acr loa level', 'openid-connect', 'oidc-acr-mapper', NULL, 'd9b12bdb-6bf5-4991-a4f5-e9dbbfff9ef9'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'birthdate', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'given name', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'website', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('26b1a380-4d40-42ac-81fa-73122b79ad0a', 'allowed web origins', 'openid-connect', 'oidc-allowed-origins-mapper', NULL, '5fae990e-ff8e-4389-91db-3f48f3ff11d5'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'nickname', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'upn', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '26884290-b7d8-4952-b224-90a6ea20de6c'),
('2a7790e9-d663-4e5f-ae27-32ef516d215b', 'full name', 'openid-connect', 'oidc-full-name-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'locale', 'openid-connect', 'oidc-usermodel-attribute-mapper', '0cbca4c2-c802-473b-b7d2-15875ed6a364', NULL),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'website', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'locale', 'openid-connect', 'oidc-usermodel-attribute-mapper', '3d1829e6-92da-4617-bb14-77c94e568f64', NULL),
('412c3d5b-f8d2-4146-805a-a1af8fca39dc', 'audience resolve', 'openid-connect', 'oidc-audience-resolve-mapper', NULL, 'a40b0daf-520e-491f-bd6e-21f22088fa6e'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'picture', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'phone number verified', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '78863cd8-8bb4-4296-9700-83618b5d1726'),
('4bf6274c-9fd0-4efb-b03a-b4210062c4d9', 'audience resolve', 'openid-connect', 'oidc-audience-resolve-mapper', '560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', NULL),
('522206d1-295d-4813-9fd7-6c8f8daf7ee5', 'allowed web origins', 'openid-connect', 'oidc-allowed-origins-mapper', NULL, '820ed402-fc77-4525-a04c-bb4b9b86a8ba'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'username', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'nickname', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'given name', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('6800e482-9c06-478c-aa20-21eeec2b5258', 'audience resolve', 'openid-connect', 'oidc-audience-resolve-mapper', NULL, '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'client roles', 'openid-connect', 'oidc-usermodel-client-role-mapper', NULL, 'a40b0daf-520e-491f-bd6e-21f22088fa6e'),
('71a04d8a-3c9b-439d-a332-40afc2d8ef7b', 'audience resolve', 'openid-connect', 'oidc-audience-resolve-mapper', '4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', NULL),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'groups', 'openid-connect', 'oidc-usermodel-realm-role-mapper', NULL, '26884290-b7d8-4952-b224-90a6ea20de6c'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'realm roles', 'openid-connect', 'oidc-usermodel-realm-role-mapper', NULL, '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'phone number', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '78863cd8-8bb4-4296-9700-83618b5d1726'),
('8abb5153-0768-4ecd-9f75-305530aab45a', 'role list', 'saml', 'saml-role-list-mapper', NULL, 'a550d1b2-19a6-4952-877c-9d720393c8e0'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'family name', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'zoneinfo', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'address', 'openid-connect', 'oidc-address-mapper', NULL, 'd763d8f2-5e16-4c96-91c8-dd70d65a2f14'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'picture', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('994b2c61-633e-4ca2-aaa9-865b2a57cc74', 'role list', 'saml', 'saml-role-list-mapper', NULL, '0f53a958-9a66-4148-9d6e-2ee8c9b3e49c'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'updated at', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'username', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'birthdate', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'client roles', 'openid-connect', 'oidc-usermodel-client-role-mapper', NULL, '44c2f057-816b-4bb3-bdd5-f2a35f8eaed7'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'middle name', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'gender', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'profile', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'updated at', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'gender', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('c1822540-128a-4eb3-b2f5-0f3888c05ca1', 'acr loa level', 'openid-connect', 'oidc-acr-mapper', NULL, '16fb9da1-1d10-47a6-a62e-4c9bad36c267'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'phone number', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'b48a1e4f-24a9-4b0b-b2ba-93a1bdacedc9'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'email verified', 'openid-connect', 'oidc-usermodel-property-mapper', NULL, '858023ed-373a-4766-9b08-e0a00cb8e3b3'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'family name', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'email verified', 'openid-connect', 'oidc-usermodel-property-mapper', NULL, 'b62b91e2-e4ca-4c34-94e6-4d60300386f7'),
('f0400cd6-b4d7-48b9-9425-c95edec90284', 'full name', 'openid-connect', 'oidc-full-name-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'realm roles', 'openid-connect', 'oidc-usermodel-realm-role-mapper', NULL, 'a40b0daf-520e-491f-bd6e-21f22088fa6e'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'address', 'openid-connect', 'oidc-address-mapper', NULL, '284ff9bc-10a5-4cc4-9c20-154258f94211'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'email', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'b62b91e2-e4ca-4c34-94e6-4d60300386f7'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'middle name', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'locale', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'profile', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '82e5650b-0e47-4554-892a-be40598874b8'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'zoneinfo', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, 'a65eed40-e4d6-45d6-ad15-bcebeb8a23ef'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'email', 'openid-connect', 'oidc-usermodel-attribute-mapper', NULL, '858023ed-373a-4766-9b08-e0a00cb8e3b3');

INSERT INTO "public"."protocol_mapper_config" ("protocol_mapper_id", "value", "name") VALUES
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'true', 'access.token.claim'),
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'phone_number_verified', 'claim.name'),
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'true', 'id.token.claim'),
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'true', 'introspection.token.claim'),
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'boolean', 'jsonType.label'),
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'phoneNumberVerified', 'user.attribute'),
('03a9e304-0ed0-4129-88fe-05bf9724d7dd', 'true', 'userinfo.token.claim'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'true', 'access.token.claim'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'locale', 'claim.name'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'true', 'id.token.claim'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'true', 'introspection.token.claim'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'String', 'jsonType.label'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'locale', 'user.attribute'),
('07c4496e-b0c2-4340-91fe-07085aed58aa', 'true', 'userinfo.token.claim'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'true', 'access.token.claim'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'upn', 'claim.name'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'true', 'id.token.claim'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'true', 'introspection.token.claim'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'String', 'jsonType.label'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'username', 'user.attribute'),
('0b9baa5c-a737-436a-ba65-52df0641a467', 'true', 'userinfo.token.claim'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'true', 'access.token.claim'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'groups', 'claim.name'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'true', 'id.token.claim'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'true', 'introspection.token.claim'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'String', 'jsonType.label'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'true', 'multivalued'),
('1292075a-3074-49b9-b882-6474f7ac0d9a', 'foo', 'user.attribute'),
('1b3fcc93-94dc-472b-a36d-4696fa7692d7', 'true', 'access.token.claim'),
('1b3fcc93-94dc-472b-a36d-4696fa7692d7', 'true', 'id.token.claim'),
('1b3fcc93-94dc-472b-a36d-4696fa7692d7', 'true', 'introspection.token.claim'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'true', 'access.token.claim'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'birthdate', 'claim.name'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'true', 'id.token.claim'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'true', 'introspection.token.claim'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'String', 'jsonType.label'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'birthdate', 'user.attribute'),
('1d4df3f2-90c1-4f25-ba51-8a8db91cc847', 'true', 'userinfo.token.claim'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'true', 'access.token.claim'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'given_name', 'claim.name'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'true', 'id.token.claim'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'true', 'introspection.token.claim'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'String', 'jsonType.label'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'firstName', 'user.attribute'),
('2535031e-f0d2-4448-a6a2-ed33469141e1', 'true', 'userinfo.token.claim'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'true', 'access.token.claim'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'website', 'claim.name'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'true', 'id.token.claim'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'true', 'introspection.token.claim'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'String', 'jsonType.label'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'website', 'user.attribute'),
('25932e06-ea65-4837-a99c-09c9726acfbb', 'true', 'userinfo.token.claim'),
('26b1a380-4d40-42ac-81fa-73122b79ad0a', 'true', 'access.token.claim'),
('26b1a380-4d40-42ac-81fa-73122b79ad0a', 'true', 'introspection.token.claim'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'true', 'access.token.claim'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'nickname', 'claim.name'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'true', 'id.token.claim'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'true', 'introspection.token.claim'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'String', 'jsonType.label'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'nickname', 'user.attribute'),
('26dec450-f881-4215-a4e0-7c918a47cd0c', 'true', 'userinfo.token.claim'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'true', 'access.token.claim'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'upn', 'claim.name'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'true', 'id.token.claim'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'true', 'introspection.token.claim'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'String', 'jsonType.label'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'username', 'user.attribute'),
('29fea011-8d00-4636-8294-d86255c3e02c', 'true', 'userinfo.token.claim'),
('2a7790e9-d663-4e5f-ae27-32ef516d215b', 'true', 'access.token.claim'),
('2a7790e9-d663-4e5f-ae27-32ef516d215b', 'true', 'id.token.claim'),
('2a7790e9-d663-4e5f-ae27-32ef516d215b', 'true', 'introspection.token.claim'),
('2a7790e9-d663-4e5f-ae27-32ef516d215b', 'true', 'userinfo.token.claim'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'true', 'access.token.claim'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'locale', 'claim.name'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'true', 'id.token.claim'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'true', 'introspection.token.claim'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'String', 'jsonType.label'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'locale', 'user.attribute'),
('39263777-5a2a-4fc2-9590-19b0b9a8864c', 'true', 'userinfo.token.claim'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'true', 'access.token.claim'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'website', 'claim.name'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'true', 'id.token.claim'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'true', 'introspection.token.claim'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'String', 'jsonType.label'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'website', 'user.attribute'),
('3b97599d-d1bf-40ee-a9ba-597f88cea66e', 'true', 'userinfo.token.claim'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'true', 'access.token.claim'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'locale', 'claim.name'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'true', 'id.token.claim'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'true', 'introspection.token.claim'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'String', 'jsonType.label'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'locale', 'user.attribute'),
('3dc01786-2a9e-403e-8253-8f6b5dab4b03', 'true', 'userinfo.token.claim'),
('412c3d5b-f8d2-4146-805a-a1af8fca39dc', 'true', 'access.token.claim'),
('412c3d5b-f8d2-4146-805a-a1af8fca39dc', 'true', 'introspection.token.claim'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'true', 'access.token.claim'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'picture', 'claim.name'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'true', 'id.token.claim'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'true', 'introspection.token.claim'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'String', 'jsonType.label'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'picture', 'user.attribute'),
('4739b00f-4810-40bb-bdc6-55e5b54eff33', 'true', 'userinfo.token.claim'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'true', 'access.token.claim'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'phone_number_verified', 'claim.name'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'true', 'id.token.claim'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'true', 'introspection.token.claim'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'boolean', 'jsonType.label'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'phoneNumberVerified', 'user.attribute'),
('47ffebdc-c1cf-4fd0-aef5-08473828655b', 'true', 'userinfo.token.claim'),
('522206d1-295d-4813-9fd7-6c8f8daf7ee5', 'true', 'access.token.claim'),
('522206d1-295d-4813-9fd7-6c8f8daf7ee5', 'true', 'introspection.token.claim'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'true', 'access.token.claim'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'preferred_username', 'claim.name'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'true', 'id.token.claim'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'true', 'introspection.token.claim'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'String', 'jsonType.label'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'username', 'user.attribute'),
('5f97e343-af57-4d24-b33e-f47fa081ad82', 'true', 'userinfo.token.claim'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'true', 'access.token.claim'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'nickname', 'claim.name'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'true', 'id.token.claim'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'true', 'introspection.token.claim'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'String', 'jsonType.label'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'nickname', 'user.attribute'),
('5fd7bca9-c4ac-485d-8ae7-6bb7cd974cb4', 'true', 'userinfo.token.claim'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'true', 'access.token.claim'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'given_name', 'claim.name'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'true', 'id.token.claim'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'true', 'introspection.token.claim'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'String', 'jsonType.label'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'firstName', 'user.attribute'),
('64f6b8bf-be31-46d5-ac13-dd49d85c2a9a', 'true', 'userinfo.token.claim'),
('6800e482-9c06-478c-aa20-21eeec2b5258', 'true', 'access.token.claim'),
('6800e482-9c06-478c-aa20-21eeec2b5258', 'true', 'introspection.token.claim'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'true', 'access.token.claim'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'resource_access.${client_id}.roles', 'claim.name'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'true', 'introspection.token.claim'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'String', 'jsonType.label'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'true', 'multivalued'),
('6e36470c-03c1-409f-880a-ca1803c52c63', 'foo', 'user.attribute'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'true', 'access.token.claim'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'groups', 'claim.name'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'true', 'id.token.claim'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'true', 'introspection.token.claim'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'String', 'jsonType.label'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'true', 'multivalued'),
('789ccf5d-08d9-476e-8141-c966bb044a12', 'foo', 'user.attribute'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'true', 'access.token.claim'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'realm_access.roles', 'claim.name'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'true', 'introspection.token.claim'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'String', 'jsonType.label'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'true', 'multivalued'),
('78b78c55-49c6-41cf-a76a-b241670b2e1a', 'foo', 'user.attribute'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'true', 'access.token.claim'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'phone_number', 'claim.name'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'true', 'id.token.claim'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'true', 'introspection.token.claim'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'String', 'jsonType.label'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'phoneNumber', 'user.attribute'),
('8499bff4-96b2-46af-a958-0b36c640041f', 'true', 'userinfo.token.claim'),
('8abb5153-0768-4ecd-9f75-305530aab45a', 'Role', 'attribute.name'),
('8abb5153-0768-4ecd-9f75-305530aab45a', 'Basic', 'attribute.nameformat'),
('8abb5153-0768-4ecd-9f75-305530aab45a', 'false', 'single'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'true', 'access.token.claim'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'family_name', 'claim.name'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'true', 'id.token.claim'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'true', 'introspection.token.claim'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'String', 'jsonType.label'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'lastName', 'user.attribute'),
('8fe88768-062a-4321-a747-95d0143b7f39', 'true', 'userinfo.token.claim'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'true', 'access.token.claim'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'zoneinfo', 'claim.name'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'true', 'id.token.claim'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'true', 'introspection.token.claim'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'String', 'jsonType.label'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'zoneinfo', 'user.attribute'),
('9609844a-f54d-4910-bf53-8a07ab0b98a7', 'true', 'userinfo.token.claim'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'true', 'access.token.claim'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'true', 'id.token.claim'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'true', 'introspection.token.claim'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'country', 'user.attribute.country'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'formatted', 'user.attribute.formatted'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'locality', 'user.attribute.locality'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'postal_code', 'user.attribute.postal_code'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'region', 'user.attribute.region'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'street', 'user.attribute.street'),
('979faf72-7d98-473d-bc92-94f7912f52ce', 'true', 'userinfo.token.claim'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'true', 'access.token.claim'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'picture', 'claim.name'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'true', 'id.token.claim'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'true', 'introspection.token.claim'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'String', 'jsonType.label'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'picture', 'user.attribute'),
('98d87e4b-28ac-485c-9338-079a6235fd2f', 'true', 'userinfo.token.claim'),
('994b2c61-633e-4ca2-aaa9-865b2a57cc74', 'Role', 'attribute.name'),
('994b2c61-633e-4ca2-aaa9-865b2a57cc74', 'Basic', 'attribute.nameformat'),
('994b2c61-633e-4ca2-aaa9-865b2a57cc74', 'false', 'single'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'true', 'access.token.claim'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'updated_at', 'claim.name'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'true', 'id.token.claim'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'true', 'introspection.token.claim'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'long', 'jsonType.label'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'updatedAt', 'user.attribute'),
('9c86ab37-dd50-4046-983a-61bd600ef9aa', 'true', 'userinfo.token.claim'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'true', 'access.token.claim'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'preferred_username', 'claim.name'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'true', 'id.token.claim'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'true', 'introspection.token.claim'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'String', 'jsonType.label'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'username', 'user.attribute'),
('a9d4a472-d8a9-434b-9448-8becc0285c95', 'true', 'userinfo.token.claim'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'true', 'access.token.claim'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'birthdate', 'claim.name'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'true', 'id.token.claim'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'true', 'introspection.token.claim'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'String', 'jsonType.label'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'birthdate', 'user.attribute'),
('ab07d043-4bd2-4b0f-8271-fdca79c5b62c', 'true', 'userinfo.token.claim'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'true', 'access.token.claim'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'resource_access.${client_id}.roles', 'claim.name'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'true', 'introspection.token.claim'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'String', 'jsonType.label'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'true', 'multivalued'),
('ac881a68-df3c-4794-9d6b-03af4f70d9e6', 'foo', 'user.attribute'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'true', 'access.token.claim'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'middle_name', 'claim.name'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'true', 'id.token.claim'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'true', 'introspection.token.claim'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'String', 'jsonType.label'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'middleName', 'user.attribute'),
('b28d2d8d-1044-4548-9566-1cd7d3b5df9e', 'true', 'userinfo.token.claim'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'true', 'access.token.claim'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'gender', 'claim.name'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'true', 'id.token.claim'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'true', 'introspection.token.claim'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'String', 'jsonType.label'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'gender', 'user.attribute'),
('b32312f9-723c-445b-a1dc-a8d235d3cc26', 'true', 'userinfo.token.claim'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'true', 'access.token.claim'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'profile', 'claim.name'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'true', 'id.token.claim'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'true', 'introspection.token.claim'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'String', 'jsonType.label'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'profile', 'user.attribute'),
('b9df220b-c396-47ad-88c7-c7176b2a0d9b', 'true', 'userinfo.token.claim'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'true', 'access.token.claim'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'updated_at', 'claim.name'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'true', 'id.token.claim'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'true', 'introspection.token.claim'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'long', 'jsonType.label'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'updatedAt', 'user.attribute'),
('bd47c5a8-144e-4c52-8c64-0c004d051577', 'true', 'userinfo.token.claim'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'true', 'access.token.claim'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'gender', 'claim.name'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'true', 'id.token.claim'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'true', 'introspection.token.claim'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'String', 'jsonType.label'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'gender', 'user.attribute'),
('bf088b40-7098-4a3f-8e38-3c632c21f131', 'true', 'userinfo.token.claim'),
('c1822540-128a-4eb3-b2f5-0f3888c05ca1', 'true', 'access.token.claim'),
('c1822540-128a-4eb3-b2f5-0f3888c05ca1', 'true', 'id.token.claim'),
('c1822540-128a-4eb3-b2f5-0f3888c05ca1', 'true', 'introspection.token.claim'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'true', 'access.token.claim'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'phone_number', 'claim.name'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'true', 'id.token.claim'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'true', 'introspection.token.claim'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'String', 'jsonType.label'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'phoneNumber', 'user.attribute'),
('ca27af7c-bfe7-4109-99bb-7e9de36b4a52', 'true', 'userinfo.token.claim'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'true', 'access.token.claim'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'email_verified', 'claim.name'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'true', 'id.token.claim'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'true', 'introspection.token.claim'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'boolean', 'jsonType.label'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'emailVerified', 'user.attribute'),
('d56f4ca1-0790-4a15-a117-da120d76b05b', 'true', 'userinfo.token.claim'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'true', 'access.token.claim'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'family_name', 'claim.name'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'true', 'id.token.claim'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'true', 'introspection.token.claim'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'String', 'jsonType.label'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'lastName', 'user.attribute'),
('db0b5495-f57c-42bc-b1b6-63ca4dce6a35', 'true', 'userinfo.token.claim'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'true', 'access.token.claim'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'email_verified', 'claim.name'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'true', 'id.token.claim'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'true', 'introspection.token.claim'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'boolean', 'jsonType.label'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'emailVerified', 'user.attribute'),
('e0290563-6868-4eda-bcc7-8f56a687639a', 'true', 'userinfo.token.claim'),
('f0400cd6-b4d7-48b9-9425-c95edec90284', 'true', 'access.token.claim'),
('f0400cd6-b4d7-48b9-9425-c95edec90284', 'true', 'id.token.claim'),
('f0400cd6-b4d7-48b9-9425-c95edec90284', 'true', 'introspection.token.claim'),
('f0400cd6-b4d7-48b9-9425-c95edec90284', 'true', 'userinfo.token.claim'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'true', 'access.token.claim'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'realm_access.roles', 'claim.name'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'true', 'introspection.token.claim'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'String', 'jsonType.label'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'true', 'multivalued'),
('f1411c65-842d-42ca-a142-91ab21a967fc', 'foo', 'user.attribute'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'true', 'access.token.claim'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'true', 'id.token.claim'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'true', 'introspection.token.claim'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'country', 'user.attribute.country'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'formatted', 'user.attribute.formatted'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'locality', 'user.attribute.locality'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'postal_code', 'user.attribute.postal_code'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'region', 'user.attribute.region'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'street', 'user.attribute.street'),
('f2d111ef-5e77-4fcc-974d-6490b4c8a8b5', 'true', 'userinfo.token.claim'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'true', 'access.token.claim'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'email', 'claim.name'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'true', 'id.token.claim'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'true', 'introspection.token.claim'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'String', 'jsonType.label'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'email', 'user.attribute'),
('f3958f16-441b-4880-aa93-0ecdfde8df7a', 'true', 'userinfo.token.claim'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'true', 'access.token.claim'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'middle_name', 'claim.name'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'true', 'id.token.claim'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'true', 'introspection.token.claim'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'String', 'jsonType.label'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'middleName', 'user.attribute'),
('f84f0e53-8c0c-4292-8a46-1cf21ff9905f', 'true', 'userinfo.token.claim'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'true', 'access.token.claim'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'locale', 'claim.name'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'true', 'id.token.claim'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'true', 'introspection.token.claim'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'String', 'jsonType.label'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'locale', 'user.attribute'),
('fb57f3f4-7ddd-4b98-9c38-d3c538523bc4', 'true', 'userinfo.token.claim'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'true', 'access.token.claim'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'profile', 'claim.name'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'true', 'id.token.claim'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'true', 'introspection.token.claim'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'String', 'jsonType.label'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'profile', 'user.attribute'),
('fcc0ce9f-1ecf-4965-8eb4-eeaeb3d83a57', 'true', 'userinfo.token.claim'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'true', 'access.token.claim'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'zoneinfo', 'claim.name'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'true', 'id.token.claim'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'true', 'introspection.token.claim'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'String', 'jsonType.label'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'zoneinfo', 'user.attribute'),
('fe2fe565-6a53-4ac3-b415-3c5b50966792', 'true', 'userinfo.token.claim'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'true', 'access.token.claim'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'email', 'claim.name'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'true', 'id.token.claim'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'true', 'introspection.token.claim'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'String', 'jsonType.label'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'email', 'user.attribute'),
('fec11e62-0ec0-4b3d-ae8e-2d8e90efed2a', 'true', 'userinfo.token.claim');

INSERT INTO "public"."realm" ("id", "access_code_lifespan", "user_action_lifespan", "access_token_lifespan", "account_theme", "admin_theme", "email_theme", "enabled", "events_enabled", "events_expiration", "login_theme", "name", "not_before", "password_policy", "registration_allowed", "remember_me", "reset_password_allowed", "social", "ssl_required", "sso_idle_timeout", "sso_max_lifespan", "update_profile_on_soc_login", "verify_email", "master_admin_client", "login_lifespan", "internationalization_enabled", "default_locale", "reg_email_as_username", "admin_events_enabled", "admin_events_details_enabled", "edit_username_allowed", "otp_policy_counter", "otp_policy_window", "otp_policy_period", "otp_policy_digits", "otp_policy_alg", "otp_policy_type", "browser_flow", "registration_flow", "direct_grant_flow", "reset_credentials_flow", "client_auth_flow", "offline_session_idle_timeout", "revoke_refresh_token", "access_token_life_implicit", "login_with_email_allowed", "duplicate_emails_allowed", "docker_auth_flow", "refresh_token_max_reuse", "allow_user_managed_access", "sso_max_lifespan_remember_me", "sso_idle_timeout_remember_me", "default_role") VALUES
('5f22ffdd-e26c-45a8-80c1-382f149facab', 60, 300, 300, NULL, NULL, NULL, 't', 'f', 0, NULL, 'avenirs', 0, NULL, 'f', 'f', 'f', 'f', 'EXTERNAL', 1800, 36000, 'f', 'f', '79a549bf-f6e4-4f81-8aeb-fcf867b823d1', 1800, 'f', NULL, 'f', 'f', 'f', 'f', 0, 1, 30, 6, 'HmacSHA1', 'totp', '811fe0f4-f25d-4ef3-a294-9f944cfb0398', 'e1554650-f0de-437a-b820-ad569fb07513', '8f960c80-811b-4ad2-b3cf-b28ecba73619', 'a1b857d5-9e63-4fec-9875-3b9fe4678176', '761dcf0d-bd44-405e-8e2e-744ce7f7c5e1', 2592000, 'f', 900, 't', 'f', 'b18a61a0-d71b-4a8b-9496-6544e0b29caa', 0, 'f', 0, 0, '3e45aa61-4df0-4eab-b2b4-498eea4a2999'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', 60, 300, 60, NULL, NULL, NULL, 't', 'f', 0, NULL, 'master', 0, NULL, 'f', 'f', 'f', 'f', 'EXTERNAL', 1800, 36000, 'f', 'f', 'bf1482b1-f598-4da3-982e-146e190880e1', 1800, 'f', NULL, 'f', 'f', 'f', 'f', 0, 1, 30, 6, 'HmacSHA1', 'totp', 'e2e271a6-ca14-4572-b3c7-c48dedc6fb08', '1c3df4bc-935c-4c14-8e45-e0a6d682dcda', '4cf88c06-b446-49e9-b478-18ae676aec57', '2ab1bbad-7868-43e9-818e-e3e13e30ac78', 'd061b399-f617-4e31-b021-71151b9cc7e8', 2592000, 'f', 900, 't', 'f', 'f3583716-db08-48ea-b8a6-ed6dd10f9461', 0, 'f', 0, 0, 'e497a841-53af-4eb6-863d-5623aa30a330');

INSERT INTO "public"."realm_attribute" ("name", "realm_id", "value") VALUES
('actionTokenGeneratedByAdminLifespan', '5f22ffdd-e26c-45a8-80c1-382f149facab', '43200'),
('actionTokenGeneratedByUserLifespan', '5f22ffdd-e26c-45a8-80c1-382f149facab', '300'),
('_browser_header.contentSecurityPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'frame-src ''self''; frame-ancestors ''self''; object-src ''none'';'),
('_browser_header.contentSecurityPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'frame-src ''self''; frame-ancestors ''self''; object-src ''none'';'),
('_browser_header.contentSecurityPolicyReportOnly', '5f22ffdd-e26c-45a8-80c1-382f149facab', ''),
('_browser_header.contentSecurityPolicyReportOnly', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', ''),
('_browser_header.referrerPolicy', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'no-referrer'),
('_browser_header.referrerPolicy', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'no-referrer'),
('_browser_header.strictTransportSecurity', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'max-age=31536000; includeSubDomains'),
('_browser_header.strictTransportSecurity', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'max-age=31536000; includeSubDomains'),
('_browser_header.xContentTypeOptions', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'nosniff'),
('_browser_header.xContentTypeOptions', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'nosniff'),
('_browser_header.xFrameOptions', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'SAMEORIGIN'),
('_browser_header.xFrameOptions', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'SAMEORIGIN'),
('_browser_header.xRobotsTag', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'none'),
('_browser_header.xRobotsTag', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'none'),
('_browser_header.xXSSProtection', '5f22ffdd-e26c-45a8-80c1-382f149facab', '1; mode=block'),
('_browser_header.xXSSProtection', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1; mode=block'),
('bruteForceProtected', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'false'),
('bruteForceProtected', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'false'),
('cibaAuthRequestedUserHint', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'login_hint'),
('cibaBackchannelTokenDeliveryMode', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'poll'),
('cibaExpiresIn', '5f22ffdd-e26c-45a8-80c1-382f149facab', '120'),
('cibaInterval', '5f22ffdd-e26c-45a8-80c1-382f149facab', '5'),
('defaultSignatureAlgorithm', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'RS256'),
('defaultSignatureAlgorithm', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'RS256'),
('displayName', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'Keycloak'),
('displayNameHtml', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '<div class="kc-logo-text"><span>Keycloak</span></div>'),
('failureFactor', '5f22ffdd-e26c-45a8-80c1-382f149facab', '30'),
('failureFactor', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '30'),
('firstBrokerLoginFlowId', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'cbed1e10-0430-4e63-9298-b9ce56f96198'),
('firstBrokerLoginFlowId', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '24d0b0fc-1390-4645-8051-60cb448acf93'),
('maxDeltaTimeSeconds', '5f22ffdd-e26c-45a8-80c1-382f149facab', '43200'),
('maxDeltaTimeSeconds', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '43200'),
('maxFailureWaitSeconds', '5f22ffdd-e26c-45a8-80c1-382f149facab', '900'),
('maxFailureWaitSeconds', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '900'),
('maxTemporaryLockouts', '5f22ffdd-e26c-45a8-80c1-382f149facab', '0'),
('maxTemporaryLockouts', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '0'),
('minimumQuickLoginWaitSeconds', '5f22ffdd-e26c-45a8-80c1-382f149facab', '60'),
('minimumQuickLoginWaitSeconds', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '60'),
('oauth2DeviceCodeLifespan', '5f22ffdd-e26c-45a8-80c1-382f149facab', '600'),
('oauth2DevicePollingInterval', '5f22ffdd-e26c-45a8-80c1-382f149facab', '5'),
('offlineSessionMaxLifespan', '5f22ffdd-e26c-45a8-80c1-382f149facab', '5184000'),
('offlineSessionMaxLifespan', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '5184000'),
('offlineSessionMaxLifespanEnabled', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'false'),
('offlineSessionMaxLifespanEnabled', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'false'),
('parRequestUriLifespan', '5f22ffdd-e26c-45a8-80c1-382f149facab', '60'),
('permanentLockout', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'false'),
('permanentLockout', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'false'),
('quickLoginCheckMilliSeconds', '5f22ffdd-e26c-45a8-80c1-382f149facab', '1000'),
('quickLoginCheckMilliSeconds', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '1000'),
('realmReusableOtpCode', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'false'),
('realmReusableOtpCode', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'false'),
('waitIncrementSeconds', '5f22ffdd-e26c-45a8-80c1-382f149facab', '60'),
('waitIncrementSeconds', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', '60'),
('webAuthnPolicyAttestationConveyancePreference', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyAttestationConveyancePreferencePasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyAuthenticatorAttachment', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyAuthenticatorAttachmentPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyAvoidSameAuthenticatorRegister', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'false'),
('webAuthnPolicyAvoidSameAuthenticatorRegisterPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'false'),
('webAuthnPolicyCreateTimeout', '5f22ffdd-e26c-45a8-80c1-382f149facab', '0'),
('webAuthnPolicyCreateTimeoutPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', '0'),
('webAuthnPolicyRequireResidentKey', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyRequireResidentKeyPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyRpEntityName', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'keycloak'),
('webAuthnPolicyRpEntityNamePasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'keycloak'),
('webAuthnPolicyRpId', '5f22ffdd-e26c-45a8-80c1-382f149facab', ''),
('webAuthnPolicyRpIdPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', ''),
('webAuthnPolicySignatureAlgorithms', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'ES256'),
('webAuthnPolicySignatureAlgorithmsPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'ES256'),
('webAuthnPolicyUserVerificationRequirement', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified'),
('webAuthnPolicyUserVerificationRequirementPasswordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'not specified');

INSERT INTO "public"."realm_events_listeners" ("realm_id", "value") VALUES
('5f22ffdd-e26c-45a8-80c1-382f149facab', 'jboss-logging'),
('b8300271-af6f-4da4-9541-bc4ee4f0779f', 'jboss-logging');

INSERT INTO "public"."realm_required_credential" ("type", "form_label", "input", "secret", "realm_id") VALUES
('password', 'password', 't', 't', '5f22ffdd-e26c-45a8-80c1-382f149facab'),
('password', 'password', 't', 't', 'b8300271-af6f-4da4-9541-bc4ee4f0779f');

INSERT INTO "public"."redirect_uris" ("client_id", "value") VALUES
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '/admin/master/console/*'),
('1acd0f60-85ea-485a-933a-16a294f49529', '/realms/master/account/*'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '/admin/avenirs/console/*'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '/realms/master/account/*'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '/realms/avenirs/account/*'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'http://localhost:5001'),
('c7207b89-d86c-425d-a252-510cb2bc7e39', '/realms/avenirs/account/*');

INSERT INTO "public"."required_action_provider" ("id", "alias", "name", "realm_id", "enabled", "default_action", "provider_id", "priority") VALUES
('053b9a47-3ec7-4e54-aa4d-f513c5a548cd', 'TERMS_AND_CONDITIONS', 'Terms and Conditions', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'f', 'f', 'TERMS_AND_CONDITIONS', 20),
('0fd8d58e-ae25-4694-8c5c-7d0b8081dcfe', 'VERIFY_PROFILE', 'Verify Profile', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'VERIFY_PROFILE', 90),
('10610ebc-14c2-4d7f-8049-d093c0dc5090', 'webauthn-register', 'Webauthn Register', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'webauthn-register', 70),
('12ab28ba-c717-4a4f-afc6-abc1eb6d9b4f', 'UPDATE_PASSWORD', 'Update Password', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'UPDATE_PASSWORD', 30),
('1502353a-3118-4594-93b1-c4be4293b48b', 'UPDATE_PROFILE', 'Update Profile', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'UPDATE_PROFILE', 40),
('15876a66-5268-433d-b1f1-8ab77de0b76c', 'CONFIGURE_TOTP', 'Configure OTP', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'CONFIGURE_TOTP', 10),
('48033b3b-d41e-41ec-af21-90fd973c8408', 'UPDATE_PROFILE', 'Update Profile', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'UPDATE_PROFILE', 40),
('4b1ec097-f1ac-4eb6-bece-3e6fef5583b6', 'VERIFY_EMAIL', 'Verify Email', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'VERIFY_EMAIL', 50),
('5adb8037-c545-4b4c-b56e-6ed144e987a0', 'VERIFY_EMAIL', 'Verify Email', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'VERIFY_EMAIL', 50),
('6dd593e1-a122-4b8d-89d8-82ddac89dead', 'update_user_locale', 'Update User Locale', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'update_user_locale', 1000),
('80cab232-fd47-42f2-acf3-0434796de327', 'delete_account', 'Delete Account', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', 'f', 'delete_account', 60),
('8be16986-c32b-414a-83de-004521bfac5f', 'update_user_locale', 'Update User Locale', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'update_user_locale', 1000),
('8cc8ad5b-a94a-42c1-b5d5-5c870a73b6b2', 'CONFIGURE_TOTP', 'Configure OTP', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'CONFIGURE_TOTP', 10),
('8cf92873-6ff7-41b7-a229-2c586c613d23', 'webauthn-register', 'Webauthn Register', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'webauthn-register', 70),
('90e5bfb5-180d-4413-8d7d-dffb14752e65', 'webauthn-register-passwordless', 'Webauthn Register Passwordless', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 't', 'f', 'webauthn-register-passwordless', 80),
('93128b19-2ea2-49ab-8825-f4385e715fd4', 'VERIFY_PROFILE', 'Verify Profile', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'VERIFY_PROFILE', 90),
('95458ab7-9ab0-4805-9e38-1bb43776b511', 'webauthn-register-passwordless', 'Webauthn Register Passwordless', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'webauthn-register-passwordless', 80),
('967b052b-d202-4edf-8f5c-d23cfeaef10f', 'TERMS_AND_CONDITIONS', 'Terms and Conditions', 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'f', 'f', 'TERMS_AND_CONDITIONS', 20),
('bfc1605d-8d98-4545-b364-962a5c369705', 'delete_account', 'Delete Account', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'f', 'f', 'delete_account', 60),
('c7a53dad-c5d5-40fc-b2d4-f2cf329bcc8c', 'UPDATE_PASSWORD', 'Update Password', '5f22ffdd-e26c-45a8-80c1-382f149facab', 't', 'f', 'UPDATE_PASSWORD', 30);

INSERT INTO "public"."scope_mapping" ("client_id", "role_id") VALUES
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', '8966b737-6fe9-47ed-ae19-05f7c19eed1f'),
('4d6bdae3-98a6-4ebf-a902-7d6ef0fcab96', 'ca61f9e8-b6d4-4f27-bf46-5803219408ca'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', '251031da-6462-4f04-8bf6-d2a8ca146ad9'),
('560456cc-571b-4bf0-b1f4-d40e9dbbf9bc', 'fd324272-4528-46f6-b03c-81e0bfad1623');

INSERT INTO "public"."user_attribute" ("name", "value", "user_id", "id", "long_value_hash", "long_value_hash_lower_case", "long_value") VALUES
('profile', 'APP-SEC', '44b414b4-223e-4782-ab2b-1819161405e7', '2b7afb02-cfb7-4037-9f5b-229983463221', NULL, NULL, NULL),
('profile', 'EDU-SEC', '32a56da2-08b8-4f5d-9b24-02727ceebde0', 'ddc36d47-0346-4279-879e-326d34cfc3b8', NULL, NULL, NULL);

INSERT INTO "public"."user_entity" ("id", "email", "email_constraint", "email_verified", "enabled", "federation_link", "first_name", "last_name", "realm_id", "username", "created_timestamp", "service_account_client_link", "not_before") VALUES
('32a56da2-08b8-4f5d-9b24-02727ceebde0', 'enseignant@example.com', 'enseignant@example.com', 't', 't', NULL, 'Marie', 'Durant', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'enseignant', 1721292207899, NULL, 0),
('32fa1821-1eb3-4df9-98a5-24f307bec772', NULL, '1df83997-efb9-4860-9ffe-905b808b3f1a', 'f', 't', NULL, NULL, NULL, 'b8300271-af6f-4da4-9541-bc4ee4f0779f', 'admin', 1721292063226, NULL, 0),
('44b414b4-223e-4782-ab2b-1819161405e7', 'eleve@example.com', 'eleve@example.com', 't', 't', NULL, 'Kévin', 'Dupont', '5f22ffdd-e26c-45a8-80c1-382f149facab', 'eleve', 1721292169623, NULL, 0);

INSERT INTO "public"."user_role_mapping" ("role_id", "user_id") VALUES
('0b879c2f-ded7-45df-bf59-adcecef77cd0', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '32a56da2-08b8-4f5d-9b24-02727ceebde0'),
('3e45aa61-4df0-4eab-b2b4-498eea4a2999', '44b414b4-223e-4782-ab2b-1819161405e7'),
('4741d45f-6128-41d3-abf0-534d847074b2', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('4c176de3-3d7c-40f3-92b5-a1ccf21610fa', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('4d316a8f-f9ca-4c26-a78a-4d0ff5a2548e', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('4f309be6-e6ca-4040-9753-eef7f37b4798', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('5be6a567-f4c6-4d39-935b-ac692f759f8e', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('5e577bdf-7d1a-45b9-a281-9b511ccf7f6c', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('646e98b8-c472-4740-b4a1-3143bc80f64a', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('77fffdcc-7d20-4a03-ba9d-44587e8fbbad', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('78c444f5-22ad-4563-afe2-a9bdc641ef85', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('8b8eac30-7aec-45ca-896e-4920dd355854', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('97a8daf9-bb0f-4762-8b47-20662bbcb5a8', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('9e36f61e-7e38-4110-bf09-3d99253617b0', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('a407e863-8cc3-4234-92dd-5733c7d13501', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('a6509e2f-e380-432f-b249-8cd8862f69fb', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('a8922e31-e6cf-4d72-b741-c0bace915424', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('e4465ad3-d92c-4895-86e2-6d9d59a6d39a', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('e497a841-53af-4eb6-863d-5623aa30a330', '32fa1821-1eb3-4df9-98a5-24f307bec772'),
('e545fc06-5f3b-4949-9282-124f94206594', '32fa1821-1eb3-4df9-98a5-24f307bec772');

INSERT INTO "public"."web_origins" ("client_id", "value") VALUES
('0cbca4c2-c802-473b-b7d2-15875ed6a364', '+'),
('3d1829e6-92da-4617-bb14-77c94e568f64', '+'),
('99b97c1a-5f00-4304-9a17-9f8c9fa3974d', 'http://localhost:5001');



-- Indices Index
CREATE UNIQUE INDEX constraint_admin_event_entity ON public.admin_event_entity USING btree (id);
CREATE INDEX idx_admin_event_time ON public.admin_event_entity USING btree (realm_id, admin_event_time);
ALTER TABLE "public"."associated_policy" ADD FOREIGN KEY ("associated_policy_id") REFERENCES "public"."resource_server_policy"("id");
ALTER TABLE "public"."associated_policy" ADD FOREIGN KEY ("policy_id") REFERENCES "public"."resource_server_policy"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsrpap ON public.associated_policy USING btree (policy_id, associated_policy_id);
CREATE INDEX idx_assoc_pol_assoc_pol_id ON public.associated_policy USING btree (associated_policy_id);
ALTER TABLE "public"."authentication_execution" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");
ALTER TABLE "public"."authentication_execution" ADD FOREIGN KEY ("flow_id") REFERENCES "public"."authentication_flow"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_auth_exec_pk ON public.authentication_execution USING btree (id);
CREATE INDEX idx_auth_exec_realm_flow ON public.authentication_execution USING btree (realm_id, flow_id);
CREATE INDEX idx_auth_exec_flow ON public.authentication_execution USING btree (flow_id);
ALTER TABLE "public"."authentication_flow" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_auth_flow_pk ON public.authentication_flow USING btree (id);
CREATE INDEX idx_auth_flow_realm ON public.authentication_flow USING btree (realm_id);
ALTER TABLE "public"."authenticator_config" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_auth_pk ON public.authenticator_config USING btree (id);
CREATE INDEX idx_auth_config_realm ON public.authenticator_config USING btree (realm_id);


-- Indices Index
CREATE UNIQUE INDEX constraint_auth_cfg_pk ON public.authenticator_config_entry USING btree (authenticator_id, name);


-- Indices Index
CREATE UNIQUE INDEX constr_broker_link_pk ON public.broker_link USING btree (identity_provider, user_id);


-- Indices Index
CREATE UNIQUE INDEX constraint_7 ON public.client USING btree (id);
CREATE UNIQUE INDEX uk_b71cjlbenv945rb6gcon438at ON public.client USING btree (realm_id, client_id);
CREATE INDEX idx_client_id ON public.client USING btree (client_id);
ALTER TABLE "public"."client_attributes" ADD FOREIGN KEY ("client_id") REFERENCES "public"."client"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_3c ON public.client_attributes USING btree (client_id, name);
CREATE INDEX idx_client_att_by_name_value ON public.client_attributes USING btree (name, substr(value, 1, 255));


-- Indices Index
CREATE UNIQUE INDEX c_cli_flow_bind ON public.client_auth_flow_bindings USING btree (client_id, binding_name);
ALTER TABLE "public"."client_initial_access" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX cnstr_client_init_acc_pk ON public.client_initial_access USING btree (id);
CREATE INDEX idx_client_init_acc_realm ON public.client_initial_access USING btree (realm_id);
ALTER TABLE "public"."client_node_registrations" ADD FOREIGN KEY ("client_id") REFERENCES "public"."client"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_84 ON public.client_node_registrations USING btree (client_id, name);


-- Indices Index
CREATE UNIQUE INDEX pk_cli_template ON public.client_scope USING btree (id);
CREATE UNIQUE INDEX uk_cli_scope ON public.client_scope USING btree (realm_id, name);
CREATE INDEX idx_realm_clscope ON public.client_scope USING btree (realm_id);
ALTER TABLE "public"."client_scope_attributes" ADD FOREIGN KEY ("scope_id") REFERENCES "public"."client_scope"("id");


-- Indices Index
CREATE UNIQUE INDEX pk_cl_tmpl_attr ON public.client_scope_attributes USING btree (scope_id, name);
CREATE INDEX idx_clscope_attrs ON public.client_scope_attributes USING btree (scope_id);


-- Indices Index
CREATE INDEX idx_clscope_cl ON public.client_scope_client USING btree (client_id);
CREATE UNIQUE INDEX c_cli_scope_bind ON public.client_scope_client USING btree (client_id, scope_id);
CREATE INDEX idx_cl_clscope ON public.client_scope_client USING btree (scope_id);
ALTER TABLE "public"."client_scope_role_mapping" ADD FOREIGN KEY ("scope_id") REFERENCES "public"."client_scope"("id");


-- Indices Index
CREATE UNIQUE INDEX pk_template_scope ON public.client_scope_role_mapping USING btree (scope_id, role_id);
CREATE INDEX idx_clscope_role ON public.client_scope_role_mapping USING btree (scope_id);
CREATE INDEX idx_role_clscope ON public.client_scope_role_mapping USING btree (role_id);
ALTER TABLE "public"."client_session" ADD FOREIGN KEY ("session_id") REFERENCES "public"."user_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_8 ON public.client_session USING btree (id);
CREATE INDEX idx_client_session_session ON public.client_session USING btree (session_id);
ALTER TABLE "public"."client_session_auth_status" ADD FOREIGN KEY ("client_session") REFERENCES "public"."client_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_auth_status_pk ON public.client_session_auth_status USING btree (client_session, authenticator);
ALTER TABLE "public"."client_session_note" ADD FOREIGN KEY ("client_session") REFERENCES "public"."client_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_5e ON public.client_session_note USING btree (client_session, name);
ALTER TABLE "public"."client_session_prot_mapper" ADD FOREIGN KEY ("client_session") REFERENCES "public"."client_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_cs_pmp_pk ON public.client_session_prot_mapper USING btree (client_session, protocol_mapper_id);
ALTER TABLE "public"."client_session_role" ADD FOREIGN KEY ("client_session") REFERENCES "public"."client_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_5 ON public.client_session_role USING btree (client_session, role_id);
ALTER TABLE "public"."client_user_session_note" ADD FOREIGN KEY ("client_session") REFERENCES "public"."client_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constr_cl_usr_ses_note ON public.client_user_session_note USING btree (client_session, name);
ALTER TABLE "public"."component" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constr_component_pk ON public.component USING btree (id);
CREATE INDEX idx_component_realm ON public.component USING btree (realm_id);
CREATE INDEX idx_component_provider_type ON public.component USING btree (provider_type);
ALTER TABLE "public"."component_config" ADD FOREIGN KEY ("component_id") REFERENCES "public"."component"("id");


-- Indices Index
CREATE UNIQUE INDEX constr_component_config_pk ON public.component_config USING btree (id);
CREATE INDEX idx_compo_config_compo ON public.component_config USING btree (component_id);
ALTER TABLE "public"."composite_role" ADD FOREIGN KEY ("child_role") REFERENCES "public"."keycloak_role"("id");
ALTER TABLE "public"."composite_role" ADD FOREIGN KEY ("composite") REFERENCES "public"."keycloak_role"("id");


-- Indices Index
CREATE INDEX idx_composite ON public.composite_role USING btree (composite);
CREATE INDEX idx_composite_child ON public.composite_role USING btree (child_role);
CREATE UNIQUE INDEX constraint_composite_role ON public.composite_role USING btree (composite, child_role);
ALTER TABLE "public"."credential" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_f ON public.credential USING btree (id);
CREATE INDEX idx_user_credential ON public.credential USING btree (user_id);
ALTER TABLE "public"."default_client_scope" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX r_def_cli_scope_bind ON public.default_client_scope USING btree (realm_id, scope_id);
CREATE INDEX idx_defcls_realm ON public.default_client_scope USING btree (realm_id);
CREATE INDEX idx_defcls_scope ON public.default_client_scope USING btree (scope_id);


-- Indices Index
CREATE UNIQUE INDEX constraint_4 ON public.event_entity USING btree (id);
CREATE INDEX idx_event_time ON public.event_entity USING btree (realm_id, event_time);


-- Indices Index
CREATE UNIQUE INDEX constr_fed_user_attr_pk ON public.fed_user_attribute USING btree (id);
CREATE INDEX idx_fu_attribute ON public.fed_user_attribute USING btree (user_id, realm_id, name);
CREATE INDEX fed_user_attr_long_values ON public.fed_user_attribute USING btree (long_value_hash, name);
CREATE INDEX fed_user_attr_long_values_lower_case ON public.fed_user_attribute USING btree (long_value_hash_lower_case, name);


-- Indices Index
CREATE UNIQUE INDEX constr_fed_user_consent_pk ON public.fed_user_consent USING btree (id);
CREATE INDEX idx_fu_consent_ru ON public.fed_user_consent USING btree (realm_id, user_id);
CREATE INDEX idx_fu_cnsnt_ext ON public.fed_user_consent USING btree (user_id, client_storage_provider, external_client_id);
CREATE INDEX idx_fu_consent ON public.fed_user_consent USING btree (user_id, client_id);


-- Indices Index
CREATE UNIQUE INDEX constraint_fgrntcsnt_clsc_pm ON public.fed_user_consent_cl_scope USING btree (user_consent_id, scope_id);


-- Indices Index
CREATE UNIQUE INDEX constr_fed_user_cred_pk ON public.fed_user_credential USING btree (id);
CREATE INDEX idx_fu_credential ON public.fed_user_credential USING btree (user_id, type);
CREATE INDEX idx_fu_credential_ru ON public.fed_user_credential USING btree (realm_id, user_id);


-- Indices Index
CREATE UNIQUE INDEX constr_fed_user_group ON public.fed_user_group_membership USING btree (group_id, user_id);
CREATE INDEX idx_fu_group_membership ON public.fed_user_group_membership USING btree (user_id, group_id);
CREATE INDEX idx_fu_group_membership_ru ON public.fed_user_group_membership USING btree (realm_id, user_id);


-- Indices Index
CREATE UNIQUE INDEX constr_fed_required_action ON public.fed_user_required_action USING btree (required_action, user_id);
CREATE INDEX idx_fu_required_action ON public.fed_user_required_action USING btree (user_id, required_action);
CREATE INDEX idx_fu_required_action_ru ON public.fed_user_required_action USING btree (realm_id, user_id);


-- Indices Index
CREATE UNIQUE INDEX constr_fed_user_role ON public.fed_user_role_mapping USING btree (role_id, user_id);
CREATE INDEX idx_fu_role_mapping ON public.fed_user_role_mapping USING btree (user_id, role_id);
CREATE INDEX idx_fu_role_mapping_ru ON public.fed_user_role_mapping USING btree (realm_id, user_id);
ALTER TABLE "public"."federated_identity" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_40 ON public.federated_identity USING btree (identity_provider, user_id);
CREATE INDEX idx_fedidentity_user ON public.federated_identity USING btree (user_id);
CREATE INDEX idx_fedidentity_feduser ON public.federated_identity USING btree (federated_user_id);


-- Indices Index
CREATE UNIQUE INDEX constr_federated_user ON public.federated_user USING btree (id);
ALTER TABLE "public"."group_attribute" ADD FOREIGN KEY ("group_id") REFERENCES "public"."keycloak_group"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_group_attribute_pk ON public.group_attribute USING btree (id);
CREATE INDEX idx_group_attr_group ON public.group_attribute USING btree (group_id);
CREATE INDEX idx_group_att_by_name_value ON public.group_attribute USING btree (name, ((value)::character varying(250)));
ALTER TABLE "public"."group_role_mapping" ADD FOREIGN KEY ("group_id") REFERENCES "public"."keycloak_group"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_group_role ON public.group_role_mapping USING btree (role_id, group_id);
CREATE INDEX idx_group_role_mapp_group ON public.group_role_mapping USING btree (group_id);
ALTER TABLE "public"."identity_provider" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_2b ON public.identity_provider USING btree (internal_id);
CREATE UNIQUE INDEX uk_2daelwnibji49avxsrtuf6xj33 ON public.identity_provider USING btree (provider_alias, realm_id);
CREATE INDEX idx_ident_prov_realm ON public.identity_provider USING btree (realm_id);
ALTER TABLE "public"."identity_provider_config" ADD FOREIGN KEY ("identity_provider_id") REFERENCES "public"."identity_provider"("internal_id");


-- Indices Index
CREATE UNIQUE INDEX constraint_d ON public.identity_provider_config USING btree (identity_provider_id, name);
ALTER TABLE "public"."identity_provider_mapper" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_idpm ON public.identity_provider_mapper USING btree (id);
CREATE INDEX idx_id_prov_mapp_realm ON public.identity_provider_mapper USING btree (realm_id);
ALTER TABLE "public"."idp_mapper_config" ADD FOREIGN KEY ("idp_mapper_id") REFERENCES "public"."identity_provider_mapper"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_idpmconfig ON public.idp_mapper_config USING btree (idp_mapper_id, name);


-- Indices Index
CREATE UNIQUE INDEX constraint_group ON public.keycloak_group USING btree (id);
CREATE UNIQUE INDEX sibling_names ON public.keycloak_group USING btree (realm_id, parent_group, name);
ALTER TABLE "public"."keycloak_role" ADD FOREIGN KEY ("realm") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_a ON public.keycloak_role USING btree (id);
CREATE INDEX idx_keycloak_role_client ON public.keycloak_role USING btree (client);
CREATE INDEX idx_keycloak_role_realm ON public.keycloak_role USING btree (realm);
CREATE UNIQUE INDEX "UK_J3RWUVD56ONTGSUHOGM184WW2-2" ON public.keycloak_role USING btree (name, client_realm_constraint);


-- Indices Index
CREATE UNIQUE INDEX constraint_migmod ON public.migration_model USING btree (id);
CREATE INDEX idx_update_time ON public.migration_model USING btree (update_time);


-- Indices Index
CREATE INDEX idx_us_sess_id_on_cl_sess ON public.offline_client_session USING btree (user_session_id);
CREATE UNIQUE INDEX constraint_offl_cl_ses_pk3 ON public.offline_client_session USING btree (user_session_id, client_id, client_storage_provider, external_client_id, offline_flag);
CREATE INDEX idx_offline_css_preload ON public.offline_client_session USING btree (client_id, offline_flag);


-- Indices Index
CREATE UNIQUE INDEX constraint_offl_us_ses_pk2 ON public.offline_user_session USING btree (user_session_id, offline_flag);
CREATE INDEX idx_offline_uss_createdon ON public.offline_user_session USING btree (created_on);
CREATE INDEX idx_offline_uss_preload ON public.offline_user_session USING btree (offline_flag, created_on, user_session_id);
CREATE INDEX idx_offline_uss_by_user ON public.offline_user_session USING btree (user_id, realm_id, offline_flag);
CREATE INDEX idx_offline_uss_by_usersess ON public.offline_user_session USING btree (realm_id, offline_flag, user_session_id);
ALTER TABLE "public"."policy_config" ADD FOREIGN KEY ("policy_id") REFERENCES "public"."resource_server_policy"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_dpc ON public.policy_config USING btree (policy_id, name);
ALTER TABLE "public"."protocol_mapper" ADD FOREIGN KEY ("client_id") REFERENCES "public"."client"("id");
ALTER TABLE "public"."protocol_mapper" ADD FOREIGN KEY ("client_scope_id") REFERENCES "public"."client_scope"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_pcm ON public.protocol_mapper USING btree (id);
CREATE INDEX idx_protocol_mapper_client ON public.protocol_mapper USING btree (client_id);
CREATE INDEX idx_clscope_protmap ON public.protocol_mapper USING btree (client_scope_id);
ALTER TABLE "public"."protocol_mapper_config" ADD FOREIGN KEY ("protocol_mapper_id") REFERENCES "public"."protocol_mapper"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_pmconfig ON public.protocol_mapper_config USING btree (protocol_mapper_id, name);


-- Indices Index
CREATE UNIQUE INDEX constraint_4a ON public.realm USING btree (id);
CREATE UNIQUE INDEX uk_orvsdmla56612eaefiq6wl5oi ON public.realm USING btree (name);
CREATE INDEX idx_realm_master_adm_cli ON public.realm USING btree (master_admin_client);
ALTER TABLE "public"."realm_attribute" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_9 ON public.realm_attribute USING btree (name, realm_id);
CREATE INDEX idx_realm_attr_realm ON public.realm_attribute USING btree (realm_id);
ALTER TABLE "public"."realm_default_groups" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX con_group_id_def_groups ON public.realm_default_groups USING btree (group_id);
CREATE INDEX idx_realm_def_grp_realm ON public.realm_default_groups USING btree (realm_id);
CREATE UNIQUE INDEX constr_realm_default_groups ON public.realm_default_groups USING btree (realm_id, group_id);
ALTER TABLE "public"."realm_enabled_event_types" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE INDEX idx_realm_evt_types_realm ON public.realm_enabled_event_types USING btree (realm_id);
CREATE UNIQUE INDEX constr_realm_enabl_event_types ON public.realm_enabled_event_types USING btree (realm_id, value);
ALTER TABLE "public"."realm_events_listeners" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE INDEX idx_realm_evt_list_realm ON public.realm_events_listeners USING btree (realm_id);
CREATE UNIQUE INDEX constr_realm_events_listeners ON public.realm_events_listeners USING btree (realm_id, value);
ALTER TABLE "public"."realm_required_credential" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_92 ON public.realm_required_credential USING btree (realm_id, type);
ALTER TABLE "public"."realm_smtp_config" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_e ON public.realm_smtp_config USING btree (realm_id, name);
ALTER TABLE "public"."realm_supported_locales" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE INDEX idx_realm_supp_local_realm ON public.realm_supported_locales USING btree (realm_id);
CREATE UNIQUE INDEX constr_realm_supported_locales ON public.realm_supported_locales USING btree (realm_id, value);
ALTER TABLE "public"."redirect_uris" ADD FOREIGN KEY ("client_id") REFERENCES "public"."client"("id");


-- Indices Index
CREATE INDEX idx_redir_uri_client ON public.redirect_uris USING btree (client_id);
CREATE UNIQUE INDEX constraint_redirect_uris ON public.redirect_uris USING btree (client_id, value);


-- Indices Index
CREATE UNIQUE INDEX constraint_req_act_cfg_pk ON public.required_action_config USING btree (required_action_id, name);
ALTER TABLE "public"."required_action_provider" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_req_act_prv_pk ON public.required_action_provider USING btree (id);
CREATE INDEX idx_req_act_prov_realm ON public.required_action_provider USING btree (realm_id);
ALTER TABLE "public"."resource_attribute" ADD FOREIGN KEY ("resource_id") REFERENCES "public"."resource_server_resource"("id");


-- Indices Index
CREATE UNIQUE INDEX res_attr_pk ON public.resource_attribute USING btree (id);
ALTER TABLE "public"."resource_policy" ADD FOREIGN KEY ("policy_id") REFERENCES "public"."resource_server_policy"("id");
ALTER TABLE "public"."resource_policy" ADD FOREIGN KEY ("resource_id") REFERENCES "public"."resource_server_resource"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsrpp ON public.resource_policy USING btree (resource_id, policy_id);
CREATE INDEX idx_res_policy_policy ON public.resource_policy USING btree (policy_id);
ALTER TABLE "public"."resource_scope" ADD FOREIGN KEY ("resource_id") REFERENCES "public"."resource_server_resource"("id");
ALTER TABLE "public"."resource_scope" ADD FOREIGN KEY ("scope_id") REFERENCES "public"."resource_server_scope"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsrsp ON public.resource_scope USING btree (resource_id, scope_id);
CREATE INDEX idx_res_scope_scope ON public.resource_scope USING btree (scope_id);


-- Indices Index
CREATE UNIQUE INDEX pk_resource_server ON public.resource_server USING btree (id);
ALTER TABLE "public"."resource_server_perm_ticket" ADD FOREIGN KEY ("resource_server_id") REFERENCES "public"."resource_server"("id");
ALTER TABLE "public"."resource_server_perm_ticket" ADD FOREIGN KEY ("scope_id") REFERENCES "public"."resource_server_scope"("id");
ALTER TABLE "public"."resource_server_perm_ticket" ADD FOREIGN KEY ("resource_id") REFERENCES "public"."resource_server_resource"("id");
ALTER TABLE "public"."resource_server_perm_ticket" ADD FOREIGN KEY ("policy_id") REFERENCES "public"."resource_server_policy"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_fapmt ON public.resource_server_perm_ticket USING btree (id);
CREATE UNIQUE INDEX uk_frsr6t700s9v50bu18ws5pmt ON public.resource_server_perm_ticket USING btree (owner, requester, resource_server_id, resource_id, scope_id);
ALTER TABLE "public"."resource_server_policy" ADD FOREIGN KEY ("resource_server_id") REFERENCES "public"."resource_server"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsrp ON public.resource_server_policy USING btree (id);
CREATE UNIQUE INDEX uk_frsrpt700s9v50bu18ws5ha6 ON public.resource_server_policy USING btree (name, resource_server_id);
CREATE INDEX idx_res_serv_pol_res_serv ON public.resource_server_policy USING btree (resource_server_id);
ALTER TABLE "public"."resource_server_resource" ADD FOREIGN KEY ("resource_server_id") REFERENCES "public"."resource_server"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsr ON public.resource_server_resource USING btree (id);
CREATE INDEX idx_res_srv_res_res_srv ON public.resource_server_resource USING btree (resource_server_id);
CREATE UNIQUE INDEX uk_frsr6t700s9v50bu18ws5ha6 ON public.resource_server_resource USING btree (name, owner, resource_server_id);
ALTER TABLE "public"."resource_server_scope" ADD FOREIGN KEY ("resource_server_id") REFERENCES "public"."resource_server"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsrs ON public.resource_server_scope USING btree (id);
CREATE UNIQUE INDEX uk_frsrst700s9v50bu18ws5ha6 ON public.resource_server_scope USING btree (name, resource_server_id);
CREATE INDEX idx_res_srv_scope_res_srv ON public.resource_server_scope USING btree (resource_server_id);
ALTER TABLE "public"."resource_uris" ADD FOREIGN KEY ("resource_id") REFERENCES "public"."resource_server_resource"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_resour_uris_pk ON public.resource_uris USING btree (resource_id, value);
ALTER TABLE "public"."role_attribute" ADD FOREIGN KEY ("role_id") REFERENCES "public"."keycloak_role"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_role_attribute_pk ON public.role_attribute USING btree (id);
CREATE INDEX idx_role_attribute ON public.role_attribute USING btree (role_id);
ALTER TABLE "public"."scope_mapping" ADD FOREIGN KEY ("client_id") REFERENCES "public"."client"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_81 ON public.scope_mapping USING btree (client_id, role_id);
CREATE INDEX idx_scope_mapping_role ON public.scope_mapping USING btree (role_id);
ALTER TABLE "public"."scope_policy" ADD FOREIGN KEY ("scope_id") REFERENCES "public"."resource_server_scope"("id");
ALTER TABLE "public"."scope_policy" ADD FOREIGN KEY ("policy_id") REFERENCES "public"."resource_server_policy"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_farsrsps ON public.scope_policy USING btree (scope_id, policy_id);
CREATE INDEX idx_scope_policy_policy ON public.scope_policy USING btree (policy_id);
ALTER TABLE "public"."user_attribute" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_user_attribute_pk ON public.user_attribute USING btree (id);
CREATE INDEX idx_user_attribute ON public.user_attribute USING btree (user_id);
CREATE INDEX idx_user_attribute_name ON public.user_attribute USING btree (name, value);
CREATE INDEX user_attr_long_values ON public.user_attribute USING btree (long_value_hash, name);
CREATE INDEX user_attr_long_values_lower_case ON public.user_attribute USING btree (long_value_hash_lower_case, name);
ALTER TABLE "public"."user_consent" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_grntcsnt_pm ON public.user_consent USING btree (id);
CREATE INDEX idx_user_consent ON public.user_consent USING btree (user_id);
CREATE UNIQUE INDEX uk_jkuwuvd56ontgsuhogm8uewrt ON public.user_consent USING btree (client_id, client_storage_provider, external_client_id, user_id);
ALTER TABLE "public"."user_consent_client_scope" ADD FOREIGN KEY ("user_consent_id") REFERENCES "public"."user_consent"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_grntcsnt_clsc_pm ON public.user_consent_client_scope USING btree (user_consent_id, scope_id);
CREATE INDEX idx_usconsent_clscope ON public.user_consent_client_scope USING btree (user_consent_id);


-- Indices Index
CREATE UNIQUE INDEX constraint_fb ON public.user_entity USING btree (id);
CREATE UNIQUE INDEX uk_dykn684sl8up1crfei6eckhd7 ON public.user_entity USING btree (realm_id, email_constraint);
CREATE INDEX idx_user_email ON public.user_entity USING btree (email);
CREATE UNIQUE INDEX uk_ru8tt6t700s9v50bu18ws5ha6 ON public.user_entity USING btree (realm_id, username);
CREATE INDEX idx_user_service_account ON public.user_entity USING btree (realm_id, service_account_client_link);
ALTER TABLE "public"."user_federation_config" ADD FOREIGN KEY ("user_federation_provider_id") REFERENCES "public"."user_federation_provider"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_f9 ON public.user_federation_config USING btree (user_federation_provider_id, name);
ALTER TABLE "public"."user_federation_mapper" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");
ALTER TABLE "public"."user_federation_mapper" ADD FOREIGN KEY ("federation_provider_id") REFERENCES "public"."user_federation_provider"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_fedmapperpm ON public.user_federation_mapper USING btree (id);
CREATE INDEX idx_usr_fed_map_fed_prv ON public.user_federation_mapper USING btree (federation_provider_id);
CREATE INDEX idx_usr_fed_map_realm ON public.user_federation_mapper USING btree (realm_id);
ALTER TABLE "public"."user_federation_mapper_config" ADD FOREIGN KEY ("user_federation_mapper_id") REFERENCES "public"."user_federation_mapper"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_fedmapper_cfg_pm ON public.user_federation_mapper_config USING btree (user_federation_mapper_id, name);
ALTER TABLE "public"."user_federation_provider" ADD FOREIGN KEY ("realm_id") REFERENCES "public"."realm"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_5c ON public.user_federation_provider USING btree (id);
CREATE INDEX idx_usr_fed_prv_realm ON public.user_federation_provider USING btree (realm_id);
ALTER TABLE "public"."user_group_membership" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_user_group ON public.user_group_membership USING btree (group_id, user_id);
CREATE INDEX idx_user_group_mapping ON public.user_group_membership USING btree (user_id);
ALTER TABLE "public"."user_required_action" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_required_action ON public.user_required_action USING btree (required_action, user_id);
CREATE INDEX idx_user_reqactions ON public.user_required_action USING btree (user_id);
ALTER TABLE "public"."user_role_mapping" ADD FOREIGN KEY ("user_id") REFERENCES "public"."user_entity"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_c ON public.user_role_mapping USING btree (role_id, user_id);
CREATE INDEX idx_user_role_mapping ON public.user_role_mapping USING btree (user_id);


-- Indices Index
CREATE UNIQUE INDEX constraint_57 ON public.user_session USING btree (id);
ALTER TABLE "public"."user_session_note" ADD FOREIGN KEY ("user_session") REFERENCES "public"."user_session"("id");


-- Indices Index
CREATE UNIQUE INDEX constraint_usn_pk ON public.user_session_note USING btree (user_session, name);


-- Indices Index
CREATE UNIQUE INDEX "CONSTRAINT_17-2" ON public.username_login_failure USING btree (realm_id, username);
ALTER TABLE "public"."web_origins" ADD FOREIGN KEY ("client_id") REFERENCES "public"."client"("id");


-- Indices Index
CREATE INDEX idx_web_orig_client ON public.web_origins USING btree (client_id);
CREATE UNIQUE INDEX constraint_web_origins ON public.web_origins USING btree (client_id, value);
