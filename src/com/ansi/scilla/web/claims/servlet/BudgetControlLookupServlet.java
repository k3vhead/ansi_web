package com.ansi.scilla.web.claims.servlet;

import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.claims.query.BudgetControlLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class BudgetControlLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "budgetControlLookup";

	public BudgetControlLookupServlet() {
		super(Permission.CLAIMS_READ);
		cols = new String[] { 
				"div",
				"job_site_name",
				"ticket.ticket_id",
				"ticket.ticket_status",
				"claim_week",
				"claimed_weekly_dl_amt",
				"claimed_weekly_dl_exp",
				"claimed_weekly_dl_total",
				"total_volume",
				"claimed_volume_total",
				"volume_remaining",
				"billed_amount",
				"claimed_vs_billed",
				"paid_amount",
				"amount_due"
				};
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		SessionUser user = AppUtils.getSessionUser(request);
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			BudgetControlLookupQuery lookupQuery = new BudgetControlLookupQuery(user.getUserId());
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
