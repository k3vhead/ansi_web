package com.ansi.scilla.web.job.servlet;

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
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.job.query.JobLookupQuery;

public class JobLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "jobTable";
	
	public static final String PROPOSAL_DATE = "proposal_date";
	public static final String ACTIVATION_DATE = "activation_date";
	public static final String CANCEL_DATE = "cancel_date";
	public static final String START_DATE = "start_date";
	
	public static final String DATE_FORMAT = "MM/dd/yyyy";
	
	
	
	public JobLookupServlet() {
		super(Permission.QUOTE_READ);
		cols = new String[] { 
				JobLookupQuery.JOB_ID,
				JobLookupQuery.QUOTE_NBR,
				JobLookupQuery.JOB_STATUS,
				JobLookupQuery.DIV,
				JobLookupQuery.BILL_TO_NAME,
				JobLookupQuery.SITE_NAME,
				JobLookupQuery.JOB_SITE,
				JobLookupQuery.START_DATE,
				JobLookupQuery.JOB_FREQUENCY,
				JobLookupQuery.PRICE_PER_CLEANING,
				JobLookupQuery.JOB_NBR,
				JobLookupQuery.SERVICE_DESCRIPTION,
				JobLookupQuery.PO_NUMBER,
				JobLookupQuery.JOB_CONTACT,
				JobLookupQuery.SITE_CONTACT,
				JobLookupQuery.CONTRACT_CONTACT,
				JobLookupQuery.BILLING_CONTACT,
				JobLookupQuery.PROPOSAL_DATE,
				JobLookupQuery.ACTIVATION_DATE,
				JobLookupQuery.CANCEL_DATE,
				JobLookupQuery.CANCEL_REASON
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
//			super.itemTransformer = new ItemTransformer();

			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			JobLookupQuery lookupQuery = new JobLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}

			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);		
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>,HashMap<String, Object>> {

		private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			for ( String key : new String[] {PROPOSAL_DATE, ACTIVATION_DATE, START_DATE, CANCEL_DATE} ) {
				Timestamp date = (Timestamp)arg0.get(key);
				if ( date != null ) {
					arg0.put(key, dateFormat.format(date));
				}
			}
			

			return arg0;
		}

	}


	
	
}
