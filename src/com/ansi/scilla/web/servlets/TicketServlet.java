package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.Invoice;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.response.ticket.InvoiceDetail;
import com.ansi.scilla.web.response.ticket.TicketReturnListResponse;
import com.ansi.scilla.web.response.ticket.TicketRecord;
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
			
			TicketRecord ticketId = new TicketRecord();
			
			Connection conn = null;
			TicketReturnListResponse ticketReturnListResponse = null;
			try {
				conn = AppUtils.getDBCPConn();
				
				
				super.sendResponse(conn, response, ResponseCode.SUCCESS, ticketReturnListResponse); //utility to send Json back
				
				
				
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
		}
	}
	
	protected void xxx(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		String panel = null;
		Integer ticketId = null;
		Connection conn = null;
		
		
		if(panel.equals("/ticket/"/*, ansiURL*/)) {
			System.out.println("Ticket(): doGet(): panel:" + panel);
			//System.out.println("Ticket(): doGet(): ticketId:" + ticketId.getTicketId());
			if (panel.equals("return")) { // ticket return panel?
				System.out.println("Ticket(): doGet(): process ticket return panel");

				try {
					conn = AppUtils.getDBCPConn();
					TicketReturnListResponse ticketReturnListResponse = null;

					//ticketReturnListResponse = new TicketReturnListResponse(conn, this.id);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, ticketReturnListResponse);
				} catch(RecordNotFoundException recordNotFoundEx) {
					super.sendNotFound(response);
				} catch ( Exception e) {
					AppUtils.logException(e);
					throw new ServletException(e);
				} finally {
					AppUtils.closeQuiet(conn);
				}
			} else if (panel.equals("invoice")) { // ticket invoice panel?
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
				Invoice invoiceId = new Invoice();
				invoiceId.getInvoiceId();
				doGetWork(invoiceId);
				
				System.out.println("Ticket(): doGet(): process ticket invoice panel");
				super.sendNotFound(response); // not coded yet
			} else {
				super.sendNotFound(response); // unexpected panel
			}
		} else {
			super.sendNotFound(response);
		}
	}
	
	public void doGetWork(Invoice invoiceId){
		InvoiceDetail invTax = new InvoiceDetail();
		invTax.getSumInvTax();
		
	}
	
	protected TicketReturnListResponse makeGetInvoiceResponse(Connection conn, AnsiURL ansiURL) throws Exception{
		 // ticket invoice panel?
			TicketReturnListResponse ticketReturnListResponse = new TicketReturnListResponse();
			Ticket ticket = new Ticket();
			ticket.setInvoiceId(Integer.valueOf(ansiURL.getQueryParameterMap().get("ticketId")[0]));
			List<Ticket> ticketList = Ticket.cast(ticket.selectSome(conn));
			List<TicketRecord> ticketReturnRecordList = new ArrayList<TicketRecord>();
			for(Ticket t: ticketList){
				TicketRecord r = new TicketRecord(t);
				ticketReturnRecordList.add(r);
			}
			
			ticketReturnListResponse.setTicketReturnList(ticketReturnRecordList);
			
			return ticketReturnListResponse;
	}
	
	protected TicketReturnListResponse makeGetYYYResponse(Connection conn, AnsiURL ansiURL) throws Exception{
		 // ticket invoice panel?
			TicketReturnListResponse ticketReturnListResponse = new TicketReturnListResponse();
			Ticket ticket = new Ticket();
			ticket.setInvoiceId(Integer.valueOf(ansiURL.getQueryParameterMap().get("ticketId")[0]));
			List<Ticket> ticketList = Ticket.cast(ticket.selectSome(conn));
			List<TicketRecord> ticketReturnRecordList = new ArrayList<TicketRecord>();
			for(Ticket t: ticketList){
				TicketRecord r = new TicketRecord(t);
				ticketReturnRecordList.add(r);
			}
			
			ticketReturnListResponse.setTicketReturnList(ticketReturnRecordList);
			
			return ticketReturnListResponse;
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
