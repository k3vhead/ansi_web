package com.ansi.scilla.web.test;

import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.common.db.Message;
import com.ansi.scilla.common.db.PermissionGroup;
import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Title;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;

public class InsertTestData extends TesterUtils {
	private final boolean debug=true;

	public static void main(String[] args) {
		
		InsertTestData itd = new InsertTestData();
		try {
			System.out.println("Start");
//			System.out.println("insertPermissionLevel");
			/*
			itd.insertPermissionLevel();
			System.out.println("insertMessage");
			itd.insertMessage();
			System.out.println("insertPermissionGroup");
			itd.insertPermissionGroup();
			System.out.println("insertPermissionGroupLevel");
			itd.insertPermissionGroupLevel();
			*/
			System.out.println("insertDivision");
			itd.insertDivision();
			/*
			System.out.println("insertTitle");
			itd.insertTitle();
			System.out.println("insertUser");
			itd.insertUser();			
			System.out.println("insertDivisionUser");
			itd.insertDivisionUser();
			System.out.println("insertAllUsers");
			itd.insertAllUsers();
			System.out.println("setPasswords");
			itd.setPasswords();
			*/
			System.out.println("Done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void setPasswords() throws Exception {
		Connection conn = null;
		String sql = "select user_id, email from ansi_user";		
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			
			PreparedStatement ps = conn.prepareStatement("update ansi_user set password=? where user_id=?");
			
			while (rs.next() ) {
				Integer userId = rs.getInt("user_id");
				String password = AppUtils.encryptPassword("password1", userId);
				System.out.println(userId + "\t" + password);
				ps.setString(1, password);
				ps.setInt(2, userId);
				ps.executeUpdate();
			}
			rs.close();
			conn.commit();
		} catch ( Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw e;
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	private void insertAllUsers() throws Exception {
		Date now = new Date();
		User user = new User();
		user.setAddedBy(1);
		user.setAddedDate(now);
		user.setPermissionGroupId(1);
		user.setStatus(User.STATUS_IS_GOOD);
		user.setSuperUser(0);
		user.setTitle("Title");
		user.setUpdatedBy(1);
		user.setUpdatedDate(now);
		
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);

			user.setEmail("dclewis@thewebthing.com");
			user.setFirstName("David");
			user.setLastName("Lewis");
			user.setSuperUser(1);
			user.insertWithKey(conn);
			
			user.setSuperUser(0);
			user.setEmail("gag@ansi.com");
			user.setFirstName("Gary");
			user.setLastName("Groce");
			user.insertWithKey(conn);
			
//			user.setEmail("jwlewis@thewebthing.com");
//			user.setFirstName("Joshua");
//			user.setLastName("Lewis");
//			user.insertWithKey(conn);
			
			user.setEmail("kjw@ansi.com");
			user.setFirstName("Kevin");
			user.setLastName("Wagner");
			user.insertWithKey(conn);
			
			user.setEmail("keegan.ovitt@gmail.com");
			user.setFirstName("Keegan");
			user.setLastName("Ovitt");
			user.insertWithKey(conn);
			
			user.setEmail("seatonja120@gmail.com");
			user.setFirstName("Jessica");
			user.setLastName("Seaton");
			user.insertWithKey(conn);
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
		
	}

	private void insertUser() throws Exception {
		Connection conn = null;		
		Date now = new Date();
		
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
		
			User user = new User();
			user.setFirstName("Joshua");
			user.setLastName("Lewis");
			user.setTitle("Guru");
			user.setAddedBy(1);
			user.setAddedDate(now);
			user.setUpdatedBy(1);
			user.setUpdatedDate(now);
			user.setPermissionGroupId(1);
			user.setEmail("jwlewis@thewebthing.com");
			user.setStatus(User.STATUS_IS_GOOD);
			user.setSuperUser(1);
			user.setAddress1("1060 Addison Ave");
			user.setCity("Chicago");
			user.setState("IL");
			user.setZip("12345");
			user.setPassword(AppUtils.encryptPassword("password1", 1));
			System.out.println(user);
			
			Integer userId = user.insertWithKey(conn, true);
			System.out.println("UserID: " + userId);
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
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
		
			for ( Permission permission : Permission.values() ) {

				PermissionGroupLevel p = new PermissionGroupLevel();
				
				if ( ! permission.equals(Permission.INVOICE)) {
					System.out.println(permission);
					p.setAddedBy(myUserId);
					p.setAddedDate(now);
					p.setPermissionGroupId(1);
					p.setPermissionLevel(1);
					p.setPermissionName(permission.toString());
					p.setUpdatedBy(myUserId);
					p.setUpdatedDate(now);
					p.insertWithNoKey(conn);
				}
			}
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
		
		int colDivisionCode = 0;
		int colDescription = 1;
		int colName = 5;
		
		String fileName = "/home/dclewis/Documents/projects/ansi/Divisions List from Dataflex.xlsx";
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(fileName));
		XSSFSheet sheet = wb.getSheetAt(0);
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			Date today = new Date();
			
			Division d = new Division();
			d.setDefaultDirectLaborPct(new BigDecimal(0.03F));
			d.setAddedBy(5);
			d.setUpdatedBy(5);
			d.setAddedDate(today);
			d.setUpdatedDate(today);
			d.setStatus(Division.STATUS_IS_ACTIVE);
			
			
			for ( int rowNum = 1; rowNum < sheet.getLastRowNum() + 1; rowNum++ ) {
				System.out.println("Row: " + rowNum);
				XSSFRow row = sheet.getRow(rowNum);
				String divisionCode = row.getCell(colDivisionCode).getStringCellValue();
				Integer divisionNbr = Integer.valueOf(divisionCode.substring(2));
				String description = row.getCell(colDescription).getStringCellValue();
				String name = row.getCell(colName) == null ? row.getCell(colDescription).getStringCellValue() : row.getCell(colName).getStringCellValue();
				name = name + " (" + divisionCode + ")";
				d.setDescription(description);
				d.setDivisionCode(divisionCode);
				d.setDivisionNbr(divisionNbr);
				d.insertWithKey(conn, true);
			}
			
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
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);

			String sql = "INSERT INTO permission_group(description,added_by,added_date,status,updated_by,updated_date,name) VALUES (?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, "Allowed to see some stuff");
			ps.setInt(2, 1);
			ps.setDate(3,new java.sql.Date(now.getTime()));
//			ps.setNull(4, Types.INTEGER);
			ps.setInt(4, PermissionGroup.STATUS_IS_ACTIVE);
			ps.setInt(5, 1);
			ps.setDate(6, new java.sql.Date(now.getTime()));
			ps.setString(7, "Permission Group1");
			ps.executeUpdate();
//			PermissionGroup d = new PermissionGroup();
//			d.setName("Permission group 2");
//			d.setDescription("Allowed to see Jobs");
//			d.setStatus(1);
//			
//			d.setAddedBy(myUserId);
//			d.setAddedDate(now);
//			d.setUpdatedBy(myUserId);
//			d.setUpdatedDate(now);
//			d.insertWithKey(conn, debug);
//			System.out.println(d);
			
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
			conn = AppUtils.getDevConn();
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
			conn = AppUtils.getDevConn();
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
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			for ( Permission p : Permission.values() ) {
				System.out.println("\t" + p);
				PermissionLevel pl = new PermissionLevel();
				pl.setPermissionName(p.toString());
				pl.setLevel(1);
				pl.setAddedBy(1);
				pl.setAddedDate(now);
				pl.setUpdatedBy(1);
				pl.setUpdatedDate(now);
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
		codeMap.put(ResponseCode.INVALID_LOGIN, "Invalid Login");
		codeMap.put(ResponseCode.EDIT_FAILURE, "Missing Required Data");
		
		
		Integer myUserId = 1;
		
		
		Connection conn = null;		
		Date now = new Date();
		try {
			conn = AppUtils.getDevConn();
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

	public class MyUser extends User {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Integer insertWithKey(Connection conn, boolean debug) throws Exception {
			String sql = "insert into ansi_user ("
					+ "status, first_name, last_name, title, email, "
					+ "password, phone, address1, address2, city, state, "
					+ "zip, permission_group_id, added_by, added_date, "
					+ "updated_by, updated_date"
					+ ") "
					+ "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
			PreparedStatement ps = conn.prepareStatement(sql);
			int n=1;
			ps.setInt(n, getStatus());
			n++;
			ps.setString(n, getFirstName());
			n++;
			ps.setString(n, getLastName());
			n++;
			ps.setString(n, getTitle());
			n++;
			ps.setString(n, getEmail());
			n++;
			ps.setString(n, getPassword());
			n++;
			ps.setString(n, getPhone());
			n++;
			ps.setString(n, getAddress1());
			n++;
			ps.setString(n, getAddress2());
			n++;
			ps.setString(n, getCity());
			n++;
			ps.setString(n, getState());
			n++;
			ps.setString(n, getZip());
			n++;
			ps.setInt(n, getPermissionGroupId());
			n++;
			ps.setInt(n, getAddedBy());
			n++;
			ps.setDate(n, new java.sql.Date(getAddedDate().getTime()));
			n++;
			ps.setInt(n, getUpdatedBy());
			n++;
			ps.setDate(n, new java.sql.Date(this.getUpdatedDate().getTime()));
			n++;
			Integer id = ps.executeUpdate();
			
//			ResultSet rs = ps.getGeneratedKeys();
//	        rs.absolute(1); // we're only getting 1 record
//	        int id = rs.getInt("GENERATED_KEY");
//	        rs.close();
			
	        return Integer.valueOf(id);
		}

		@Override
		public Integer insertWithKey(Connection conn) throws Exception {
			return insertWithKey(conn, false);
		}
	}
}
