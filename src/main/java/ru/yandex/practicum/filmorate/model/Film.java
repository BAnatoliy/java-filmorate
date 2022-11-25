package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.validator.FilmValid;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.*;

@FilmValid
@Data
public class Film {
    private Set<Long> idUsersLike = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private long id;
    @NotNull @NotBlank
    private final String name;
    @Size(max = 200)
    private final String description;
    private final LocalDate releaseDate;
    @Min(0)
    private final int duration;
    private final int rate;
    private Mpa mpa;
    private Set<Genre> genres = new TreeSet<>(Comparator.comparing(Genre::getId));

    public void addLike(long id) {
        idUsersLike.add(id);
    }
    public void deleteLike(long id) {
        if (!idUsersLike.contains(id)) {
            throw new EntityNotFoundException("Film not found!");
        }
        idUsersLike.remove(id);
    }

    public Map<String,Object> toMap() {
        Map <String, Object> values = new HashMap<>();
        values.put("FILM_NAME", name);
        values.put("RELEASE_DATE", releaseDate);
        values.put("DESCRIPTION", description);
        values.put("DURATION", duration);
        values.put("RATE", rate);
        values.put("MPA_ID", mpa.getId());
        return values;
    }
}
