package com.helen.database.data;

import com.helen.commands.CommandData;
import com.helen.commands.CommandResponse;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;

import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DotCommand {

    public static CommandResponse setDotCommand(CommandData data) {
        try {
            String[] tokens = Arrays.stream(data.getMessageWithoutCommand().split("\\|")).map(String::trim).toArray(String[]::new);
            if(tokens.length > 2){
                return new CommandResponse(false, "You should only specify command|message don't include anything else.");
            } else if (tokens[0].contains(".")) {
                return new CommandResponse(false,"Don't specify the . at the beginning of the command (or anywhere else in the command for that matter).  Just give the name you want to use.");
            }
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("addDotCommand"), "." + tokens[0].toLowerCase(), tokens[1]);
            if (stmt.executeUpdate()) {
                return new CommandResponse(true,"*Jots that down on her clipboard* Mmhmm, I'll post it on the notice board.");
            } else {
                return new CommandResponse(false,"Hmm, I didn't quite get that.  Magnus, a word?");
            }
        } catch (Exception e) {
            return new CommandResponse(false,"Hmm, I didn't quite get that.  Magnus, a word?");
        }
    }



    public static Optional<Map<String, String>> getDotCommands() {
        try {

            Map<String, String> dotCommands = new HashMap<>();
            ResultSet rs;

            try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("getDotCommands"))){
                rs = stmt.execute();
                while(rs != null && rs.next()){
                    dotCommands.put(rs.getString("command"), rs.getString("message"));
                }
            }
            return Optional.of(dotCommands);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public static CommandResponse deleteCommand(CommandData data) {
        String command = data.getMessageWithoutCommand().toLowerCase();
        if(!command.startsWith(".")){
            command = "." + command;
        }
        try(CloseableStatement stmt = Connector.getStatement(Queries.getQuery("deleteDotCommand"), command.toLowerCase())) {
            if (stmt != null && stmt.executeDelete()) {
                return new CommandResponse(true,"Command: " + data.getMessageWithoutCommand().toLowerCase().trim() + " was deleted.");

            } else {
                return new CommandResponse(false,"That didn't quite delete properly.  Magnus?");
            }
        } catch (Exception e) {
            return new CommandResponse(false,"Hmm, I didn't quite get that.  Magnus, a word?");
        }
    }
}
