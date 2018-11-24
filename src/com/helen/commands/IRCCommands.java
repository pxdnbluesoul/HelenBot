package com.helen.commands;

import com.helen.commandframework.CommandClass;
import com.helen.commandframework.CommandData;
import com.helen.commandframework.CommandResponse;
import com.helen.commandframework.IRCCommand;
import com.helen.database.Hugs;

class IRCCommands implements CommandClass {

    //private int bullets = 6;


    @IRCCommand(command = { ".ch", ".choose" }, arguments = "A list of comma separated choices", description = "Make the bot choose for you", securityLevel = 1)
    public CommandResponse choose(CommandData data) {
        String[] choices = data.getMessage().substring(data.getMessage().indexOf(" ")).split(",");
        return new CommandResponse(data.getSender() + ": " + choices[((int) (Math.random() * (choices.length - 1)) + 1)]);
    }

    @IRCCommand(command = {".hugme"}, coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse hugMe(CommandData data){
        if(data.isHugList()){
            return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + Hugs.storeHugmessage(data));
        }else{
            return new CommandResponse(data.getResponseTarget(), data.getSender() + ": You're not authorized to do that.");
        }
    }

    @IRCCommand(command={".hugHelen",".helenhug",".hugsplox"}, coexistWithJarvis = true, securityLevel = 1)
    public CommandResponse hug(CommandData data){
        if(data.isHugList()){
            return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + Hugs.getHugMessage(data.getSender().toLowerCase()));
        }else if(data.isWhiteList()){
            return new CommandResponse( "hugs " + data.getSender() +".", CommandResponse.ResponseType.Action);
        }else{
            String[] messages = new String[]{
                    "Thank you for the display of affection.",
                    "*Click* Please remove your hands from my cylinders.",
                    "Hugging a revolver must be difficult.",
                    "You're smudging my finish.",
                    "*Sigh* And I just calibrated my sights...",
                    "I'm not sure why you're hugging me but...thank...you?",
                    "Yes...Human emotion.  This is...nice.  Please let go of me."};

            return new CommandResponse(data.getResponseTarget(), data.getSender() + ": " + messages[((int) (Math.random() * (messages.length - 1)))]);
        }
    }

    //These are surprisingly hard to implement for a joke
/*
    @IRCCommand(command = ".shoot", securityLevel = 4, coexistWithJarvis = true)
    public CommandResponse shootUser(CommandData data) {
        if(Configs.commandEnabled(data, "shoot")){
            if(data.getTarget().equalsIgnoreCase("Secretary_Helen")){
                bullets--;
                return new CommandResponse("shoots " + data.getSender(), CommandResponse.ResponseType.Action);
                if(bullets < 1){
                    reload(data);
                }
            }else{
                bullets--;
                if(bullets < 1){
                    reload(data);
                }
                return new CommandResponse("Be careful " + data.getTarget() + ". I still have " +
                        (bullets > 1 ? bullets + " bullets left." : "one in the chamber."), CommandResponse.ResponseType.Action,"shoots " + data.getTarget());
            }
        }
    }

    @IRCCommand(command = ".reload", securityLevel = 4)
    public CommandResponse reload(CommandData data) {
        bullets = 6;
        return new CommandResponse("reloads all six cylinders.", CommandResponse.ResponseType.Action);
    }

    @IRCCommand(command = ".unload", securityLevel = 4, coexistWithJarvis = true)
    public CommandResponse unload(CommandData data) {
        if(Configs.commandEnabled(data, "shoot")){
            if(data.getTarget().equalsIgnoreCase("Secretary_Helen")){
                bullets--;
                if(bullets < 1){
                    reload(data);
                }
                return new CommandResponse("shoots " + data.getSender(), CommandResponse.ResponseType.Action);
            }else{
                return new CommandResponse("Stay out of the revolver's sights.", CommandResponse.ResponseType.Action,"calmly thumbs back the hammer and unleashes"
                        + (bullets == 6 ? " all six cylinders on " : " the remaining " + bullets + " cylinders on ")
                        + data.getTarget() + ".");
                reload(data);
            }
        }
    }*/

    @IRCCommand(command = ".discord", securityLevel = 4, coexistWithJarvis = true)
    public CommandResponse showDiscordMessage(CommandData data){
        return new CommandResponse("There are currently no plans for an official SCP Discord." +
                " Staff feel that, at this time, the benefits of Discord do not outweigh the difficulties of moderation," +
                " and the resulting fracturing between IRC and Discord. There are also several concerns about " +
                "the technical and financial viability of discord.");
    }
}
