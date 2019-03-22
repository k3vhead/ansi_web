package com.ansi.scilla.web.claims.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.claims.WorkHoursType;
import com.ansi.scilla.web.claims.query.TicketStatusLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;

public class TicketStatusLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;

	public TicketStatusLookupServlet() {
		super(Permission.CLAIMS_READ);
		cols = new String[] { 
				"div, job_site_name, ticket.ticket_id, claimed_dl_amt, claimed_dl_exp",
				"claimed_dl_total, total_volume, claimed_volume, passthru_volume, claimed_volume_total",
				"remaining_volume, invoiced_amount, claimed_vs_billed, paid_amount, amount_due",
				"ticket_status"
				};
		super.itemTransformer = new ItemTransformer();
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		SessionUser user = AppUtils.getSessionUser(request);
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		LookupQuery lookupQuery = new TicketStatusLookupQuery(user.getUserId());
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
		return lookupQuery;
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
		private SimpleDateFormat weekFormatter = new SimpleDateFormat("yyyy-ww");
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String hoursType = (String)arg0.get("hours_type");
			if ( hoursType != null ) {
				WorkHoursType workHoursType = WorkHoursType.valueOf(hoursType);
				arg0.put("hours_description", workHoursType.getDescription());
			}
			
			java.sql.Timestamp workDate = (java.sql.Timestamp)arg0.get("work_date");
			if ( workDate != null ) {				
				arg0.put("work_date", dateFormatter.format(workDate));	
				arg0.put("week", weekFormatter.format(workDate));
			}
			
			
			return arg0;
		}
		
	}


	
	
}
