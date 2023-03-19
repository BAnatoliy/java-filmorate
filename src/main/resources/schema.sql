create table IF NOT EXISTS MPA_RATING
(
    MPA_ID        INTEGER auto_increment,
    MPA_NAME    CHARACTER VARYING(5) not null,
    constraint MPA_KEY
        primary key (MPA_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID        INTEGER auto_increment,
    FILM_NAME      CHARACTER VARYING(50) not null,
    RELEASE_DATE   DATE,
    DESCRIPTION    CHARACTER VARYING(200),
    DURATION       INTEGER,
    RATE           INTEGER,
    MPA_ID         INTEGER,
    constraint FILM_KEY
        primary key (FILM_ID),
    constraint MPA_RATING_FK
        foreign key (MPA_ID) references MPA_RATING
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID      INTEGER auto_increment,
    GENRE_NAME    CHARACTER VARYING(50) not null,
    constraint GENRES_KEY
        primary key (GENRE_ID)
);

create table IF NOT EXISTS FILMS_GENRES
(
    FILM_ID    INTEGER,
    GENRE_ID   INTEGER,
    constraint FILMS_FK
        foreign key (FILM_ID) references FILMS on delete cascade,
    constraint GENRES_FK
        foreign key (GENRE_ID) references GENRES
);

create table IF NOT EXISTS USERS
(
    USER_ID       INTEGER auto_increment,
    USER_LOGIN    CHARACTER VARYING(50) not null,
    USER_NAME     CHARACTER VARYING(50),
    EMAIL         CHARACTER VARYING(50),
    BIRTHDAY      DATE,
    constraint USER_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER,
    FRIEND_ID INTEGER,
    constraint FRIEND_ID
        foreign key (FRIEND_ID) references USERS on delete cascade,
    constraint USER_FK
        foreign key (USER_ID) references USERS on delete cascade
);

create table IF NOT EXISTS LIKES
(
    FILM_ID integer,
    USER_ID integer,
    constraint FILM_FK
        foreign key (FILM_ID) references FILMS (FILM_ID) on delete cascade,
    constraint USER_LIKE_FK
        foreign key (USER_ID) references USERS (USER_ID) on delete cascade
);
