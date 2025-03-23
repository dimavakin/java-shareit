package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class UserMapperTest {

    @Test
    void testMapToUserDto() {
        User user = new User(1L, "John Doe", "john.doe@example.com");
        UserDto userDto = UserMapper.mapToUserDto(user);

        assertThat(userDto.getId()).isEqualTo(user.getId());
        assertThat(userDto.getName()).isEqualTo(user.getName());
        assertThat(userDto.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    void testMapToUserDtoWhenUserIsNull() {
        assertThatThrownBy(() -> UserMapper.mapToUserDto((User) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user null");
    }

    @Test
    void testMapToUser() {
        UserDto userDto = new UserDto(1L, "John Doe", "john.doe@example.com");
        User user = UserMapper.mapToUser(userDto);

        assertThat(user.getId()).isEqualTo(userDto.getId());
        assertThat(user.getName()).isEqualTo(userDto.getName());
        assertThat(user.getEmail()).isEqualTo(userDto.getEmail());
    }

    @Test
    void testMapToUserWhenUserDtoIsNull() {
        assertThatThrownBy(() -> UserMapper.mapToUser(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user null");
    }

    @Test
    void testMapToUserDtoList() {
        User user1 = new User(1L, "John Doe", "john.doe@example.com");
        User user2 = new User(2L, "Jane Doe", "jane.doe@example.com");
        List<User> users = List.of(user1, user2);

        List<UserDto> userDtos = UserMapper.mapToUserDto(users);

        assertThat(userDtos).hasSize(2);
        assertThat(userDtos.get(0).getId()).isEqualTo(user1.getId());
        assertThat(userDtos.get(0).getName()).isEqualTo(user1.getName());
        assertThat(userDtos.get(0).getEmail()).isEqualTo(user1.getEmail());
        assertThat(userDtos.get(1).getId()).isEqualTo(user2.getId());
        assertThat(userDtos.get(1).getName()).isEqualTo(user2.getName());
        assertThat(userDtos.get(1).getEmail()).isEqualTo(user2.getEmail());
    }
}