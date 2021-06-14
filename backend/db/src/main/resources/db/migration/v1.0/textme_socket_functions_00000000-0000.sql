CREATE OR REPLACE FUNCTION new_direct_msg(u_from INTEGER, u_to INTEGER, text CHARACTER VARYING)
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
    SELECT id INTO from_id FROM users WHERE id = u_from;
    SELECT id INTO to_id FROM users WHERE id = u_to;
    INSERT INTO messages (content) VALUES (text) RETURNING id INTO m_id;
    INSERT INTO direct_messages (user_from_id, user_to_id, message_id) VALUES (from_id, to_id, m_id) RETURNING * INTO msg;
    RETURN msg;
END
$$;

CREATE OR REPLACE FUNCTION new_chat_msg(u_from INTEGER, c_id INTEGER, text CHARACTER VARYING)
    RETURNS chat_messages
    LANGUAGE plpgsql
AS
$$
DECLARE
    u_id INTEGER;
    m_id INTEGER;
    msg chat_messages;
BEGIN
    SELECT id INTO u_id FROM users WHERE id = u_from;
    INSERT INTO messages (content) VALUES (text) RETURNING id INTO m_id;
    INSERT INTO chat_messages (user_id, chat_id , message_id) VALUES (u_id, c_id, m_id) RETURNING * INTO msg;
    RETURN msg;
END;
$$;
