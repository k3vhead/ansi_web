package com.ansi.scilla.web.tax.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.db.LocaleTaxRate;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.tax.request.LocaleTaxRateRequest;
import com.ansi.scilla.web.tax.response.LocaleTaxRateResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class LocaleTaxRateServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "localeTaxRate";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL ansiURL = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleTaxRateResponse data = new LocaleTaxRateResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				LocaleTaxRateRequest localeTaxRateRequest = new LocaleTaxRateRequest();
				AppUtils.json2object(jsonString, localeTaxRateRequest);
								
				if(ansiURL.getId() == null) {
					//this is add
					webMessages = localeTaxRateRequest.validateAdd(conn);
					if(webMessages.isEmpty() == true) {
						doAdd(conn, localeTaxRateRequest, sessionData, response);
					} else {
						data.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
					}
					
				} else {
					//this is update
					webMessages = localeTaxRateRequest.validateUpdate(conn, ansiURL.getId());
					if(webMessages.isEmpty() == true) {
						doUpdate(conn, ansiURL.getId(), localeTaxRateRequest, sessionData, response);
					} else {
						data.setWebMessages(webMessages);
						super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
					}
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.TAX_READ);
			Connection conn = null;
			LocaleTaxRateResponse taxRateResponseResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				taxRateResponseResponse = new LocaleTaxRateResponse(conn, ansiURL.getId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponseResponse); 
				
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

	protected void doAdd(Connection conn, LocaleTaxRateRequest taxRateRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		LocaleTaxRate taxRate = new LocaleTaxRate();
		makeTaxRate(taxRate, taxRateRequest, sessionData.getUser());
		Integer localeId = taxRate.insertWithKey(conn);
		conn.commit();
		taxRate.setLocaleId(localeId);
		LocaleTaxRateResponse localeTaxRateResponse = new LocaleTaxRateResponse(taxRate, conn);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, localeTaxRateResponse);
	}
	
	private void makeTaxRate(LocaleTaxRate taxRate, LocaleTaxRateRequest taxRateRequest, SessionUser sessionUser) {
		Date today = new Date();

		if ( taxRateRequest.getLocaleId() != null ) {
			taxRate.setLocaleId(taxRateRequest.getLocaleId());
		}
		taxRate.setRateValue(taxRateRequest.getRateValue());
		taxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		taxRate.setTypeId(taxRateRequest.getTypeId());
		if ( taxRate.getAddedBy() == null ) {
			taxRate.setAddedBy(sessionUser.getUserId());
			taxRate.setAddedDate(today);
		}
		taxRate.setUpdatedBy(sessionUser.getUserId());
		taxRate.setUpdatedDate(today);

	}

	protected void doUpdate(Connection conn, Integer localeId, 
		LocaleTaxRateRequest taxRateRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		LocaleTaxRate taxRate = new LocaleTaxRate();
		taxRate.setLocaleId(localeId);
		taxRate.selectOne(conn);
		makeTaxRate(taxRate, taxRateRequest, sessionData.getUser());
		Locale key = new Locale();
		key.setLocaleId(localeId);
		taxRate.update(conn, key);
		conn.commit();
		LocaleTaxRateResponse taxRateResponse = new LocaleTaxRateResponse(taxRate, conn);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponse);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
		
	}
	
}
