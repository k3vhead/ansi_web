package com.ansi.scilla.web.servlets.tickets;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.request.TicketReturnRequest;
import com.ansi.scilla.web.response.ticket.TicketReturnResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.ansi.scilla.web.struts.SessionData;
import com.ansi.scilla.web.struts.SessionUser;
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
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, "ticket", (String[])null); //which panel to 122
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
			TicketReturnRequest ticketReturnRequest = (TicketReturnRequest)AppUtils.json2object(jsonString, TicketReturnRequest.class);
			ansiURL = new AnsiURL(request, "ticket", (String[])null); //  .../ticket/etc
			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			SessionUser sessionUser = sessionData.getUser(); 
			
			Ticket ticket = new Ticket();
			try{
				ticket.setTicketId(ansiURL.getId());
				ticket.selectOne(conn);
				if ( ticketReturnRequest.getAction().equals(TicketReturnRequest.POST_ACTION_IS_COMPLETE)) {
					processComplete(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getAction().equals(TicketReturnRequest.POST_ACTION_IS_SKIP)) {
					processSkip(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getAction().equals(TicketReturnRequest.POST_ACTION_IS_VOID)) {
					processVoid(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getAction().equals(TicketReturnRequest.POST_ACTION_IS_REJECT)) {
					processReject(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else if ( ticketReturnRequest.getAction().equals(TicketReturnRequest.POST_ACTION_IS_REQUEUE)) {
					processRequeue(conn, response, ticket, ticketReturnRequest, sessionUser);
				} else {
					// this is an error -- a bad action was requested
					super.sendNotAllowed(response);
				}
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


	private void processComplete (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		// edit input fields to make sure everything is present and valid
		// if all input is good
		//		update the ticket with info from the request
		TicketReturnResponse ticketReturnResponse = null;
		WebMessages messages = new WebMessages();
		ResponseCode responseCode = null;
		if (ticketReturnRequest.getProcessDate() == null) {
			messages.addMessage(TicketReturnRequest.PROCESS_DATE, "Required Field");
		}
		if (ticketReturnRequest.getActPricePerCleaning() == null ) {
			messages.addMessage(TicketReturnRequest.ACT_PRICE_PER_CLEANING, "Required Field");
		}
		if (ticketReturnRequest.getActDlPct() == null ) {
			messages.addMessage(TicketReturnRequest.ACT_DL_PCT, "Required Field");
		}
		if (ticketReturnRequest.getActDlAmt() == null ) {
			messages.addMessage(TicketReturnRequest.ACT_DL_AMT, "Required Field");
		}
		if ( ! isValidNewStatus(ticket.getStatus(), ticketReturnRequest.getNewStatus())) {
			messages.addMessage(TicketReturnRequest.NEW_STATUS, "Invalid status sequence");
		}
		if ( messages.isEmpty() ) {
			try {
				ticket.setStatus(TicketStatus.COMPLETED.code());
				//required fields
				ticket.setProcessDate(ticketReturnRequest.getProcessDate());
				ticket.setActPricePerCleaning(ticketReturnRequest.getActPricePerCleaning());
				ticket.setActDlPct(ticketReturnRequest.getActDlPct());
				ticket.setActDlAmt(ticketReturnRequest.getActDlAmt());
				//optional fields
				if(!StringUtils.isBlank(ticketReturnRequest.getProcessNotes())){
					ticket.setProcessNotes(ticketReturnRequest.getProcessNotes());
				} 
				if (ticketReturnRequest.getCustomerSignature() != null && ticketReturnRequest.getCustomerSignature() == 1){
					ticket.setCustomerSignature(Ticket.CUSTOMER_SIGNATURE_IS_YES);
				} 
				if (ticketReturnRequest.getMgrApproval() != null && ticketReturnRequest.getMgrApproval() == 1){
					ticket.setMgrApproval(Ticket.MGR_APPROVAL_IS_YES);
				} 
				if (ticketReturnRequest.getBillSheet() != null && ticketReturnRequest.getBillSheet() == 1){
					ticket.setBillSheet(Ticket.BILL_SHEET_IS_YES);
				}
				doTicketUpdate(conn, ticket, sessionUser);
				conn.commit();
				responseCode = ResponseCode.SUCCESS;
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, "Update Successful");
				ticketReturnResponse = new TicketReturnResponse(conn, ticket.getTicketId());
			} catch ( RecordNotFoundException e) {
				responseCode = ResponseCode.EDIT_FAILURE;
			}
		} else { 
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
		
	}


	private void processSkip (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {

		TicketReturnResponse ticketReturnResponse = null;
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


	private void processVoid (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		TicketReturnResponse ticketReturnResponse = null;
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
			responseCode = ResponseCode.EDIT_FAILURE;
		}
		ticketReturnResponse.setWebMessages(messages);
		super.sendResponse(conn, response, responseCode, ticketReturnResponse);
	}


	private void processReject (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		TicketReturnResponse ticketReturnResponse = null;
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


	private void processRequeue (Connection conn, HttpServletResponse response, Ticket ticket, TicketReturnRequest ticketReturnRequest,
			SessionUser sessionUser) throws RecordNotFoundException, Exception {
		
		TicketReturnResponse ticketReturnResponse = null;
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


	private boolean isValidNewStatus(String status, String newStatus) {
		TicketStatus oldStatus = TicketStatus.lookup(status);
		TicketStatus checkThis = TicketStatus.lookup(newStatus);
		return oldStatus.nextValues().contains(checkThis);
	}


	private void doTicketUpdate(Connection conn, Ticket ticket, SessionUser sessionUser) throws Exception {
		Ticket key = new Ticket();
		key.setTicketId(ticket.getTicketId());
		ticket.setUpdatedBy(sessionUser.getUserId());
		Date today = new Date();
		ticket.setUpdatedDate(today);
		ticket.update(conn, key);
	}
	

	

/*	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		SessionUser sessionUser = AppUtils.getSessionUser(request);
		String url = request.getRequestURI();
		System.out.println("TicketReturn(): doPost(): Url:" + url);
		if(parsePanelUrl("/ticket/",url)) {
			System.out.println("Ticket(): doPost(): panel:" + this.panel);
			System.out.println("Ticket(): doPost(): ticketId:" + this.id);
			if ( this.panel.equals("return")) { // ticket return panel?
				System.out.println("Ticket(): doPost(): process ticket return panel");
				String jsonString = super.makeJsonString(request);
				try {
					TicketReturnRequest ticketReturnRequest = (TicketReturnRequest) AppUtils.json2object(jsonString, TicketReturnRequest.class);
					processTicketReturnPostRequest(conn, response, command, sessionUser, ticketReturnRequest);
				} catch ( InvalidFormatException formatException) {
					processBadPostRequest(conn, response, formatException);
				}

				SessionUser sessionUser = AppUtils.getSessionUser(request);
				String url = request.getRequestURI();
				
===============
				Connection conn = null;
				try {
					conn = AppUtils.getDBCPConn();
					conn.setAutoCommit(false);

					// figure out if this is an "add" or an "update"
					int idx = url.indexOf("/division/");
					String myString = url.substring(idx + "/division/".length());		
					String[] urlPieces = myString.split("/");
					String command = urlPieces[0];
					String jsonString = super.makeJsonString(request);
					try {
						DivisionRequest divisionRequest = (DivisionRequest) AppUtils.json2object(jsonString, DivisionRequest.class);
						processPostRequest(conn, response, command, sessionUser, divisionRequest);
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
				
				

			} else if ( this.panel.equals("invoice")) { // ticket invoice panel?
*/				/* Needs to return:
				 * 		for Ticket = ticketId
				 * 			act_price_per_cleaning
				 * 			act_tax_amt
				 * 			sum(ticket_payment.amount)
				 * 			sum(ticket_payment.tax_amt)
				 * 			balance(total of ppc + tax - (payment amt + payment tax))
				 * 			daysToPay(today, invoiceDate, balance) 
				 * 					if balance == 0, daysToPay = max(paymentDate)-invoiceDate
				 * 					if balance <> 0, daysToPay = today - invoiceDate
				 * 			**ticket write off amount - stub for v 2.0

				 * 		for Invoice = invoiceId
				 * 			invoice_id (this is the invoice number)
				 * 			sum(act_price_per_cleaning)
				 * 			sum(act_tax_amt)
				 * 			sum(ticket_payment.amount)
				 * 			sum(ticket_payment.tax_amt)
				 * 			balance(total of inv.ppc + inv.tax - (inv.payment amt + inv.payment tax))
				 * 			**invoice write off amount - stub for v 2.0
				 * 			**invoice MSFC amount - stub for v 2.0
				 * 			**invoice excess payment amount - stub for v 2.0
				 * 
				 */
/*				System.out.println("Ticket(): doGet(): process ticket invoice panel");
				super.sendNotFound(response); // not coded yet
			} else {
				super.sendNotFound(response); // unexpected panel
			}
		} else {
			super.sendNotFound(response);
		}
	}
*/


	
}
