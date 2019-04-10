package com.ansi.scilla.web.claims.servlet;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.claims.query.BudgetControlLookupQuery;
import com.ansi.scilla.web.claims.query.ClaimDetailLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;

public class ClaimDetailLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;

	public ClaimDetailLookupServlet() {
		super(Permission.CLAIMS_READ);
		cols = new String[] { 
				"CONCAT(division_nbr,'-',division_code)",
				"claim_week = case ticket_claim.work_date \r\n" +
				"      when null then null\r\n" +
				"      else concat(year(work_date),'-',right('00'+CAST(datepart(wk,work_date) as VARCHAR),2))\r\n" +
				"  end",
				"ticket_claim.work_date",
				"ticket.ticket_id",
				"ticket.ticket_status",
				"concat(ansi_user.last_name,', ', ansi_user.first_name)",
				"job.price_per_cleaning",
				"job.budget",
				"isnull(ticket_claim.dl_amt,0.00)",
				"0.00",
				"isnull(ticket_claim.dl_amt,0.00) + 0.00",
				"isnull(ticket_claim.hours,0.00)",
				"isnull(ticket_claim_totals.claimed_volume,0.00)",
				"isnull(ticket_claim_passthru_totals.passthru_volume,0.00)",
				"isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)",
				"job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))",
				"isnull(ticket_claim_totals.claimed_dl_amt,0.00)+ISNULL(ticket_claim_totals.claimed_dl_exp,0.00) claimed_dl_total",
				"job.budget - (isnull(ticket_claim_totals.claimed_dl_amt,0.00)+ISNULL(ticket_claim_totals.claimed_dl_exp,0.00))"

//				"ticket.job_id",
//				"job_site.name",
//				"job.om_notes",
//				"isnull(ticket_claim.volume,0.00)",
//				"isnull(ticket_claim_totals.claimed_dl_amt,0.00)",
//				"isnull(ticket_claim_totals.claimed_dl_exp,0.00)",
				
				};
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		LookupQuery lookupQuery = new ClaimDetailLookupQuery(user.getUserId(), divisionList);
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
		return lookupQuery;
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String ticketStatus = (String)arg0.get(BudgetControlLookupQuery.TICKET_STATUS);
			if ( ! StringUtils.isBlank(ticketStatus) ) {
				TicketStatus status = TicketStatus.lookup(ticketStatus);
				arg0.put("ticket_status_description", status.display());
			}
			
			return arg0;
		}
		
	}


	
	
}
