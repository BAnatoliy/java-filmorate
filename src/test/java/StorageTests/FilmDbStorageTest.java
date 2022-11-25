package StorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.impl.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FilmDbStorageTest {
    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    void addFilm() {
        Mpa mpa = Mpa.builder().id(1L).name("G").build();

        Film film = FilmBuilder.builder()
                .name("Film1")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        filmStorage.addFilm(film);
        Film film2 = FilmBuilder.builder()
                .name("Film2")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        filmStorage.addFilm(film2);
        Film film3 = FilmBuilder.builder()
                .name("Film3")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        filmStorage.addFilm(film3);

        User newUser1 = UserBuilder.builder()
                .name("User1")
                .login("qwe")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser1);
        User newUser2 = UserBuilder.builder()
                .name("User2")
                .login("qwe")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser2);
        User newUser3 = UserBuilder.builder()
                .name("User3")
                .login("qwe")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser3);

        filmStorage.addLike(2, 1);
        filmStorage.addLike(2, 2);
        filmStorage.addLike(2, 3);
        filmStorage.addLike(3, 1);
        filmStorage.addLike(3, 2);
    }

    @Sql({"classpath:/schema.sql", "classpath:/data.sql"})
    @Order(1)
    @Test()
    void add_Film_Test() {
        addFilm();
        Mpa mpa = Mpa.builder().id(1L).name("G").build();
        Film newFilm = FilmBuilder.builder()
                .name("NewFilm")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.addFilm(newFilm));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("name", "NewFilm")
                );

        Optional<Film> filmOptionalById = Optional.ofNullable(filmStorage.getFilmById(4L));
        assertThat(filmOptionalById)
                .isPresent()
                .hasValueSatisfying((film -> assertThat(film).hasFieldOrPropertyWithValue("id", 4L)
                        .hasFieldOrPropertyWithValue("name", "NewFilm")));
    }

    @Order(2)
    @Test()
    void update_Film_Test() {
        Mpa mpa = Mpa.builder().id(1L).name("G").build();
        Film newFilm = FilmBuilder.builder()
                .id(4L)
                .name("Film4")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.updateFilm(newFilm));
        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("name", "Film4")
                );

        Optional<Film> filmOptionalById = Optional.ofNullable(filmStorage.getFilmById(4L));
        assertThat(filmOptionalById)
                .isPresent()
                .hasValueSatisfying((film -> assertThat(film).hasFieldOrPropertyWithValue("id", 4L)
                        .hasFieldOrPropertyWithValue("name", "Film4")));
    }

    @Order(3)
    @Test
    void should_Not_Deleted_Film_When_Id_Wrong_And_Get_Films() {
        Optional<List<Film>> filmListBeforeDeleted = Optional.ofNullable(filmStorage.getFilms());
        assertThat(filmListBeforeDeleted).isPresent().hasValueSatisfying(films -> assertEquals(4, films.size()));

        assertThrows(EntityNotFoundException.class, () -> filmStorage.deleteFilm(555L));

        Optional<List<Film>> filmListAfterDeleted = Optional.ofNullable(filmStorage.getFilms());
        assertThat(filmListAfterDeleted).isPresent().hasValueSatisfying(films -> assertEquals(4, films.size()));
    }

    @Order(4)
    @Test
    void should_Deleted_User_When_Id_Correct() {
        Optional<List<Film>> filmListBeforeDeleted = Optional.ofNullable(filmStorage.getFilms());
        assertThat(filmListBeforeDeleted).isPresent().hasValueSatisfying(films -> assertEquals(4, films.size()));

        filmStorage.deleteFilm(4L);

        Optional<List<Film>> filmListAfterDeleted = Optional.ofNullable(filmStorage.getFilms());
        assertThat(filmListAfterDeleted).isPresent().hasValueSatisfying(films -> assertEquals(3, films.size()));
    }

    @Order(5)
    @Test
    void should_get_Film_By_Id_When_Id_Correct() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(2L));
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> {
            assertThat(film)
                    .hasFieldOrPropertyWithValue("id", 2L)
                    .hasFieldOrPropertyWithValue("name", "Film2");
            assertEquals(1L, film.getMpa().getId());
        });
    }

    @Order(6)
    @Test
    void should_get_Film_By_Id_When_Id_Wrong() {
        assertThrows(EmptyResultDataAccessException.class, () -> filmStorage.getFilmById(555L));
    }

    @Order(7)
    @Test
    void should_Add_Like_When_User_Created_And_Film_Created() {
        filmStorage.addLike(1,1);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1L));
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> assertThat(film.getIdUsersLike()).hasSize(1));
        filmStorage.addLike(1,2);
        Optional<Film> filmOptional2 = Optional.ofNullable(filmStorage.getFilmById(1L));
        assertThat(filmOptional2).isPresent().hasValueSatisfying(film -> assertThat(film.getIdUsersLike()).hasSize(2));
    }

    @Order(8)
    @Test
    void should_Not_Add_Like_When_User_Not_Created_Or_Film_Not_Created() {
        assertThrows(DataIntegrityViolationException.class, () -> filmStorage.addLike(1111,1));
        assertThrows(DataIntegrityViolationException.class, () -> filmStorage.addLike(1,2222));
    }

    @Order(9)
    @Test
    void should_Delete_Like_When_User_Created_And_Film_Created() {
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(1L));
        assertThat(filmOptional).isPresent().hasValueSatisfying(film -> assertThat(film.getIdUsersLike()).hasSize(2));
        filmStorage.deleteLike(1,2);
        Optional<Film> filmOptional2 = Optional.ofNullable(filmStorage.getFilmById(1L));
        assertThat(filmOptional2).isPresent().hasValueSatisfying(film -> assertThat(film.getIdUsersLike()).hasSize(1));
    }

    @Order(10)
    @Test
    void should_Not_Delete_Like_When_User_Not_Created_Or_Film_Not_Created() {
        assertThrows(EntityNotFoundException.class, () -> filmStorage.deleteLike(1111,1));
        assertThrows(EntityNotFoundException.class, () -> filmStorage.deleteLike(1,2222));
    }

    @Order(11)
    @Test
    void get_Best_Film_Test() {
        Optional<List<Film>> filmList = Optional.ofNullable(filmStorage.getBestFilms(2));
        assertThat(filmList).isPresent().hasValueSatisfying(users -> assertEquals(2, users.size()));

        Optional<List<Film>> filmList2 = Optional.ofNullable(filmStorage.getBestFilms(3));
        assertThat(filmList2).isPresent().hasValueSatisfying(users -> {
            assertEquals(3, users.size());
            assertEquals(2L, users.get(0).getId());
            assertEquals(3L, users.get(1).getId());
            assertEquals(1L, users.get(2).getId());
        });

        Optional<List<Film>> filmList3 = Optional.ofNullable(filmStorage.getBestFilms(200));
        assertThat(filmList3).isPresent().hasValueSatisfying(users -> assertEquals(3, users.size()));
    }
}