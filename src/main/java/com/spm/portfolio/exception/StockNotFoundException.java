package com.spm.portfolio.exception;

public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) {

        super(message);

    }
}