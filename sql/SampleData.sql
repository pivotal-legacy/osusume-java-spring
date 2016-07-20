-- Delete existing data

TRUNCATE TABLE restaurant, cuisine, session, comment, users, likes, photo_url, price_range;


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

  INSERT INTO users (name, email, password) VALUES ('Hiroko', 'hiroko', 'hiroko');
  INSERT INTO users (name, email, password) VALUES ('Seiji', 'seiji', 'seiji');
  INSERT INTO users (name, email, password) VALUES ('Lisa', 'lisa', 'lisa');


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

  INSERT INTO restaurant (name, address, notes, created_by_user_id, place_id, latitude, longitude) VALUES
    ('Butagumi', '2-24-9 Nishiazabu, Minato, Tokyo 106-0031, Japan', 'Juicy tonkatsu!', dannyUserId, 'ChIJ72OI0nqLGGARy5_3z1EihGk', 35.66096400000001, 139.7220907);

  INSERT INTO restaurant (name, address, notes, created_by_user_id, place_id, latitude, longitude) VALUES
    ('AFURI 六本木ヒルズ', '6 Chome-2-31 Roppongi, Minato, Tokyo 106-0032, Japan', 'Light yuzu ramen', dannyUserId, 'ChIJP1lEhXeLGGARhk5zBfKyb_A', 35.661875, 139.730148);

  INSERT INTO restaurant (name, address, notes, created_by_user_id, place_id, latitude, longitude) VALUES
    ('ピザカヤ PIZZAKAYA', 'Japan, 〒106-0031 Tokyo, Minato, Nishiazabu, 3 Chome−1−19, 小山ビル ２Ｆ', 'Excellent pizza and craft beer.', dannyUserId, 'ChIJHSGarXCLGGARKEzuSpVwYqs', 35.6605225, 139.7267254);

  INSERT INTO restaurant (name, address, notes, created_by_user_id, place_id, latitude, longitude) VALUES
    ('六本木百鳥', 'Japan, 〒106-0032 Tokyo, Minato, Roppongi, 6 Chome−4−1, 六本木ヒルズ メトロハット/ハリウッドプラザb2', 'Only non-smoking during lunch', dannyUserId, 'ChIJZ3WPl3eLGGAR4Vrt8aCUMYo', 35.66150390000001, 139.7296809);

  INSERT INTO restaurant (name, address, notes, created_by_user_id, place_id, latitude, longitude) VALUES
    ('ぴんとこな 六本木ヒルズ店', 'Japan, 〒106-0032 Tokyo, 港区Roppongi, 6−4−1 六本木ヒルズメトロハットハリウッドプラザ', 'Himawari lunch is cheap with lots of sushi', dannyUserId, 'ChIJZ3WPl3eLGGARcE-WXnks-j4', 35.66154400000001, 139.729363);
END $$;
