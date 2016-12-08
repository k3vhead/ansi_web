package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.response.codes.CodeListResponse;
import com.ansi.scilla.web.response.codes.CodeResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * The url for delete will be of the form /code/<table>/<field>/<value>
 * 
 * The url for get will be one of:
 * 		/code    (retrieves everything)
 * 		/code/<table>      (filters code table by tablename)
 * 		/code/<table>/<field>	(filters code table tablename and field
 * 		/code/<table>/<field>/<value>	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/code/add   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/code/<table>/<field>/<value> with parameters in the JSON
 * 
 * 
 * @author dclewis
 *
 */
public class CodeServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			String jsonString = super.makeJsonString(request);
			CodeRequest codeRequest = new CodeRequest(jsonString);
			System.out.println(codeRequest);
			Code code = new Code();
			code.setTableName(codeRequest.getTableName());
			code.setFieldName(codeRequest.getFieldName());
			code.setValue(codeRequest.getValue());
			code.delete(conn);
			
			CodeResponse codeResponse = new CodeResponse();
			super.sendResponse(conn, response, ResponseCode.SUCCESS, codeResponse);
			
			conn.commit();
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		int idx = url.indexOf("/code/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			String queryString = request.getQueryString();
			System.out.println("Query String: " + queryString);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/code/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
					super.sendNotFound(response);
				} else {
					if ( command.equals("list")) {
						// we're getting all the codes in the database
						CodeListResponse codesListResponse = makeCodesListResponse(conn);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, codesListResponse);
					} else {
						CodeListResponse codesListResponse = makeFilteredListResponse(conn, urlPieces);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, codesListResponse);
					}
				}
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response);
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
			System.out.println(jsonString);
			CodeRequest codeRequest = new CodeRequest(jsonString);
			
			if ( command.equals(ACTION_IS_ADD) ) {
				CodeResponse codeResponse = doAddWork(conn, codeRequest, sessionUser);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, codeResponse);
			} else if ( urlPieces.length == 3 ) {
				System.out.println("Doing Update Stuff");
				Code key = new Code();
				key.setTableName(urlPieces[0]);
				key.setFieldName(urlPieces[0]);
				key.setValue(urlPieces[2]);
				try {
					CodeResponse codeResponse = doUpdateWork(conn, key, codeRequest, sessionUser);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, codeResponse);
				} catch ( RecordNotFoundException e ) {
					super.sendNotFound(response);
				}
			} else {
				super.sendNotFound(response);
			}
			
			conn.commit();
		} catch ( Exception e ) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		}
		
	}

	public CodeResponse doAddWork(Connection conn, CodeRequest codeRequest, SessionUser sessionUser) throws Exception {
		CodeResponse codeResponse = new CodeResponse();
		Code code = null;
		WebMessages webMessages = validateAdd(conn, codeRequest);
		if ( webMessages.isEmpty() ) {
			try {
				code = doAdd(conn, codeRequest, sessionUser);
			} catch ( DuplicateEntryException e) {
				String messageText = AppUtils.getMessageText(conn, MessageKey.DUPLICATE_ENTRY, "Record already Exists");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);				
			} catch ( Exception e) {
				// insert failed
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
			}
			codeResponse.setCode(code);
		}
		if ( webMessages.isEmpty() ) {
			String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
		}
		codeResponse.setWebMessages(webMessages);
		return codeResponse;
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

	public CodeResponse doUpdateWork(Connection conn, Code key, CodeRequest codeRequest, SessionUser sessionUser) throws RecordNotFoundException, Exception {
		CodeResponse codeResponse = new CodeResponse();
		Code code = null;
		
		WebMessages webMessages = validateUpdate(conn, key, codeRequest);
		if ( webMessages.isEmpty() ) {
			code = doUpdate(conn, key, codeRequest, sessionUser);
		}
		if ( webMessages.isEmpty() ) {
			String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
		}
		codeResponse.setWebMessages(webMessages);
		codeResponse.setCode(code);
		return codeResponse;
	}

	protected Code doUpdate(Connection conn, Code key, CodeRequest codeRequest, SessionUser sessionUser) throws Exception {
		System.out.println("This is the key:");
		System.out.println(key);
		System.out.println("************");
		WebMessages webMessages = new WebMessages();
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
		try {
			code.update(conn, key);
		} catch ( Exception e) {
			AppUtils.logException(e);
			String messageText = AppUtils.getMessageText(conn, MessageKey.UPDATE_FAILED, "Update Failed");
			webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
		}
		return code;
	}

	private CodeListResponse makeCodesListResponse(Connection conn) throws Exception {
		CodeListResponse codesListResponse = new CodeListResponse(conn);
		return codesListResponse;
	}

	private CodeListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
		String tableName = null;
		String fieldName = null;
		String value = null;
		try {
			tableName = urlPieces[0];
			fieldName = urlPieces[1];
			value = urlPieces[2];
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is OK, just means we ran out of filters
		}
		Code code = new Code();
		if ( ! StringUtils.isBlank(tableName)) {
			code.setTableName(tableName);
		}
		if ( ! StringUtils.isBlank(fieldName)) {
			code.setFieldName(fieldName);
		}
		if ( ! StringUtils.isBlank(value)) {
			code.setValue(value);
		}
		List<Code> codeList = Code.cast(code.selectSome(conn));
		Collections.sort(codeList,
				new Comparator<Code>() {
			public int compare(Code o1, Code o2) {
				int ret = o1.getTableName().compareTo(o2.getTableName());
				if ( ret == 0 ) {
					ret = o1.getFieldName().compareTo(o2.getFieldName());
				}
				if ( ret == 0 ) {
					ret = o1.getValue().compareTo(o2.getValue());
				}
				return ret;
			}
		});
		CodeListResponse codeListResponse = new CodeListResponse();
		codeListResponse.setCodeList(codeList);
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
	
}
