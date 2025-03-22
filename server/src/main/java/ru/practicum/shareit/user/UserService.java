package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {
    List<UserDto> getAllUsers();

    UserDto getUser(Long userId);

    UserDto saveUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, Long userId);

    UserDto delete(Long id);
}