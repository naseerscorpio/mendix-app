package com.mendix.demo.exception;

/**
 * Created by naseers on 14/04/2018.
 */
public class MendixAppException extends Exception {

    public MendixAppException(String message) {
        super(message);
    }

    public MendixAppException(Exception ex) {
        super(ex);
    }
}
