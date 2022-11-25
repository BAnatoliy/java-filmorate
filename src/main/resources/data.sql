delete from LIKES;
delete from FILMS_GENRES;
delete from FRIENDS;
delete from FILMS;
delete from USERS;
alter table FILMS alter column FILM_ID restart with 1;
alter table USERS alter column USER_ID restart with 1;
merge into MPA_RATING (MPA_ID, MPA_NAME)
    values (1, 'G'),
           (2, 'PG'),
           (3, 'PG-13'),
           (4, 'R'),
           (5, 'NC-17');
merge into GENRES (GENRE_ID, GENRE_NAME)
    values (1, 'Комедия'),
           (2, 'Драма'),
           (3, 'Мультфильм'),
           (4, 'Триллер'),
           (5, 'Документальный'),
           (6, 'Боевик');