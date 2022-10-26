package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.validator.UserValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@UserValid
@Data
public class User {
    @EqualsAndHashCode.Exclude
    private final Set<Long> friendsId = new HashSet<>();
    @EqualsAndHashCode.Exclude
    private long id;
    @NotNull @NotBlank
    private final String login;
    private String name;
    @Email @NotNull
    private final String email;
    private final LocalDate birthday;

    public User(String login, String name, String email, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public void addFriend(long id) {
        friendsId.add(id);
    }

    public void deleteFriend(long id) {
        if (!friendsId.contains(id)) {
            throw new EntityNotFoundException("User not found!");
        }
        friendsId.remove(id);
    }

    public Set<Long> getFriendsId() {
        return friendsId;
    }
}
