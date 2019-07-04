package com.ansi.scilla.web.tax.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.LocaleTaxRate;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.tax.request.TaxRateCrudRequest;
import com.ansi.scilla.web.tax.response.TaxRateCrudResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;



public class TaxRateCrudServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "taxRate";
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TAX_WRITE);
			TaxRateCrudResponse data = new TaxRateCrudResponse();
			WebMessages webMessages = new WebMessages();

			
			try{
				TaxRateCrudRequest taxRateRequest = new TaxRateCrudRequest();
				AppUtils.json2object(jsonString, taxRateRequest);
				Date today = new Date();				
				SessionUser sessionUser = sessionData.getUser(); 
				
				LocaleTaxRate taxRate = new LocaleTaxRate();
				
				if(taxRateRequest.getLocaleId() == null) {
					//this is add
					webMessages = taxRateRequest.validateAdd(conn);
					if(webMessages.isEmpty() == true) {
						taxRate = doAdd(conn, taxRate, taxRateRequest);
					}
					
				} else {
					//this is update
					webMessages = taxRateRequest.validateUpdate(conn);
					if(webMessages.isEmpty() == true) {
						LocaleTaxRate key = new LocaleTaxRate();
						key.setLocaleId(taxRateRequest.getLocaleId());
						//key.setTaxRateId(taxRateRequest.getTypeId());
						taxRate = doUpdate(conn, key, taxRateRequest);
						taxRate.update(conn, key);
					}
				}
				
				
				data = new TaxRateCrudResponse(taxRateRequest.getLocaleId(), taxRateRequest.getName(), taxRateRequest.getStateName(), 
						taxRateRequest.getEffectiveDate(), taxRateRequest.getLocaleTypeId(), 
						taxRateRequest.getRateValue(), taxRateRequest.getTypeId(), taxRateRequest.getTypeName());
				
				try {
					taxRate.selectOne(conn);
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Duplicate");
				} catch ( RecordNotFoundException e) {
					taxRate.setAddedBy(sessionUser.getUserId());
					taxRate.setAddedDate(today);
					taxRate.setUpdatedBy(sessionUser.getUserId());
					taxRate.setUpdatedDate(today);
					taxRate.insertWithNoKey(conn);
					conn.commit();
					webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success");
				}
				data.setWebMessages(webMessages);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, data);		
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

	protected LocaleTaxRate doAdd(Connection conn, LocaleTaxRate taxRate, TaxRateCrudRequest taxRateRequest) throws Exception {
		
		taxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		taxRate.setRateValue(taxRateRequest.getRateValue());
		
		return taxRate;
	}
	
	protected LocaleTaxRate doUpdate(Connection conn, LocaleTaxRate taxRate, TaxRateCrudRequest taxRateRequest) throws Exception {
		
		taxRate.selectOne(conn);
		
		taxRate.setEffectiveDate(taxRateRequest.getEffectiveDate());
		taxRate.setRateValue(taxRateRequest.getRateValue());
		
		return taxRate;
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
		
	}
	
}
