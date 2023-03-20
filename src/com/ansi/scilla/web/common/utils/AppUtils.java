package com.ansi.scilla.web.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.ApplicationProperties;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.common.utils.PropertyNames;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.InvalidLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.db2.RecordNotFoundException;
import com.thewebthing.commons.lang.StringUtils;


public class AppUtils extends com.ansi.scilla.common.utils.AppUtils {
	private static final String KEYTEXT = "Y0u know, in sertin 0lder c1v1liz3d cultur3s, when m3n f@iled as entirely as y0u h@ve, they would thr0w th3ms3lv3s on they'r3 swords";

	private static Random random = new Random((new Date()).getTime());

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



	public static void checkPermission(Permission requiredPermission, List<UserPermission> userPermissionList) throws NotAllowedException {
		boolean isAllowed = false;
		for ( UserPermission userPermission : userPermissionList) {
			Permission myPermission = Permission.valueOf(userPermission.getPermissionName());
			if ( myPermission.equals(requiredPermission)) {
				isAllowed = true;
			}
		}
		if ( ! isAllowed ) {
	        throw new NotAllowedException();
	    }		
	}
	

	/**
	 * We have discovered that the HTML5 date and time tags are being translated at UTC, but we expect the 
	 * dates in the DB to be in US/Central (essentially, Chicago Time). This conversion should be handled by
	 * the JSON serializer, but that doesn't appear to be working as expected. Even if the serializer returns a
	 * chicago time, this code will just change Chicago to Chicago and all is good.
	 * @param date
	 * @return
	 */
	public static Date convertToChicagoTime(Calendar date) {
		TimeZone startTimeZone = date.getTimeZone();
		ZoneId zid = startTimeZone.toZoneId();
		ZonedDateTime startTime = ZonedDateTime.of(
				date.get(Calendar.YEAR),
				date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH),
				date.get(Calendar.HOUR_OF_DAY),
				date.get(Calendar.MINUTE),
				date.get(Calendar.SECOND),
				date.get(Calendar.MILLISECOND),
				zid
			);
		ZoneId chicagoZone = ZoneId.of("US/Central");
		ZonedDateTime adjustedTime = startTime.withZoneSameInstant(chicagoZone);
		Date adjustedDate = Date.from(adjustedTime.toInstant());
		return adjustedDate;
	}
	
	
	/**
	 * We have discovered that the HTML5 date and time tags are being translated at UTC, but we expect the 
	 * dates in the DB to be in US/Central (essentially, Chicago Time). This conversion should be handled by
	 * the JSON serializer, but that doesn't appear to be working as expected. Even if the serializer returns a
	 * chicago time, this code will just change Chicago to Chicago and all is good.
	 * 
	 * Use this method to when the date and time are in different fields from input
	 * 
	 * @param date
	 * @param time
	 * @return
	 */
	public static Date convertToChicagoTime(Calendar date, Calendar time) {
		TimeZone startTimeZone = date.getTimeZone();
		ZoneId zid = startTimeZone.toZoneId();
		ZonedDateTime startTime = ZonedDateTime.of(
				date.get(Calendar.YEAR),
				date.get(Calendar.MONTH) + 1,
				date.get(Calendar.DAY_OF_MONTH),
				time.get(Calendar.HOUR_OF_DAY),
				time.get(Calendar.MINUTE),
				0, //second
				0, //millisecond
				zid
			);
		ZoneId chicagoZone = ZoneId.of("US/Central");
		ZonedDateTime adjustedTime = startTime.withZoneSameInstant(chicagoZone);
		Date adjustedDate = Date.from(adjustedTime.toInstant());
		return adjustedDate;
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
	 * Get the applcation logger (name is set in resource/config.properties)
	 * @return
	 */
	public static Logger getLogger() {
		Logger logger = LogManager.getLogger(getProperty(PropertyNames.LOG_NAME));
		return logger;
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
	 * Parses a query sting into name, value pairs
	 * @param queryString
	 * @return
	 * @throws UnsupportedEncodingException
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
	 * Return a calendar object with the standard (midwest) ansi timezone
	 * @return
	 */
	public static Calendar getToday() {
		return Calendar.getInstance(new AnsiTime());
	}



	public static String jsonPost(HttpServletRequest request) throws UnsupportedEncodingException, IOException {
		Writer writer = new StringWriter();
		 
	    char[] buffer = new char[1024];
	    try {
	        Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
	        int n;
	        while ((n = reader.read(buffer)) != -1) {
	            writer.write(buffer, 0, n);
	        }
	    } finally {
	    	request.getInputStream().close();
	    }
	    String jsonString = writer.toString();
	    AppUtils.logTransaction(request, jsonString);
	    return jsonString;        
	}

	


	private static void logBadRequestSession(HttpServletRequest request) {
		Logger logger = LogManager.getLogger(AppUtils.class);
		logger.log(Level.FATAL, "**********  NULL SESSSION ERRROR ********");
		logger.log(Level.FATAL, "URI: " + request.getRequestURI());
		logger.log(Level.FATAL, "Query String: " + request.getQueryString());
		Enumeration<String> attrNames = request.getAttributeNames();
		if ( attrNames != null ) {
			while ( attrNames.hasMoreElements() ) {
				String name = attrNames.nextElement();
				logger.log(Level.FATAL, "Attr: " + name + "\t" + request.getAttribute(name));
			}
		}
		try {
			logger.log(Level.FATAL, "Post Data: " + jsonPost(request));
		} catch (UnsupportedEncodingException e1) {
			logger.log(Level.FATAL, e1.getMessage());
		} catch (IOException e1) {
			logger.log(Level.FATAL, e1.getMessage());
		}			
		
	}

	

	/**
	 * Log exception to the application logger
	 * @param e
	 */
	public static void logException(Exception e) {
		Logger logger = LogManager.getLogger(getProperty(PropertyNames.LOG_NAME));
	
		StringBuffer buf = new StringBuffer();
		buf.append(e.toString() + "\n");
		for ( StackTraceElement el : e.getStackTrace()) {
			buf.append(el.toString() + "\n");
		}
		logger.fatal(buf.toString());
	}



	public static void logTransaction(HttpServletRequest request, String jsonString) {
	    SessionUser user = AppUtils.getSessionUser(request);
	    String userEmail = user == null ? "n/a" : user.getEmail();
	    // Don't log passwords
	    String logString = jsonString.replaceAll("\"password\":\".*\"", "\"password\":\"*******\"");
	    Logger logger = LogManager.getLogger(PropertyNames.TRANSACTION_LOG.toString());
	    logger.info("User Login: " + userEmail + "\tParameters: " + logString);
		
	}

	

	/**
	 * Create an application logger.
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public static void makeApplicationLogger() throws IOException {
//		Properties properties = System.getProperties();
//		String catalinaBase = properties.getProperty("catalina.base");
//		if ( catalinaBase == null ) {
//			catalinaBase = "/Users/dclewis/Documents";
//		}
//		String fileSeparator = properties.getProperty("file.separator");
//
//		String logName = getProperty(PropertyNames.LOG_NAME);
//		String logfilename = logName.replaceAll("\\.", "_"); 
//
//		Logger logger = Logger.getLogger(logName);
//		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
//		FileAppender systemFileAppender = new FileAppender(
//				layout,
//				catalinaBase + fileSeparator + "logs" + fileSeparator + logfilename);
//		logger.addAppender(systemFileAppender);
//		systemFileAppender.activateOptions();
//		logger.setLevel(Level.DEBUG);
	}
	
	/**
	 * Create a logger to be used by apache utilities
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public static void makeApacheLogger() throws IOException {
//		Properties properties = System.getProperties();
//		String catalinaBase = properties.getProperty("catalina.base");
//		if ( catalinaBase == null ) {
//			catalinaBase = "/Users/dclewis/Documents";
//		}
//		String fileSeparator = properties.getProperty("file.separator");
//
//
//		Logger logger = Logger.getLogger("org.apache.struts");
//		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
//		FileAppender systemFileAppender = new FileAppender(
//				layout,
//				catalinaBase + fileSeparator + "logs" + fileSeparator + "com_scilla_apache_log");
//		logger.addAppender(systemFileAppender);
//		systemFileAppender.activateOptions();
//		logger.setLevel(Level.INFO);
	}

	public static String makeSessionKey() {
		boolean useLetters = true;
        boolean useNumbers = true;
        Integer passwordLength = 8;
        String code =  RandomStringUtils.random(passwordLength, useLetters, useNumbers);
        return code;

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



	/**
	 * Create a logger to be used by webthing utilities
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public static void makeWebthingLogger() throws IOException {
//		Properties properties = System.getProperties();
//		String catalinaBase = properties.getProperty("catalina.base");
//		if ( catalinaBase == null ) {
//			catalinaBase = "/Users/dcl/Documents";
//		}
//		String fileSeparator = properties.getProperty("file.separator");
//
//
//		Logger logger = Logger.getLogger("com.thewebthing");
//		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
//		FileAppender systemFileAppender = new FileAppender(
//				layout,
//				catalinaBase + fileSeparator + "logs" + fileSeparator + "com_thewebthing_log");
//		logger.addAppender(systemFileAppender);
//		systemFileAppender.activateOptions();
//		logger.setLevel(Level.DEBUG);
	}

	/**
	 * Create a logger to track all transactions
	 * @throws IOException
	 * @deprecated
	 */
	@Deprecated
	public static void makeTransactionLogger() throws IOException {
//		Properties properties = System.getProperties();
//		String catalinaBase = properties.getProperty("catalina.base");
//		if ( catalinaBase == null ) {
//			catalinaBase = "/Users/dcl/Documents";
//		}
//		String fileSeparator = properties.getProperty("file.separator");
//
//
//		Logger logger = Logger.getLogger(PropertyNames.TRANSACTION_LOG.toString());
//		PatternLayout layout = new PatternLayout(getProperty(PropertyNames.LOG_PATTERN));
//		FileAppender systemFileAppender = new FileAppender(
//				layout,
//				catalinaBase + fileSeparator + "logs" + fileSeparator + "ansi_scilla_transaction_log");
//		logger.addAppender(systemFileAppender);
//		systemFileAppender.activateOptions();
//		logger.setLevel(Level.INFO);		
	}



	/**
	 * Given a result set and metadata, create a hashmap (fieldname -> field value)
	 * @param rs
	 * @param rsmd
	 * @return
	 * @throws SQLException
	 */
	public static HashMap<String, Object> rs2Map(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		HashMap<String, Object> dataItem = new HashMap<String, Object>();
		for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
			int idx = i + 1;
			String column = rsmd.getColumnName(idx);
			Object value = rs.getObject(idx);
			
			dataItem.put(column, value);
		}
		return dataItem;
	}


	public static String stripHtml(String source) {
		String plainText = source.replaceAll("\\<.*?\\>", "");
		return plainText;
	}

	/**
	 * Ensure that a user is logged in. There is no permission/level requirement
	 * @param request
	 * @return
	 * @throws TimeoutException 
	 */
	public static SessionData validateSession(HttpServletRequest request) throws TimeoutException  {		
		HttpSession session = null;
		SessionData sessionData = null;
		try {
			session = request.getSession();
			sessionData = (SessionData)session.getAttribute(SessionData.KEY);			
		} catch ( Exception e ) {
			logBadRequestSession(request);
			throw new RuntimeException(e);
		}
		
		// check for login
		if ( sessionData == null || sessionData.getUser() == null ) {
			throw new TimeoutException();
		} 
		
		return sessionData;
	}
	
	
	/**
	 * Ensure that a user is logged in and has permission to execute the current function
	 * 
	 * "Required level" is deprecated. Use validateSEssion(HttpServletRequest request, Permission requiredPermission)
	 * 
	 * @param request
	 * @param requiredPermission
	 * @return
	 * @throws TimeoutException
	 * @throws NotAllowedException
	 * @throws ExpiredLoginException
	 */
	

	
	/**
	 * Validate that a user is logged in, and is allowed to be where he's trying to go
	 * 
	 * @param request
	 * @param requiredPermission
	 * @return
	 * @throws TimeoutException
	 * @throws NotAllowedException
	 * @throws ExpiredLoginException
	 */
	public static SessionData validateSession(HttpServletRequest request, Permission requiredPermission) throws TimeoutException, NotAllowedException, ExpiredLoginException {
		HttpSession session = null;
		SessionData sessionData = null;
		try {
			session = request.getSession();
			sessionData = (SessionData)session.getAttribute(SessionData.KEY);			
		} catch ( Exception e ) {
			logBadRequestSession(request);
			throw new RuntimeException(e);
		}
		
		// check for login
		if ( sessionData == null || sessionData.getUser() == null ) {
			throw new TimeoutException();
		} 
		
		// check for superuser, or that user has permission
		SessionUser user = sessionData.getUser();
		if ( ! user.getSuperUser().equals(User.SUPER_USER_IS_YES)) {
			checkPermission(requiredPermission, sessionData.getUserPermissionList());			
		}
		
		return sessionData;
	}
	
	
	
	public static void writeSpreadSheet(HttpServletResponse response, XSSFWorkbook workbook, String fileName) throws IOException {
//		Logger logger = LogManager.getLogger(AppUtils.class);
		
		response.setStatus(HttpServletResponse.SC_OK);
		OutputStream out = response.getOutputStream();
		
	    response.setHeader("Expires", "0");
	    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
	    response.setHeader("Pragma", "public");
	    // setting the content type
//	    response.setContentType("application/vnd.ms-excel");
	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    String dispositionHeader = "attachment; filename=" + fileName + ".xlsx";
		response.setHeader("Content-disposition",dispositionHeader);
	    // the contentlength
//	    response.setContentLength(baos.size());

		workbook.write(out);
		out.close();
	}
}
