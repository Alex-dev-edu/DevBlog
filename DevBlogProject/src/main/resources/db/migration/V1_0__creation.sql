drop table if exists captcha_codes;
drop table if exists global_settings;
drop table if exists post_comments;
drop table if exists post_votes;
drop table if exists posts;
drop table if exists tag2post;
drop table if exists tags;
drop table if exists users;
create table captcha_codes (id integer not null auto_increment, code TINYTEXT, secret_code TINYTEXT, time datetime(6), primary key (id)) engine=InnoDB;
create table global_settings (id integer not null auto_increment, code varchar(255), name varchar(255), value varchar(255), primary key (id)) engine=InnoDB;
create table post_comments (id integer not null auto_increment, parent_id integer, post_id integer, text varchar(255), time datetime(6), user_id integer, primary key (id)) engine=InnoDB;
create table post_votes (id integer not null auto_increment, post_id integer, time datetime(6), user_id integer, value TINYINT, primary key (id)) engine=InnoDB;
create table posts (id integer not null auto_increment, is_active TINYINT, moderation_status ENUM('NEW', 'ACCEPTED', 'DECLINED'), moderator_id integer, text TEXT, time datetime(6), title varchar(255), user_id integer, view_count integer, primary key (id)) engine=InnoDB;
create table tag2post (id integer not null auto_increment, post_id integer, tag_id integer, primary key (id)) engine=InnoDB;
create table tags (id integer not null auto_increment, name varchar(255), primary key (id)) engine=InnoDB;
create table users (id integer not null auto_increment, code varchar(255), email varchar(255), is_moderator TINYINT, name varchar(255), password varchar(255), photo TEXT, reg_time datetime(6), primary key (id)) engine=InnoDB;
alter table post_comments add constraint FKc3b7s6wypcsvua2ycn4o1lv2c foreign key (parent_id) references post_comments (id);
alter table post_comments add constraint FKaawaqxjs3br8dw5v90w7uu514 foreign key (post_id) references posts (id);
alter table post_comments add constraint FKsnxoecngu89u3fh4wdrgf0f2g foreign key (user_id) references users (id);
alter table post_votes add constraint FK9jh5u17tmu1g7xnlxa77ilo3u foreign key (post_id) references posts (id);
alter table post_votes add constraint FK9q09ho9p8fmo6rcysnci8rocc foreign key (user_id) references users (id);
alter table posts add constraint FK6m7nr3iwh1auer2hk7rd05riw foreign key (moderator_id) references users (id);
alter table posts add constraint FK5lidm6cqbc7u4xhqpxm898qme foreign key (user_id) references users (id);
alter table tag2post add constraint FKpjoedhh4h917xf25el3odq20i foreign key (post_id) references posts (id);
alter table tag2post add constraint FKjou6suf2w810t2u3l96uasw3r foreign key (tag_id) references tags (id);