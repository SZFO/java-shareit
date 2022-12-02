package ru.practicum.shareit.exception;

public class IncorrectStateException extends RuntimeException {
    public IncorrectStateException(String message) {
        super(message);
    }
}