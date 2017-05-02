package com.irc.helen;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.apache.xmlrpc.XmlRpcException;

import com.helen.database.Connector;

public class TestMain {

	

	public static void main(String args[]) throws XmlRpcException {
		try {
			int indexOffset = 2;
			String[] terms = new String[]{".s","-e","the","ol","ma"};
			String query = "select pagename,title,scptitle,scppage from pages where";
			for(int j = indexOffset; j < terms.length; j++){
				if(j != indexOffset){
					query +=" and";
				}
				query += " lower(coalesce(scptitle, title)) like ?";
			}
			
			System.out.println(query);
			//Connection conn = Connector.getConnection();
			//PreparedStatement state = conn.prepareStatement(query);
			for(int j = indexOffset; j < terms.length; j++){
				System.out.println("Setting string : " + (j - (indexOffset - 1)) + " to " + terms[j]);
				//state.setString(j - indexOffset, terms[j]);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
