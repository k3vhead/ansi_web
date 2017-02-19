package com.ansi.scilla.web.response.ticket;

import com.ansi.scilla.common.ApplicationObject;

public class TicketDetailResponse extends ApplicationObject {
	/*
	 *				ticket
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
	 *			actPpc
	 * 			actTax
	 * 			sumTktPpcPaid - sum(ticket_payment.amount)
	 * 			sumTktTaxPaid - sum(ticket_payment.tax_amt)
	 * 			balance(actPpc + actTax - (sumTcktPpcPaid + sumTktTaxPaid))
	 * 			daysToPay(today, invoiceDate, balance) 
	 * 					if balance == 0, daysToPay = max(paymentDate)-invoiceDate
	 * 					if balance <> 0, daysToPay = today - invoiceDate
	 * 			**ticket write off amount - stub for v 2.0
	*/
	
}
