package com.ansi.scilla.web.invoice.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.invoice.query.InvoiceLookupQuery;
import com.ansi.scilla.web.invoice.request.InvoiceGenerationRequestTkt;
import com.ansi.scilla.web.invoice.response.InvoiceGenerationResponse;
import com.ansi.scilla.web.invoice.response.InvoiceGenerationTktResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;

public class InvoiceGenerationTktServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			try{
				conn = AppUtils.getDBCPConn();
				String jsonString = super.makeJsonString(request);
				InvoiceGenerationRequestTkt invoiceGenerationRequest = new InvoiceGenerationRequestTkt();
				AppUtils.json2object(jsonString, invoiceGenerationRequest);
				SessionData sessionData = AppUtils.validateSession(request, Permission.INVOICE_WRITE);
				
				SessionUser sessionUser = sessionData.getUser(); 
				InvoiceGenerationTktResponse tktResponse = invoiceGenerationRequest.validate(conn);
				
				boolean goodRequest = tktResponse.getWebMessages().isEmpty();
				goodRequest = goodRequest == true && ( tktResponse.getTicketErrorList() == null || tktResponse.getTicketErrorList().size() == 0 );
				if ( goodRequest ) {
					processUpdate(conn, response, invoiceGenerationRequest, sessionUser);
				} else {
					processError(conn, response, tktResponse);
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

	
	private void processUpdate(Connection conn, HttpServletResponse response,
			InvoiceGenerationRequestTkt invoiceGenerationRequest, SessionUser sessionUser) throws Exception {
		InvoiceGenerationTktResponse data = new InvoiceGenerationTktResponse();

		Calendar invoiceDate = Calendar.getInstance(new AnsiTime());
		invoiceDate.setTime(invoiceGenerationRequest.getInvoiceDate());
		Integer userId = sessionUser.getUserId();
		List<Integer> invoiceList = InvoiceUtils.generateInvoicesForTicketList(conn, invoiceDate, invoiceGenerationRequest.getTicketList(), userId);
		//Note: commit is done in the generateInvoices method
		
		String sql = InvoiceLookupQuery.sqlSelectClause + InvoiceLookupQuery.sqlFromClause + "where invoice.invoice_id in " + AppUtils.makeBindVariables(invoiceList);
		logger.log(Level.DEBUG, sql);
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, sessionUser.getUserId()); //user id
		for ( int i = 0; i < invoiceList.size(); i++ ) {
			ps.setInt(i+2, invoiceList.get(i));
			logger.log(Level.DEBUG, "Invoice: " + invoiceList.get(i));
		}
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			data.addInvoiceDisplay(rs);
		}
		rs.close();
		
		
		WebMessages webMessages = new WebMessages();
		webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
		data.setWebMessages(webMessages);
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	
	private void processError(Connection conn, HttpServletResponse response, InvoiceGenerationTktResponse tktResponse) throws Exception {
		super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, tktResponse);
	}

	
}
