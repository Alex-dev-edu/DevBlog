insert into posts (id, is_active, moderation_status, moderator_id, text, time, title, user_id, view_count) values
(4, 1, 'ACCEPTED', 1, 'test text 4', '2021-01-18 13:13:17', 'Title4', 2, 15),
(5, 1, 'ACCEPTED', 1, 'test text 5', '2021-01-18 13:13:40', 'Title5', 2, 16),
(6, 1, 'ACCEPTED', 1, 'test text 6', '2017-04-18 13:13:15', 'Title6', 1, 666),
(7, 1, 'ACCEPTED', 1, 'test text 7', '2017-04-18 13:13:15', 'Title7', 2, 1100),
(8, 1, 'ACCEPTED', 1, 'test text 8', '2017-04-19 00:13:13', 'Title8', 2, 17),
(9, 1, 'ACCEPTED', 1, 'test text 9', '2017-04-17 23:13:17', 'Title9', 2, 18),
(10, 1, 'ACCEPTED', 1, '<h5>tagged text</h5>', '2016-11-18 13:13:13', 'Title10', 2, 25),
(11, 1, 'ACCEPTED', 1, 'test text 11', '2017-04-18 00:13:18', 'Title11', 2, 7),
(12, 1, 'DECLINED', 1, 'test text 12', '2019-11-18 13:13:19', 'Title12', 2, 0),
(13, 1, 'ACCEPTED', 1, 'test text 13', '2021-11-18 13:13:11', 'Title13', 2, 0);
insert into posts (id, is_active, moderation_status, text, time, title, user_id, view_count) values
(14, 1, 'NEW', 'test text 14', '2016-11-18 13:13:13', 'Title14', 2, 0);