package com.helen.database;

import org.jibble.pircbot.Colors;

import java.util.ArrayList;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roll implements DatabaseObject {

	private String regex = ".roll\\s([0-9]+)([df])([0-9]+)(\\s[+|-]?[0-9]+)?(\\s-e|-s)?\\s?(-e|-s)?\\s?(.+)?";
	private String diceString;
	private boolean expand = false;
	private Integer diceSize = null;
	private Integer bonus = 0;
	private String diceMessage = "";
	private String dicetype = "u";
	private ArrayList<Integer> values;
	private Integer diceThrows = null;
	private String username;

	public Roll(String diceCommand, String username) {
		diceString = diceCommand;
		parse();
		computeRoll();
		this.username = username;
		values = new ArrayList<>();
	}
	
	public Roll(String diceCommand, String username, String regex) {
		this.regex = regex;
		diceString = diceCommand;
		parse();
		computeRoll();
		this.username = username;
		values = new ArrayList<>();
	}

	public Integer getDiceThrows() {
		return diceThrows;
	}

	public String getUsername() {
		return username;
	}

	public Integer getComputedRoll() {
		Integer sum = 0;
		for (Integer i : values) {
			sum += i;
		}
		return sum + bonus;
	}

	public String getDelimiter() {
		Optional<Config> diceDelimiter = Configs.getSingleProperty("diceDelim");
		return diceDelimiter.isPresent() ? diceDelimiter.get().getValue() : ",";
	}

	public String toString() {
		if (dicetype.equals("d")) {
			StringBuilder str = new StringBuilder();
			if (diceMessage != null && diceMessage.length() > 0) {
				str.append(diceMessage);
				str.append(": ");
			}
			str.append(Colors.BOLD);
			str.append(getComputedRoll());
			str.append(Colors.NORMAL);
			
			
			if (expand) {
				str.append(" Expanded:");
				str.append(getExpanded());
			}else{
				str.append(" (");
				str.append(getDiceThrows());
				str.append(dicetype);
				str.append(diceSize);
				Integer sum = 0;
				for (Integer i : values) {
					sum += i;
				}
				str.append("=");
				str.append(Colors.BOLD);
				str.append(sum);
				str.append(Colors.NORMAL);
				if (bonus != 0) {
					str.append(" ");
					str.append(bonus > 0 ? ("+" + bonus) : "");

					str.append("= ");
					str.append(Colors.BOLD);
					str.append(sum + bonus);
					str.append(Colors.NORMAL);
				}
				str.append(")");
			}

			return str.toString();
		} else {
			return "Fudge dice are currently under development";
		}
	}

	private void parse() {
		Pattern r = Pattern.compile(regex);
		Matcher m = r.matcher(diceString);

		if (m.find()) {
			diceThrows = Integer.parseInt(m.group(1).trim());
			dicetype = m.group(2).trim();
			diceSize = Integer.parseInt(m.group(3).trim());
			if (m.group(4) != null) {
				bonus = Integer.parseInt(m.group(4).trim());
			}

			if (m.group(5) != null) {
				if (m.group(5).trim().equals("-e")) {
					expand = true;
				}
			}

			if (m.group(6) != null) {
				if (m.group(6).trim().equals("-e")) {
					expand = true;
				}
			}
			if (m.group(7) != null) {
				diceMessage = m.group(7).trim();
			}

		}
	}

	private void computeRoll() {
		if(diceThrows <= 20) {
			for (int i = 0; i < diceThrows; i++) {
				int roll = (int) (Math.random() * (diceSize)) + 1;
				values.add(roll);
			}
		}

	}

	public String getExpanded() {
		StringBuilder rollString = new StringBuilder();
		int count = 0;
		for (Integer i : values) {
			if (count++ < 20) {
				rollString.append((rollString.length() == 0) ? i : "," + i);
			} 
		}
		if(count >= 20){
			rollString.append("...truncated to 20 rolls.");
		}
		StringBuilder expandedString = new StringBuilder();
		if (rollString.length() > 0) {

			expandedString.append("[");
			expandedString.append(getDiceThrows());
			expandedString.append(dicetype);
			expandedString.append(diceSize);
			expandedString.append("=");
			expandedString.append(Colors.BOLD);
			expandedString.append(rollString.toString());
			expandedString.append(Colors.NORMAL);
			expandedString.append("|Bonus=");
			expandedString.append(Colors.BOLD);
			expandedString.append(bonus);
			expandedString.append(Colors.NORMAL);
			expandedString.append("]");
		}
		return expandedString.toString();
	}
	
	public boolean displayToUser(){
		return true;
	}

}
