package com.ansi.scilla.web.locale.servlet;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.locale.query.LocaleDivisionLookupQuery;

public class LocaleDivisionLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "localeDivisionLookup";
	
	public static final String DIVISION_ID = "locale_division.division_id";
	public static final String LOCALE_ID = "locale_division.locale_id";
	public static final String EFF_START_DATE = "locale_division.effective_start_date";
	public static final String EFF_STOP_DATE = "locale_division.effective_stop_date"; 
	public static final String ADDRESS_ID = "locale_division.address_id"; 
	public static final String NAME = "locale.name";
	public static final String LOCALE_STATE_NAME = "locale.state_name";
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";
	public static final String DIVISION_CODE = "division.division_code";
	public static final String DIVISION_NBR = "division.division_nbr";
	public static final String DIVISION_DISPLAY = "concat(division_nbr,'-',division_code)";
	public static final String DESCRIPTION = "division.description"; 
	public static final String ADDRESS1 = "address.address1";
	public static final String ADDRESS2 = "address.address2";
	public static final String CITY = "address.city";
	public static final String STATE = "address.state";
	public static final String ZIP = "address.zip";
	
	//private static final String ADDRESS = ""+ADDRESS1+"\n"+ADDRESS2+"/n"+CITY+" "+STATE+", "+ZIP+"";
	
	public LocaleDivisionLookupServlet() {
		super(Permission.TAX_READ);
		cols = new String[] { 
			LocaleDivisionLookupQuery.DIVISION_DISPLAY,
			LocaleDivisionLookupQuery.NAME,
			LocaleDivisionLookupQuery.STATE,
			LocaleDivisionLookupQuery.EFF_START_DATE,
			LocaleDivisionLookupQuery.EFF_STOP_DATE,
			LocaleDivisionLookupQuery.ADDRESS_NAME,
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

			LocaleDivisionLookupQuery lookupQuery = new LocaleDivisionLookupQuery(user.getUserId(), divisionList);
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

		private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy");
//		private DecimalFormat rateFormatter = new DecimalFormat("#0.000%");

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date effectiveStartDate = (Date)arg0.get("effective_start_date");
			if ( effectiveStartDate != null ) {
				arg0.put("effective_start_date", dateFormatter.format(effectiveStartDate));
			}
			
			Date effectiveStopDate = (Date)arg0.get("effective_stop_date");
			if ( effectiveStopDate != null ) {
				arg0.put("effective_stop_date", dateFormatter.format(effectiveStopDate));
			}
			


			return arg0;
		}
	
	
	
	}
}
