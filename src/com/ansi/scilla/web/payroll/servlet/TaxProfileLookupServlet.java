package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.payroll.query.TaxProfileLookupQuery;

public class TaxProfileLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "taxProfileLookup";
	
	public static final String PROFILE_DESC = "profile_desc";
	public static final String PROFILE_ID = "profile_id";
	public static final String REGULAR_HOURS = "regular_hours";
	public static final String REGULAR_PAY = "regular_pay";
	public static final String OT_HOURS = "ot_hours";
	public static final String OT_PAY = "ot_pay";
	
	public TaxProfileLookupServlet() {
		super(Permission.PAYROLL_READ);
		cols = new String[] { 
			PROFILE_ID,
			PROFILE_DESC,
			REGULAR_HOURS,
			REGULAR_PAY,
			OT_HOURS,
			OT_PAY,
		};
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
		try {
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);
			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			TaxProfileLookupQuery lookupQuery = new TaxProfileLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			return lookupQuery;
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

}
