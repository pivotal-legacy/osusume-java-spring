-- Delete existing data

TRUNCATE TABLE restaurant, cuisine, session, comment, users, likes, price_range;


-- Users

DO $$

DECLARE dannyUserId users.id%TYPE;

BEGIN

  INSERT INTO users (name, email, password) VALUES ('Danny', 'danny', 'danny') RETURNING id INTO dannyUserId;
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

  INSERT INTO cuisine (id, name) VALUES (0, 'Not Specified');
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


  -- Price Ranges

  INSERT INTO price_range (id, range) VALUES (0, 'Not Specified');
  INSERT INTO price_range (range) VALUES ('¥0~999');
  INSERT INTO price_range (range) VALUES ('¥1000~1999');
  INSERT INTO price_range (range) VALUES ('¥2000~2999');
  INSERT INTO price_range (range) VALUES ('¥3000~3999');
  INSERT INTO price_range (range) VALUES ('¥4000~4999');
  INSERT INTO price_range (range) VALUES ('¥5000~9999');
  INSERT INTO price_range (range) VALUES ('¥10,000~');


  -- Restaurants

  INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes, created_by_user_id) VALUES
    ('Butagumi', 'Roppongi Station underground', TRUE, TRUE, TRUE, 'Juicy tonkatsu!', dannyUserId);

  INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes, created_by_user_id) VALUES
    ('Afuri', 'Roppongi Station underground', FALSE, TRUE, FALSE , 'Light yuzu ramen', dannyUserId);

  INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes, created_by_user_id) VALUES
    ('Pizzakaya', 'Down the street', TRUE, TRUE, TRUE, '', dannyUserId);

  INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes, created_by_user_id) VALUES
    ('100 Birds', 'Bottom of the escalator', TRUE, TRUE, TRUE, 'Only non-smoking during lunch', dannyUserId);

  INSERT INTO restaurant (name, address, offers_english_menu, walk_ins_ok, accepts_credit_cards, notes, created_by_user_id) VALUES
    ('Kaiten Sushi', 'Bottom of the escalator', TRUE, TRUE, TRUE, 'Himawari lunch is cheap with lots of sushi', dannyUserId);

END $$;
