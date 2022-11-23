package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {
    Genre getGenreById(long id);
    List<Genre> getAllGenres();
    void addGenreToFilmById(long filmId, long genreId);
    void deleteGenreOfFilmById(long filmId, long genreId);
}
