package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.common.jobticket.TicketStatus;
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
import com.ansi.scilla.web.ticket.request.TicketReturnRequest;
import com.ansi.scilla.web.ticket.response.TicketReturnResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * The url for delete will return methodNotAllowed
 * 
 * The url for get will be one of:
 * 		/ticket/<ticketId>	json includes panel="return" -- (returns ticket return fields for a given ticket)
 * 			Needs to return:
 * 				ticket
 * 				status
 * 				division
 * 				processDate
 * 				processNotes
 * 				actDl
 * 				actDlPct
 * 				actPricePerCleaning
 * 				billSheet
 * 				customerSignature
 * 				mgrApproval
 * 				nextAllowedStatusList
 * 				jobId - passed to job panels 
 * 
 * 		/ticket/<ticketId>	json includes panel="invoice" -- (returns ticket invoice fields for a given ticket)
 * 				Includes the invoice detail for the ticket and the invoice totals for the invoice
 * 				Needs to return:
				 * 		for Ticket = ticketId
				 * 			actPpc
				 * 			actTax
				 * 			sumTktPpcPaid - sum(ticket_payment.amount)
				 * 			sumTktTaxPaid - sum(ticket_payment.tax_amt)
				 * 			balance(actPpc + actTax - (sumTcktPpcPaid + sumTktTaxPaid))
				 * 			daysToPay(today, invoiceDate, balance) 
				 * 					if balance == 0, daysToPay = max(paymentDate)-invoiceDate
				 * 					if balance <> 0, daysToPay = today - invoiceDate
				 * 			**ticket write off amount - stub for v 2.0
				 * 		for Invoice = invoiceId
				 * 			invoice_id (this is the invoice number)
				 * 			sumInvPpc - sum(invoice.ticket.act_price_per_cleaning)
				 * 			sumInvTax - sum(invoice.ticket.act_tax_amt)
				 * 			sumInvPpcPaid - sum(invoice.ticket_payment.amount)
				 * 			sumInvTaxPaid - sum(invoice.ticket_payment.tax_amt)
				 * 			balance(sumInvPpc + sumInvTax - (sumInvPpcPaid + sumInvTaxPaid))
				 * 			**invoice write off amount - stub for v 2.0
				 * 			**invoice MSFC amount - stub for v 2.0
				 * 			**invoice excess payment amount - stub for v 2.0
 * 					
 * 
 * The url for update will be a POST to:  
 * 		/ticket/<ticket>  json with panel="invoice" will return methodNotAllowed - invoice panel is read only
 * 
 * 		/ticket/<ticket>  json includes panel="return" with parameters in the JSON
 * 			Action 		Next Status			Parameters
 * 			complete	"C"omplete			<nextStatus><processDate><processNotes><actualPricePerCleaning>
 * 											<actualDirectLaborPct><actualDirectLabor>
 * 											<customerSignature><billSheet><managerApproval>
 * 			skip		"S"kipped			<nextStatus><processDate><processNotes>
 * 			void		"V"oided			<nextStatus><processDate><processNotes>
 * 			reject		"R"ejected			<nextStatus><processDate><processNotes>
 * 			re-queue	"N"ot Dispatched	<nextStatus>
 * 
 * 
 * Processing for POST:
 * 	if panel != "return" return "not found"
 * 	if invalid ticketId return "not found"
 * 	check nextStatus against ticket.status.nextValues()
 * 	if nextStatus is invalid return "403 forbidden"
 * 	else
 * 		nextStatus="C" 
 * 			validate processDate - make sure this is not more than 35 days in the past or more than 30 days in the future for now
 * 			validate processNotes - can be null or any valid string
 * 			validate actualPricePerCleaning - can be 0 cannot be null
 * 			validate actualDirectLabor - can be 0 cannot be null
 * 			validate customerSignature, billSheet and managerApproval - these are checkboxes can either be checked or not
 * 			if values are valid update ticket table with values
 * 		nextStatus="S"/"V"/"R"/"N"
 * 			validate processDate - make sure this is not more than 35 days in the past or more than 30 days in the future for now
 * 			validate processNotes - cannot be null or blank
 * 			if values are valid update ticket table with processDate and processNotes
 * 
 * @author ggroce
 */
public class TicketServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Parses a URL of the form /<servlet>/<id>/<panel>
	 * @param expected servlet identifier "/job/", "/ticket/", "/quote/table/", etc.
	 * @param url of the form /<servlet>/<id>/<panel> where <servlet> and <panel> are String and <id> is integer
	 * @return Boolean - true == valid url, false == invalid url
	 * 
	 */
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		processGetRequest(request, response, "ticket");		
	}


	protected void processGetRequest(HttpServletRequest request, HttpServletResponse response, String realm) throws ServletException {
		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, realm, (String[])null); //which panel to 122
			AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			Connection conn = null;
			TicketReturnResponse ticketReturnResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				ticketReturnResponse = new TicketReturnResponse(conn, ansiURL.getId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, ticketReturnResponse); //utility to send Json back
				
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
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null; 
		Connection conn = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);
			
			Ticket ticket = new Ticket();
			try{
				TicketReturnRequest ticketReturnRequest = new TicketReturnRequest();
				AppUtils.json2object(jsonString, ticketReturnRequest);
				ansiURL = new AnsiURL(request, "ticket", (String[])null); //  .../ticket/etc

				SessionUser sessionUser = sessionData.getUser(); 
				ticket.setTicketId(ansiURL.getId());
				ticket.selectOne(conn);
				if ( ticketReturnRequest.getNewStatus().equals(TicketStatus.COMPLETED.code())) {
					processComplete(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getNewStatus().equals(TicketStatus.SKIPPED.code())) {
					processSkip(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getNewStatus().equals(TicketStatus.VOIDED.code())) {
					processVoid(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getNewStatus().equals(TicketStatus.REJECTED.code())) {
					processReject(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getNewStatus().equals(TicketStatus.NOT_DISPATCHED.code())) {
					processRequeue(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else {
					// this is an error -- a bad action was requested
					super.sendNotAllowed(response);
				}
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				TicketReturnResponse data = new TicketReturnResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				data.setWebMessages(messages);
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
		
		super.sendNotAllowed(response);
	}


	protected void processComplete (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		// edit input fields to make sure everything is present and valid
		// if all input is good
		//		update the ticket with info from the request
		TicketReturnResponse ticketReturnResponse = new TicketReturnResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		boolean checkBoxChecked = false;
		if (ticketReturnRequest.getBillSheet() ) {
			checkBoxChecked = true;
		}
		if (ticketReturnRequest.getCustomerSignature() ) {
			checkBoxChecked = true;
		}
		if (ticketReturnRequest.getMgrApproval() ) {
			checkBoxChecked = true;
		}
		if ( ! checkBoxChecked ) {
			messages.addMessage(TicketReturnRequest.CUSTOMER_SIGNATURE, "At Least 1 Approval Required");
		}
		if (ticketReturnRequest.getProcessDate() == null) {
			logger.log(Level.DEBUG, "No process date");
			messages.addMessage(TicketReturnRequest.PROCESS_DATE, "Required Field");
		}
		if (ticketReturnRequest.getActPricePerCleaning() == null ) {
			logger.log(Level.DEBUG, "No act ppc");
			messages.addMessage(TicketReturnRequest.ACT_PRICE_PER_CLEANING, "Required Field");
		}
		if (ticketReturnRequest.getActDlPct() == null ) {
			logger.log(Level.DEBUG, "No act dl pct");
			messages.addMessage(TicketReturnRequest.ACT_DL_PCT, "Required Field");
		} else {
			BigDecimal testPct = ticketReturnRequest.getActDlPct().multiply(new BigDecimal(100D));
			if ( ! JobUtils.isValidDLPct(testPct)) {
				messages.addMessage(TicketReturnRequest.ACT_DL_PCT, "Invalid Direct Labor");
			}
		}
		if (ticketReturnRequest.getActDlAmt() == null ) {
			logger.log(Level.DEBUG, "No act dl amt");
			messages.addMessage(TicketReturnRequest.ACT_DL_AMT, "Required Field");
		}
		if ( ! isValidNewStatus(ticket.getStatus(), ticketReturnRequest.getNewStatus())) {
			logger.log(Level.DEBUG, "invalid next status");
			messages.addMessage(TicketReturnRequest.NEW_STATUS, "Invalid status sequence");
		}
		if ( messages.isEmpty() ) {
			try {
				logger.log(Level.DEBUG, "process update");
				ticket.setStatus(TicketStatus.COMPLETED.code());
				//required fields
				ticket.setProcessDate(ticketReturnRequest.getProcessDate());
				ticket.setActPricePerCleaning(ticketReturnRequest.getActPricePerCleaning());
				ticket.setActDlPct(ticketReturnRequest.getActDlPct());
				ticket.setActDlAmt(ticketReturnRequest.getActDlAmt());
				Job job = new Job();
				job.setJobId(ticket.getJobId());
				job.selectOne(conn);
				if (job.getTaxExempt() == 0) {
					TaxRate taxRate = JobUtils.getTaxRate( conn, ticket.getJobId(), ticketReturnRequest.getProcessDate(), sessionUser.getUserId());
					ticket.setActTaxAmt(ticket.getActPricePerCleaning().multiply(taxRate.getRate()));
					ticket.setActTaxRateId(taxRate.getTaxRateId());
				} else {
					ticket.setActTaxAmt(new BigDecimal( "0.00" ));
					ticket.setActTaxRateId(0);
				}
				//optional fields
				if(StringUtils.isBlank(ticketReturnRequest.getProcessNotes())){
					ticket.setProcessNotes("");
				} else {
					ticket.setProcessNotes(ticketReturnRequest.getProcessNotes());
				} 
				if ( StringUtils.isBlank(ticketReturnRequest.getActPoNumber())) {
					ticket.setActPoNumber(null);
				} else {
					ticket.setActPoNumber(ticketReturnRequest.getActPoNumber());
				}
				if (ticketReturnRequest.getCustomerSignature() != null && ticketReturnRequest.getCustomerSignature()){
					ticket.setCustomerSignature(Ticket.CUSTOMER_SIGNATURE_IS_YES);
				} 
				if (ticketReturnRequest.getMgrApproval() != null && ticketReturnRequest.getMgrApproval()){
					ticket.setMgrApproval(Ticket.MGR_APPROVAL_IS_YES);
				} 
				if (ticketReturnRequest.getBillSheet() != null && ticketReturnRequest.getBillSheet()){
					ticket.setBillSheet(Ticket.BILL_SHEET_IS_YES);
				}
				doTicketUpdate(conn, ticket, sessionUser);
				conn.commit();
				responseCode = ResponseCode.SUCCESS;
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				ticketReturnResponse = new TicketReturnResponse(conn, ticket.getTicketId());
			} catch ( RecordNotFoundException e) {
				logger.log(Level.DEBUG, "update failed");
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		logger.log(Level.DEBUG, "messages:"+messages);
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
		
	}


	protected void processSkip (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {

		TicketReturnResponse ticketReturnResponse = new TicketReturnResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;

		if (ticketReturnRequest.getProcessDate() == null) {
			messages.addMessage("processDate", "Required Field");
		}
		if (StringUtils.isBlank(ticketReturnRequest.getProcessNotes())) {
			messages.addMessage("processNotes", "Required Field");
		}
		if ( ! isValidNewStatus(ticket.getStatus(), ticketReturnRequest.getNewStatus())) {
			messages.addMessage(TicketReturnRequest.NEW_STATUS, "Invalid status sequence");
		}

		if ( messages.isEmpty() ) {
			ticket.setStatus(TicketStatus.SKIPPED.code());
			//required fields
			ticket.setProcessDate(ticketReturnRequest.getProcessDate());
			//optional fields
			ticket.setProcessNotes(ticketReturnRequest.getProcessNotes());
			doTicketUpdate(conn, ticket, sessionUser);
			conn.commit();
			responseCode = ResponseCode.SUCCESS;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
			ticketReturnResponse = new TicketReturnResponse(conn, ticket.getTicketId());
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
		
	}


	protected void processVoid (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		TicketReturnResponse ticketReturnResponse = new TicketReturnResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;

		if (ticketReturnRequest.getProcessDate() == null) {
			logger.log(Level.DEBUG, "No process date");
			messages.addMessage("processDate", "Required Field");
		}
		if (StringUtils.isBlank(ticketReturnRequest.getProcessNotes())) {
			logger.log(Level.DEBUG, "No process notes");
			messages.addMessage("processNotes", "Required Field");
		}
		if ( ! isValidNewStatus(ticket.getStatus(), ticketReturnRequest.getNewStatus())) {
			logger.log(Level.DEBUG, "Invalid next status");
			messages.addMessage(TicketReturnRequest.NEW_STATUS, "Invalid status sequence");
		}

		if ( messages.isEmpty() ) {
			logger.log(Level.DEBUG, "valid void");
			ticket.setStatus(TicketStatus.VOIDED.code());
			//required fields
			ticket.setProcessDate(ticketReturnRequest.getProcessDate());
			//optional fields
			ticket.setProcessNotes(ticketReturnRequest.getProcessNotes());
			doTicketUpdate(conn, ticket, sessionUser);
			conn.commit();
			responseCode = ResponseCode.SUCCESS;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
			ticketReturnResponse = new TicketReturnResponse(conn, ticket.getTicketId());
		} else { 
			logger.log(Level.DEBUG, "Edit failure");
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
	}


	protected void processReject (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		TicketReturnResponse ticketReturnResponse = new TicketReturnResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;

		if (ticketReturnRequest.getProcessDate() == null) {
			messages.addMessage("processDate", "Required Field");
		}
		if (StringUtils.isBlank(ticketReturnRequest.getProcessNotes())) {
			messages.addMessage("processNotes", "Required Field");
		}
		if ( ! isValidNewStatus(ticket.getStatus(), ticketReturnRequest.getNewStatus())) {
			messages.addMessage(TicketReturnRequest.NEW_STATUS, "Invalid status sequence");
		}

		if ( messages.isEmpty() ) {
			ticket.setStatus(TicketStatus.REJECTED.code());
			//required fields
			ticket.setProcessDate(ticketReturnRequest.getProcessDate());
			//optional fields
			ticket.setProcessNotes(ticketReturnRequest.getProcessNotes());
			doTicketUpdate(conn, ticket, sessionUser);
			conn.commit();
			responseCode = ResponseCode.SUCCESS;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
			ticketReturnResponse = new TicketReturnResponse(conn, ticket.getTicketId());
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
		
	}


	protected void processRequeue (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		TicketReturnResponse ticketReturnResponse = new TicketReturnResponse();
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;

		if (ticketReturnRequest.getProcessDate() == null) {
			messages.addMessage("processDate", "Required Field");
		}
		if ( ! isValidNewStatus(ticket.getStatus(), ticketReturnRequest.getNewStatus())) {
			messages.addMessage(TicketReturnRequest.NEW_STATUS, "Invalid status sequence");
		}

		if ( messages.isEmpty() ) {
			ticket.setStatus(TicketStatus.NOT_DISPATCHED.code());
			//required fields
			ticket.setProcessDate(ticketReturnRequest.getProcessDate());
			//optional fields
			doTicketUpdate(conn, ticket, sessionUser);
			conn.commit();
			responseCode = ResponseCode.SUCCESS;
			messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
			ticketReturnResponse = new TicketReturnResponse(conn, ticket.getTicketId());
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
		
	}


	protected boolean isValidNewStatus(String status, String newStatus) {
		TicketStatus oldStatus = TicketStatus.lookup(status);
		TicketStatus checkThis = TicketStatus.lookup(newStatus);
		return oldStatus.nextValues().contains(checkThis);
	}


	private void doTicketUpdate(Connection conn, Ticket ticket, SessionUser sessionUser) throws Exception {
		Ticket key = new Ticket();
		//updateTicketHistory(conn, ticket);
		key.setTicketId(ticket.getTicketId());
		ticket.setUpdatedBy(sessionUser.getUserId());
		Date today = new Date();
		ticket.setUpdatedDate(today);
		ticket.update(conn, key);
	}
	

	/*
	private void updateTicketHistory(Connection conn, Ticket ticket) throws Exception {
		Ticket testTicket = new Ticket();
		testTicket.setTicketId(ticket.getTicketId());
		testTicket.selectOne(conn);
		testTicket.insertHistory(conn);
	}
	*/	




	
}
