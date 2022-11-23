package StorageTests;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = FilmorateApplication.class)
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MpaDbStorageTest {
    private final MpaDbStorage mpaDbStorage;

    @Test
    void get_All_Mpa_Test() {
        Optional<List<Mpa>> mpaList = Optional.ofNullable(mpaDbStorage.getAllMpa());
        assertThat(mpaList).isPresent().hasValueSatisfying(mpas -> {
            assertEquals(5, mpas.size());
            assertEquals("G", mpas.get(0).getName());
            assertEquals("PG", mpas.get(1).getName());
            assertEquals("PG-13", mpas.get(2).getName());
            assertEquals("R", mpas.get(3).getName());
            assertEquals("NC-17", mpas.get(4).getName());
        });
    }

    @Test
    void should_Get_Genre_By_Correct_Id() {
        Optional<Mpa> mpaOptional = Optional.ofNullable(mpaDbStorage.getMpaById(3L));
        assertThat(mpaOptional).isPresent().hasValueSatisfying(mpa -> assertThat(mpa)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("name", "PG-13"));
    }

    @Test
    void should_Get_Genre_By_Wrong_Id() {
        assertThrows(EmptyResultDataAccessException.class, () -> mpaDbStorage.getMpaById(333L));
    }
}