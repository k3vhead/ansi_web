package com.ansi.scilla.web.claims.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.web.claims.query.PassthruExpenseLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class PassthruExpenseLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "passthruExpenseLookup";

	public PassthruExpenseLookupServlet() {
		super(Permission.CLAIMS_READ);
		cols = new String[] { 
				"ticket_claim_passthru.work_date",  
				"code.display_value",			// as passthru_expense_type,\n" + 
				"ticket_claim_passthru.passthru_expense_volume",
				"CONCAT(ansi_user.first_name, ', ', ansi_user.last_name)",
				"ticket_claim_passthru.notes",
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
			PassthruExpenseLookupQuery lookupQuery = new PassthruExpenseLookupQuery(user.getUserId(), divisionList, ticketFilter);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}




	


	
	
}
