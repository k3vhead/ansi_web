package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.request.DivisionRequest;
import com.ansi.scilla.web.response.code.CodeResponse;
import com.ansi.scilla.web.response.division.DivisionListResponse;
import com.ansi.scilla.web.response.division.DivisionResponse;
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
 * 		/code/new   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/code/<table>/<field>/<value> with parameters in the JSON
 * 
 * 
 * @author dclewis
 *
 */
public class DivisionServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		int idx = url.indexOf("/division/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/division/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
					super.sendNotFound(response);
				} else {
					try {
						doDeleteWork(conn, url);
						DivisionResponse divisionResponse = new DivisionResponse();
						super.sendResponse(conn, response, ResponseCode.SUCCESS, divisionResponse);
					} catch(RecordNotFoundException recordNotFoundEx) {
						super.sendNotFound(response);
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
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		int idx = url.indexOf("/division/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			String queryString = request.getQueryString();
			System.out.println("Query String: " + queryString);
			
			// Figure out what we've got:
			// "myString" is the piece of the URL that we actually care about
			String myString = url.substring(idx + "/division/".length());
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			System.out.println("DivisionServ 104: " + command);
			Connection conn = null;
			try {
				if ( StringUtils.isBlank(command)) {
					throw new RecordNotFoundException();
				}
				conn = AppUtils.getDBCPConn();

				DivisionListResponse divisionListResponse = doGetWork(conn, myString, queryString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, divisionListResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
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

	public DivisionListResponse doGetWork(Connection conn, String url, String qs) throws RecordNotFoundException, Exception {
		DivisionListResponse divisionListResponse = new DivisionListResponse();
		String[] x = url.split("/");
		if(x[0].equals("list")){
			divisionListResponse = new DivisionListResponse(conn);
		} else if (StringUtils.isNumeric(x[0])) {
			Integer divisionId = Integer.valueOf(x[0]);
			divisionListResponse = new DivisionListResponse(conn, divisionId);
		} else {
			throw new RecordNotFoundException();
		}
		return divisionListResponse;
		
	}
	
	public void doDeleteWork(Connection conn, String url) throws RecordNotFoundException, Exception {
		
		String[] x = url.split("/");
		
		if (StringUtils.isNumeric(x[0])){
			Division div = new Division();
			div.setDivisionId(Integer.valueOf(x[0]));
			
			try {
				DivisionUser divisionUser = new DivisionUser();
				divisionUser.setDivisionId(Integer.valueOf(x[0]));
				divisionUser.selectOne(conn);
				System.out.println("Hello Delete: " + x[0]);
				System.out.println("Cannot Delete, Users Inside");
			} catch (RecordNotFoundException e) {
				System.out.println("Hello Delete: " + x[0]);
				try {
					div.delete(conn);
					System.out.println("Deleted!");
				} catch(RecordNotFoundException er) {
					System.out.println("Error! Division Not Found!");
				}
			}
			
			
		} else {
			throw new RecordNotFoundException();
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
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
			DivisionRequest divisionRequest = new DivisionRequest(jsonString);
			
			Division division = null;
			ResponseCode responseCode = null;
			if ( command.equals(ACTION_IS_ADD) ) {
				WebMessages webMessages = validateAdd(conn, divisionRequest);
				if (webMessages.isEmpty()) {
					try {
						division = doAdd(conn, divisionRequest, sessionUser);
						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
						responseCode = ResponseCode.SUCCESS;
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
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
				
				
				DivisionResponse divisionResponse = new DivisionResponse(conn, division);
				super.sendResponse(conn, response, responseCode, divisionResponse);
				
			} else if ( urlPieces.length == 3 ) {   //  /<tableName>/<fieldName>/<value> = 3 pieces
//				System.out.println("Doing Update Stuff");				
//				WebMessages webMessages = validateAdd(conn, divisionRequest);
//				if (webMessages.isEmpty()) {
//					System.out.println("passed validation");
//					try {
//						Division key = new Division();
//						key.setTableName(urlPieces[0]);
//						key.setFieldName(urlPieces[1]);
//						key.setValue(urlPieces[2]);
//						System.out.println("Trying to do update");
//						division = doUpdate(conn, key, divisionRequest, sessionUser);
//						String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
//						responseCode = ResponseCode.SUCCESS;
//						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
//					} catch ( RecordNotFoundException e ) {
//						System.out.println("Doing 404");
//						super.sendNotFound(response);						
//					} catch ( Exception e) {
//						System.out.println("Doing SysFailure");
//						divisionResponse = DivisionResponse.SYSTEM_FAILURE;
//						AppUtils.logException(e);
//						String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
//						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
//					}
//				} else {
//					System.out.println("Doing Edit Fail");
//					divisionResponse = DivisionResponse.EDIT_FAILURE;
//				}
//				DivisionResponse divisionResponse = new DivisionResponse(division, webMessages);
//				super.sendResponse(conn, response, responseCode, divisionResponse);
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

	protected Division doAdd(Connection conn, DivisionRequest divisionRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Division division = new Division();
		division.setAddedBy(sessionUser.getUserId());
		division.setAddedDate(today);
		if ( ! StringUtils.isBlank(divisionRequest.getDescription())) {
			division.setDescription(divisionRequest.getDescription());
		}
		if ( divisionRequest.getParentId() != null) {
			division.setParentId(divisionRequest.getParentId());
		}
		division.setName(divisionRequest.getName());
		division.setDivisionId(divisionRequest.getDivisionId());
		division.setStatus(divisionRequest.getStatus());
		division.setDefaultDirectLaborPct(divisionRequest.getDefaultDirectLaborPct());
		division.setUpdatedBy(sessionUser.getUserId());
		division.setUpdatedDate(today);
		division.setDivisionNbr(divisionRequest.getDivisionNbr());
		division.setDivisionCode(divisionRequest.getDivisionCode());
		try {
			division.insertWithNoKey(conn);
		} catch ( SQLException e) {
			if ( e.getMessage().contains("duplicate key")) {
				throw new DuplicateEntryException();
			} else {
				AppUtils.logException(e);
				throw e;
			}
		} 
		return division;
	}
	
	protected WebMessages validateAdd(Connection conn, DivisionRequest divisionRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> missingFields = super.validateRequiredAddFields(divisionRequest);
		if ( ! missingFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			for ( String field : missingFields ) {
				webMessages.addMessage(field, messageText);
			}
		}
		return webMessages;
	}
	
	/*private DivisionListResponse makeDivisionListResponse(Connection conn) throws Exception {
		DivisionListResponse divisionListResponse = new DivisionListResponse(conn);
		return divisionListResponse;
	}*/

	/*private DivisionListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
		Integer divisionId = null;
		
		try {
			divisionId = Integer.valueOf(urlPieces[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is OK, just means we ran out of filters
		} catch (NumberFormatException e) {
			
		}
		Division division = new Division();
		
		division.setDivisionId(divisionId);
		
		
		division.selectOne(conn);
		List<Division> divisionList = new ArrayList<Division>();
		divisionList.add(division);
		DivisionListResponse divisionListResponse = new DivisionListResponse();
		divisionListResponse.setDivisionList(divisionList);
		return divisionListResponse;
	}*/

	
}
