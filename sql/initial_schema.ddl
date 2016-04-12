-- CUISINE Table

CREATE TABLE cuisine (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  name VARCHAR(100) NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL
);


-- USERS Table

CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  email VARCHAR(100),
  name VARCHAR(100),
  password VARCHAR(100),
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL
);

CREATE UNIQUE INDEX index_users_on_email ON users USING BTREE (email);


-- RESTAURANT Table

CREATE TABLE restaurant (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    name VARCHAR(100) NOT NULL,
    address VARCHAR(255),
    offers_english_menu BOOLEAN,
    walk_ins_ok BOOLEAN,
    accepts_credit_cards BOOLEAN,
    notes VARCHAR(1000),
    cuisine_id BIGINT REFERENCES cuisine(id) NOT NULL DEFAULT 0,
    created_by_user_id BIGINT REFERENCES users(id) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL
);


-- COMMENT Table

CREATE TABLE comment (
    id BIGSERIAL PRIMARY KEY NOT NULL,
    content VARCHAR(500) NOT NULL,
    restaurant_id BIGINT REFERENCES restaurant(id) NOT NULL,
    created_by_user_id BIGINT REFERENCES users(id) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL
);


-- PHOTO_URL Table

CREATE TABLE photo_url (
  id BIGSERIAL PRIMARY KEY NOT NULL,
  url VARCHAR(500) NOT NULL,
  restaurant_id BIGINT,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL
);


-- SESSION Table

CREATE TABLE session (
  token VARCHAR(255) PRIMARY KEY NOT NULL,
  user_id BIGINT REFERENCES users(id) ON DELETE CASCADE NOT NULL,
  created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL,
  updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT current_timestamp NOT NULL
);

CREATE UNIQUE INDEX index_session_on_token ON session USING BTREE (token);
