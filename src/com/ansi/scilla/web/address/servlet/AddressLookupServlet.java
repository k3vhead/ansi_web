package com.ansi.scilla.web.address.servlet;

import java.sql.Connection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.address.query.AddressLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.exceptions.InvalidParameterException;

public class AddressLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	
	
	
	public AddressLookupServlet() {
		super(Permission.ADDRESS_READ);
		cols = new String[] { 
				"a.address_id",
				"a.name",
				"a.address_status",
				"a.address1",
				"a.address2",
				"a.city",
				"a.county",
				"a.state",
				"a.zip", 
		};
//		super.itemTransformer = new ItemTransformer();
	}
	
	
	@Override
	public LookupQuery makeQuery(Connection conn, HttpServletRequest request) throws InvalidParameterException {
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		
		SessionUser user = sessionData.getUser();
		List<SessionDivision> divisionList = sessionData.getDivisionList();
		try {
//			super.itemTransformer = new ItemTransformer();

			String searchTerm = null;
			if(request.getParameter("search[value]") != null){
				searchTerm = request.getParameter("search[value]");
			}
			LookupQuery lookupQuery = new AddressLookupQuery(user.getUserId(), divisionList);
			if ( searchTerm != null ) {
				lookupQuery.setSearchTerm(searchTerm);
			}

			return lookupQuery;
		} catch (Exception e) { 
			e.printStackTrace();
			throw new RuntimeException(e);		
		}
	}

	/*
	public class ItemTransformer implements Transformer<HashMap<String, Object>,HashMap<String, Object>> {

		private final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
		
		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			for ( String key : new String[] {TicketDRVQuery.START_DATE, TicketDRVQuery.LAST_DONE} ) {
				Timestamp date = (Timestamp)arg0.get(key);
				if ( date != null ) {
					arg0.put(key, dateFormat.format(date));
				}
			}
			

			return arg0;
		}

	}
	*/
}
