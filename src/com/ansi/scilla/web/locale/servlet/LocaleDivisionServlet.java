package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.LocaleDivision;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.locale.request.LocaleDivisionRequest;
import com.ansi.scilla.web.locale.response.LocaleDivisionResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class LocaleDivisionServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "localeDivision";
	public static final String LOCALE_ID = "localeId";
	public static final String EFFECTIVE_START_DATE = "effectiveStartDate";
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.TAX_READ);
			Connection conn = null;
			LocaleDivisionResponse localeDivisionResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
				String localeId = request.getParameter(LOCALE_ID);
				String effectiveStartDateString = request.getParameter(EFFECTIVE_START_DATE);
				if ( StringUtils.isBlank(effectiveStartDateString)) {
					throw new RecordNotFoundException();
				}
				Date effectiveStartDate = sdf.parse(effectiveStartDateString);
				Integer divisionId = ansiURL.getId();
				if ( ! StringUtils.isNumeric(localeId)) {
					throw new RecordNotFoundException();
				}
				localeDivisionResponse = new LocaleDivisionResponse(conn, Integer.valueOf(localeId), divisionId, effectiveStartDate);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, localeDivisionResponse); 
				
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (ResourceNotFoundException e1) {
			AppUtils.logException(e1);
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}		
		
		
		
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		AnsiURL ansiURL = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 

			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleDivisionResponse data = new LocaleDivisionResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				LocaleDivisionRequest localeDivisionRequest = new LocaleDivisionRequest();
				AppUtils.json2object(jsonString, localeDivisionRequest);

				if ( localeDivisionRequest.getAction().equals(LocaleDivisionRequest.ACTION_IS_ADD)) {
					processAdd(conn, request, response, sessionData, data, localeDivisionRequest);
				} else if ( localeDivisionRequest.getAction().equals(LocaleDivisionRequest.ACTION_IS_UPDATE)) {
					processUpdate(conn, request, response, sessionData, data, localeDivisionRequest);
				} else {
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Invalid Action. Reload and try again");
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}

						
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());				
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				//send a Bad Ticket message back
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		AnsiURL ansiURL = null;
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
	
			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleDivisionResponse data = new LocaleDivisionResponse();
			WebMessages webMessages = new WebMessages();
	
			
			try{
				LocaleDivisionRequest localeDivisionRequest = new LocaleDivisionRequest();
				AppUtils.json2object(jsonString, localeDivisionRequest);
	
				webMessages = localeDivisionRequest.validateDelete(conn);
				if(webMessages.isEmpty() == true) {
					Integer divId = ansiURL.getId();
					LocaleDivision localeDivision = new LocaleDivision();
					localeDivision.setDivisionId(divId);
					localeDivision.setLocaleId(localeDivisionRequest.getLocaleId());
					localeDivision.setEffectiveStartDate(localeDivisionRequest.getEffectiveStartDate());
					localeDivision.delete(conn);
					conn.commit();
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					data.setWebMessages(webMessages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
						
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());				
				webMessages.addMessage(badField, "Invalid Format");
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				//send a Bad Ticket message back
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}		
	}

	private void processAdd(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionData sessionData, LocaleDivisionResponse data, LocaleDivisionRequest localeDivisionRequest) throws Exception {
		WebMessages webMessages = localeDivisionRequest.validateAdd(conn);
		if ( webMessages.isEmpty() ) {
			doAdd(conn, localeDivisionRequest, sessionData, response);
		} else {
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}
	}


	
	private void processUpdate(Connection conn, HttpServletRequest request, HttpServletResponse response, SessionData sessionData, LocaleDivisionResponse data, LocaleDivisionRequest localeDivisionRequest) throws Exception {
		WebMessages webMessages = localeDivisionRequest.validateUpdate(conn);
		if ( webMessages.isEmpty() ) {
			doUpdate(conn, localeDivisionRequest, sessionData, response);
		} else {
			data.setWebMessages(webMessages);
			super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
		}
	}


	
	private void doAdd(Connection conn, LocaleDivisionRequest localeDivisionRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		LocaleDivision localeDivision = new LocaleDivision();
		makeLocaleDivision(localeDivision, localeDivisionRequest, sessionData.getUser());
		localeDivision.insertWithKey(conn);
		conn.commit();
//		localeDivision.setDivisionId(divisionId);
		LocaleDivisionResponse localeResponse = new LocaleDivisionResponse(conn, localeDivision);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, localeResponse);
	}

	
	
	

	private void doUpdate(Connection conn, LocaleDivisionRequest localeDivisionRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		LocaleDivision localeDivision = new LocaleDivision();
		localeDivision.setDivisionId(localeDivisionRequest.getDivisionId());
		localeDivision.setLocaleId(localeDivisionRequest.getLocaleId());
		localeDivision.setEffectiveStartDate(localeDivisionRequest.getEffectiveStartDate());
		try {
			localeDivision.selectOne(conn);
			makeLocaleDivision(localeDivision, localeDivisionRequest, sessionData.getUser());
			LocaleDivision key = new LocaleDivision();
			key.setDivisionId(localeDivisionRequest.getDivisionId());
			key.setLocaleId(localeDivisionRequest.getLocaleId());
			key.setEffectiveStartDate(localeDivisionRequest.getEffectiveStartDate());
			localeDivision.update(conn, key);
			conn.commit();
			LocaleDivisionResponse localeResponse = new LocaleDivisionResponse(conn, localeDivision);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, localeResponse);
		} catch ( RecordNotFoundException e) {
			logger.log(Level.DEBUG, "Updating non-exestent localeDivision localeid:" + localeDivisionRequest.getLocaleId() +
					"\tDivision: " + localeDivisionRequest.getDivisionId() + "\tStartDate: " + localeDivisionRequest.getEffectiveStartDate());
			super.sendNotFound(response);
		}
	}

	protected void makeLocaleDivision(LocaleDivision localeDivision, LocaleDivisionRequest localeDivisionRequest, SessionUser sessionUser)  {
		Date today = new Date();
		
		if ( localeDivisionRequest.getLocaleId() != null ) {
			localeDivision.setLocaleId(localeDivisionRequest.getLocaleId());
		}
		localeDivision.setDivisionId(localeDivisionRequest.getDivisionId());
		localeDivision.setLocaleId(localeDivisionRequest.getLocaleId());
		localeDivision.setEffectiveStartDate(localeDivisionRequest.getEffectiveStartDate());
		localeDivision.setEffectiveStopDate(localeDivisionRequest.getEffectiveStopDate());
		localeDivision.setAddressId(localeDivisionRequest.getAddressId());
		if ( localeDivision.getAddedBy() == null ) {
			localeDivision.setAddedBy(sessionUser.getUserId());
			localeDivision.setAddedDate(today);
		}
		localeDivision.setUpdatedBy(sessionUser.getUserId());
		localeDivision.setUpdatedDate(today);
	}
	
}
