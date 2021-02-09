package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

public class BcrWeeklyTicketLookupServlet extends AbstractBcrTicketLookupServlet {
	
	private static final long serialVersionUID = 1L;

	
	
	
	@Override
	protected void makeMyColumns() {
		cols = new String[] { 
			"job_site_name", //	Account
			"ticket_id",	//Ticket Number
			"claim_week",	// ClaimWeek
			"dl_amt", //"D/L"  
			"total_volume", //"Total Volume"  
			"volume_claimed", //"Volume Claimed" 
			"volume_remaining", //"Volume Remaining"  
			"notes", //"Notes"  
			"billed_amount", //"Billed Amount"  
			"claimed_vs_billed", //"Diff Clm/Bld"  
			"ticket_status", //"Ticket Status"  
			"service_tag_id", //"Service"  
			"equipment_tags", //"Equipment"  
			"employee", //"Employee" 
					
		};
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, request.getRequestURI());
		String divisionString = request.getParameter(DIVISION_ID);
		String workYearString = request.getParameter(WORK_YEAR);
		String workWeeks = request.getParameter(WORK_WEEKS);  // comma-delimited list of work weeks.
		String workWeek = request.getParameter(WORK_WEEK);  // the single week we want to look at
		boolean errorFound = false;
		if(StringUtils.isBlank(divisionString)) {
			errorFound = true;
		} 
		if(!StringUtils.isNumeric(divisionString) || !StringUtils.isNumeric(workYearString)) {
			errorFound = true;
		}
		if(StringUtils.isBlank(workWeeks)) {
			errorFound = true;
		}
		if(StringUtils.isBlank(workWeek)) {
			errorFound = true;
		}
		if(errorFound) {
			super.sendNotFound(response);
		} else {
			super.doGet(request, response);
		}
		
	}
	

}
