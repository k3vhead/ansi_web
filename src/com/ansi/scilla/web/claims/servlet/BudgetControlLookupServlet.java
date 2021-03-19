package com.ansi.scilla.web.claims.servlet;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.claims.query.BudgetControlLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class BudgetControlLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "budgetControlLookup";

	public BudgetControlLookupServlet() {
		super(Permission.CLAIMS_READ);
		cols = new String[] { 
				"CONCAT(division_nbr,'-',division_code)",
				"job_site.name",
				"ticket.ticket_id",
				"ticket.ticket_status",
				"ticket_claim_weekly_totals.claim_week",
				"job.budget",
				"isnull(ticket_claim_weekly_totals.claimed_weekly_dl_amt,0.00)",   //claimed_weekly_dl_amt
				"isnull(ticket_claim_weekly_totals.claimed_weekly_dl_exp,0.00)", // as claimed_weekly_dl_exp"
				"isnull(ticket_claim_weekly_totals.claimed_weekly_dl_amt,0.00)+ISNULL(ticket_claim_weekly_totals.claimed_weekly_dl_exp,0.00)", //as claimed_weekly_dl_total"
				"job.price_per_cleaning", // as total_volume
				"isnull(ticket_claim_totals.claimed_volume,0.00)", //as claimed_volume
				"ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)", //as passthru_volume
				"isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)", //as claimed_volume_total
				"job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))", //as volume_remaining"
				"isnull(invoice_totals.invoiced_amount,0.00)", // as billed_amount",
				"(isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))- isnull(invoice_totals.invoiced_amount,0.00)", // as claimed_vs_billed
				"ISNULL(ticket_payment_totals.paid_amount,0.00)", // as paid_amt
				"ISNULL(invoice_totals.invoiced_amount,0.00)-ISNULL(ticket_payment_totals.paid_amount,0.00)" // as amount_due"
				};
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			BudgetControlLookupQuery lookupQuery = new BudgetControlLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			if ( url.getId() != null ) {
				lookupQuery.setTicketFilter(url.getId());
			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
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
