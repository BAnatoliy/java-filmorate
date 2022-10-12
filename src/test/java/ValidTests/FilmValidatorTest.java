package ValidTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ValidTests.ValidatorTestUtil.valueHasErrorMessage;

public class FilmValidatorTest {

    @Test
    public void createFilmWithEmptyName() {
        Film film = new Film(null, "description", LocalDate.of(1995, 10, 12), 150);
        Film film2 = new Film("", "description", LocalDate.of(1995, 10, 12), 150);
        Film film3 = new Film("   ", "description", LocalDate.of(1995, 10, 12), 150);

        assertTrue(valueHasErrorMessage(film, "Не корректное название фильма."));
        assertTrue(valueHasErrorMessage(film2, "Не корректное название фильма."));
        assertTrue(valueHasErrorMessage(film3, "Не корректное название фильма."));
    }

    @Test
    public void createFilmWithLengthOfDescriptionMore200() {
        Film film = new Film("name", "descriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                "descriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                "descriptiodescriptiodescriptiodescriptio", LocalDate.of(1995, 10, 12), 150);
        Film film2 = new Film("name", "descriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                "descriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                  "descriptiodescriptiodescriptiodescription", LocalDate.of(1995, 10, 12), 150);
        Film film3 = new Film("name", "descriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                "descriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                "descriptiodescriptiodescriptiodescripti", LocalDate.of(1995, 10, 12), 150);

        assertFalse(valueHasErrorMessage(film, "Слишком длинное описание фильма."));
        assertTrue(valueHasErrorMessage(film2, "Слишком длинное описание фильма."));
        assertFalse(valueHasErrorMessage(film3, "Слишком длинное описание фильма."));
    }

    @Test
    public void createFilmWithReleaseDateBefore1895_12_28() {
        Film film = new Film("name", "description", LocalDate.of(1895, 12, 29), 150);
        Film film2 = new Film("name", "description", LocalDate.of(1895, 12, 28), 150);
        Film film3 = new Film("name", "description", LocalDate.of(1895, 12, 27), 150);

        assertFalse(valueHasErrorMessage(film, "Дата выхода фильма не может быть раньше 28 декабря 1895 г."));
        assertFalse(valueHasErrorMessage(film2, "Дата выхода фильма не может быть раньше 28 декабря 1895 г."));
        assertTrue(valueHasErrorMessage(film3, "Дата выхода фильма не может быть раньше 28 декабря 1895 г."));
    }

    @Test
    public void createFilmWithNegativeDuration() {
        Film film = new Film("name", "description", LocalDate.of(1995, 10, 12), 1);
        Film film2 = new Film("name", "description", LocalDate.of(1995, 10, 12), 0);
        Film film3 = new Film("name", "description", LocalDate.of(1995, 10, 12), -1);

        assertFalse(valueHasErrorMessage(film, "Продолжительность фильма должна быть положительной."));
        assertFalse(valueHasErrorMessage(film2, "Продолжительность фильма должна быть положительной."));
        assertTrue(valueHasErrorMessage(film3, "Продолжительность фильма должна быть положительной."));
    }
}
