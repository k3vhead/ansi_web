package com.ansi.scilla.web.callNote.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.web.callNote.query.CallNoteLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class CallNoteLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "callNoteLookup";

	public CallNoteLookupServlet() {
		super(Permission.CALL_NOTE_READ);
		cols = new String[] { 
				CallNoteLookupQuery.CALL_LOG_ID,
				CallNoteLookupQuery.ADDRESS_ID,
				CallNoteLookupQuery.ADDRESS_NAME,
				CallNoteLookupQuery.CONTACT_ID,
				CallNoteLookupQuery.CONTACT_NAME,
				CallNoteLookupQuery.SUMMARY,
				CallNoteLookupQuery.USER_ID,
				CallNoteLookupQuery.ANSI_CONTACT,
				CallNoteLookupQuery.START_TIME,
				CallNoteLookupQuery.CONTACT_TYPE,
				};
//		super.itemTransformer = new ItemTransformer();
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
			CallNoteLookupQuery lookupQuery = new CallNoteLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}




//	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
//
//		@Override
//		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
//			String ticketStatus = (String)arg0.get(BudgetControlLookupQuery.TICKET_STATUS);
//			if ( ! StringUtils.isBlank(ticketStatus) ) {
//				TicketStatus status = TicketStatus.lookup(ticketStatus);
//				arg0.put("ticket_status_description", status.display());
//			}
//			
//			return arg0;
//		}
//		
//	}


	
	
}
