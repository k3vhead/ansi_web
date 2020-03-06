package com.ansi.scilla.web.calendar.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.calendar.CalendarDateType;
import com.ansi.scilla.web.calendar.query.CalendarLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class CalendarLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "calendarLookup";
	
	public static final String ANSI_DATE = "ansi_date";
	public static final String DATE_TYPE = "date_type";
	public static final String ANSI_DATE_DISPLAY = "ansi_date_display";
	public static final String DATE_TYPE_DISPLAY = "date_type_display";

	public CalendarLookupServlet() {
		super(Permission.CALENDAR_READ);
		cols = new String[] { 
				CalendarLookupQuery.ANSI_DATE,
				CalendarLookupQuery.DATE_TYPE,				
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
			
			CalendarLookupQuery lookupQuery = new CalendarLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			// parse exception is thrown by SimpleDateFormat
			throw new RuntimeException(e);
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date ansiDate = (Date)arg0.get(ANSI_DATE);
			if ( ansiDate != null ) {
				arg0.put(ANSI_DATE_DISPLAY, dateFormatter.format(ansiDate));
			}
			String dateType = (String)arg0.get(DATE_TYPE);
			if ( ! StringUtils.isBlank(dateType)) {
				try {
					CalendarDateType type = CalendarDateType.valueOf(dateType);
					arg0.put(DATE_TYPE_DISPLAY, type.description());
				} catch ( IllegalArgumentException e ) {
					arg0.put(DATE_TYPE_DISPLAY, dateType +  " (Invalid value)");
				}
			}
				
			
			return arg0;
		}
		
	}


	
	
}
