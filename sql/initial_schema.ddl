-- CUISINE Table

create table cuisine (
  id BIGSERIAL primary key,
  name varchar(255) not null
);


-- RESTAURANT Table

create table restaurant (
    id BIGSERIAL primary key,
    name varchar(255) not null,
    address varchar(255),
    offers_english_menu boolean,
    walk_ins_ok boolean,
    accepts_credit_cards boolean,
    notes text,
    cuisine_id BIGINT REFERENCES cuisine(id) NOT NULL DEFAULT 0
);


-- PHOTO_URL Table

create table photo_url (
  id BIGSERIAL primary key,
  url varchar(255) not null,
  restaurant_id BIGINT
);


-- USERS Table

CREATE TABLE users (
  id integer NOT NULL,
  email character varying,
  name character varying,
  password character varying,
  created_at timestamp without time zone default current_timestamp NOT NULL,
  updated_at timestamp without time zone default current_timestamp NOT NULL
);

ALTER TABLE users OWNER TO pivotal;

CREATE SEQUENCE users_id_seq
START WITH 1
INCREMENT BY 1
NO MINVALUE
NO MAXVALUE
CACHE 1;

ALTER TABLE users_id_seq OWNER TO pivotal;
ALTER SEQUENCE users_id_seq OWNED BY users.id;
ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);
ALTER TABLE ONLY users ADD CONSTRAINT users_pkey PRIMARY KEY (id);

CREATE UNIQUE INDEX index_users_on_email ON users USING btree (email);


-- SESSION Table

CREATE TABLE session (
  token character varying NOT NULL,
  user_id integer NOT NULL,
  created_at timestamp without time zone default current_timestamp NOT NULL,
  updated_at timestamp without time zone default current_timestamp NOT NULL
);

ALTER TABLE session OWNER TO pivotal;

ALTER TABLE ONLY session ADD CONSTRAINT session_pkey PRIMARY KEY (token);

CREATE UNIQUE INDEX index_users_on_token ON session USING btree (token);

ALTER TABLE session ADD CONSTRAINT session_user_id_fkey
FOREIGN KEY (user_id) REFERENCES users (id)
ON DELETE CASCADE;
