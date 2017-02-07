package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.request.DivisionRequest;
import com.ansi.scilla.web.response.ticket.TicketReturnListResponse;
import com.ansi.scilla.web.struts.SessionUser;
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
	private String panel;
	private Integer id;
	private String remainder;
	
	/**
	 * Parses a URL of the form /<servlet>/<id>/<panel>
	 * @param expected servlet identifier "/job/", "/ticket/", "/quote/table/", etc.
	 * @param url of the form /<servlet>/<id>/<panel> where <servlet> and <panel> are String and <id> is integer
	 * @return Boolean - true == valid url, false == invalid url
	 * 
	 */
	Boolean parsePanelUrl ( String expected, String url ) {
		this.panel = "";
		this.id = null;
		
		System.out.println("parsePanelUrl(): Url:" + url);
		int idx = url.indexOf(expected);
		if ( idx > -1 ) {
			String myString = url.substring(idx + expected.length());
			String[] urlPieces = myString.split("/");
			System.out.println("parsePanelUrl(): myString:" + myString);
			if ( StringUtils.isBlank(urlPieces[0])) { // is the id blank?
				System.out.println("parsePanelUrl(): no id");
			} else if ( StringUtils.isNumeric(urlPieces[0])) { // is the id numeric?
				this.id = Integer.valueOf(urlPieces[0]);
				System.out.println("parsePanelUrl(): id:" + this.id);
				if ( StringUtils.isBlank(urlPieces[1])) { // is the panel blank?
					System.out.println("parsePanelUrl(): no panel");
				} else { // we have a panel
					System.out.println("parsePanelUrl(): panel:" + urlPieces[1]);
					this.panel = urlPieces[1];
					return(true);
				}
			} else { // not a valid id
				System.out.println("parsePanelUrl(): not a valid id");
			}
		} 
		return(false);
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		System.out.println("TicketReturn(): doGet(): Url:" + url);
		if(parsePanelUrl("/ticket/",url)) {
			System.out.println("Ticket(): doGet(): panel:" + this.panel);
			System.out.println("Ticket(): doGet(): ticketId:" + this.id);
			if ( this.panel.equals("return")) { // ticket return panel?
				System.out.println("Ticket(): doGet(): process ticket return panel");
				Connection conn = null;
				try {
					conn = AppUtils.getDBCPConn();

					TicketReturnListResponse ticketReturnListResponse = new TicketReturnListResponse(conn, this.id);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, ticketReturnListResponse);
				} catch(RecordNotFoundException recordNotFoundEx) {
					super.sendNotFound(response);
				} catch ( Exception e) {
					AppUtils.logException(e);
					throw new ServletException(e);
				} finally {
					AppUtils.closeQuiet(conn);
				}
			} else if ( this.panel.equals("invoice")) { // ticket invoice panel?
				/* Needs to return:
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
				 */
				System.out.println("Ticket(): doGet(): process ticket invoice panel");
				super.sendNotFound(response); // not coded yet
			} else {
				super.sendNotFound(response); // unexpected panel
			}
		} else {
			super.sendNotFound(response);
		}
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
