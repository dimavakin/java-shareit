package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ItemDto {
    Long id;
    @NotBlank(message = "Название не может быть пустым ")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull(message = "available не может быть пустой")
    Boolean available;
    Long requestId;
    Long userId;
}