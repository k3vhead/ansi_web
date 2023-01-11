package com.ansi.scilla.web.ticket.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.ticket.query.TicketAssignmentLookupQuery;

public class TicketAssignmentLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "ticketAssignmentLookup";
	
	public static final String START_DATE = "view_start_date";
	public static final String FREQUENCY_DESC = "frequency_desc";
	public static final String JOB_FREQUENCY = "job_frequency";
	public static final String WASHER = "washer";
	
	public TicketAssignmentLookupServlet() {
		super(Permission.TICKET_READ);
		cols = new String[] { 
				TicketAssignmentLookupQuery.TICKET_ID,
				TicketAssignmentLookupQuery.DIV,
				TicketAssignmentLookupQuery.JOB_SITE_NAME,
				TicketAssignmentLookupQuery.JOB_SITE_ADDRESS,
				TicketAssignmentLookupQuery.START_DATE,
				TicketAssignmentLookupQuery.JOB_FREQUENCY,
				TicketAssignmentLookupQuery.PRICE_PER_CLEANING,
				TicketAssignmentLookupQuery.JOB_ID,
				TicketAssignmentLookupQuery.WASHER,
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
//			Integer jobIdFilterValue = null;
//			Integer ticketIdFilterValue = null;
//			Integer divisionIdFilterValue = url.getId();
//			Integer washerIdFilterValue = null;
			TicketAssignmentLookupQuery lookupQuery = new TicketAssignmentLookupQuery(user.getUserId(), divisionList);
//			lookupQuery.setDivisionId(divisionIdFilterValue);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			if (! StringUtils.isBlank(request.getParameter("jobId"))) {
//				jobIdFilterValue = Integer.valueOf(request.getParameter("jobId"));
//				lookupQuery.setJobId(jobIdFilterValue);
//			}
//			if (! StringUtils.isBlank(request.getParameter("ticketId"))) {
//				ticketIdFilterValue = Integer.valueOf(request.getParameter("ticketId"));
//				lookupQuery.setTicketId(ticketIdFilterValue);
//			}
//			if (! StringUtils.isBlank(request.getParameter("washerId"))) {
//				washerIdFilterValue = Integer.valueOf(request.getParameter("washerId"));
//				lookupQuery.setWasherId(washerIdFilterValue);
//			}
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			// parse exception is thrown by SimpleDateFormat
			throw new RuntimeException(e);
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date startDate = (Date)arg0.get(START_DATE);
			if ( startDate != null ) {
				arg0.put(START_DATE, dateFormatter.format(startDate));
			}
			
			String jobFrequency = (String)arg0.get(JOB_FREQUENCY);
			if ( ! StringUtils.isBlank(jobFrequency) ) {
				JobFrequency freq = JobFrequency.lookup(jobFrequency);
				arg0.put(FREQUENCY_DESC, freq.display());
			}
			return arg0;
		}
		
	}


	
	
}
