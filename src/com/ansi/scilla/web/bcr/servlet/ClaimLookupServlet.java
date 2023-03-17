package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.bcr.common.BcrTicketLookupItemTransformer;
import com.ansi.scilla.web.bcr.query.ClaimLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.exceptions.InvalidParameterException;

public class ClaimLookupServlet extends AbstractLookupServlet {
	
	private static final long serialVersionUID = 1L;

	public ClaimLookupServlet() {
		super(Permission.CLAIMS_READ);
		makeMyColumns();
		super.itemTransformer = new BcrTicketLookupItemTransformer();
	}
	
	protected void makeMyColumns() {
		cols = new String[] { 
				"job_site.name", //	Account
				"concat(division.division_nbr,'-',division.division_code)", // Div
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
	
	
	
	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) throws InvalidParameterException {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);

		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();

		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		
		LookupQuery lookupQuery = new ClaimLookupQuery(user.getUserId(), divisionList);
		if ( ! StringUtils.isBlank(searchTerm)) {
			lookupQuery.setSearchTerm(searchTerm);
		}
		
		return lookupQuery;
	}
	
	

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}


}
