package com.ansi.scilla.web.dataload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.utils.AppUtils;

public class ChangePassword extends ApplicationObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		try {
			new ChangePassword().gomin();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void gomin() throws Exception {
		Connection conn = null;
		String DB = "DEV";
//		String DB = "PROD";
		Integer userId = 65;
		String newPassword = "XisaGoodPassword";
		Integer myUserId = 5;
		Boolean save = true;
		
		try {
			if ( DB.equals("PROD")) {
				conn = AppUtils.getProdConn();
			} else {
				conn = AppUtils.getDevConn();
			}
			conn.setAutoCommit(false);
			PreparedStatement psGet = conn.prepareStatement("select first_name, last_name, email from ansi_user where user_id=?");
			PreparedStatement psPut = conn.prepareStatement("update ansi_user set password=?, updated_by=?, updated_date=? where user_id=?");
			psGet.setInt(1, userId);
			ResultSet rs = psGet.executeQuery();
			while ( rs.next() ) {
				System.out.println(rs.getString("first_name") + "\t" + rs.getString("last_name") + "\t" + rs.getString("email"));
			}
			rs.close();
			if (save) {
				java.sql.Date today = new java.sql.Date((new Date()).getTime());
				psPut.setString(1,newPassword);
				psPut.setInt(2, myUserId);
				psPut.setDate(3, today);
				psPut.setInt(4, userId);
				psPut.executeUpdate();
				conn.commit();
				System.out.println("Updated: " + newPassword);
			}
		} catch ( Exception e) {
			conn.rollback();
		} finally {
			conn.close();
		}
	}
	public void go() throws Exception {
		Connection conn = null;
//		String DB = "DEV";
		String DB = "PROD";
		Integer userId = 65;
		String newPassword = "XisaGoodPassword";
		Integer myUserId = 5;
		
		try {
			if ( DB.equals("PROD")) {
				conn = AppUtils.getProdConn();
			} else {
				conn = AppUtils.getDevConn();
			}
			conn.setAutoCommit(false);
			
			User user = new User();
			user.setUserId(userId);
			user.selectOne(conn);
			
			System.out.println(user);
			
			String encryptedPassword = AppUtils.encryptPassword(newPassword, userId);	
			System.out.println("Password is: " + newPassword);
			
			user.setPassword(encryptedPassword);
			user.setUpdatedBy(myUserId);
			user.setUpdatedDate(new Date());
			
			User key = new User();
			key.setUserId(userId);
			user.update(conn, key);
			
			conn.commit();
		} catch ( Exception e) {
			if ( conn != null ) {
				AppUtils.rollbackQuiet(conn);
			}
			throw e;
		} finally {
			if ( conn != null ) {
				AppUtils.closeQuiet(conn);
			}
		}
		
	}

}
