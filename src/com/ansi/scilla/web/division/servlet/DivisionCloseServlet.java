package com.ansi.scilla.web.division.servlet;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ansi.scilla.web.division.query.DivisionCloseQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class DivisionCloseServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "divisionClose";
	
	
	public static final String ACT_CLOSE_DATE = "act_close_date";
	public static final String NEXT_CLOSE_DATE = "next_close_date";
	public static final String LAST_CLOSE_DATE = "last_close_date";
	
	public static final String ACT_CLOSE_DATE_DISPLAY = "act_close_date_display";
	public static final String NEXT_CLOSE_DATE_DISPLAY = "next_close_date_display";
	public static final String LAST_CLOSE_DATE_DISPLAY = "last_close_date_display";
	public static final String CAN_CLOSE = "can_close";

	public DivisionCloseServlet() {
		super(Permission.DIVISION_CLOSE_READ);
		cols = new String[] { 
				DivisionCloseQuery.DIVISION_ID,
				DivisionCloseQuery.DIV,
				DivisionCloseQuery.DIVISION_DESCRIPTION,
				DivisionCloseQuery.ACT_CLOSE_DATE,
				DivisionCloseQuery.LAST_CLOSE_DATE,
				DivisionCloseQuery.NEXT_CLOSE_DATE,
				CAN_CLOSE,
				};
		super.setAmount(50);
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
//			Integer divisionIdFilterValue = null;			
			DivisionCloseQuery lookupQuery = new DivisionCloseQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			divisionIdFilterValue = url.getId();
//			if ( divisionIdFilterValue == null ) {
//				throw new ResourceNotFoundException();
//			}
//			lookupQuery.setDivisionId(divisionIdFilterValue);
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);
		}
	}


	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {
		private SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy"); 

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			
			Timestamp actCloseDate = (Timestamp)arg0.get(ACT_CLOSE_DATE);
			if ( actCloseDate != null ) {
				String completedDateDisplay = sdf.format(actCloseDate);
				arg0.put(ACT_CLOSE_DATE_DISPLAY, completedDateDisplay);
			}
			
			java.sql.Date nextCloseDate = (java.sql.Date)arg0.get(NEXT_CLOSE_DATE);
			if ( nextCloseDate != null ) {
				String nextCloseDateDisplay = sdf.format(nextCloseDate);
				arg0.put(NEXT_CLOSE_DATE_DISPLAY, nextCloseDateDisplay);
			}
			
			java.sql.Date lastCloseDate = (java.sql.Date)arg0.get(LAST_CLOSE_DATE);
			if ( lastCloseDate != null ) {
				String lastDateDisplay = sdf.format(lastCloseDate);
				arg0.put(LAST_CLOSE_DATE_DISPLAY, lastDateDisplay);
			}
			
			if ( actCloseDate == null ) {
				arg0.put(CAN_CLOSE, true);
			} else {
				Date now = new Date();
				if ( now.after(nextCloseDate)) {
					arg0.put(CAN_CLOSE, true);
				} else {
					arg0.put(CAN_CLOSE, false);
				}
			}
			return arg0;
		}
		
	}

		
}
