package com.ansi.scilla.web.code.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.web.code.query.CodeLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class CodeLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "codeLookup";
	
	
	
	public CodeLookupServlet() {
		super(Permission.SYSADMIN_READ);
		cols = new String[] { 
				CodeLookupQuery.TABLE_NAME,
				CodeLookupQuery.FIELD_NAME,
				CodeLookupQuery.VALUE,
				CodeLookupQuery.DISPLAY_VALUE,
				CodeLookupQuery.SEQ,
				CodeLookupQuery.DESCRIPTION,
				CodeLookupQuery.CODE_STATUS,
				};
//		super.itemTransformer = new ItemTransformer();
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
			CodeLookupQuery lookupQuery = new CodeLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}

//			}
			
			return lookupQuery;
			
		} catch (ResourceNotFoundException e) { 
			throw new RuntimeException(e);
		}
	}







	
	
}
