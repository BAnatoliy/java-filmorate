package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {
    private FilmController filmController;

    @BeforeEach
    public void setUp() {
        filmController = new FilmController();
    }

    @Test
    public void should_List_Of_Films_Be_Empty_When_Films_Not_Created() {
        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertTrue(listOfFilm.isEmpty());
    }

    @Test
    public void should_Create_One_Valid_Film_And_Not_Create_Two_Invalid_Films_When_Name_Of_Films_Is_Empty_Or_Null() {
        Film film = new Film(null, "class", LocalDate.of(2005, 10, 17),
                100);
        ValidationException exception1 = assertThrows(ValidationException.class,
                () -> filmController.create(film)
        );

        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                110);
        filmController.create(film2);

        Film film3 = new Film("", "drama", LocalDate.of(1995, 7, 25),
                127);
        ValidationException exception2 = assertThrows(ValidationException.class,
                () -> filmController.create(film3)
                );

        String expectMassageException = "Не корректное название фильма.";
        String resultMassageException1 = exception1.getMessage();
        String resultMassageException2 = exception2.getMessage();

        assertEquals(expectMassageException, resultMassageException1);
        assertEquals(expectMassageException, resultMassageException2);

        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertEquals(1, listOfFilm.size());
    }

    @Test
    public void should_Create_Two_Valid_Film_When_Length_Of_Description_Is_199_And_200_And_Not_Create_One_Invalid_Films_When_Length_Of_Description_Is_201() {
        Film film = new Film("Otto", "classclassclassclassclassclassclassclassclassclassclassclassclass" +
                "classclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclassclass" +
                "classclassclassclassclassclassclassc", LocalDate.of(2005, 10, 17),
                100);
        assertEquals(201, film.getDescription().length());
        ValidationException exception1 = assertThrows(ValidationException.class,
                () -> filmController.create(film)
        );

        String expectMassageException = "Слишком длинное описание фильма.";
        String resultMassageException1 = exception1.getMessage();
        assertEquals(expectMassageException, resultMassageException1);

        Film film2 = new Film("Tom", "comedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedy" +
                "comedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedycomedy" +
                "comedycomedycomedycomedycomedyc", LocalDate.of(2007, 12, 27),
                110);
        filmController.create(film2);
        assertEquals(199, film2.getDescription().length());

        Film film3 = new Film("Titanic", "dramadramadramadramadramadramadramadramadramadramadramadramadramadrama" +
                "dramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadramadrama" +
                "dramadramadramadramadramadramadrama", LocalDate.of(1995, 7, 25),
                127);
        filmController.create(film3);
        assertEquals(200, film3.getDescription().length());

        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertEquals(2, listOfFilm.size());
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
        String expectMassageException = "Фильм уже существует.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        ArrayList<Object> listOfFilms = new ArrayList<>(filmController.findAll());
        assertEquals(2, listOfFilms.size());
    }

    @Test
    public void should_Create_One_Valid_Film_When_ReleaseDate_Of_Film_Is_1895_12_28_And_1895_12_29_And_Not_Create_One_Invalid_Film_When_ReleaseDate_Of_Film_Is_1895_12_27() {
        Film film = new Film("Otto", "class", LocalDate.of(1895, 12, 29),
                100);
        filmController.create(film);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(1895, 12, 28),
                110);
        filmController.create(film2);

        Film film3 = new Film("Titanic", "drama", LocalDate.of(1895, 12, 27),
                127);
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film3)
        );
        String expectMassageException = "Дата выхода фильма не может быть раньше 28 декабря 1895 г.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertEquals(2, listOfFilm.size());
    }

    @Test
    public void should_Create_Two_Valid_Films_When_Duration_Of_Film_Is_0_And_Positive_And_Not_Create_Two_Invalid_Films_When_Duration_Of_Film_Is_Negative() {
        Film film = new Film("Otto", "class", LocalDate.of(2005, 10, 17),
                1);
        filmController.create(film);
        Film film2 = new Film("Tom", "comedy", LocalDate.of(2007, 12, 27),
                0);
        filmController.create(film2);

        Film film3 = new Film("Titanic", "drama", LocalDate.of(1997, 7, 25),
                -1);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.create(film3)
        );
        String expectMassageException = "Продолжительность фильма должна быть положительной.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        ArrayList<Film> listOfFilm = new ArrayList<>(filmController.findAll());
        assertEquals(2, listOfFilm.size());
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
                .sorted(Comparator.comparingInt(Film::getId))
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
                .sorted(Comparator.comparingInt(Film::getId))
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
        ValidationException exception = assertThrows(ValidationException.class,
                () -> filmController.update(newFilm)
                );

        String expectMassageException = "Такого фильма нет.";
        String resultMassageException = exception.getMessage();
        assertEquals(expectMassageException, resultMassageException);

        List<Film> films = new ArrayList<>(filmController.findAll()).stream()
                .sorted(Comparator.comparingInt(Film::getId))
                .collect(Collectors.toList());

        assertAll(() -> {
            assertEquals(film, films.get(0));
            assertEquals(film2, films.get(1));
            assertEquals(film3, films.get(2));
            assertEquals(3, films.size());
        });
    }
}