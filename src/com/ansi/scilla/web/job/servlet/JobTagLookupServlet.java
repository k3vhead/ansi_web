package com.ansi.scilla.web.job.servlet;

import java.sql.Connection;
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
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.job.query.JobTagLookupQuery;

public class JobTagLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "jobTagLookup";
	
	public static final String CAN_DELETE = "can_delete";
	
	public JobTagLookupServlet() {
		super(Permission.QUOTE_READ);
		cols = new String[] { 
				JobTagLookupQuery.TAG_ID,
				JobTagLookupQuery.makeTypeSql(),
				JobTagLookupQuery.LONG_CODE,
				JobTagLookupQuery.ABBREV,
				JobTagLookupQuery.DESCRIPTION,
				JobTagLookupQuery.makeStatusSql(),
				JobTagLookupQuery.JOB_COUNT,
				CAN_DELETE,
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
			JobTagLookupQuery lookupQuery = new JobTagLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}

			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);		
		}
	}




	public class ItemTransformer implements Transformer<HashMap<String, Object>,HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Integer jobCount = (Integer)arg0.get(JobTagLookupQuery.JOB_COUNT);				
			Boolean canDelete = jobCount.intValue() == 0;				
			arg0.put(CAN_DELETE, canDelete);
			return arg0;
		}

	}


	
	
}
