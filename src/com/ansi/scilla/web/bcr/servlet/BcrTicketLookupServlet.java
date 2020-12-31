package com.ansi.scilla.web.bcr.servlet;

public class BcrTicketLookupServlet extends AbstractBcrTicketLookupServlet {
	
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

	

}
