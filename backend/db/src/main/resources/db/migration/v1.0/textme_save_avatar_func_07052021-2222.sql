CREATE OR REPLACE FUNCTION save_avatar(nick VARCHAR, path VARCHAR, type INTEGER)
    RETURNS users LANGUAGE plpgsql AS
$$
DECLARE
    _id INTEGER;
BEGIN
    SELECT _id = image_id FROM users WHERE nickname = nick;
    IF _id THEN
        UPDATE files SET url = path, path_type = type WHERE image_id = _id;
    ELSE
        INSERT INTO files (url, path_type) VALUES (path, type) RETURNING id AS _id;
        UPDATE users SET image_id = _id WHERE nickname = nick;
    END IF;
END;
$$