package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestDBConn {
	private final String DB_DEV_URL="jdbc:sqlserver://192.168.10.252:1433;databaseName=ansi_sched;integratedSecurity=false;";
	private final String DB_DEV_USERID="ansi_sched.webapp";
	private final String DB_DEV_PASSWORD="@dataflex1997";


	private final String DB_UAT_URL="jdbc:sqlserver://192.168.10.50:1433;databaseName=ansi_sched;integratedSecurity=false;";
	private final String DB_UAT_USERID="ansi_sched.webapp";
	private final String DB_UAT_PASSWORD="@dataflex1997";

	private final String DB_DRIVER="com.microsoft.sqlserver.jdbc.SQLServerDriver";
	private final String DB_PROD_URL="jdbc:sqlserver://192.168.10.100:1433;databaseName=asap;integratedSecurity=false;";
	private final String DB_PROD_USERID="ansi_sched.webapp";
	private final String DB_PROD_PASSWORD="@dataflex1997";

	
	public void go() throws Exception {
		Class.forName(DB_DRIVER);	
        testConn(DB_DEV_URL, DB_DEV_USERID, DB_DEV_PASSWORD);
        testConn(DB_UAT_URL, DB_UAT_USERID, DB_UAT_PASSWORD);
        testConn(DB_PROD_URL, DB_PROD_USERID, DB_PROD_PASSWORD);
	}
	
	private void testConn(String url, String userid, String password) throws SQLException {
		try {
			Connection conn =  DriverManager.getConnection(url, userid, password);
			if ( conn == null ) {
				System.err.println("Failed: " + url);
			} else {
				System.out.println("Success: " + url);
				conn.close();
			}
		} catch ( Exception e) {
			System.err.println("Failed: " + url);
			e.printStackTrace();
		}
	}
	
	
	

	
	public static void main(String[] args) {
		try {
			new TestDBConn().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
