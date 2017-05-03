
package com.irc.helen;

import org.apache.xmlrpc.XmlRpcException;

public class TestMain {

	private static final Long YEARS = 1000 * 60 * 60 * 24 * 365l;
	private static final Long DAYS = 1000 * 60 * 60 * 24l;
	private static final Long HOURS = 1000 * 60l * 60;
	private static final Long MINUTES = 1000 * 60l;
	

	public static void main(String args[]){
		try {
			Long t = System.currentTimeMillis();
			
			System.out.println(findTime(t - YEARS * 2));
			System.out.println(findTime(t - DAYS * 4));
			System.out.println(findTime(t - HOURS * 1));
			System.out.println(findTime(t - MINUTES * 2));
			System.out.println(findTime(t - (1000 * 55)));
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static String findTime(Long time){
		time = System.currentTimeMillis() - time;
		Long diff = 0l;
		if(time >= YEARS){
			diff = time/YEARS;
			return (time/YEARS) + " year" + (diff > 1 ? "s" : "") + " ago by ";
		}else if( time >= DAYS){
			diff = time/DAYS;
			return (time/DAYS) + " day" + (diff > 1 ? "s" : "") + " ago by ";
		}else if(time >= HOURS){
			diff = (time/HOURS);
			return (time/HOURS) + " hour" + (diff > 1 ? "s" : "") + " ago by ";
		}else if( time >= MINUTES){
			diff = time/MINUTES;
			return (time/MINUTES) + " minute" + (diff > 1 ? "s" : "") + " ago by ";
		}else{
			return "A few seconds ago ";
		}
	
	}
	
}
