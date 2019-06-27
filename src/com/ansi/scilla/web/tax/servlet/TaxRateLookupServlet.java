package com.ansi.scilla.web.tax.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.locale.query.LocaleLookupQuery;

public class TaxRateLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "taxRateLookup";
	
	public static final String LOCALE_ID = "locale_id";
	public static final String EFFECTIVE_DATE = "effective_date";
	public static final String RATE_VALUE = "rate_value";
	public static final String TYPE_ID = "type_id";
	
	
	public TaxRateLookupServlet() {
		super(Permission.TAX_READ);
		cols = new String[] { 
				LOCALE_ID,
				LocaleLookupQuery.NAME,
				LocaleLookupQuery.LOCALE_TYPE_ID,
				TYPE_ID,
				LocaleLookupQuery.STATE_NAME,
				EFFECTIVE_DATE,
				RATE_VALUE,
				
				};
		//super.itemTransformer = new ItemTransformer();
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

			LocaleLookupQuery lookupQuery = new LocaleLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}

			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			// parse exception is thrown by SimpleDateFormat
			throw new RuntimeException(e);
		}
	}
	
	
	
}
