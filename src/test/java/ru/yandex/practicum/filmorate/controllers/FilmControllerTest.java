package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmBuilder;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;
    Mpa mpa = Mpa.builder().id(1L).name("G").build();

    @BeforeEach
    public void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(new InMemoryUserStorage())));
    }

    @Test
    void should_List_Of_Films_Be_Empty_When_Films_Not_Created() {
        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertTrue(listOfFilm.isEmpty());
    }

    @Test
    void should_Create_Two_Valid_Film_And_Not_Create_One_Invalid_Films_When_This_Film_Already_Created() {
        Film film = FilmBuilder.builder()
                .name("Otto")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film2 = FilmBuilder.builder()
                .name("Tom")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film3 = FilmBuilder.builder()
                .name("Tom")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        filmController.create(film);
        filmController.create(film2);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film3)
                );
        String expectMassageException = "Film already created";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        ArrayList<Object> listOfFilms = new ArrayList<>(filmController.findAll());
        assertEquals(2, listOfFilms.size());
    }

    @Test
    void should_Create_Three_Valid_Films_And_Find_All_Films() {
        Film film = FilmBuilder.builder()
                .name("Otto")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film2 = FilmBuilder.builder()
                .name("Tom")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film3 = FilmBuilder.builder()
                .name("Titanic")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        filmController.create(film);
        filmController.create(film2);
        filmController.create(film3);

        List<Film> films = new ArrayList<>(filmController.findAll()).stream()
                .sorted((f1, f2) -> (int) (f1.getId() - f2.getId()))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(film, films.get(0));
            assertEquals(film2, films.get(1));
            assertEquals(film3, films.get(2));
            assertEquals(3, films.size());
        });
    }

    @Test
    void should_Update_Film_When_Created_Film_With_The_Same_Id() {
        Film film = FilmBuilder.builder()
                .name("Otto")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film2 = FilmBuilder.builder()
                .name("Tom")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film3 = FilmBuilder.builder()
                .name("Titanic")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        filmController.create(film);
        filmController.create(film2);
        filmController.create(film3);

        Film newFilm = FilmBuilder.builder()
                .id(3)
                .name("Spider Man")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        filmController.update(newFilm);

        List<Film> films = new ArrayList<>(filmController.findAll()).stream()
                .sorted((f1, f2) -> (int) (f1.getId() - f2.getId()))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(film, films.get(0));
            assertEquals(film2, films.get(1));
            assertEquals(newFilm, films.get(2));
            assertEquals(3, films.size());
        });
    }

    @Test
    void should_Not_Update_Film_When_Film_With_The_Same_Id_Not_Created() {
        Film film = FilmBuilder.builder()
                .name("Otto")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film2 = FilmBuilder.builder()
                .name("Tom")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        Film film3 = FilmBuilder.builder()
                .name("Titanic")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();

        filmController.create(film);
        filmController.create(film2);
        filmController.create(film3);

        Film newFilm = FilmBuilder.builder()
                .id(333)
                .name("Spider Man")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .mpa(mpa)
                .build();
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> filmController.update(newFilm)
                );

        String expectMassageException = "Film not found!";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<Film> films = new ArrayList<>(filmController.findAll()).stream()
                .sorted((f1, f2) -> (int) (f1.getId() - f2.getId()))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(film, films.get(0));
            assertEquals(film2, films.get(1));
            assertEquals(film3, films.get(2));
            assertEquals(3, films.size());
        });
    }
}