package com.helen.commandframework;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandResponse {

    public enum ResponseType{Message,Action}

    private String message;
    private List<String> commands;
    private ResponseType type = ResponseType.Message;

    public String getMessage(){
        return message;
    }

    public List<String> getCommands() {
        return commands;
    }

    public ResponseType getType(){
        return type;
    }

    public CommandResponse(){

    }

    public CommandResponse(String message, ResponseType type){
        this.message = message;
        this.type = type;
    }

    public CommandResponse(String message, ResponseType type, String actionMessage){
        this.message = message;
        this.type = type;
        String actionMessage1 = actionMessage;
    }

    public CommandResponse(String message, String... commands){
        this.message = message;
        this.commands = Arrays.asList(commands);
    }

    public CommandResponse(String message){
        this.message = message;
        this.commands = new ArrayList<>();
    }
}
