package com.ansi.scilla.web.tax.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.LocaleTaxRate;
import com.ansi.scilla.common.db.TaxRateType;
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
	
	public static final String RATE_TYPE_ID = "rateTypeId";
	public static final String EFFECTIVE_DATE = "effectiveDate";
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		AnsiURL ansiURL = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);
			ansiURL = new AnsiURL(request, REALM, (String[])null); 

			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			LocaleTaxRateResponse data = new LocaleTaxRateResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				LocaleTaxRateRequest localeTaxRateRequest = new LocaleTaxRateRequest();
				AppUtils.json2object(jsonString, localeTaxRateRequest);
								
				if(localeTaxRateRequest.getKeyRateTypeId() == null || localeTaxRateRequest.getKeyEffectiveDate() == null) {
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

		String effectiveDateParm = request.getParameter(EFFECTIVE_DATE);
		String rateTypeIdParm = request.getParameter(RATE_TYPE_ID);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		
		
		AnsiURL ansiURL = null;
		try {
			Date effectiveDate = StringUtils.isBlank(effectiveDateParm) ? null : sdf.parse(effectiveDateParm);
			Integer rateTypeId = StringUtils.isBlank(rateTypeIdParm) ? null : Integer.valueOf(rateTypeIdParm);

			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.TAX_READ);
			Connection conn = null;
			LocaleTaxRateResponse taxRateResponseResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				taxRateResponseResponse = new LocaleTaxRateResponse(conn, ansiURL.getId(), effectiveDate, rateTypeId);
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
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		}		
		
	}

	protected void doAdd(Connection conn, LocaleTaxRateRequest taxRateRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		LocaleTaxRate localeTaxRate = new LocaleTaxRate();
		makeTaxRate(conn, localeTaxRate, taxRateRequest, sessionData.getUser());
		localeTaxRate.insertWithNoKey(conn);
		conn.commit();
		LocaleTaxRateResponse localeTaxRateResponse = new LocaleTaxRateResponse(conn, localeTaxRate);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, localeTaxRateResponse);
	}
	
	
	
	protected void doUpdate(Connection conn, Integer localeId, 
		LocaleTaxRateRequest taxRateRequest, SessionData sessionData, HttpServletResponse response) throws Exception {
		LocaleTaxRate taxRate = new LocaleTaxRate();
		taxRate.setLocaleId(localeId);
		taxRate.selectOne(conn);
		makeTaxRate(conn, taxRate, taxRateRequest, sessionData.getUser());
		LocaleTaxRate key = new LocaleTaxRate();
		key.setLocaleId(localeId);
		key.setEffectiveDate(taxRateRequest.getKeyEffectiveDate());
		key.setTypeId(taxRateRequest.getKeyRateTypeId());
		taxRate.update(conn, key);
		conn.commit();
		LocaleTaxRateResponse taxRateResponse = new LocaleTaxRateResponse(conn, taxRate);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, taxRateResponse);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
		
	}

	private void makeTaxRate(Connection conn, LocaleTaxRate localeTaxRate, LocaleTaxRateRequest taxRateRequest, SessionUser sessionUser) throws Exception {
		Date today = new Date();
	
		localeTaxRate.setLocaleId(taxRateRequest.getLocaleId());
		localeTaxRate.setRateValue(taxRateRequest.getRateValue());
		localeTaxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		
		if ( StringUtils.isBlank(taxRateRequest.getTypeName())) {
			// we're using an existing tax type
			localeTaxRate.setTypeId(taxRateRequest.getTypeId());
		} else {
			// we're adding a new tax type
			Integer taxRateTypeId = makeTaxRateType(conn, taxRateRequest.getTypeName(), sessionUser);
			localeTaxRate.setTypeId(taxRateTypeId);
		}
		
		
		if ( localeTaxRate.getAddedBy() == null ) {
			localeTaxRate.setAddedBy(sessionUser.getUserId());
			localeTaxRate.setAddedDate(today);
		}
		localeTaxRate.setUpdatedBy(sessionUser.getUserId());
		localeTaxRate.setUpdatedDate(today);
	
	}

	private Integer makeTaxRateType(Connection conn, String typeName, SessionUser sessionUser) throws Exception {
		Date today = new Date();
		TaxRateType taxRateType = new TaxRateType();
		taxRateType.setAddedBy(sessionUser.getUserId());
		taxRateType.setAddedDate(today);
		taxRateType.setTypeName(typeName);
		taxRateType.setUpdatedBy(sessionUser.getUserId());
		taxRateType.setUpdatedDate(today);
		Integer taxRateTypeId = taxRateType.insertWithKey(conn);
		return taxRateTypeId;
	}
	
}
