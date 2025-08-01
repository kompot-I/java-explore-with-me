package ru.practicum.explorewithme.main.user.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.explorewithme.main.user.model.User;

import java.util.Collection;
import java.util.List;

@UtilityClass
public class UserMapper {
    public UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public List<UserDto> toDto(Collection<User> users) {
        return users.stream()
                .map(UserMapper::toDto)
                .toList();
    }

    public User toUser(NewUserDto dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public UserShortDto toUserShortDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }
}
