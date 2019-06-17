package com.ansi.scilla.web.locale.servlet;

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

public class LocaleLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "locale";
	
	public static final String LOCALE_ID = "locale_id";
	public static final String NAME = "name";
	public static final String STATE_NAME = "state_name";
	public static final String ABBREVIATION = "abbreviation";
	public static final String LOCALE_TYPE_ID = "locale_type_id";
	
	
	public LocaleLookupServlet() {
		super(Permission.TAX_READ);
		cols = new String[] { 
				LocaleLookupQuery.LOCALE_ID,
				LocaleLookupQuery.NAME,
				LocaleLookupQuery.STATE_NAME,
				LocaleLookupQuery.LOCALE_ABBREVIATION,
				LocaleLookupQuery.LOCALE_TYPE_ID
				
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
//			Integer localeIdFilterValue = null;
//			Integer divisionIdFilterValue = null;
//			Calendar startDateFilterValue = null;
//			String ticketStatusFilterValue = null;
			LocaleLookupQuery lookupQuery = new LocaleLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			if (! StringUtils.isBlank(request.getParameter("localeId"))) {
//				localeIdFilterValue = Integer.valueOf(request.getParameter("localeId"));
//				lookupQuery.setBaseWhereClause(LOCALE_ID);
//			}
//			if (! StringUtils.isBlank(request.getParameter("divisionId"))) {
//				divisionIdFilterValue = Integer.valueOf(request.getParameter("divisionId"));
//				lookupQuery.setDivisionId(divisionIdFilterValue);
//			}
//			if (! StringUtils.isBlank(request.getParameter("startDate"))) {
//				SimpleDateFormat parmDateFormatter = new SimpleDateFormat("MM/dd/yyyy");
//				Date parmDate = parmDateFormatter.parse(request.getParameter("startDate"));
//				startDateFilterValue = Calendar.getInstance(new AnsiTime());
//				startDateFilterValue.setTime(parmDate);
//				lookupQuery.setStartDate(startDateFilterValue);
//			}
//			if ( ! StringUtils.isBlank(request.getParameter("status"))) {
//				ticketStatusFilterValue = request.getParameter("status");
//				lookupQuery.setStatus(ticketStatusFilterValue);
//			}
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			// parse exception is thrown by SimpleDateFormat
			throw new RuntimeException(e);
		}
	}
	
	
	
}
