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

create procedure save_avatar(u_id integer, path character varying)
    language plpgsql
as
$$
DECLARE
im_id INTEGER;
BEGIN
SELECT image_id FROM users WHERE id = u_id INTO im_id;
IF im_id IS NOT NULL THEN
UPDATE files SET url = path, path_type = 0 WHERE id = im_id;
ELSE
        INSERT INTO files (url, path_type) VALUES (path, 0) RETURNING id INTO im_id;
UPDATE users SET image_id = im_id WHERE id = u_id;
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

create or replace procedure save_chat_avatar(ch_id integer, path character varying)
    language plpgsql
as
$$
DECLARE
    im_id INTEGER;
BEGIN
    SELECT image_id FROM chats WHERE id = ch_id INTO im_id;
    IF im_id IS NOT NULL THEN
        UPDATE files SET url = path, path_type = 0 WHERE id = im_id;
    ELSE
        INSERT INTO files (url, path_type) VALUES (path, 0) RETURNING id INTO im_id;
        UPDATE chats SET image_id = im_id WHERE id = ch_id;
    END IF;
END;
$$;

create or replace procedure save_message_file(msg_id integer, path character varying)
    language plpgsql
as
$$
DECLARE
    im_id INTEGER;
BEGIN

    INSERT INTO files (url, path_type) VALUES (path, 0) RETURNING id INTO im_id;
    INSERT INTO message_file (message_id, file_id) VALUES (msg_id, im_id);
END;
$$;
