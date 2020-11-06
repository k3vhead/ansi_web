package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.web.bcr.query.BcrLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.Permission;

public class BcrTicketLookupServlet extends AbstractLookupServlet {
	
	private static final long serialVersionUID = 1L;

	public static final String CLAIM_WEEK = "claim_week";
	public static final String DIRECT_LABOR = "direct_labor";
	public static final String EXPENSES = "expenses";
	public static final String TOTAL_DIRECT_LABOR = "total_direct_labor";
	public static final String TOTAL_VOLUME = "total_volume";
	public static final String TOTAL_CLAIMED = "total_claimed";
	public static final String VOLUME_REMAINING = "volume_remaining";
	public static final String NOTES = "notes";
	public static final String DIFF_CLM_BLD = "diff_clm_bld";
	public static final String EMPLOYEE = "employee";
	
	
	public BcrTicketLookupServlet() {
		super(Permission.TICKET_READ);
		cols = new String[] { 
				BcrLookupQuery.NAME,
				BcrLookupQuery.TICKET_ID,
				CLAIM_WEEK,
				BcrLookupQuery.TICKET_TYPE,
				DIRECT_LABOR,
				EXPENSES,
				TOTAL_DIRECT_LABOR,
				TOTAL_VOLUME,
				TOTAL_CLAIMED,
				VOLUME_REMAINING,
				NOTES,
				BcrLookupQuery.PRICE_PER_CLEANING,
				DIFF_CLM_BLD,
				BcrLookupQuery.TICKET_STATUS,
				EMPLOYEE
				};
//		super.itemTransformer = new ItemTransformer();
	}

	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}
	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
//			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			Integer divisionId = Integer.valueOf(request.getParameter("divisionId"));
			String dateString = request.getParameter("startDate");
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
			Calendar startDate = DateUtils.toCalendar(sdf.parse(dateString));
			BcrLookupQuery lookupQuery = new BcrLookupQuery(user.getUserId(), divisionList, divisionId, startDate);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (ParseException e) {
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
