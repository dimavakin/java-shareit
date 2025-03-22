package ru.practicum.shareit.booking;

public enum BookingState {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static BookingState fromString(String value) {
        try {
            return BookingState.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестное состояние бронирования: " + value, e);
        }
    }
}
