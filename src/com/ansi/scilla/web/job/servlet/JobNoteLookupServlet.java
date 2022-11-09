package com.ansi.scilla.web.job.servlet;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.queries.TicketDRVQuery;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.InvalidParameterException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.job.query.JobNotesLookupQuery;

public class JobNoteLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "jobNoteLookup";
	public static final String FIELD_DIVISION_ID = "divisionId";
	public static final String FIELD_START_MONTH = "startMonth";
	
	private static final String DATE_FORMAT = "MM/dd/yyyy"; 
	
	
	public JobNoteLookupServlet() {
		super(Permission.QUOTE_READ);
		cols = new String[] { 
			"this won't work",
			"ticket." + TicketDRVQuery.TICKET_ID,
			"ticket." + TicketDRVQuery.STATUS,
			TicketDRVQuery.NAME,
			TicketDRVQuery.ADDRESS1,
			TicketDRVQuery.CITY,
			"ticket.process_date",
			"ticket." + TicketDRVQuery.START_DATE,
			TicketDRVQuery.JOB_NBR,
			TicketDRVQuery.JOB_FREQUENCY,
			TicketDRVQuery.BUDGET,
			TicketDRVQuery.PRICE_PER_CLEANING,
			TicketDRVQuery.INVOICE_STYLE,
			"job." + TicketDRVQuery.JOB_ID			
		};
		super.itemTransformer = new ItemTransformer();
	}
	
	
	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) throws InvalidParameterException {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		Boolean includeBilling = false;
		try {
			AppUtils.checkPermission(Permission.INVOICE_READ, sessionData.getUserPermissionList());
			includeBilling = true;
		} catch (NotAllowedException e) {
			// includeBilling stays false
		}
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
//			super.itemTransformer = new ItemTransformer();

			String divisionParm = request.getParameter(FIELD_DIVISION_ID);
			String monthParm = request.getParameter(FIELD_START_MONTH);
			
			if(StringUtils.isBlank(monthParm)||!StringUtils.isNumeric(monthParm)||Integer.valueOf(monthParm)>12){
				throw new InvalidParameterException();
			}

			try {
				Calendar rightNow = Calendar.getInstance(new AnsiTime());
				Integer thisMonth = rightNow.get(Calendar.MONTH);
				Integer startYear = rightNow.get(Calendar.YEAR);
				
				Integer divisionId = Integer.valueOf(divisionParm);
				Integer startMonth = Integer.valueOf(monthParm) + 1; // convert from java 0-11 to SQL 1-12

				if(startMonth<thisMonth){
					startYear++;
				}

				String searchTerm = null;
				if(request.getParameter("search[value]") != null){
					searchTerm = request.getParameter("search[value]");
				}
				LookupQuery lookupQuery = new JobNotesLookupQuery(user.getUserId(), divisionList, divisionId, startMonth, startYear, includeBilling);
				if ( searchTerm != null ) {
					lookupQuery.setSearchTerm(searchTerm);
				}


				return lookupQuery;
			} catch ( NumberFormatException e ) {
				throw new InvalidParameterException();
			}
			
		} catch (Exception e) { 
			e.printStackTrace();
			throw new RuntimeException(e);		
		}
	}

	public class ItemTransformer implements Transformer<HashMap<String, Object>,HashMap<String, Object>> {

		private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			for ( String key : new String[] {TicketDRVQuery.START_DATE, TicketDRVQuery.LAST_DONE} ) {
				Timestamp date = (Timestamp)arg0.get(key);
				if ( date != null ) {
					arg0.put(key, dateFormat.format(date));
				}
			}
			

			return arg0;
		}

	}
}
