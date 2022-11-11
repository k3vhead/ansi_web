package com.ansi.scilla.web.contact.servlet;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.contact.ContactStatus;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.contact.query.ContactLookupQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class ContactLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "contactLookup";
	public static final String STATUS_DISPLAY = "status_display";
	public static final String FIELD_VALID_ONLY = "validOnly";
	
	
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
				ContactLookupQuery.CONTACT_STATUS,
				};
		super.itemTransformer = new ItemTransformer();
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
			String validParm = request.getParameter(FIELD_VALID_ONLY);
			Boolean validOnly = StringUtils.isBlank(validParm) || ! validParm.equalsIgnoreCase("false"); // if there's any doubt, go valid only
			logger.log(Level.DEBUG, "Valid Only: [" + validParm + "][" + validOnly + "]");
					
			ContactLookupQuery lookupQuery = new ContactLookupQuery(user.getUserId(), divisionList, validOnly);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String statusDisplay = null;
			if ( arg0.containsKey(ContactLookupQuery.CONTACT_STATUS) ) {
				statusDisplay = ContactStatus.valueOf((String)arg0.get(ContactLookupQuery.CONTACT_STATUS)).display();				
			}
			arg0.put(STATUS_DISPLAY, statusDisplay);
					
			return arg0;
		}
		
	}


	
	
}
