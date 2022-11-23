package StorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmBuilder;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GenreDbStorageTest {
    private final GenreDbStorage genreDbStorage;
    private final FilmDbStorage filmDbStorage;

    void addFilm() {
        Mpa mpa = Mpa.builder().id(1L).name("G").build();

        Film film = FilmBuilder.builder()
                .name("Film1")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        filmDbStorage.addFilm(film);
        Film film2 = FilmBuilder.builder()
                .name("Film2")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        filmDbStorage.addFilm(film2);
        Film film3 = FilmBuilder.builder()
                .name("Film3")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        filmDbStorage.addFilm(film3);
    }

    @Sql({"classpath:/schema.sql", "classpath:/data.sql"})
    @Order(1)
    @Test
    void addGenreToFilm() {
        addFilm();
        genreDbStorage.addGenreToFilmById(1L,1L);
        genreDbStorage.addGenreToFilmById(1L,2L);
        Optional<Film> filmOptional = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmOptional)
                .isPresent().hasValueSatisfying(film -> assertEquals(2, film.getGenres().size()));
    }

    @Order(2)
    @Test
    void should_Deleted_Genre_When_Id_Correct() {
        Optional<Film> filmBeforeDeleted = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmBeforeDeleted).isPresent().hasValueSatisfying(film -> assertEquals(2, film.getGenres().size()));

        genreDbStorage.deleteGenreOfFilmById(1L, 2L);

        Optional<Film> filmAfterDeleted = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmAfterDeleted).isPresent().hasValueSatisfying(film -> assertEquals(1, film.getGenres().size()));
    }

    @Order(3)
    @Test
    void should_Deleted_Genre_When_Id_Wrong() {
        Optional<Film> filmBeforeDeleted = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmBeforeDeleted).isPresent().hasValueSatisfying(film -> assertEquals(1, film.getGenres().size()));

        assertThrows(EntityNotFoundException.class, () -> genreDbStorage.deleteGenreOfFilmById(111L, 1L));
        assertThrows(EntityNotFoundException.class, () -> genreDbStorage.deleteGenreOfFilmById(1L, 222L));

        Optional<Film> filmAfterDeleted = Optional.ofNullable(filmDbStorage.getFilmById(1L));
        assertThat(filmAfterDeleted).isPresent().hasValueSatisfying(film -> assertEquals(1, film.getGenres().size()));
    }

    @Order(4)
    @Test
    void get_All_Genres_Test() {
        Optional<List<Genre>> genres = Optional.ofNullable(genreDbStorage.getAllGenres());
        assertThat(genres).isPresent().hasValueSatisfying(films -> {
                assertEquals(6, films.size());
                assertEquals("Комедия", films.get(0).getName());
                assertEquals("Драма", films.get(1).getName());
                assertEquals("Мультфильм", films.get(2).getName());
                assertEquals("Триллер", films.get(3).getName());
                assertEquals("Документальный", films.get(4).getName());
                assertEquals("Боевик", films.get(5).getName());
        });
    }

    @Order(4)
    @Test
    void should_Get_Genre_By_Correct_Id() {
        Optional<Genre> genre = Optional.ofNullable(genreDbStorage.getGenreById(3L));
        assertThat(genre).isPresent().hasValueSatisfying(film -> assertThat(film)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "Мультфильм"));
    }

    @Order(4)
    @Test
    void should_Get_Genre_By_Wrong_Id() {
        assertThrows(EmptyResultDataAccessException.class, () -> genreDbStorage.getGenreById(333L));
    }
}