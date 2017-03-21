package com.helen.bots;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class PropertiesManager {

	private static boolean propsOpen = false;
	private static Properties props = new Properties();
	private static InputStream propsIn;
	
	public static String getProperty(String key) {
		if (!propsOpen) {
			openProperties();
		}
		return props.getProperty(key) == null ? "" : props.getProperty(key);
	}

	public static ArrayList<String> getPropertyList(String key) {
		return new ArrayList<String>(Arrays.asList(getProperty(key).split(",")));
	}

	private static void openProperties() {
		// open file for reading
		if(propsOpen){
			closeProperties();
		}
		try {
			propsIn = HelenBot.class.getResourceAsStream("/Helen.properties");
			props.load(propsIn);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			String currentDirectory = new File("").getAbsolutePath();
			System.out.println(currentDirectory);

		} catch (Exception e) {
			e.printStackTrace();

		}
		propsOpen = true;
	}
	
	private static void closeProperties() {
		try {
			propsIn.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		propsOpen = false;
	}
	
}
