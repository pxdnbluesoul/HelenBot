package com.helen.database;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Roll {

	final static String regex = ".roll\\s([0-9]+)(d|f)([0-9]+)(\\s[+|-]?[0-9]+)?(\\s-e|-s)?\\s?(-e|-s)?\\s?(.+)?";
	private final static Pattern r = Pattern.compile(regex);
	private String diceString = null;
	private Integer diceThrows = null;
	private boolean expand = false;
	private boolean save = false;
	private Integer diceSize = null;
	private Integer bonus = null;
	private String diceMessage = null;
	private String dicetype = "u";
	private Integer computedRoll = null;
	private String expanded = null;

	private String username = null;


	public Roll(String diceCommand, String username) {
		diceString = diceCommand;
		parse();
		computeRoll();
		this.username = username;
	}
	
	public Roll(Integer diceThrows,String diceType, Integer diceSize,
			Integer bonus, String diceMessage, Integer computedRoll,
			String expanded, String username){
		this.diceThrows = diceThrows;
		this.dicetype = diceType;
		this.diceSize = diceSize;
		this.bonus = bonus;
		this.diceMessage = diceMessage;
		this.computedRoll = computedRoll;
		this.expanded = expanded;
		this.username = username;
		this.expand = true;
	}

	public static String getRegex() {
		return regex;
	}

	public static Pattern getR() {
		return r;
	}

	public String getDiceString() {
		return diceString;
	}

	public Integer getDiceThrows() {
		return diceThrows;
	}

	public boolean isExpand() {
		return expand;
	}

	public boolean isSave() {
		return save;
	}

	public Integer getDiceSize() {
		return diceSize;
	}

	public Integer getBonus() {
		return bonus;
	}

	public String getDiceMessage() {
		return diceMessage;
	}

	public String getDicetype() {
		return dicetype;
	}

	public String getUsername() {
		return username;
	}

	public Integer getComputedRoll() {
		return computedRoll;
	}

	public String getExpanded() {
		return expanded;
	}

	public String toString() {
		if (dicetype.equals("d")) {
			StringBuilder str = new StringBuilder();
			if (diceMessage != null) {
				str.append(diceMessage);
				str.append(": ");
			}
			str.append(diceThrows);
			str.append(dicetype);
			str.append(diceSize);
			if (bonus != null) {
				str.append(" ");
				str.append(bonus > 0 ? "+" + bonus : bonus);
			}
			str.append(", total: ");
			str.append(computedRoll);
			str.append(".");
			if (expand) {
				str.append(" Expanded:");
				str.append(expanded);
			}

			return str.toString();
		} else {
			return "Fudge dice are currently under development";
		}
	}

	private void parse() {

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
				} else if (m.group(5).trim().equals("-s")) {
					save = true;
				}
			}

			if (m.group(6) != null) {
				if (m.group(6).trim().equals("-e")) {
					expand = true;
				} else if (m.group(6).trim().equals("-s")) {
					save = true;
				}
			}
			if (m.group(7) != null) {
				diceMessage = m.group(7).trim();
			}

		}
	}

	private Integer computeRoll() {
		if (computedRoll == null) {
			StringBuilder rolls = new StringBuilder();
			Integer rollSum = 0;

			for (int i = 0; i < diceThrows; i++) {
				int roll = (int) (Math.random() * (diceSize - 1)) + 1;
				if (i < 20) {
					rolls.append((rolls.length() == 0) ? roll : "," + roll);
				} else {
					rolls.append("...truncated to 20 rolls.");
				}
				rollSum += roll;
			}

			if (rolls.length() > 0) {
				expanded = "[" + diceThrows + dicetype + diceSize +"=" + rolls.toString() + "|" + (bonus == null ? "" : bonus) + "]";
			}

			rollSum += bonus;
			computedRoll = rollSum;
		}
		return computedRoll;
	}

}
