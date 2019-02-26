package com.crossover.techtrial.exceptions;

public class NotFoundException extends IllegalArgumentException {
    public NotFoundException(String msg) {
        super(msg);
    }
}
