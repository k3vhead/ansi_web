package com.ansi.scilla.web.batch.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.batch.query.BatchLogLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class BatchLogLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "batchLogLookup";
	
	public static final String BATCH_LOG_ID = "batch_log_id";
	public static final String BATCH_TYPE = "batch_type";
	public static final String BATCH_TYPE_DETAIL = "batch_type_detail";
	public static final String END_TIME = "end_time";
	public static final String PARAMETERS = "parameters";
	public static final String START_TIME = "start_time";
	public static final String STATUS = "status";
	public static final String START_TIME_DISPLAY = "start_time_display";
	public static final String END_TIME_DISPLAY = "end_time_display";

	public BatchLogLookupServlet() {
		super(Permission.BATCH_LOG_READ);
		cols = new String[] {
				BATCH_LOG_ID,
				START_TIME,
				END_TIME,
				BATCH_TYPE,
				BATCH_TYPE_DETAIL,
				STATUS,
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
			
			BatchLogLookupQuery lookupQuery = new BatchLogLookupQuery(user.getUserId(), divisionList);
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

		private final SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss z");

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date ansiDate = (Date)arg0.get(START_TIME);
			if ( ansiDate != null ) {
				arg0.put(START_TIME_DISPLAY, dateFormatter.format(ansiDate));
			}
			Date dateType = (Date)arg0.get(END_TIME);
			if ( dateType != null ) {
				arg0.put(END_TIME_DISPLAY, dateFormatter.format(dateType));
			}
				
			
			return arg0;
		}
		
	}


	
	
}
