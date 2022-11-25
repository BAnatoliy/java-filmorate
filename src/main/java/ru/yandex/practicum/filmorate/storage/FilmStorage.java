package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);
    void deleteFilm(long id);
    Film updateFilm(Film film);
    List<Film> getFilms();
    List<Film> getBestFilms(int count);
    Film getFilmById(long id);
    void addLike(long filmId, long userId);
    void deleteLike(long filmId, long userId);
}
