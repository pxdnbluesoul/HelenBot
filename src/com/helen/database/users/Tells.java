package com.helen.database.users;

import com.helen.commands.Command;
import com.helen.commands.CommandData;
import com.helen.database.framework.CloseableStatement;
import com.helen.database.framework.Connector;
import com.helen.database.framework.Queries;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tells {

    private final static Logger logger = Logger.getLogger(Tells.class);

    public static String sendMassTell(CommandData data){
        String[] message = data.getSplitMessage();
        if(Arrays.stream(message).noneMatch(s -> s.equalsIgnoreCase("|"))){
            return "I'm sorry I think you may have the wrong address....please use .masstell name1 name2 name3 | message";
        }else{
            List<String> targets = new ArrayList<>();
            int i = 1;
            for(; i < message.length; i++){
                if(!message[i].equalsIgnoreCase("|")){
                    targets.add(message[i]);
                }else{
                    break;
                }
            }
            targets.forEach(target -> sendMultiTell(data.getSender(),
                    target.replace(",","").trim(),
                    data.getMessage().split("\\|")[1].trim(),
                    data.getChannel().isEmpty()) );
            return "I've sent the message to the following users: " + String.join(" ", targets);
        }
    }

    public static String sendMultiTell(String sender, String target, String message, boolean privateMessage){
        if(message.length() > 400){
            return "I'm sorry, I only send tells of four hundred or less characters.  Please shorten your message.";
        }
        Integer id = Nicks.getNickGroup(target);
        if (id == null || id == -1) {
            return sendTell(target, sender, message, privateMessage);
        } else {
            return sendTell(id.toString(), sender, message, privateMessage);
        }
    }

    public static String sendMultitell(CommandData data) {
        String sender = data.getSender().toLowerCase();
        String target = data.getTarget().toLowerCase();
        String message = data.getTellMessage();
        boolean privateMessage = data.getChannel().isEmpty();
        if(StringUtils.isEmpty(target) || StringUtils.isEmpty(target.trim())){
            return "I think you may have messed up the command...";
        }else {
            return sendMultiTell(sender, target, message, privateMessage);
        }
    }

    public static String sendTell(String target, String sender, String message, boolean privateMessage) {
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("insertTell"),
                    target.toLowerCase(),
                    sender,
                    message,
                    privateMessage);
            if (stmt.executeUpdate()) {
                return "*Jots that down on her clipboard* Got it, I'll let them know...";
            }
        } catch (Exception e) {
            logger.error("Exception sending tell", e);
        }
        return Command.ERROR;
    }

    public static ArrayList<Tell> getTells(String username) {
        ArrayList<Tell> list = new ArrayList<>();
        try {
            CloseableStatement stmt = Connector.getStatement(Queries.getQuery("searchTells"),
                    username.toLowerCase(), username.toLowerCase());
            ResultSet rs = stmt.executeQuery();
            while (rs != null && rs.next()) {
                list.add(new Tell(rs.getString("sender"), username
                        , rs.getTimestamp("tell_time"), rs.getString("message")));
            }
            stmt.close();
            return list;
        } catch (Exception e) {
            logger.error("Exception retreiving tells", e);
        }
        return list;
    }

    public static void clearTells(String username) {
        try {
            Connector.getStatement(Queries.getQuery("clearTells"), username.toLowerCase(), username.toLowerCase()).executeUpdate();
        } catch (Exception e) {
            logger.error("Exception clearing tells", e);
        }
    }

}
