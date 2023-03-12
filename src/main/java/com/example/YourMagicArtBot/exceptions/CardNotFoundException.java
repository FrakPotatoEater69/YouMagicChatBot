package com.example.YourMagicArtBot.exceptions;

public class CardNotFoundException extends RuntimeException {
    public CardNotFoundException() {
        super("There is no cards found. Maybe someone already drop database or it's just empty, ok?");
    }
}
