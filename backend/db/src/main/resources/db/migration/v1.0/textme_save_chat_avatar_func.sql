create procedure save_chat_avatar(ch_id integer, path character varying)
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