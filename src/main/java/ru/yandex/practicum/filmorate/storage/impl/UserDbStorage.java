package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserBuilder;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Qualifier("userDbStorage")
@Slf4j
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public User addUser(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        String sql = "insert into USERS (USER_LOGIN, USER_NAME, EMAIL, BIRTHDAY) " +
                "values (?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());
        log.debug("User created");

        String sql2 = "select * from USERS where USER_LOGIN = ? and USER_NAME = ? and EMAIL = ? and BIRTHDAY = ?";
        return jdbcTemplate.queryForObject(sql2, this::mapRowToUser, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday());//-
    }

    @Override
    public void deleteUser(long id) {
        String sql = "delete from USERS where USER_ID = ?";
        boolean isDelete = jdbcTemplate.update(sql, id) > 0;
        if (!isDelete) {
            throw new EntityNotFoundException("User not found!");
        }

        String sql2 = "delete from FRIENDS where USER_ID = ?";
        jdbcTemplate.update(sql2, id);

        log.debug("User with id: {} deleted", id);
    }

    @Override
    public User updateUser(User user) {
        String sql = "update USERS set USER_LOGIN = ?, USER_NAME = ?, EMAIL = ?, BIRTHDAY = ? where USER_ID = ? ";
        boolean isUpdate = jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(), user.getBirthday(), user.getId()) > 0;
        if (isUpdate) {
            log.debug("User with id: {} updated", user.getId());
        } else {
            throw new EntityNotFoundException("User not found!");
        }
        return getUserById(user.getId());
    }

    @Override
    public List<User> getUsers() {
        String sql = "select * from USERS order by USER_ID";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser);
        users.forEach(user -> user.setFriendsId(new HashSet<>(getListOfFriendIds(user.getId()))));
        log.debug("Get all users");
        return users;
    }

    @Override
    public User getUserById(Long id) {
        String sql = "select * from USERS where USER_ID = ?";
        User user = jdbcTemplate.queryForObject(sql, this::mapRowToUser, id);
        if(user == null) {
            log.debug("User with ID {} not found", id);
            throw new EntityNotFoundException("User not found");
        }
        List<Long> friendIds = getListOfFriendIds(user.getId());
        user.setFriendsId(new HashSet<>(friendIds));
        log.debug("Get user with ID {}", id);
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "insert into FRIENDS (USER_ID, FRIEND_ID) " +
                "values (?, ?)";
        jdbcTemplate.update(sql, userId, friendId);
        log.debug("Users with ID {}, {} are friends now!", userId, friendId);
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        boolean isDelete = jdbcTemplate.update(sql, userId, friendId) > 0;
        if (!isDelete) {
            throw new EntityNotFoundException("User not found!");
        }
        log.debug("User with id: {} deleted friend with id: {}", userId, friendId);
    }

    @Override
    public List<User> getFriends(long userId) {
        List<Long> friendIds = getListOfFriendIds(userId);
        return friendIds.stream().map(this::getUserById).collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(long userId, long otherUserId) {
        String sql = "select * from USERS as U join (select FRIEND_ID from FRIENDS where USER_ID = ?) as F " +
                " on U.USER_ID = F.FRIEND_ID join (select FRIEND_ID from FRIENDS where USER_ID = ?) as FO " +
                "on U.USER_ID = FO.FRIEND_ID";
        List<User> users = jdbcTemplate.query(sql, this::mapRowToUser, userId, otherUserId);
        users.forEach(user -> user.setFriendsId(new HashSet<>(getListOfFriendIds(user.getId()))));
        return users;
    }

    private List<Long> getListOfFriendIds(long userId) {
        String sql = "select * from FRIENDS where USER_ID = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> resultSet.getLong("FRIEND_ID"), userId);
    }

    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return UserBuilder.builder()
                .id(resultSet.getLong("USER_ID"))
                .login(resultSet.getString("USER_LOGIN"))
                .name(resultSet.getString("USER_NAME"))
                .email(resultSet.getString("EMAIL"))
                .birthday(resultSet.getDate("BIRTHDAY").toLocalDate())
                .build();
    }
}
