package com.ansi.scilla.web.dataload;

import java.sql.Connection;
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
			new ChangePassword().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		String DB = "DEV";
//		String DB = "PROD";
		Integer userId = 64;
		String newPassword = "password1";
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
