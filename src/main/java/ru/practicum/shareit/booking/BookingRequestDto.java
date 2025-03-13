package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BookingRequestDto {
    @NotBlank(message = "Время начала должно быть указано")
    LocalDateTime start;

    @NotNull(message = "Время окончания должно быть указано")
    LocalDateTime end;

    @NotBlank(message = "Время окончания должно быть указано")
    Long itemId;

    Long bookerId;
}
