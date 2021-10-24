package com.ansi.scilla.web.contact.servlet;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.contact.query.ContactLookupQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class ContactLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "contactLookup";
	public static final String START_TIME = "start_time";
	
	
	public ContactLookupServlet() {
		super(Permission.CONTACT_READ);
		cols = new String[] {
				ContactLookupQuery.CONTACT_ID,
				ContactLookupQuery.LAST_NAME,
				ContactLookupQuery.FIRST_NAME,
				ContactLookupQuery.BUSINESS_PHONE,
				ContactLookupQuery.EMAIL,
				ContactLookupQuery.FAX,
				ContactLookupQuery.MOBILE_PHONE,
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
			ContactLookupQuery lookupQuery = new ContactLookupQuery(user.getUserId(), divisionList);
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
