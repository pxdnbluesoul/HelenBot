package com.helen.commands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RollData {

	final static String regex = ".roll\\s([0-9]+)(d|f)([0-9]+)(\\s[+|-]?[0-9]+)?(\\s-e|-s)?\\s?(-e|-s)?\\s?(.+)?";
	private final static Pattern r = Pattern.compile(regex);

	private String diceString = null;

	private Integer diceThrows = 0;
	private Integer diceSize = 0;
	private Integer bonus = 0;
	private String diceMessage = null;

	private Long computedRoll = null;

	private String expanded = null;

	private boolean expand = false;
	private boolean save = false;

	private enum DICETYPE {
		d, f, unknown
	};

	private DICETYPE dicetype = DICETYPE.unknown;

	public RollData(String diceCommand) {
		diceString = diceCommand;
		parse();
		computeRoll();
	}

	public boolean save() {
		return save;
	}

	public String getRoll() {
		if (dicetype == DICETYPE.d) {
			StringBuilder str = new StringBuilder();
			str.append(diceMessage);
			str.append(":");
			str.append(diceThrows);
			str.append(dicetype);
			str.append(diceSize);
			str.append(" ");
			str.append(bonus > 0 ? "+" + bonus : bonus);
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
			dicetype = DICETYPE.valueOf(m.group(2).trim());
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

	private Long computeRoll() {
		if (computedRoll == null) {
			StringBuilder rolls = new StringBuilder();
			Long rollSum = 0l;

			for (int i = 0; i < diceThrows; i++) {
				int roll = (int) (Math.random() * (diceSize - 1)) + 1;
				if (expand) {
					if (i < 20) {
						rolls.append((rolls.length() == 0) ? roll : "," + roll);
					} else {
						rolls.append("...truncated to 20 rolls.");
					}
				}
				rollSum += roll;
			}

			if (expand && rolls.length() > 0) {
				expanded = "[" + rolls.toString() + "]";
			}

			rollSum += bonus;
			computedRoll = rollSum;
		}
		return computedRoll;
	}

}
