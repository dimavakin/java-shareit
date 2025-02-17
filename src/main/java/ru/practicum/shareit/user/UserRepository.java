package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> findAll();

    User save(User user);

    User updateUser(User newUser, Long userId);

    User getUser(Long userId);

    User delete(Long id);

    boolean checkUserExists(Long id);
} 