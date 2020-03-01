package com.example.rest.exceptions;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id) {
        super("Could not found order number " + id);
    }
}
