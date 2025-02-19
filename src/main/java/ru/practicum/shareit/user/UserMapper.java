package ru.practicum.shareit.user;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {
    public static UserDto mapToUserDto(User user) {
        objectNull(user);
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }

    public static User mapToUser(UserDto userDto) {
        objectNull(userDto);
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail());
    }

    public static List<UserDto> mapToUserDto(List<User> users) {
        List<UserDto> dtos = new ArrayList<>();
        for (User user : users) {
            objectNull(user);
            dtos.add(mapToUserDto(user));
        }
        return dtos;
    }

    private static void objectNull(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("user null");
        }
    }
}
