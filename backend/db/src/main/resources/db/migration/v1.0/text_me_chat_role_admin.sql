ALTER TABLE user_chat_role DROP CONSTRAINT user_chat_role_pkey;
ALTER TABLE user_chat_role ADD CONSTRAINT user_chat_role_pkey PRIMARY KEY (user_id, chat_id, role_id);

INSERT INTO chat_roles VALUES (4, 'ROLE_ADMIN') ON CONFLICT DO NOTHING ;
