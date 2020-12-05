package com.helen.database.users;

import com.helen.commands.Command;
import org.apache.commons.lang.StringUtils;
import org.jibble.pircbot.Colors;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roll {

    private static String regex = ".roll\\s([0-9]+)([df])([0-9]+)(\\s[+|-]?[0-9]+)?(\\s-e|-s)?\\s?(-e|-s)?\\s?(.+)?";
    private Integer diceThrows;
    private String result;


    public Roll(String command){

        Pattern r = Pattern.compile(regex);
        Matcher m = r.matcher(command);

        if (m.find()) {
             diceThrows = Integer.parseInt(m.group(1).trim());
            String dicetype = m.group(2).trim();
            int diceSize = Integer.parseInt(m.group(3).trim());
            int bonus = 0;
            int sum = 0;
            String diceMessage = null;
            if (m.group(4) != null) {
                bonus = Integer.parseInt(m.group(4).trim());
            }
            if (m.group(7) != null) {
                diceMessage = m.group(7).trim();
            }
            if (diceThrows <= 20) {
                for (int i = 0; i < diceThrows; i++) {
                    sum += (int) (Math.random() * (diceSize)) + 1;
                }
            }
            if (dicetype.equals("d")) {
                StringBuilder str = new StringBuilder();
                if (!StringUtils.isEmpty(diceMessage)) {
                    str.append(diceMessage).append(": ");
                }
                str.append(Colors.BOLD).append(sum).append(Colors.NORMAL).append(" (").append(diceThrows).append(dicetype);
                str.append(diceSize).append("=").append(Colors.BOLD).append(sum).append(Colors.NORMAL);
                    if (bonus != 0) {
                        str.append(" ").append(bonus > 0 ? ("+" + bonus) : "").append("= ").append(Colors.BOLD).append(sum + bonus).append(Colors.NORMAL);
                    }
                    str.append(")");

                result = str.toString();
            } else {
                result = "Fudge dice are currently under development";
            }
        }
        else{
            result = Command.ERROR;
        }
    }

    public String getResult(){
        return result;
    }

    public Integer getDiceThrows(){
        return diceThrows;
    }



}
