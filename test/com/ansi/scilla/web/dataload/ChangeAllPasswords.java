package com.ansi.scilla.web.dataload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ansi.scilla.web.common.utils.AppUtils;


public class ChangeAllPasswords {

	private static final boolean doUpdate = true;
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			Statement s = conn.createStatement();
			ResultSet rsGet = s.executeQuery("select first_name, last_name, user_id from ansi_user");
			PreparedStatement ps = conn.prepareStatement("update ansi_user set password=?,updated_date=?,updated_by=? where user_id=?");
			java.sql.Date now = new java.sql.Date(new java.util.Date().getTime());
			while ( rsGet.next() ) {
				Integer userId = rsGet.getInt("user_id");
				System.out.println(rsGet.getString("first_name") + " " + rsGet.getString("last_name") + "\t" + userId);
				if (doUpdate) {
					System.out.println("\tUpdating");
					String encryptedPassword = AppUtils.encryptPassword("password1", userId);
					ps.setString(1, encryptedPassword);
					ps.setDate(2, now);
					ps.setInt(3, 0);
					ps.setInt(4, userId);
					ps.executeUpdate();
				}
			}
			rsGet.close();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.commit();
		}
	}
	
	public static void main(String[] args) {
		try {
			new ChangeAllPasswords().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
