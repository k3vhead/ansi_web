package com.ansi.scilla.web.division.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.division.query.DivisionLookupQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class DivisionLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "divisionLookup";
	
	public static final String DIVISION_ID = "division.division_id";
	public static final String DIV = "concat(division_nbr,'-',division_code)";
	public static final String DESCRIPTION = DivisionLookupQuery.DESCRIPTION;
	public static final String DEFAULT_DIRECT_LABOR_PCT = DivisionLookupQuery.DEFAULT_DIRECT_LABOR_PCT;
	public static final String MAX_REG_HRS_PER_DAY = DivisionLookupQuery.MAX_REG_HRS_PER_DAY;
	public static final String MAX_REG_HRS_PER_WEEK = DivisionLookupQuery.MAX_REG_HRS_PER_WEEK;
	public static final String MINIMUM_HOURLY_PAY = DivisionLookupQuery.MINIMUM_HOURLY_PAY;
	public static final String OVERTIME_RATE = DivisionLookupQuery.OVERTIME_RATE;
	public static final String WEEKEND_IS_OT = DivisionLookupQuery.WEEKEND_IS_OT;
	public static final String HOURLY_RATE_IS_FIXED = DivisionLookupQuery.HOURLY_RATE_IS_FIXED;
	public static final String DIVISION_STATUS = DivisionLookupQuery.DIVISION_STATUS;
	public static final String USER_COUNT = DivisionLookupQuery.USER_COUNT;

	public DivisionLookupServlet() {
		super((Permission)null);
		cols = new String[] { 
				DIVISION_ID,
				DIV,
				DESCRIPTION,
				DEFAULT_DIRECT_LABOR_PCT,
				MAX_REG_HRS_PER_DAY,
				MAX_REG_HRS_PER_WEEK,
				MINIMUM_HOURLY_PAY,
				OVERTIME_RATE,
				WEEKEND_IS_OT,
				HOURLY_RATE_IS_FIXED,
				DIVISION_STATUS,
//				USER_COUNT,
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
			DivisionLookupQuery lookupQuery = new DivisionLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			if ( url.getId() != null ) {
//				lookupQuery.setTicketFilter(url.getId());
//			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}




//	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
//
//		private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//		
//		@Override
//		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
//			java.sql.Date documentDate = (java.sql.Date)arg0.get(DOCUMENT_DATE);
//			if ( documentDate != null ) {
//				String documentDateDisplay = sdf.format(documentDate);
//				arg0.put(DOCUMENT_DATE, documentDateDisplay);
//			}
//			
//			java.sql.Date expirationDate = (java.sql.Date)arg0.get(EXPIRATION_DATE);
//			if ( expirationDate != null ) {
//				String expirationDateDisplay = sdf.format(expirationDate);
//				arg0.put(EXPIRATION_DATE, expirationDateDisplay);
//			}
//			
//			DocumentType documentType = DocumentType.valueOf((String)arg0.get(XREF_TYPE));
//			arg0.put(XREF_TYPE_DISPLAY, documentType.description());
//			return arg0;
//		}
//		
//	}


	
	
}
