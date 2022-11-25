package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public final class FilmBuilder {
    private Set<Long> idUsersLike = new HashSet<>();
    private long id;
    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
    private @NotNull @NotBlank String name;
    private @Size(max = 200) String description;
    private LocalDate releaseDate;
    private @Min(0) int duration;
    private int rate;

    private FilmBuilder() {
    }

    public static FilmBuilder builder() {
        return new FilmBuilder();
    }

    public FilmBuilder idUsersLike(Set<Long> idUsersLike) {
        this.idUsersLike = idUsersLike;
        return this;
    }

    public FilmBuilder id(long id) {
        this.id = id;
        return this;
    }

    public FilmBuilder mpa(Mpa mpa) {
        this.mpa = mpa;
        return this;
    }

    public FilmBuilder genres(Set<Genre> genres) {
        this.genres = genres;
        return this;
    }

    public FilmBuilder name(String name) {
        this.name = name;
        return this;
    }

    public FilmBuilder description(String description) {
        this.description = description;
        return this;
    }

    public FilmBuilder releaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        return this;
    }

    public FilmBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public FilmBuilder rate(int rate) {
        this.rate = rate;
        return this;
    }

    public Film build() {
        Film film = new Film(name, description, releaseDate, duration, rate);
        film.setIdUsersLike(idUsersLike);
        film.setId(id);
        film.setMpa(mpa);
        film.setGenres(genres);
        return film;
    }
}
