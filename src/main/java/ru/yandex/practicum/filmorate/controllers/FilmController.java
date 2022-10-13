package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int id = 1;

    @GetMapping
    public Collection<Film> findAll() {
        log.debug("Текущее количество фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        checkValidation(film);
        if (films.containsValue(film)) {
            throw new ValidationException("Фильм уже существует.");
        }
        film.setId(id);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film);
        generationId();
        return film;
    }

    @PutMapping
    public Film update(@Valid  @RequestBody Film film) {
        checkValidation(film);
        if (films.containsKey(film.getId())) {
            log.debug("Обновлен фильм: {}", film);
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Такого фильма нет.");
        }
    }

    private void generationId() {
        id++;
    }

    private void checkValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Не корректное название фильма.");
        }
        if (film.getDescription().length() > 200) {
            throw new ValidationException("Слишком длинное описание фильма.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата выхода фильма не может быть раньше 28 декабря 1895 г.");
        }
        if (film.getDuration() < 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
