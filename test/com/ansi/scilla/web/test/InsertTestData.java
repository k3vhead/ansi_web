package com.ansi.scilla.web.test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.util.HashMap;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.common.db.Message;
import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Title;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;

public class InsertTestData extends TesterUtils {
	private final boolean debug=true;

	public static void main(String[] args) {
		
		InsertTestData itd = new InsertTestData();
		try {
			System.out.println("Start");
//			itd.insertMessage();
//			itd.insertPermissionLevel();
//			itd.insertPermissionGroup();
//			itd.insertPermissionGroupLevel();
//			itd.insertUser();
//			itd.insertDivision();
//			itd.insertTitle();
			itd.insertDivisionUser();
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void insertUser() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);
		
			User user = new User();
			user.setFirstName("David");
			user.setLastName("Lewis");
			user.setTitle("Fundi");
			user.setAddedBy(1);
			user.setAddedDate(now);
			user.setUpdatedBy(1);
			user.setUpdatedDate(now);
			user.setPermissionGroupId(1);
			user.setEmail("dclewis@thewebthing.com");
			user.setStatus(User.STATUS_IS_GOOD);
			user.setSuperUser(1);
			user.setAddress1("1600 Pennsylvania Ave");
			System.out.println(user);
			
			user.insertWithKey(conn);
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}	
	}
	private void insertPermissionGroupLevel() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		Integer myUserId = 1;
		
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);
		
			PermissionGroupLevel p = new PermissionGroupLevel();
			p.setAddedBy(myUserId);
			p.setAddedDate(now);
			p.setPermissionGroupId(1);
			p.setPermissionLevel(1);
			p.setPermissionName(Permission.INVOICE.toString());
			p.setUpdateBy(myUserId);
			p.setUpdateDate(now);
			p.insertWithNoKey(conn);
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}		
	}

	private void insertDivision() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		Integer myUserId = 1;
		
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);

			Division d = new Division();
			d.setName("Test Div 2");
			d.setDefaultDirectLaborPct(new BigDecimal(0.03F));
			
			
			d.setAddedBy(myUserId);
			d.setAddedDate(now);
			d.setUpdatedBy(myUserId);
			d.setUpdatedDate(now);
			d.insertWithKey(conn, debug);
			System.out.println(d);
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}		
	}

	private void insertPermissionGroup() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		Integer myUserId = 1;
		
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);

			PermissionGroup d = new PermissionGroup();
			d.setName("Permission group 2");
			d.setDescription("Allowed to see Jobs");
			d.setStatus(1);
			
			d.setAddedBy(myUserId);
			d.setAddedDate(now);
			d.setUpdatedBy(myUserId);
			d.setUpdatedDate(now);
			d.insertWithKey(conn, debug);
			System.out.println(d);
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}		
	}

	
	private void insertTitle() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		Integer myUserId = 1;
		
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);

			Title d = new Title();
			d.setName("Div 2 title");
			d.setStatus(1);
			
			
			d.setAddedBy(myUserId);
			d.setAddedDate(now);
			d.setUpdatedBy(myUserId);
			d.setUpdatedDate(now);
			d.insertWithKey(conn, debug);
			System.out.println(d);
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}		
	}

	private void insertDivisionUser() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		Integer myUserId = 1;
		
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);

			DivisionUser d = new DivisionUser();
			d.setUserId(1);
			d.setDivisionId(1);
//			d.setPermissionGroupId(2);
			d.setTitleId(1);
			
			
			
			d.setAddedBy(myUserId);
			d.setAddedDate(now);
			d.setUpdatedBy(myUserId);
			d.setUpdatedDate(now);
			d.insertWithNoKey(conn);
			System.out.println(d);
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}		
	}

	private void insertPermissionLevel() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		try {
			conn = AppUtils.getConn();
			conn.setAutoCommit(false);
			
			for ( Permission p : Permission.values() ) {
				System.out.println("\t" + p);
				PermissionLevel pl = new PermissionLevel();
				pl.setPermissionName(p.toString());
				pl.setLevel(1);
				pl.setAddedBy(1);
				pl.setAddedDate(now);
				pl.setUpdateBy(1);
				pl.setUpdateDate(now);
				pl.insertWithNoKey(conn);
				
				pl.setLevel(0);
				pl.insertWithNoKey(conn);
			}
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	public void insertMessage() throws Exception {
		HashMap<ResponseCode, String> codeMap = new HashMap<ResponseCode, String>();
		codeMap.put(ResponseCode.SUCCESS, "Success!");
		codeMap.put(ResponseCode.EXPIRED_LOGIN, "Login has expired. See your admin");
		codeMap.put(ResponseCode.INVALID_LOGIN, "Your userid/password do not match our records");
		
		
		Integer myUserId = 1;
		
		
		Connection conn = null;		
		Date now = new Date();
		try {
			conn = AppUtils.getConn();
			if (conn == null ) {
				throw new Exception("null conn");
			}
			conn.setAutoCommit(false);
			
			for ( ResponseCode code : new ResponseCode[] {ResponseCode.SUCCESS, ResponseCode.EXPIRED_LOGIN, ResponseCode.INVALID_LOGIN}) {
				Message message = new Message();
				message.setKey(code.toString());
				message.setMessage(codeMap.get(code));
				message.setSeq(1);
				message.setAddedBy(myUserId);
				message.setAddedDate(now);
				message.setUpdatedBy(myUserId);
				message.setUpdatedDate(now);
				message.setStatus(Message.STATUS_IS_ACTIVE);
				message.insertWithNoKey(conn);
				
				System.out.println(message);
				
			}
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

}
