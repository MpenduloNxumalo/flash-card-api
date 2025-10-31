package com.example.rest.webservices.flash_card_api.exceptions;

public class NotFoundException extends Exception{
    public NotFoundException() {
    }

    public NotFoundException(String message) {
        super(message);
    }
}
