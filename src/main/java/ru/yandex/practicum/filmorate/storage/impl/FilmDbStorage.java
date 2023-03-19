package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
@Qualifier("filmDbStorage")
@Slf4j
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Film addFilm(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("FILMS")
                .usingGeneratedKeyColumns("FILM_ID");

        long filmId = simpleJdbcInsert.executeAndReturnKey(film.toMap()).longValue();

        film.getGenres().forEach(genre -> {
            String sql2 = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
            jdbcTemplate.update(sql2, filmId, genre.getId());
        });

        return getFilmById(filmId);
    }

    @Override
    public Film getFilmById(long id) {
        String sql2 = "select * from FILMS where FILM_ID = ?";
        return jdbcTemplate.queryForObject(sql2, this::mapRowToFilm, id); // genres
    }

    @Override
    public void deleteFilm(long id) {
        String sql = "delete from FILMS where FILM_ID = ?";
        boolean isDeleted = jdbcTemplate.update(sql, id) == 0;
        if (isDeleted) {
            throw new EntityNotFoundException("Film with not found");
        }
        log.debug("Film with id = {} delete", id);
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "delete from FILMS_GENRES where FILM_ID = ?";
        if (jdbcTemplate.update(sql, film.getId()) > 0) {
            log.debug("Deleted genres of film with id: {}", film.getId());
        }

        String sql2 = "update FILMS set FILM_NAME = ?, RELEASE_DATE = ?, " +
                "DESCRIPTION = ?, DURATION = ?, RATE = ?, MPA_ID = ? where FILM_ID = ?";
        boolean isUpdate = jdbcTemplate.update(sql2, film.getName(), film.getReleaseDate(), film.getDescription(),
                film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId()) > 0;

        film.getGenres().forEach(genre -> {
            String sql3 = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) " +
                    "values (?, ?)";
            jdbcTemplate.update(sql3, film.getId(), genre.getId());
        });

        if (isUpdate) {
            log.debug("User with id: {} updated", film.getId());
        } else {
            throw new EntityNotFoundException("User not found!");
        }
        return getFilmById(film.getId());
    }

    @Override
    public List<Film> getFilms() {
        String sql = "select * from FILMS order by FILM_ID";
        return jdbcTemplate.query(sql, this::mapRowToFilm);
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "insert into LIKES (FILM_ID, USER_ID) values (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sql = "delete from LIKES where FILM_ID = ? and USER_ID = ?";
        if (jdbcTemplate.update(sql, filmId, userId) == 0) {
            throw new EntityNotFoundException("Wrong ID");
        }
    }

    @Override
    public List<Film> getBestFilms(int count) {
        if (count < 0) {
            throw new ValidationException("Count should be positive");
        }

        String sql = "select F.FILM_ID, F.FILM_NAME, F.RELEASE_DATE, F.DESCRIPTION, F.DURATION, F.RATE, " +
                "F.MPA_ID, count(L.USER_ID) as Likes from FILMS as F left join LIKES as L on F.FILM_ID = L.FILM_ID " +
                "GROUP BY F.FILM_ID order by Likes desc LIMIT ?";
        List<Film> bestFilms = jdbcTemplate.query(sql, this::mapRowToFilm, count);

        log.debug("Get {} best films", count);
        return bestFilms;
    }

    private List<Genre> getGenresByFilmsId(long id) {
        String sql = "select G.GENRE_ID, G.GENRE_NAME from GENRES as G " +
                "join FILMS_GENRES as FG on G.GENRE_ID = FG.GENRE_ID where FG.FILM_ID = ?";
        return jdbcTemplate.query(sql, this::mapRowToGenre, id);
    }

    private Mpa getMpaOfFilm(long id) {
        String sql = "select * from MPA_RATING where MPA_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToMpa, id);
    }

    private List<Long> getFilmsLikes(long filmId) {
        String sql = "select USER_ID from LIKES where FILM_ID = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> resultSet.getLong("USER_ID"), filmId);
    }

    private Set<Genre> getSetOfGenres(List<Genre> genresList) {
        Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));
        genres.addAll(genresList);
        return genres;
    }

    private Mpa mapRowToMpa(ResultSet resultSet, int rowNum) throws SQLException {
        return Mpa.builder()
                .id(resultSet.getLong("MPA_ID"))
                .name(resultSet.getString("MPA_NAME"))
                .build();
    }

    private Film mapRowToFilm(ResultSet resultSet, int rowNum) throws SQLException {
        long filmId = resultSet.getLong("FILM_ID");
        return FilmBuilder.builder()
                .id(filmId)
                .idUsersLike(new HashSet<>(getFilmsLikes(filmId)))
                .name(resultSet.getString("FILM_NAME"))
                .releaseDate(resultSet.getDate("RELEASE_DATE").toLocalDate())
                .description(resultSet.getString("DESCRIPTION"))
                .duration(resultSet.getInt("DURATION"))
                .rate(resultSet.getInt("RATE"))
                .mpa(getMpaOfFilm(resultSet.getLong("MPA_ID")))
                .genres(getSetOfGenres(getGenresByFilmsId(filmId)))
                .build();
    }

    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}