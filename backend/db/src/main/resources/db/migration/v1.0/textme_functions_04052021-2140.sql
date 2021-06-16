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

create or replace procedure ban_card(user_by integer, card integer, date_to timestamp with time zone)
    language plpgsql
as
$$
DECLARE
    banned_id INTEGER;
    report_count INTEGER;
BEGIN
    SELECT count(*) INTO report_count FROM reports WHERE card_id = card AND review_date IS NOT NULL;
    IF report_count = 0
    THEN RETURN;
    END IF;
    SELECT u.id INTO banned_id FROM users u WHERE u.card_id = card LIMIT 1;
    IF banned_id IS NOT NULL
    THEN
        UPDATE reports SET review_date = now(), reviewer_id = 1 WHERE card_id = 2 AND review_date IS NULL;
        INSERT INTO banned_users(user_who_id, user_banned_id, time_to) VALUES (user_by,banned_id, date_to);
    END IF;
END;
$$;
