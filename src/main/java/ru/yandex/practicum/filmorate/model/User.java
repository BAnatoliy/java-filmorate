package ru.yandex.practicum.filmorate.model;

import lombok.*;
import validator.UserValid;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@UserValid
@Data
public class User {
    @EqualsAndHashCode.Exclude
    private int id;
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
}
