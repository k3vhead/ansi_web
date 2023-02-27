package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class BcrTicketLookupServlet extends AbstractBcrTicketLookupServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void makeMyColumns() {
		cols = new String[] { 
				"job_site.name", //	Account
				"ticket.ticket_id",	//Ticket Number
				"ticket_claim.claim_week",	// ClaimWeek
				"dl_amt", //"D/L"  
				"total_volume", //"Total Volume"  
				"isnull(ticket_claim.volume,0.00)", //"Volume Claimed" 
				"ISNULL(ticket_claim.passthru_expense_volume,0.00)", // Expense
				"ISNULL(ticket_claim.passthru_expense_volume,0.00)", //"Volume Remaining"  
				"notes", //"Notes"  
				"isnull(invoice_totals.invoiced_amount,0.00)", //"Billed Amount"  
				"(isnull(ticket_claim_totals.claimed_total_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00)", //"Diff Clm/Bld"  
				"ticket_status", //"Ticket Status"  
				"job_tag.abbrev", //"Service"  
				"tag_list.equipment_tags", //"Equipment"  
				"ticket_claim.employee_name", //"Employee"
		};
	}
	
	protected void processLookup(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, request.getRequestURI());
		
		boolean errorFound = false;
		String divisionString = request.getParameter(DIVISION_ID);
		String workYearString = request.getParameter(WORK_YEAR);
		String workWeeks = request.getParameter(WORK_WEEKS);  // comma-delimited list of work weeks.
		if(StringUtils.isBlank(divisionString)) {
			errorFound = true;
		} 
		if(!StringUtils.isNumeric(divisionString)) {
			errorFound = true;
		}
		if(!StringUtils.isNumeric(workYearString)) {
			errorFound = true;
		}
		if(StringUtils.isBlank(workWeeks)) {
			errorFound = true;
		}
		if(errorFound) {
			super.sendNotFound(response);
		} else {
			super.doGet(request, response);
		}
		
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processLookup(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processLookup(request, response);
	}


}
