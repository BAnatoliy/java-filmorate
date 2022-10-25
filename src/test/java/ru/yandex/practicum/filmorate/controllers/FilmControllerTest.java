package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
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

    @BeforeEach
    public void setUp() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage())); // delete
    }

    @Test
    public void should_List_Of_Films_Be_Empty_When_Films_Not_Created() {
        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertTrue(listOfFilm.isEmpty());
    }

    @Test
    public void should_Create_Two_Valid_Film_And_Not_Create_One_Invalid_Films_When_This_Film_Already_Created() {
        Film film = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        Film film3 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
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
    public void should_Create_Three_Valid_Films_And_Find_All_Films() {
        Film film = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        Film film3 = new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127);
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
    public void should_Update_Film_When_Created_Film_With_The_Same_Id() {
        Film film = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        Film film3 = new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127);
        filmController.create(film);
        filmController.create(film2);
        filmController.create(film3);

        Film newFilm = new Film("Spider Man", "action", LocalDate.of(2008, 1, 13),
                95);
        newFilm.setId(3);
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
    public void should_Not_Update_Film_When_Film_With_The_Same_Id_Not_Created() {
        Film film = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                100);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        Film film3 = new Film("Titanic", "drama", LocalDate.of(1995, 7, 25),
                127);
        filmController.create(film);
        filmController.create(film2);
        filmController.create(film3);

        Film newFilm = new Film("Spider Man", "action", LocalDate.of(2008, 1, 13),
                95);
        newFilm.setId(111);
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