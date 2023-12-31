package com.ansi.scilla.web.invoice.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.AnsiTime;
// import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.request.InvoiceGenerationRequest;
import com.ansi.scilla.web.invoice.response.InvoiceGenerationResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;



public class InvoiceGenerationServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {		
		super.sendNotAllowed(response);
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
//		AnsiURL ansiURL = null; 
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				String jsonString = super.makeJsonString(request);
				InvoiceGenerationRequest invoiceGenerationRequest = (InvoiceGenerationRequest)AppUtils.json2object(jsonString, InvoiceGenerationRequest.class);
//				ansiURL = new AnsiURL(request, "invoiceGeneration", (String[])null); //  .../ticket/etc
				//kjw SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
				SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE_WRITE);
				
				SessionUser sessionUser = sessionData.getUser(); 
				List<String> addErrors = super.validateRequiredAddFields(invoiceGenerationRequest);
				if ( addErrors.isEmpty() ) {
					processUpdate(conn, response, invoiceGenerationRequest, sessionUser);
				} else {
					processError(conn, response, addErrors);
				}
				conn.commit();
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				InvoiceGenerationResponse data = new InvoiceGenerationResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
				super.sendForbidden(response);
			}
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		super.sendNotAllowed(response);
	}


	private void processUpdate(Connection conn, HttpServletResponse response, InvoiceGenerationRequest invoiceGenerationRequest, SessionUser sessionUser) throws Exception {
		Calendar invoiceDate = Calendar.getInstance(new AnsiTime());
		invoiceDate.setTime(invoiceGenerationRequest.getInvoiceDate());
		Boolean monthlyFlag = invoiceGenerationRequest.getMonthlyFlag();
		Integer userId = sessionUser.getUserId();
		InvoiceUtils.generateInvoices(conn, invoiceDate, monthlyFlag, userId);
		
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
		InvoiceGenerationResponse data = new InvoiceGenerationResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
		
	}


	private void processError(Connection conn, HttpServletResponse response, List<String> addErrors) throws Exception {
		WebMessages webMessages = new WebMessages();
		for ( String error : addErrors ) {
			webMessages.addMessage(error, "Required field");
		}
		
		InvoiceGenerationResponse data = new InvoiceGenerationResponse();
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
	}





	
}
