package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    Long id;
    @NotBlank(message = "Название не может быть пустым ")
    String name;
    @NotBlank(message = "Описание не может быть пустым")
    String description;
    @NotNull(message = "available не может быть пустой")
    Boolean available;
    Long userId;
}
