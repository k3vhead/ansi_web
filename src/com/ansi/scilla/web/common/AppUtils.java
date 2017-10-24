package com.ansi.scilla.web.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.ResourceBundle;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.PropertyNames;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;
import com.thewebthing.commons.lang.StringUtils;


public class AppUtils extends com.ansi.scilla.common.utils.AppUtils {
	private static final String KEYTEXT = "Y0u know, in sertin 0lder c1v1liz3d cultur3s, when m3n f@iled as entirely as y0u h@ve, they would thr0w th3ms3lv3s on they'r3 swords";

	private static Random random = new Random((new Date()).getTime());

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
		ds.setTestOnBorrow(true);

		// Get Connection and Statement
		Connection conn =  ds.getConnection();

		if ( conn == null ) {
			Logger logger = AppUtils.getLogger();
			logger.info("Retrieved null connection, trying again");
			conn = ds.getConnection();
		}
		return conn;
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
	 * Create a logger to track all transactions
	 * @throws IOException
	 */
	public static void makeTransactionLogger() throws IOException {
		Properties properties = System.getProperties();
		String catalinaBase = properties.getProperty("catalina.base");
		if ( catalinaBase == null ) {
			catalinaBase = "/Users/dcl/Documents";
		}
		String fileSeparator = properties.getProperty("file.separator");


		Logger logger = Logger.getLogger(PropertyNames.TRANSACTION_LOG.toString());
		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
		FileAppender systemFileAppender = new FileAppender(
				layout,
				catalinaBase + fileSeparator + "logs" + fileSeparator + "ansi_scilla_transaction_log");
		logger.addAppender(systemFileAppender);
		systemFileAppender.activateOptions();
		logger.setLevel(Level.INFO);		
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

	
	/**
	 * Get the applcation logger (name is set in resource/config.properties)
	 * @return
	 */
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

				int ret = o1.getDivisionCode().compareTo(o2.getDivisionCode());
				return ret;

			}

		});
		return divisionList;
	}

	public static String getMessageText(Connection conn, MessageKey messageKey, String defaultText) throws Exception {
		String messageText = null;
		ApplicationProperties message = new ApplicationProperties();
		message.setPropertyId("MessageKey." + messageKey.name());
		try {
			message.selectOne(conn);
			messageText = message.getValueString();
		} catch ( RecordNotFoundException e ) {
			messageText = defaultText;
		}	
		return messageText;
	}

	public static String getResponseText(Connection conn, ResponseCode responseCode, String defaultText) throws Exception {
		String messageText = null;
		ApplicationProperties message = new ApplicationProperties();
		message.setPropertyId("ResponseCode." + responseCode.name());
		try {
			message.selectOne(conn);
			messageText = message.getValueString();
		} catch ( RecordNotFoundException e ) {
			messageText = defaultText;
		}	
		return messageText;
	}


	public static SessionUser getSessionUser(HttpServletRequest request) {
		SessionUser sessionUser = null;
		HttpSession session = request.getSession();
		if ( session != null ) {
			SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
			if ( sessionData != null ) {
				sessionUser = sessionData.getUser();
			}
		}
		return sessionUser;
	}


	
	
	/**
	 * Ensure that a user is logged in and has permission to execute the current function
	 * 
	 * @param request
	 * @param requiredPermission
	 * @return
	 * @throws TimeoutException
	 * @throws NotAllowedException
	 * @throws ExpiredLoginException
	 */
	public static SessionData validateSession(HttpServletRequest request, Permission requiredPermission, Integer requiredLevel) throws TimeoutException, NotAllowedException, ExpiredLoginException {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		// check for login
		if ( sessionData == null || sessionData.getUser() == null ) {
			throw new TimeoutException();
		} 
		
		// check for superuser, or that user has permission
		SessionUser user = sessionData.getUser();
		if ( ! user.getSuperUser().equals(User.SUPER_USER_IS_YES)) {
			boolean isAllowed = false;
			for ( UserPermission userPermission : sessionData.getUserPermissionList()) {
				Permission myPermission = Permission.valueOf(userPermission.getPermissionName());
				if ( myPermission.equals(requiredPermission)) {
					if ( userPermission.getLevel() >= requiredLevel ) {
						isAllowed = true;
					}
				}
			}
			if ( ! isAllowed ) {
	            throw new NotAllowedException();
	        }
		}
		
		return sessionData;
	}

	/**
	 * @throws UnsupportedEncodingException 
	 * Parses a query sting into name, value pairs
	 * 
	 * @param string
	 * @return Map<String,String>
	 * @throws 
	 */
	public static HashMap<String, String> getQueryMap(String queryString) throws UnsupportedEncodingException {
		HashMap<String, String> queryMap = new HashMap<String, String>();
		
		if ( ! StringUtils.isBlank(queryString)) {
		    String[] params = queryString.split("&");  
		    for (String param : params) {
		    	String[] pair = param.split("=");		    	
		        String name = pair[0];
		        String value = null;
		        if ( pair.length == 2 ) {
		        	value = pair[1];
		        	if ( ! StringUtils.isBlank(value)) {
		        		value = URLDecoder.decode(value, "UTF-8");
		        	}
		        }
		        queryMap.put(name, value);  
		    }
		}
		
	    return queryMap;  
	}

	public static String getRandomQuote() {
		String resultString = "Get to Work!";
		try {
			String webResource="resources/randomquotes.txt";
			InputStream is = AppUtils.class.getClassLoader().getResourceAsStream(webResource);
			Writer writer = new StringWriter();
			 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            String[] lines =  writer.toString().split("\n");
            int low = 0;
            int high = lines.length;
            int result = random.nextInt(high-low) + low;
            resultString = lines[result];
		} catch ( Exception e ) {
			AppUtils.logException(e);
		}
		return resultString;
	}
	
	public static void main(String[] args ) {
		for ( int i = 0; i < 100; i++ ) {
			for ( int j = 0; j < random.nextInt(100000); j++) {
				// doing nothing
			}
			System.out.println(AppUtils.getRandomQuote());			
		}
	}



	public static void logTransaction(HttpServletRequest request, String jsonString) {
        SessionUser user = AppUtils.getSessionUser(request);
        String userEmail = user == null ? "n/a" : user.getEmail();
        // Don't log passwords
        String logString = jsonString.replaceAll("\"password\":\".*\"", "\"password\":\"*******\"");
        Logger logger = Logger.getLogger(PropertyNames.TRANSACTION_LOG.toString());
        logger.info("User: " + userEmail + "\tParameters: " + logString);
		
	}
}
