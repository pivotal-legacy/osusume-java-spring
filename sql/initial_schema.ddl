-- RESTAURANT Table

create table restaurant (
    id serial primary key,
    name varchar(255) not null,
    address varchar(255),
    offers_english_menu boolean,
    walk_ins_ok boolean,
    accepts_credit_cards boolean,
    notes text
);


-- PHOTO_URL Table

create table photo_url (
  id serial primary key,
  url varchar(255) not null,
  restaurant_id INTEGER
);


-- USERS Table

CREATE TABLE users (
  id integer NOT NULL,
  email character varying,
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
