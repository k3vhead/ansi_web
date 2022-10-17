package com.ansi.scilla.web.locale.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.locale.query.LocaleLookupQuery;

public class LocaleLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "localeLookup";
	
	public static final String LOCALE_ID = "locale_id";
	public static final String NAME = "name";
	public static final String STATE_NAME = "state_name";
	public static final String ABBREVIATION = "abbreviation";
	public static final String LOCALE_TYPE_ID = "locale_type_id";
	
	
	public LocaleLookupServlet() {
		super(Permission.TAX_READ);
		cols = new String[] { 
				LocaleLookupQuery.LOCALE_ID,
				LocaleLookupQuery.NAME,
				LocaleLookupQuery.STATE_NAME,
				LocaleLookupQuery.LOCALE_ABBREVIATION,
				LocaleLookupQuery.LOCALE_TYPE_ID,
				LocaleLookupQuery.PARENT_NAME,
				LocaleLookupQuery.PAYROLL_TAX_PROFILE,				
			};
		super.itemTransformer = new ItemTransformer();
	}


	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}


	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		String searchTerm = null;
		if(request.getParameter("search[value]") != null){
			searchTerm = request.getParameter("search[value]");
		}
		LocaleLookupQuery lookupQuery = new LocaleLookupQuery(user.getUserId(), divisionList);
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
			return lookupQuery;
			
	}
	
	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String,Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String parent = null;
			if ( arg0.containsKey("parent_name")) {
				String parentName = (String)arg0.get("parent_name");
				if ( ! StringUtils.isEmpty(parentName)) {
					parent = parentName + ", " + arg0.get("parent_state") + " (" + arg0.get("parent_type_id") + ")"; 
				}
			}
			arg0.put("parent", parent);
			return arg0;
		}
		
	}
}
