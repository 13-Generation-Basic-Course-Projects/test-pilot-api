package com.both.testing_pilot_backend.exceptions;

public class DuplicateRecord extends RuntimeException{
    public DuplicateRecord(String message) {
        super(message);
    }
}
