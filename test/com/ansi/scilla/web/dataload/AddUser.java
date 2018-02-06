package com.ansi.scilla.web.dataload;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.utils.AppUtils;

public class AddUser extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	private final String DB = "DB";
	private final String FIRST_NAME = "FIRST_NAME";
	private final String LAST_NAME = "LAST_NAME";
	private final String EMAIL = "EMAIL";
	private final String PHONE = "PHONE";
	private final String DB_TYPE_DEV = "DEV";
	private final String DB_TYPE_PROD = "PROD";
	private final String ADDED_BY = "ADDED_BY";
	private final String PERMISSION_GROUP = "PERMISSION_GROUP";
	private final String TITLE = "TITLE";
	
	private Random random = new Random();

	
	private final List<String> argNames    = Arrays.asList(new String[] {DB,FIRST_NAME,LAST_NAME,EMAIL,PHONE,ADDED_BY,PERMISSION_GROUP,TITLE} );
	private final List<String> argRequired = Arrays.asList(new String[] {DB,FIRST_NAME,LAST_NAME,EMAIL,      ADDED_BY,PERMISSION_GROUP,TITLE} );
	private final List<String> dbTypes = Arrays.asList(new String[] {DB_TYPE_DEV, DB_TYPE_PROD} );
	
	public static void main(String[] args) {

		String[] myargs = new String[] {
				"DB=DEV",
				"FIRST_NAME=George",
		    	"LAST_NAME=Washington",
		    	"EMAIL=geo@whitehouse.gov",
//		    	"PHONE=",
		    	"PERMISSION_GROUP=7",
		    	"ADDED_BY=5",
		    	"TITLE=Division Manager"
		};
		System.out.println("Starting AddUser");
		try {
			new AddUser().go(myargs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done With AddUser");
	}

	private void go(String[] args) throws Exception {
		Connection conn = null;
		try {
			HashMap<String, String> parms = parseArgs(args);
			if ( parms.get(DB).equals(DB_TYPE_PROD)) {
				conn = AppUtils.getProdConn();
			} else {
				conn = AppUtils.getDevConn();
			}
			conn.setAutoCommit(false);
			Calendar now = Calendar.getInstance(new AnsiTime());
			Date today = now.getTime();
			
			User newUser = new User();
			newUser.setAddedBy(Integer.valueOf(parms.get(ADDED_BY)));
			newUser.setAddedDate(today);
			newUser.setEmail(parms.get(EMAIL));
			newUser.setFirstName(parms.get(FIRST_NAME));
			newUser.setLastName(parms.get(LAST_NAME));
			newUser.setPassword("not set yet");
			newUser.setPermissionGroupId(Integer.valueOf(parms.get(PERMISSION_GROUP)));
			newUser.setPhone(parms.get(PHONE));
			newUser.setStatus(User.STATUS_IS_GOOD);
			newUser.setSuperUser(User.SUPER_USER_IS_NO);
			newUser.setUpdatedBy(Integer.valueOf(parms.get(ADDED_BY)));
			newUser.setUpdatedDate(today);
			newUser.setTitle(parms.get(TITLE));
			
			Integer userId = newUser.insertWithKey(conn);
			
			Integer passnum = random.nextInt(1000-100) + 100;
			String password = parms.get(FIRST_NAME).substring(0,3) + parms.get(LAST_NAME).substring(0,3) + passnum;
//			String password = "password" + passnum;
			String encryptedPassword = AppUtils.encryptPassword(password, userId);	
			System.out.println("Password is: " + password);
			
			PreparedStatement ps = conn.prepareStatement("update ansi_user set password=? where user_id=?");
			ps.setString(1, encryptedPassword);
			ps.setInt(2,  userId);
			ps.executeUpdate();
			
			newUser.setPassword("******");
			newUser.setUserId(userId);
			System.out.println(newUser);
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

	private HashMap<String, String> parseArgs(String[] args) throws Exception {
		HashMap<String, String> parms = new HashMap<String, String>();
		for ( String arg : args ) {
			String[] pieces = arg.split("=");
			if ( pieces.length != 2 ) {
				throw new Exception("Invalid argument " + arg);
			}
			if ( ! argNames.contains(pieces[0])) {
				throw new Exception("Invalid argument " + arg);
			}
			parms.put(pieces[0], pieces[1]);
		}
		for ( String argName : argRequired ) {
			if ( ! parms.containsKey(argName)) {
				throw new Exception("Missing argument " + argName);
			}
		}
		if ( ! dbTypes.contains( parms.get(DB) )) {
			throw new Exception("Invalid database: " + parms.get(DB));
		}
		return parms;
	}

	
}
