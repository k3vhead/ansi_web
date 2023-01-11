package com.ansi.scilla.web.division.servlet;

import java.sql.Connection;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.division.query.UserDivisionLookupQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class UserDivisionServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "userDivision";
	
	

	public UserDivisionServlet() {
		super(Permission.TICKET_WRITE);
		cols = new String[] { 
				UserDivisionLookupQuery.USER_ID,
				UserDivisionLookupQuery.LAST_NAME,
				UserDivisionLookupQuery.FIRST_NAME,
				UserDivisionLookupQuery.TITLE,
				UserDivisionLookupQuery.PHONE,
				UserDivisionLookupQuery.EMAIL,
				};
		super.setAmount(100);
	}


	


	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		Enumeration<String> parmList = request.getParameterNames();
		while ( parmList.hasMoreElements() ) {
			String parmName = parmList.nextElement();
			logger.log(Level.DEBUG, parmName + "\t" + request.getParameter(parmName));
		}
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
			Integer divisionIdFilterValue = null;			
			UserDivisionLookupQuery lookupQuery = new UserDivisionLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
			divisionIdFilterValue = url.getId();
			if ( divisionIdFilterValue != null ) {
				lookupQuery.setDivisionId(divisionIdFilterValue);
			}
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);
		}
	}




		
}
