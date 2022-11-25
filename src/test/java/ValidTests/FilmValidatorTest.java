
package ValidTests;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmBuilder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ValidTests.ValidatorTestUtil.valueHasErrorMessage;

class FilmValidatorTest {

    @Test
    void createFilmWithEmptyName() {
        Film film = FilmBuilder.builder()
                .name(null)
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .build();
        Film film2 = FilmBuilder.builder()
                .name("")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .build();
        Film film3 = FilmBuilder.builder()
                .name("   ")
                .description("description")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .build();

        assertTrue(valueHasErrorMessage(film, "Не корректное название фильма."));
        assertTrue(valueHasErrorMessage(film2, "Не корректное название фильма."));
        assertTrue(valueHasErrorMessage(film3, "Не корректное название фильма."));
    }

    @Test
    void createFilmWithLengthOfDescriptionMore200() {
        Film film = FilmBuilder.builder()
                .name("name")
                .description("descriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                        "descriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                        "descriptiodescriptiodescriptiodescriptio")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .build();

        Film film2 = FilmBuilder.builder()
                .name("name")
                .description("descriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                        "descriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                        "descriptiodescriptiodescriptiodescription")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .build();

        Film film3 = FilmBuilder.builder()
                .name("name")
                .description("descriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                        "descriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptiodescriptio" +
                        "descriptiodescriptiodescriptiodescripti")
                .releaseDate(LocalDate.of(1995, 10, 12))
                .duration(150)
                .build();


        assertFalse(valueHasErrorMessage(film, "Слишком длинное описание фильма."));
        assertTrue(valueHasErrorMessage(film2, "Слишком длинное описание фильма."));
        assertFalse(valueHasErrorMessage(film3, "Слишком длинное описание фильма."));
    }

    @Test
    void createFilmWithReleaseDateBefore1895_12_28() {
        Film film = FilmBuilder.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(150)
                .build();

        Film film2 = FilmBuilder.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 28))
                .duration(150)
                .build();

        Film film3 = FilmBuilder.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .duration(150)
                .build();

        assertFalse(valueHasErrorMessage(film, "Дата выхода фильма не может быть раньше 28 декабря 1895 г."));
        assertFalse(valueHasErrorMessage(film2, "Дата выхода фильма не может быть раньше 28 декабря 1895 г."));
        assertTrue(valueHasErrorMessage(film3, "Дата выхода фильма не может быть раньше 28 декабря 1895 г."));
    }

    @Test
    void createFilmWithNegativeDuration() {
        Film film = FilmBuilder.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(1)
                .build();
        Film film2 = FilmBuilder.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(0)
                .build();
        Film film3 = FilmBuilder.builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1895, 12, 29))
                .duration(-1)
                .build();

        assertFalse(valueHasErrorMessage(film, "Продолжительность фильма должна быть положительной."));
        assertFalse(valueHasErrorMessage(film2, "Продолжительность фильма должна быть положительной."));
        assertTrue(valueHasErrorMessage(film3, "Продолжительность фильма должна быть положительной."));
    }
}
