package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.quote.query.QuoteLookupQuery;

public class QuoteLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "quoteTable";

	public static final String PROPOSAL_DATE = "proposal_date";
	public static final String PAC_STATUS = "pac_status";
	public static final String PROPOSED_JOB_COUNT = "proposed_job_count";
	public static final String ACTIVE_JOB_COUNT = "active_job_count";
	public static final String CANCELED_JOB_COUNT = "canceled_job_count";
	public static final String NEW_JOB_COUNT = "new_job_count";
	
	
	
	public QuoteLookupServlet() {
		super(Permission.QUOTE_READ);
		cols = new String[] { 
				QuoteLookupQuery.QUOTE_ID,
				QuoteLookupQuery.QUOTE_CODE,
				QuoteLookupQuery.DIV,
				QuoteLookupQuery.BILL_TO_NAME,
				QuoteLookupQuery.JOB_SITE_NAME,
				QuoteLookupQuery.JOB_SITE_ADDRESS,
				QuoteLookupQuery.MANAGER_NAME,
				QuoteLookupQuery.PROPOSAL_DATE,
				QuoteLookupQuery.QUOTE_JOB_COUNT,
				QuoteLookupQuery.QUOTE_PPC_SUM,
				QuoteLookupQuery.PAC_STATUS,
		};
		super.itemTransformer = new ItemTransformer();
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
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			LookupQuery lookupQuery = new QuoteLookupQuery(user.getUserId(), divisionList);
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
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			java.sql.Timestamp proposalDate = (java.sql.Timestamp)arg0.get(PROPOSAL_DATE);			
			String formattedDate = proposalDate == null ? null : sdf.format(proposalDate);
			arg0.put(PROPOSAL_DATE, formattedDate);

			/*
			Integer proposedJobCount = (Integer)arg0.get(PROPOSED_JOB_COUNT);
			Integer activeJobCount = (Integer)arg0.get(ACTIVE_JOB_COUNT);
			Integer canceledJobCount = (Integer)arg0.get(CANCELED_JOB_COUNT);
			
			String pacStatus = null;
			if (activeJobCount > 0 ) { pacStatus = "A"; }
			else if ( canceledJobCount > 0 ) { pacStatus = "C"; }
			else if ( proposedJobCount > 0 ) { pacStatus = "P"; }
			else { pacStatus = "N"; }
			
			arg0.put(PAC_STATUS, pacStatus);
			*/
			
			return arg0;
		}

	}

}
