package com.ansi.scilla.web.bcr.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.query.BcrWeeklyLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;

public class BcrWeeklyTicketLookupServlet extends AbstractBcrTicketLookupServlet {
	
	private static final long serialVersionUID = 1L;
	
	
	
	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		logger.log(Level.DEBUG, "Making weekly Query");
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
//			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		Integer divisionId = Integer.valueOf(request.getParameter("divisionId"));
		Integer workYear = Integer.valueOf(request.getParameter("workYear"));
		String workWeek = request.getParameter("workWeek");
		logger.log(Level.DEBUG, "Parms: " + divisionId + " " + workYear + " " + workWeek);
		BcrWeeklyLookupQuery lookupQuery = new BcrWeeklyLookupQuery(user.getUserId(), divisionList, divisionId, workYear, workWeek);
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
		return lookupQuery;
		
	}
	

}
