create table photo_url (
  id serial primary key,
  url varchar(255) not null,
  restaurant_id INTEGER
);
