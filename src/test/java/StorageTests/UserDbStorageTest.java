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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserBuilder;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserDbStorageTest {
    private final UserDbStorage userStorage;

    void add_Users_And_Add_Friends() {
        User newUser1 = UserBuilder.builder()
                .name("User1")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser1);
        User newUser2 = UserBuilder.builder()
                .name("User2")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser2);

        User newUser3 = UserBuilder.builder()
                .name("User3")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser3);

        User newUser4 = UserBuilder.builder()
                .name("User4")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();
        userStorage.addUser(newUser4);

        userStorage.addFriend(1L, 2L);
        userStorage.addFriend(1L, 3L);
        userStorage.addFriend(1L, 4L);
    }

    @Sql({"classpath:/schema.sql", "classpath:/data.sql"})
    @Order(1)
    @Test()
    void add_User_Test() {
        add_Users_And_Add_Friends();
        User newUser = UserBuilder.builder()
                .name("asd")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        Optional<User> userOptional = Optional.ofNullable(userStorage.addUser(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 5L)
                                .hasFieldOrPropertyWithValue("name", "asd")
                );

        Optional<User> userOptionalById = Optional.ofNullable(userStorage.getUserById(5L));
        assertThat(userOptionalById)
                .isPresent()
                .hasValueSatisfying((user -> assertThat(user).hasFieldOrPropertyWithValue("id", 5L)
                        .hasFieldOrPropertyWithValue("name", "asd")));
    }

    @Order(2)
    @Test
    void should_Updated_User_When_Id_Correct() {
        User newUser = UserBuilder.builder()
                .id(1L)
                .name("NewUser")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        Optional<User> userOptional = Optional.ofNullable(userStorage.updateUser(newUser));
        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "NewUser")
                );

        Optional<User> userOptionalById = Optional.ofNullable(userStorage.getUserById(1L));
        assertThat(userOptionalById)
                .isPresent()
                .hasValueSatisfying((user -> assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("name", "NewUser")));
    }

    @Order(3)
    @Test
    void should_Not_Updated_User_When_Id_Wrong() {
        User newUser = UserBuilder.builder()
                .id(555L)
                .name("NewUser")
                .login("qw")
                .email("qw@yandex.ru")
                .birthday(LocalDate.of(1995, 12, 27))
                .build();

        assertThrows(EntityNotFoundException.class,
                () -> userStorage.updateUser(newUser));
    }

    @Order(4)
    @Test
    void getUsersTest() {
        Optional<List<User>> userListAfterDeleted = Optional.ofNullable(userStorage.getUsers());
        assertThat(userListAfterDeleted).isPresent().hasValueSatisfying(users -> assertEquals(5, users.size()));
    }

    @Order(5)
    @Test
    void should_get_User_By_Id_When_Id_Correct() {
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(2L));
        assertThat(userOptional).isPresent().hasValueSatisfying(user -> assertThat(user)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("name", "User2"));
    }

    @Order(6)
    @Test
    void should_get_User_By_Id_When_Id_Wrong() {
        assertThrows(EmptyResultDataAccessException.class, () -> userStorage.getUserById(555L));
    }

    @Order(7)
    @Test
    void should_Not_Deleted_User_When_Id_Wrong() {
        Optional<List<User>> userListBeforeDeleted = Optional.ofNullable(userStorage.getUsers());
        assertThat(userListBeforeDeleted).isPresent().hasValueSatisfying(users -> assertEquals(5, users.size()));

        assertThrows(EntityNotFoundException.class, () -> userStorage.deleteUser(555L));

        Optional<List<User>> userListAfterDeleted = Optional.ofNullable(userStorage.getUsers());
        assertThat(userListAfterDeleted).isPresent().hasValueSatisfying(users -> assertEquals(5, users.size()));
    }

    @Order(8)
    @Test
    void should_Deleted_User_When_Id_Correct() {
        Optional<List<User>> userListBeforeDeleted = Optional.ofNullable(userStorage.getUsers());
        assertThat(userListBeforeDeleted).isPresent().hasValueSatisfying(users -> assertEquals(5, users.size()));

        userStorage.deleteUser(5L);

        Optional<List<User>> userListAfterDeleted = Optional.ofNullable(userStorage.getUsers());
        assertThat(userListAfterDeleted).isPresent().hasValueSatisfying(users -> assertEquals(4, users.size()));
    }

    @Order(9)
    @Test
    void should_Add_Friend_Id3_For_User_Id2_And_Get_Friends_Users_Id2() {
        userStorage.addFriend(2L, 3L);
        userStorage.addFriend(2L, 4L);
        Optional<List<User>> userOptional = Optional.ofNullable(userStorage.getFriends(2L));
        assertThat(userOptional).isPresent().hasValueSatisfying(users -> {
            assertThat(users).hasSize(2);
            assertThat(users.get(0).getId()).isEqualTo(3L);
            assertThat(users.get(1).getId()).isEqualTo(4L);
        });
    }

    @Order(10)
    @Test
    void should_Not_Add_Friend_With_Wrong_Id_For_User_With_Wrong_Id() {
        assertThrows(DataIntegrityViolationException.class, () -> userStorage.addFriend(222L, 3L));
        assertThrows(DataIntegrityViolationException.class, () -> userStorage.addFriend(2L, 4444L));
    }

    @Order(11)
    @Test
    void should_Delete_Friend_Id4_Users_Id2() {
        userStorage.deleteFriend(2L, 4L);
        Optional<List<User>> userOptional = Optional.ofNullable(userStorage.getFriends(2L));
        assertThat(userOptional).isPresent().hasValueSatisfying(users -> {
            assertThat(users).hasSize(1);
            assertThat(users.get(0).getId()).isEqualTo(3L);
        });
    }

    @Order(12)
    @Test
    void should_Not_Delete_Friend_With_Wrong_Id_Users_With_Wrong_Id() {
        assertThrows(EntityNotFoundException.class, () -> userStorage.deleteFriend(222L, 3L));
        assertThrows(EntityNotFoundException.class, () -> userStorage.deleteFriend(2L, 4444L));
    }

    @Order(13)
    @Test
    void should_Not_Get_Friends_Users_With_WrongId() {
        Optional<List<User>> userOptional = Optional.ofNullable(userStorage.getFriends(222L));
        assertThat(userOptional).isPresent().hasValueSatisfying(users -> assertThat(users).isEmpty());
    }

    @Order(14)
    @Test
    void should_Get_Common_Friends_User_Id1_Add_User_Id2() {
        Optional<List<User>> commonFriends = Optional.ofNullable(userStorage.getCommonFriends(1L, 2L));
        assertThat(commonFriends).isPresent().hasValueSatisfying(users -> {
            assertThat(users).hasSize(1);
            assertThat(users.get(0).getId()).isEqualTo(3);
        });
    }

    @Order(15)
    @Test
    void should_Not_Get_Common_Friends_Users_With_Wrong_Ids() {
        Optional<List<User>> userOptional = Optional.ofNullable(userStorage.getCommonFriends(222L, 3L));
        assertThat(userOptional).isPresent().hasValueSatisfying(users -> assertThat(users).isEmpty());Optional<List<User>> userOptional2 = Optional.ofNullable(userStorage.getCommonFriends(2L, 4444L));
        assertThat(userOptional2).isPresent().hasValueSatisfying(users -> assertThat(users).isEmpty());
    }
}