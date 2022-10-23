package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.util.List;

@Service
@Slf4j
public class FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage; // delete

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage, InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public Film addFilm(Film film) {
        return inMemoryFilmStorage.addFilm(film);
    }

    public void deleteFilm(long id) {
        inMemoryFilmStorage.deleteFilm(id);
    }

    public Film updateFilm(Film film) {
        return inMemoryFilmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return inMemoryFilmStorage.getFilms();
    }

    public List<Film> getBestFilms(int count) {
        return inMemoryFilmStorage.getBestFilms(count);
    }
    public Film getFilmById(long id) {
        return inMemoryFilmStorage.getFilmById(id);
    }

    public void addLike(long filmId, long userId) {
        if(inMemoryUserStorage.getUserById(userId).getClass().equals(User.class)) {
            inMemoryFilmStorage.getFilmById(filmId).addLike(userId);
            log.debug("User with id {} liked film with id {}", userId, filmId);
        } else {
            throw new EntityNotFoundException("User not found!");
        }
    }

    public void deleteLike(long filmId, long userId) {
        if(inMemoryUserStorage.getUserById(userId).getClass().equals(User.class)) {
            inMemoryFilmStorage.getFilmById(filmId).deleteLike(userId);
            log.debug("User with id {} remove like film with id {}", userId, filmId);
        } else {
            throw new EntityNotFoundException("User not found!");
        }
    }
}
