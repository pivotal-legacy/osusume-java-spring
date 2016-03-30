create table restaurant (
    id serial primary key,
    name varchar(255) not null,
    address varchar(255),
    offers_english_menu boolean,
    walk_ins_ok boolean,
    accepts_credit_cards boolean,
    notes text
);

