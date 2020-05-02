package com.sasu.chatserver.excepions;

public class ElementNotPresentException extends RuntimeException {
    public ElementNotPresentException(String message) {
        super(message);
    }
}
