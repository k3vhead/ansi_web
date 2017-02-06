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

//import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.common.exceptions.InvalidDeleteException;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
//import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.request.DivisionRequest;
//import com.ansi.scilla.web.response.code.CodeResponse;
import com.ansi.scilla.web.response.division.DivisionListResponse;
import com.ansi.scilla.web.response.division.DivisionResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * The url for HTTP DELETE will be of the form /division/&lt;id&gt;
 * 
 * The url for HTTP GET will be one of:
 * 		/division/list    (retrieves everything)
 * 		/division/&lt;id&gt;      (filters division table by id)
 * 
 * The url for adding a new record will be an HTTP POST to:
 * 		/division/add   with parameters in the JSON
 * 
 * The url for update will be an HTTP POST to:
 * 		/division/&lt;id&gt; with parameters in the JSON
 * 
 * 
 * @author dclewis
 * editer: jwlewis
 */
public class DivisionServlet extends AbstractServlet {

	public static final String REALM = "division";
	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			
			
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				
				try {
					doDeleteWork(conn, url.getId());
					conn.commit();
					DivisionResponse divisionResponse = new DivisionResponse();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, divisionResponse);
				} catch (InvalidDeleteException e) {
					String message = AppUtils.getMessageText(conn, MessageKey.DELETE_FAILED, "Invalid Delete");
					WebMessages webMessages = new WebMessages();
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
					DivisionResponse divisionResponse = new DivisionResponse();
					divisionResponse.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, divisionResponse);
				} catch(RecordNotFoundException recordNotFoundEx) {
					super.sendNotFound(response);
				}
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (ResourceNotFoundException e1) {
			super.sendNotFound(response);
		}
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
				
		try {
			AnsiURL url = new AnsiURL(request, REALM, new String[] { ACTION_IS_LIST });

		
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				DivisionListResponse divisionListResponse = new DivisionListResponse();
				if( ! StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_LIST)){
					divisionListResponse = new DivisionListResponse(conn);
				} else if (url.getId() != null) {
					divisionListResponse = new DivisionListResponse(conn, url.getId());
				} else {
					// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
					throw new RecordNotFoundException();
				}

				super.sendResponse(conn, response, ResponseCode.SUCCESS, divisionListResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

			
		} catch (ResourceNotFoundException e) {
			super.sendNotFound(response);
		}
		

	}

	
	public void doDeleteWork(Connection conn, Integer divisionId) throws RecordNotFoundException, InvalidDeleteException, Exception {

		Division div = new Division();
		div.setDivisionId(divisionId);

		DivisionUser divisionUser = new DivisionUser();
		divisionUser.setDivisionId(divisionId);

		try {
			divisionUser.selectOne(conn);
			throw new InvalidDeleteException();
		} catch (RecordNotFoundException e) {
			div.delete(conn);
		}
	}

	
	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		try {
			String jsonString = super.makeJsonString(request);
			AnsiURL url = new AnsiURL(request, REALM, new String[] {ACTION_IS_ADD});
			
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);

				
				// figure out if this is an "add" or an "update"								
				try {
					DivisionRequest divisionRequest = (DivisionRequest) AppUtils.json2object(jsonString, DivisionRequest.class);
					if ( ! StringUtils.isBlank(url.getCommand()) && url.getCommand().equals(ACTION_IS_ADD)) {
						processAddRequest(conn, response, sessionUser, divisionRequest);
					} else if ( url.getId() != null ) {
						processUpdateRequest(conn, response, url.getId(), sessionUser, divisionRequest);
					} else {
						super.sendNotFound(response);
					}
				} catch ( InvalidFormatException formatException) {
					processBadPostRequest(conn, response, formatException);
				}
			} catch ( Exception e ) {
				AppUtils.logException(e);
				AppUtils.rollbackQuiet(conn);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} catch ( ResourceNotFoundException e) {
			super.sendNotFound(response);
		}
		
	}

	
	
	private void processAddRequest(Connection conn, HttpServletResponse response, SessionUser sessionUser, DivisionRequest divisionRequest) throws Exception {	
		Division division = null;
		ResponseCode responseCode = null;

		WebMessages webMessages = validateAdd(conn, divisionRequest);
		if (webMessages.isEmpty()) {
			webMessages = validateFormat(conn, divisionRequest);
		}
		if (webMessages.isEmpty()) {
			try {
				division = doAdd(conn, divisionRequest, sessionUser);
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


		DivisionResponse divisionResponse = new DivisionResponse();
		if ( division != null ) {
			divisionResponse = new DivisionResponse(conn, division);
		}
		if ( ! webMessages.isEmpty()) {
			divisionResponse.setWebMessages(webMessages);
		}
		super.sendResponse(conn, response, responseCode, divisionResponse);
	}

	private void processUpdateRequest(Connection conn, HttpServletResponse response, Integer id, SessionUser sessionUser, DivisionRequest divisionRequest) throws Exception {	
		Division division = null;
		ResponseCode responseCode = null;
		WebMessages webMessages = validateAdd(conn, divisionRequest);
		if (webMessages.isEmpty()) {
			webMessages = validateFormat(conn, divisionRequest);
		}
		if (webMessages.isEmpty()) {
			try {
				Division key = new Division();
				key.setDivisionId(id);
				division = doUpdate(conn, key, divisionRequest, sessionUser);
				String message = AppUtils.getMessageText(conn, MessageKey.SUCCESS, "Success!");
				responseCode = ResponseCode.SUCCESS;
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
				conn.commit();
			} catch ( RecordNotFoundException e ) {
				super.sendNotFound(response);
				conn.rollback();
			} catch ( Exception e) {
				responseCode = ResponseCode.SYSTEM_FAILURE;
				AppUtils.logException(e);
				String messageText = AppUtils.getMessageText(conn, MessageKey.INSERT_FAILED, "Insert Failed");
				webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, messageText);
				conn.rollback();
			}
		} else {
			responseCode = ResponseCode.EDIT_FAILURE;
		}

		DivisionResponse divisionResponse = new DivisionResponse();
		if ( division != null ) {
			divisionResponse = new DivisionResponse(conn, division);
		}
		if ( ! webMessages.isEmpty()) {
			divisionResponse.setWebMessages(webMessages);
		}

		super.sendResponse(conn, response, responseCode, divisionResponse);
	}


	private void processBadPostRequest(Connection conn, HttpServletResponse response, InvalidFormatException formatException) throws Exception {
		WebMessages webMessages = new WebMessages();
		String field = findBadField(formatException.toString());
		String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Format");
		webMessages.addMessage(field, messageText);
		DivisionResponse divisionResponse = new DivisionResponse();
		divisionResponse.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, divisionResponse);
		
	}

	protected Division doAdd(Connection conn, DivisionRequest divisionRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		Division division = new Division();
		makeDivision(division, divisionRequest, sessionUser, today);
		division.setAddedBy(sessionUser.getUserId());
		division.setAddedDate(today);

		
		try {
			Integer divisionId = division.insertWithKey(conn);
			division.setDivisionId(divisionId);
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
	
	
	protected Division doUpdate(Connection conn, Division key, DivisionRequest divisionRequest, SessionUser sessionUser) throws Exception{
		Date today = new Date();
		Division division = new Division();
		division.setDivisionId(key.getDivisionId());
		division.selectOne(conn);
		makeDivision(division, divisionRequest, sessionUser, today);

		try {
			division.update(conn, key);
			conn.commit();
		} catch ( SQLException e) {
			AppUtils.logException(e);
			throw e;
		} 
		return division;
	}
	
	
	private Division makeDivision(Division division, DivisionRequest divisionRequest, SessionUser sessionUser, Date today) {
		if ( ! StringUtils.isBlank(divisionRequest.getDescription())) {
			division.setDescription(divisionRequest.getDescription());
		}
		if ( divisionRequest.getParentId() != null) {
			division.setParentId(divisionRequest.getParentId());
		}
		division.setStatus(divisionRequest.getStatus());
		division.setDefaultDirectLaborPct(divisionRequest.getDefaultDirectLaborPct());
		division.setUpdatedBy(sessionUser.getUserId());
		division.setUpdatedDate(today);
		division.setDivisionNbr(divisionRequest.getDivisionNbr());
		division.setDivisionCode(divisionRequest.getDivisionCode());
		
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
	
	protected WebMessages validateFormat(Connection conn, DivisionRequest divisionRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		List<String> badFormatFields = super.validateFormat(divisionRequest);
		if ( ! badFormatFields.isEmpty() ) {
			String messageText = AppUtils.getMessageText(conn, MessageKey.INVALID_DATA, "Invalid Format");
			for ( String field : badFormatFields ) {
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
