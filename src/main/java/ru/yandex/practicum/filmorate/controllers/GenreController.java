package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable long id) {
        return genreService.getGenreById(id);
    }

    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    @PutMapping("/{genreId}/film/{filmId}")
    public void addGenreToFilmById(@PathVariable long genreId, @PathVariable long filmId) {
        genreService.addGenreToFilmById(genreId, filmId);
    }

    @DeleteMapping("/{genreId}/film/{filmId}")
    public void deleteGenreOfFilmById(@PathVariable long genreId, @PathVariable long filmId) {
        genreService.deleteGenreOfFilmById(genreId, filmId);
    }
}
