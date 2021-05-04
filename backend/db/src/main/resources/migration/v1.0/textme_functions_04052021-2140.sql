CREATE OR REPLACE FUNCTION create_user(nick CHARACTER VARYING, mail CHARACTER VARYING, pass CHARACTER VARYING)
    RETURNS SETOF users
    LANGUAGE plpgsql
AS
$$
DECLARE
    _user users;
    _id INTEGER;
BEGIN
    INSERT INTO cards(content) VALUES (NULL) RETURNING id INTO _id;
    INSERT INTO users(nickname, email, password, card_id) VALUES (nick, mail, pass, _id) RETURNING * INTO _user;
    INSERT INTO user_app_role(user_id, role_id) VALUES (_id, 0);
    RETURN NEXT _user;
END;
$$;

CREATE OR REPLACE FUNCTION create_chat(owner_id INTEGER, name CHARACTER VARYING)
    RETURNS SETOF chats
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
    RETURN NEXT _chat;
END;
$$;
