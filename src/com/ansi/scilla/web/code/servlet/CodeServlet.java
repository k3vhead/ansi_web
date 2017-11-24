package com.ansi.scilla.web.code.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.code.request.CodeRequest;
import com.ansi.scilla.web.code.response.CodeListResponse;
import com.ansi.scilla.web.code.response.CodeResponse;
import com.ansi.scilla.web.common.response.MessageKey;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /code/&lt;table&gt;/&lt;field&gt;/&lt;value&gt;
 * 
 * The url for get will be one of:<br />
 * 		/code/list    (retrieves everything)<br />
 * 		/code/&lt;table&gt;      (filters code table by tablename)<br />
 * 		/code/&lt;table&gt;/&lt;field&gt;	(filters code table tablename and field<br />
 * 		/code/&lt;table&gt;/&lt;field&gt;/&lt;value&gt;	(retrieves a single record)<br />
 * <br />
 * The url for adding a new record will be a POST to:<br />
 * 		/code/add   with parameters in the JSON<br />
 * <br />
 * The url for update will be a POST to:<br />
 * 		/code/&lt;table&gt;/&lt;field&gt;/&lt;value&gt; with parameters in the JSON<br />
 * <br />
 * 
 * @author dclewis
 *
 */
public class CodeServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;


	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		SessionData sessionData = null;
		Connection conn = null;
		try {			
			conn = AppUtils.getDBCPConn();			
			sessionData = AppUtils.validateSession(request, Permission.SYSADMIN, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			
			String sortBy = request.getParameter("sortBy");
			
			Logger logger = AppUtils.getLogger();
			logger.debug(sessionData);
			String url = request.getRequestURI();
			ParsedUrl parsedUrl = new ParsedUrl(url);			
			
			CodeListResponse codesListResponse = null;
			if ( parsedUrl.tableName.equals("list")) {
				// we're getting all the codes in the database
				codesListResponse = makeCodesListResponse(conn);
			} else {
				codesListResponse = makeFilteredListResponse(conn, parsedUrl);				
			}
			if ( ! StringUtils.isBlank(sortBy) && sortBy.equalsIgnoreCase("display")) {
				codesListResponse.sortByDisplay();
			}
			super.sendResponse(conn, response, ResponseCode.SUCCESS, codesListResponse);

		} catch (TimeoutException  | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
			
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
//		String queryString = request.getQueryString();
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			// figure out if this is an "add" or an "update"
			int idx = url.indexOf("/code/");
			String myString = url.substring(idx + "/code/".length());				
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			String jsonString = super.makeJsonString(request);
			CodeRequest codeRequest = new CodeRequest(jsonString);
			
			Code code = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				WebMessages webMessages = validateAdd(conn, codeRequest);
				if (webMessages.isEmpty()) {
					try {
						code = doAdd(conn, codeRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						conn.commit();
					} catch ( DuplicateEntryException e ) {
						String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
						responseCode = ResponseCode.EDIT_FAILURE;
					} catch ( Exception e ) {
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
					}
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
				}
				CodeResponse codeResponse = new CodeResponse(code, webMessages);
				super.sendResponse(conn, response, responseCode, codeResponse);
				
			} else if ( urlPieces.length == 3 ) {   //  /<tableName>/<fieldName>/<value> = 3 pieces
				WebMessages webMessages = validateAdd(conn, codeRequest);
				if (webMessages.isEmpty()) {
					try {
						ParsedUrl parsedUrl = new ParsedUrl(request.getRequestURI());
						Code key = new Code();
						key.setTableName(parsedUrl.tableName);
						key.setFieldName(parsedUrl.fieldName);
						key.setValue(parsedUrl.value);
						code = doUpdate(conn, key, codeRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
						conn.commit();
						CodeResponse codeResponse = new CodeResponse(code, webMessages);
						super.sendResponse(conn, response, responseCode, codeResponse);
					} catch ( RecordNotFoundException e ) {
						super.sendNotFound(response);						
					} catch ( Exception e) {
						responseCode = ResponseCode.SYSTEM_FAILURE;
						AppUtils.logException(e);
						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
						CodeResponse codeResponse = new CodeResponse(code, webMessages);
						super.sendResponse(conn, response, responseCode, codeResponse);
					}
				} else {
					responseCode = ResponseCode.EDIT_FAILURE;
					CodeResponse codeResponse = new CodeResponse(code, webMessages);
					super.sendResponse(conn, response, responseCode, codeResponse);
				}
			} else {
				super.sendNotFound(response);
			}
			
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		
		Connection conn = null;
		try {
			ParsedUrl parsedUrl = new ParsedUrl(url);
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			Code code = new Code();
			code.setTableName(parsedUrl.tableName);
			code.setFieldName(parsedUrl.fieldName);
			code.setValue(parsedUrl.value);
			code.delete(conn);
			CodeResponse codeResponse = new CodeResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, codeResponse);
			conn.commit();
		} catch ( RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	protected Code doAdd(Connection conn, CodeRequest codeRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Code code = new Code();
		code.setAddedBy(sessionUser.getUserId());
		code.setAddedDate(today);
		if ( ! StringUtils.isBlank(codeRequest.getDescription())) {
			code.setDescription(codeRequest.getDescription());
		}
		if ( ! StringUtils.isBlank(codeRequest.getDisplayValue())) {
			code.setDisplayValue(codeRequest.getDisplayValue());
		}
		code.setFieldName(codeRequest.getFieldName());
		code.setSeq(codeRequest.getSeq());
		code.setStatus(codeRequest.getStatus());
		code.setTableName(codeRequest.getTableName());
		code.setUpdatedBy(sessionUser.getUserId());
		code.setUpdatedDate(today);
		code.setValue(codeRequest.getValue());
		try {
			code.insertWithNoKey(conn);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
		return code;
	}


	protected Code doUpdate(Connection conn, Code key, CodeRequest codeRequest, SessionUser sessionUser) throws Exception {
		Logger logger = AppUtils.getLogger();
		logger.debug(key);
		logger.debug(codeRequest);
		Date today = new Date();
		Code code = new Code();
		if ( ! StringUtils.isBlank(codeRequest.getDescription())) {
			code.setDescription(codeRequest.getDescription());
		}
		if ( ! StringUtils.isBlank(codeRequest.getDisplayValue())) {
			code.setDisplayValue(codeRequest.getDisplayValue());
		}
		code.setFieldName(codeRequest.getFieldName());
		code.setSeq(codeRequest.getSeq());
		code.setStatus(codeRequest.getStatus());
		code.setTableName(codeRequest.getTableName());
		code.setUpdatedBy(sessionUser.getUserId());
		code.setUpdatedDate(today);
		code.setValue(codeRequest.getValue());
		// if we update something that isn't there, a RecordNotFoundException gets thrown
		// that exception get propagated and turned into a 404
		code.update(conn, key);		
		return code;
	}

	private CodeListResponse makeCodesListResponse(Connection conn) throws Exception {
		CodeListResponse codesListResponse = new CodeListResponse(conn);
		return codesListResponse;
	}

	private CodeListResponse makeFilteredListResponse(Connection conn, ParsedUrl parsedUrl) throws Exception {
		CodeListResponse codeListResponse = new CodeListResponse(conn, parsedUrl.tableName, parsedUrl.fieldName, parsedUrl.value);
		return codeListResponse;
	}

	
	protected WebMessages validateAdd(Connection conn, CodeRequest codeRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(codeRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}

	protected WebMessages validateUpdate(Connection conn, Code key, CodeRequest codeRequest) throws RecordNotFoundException, Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredUpdateFields(codeRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		// if we "select" the key, and it isn't found, a "RecordNotFoundException" is thrown.
		// That exception will propagate up the tree until it turns into a 404 message sent to the client
		Code testKey = (Code)key.clone(); 
		testKey.selectOne(conn);
		return webMessages;
	}

	
	public class ParsedUrl extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public String tableName;
		public String fieldName;
		public String value;
		public ParsedUrl(String url) throws RecordNotFoundException, UnsupportedEncodingException {
			int idx = url.indexOf("/code/");	
			if ( idx < 0 ) {
				throw new RecordNotFoundException();
			}
			String myString = url.substring(idx + "/code/".length());			
			String[] urlPieces = myString.split("/");
			if ( urlPieces.length >= 1 ) {
				this.tableName = URLDecoder.decode(urlPieces[0],"UTF-8");
			}
			if ( urlPieces.length >= 2 ) {
				this.fieldName = URLDecoder.decode(urlPieces[1],"UTF-8");
			}
			if ( urlPieces.length >= 3 ) {
				this.value = URLDecoder.decode(urlPieces[2],"UTF-8");
			}
		}
	}
}
