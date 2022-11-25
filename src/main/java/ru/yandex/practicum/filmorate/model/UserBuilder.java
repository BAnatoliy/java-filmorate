package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public final class UserBuilder {
    private Set<Long> friendsId = new HashSet<>();
    private long id;
    private String name;
    private @NotNull @NotBlank String login;
    private @Email @NotNull String email;
    private LocalDate birthday;

    private UserBuilder() {
    }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public UserBuilder friendsId(Set<Long> friendsId) {
        this.friendsId = friendsId;
        return this;
    }

    public UserBuilder id(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder name(String name) {
        this.name = name;
        return this;
    }

    public UserBuilder login(String login) {
        this.login = login;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder birthday(LocalDate birthday) {
        this.birthday = birthday;
        return this;
    }

    public User build() {
        User user = new User(login, email, birthday);
        user.setFriendsId(friendsId);
        user.setId(id);
        user.setName(name);
        return user;
    }
}
