CREATE OR REPLACE FUNCTION new_direct_msg(u_from CHARACTER VARYING, u_to CHARACTER VARYING,
                                          text CHARACTER VARYING, images CHARACTER VARYING)
    RETURNS direct_messages
    LANGUAGE plpgsql
AS
$$
DECLARE
    from_id INTEGER;
    to_id INTEGER;
    m_id INTEGER;
    msg direct_messages;
BEGIN
    SELECT id INTO from_id FROM users WHERE nickname = u_from;
    SELECT id INTO to_id FROM users WHERE nickname = u_to;
    INSERT INTO messages (content) VALUES (text) RETURNING id INTO m_id;
    INSERT INTO direct_messages (user_from_id, user_to_id, message_id) VALUES (from_id, to_id, m_id) RETURNING * INTO msg;
    INSERT INTO message_file (message_id, file_id) SELECT m_id, f.url FROM files f WHERE f.url = ANY(images::varchar[]);

    RETURN msg;
END
$$;

CREATE OR REPLACE FUNCTION new_chat_msg(u_from CHARACTER VARYING, c_id INTEGER,
                                        text CHARACTER VARYING, images CHARACTER VARYING)
    RETURNS chat_messages
    LANGUAGE plpgsql
AS
$$
DECLARE
    u_id INTEGER;
    m_id INTEGER;
    msg chat_messages;
BEGIN
    SELECT id INTO u_id FROM users WHERE nickname = u_from;
    INSERT INTO messages (content) VALUES (text) RETURNING id INTO m_id;
    INSERT INTO chat_messages (user_id, chat_id , message_id) VALUES (u_id, c_id, m_id) RETURNING * INTO msg;
    INSERT INTO message_file (message_id, file_id) SELECT m_id, f.url FROM files f WHERE f.url = ANY(images::varchar[]);
    RETURN msg;
END;
$$;
