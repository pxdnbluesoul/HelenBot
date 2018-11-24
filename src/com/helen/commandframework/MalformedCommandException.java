package com.helen.commandframework;

class MalformedCommandException extends Exception {

    public MalformedCommandException(String errorMessage){
        super(errorMessage);
    }
}
