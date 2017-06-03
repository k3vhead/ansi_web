package com.ansi.scilla.web.test;

import java.sql.Connection;
import java.util.List;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.AppUtils;

public class TestMS {

	public static void main(String[] args) {
		System.out.println("Start");
		try {
			TesterUtils.makeLoggers();
			new TestMS().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("end");
	}

	
	public void go() throws Exception {		
//		String dbURL = "jdbc:sqlserver://192.168.10.50:1433;databaseName=ansi_sched;integratedSecurity=false;";		
//		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
//		String userId = "ansi_sched.dbo";
//		String password = "@6309418500";
		
        Connection conn = null;
        try {
//        	conn =  DriverManager.getDevConnection(dbURL, userId, password);
        	conn = AppUtils.getDevConn();
            if ( conn == null ) {
            	throw new Exception("Null");
            }
            List<User> userList = new User().cast(new User().selectAll(conn));
            for ( User user : userList ) {
            	System.out.println(user.getLastName());
            }
//            Statement s = conn.createStatement();
//            ResultSet rs = s.executeQuery("select * from address");
//            while ( rs.next() ) {
//            	System.out.println(rs.getString("last_name"));
//            }
//            rs.close();
            
        } finally {
        	conn.close();
        }
	}
}
