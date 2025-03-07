package com.example.virtualwallet.exceptions;

public class EmptyPageException extends RuntimeException {
    public EmptyPageException() {
        super("There is no content found on this page!");
    }
}
