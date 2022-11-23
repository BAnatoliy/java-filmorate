package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    User addUser(User user);
    void deleteUser(long id);
    User updateUser(User user);
    List<User> getUsers();
    User getUserById(Long id);
    void addFriend(long userId, long friendId);
    void deleteFriend(long userId, long friendId);
    List<User> getFriends(long userId);
    List<User> getCommonFriends(long userId, long otherUserId);
}
