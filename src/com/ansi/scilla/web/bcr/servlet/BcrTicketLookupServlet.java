package com.ansi.scilla.web.bcr.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.query.BcrLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;

public class BcrTicketLookupServlet extends AbstractBcrTicketLookupServlet {
	
	private static final long serialVersionUID = 1L;

	
	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		logger.log(Level.DEBUG, "Making all-ticket Query");
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
		String workWeek = request.getParameter("workWeek");  // comma-delimited list of work weeks.
		logger.log(Level.DEBUG, "Parms: " + divisionId + " " + workYear + " " + workWeek);
		BcrLookupQuery lookupQuery = new BcrLookupQuery(user.getUserId(), divisionList, divisionId, workYear, workWeek);
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
		return lookupQuery;
		
	}
	
	
	
//	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
//
//		private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
//		@Override
//		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
//			Timestamp startTime = (Timestamp)arg0.get(START_TIME);
//			String display = "XXX";
//			if ( startTime != null ) {
//				display = sdf.format(startTime);
//				arg0.put(START_TIME, display);
//			}
//			return arg0;
//		}
//		
//	}

}
