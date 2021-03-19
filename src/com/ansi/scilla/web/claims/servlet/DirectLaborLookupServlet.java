package com.ansi.scilla.web.claims.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.claims.query.DirectLaborLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class DirectLaborLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "directLaborLookup";

	public DirectLaborLookupServlet() {
		super(Permission.CLAIMS_READ);
		cols = new String[] { 
				"ticket_claim.work_date",  
				"ansi_user.last_name", // as washer_last_name,\n" + 
				//"ansi_user.first_name",		// as washer_first_name,\n" + 
				"ticket_claim.volume", 
				"ticket_claim.dl_amt", 
				"ticket_claim.hours",
				"ticket_claim.notes"
			};
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
			Integer ticketFilter = url.getId();			
			DirectLaborLookupQuery lookupQuery = new DirectLaborLookupQuery(user.getUserId(), divisionList, ticketFilter);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}




	


	
	
}
