package com.ansi.scilla.web.bcr.servlet;

import com.ansi.scilla.web.bcr.query.BcrTicketLookupQuery;

public class BcrWeeklyTicketLookupServlet extends AbstractBcrTicketLookupServlet {
	
	private static final long serialVersionUID = 1L;

	@Override
	protected void makeMyColumns() {
		cols = new String[] { 
				"job_site_name",
				"ticket_id",
				CLAIM_WEEK,
				BcrTicketLookupQuery.TICKET_TYPE,
				DIRECT_LABOR,
				EXPENSES,
				TOTAL_DIRECT_LABOR,
				TOTAL_VOLUME,
				TOTAL_CLAIMED,
				VOLUME_REMAINING,
				NOTES,
				BcrTicketLookupQuery.PRICE_PER_CLEANING,
				DIFF_CLM_BLD,
				BcrTicketLookupQuery.TICKET_STATUS,
				EMPLOYEE
		};
	}

	

}
