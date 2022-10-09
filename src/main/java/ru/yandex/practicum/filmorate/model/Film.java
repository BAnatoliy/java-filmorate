package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.Duration;
import java.time.LocalDate;

@Data
public class Film {
    @EqualsAndHashCode.Exclude
    private int id;
    @NotNull @NotBlank
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной.")
    private final int duration;
}
