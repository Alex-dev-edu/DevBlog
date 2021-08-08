insert into users (id, code, email, is_moderator, name, password, reg_time) values
(1, 'code1', 'ivan.ivan@mail.ru', 1, 'Ivan', 'Ivan1967', '2011-12-18 13:13:13'),
(2, 'code2', 'ivan.vasiliy@mail.ru', -1, 'Vasiliy', 'Vasiliy2006', '2014-04-12 06:06:06');
insert into posts (id, is_active, moderation_status, moderator_id, text, time, title, user_id, view_count) values
(1, 1, 'ACCEPTED', 1, 'test text 1', '2015-12-18 13:13:13', 'Title1', 2, 13),
(2, 1, 'ACCEPTED', 1, 'test text 2', '2016-11-18 13:13:13', 'Title2', 2, 14),
(3, 1, 'ACCEPTED', 1, 'test text 3', '2016-12-18 13:13:13', 'Title3', 1, 666);
insert into post_votes (id, post_id, time, user_id, value) values
(1, 1, '2017-10-18 13:13:13', 1, 1),
(2, 3, '2017-10-20 13:13:13', 1, 1),
(3, 3, '2017-10-21 13:13:13', 2, -1),
(4, 2, '2017-10-21 13:13:13', 2, 1),
(5, 1, '2017-10-19 13:13:13', 2, 1);
insert into tags (id, name) values
(1, 'Java'),
(2, 'Roblox');
insert into tag2post (id, post_id, tag_id) values
(1, 1, 1),
(2, 1, 2),
(3, 2, 1);
insert into post_comments (id, post_id, text, time, user_id) values
(1, 1, 'commenttext1', '2018-10-19 13:13:13', 1),
(2, 2, 'commenttext2', '2018-10-11 13:13:13', 1),
(3, 1, 'commenttext3', '2018-10-10 13:13:13', 1);
insert into post_comments (id, parent_id, post_id, text, time, user_id) values
(4, 3, 1, 'commenttext4', '2018-10-20 13:13:13', 2);