package com.ansi.scilla.web.document.servlet;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.document.query.DocumentLookupQuery;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public class DocumentViewLookupServlet extends DocumentLookupServlet {

	private static final long serialVersionUID = 1L;

	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		try {
			String url = request.getRequestURI();
			String[] path = new URI(url).getPath().split("/"); 
			String xrefType = path[path.length-2];
			String xrefId = path[path.length-1];		
			
			try {
				DocumentType.valueOf(xrefType);
				Integer.valueOf(xrefId);
			} catch (IllegalArgumentException e) {
				throw new ResourceNotFoundException(e);
			}
			
			SessionUser user = sessionData.getUser();
			List<SessionDivision> divisionList = sessionData.getDivisionList();

			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			DocumentLookupQuery lookupQuery = new DocumentLookupQuery(user.getUserId(), divisionList, xrefType, Integer.valueOf(xrefId));
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}
//			if ( url.getId() != null ) {
//				lookupQuery.setTicketFilter(url.getId());
//			}
			return lookupQuery;
		} catch ( URISyntaxException e ) {
			throw new RuntimeException(e);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}
