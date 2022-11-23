package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@Qualifier("genreDbStorage")
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenreById(long id) {
        String sql = "select * from GENRES where GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToGenre, id);
    }

    @Override
    public List<Genre> getAllGenres() {
        String sql = "select * from GENRES order by GENRE_ID";
        return jdbcTemplate.query(sql, this::mapRowToGenre);
    }

    public void addGenreToFilmById(long filmId, long genreId) {
        String sql = "insert into FILMS_GENRES (FILM_ID, GENRE_ID) values (?, ?)";
        jdbcTemplate.update(sql, filmId, genreId);
    }

    public void deleteGenreOfFilmById(long filmId, long genreId) {
        String sql = "delete from FILMS_GENRES where FILM_ID = ? and GENRE_ID = ?";
        if (jdbcTemplate.update(sql, filmId, genreId) == 0) {
            throw new EntityNotFoundException("Wrong ID");
        }
    }
    private Genre mapRowToGenre(ResultSet resultSet, int rowNum) throws SQLException {
        return Genre.builder()
                .id(resultSet.getLong("GENRE_ID"))
                .name(resultSet.getString("GENRE_NAME"))
                .build();
    }
}
