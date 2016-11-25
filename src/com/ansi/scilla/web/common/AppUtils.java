package com.ansi.scilla.web.common;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidLoginException;
import com.thewebthing.commons.db2.RecordNotFoundException;
import com.thewebthing.commons.lang.StringUtils;


public class AppUtils {
	private static final String KEYTEXT = "Y0u know, in sertin 0lder c1v1liz3d cultur3s, when m3n f@iled as entirely as y0u h@ve, they would thr0w th3ms3lv3s on they'r3 swords";

	/**
	 * Retrieve configuration properties
	 * @return
	 */
	public static Properties getConfig() {
		ResourceBundle rb = ResourceBundle.getBundle("resources.config");
		Properties props = new Properties();
		for (Enumeration<String> e = rb.getKeys(); e.hasMoreElements(); ) {
			String key = e.nextElement();
			props.setProperty(key, rb.getString(key));
		}
		return props;
	}

	/**
	 * Retrieve a configuration property
	 * @param propertyName
	 * @return
	 */
	public static String getProperty(PropertyNames propertyName) {
		return getConfig().getProperty(propertyName.toString());
	}


	/**
	 * Retrieve a non-pooled database connection (use with batch or test modules)
	 * @return
	 * @throws Exception
	 */
	public static Connection getConn() throws Exception {
		
		String driver = getProperty(PropertyNames.DB_DRIVER);
		String dbURL = getProperty(PropertyNames.DB_URL);
		String dbID = getProperty(PropertyNames.DB_USERID);
		String dbPass = getProperty(PropertyNames.DB_PASSWORD);
		System.out.println(dbURL);
		System.out.println(dbID + "\t" + dbPass);
		Class.forName(driver);		
        Connection conn =  DriverManager.getConnection(dbURL, dbID, dbPass);
        
		return conn;
	}
       


	/**
	 * Returns a connection from the application-specified DBCP as defined in META-INF/context.xml
	 * @return Connection
	 * @throws NamingException
	 * @throws SQLException
	 **/
	public static Connection getDBCPConn() throws NamingException, SQLException {
		Context ctx = new InitialContext();
		BasicDataSource ds = (BasicDataSource)ctx.lookup("java:comp/env/jdbc/ansi");
		ds.setLogAbandoned(true);

		// Get Connection and Statement
		Connection conn =  ds.getConnection();

		return conn;
	}

	/**
	 * Wraps a DB close connection in a try/catch which throws a RuntimeException. Use this
	 * to avoid nested try/catch structures
	 * 
	 * @param conn
	 */
	public static void closeQuiet(Connection conn) {
		try {
			conn.close();
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Create an application logger.
	 * @throws IOException
	 */
	public static void makeApplicationLogger() throws IOException {
		Properties properties = System.getProperties();
		String catalinaBase = properties.getProperty("catalina.base");
		if ( catalinaBase == null ) {
			catalinaBase = "/Users/dclewis/Documents";
		}
		String fileSeparator = properties.getProperty("file.separator");

		String logName = getProperty(PropertyNames.LOG_NAME);
		String logfilename = logName.replaceAll("\\.", "_"); 

		Logger logger = Logger.getLogger(logName);
		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
		FileAppender systemFileAppender = new FileAppender(
				layout,
				catalinaBase + fileSeparator + "logs" + fileSeparator + logfilename);
		logger.addAppender(systemFileAppender);
		systemFileAppender.activateOptions();
		logger.setLevel(Level.DEBUG);
	}
	
	/**
	 * Create a logger to be used by apache utilities
	 * @throws IOException
	 */
	public static void makeApacheLogger() throws IOException {
		Properties properties = System.getProperties();
		String catalinaBase = properties.getProperty("catalina.base");
		if ( catalinaBase == null ) {
			catalinaBase = "/Users/dclewis/Documents";
		}
		String fileSeparator = properties.getProperty("file.separator");


		Logger logger = Logger.getLogger("org.apache.struts");
		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
		FileAppender systemFileAppender = new FileAppender(
				layout,
				catalinaBase + fileSeparator + "logs" + fileSeparator + "com_scilla_apache_log");
		logger.addAppender(systemFileAppender);
		systemFileAppender.activateOptions();
		logger.setLevel(Level.INFO);
	}

	/**
	 * Create a logger to be used by webthing utilities
	 * @throws IOException
	 */
	public static void makeWebthingLogger() throws IOException {
		Properties properties = System.getProperties();
		String catalinaBase = properties.getProperty("catalina.base");
		if ( catalinaBase == null ) {
			catalinaBase = "/Users/dcl/Documents";
		}
		String fileSeparator = properties.getProperty("file.separator");


		Logger logger = Logger.getLogger("com.thewebthing");
		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
		FileAppender systemFileAppender = new FileAppender(
				layout,
				catalinaBase + fileSeparator + "logs" + fileSeparator + "com_thewebthing_log");
		logger.addAppender(systemFileAppender);
		systemFileAppender.activateOptions();
		logger.setLevel(Level.DEBUG);
	}

	/**
	 * Log exception to the application logger
	 * @param e
	 */
	public static void logException(Exception e) {
		Logger logger = Logger.getLogger(getProperty(PropertyNames.LOG_NAME));

		StringBuffer buf = new StringBuffer();
		buf.append(e.toString() + "\n");
		for ( StackTraceElement el : e.getStackTrace()) {
			buf.append(el.toString() + "\n");
		}
		logger.fatal(buf.toString());
	}

	
	public static Logger getLogger() {
		Logger logger = Logger.getLogger(getProperty(PropertyNames.LOG_NAME));
		return logger;
	}
	
	/**
	 * Encrypt a password for checking against the user table
	 * @param password
	 * @param userid
	 * @return
	 */
	public static String encryptPassword(String password, Integer userid) {
		return StringUtils.encryptPassword(KEYTEXT, password, String.valueOf(userid));
	}


	/**
	 * Generate a password of a length specified in the application configuration resource bundle
	 * @return
	 */
	public static String generatePassword() {
		boolean useLetters = true;
		boolean useNumbers = true;
		String passwordLength = getProperty(PropertyNames.MINIMUM_PASSWORD_LENGTH);
		return RandomStringUtils.random(Integer.valueOf(passwordLength), useLetters, useNumbers);
	}

	/**
	 * Retrieve message text as specified in the application resources resource bundle.
	 * @param msgKey
	 * @return
	 */
	public static String getMsg(String msgKey) {
		ResourceBundle rb = ResourceBundle.getBundle("resources.application");
		return rb.getString(msgKey);
	}

	/**
	 * Verifies userid/password combination. If valid, returns user, else throws appropriate exception
	 * @param conn
	 * @param userid
	 * @param password
	 * @return
	 * @throws ExpiredLoginException
	 * @throws InvalidLoginException
	 */
	public static User checkLogin(Connection conn, String userid, String password) throws ExpiredLoginException, InvalidLoginException, Exception {
		Logger logger = AppUtils.getLogger();
		User user = new User();
		user.setEmail(userid);
		try {
			logger.debug("user: " + userid);
			user.selectOne(conn);
			if ( ! user.getStatus().equals(User.STATUS_IS_GOOD)) {
				logger.debug("expired");
				throw new ExpiredLoginException();
			}
			logger.debug(user);
			String checkPass = encryptPassword(password, user.getUserId());
			logger.debug(checkPass);
			if ( ! checkPass.equals(user.getPassword())) {
				throw new InvalidLoginException();
			}
			
		} catch ( RecordNotFoundException e) {
			throw new InvalidLoginException();
		}
		
		return user;
		
	}

	/**
	 * Return list of divisions to which a user belongs
	 * @param conn
	 * @param userId
	 * @return
	 * @throws Exception
	 */
	public static List<Division> makeDivisionList(Connection conn, Integer userId) throws Exception {
		String sql = "select division.* from division_user  " +
				" inner join division on division.division_id=division_user.division_id " + 
				" where division_user.user_id=?";
		List<Division> divisionList = new ArrayList<Division>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, userId);		
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			divisionList.add( (Division)new Division().rs2Object(rsmd, rs) ); 
		}
		Collections.sort(divisionList,

				new Comparator<Division>() {

			public int compare(Division o1, Division o2) {

				int ret = o1.getName().compareTo(o2.getName());
				return ret;

			}

		});
		return divisionList;
	}







}
