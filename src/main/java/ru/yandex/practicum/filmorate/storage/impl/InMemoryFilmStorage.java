package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private final Map<Long, Film> films = new HashMap<>();
    private long filmId = 1;

    @Override
    public Film addFilm(Film film) {
        if (films.containsValue(film)) {
            throw new ValidationException("Film already created");
        }
        film.setId(filmId);
        films.put(film.getId(), film);
        generationId();
        log.debug("Film created: {}", film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        if(!films.containsKey(id)) {
            throw new EntityNotFoundException("Film not found!");
        }
        log.debug("Film with id: {} deleted", id);
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if(!films.containsKey(film.getId())) {
            throw new EntityNotFoundException("Film not found!");
        }
        films.put(film.getId(), film);
        log.info("Update film: " + film);
        return film;
    }

    @Override
    public List<Film> getFilms() {
        log.debug("Count of films: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override
    public List<Film> getBestFilms(int count) {
        if (count < 0) {
            throw new ValidationException("Count should be positive");
        }
        List<Film> filmList = new ArrayList<>(films.values());
        log.debug("Get {} best films", count);
        return filmList.stream()
                .sorted((f1, f2) -> f2.getIdUsersLike().size() - f1.getIdUsersLike().size())
                .limit(count).collect(Collectors.toList());
    }

    @Override
    public Film getFilmById(long id) {
        if(!films.containsKey(id)) {
            throw new EntityNotFoundException("Film not found!");
        }
        log.debug("Find film with id: {}", id);
        return films.get(id);
    }

    private void generationId() {
        filmId++;
    }
}
