package com.ansi.scilla.web.batch.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.batch.query.BatchLogQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;

public class BatchLogServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;

	public static final String ID = "batch_log_id";
	public static final String TYPE = "batch_type";
	public static final String TYPE_DETAIL = "batch_type_detail";
	public static final String PARAMETERS = "parameters";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	public static final String STATUS = "status";
	
	
	public BatchLogServlet() {
		super(Permission.BATCH_LOG_READ);
		cols = new String[] { 
				ID,
				TYPE,
				TYPE_DETAIL,
				PARAMETERS,
				START_TIME,
				END_TIME,
				STATUS,
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
//		HttpSession session = request.getSession();
//		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);

//		SessionUser user = sessionData.getUser();
//		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			BatchLogQuery lookupQuery = new BatchLogQuery();
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.S");
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			for ( String label : new String[] {START_TIME, END_TIME} ) {
				java.sql.Timestamp startTime = (java.sql.Timestamp)arg0.get(label);
				if ( startTime != null ) {
					String documentDateDisplay = sdf.format(startTime);
					arg0.put(label, documentDateDisplay);
				}
			}
			
			
			return arg0;
		}
		
	}
}
