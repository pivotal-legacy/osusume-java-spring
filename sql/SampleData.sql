-- Delete existing data

TRUNCATE TABLE users, session;
TRUNCATE TABLE cuisine;
TRUNCATE TABLE restaurant;


-- Users

INSERT INTO users (name, email, password) VALUES ('Danny', 'danny', 'danny');
INSERT INTO users (name, email, password) VALUES ('Derek', 'derek', 'derek');
INSERT INTO users (name, email, password) VALUES ('Eno', 'eno', 'eno');
INSERT INTO users (name, email, password) VALUES ('Erika', 'erika', 'erika');
INSERT INTO users (name, email, password) VALUES ('Eugene', 'eugene', 'eugene');
INSERT INTO users (name, email, password) VALUES ('Hadrien', 'hadrien', 'hadrien');
INSERT INTO users (name, email, password) VALUES ('Heewon', 'heewon', 'heewon');
INSERT INTO users (name, email, password) VALUES ('Ichizo', 'ichizo', 'ichizo');
INSERT INTO users (name, email, password) VALUES ('Jeana', 'jeana', 'jeana');
INSERT INTO users (name, email, password) VALUES ('Kyle', 'kyle', 'kyle');
INSERT INTO users (name, email, password) VALUES ('Miya', 'miya', 'miya');
INSERT INTO users (name, email, password) VALUES ('Robert', 'robert', 'robert');
INSERT INTO users (name, email, password) VALUES ('Sukjun', 'sukjun', 'sukjun');
INSERT INTO users (name, email, password) VALUES ('Whitney', 'whitney', 'whitney');
INSERT INTO users (name, email, password) VALUES ('Yuki', 'yuki', 'yuki');


-- Cuisines

INSERT INTO cuisine (name) VALUES ('Japanese');
INSERT INTO cuisine (name) VALUES ('Korean');
INSERT INTO cuisine (name) VALUES ('Chinese');
INSERT INTO cuisine (name) VALUES ('Beer');
INSERT INTO cuisine (name) VALUES ('American');
INSERT INTO cuisine (name) VALUES ('Italian');
INSERT INTO cuisine (name) VALUES ('Thai');
INSERT INTO cuisine (name) VALUES ('French');
INSERT INTO cuisine (name) VALUES ('Seafood');
INSERT INTO cuisine (name) VALUES ('Russian');
INSERT INTO cuisine (name) VALUES ('Turkish');
INSERT INTO cuisine (name) VALUES ('Carribean');


-- Restaurants

INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) VALUES
  ('Butagumi', 'Roppongi Station underground', TRUE, TRUE, TRUE, 'Juicy tonkatsu!');

INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) VALUES
  ('Afuri', 'Roppongi Station underground', FALSE, TRUE, FALSE , 'Light yuzu ramen');

INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) VALUES
  ('Pizzakaya', 'Down the street', TRUE, TRUE, TRUE, '');

INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) VALUES
  ('100 Birds', 'Bottom of the escalator', TRUE, TRUE, TRUE, 'Only non-smoking during lunch');

INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes) VALUES
  ('Kaiten Sushi', 'Bottom of the escalator', TRUE, TRUE, TRUE, 'Himawari lunch is cheap with lots of sushi');
