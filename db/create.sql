create table artists (id varchar(32) not null, genre varchar(100), name varchar(200) not null, primary key (id));

create table comments (id varchar(32) not null, artist_id varchar(32) not null, comment varchar(20000) not null, date timestamp not null, user_name varchar(40) not null, primary key (id));

alter table comments add constraint FKq0owkewugt5jcybkhs78ihh8e foreign key (artist_id) references artists;

create index idx_genre on artists (genre) include(id);