package com.helen.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.log4j.Logger;

public class Configs {

	private static final Logger logger = Logger.getLogger(Configs.class);

	private static HashMap<String, ArrayList<Config>> cachedProperties = new HashMap<String, ArrayList<Config>>();
	private static Boolean cacheValid = false;
	private final static String kvQuery = "select * from properties";
	private final static String keysQuery = "select distinct key from properties where public = true";
	private final static String propertySet = "insert into properties (key, value, updated, public) values (?,?,?,?)";
	private final static String updateCheck = "select count(*) as counted from properties where key like ?";
	private final static String updatePush = "update properties set value = ?, public = ? where key like ?";
	private final static String deleteConfig = "delete from properties where key = ? and value = ?";

	public static ArrayList<Config> getProperty(String key) {
		if (!cacheValid) {
			loadProperties();
		}
		if (cachedProperties.containsKey(key)) {
			return cachedProperties.get(key);
		} else {
			return null;
		}
	}

	public static Config getSingleProperty(String key) {
		if (!cacheValid) {
			loadProperties();
		}
		if (cachedProperties.containsKey(key)) {
			return cachedProperties.get(key).get(0);
		} else {
			return null;
		}
	}

	public static String setProperty(String key, String value, String publicFlag) {
		Connection conn = Connector.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(propertySet);
			stmt.setString(1, key);
			stmt.setString(2, value);
			stmt.setDate(3, new java.sql.Date(System.currentTimeMillis()));
			stmt.setBoolean(4, publicFlag.equals("t") ? true : false);
			int updated = stmt.executeUpdate();
			if (updated > 0) {
				cacheValid = false;
				return "Property " + key + " has been set to " + value;
			} else {
				return "Failure to set new property, please check the logs.";
			}
		} catch (SQLException e) {
			logger.error("Exception attempting to set property", e);
		} finally {
			try {
				conn.close();
			} catch (NullPointerException e) {
				// nothing to do here!
			} catch (Exception e) {
				logger.error("There was an exception closing result set", e);
			}
		}

		return "There was an error during the update process.  Please check the logs.";
	}

	public static String updateSingle(String key, String value, String publicFlag) {
		Connection conn = Connector.getConnection();
		try {
			PreparedStatement stmt = conn.prepareStatement(updateCheck);
			stmt.setString(1, key);

			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				if (rs.getInt("counted") > 1) {
					return "That property has multiple values.  Please contact Dr. Magnus to have it modified.";
				} else if (rs.getInt("counted") < 1) {
					return "That property currently is not set.  This operation doesn't support insertion.";
				} else {
					PreparedStatement updatestmt = conn.prepareStatement(updatePush);
					updatestmt.setString(1, value);
					updatestmt.setBoolean(2, publicFlag.equals("t") ? true : false);
					updatestmt.setString(3, key);

					int i = updatestmt.executeUpdate();
					if (i > 0) {
						cacheValid = false;
						return "Updated " + key + " to value " + value;
					} else {
						return "I'm sorry, there was an error updating the key specified.";
					}
				}
			} else {
				logger.error("Exception attempting to set property.  The returned result set had no values.");
			}
		} catch (SQLException e) {
			logger.error("Exception attempting to set property", e);
		} finally {
			try {
				conn.close();
			} catch (NullPointerException e) {
				// nothing to do here!
			} catch (Exception e) {
				logger.error("There was an exception closing result set", e);
			}
		}

		return "There was an error during the update process.  Please check the logs.";
	}

	public static String removeProperty(String key, String value) {
		Connection conn = Connector.getConnection();
		boolean okayToDelete = false;
		if (cachedProperties.containsKey(key)) {
			ArrayList<Config> configs = cachedProperties.get(key);
			for (Config c : configs) {
				if (c.getValue().equals(value)) {
					okayToDelete = c.isPublic();
				}
			}
		}
		if (okayToDelete) {
			try {
				PreparedStatement stmt = conn.prepareStatement(deleteConfig);
				stmt.setString(1, key);
				stmt.setString(2, value);
				int i = stmt.executeUpdate();
				if (i > 0) {
					cacheValid = false;
					return "Successfully removed " + key + " with the value " + value + " from the properties table.";
				} else {
					return "There was an error removing the specified key/value pair.";
				}
			} catch (Exception e) {
				logger.error("Exception deleting property.", e);
			}
		} else {
			return "Apologies, this property is either not currently configured, or is not publically accessible.";
		}
		
		return "There was an unexpected error attempting to delete property.";

	}

	private static void loadProperties() {
		if (!cacheValid) {
			cachedProperties = new HashMap<String, ArrayList<Config>>();
			Connection conn = Connector.getConnection();
			ResultSet rs = null;
			try {
				PreparedStatement stmt = conn.prepareStatement(kvQuery);
				rs = stmt.executeQuery();
				while (rs.next()) {
					if (!cachedProperties.containsKey(rs.getString("key"))) {
						cachedProperties.put(rs.getString("key"), new ArrayList<Config>());
					}
					cachedProperties.get(rs.getString("key")).add(new Config(rs.getString("key"), rs.getString("value"),
							rs.getString("updated"), rs.getBoolean("public")));
				}
				cacheValid = true;
			} catch (SQLException e) {
				logger.error("Exception attempting to retreive properties list", e);
			} finally {
				try {
					rs.close();
					conn.close();
				} catch (NullPointerException e) {
					// nothing to do here!
				} catch (Exception e) {
					logger.error("There was an exception closing result set", e);
				}
			}
		}
	}

	public static ArrayList<Config> getConfiguredProperties(boolean showPublic) {
		ArrayList<Config> keyValues = new ArrayList<Config>();
		if (!cacheValid) {
			loadProperties();
		} else {
			for (String key : cachedProperties.keySet()) {
				for (Config value : cachedProperties.get(key)) {
					if (showPublic) {
						if (value.isPublic()) {
							keyValues.add(value);
						}
					} else {
						keyValues.add(value);
					}
				}
			}

			return keyValues;
		}

		return keyValues;
	}

	public static ArrayList<String> getPropertyTypes() {
		Connection conn = Connector.getConnection();
		ArrayList<String> keyValues = new ArrayList<String>();
		ResultSet rs = null;
		try {
			PreparedStatement stmt = conn.prepareStatement(keysQuery);

			rs = stmt.executeQuery();
			while (rs.next()) {
				keyValues.add(rs.getString("key"));
			}
		} catch (SQLException e) {
			logger.error("Exception attempting to retreive properties list", e);
		} finally {
			try {
				rs.close();
				conn.close();
			} catch (NullPointerException e) {
				// nothing to do here!
			} catch (Exception e) {
				logger.error("There was an exception closing result set", e);
			}
		}

		return keyValues;

	}

}
