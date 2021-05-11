CREATE OR REPLACE FUNCTION create_user(nick CHARACTER VARYING, mail CHARACTER VARYING, pass CHARACTER VARYING)
    RETURNS users
    LANGUAGE plpgsql
AS
$$
DECLARE
    _user users;
    _id INTEGER;
BEGIN
    IF EXISTS (SELECT * FROM users WHERE nickname = nick OR email = mail)
    THEN RETURN NULL;
    END IF;
    INSERT INTO cards(content) VALUES (NULL) RETURNING id INTO _id;
    INSERT INTO users(nickname, email, password, card_id) VALUES (nick, mail, pass, _id) RETURNING * INTO _user;
    RETURN _user;
END;
$$;

CREATE OR REPLACE FUNCTION create_chat(owner_id INTEGER, name CHARACTER VARYING)
    RETURNS chats
    LANGUAGE plpgsql
AS
$$
DECLARE
    _chat chats;
    _id INTEGER;
BEGIN
    INSERT INTO cards(content) VALUES (NULL) RETURNING id INTO _id;
    INSERT INTO chats(title, card_id) VALUES (name, _id) RETURNING * INTO _chat;
    INSERT INTO user_chat_role(user_id, chat_id, role_id) VALUES (owner_id, _chat.id, 2);
    RETURN _chat;
END;
$$;

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
$$;

CREATE OR REPLACE FUNCTION activate_email(mail_code VARCHAR)
    RETURNS users LANGUAGE plpgsql AS
$$
DECLARE
    _user users;
    _mail VARCHAR;
    _id INTEGER;
BEGIN
    DELETE FROM inactive_emails WHERE uuid = mail_code RETURNING email, user_id INTO _mail, _id;
    IF _mail IS NULL THEN
        RETURN NULL;
    END IF;
    UPDATE users SET email = _mail WHERE id = _id RETURNING * INTO _user;
    RETURN _user;
END
$$;
