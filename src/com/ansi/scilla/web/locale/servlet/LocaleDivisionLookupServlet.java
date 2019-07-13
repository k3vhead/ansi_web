package com.ansi.scilla.web.locale.servlet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.text.DecimalFormat;
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
import com.ansi.scilla.web.locale.query.LocaleLookupQuery;

public class LocaleDivisionLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "localeDivisionLookup";
	
	public static final String DIVISION_ID = "division.division_id";
	public static final String LOCALE_ID = "locale.locale_id";
	public static final String EFFECTIVE_START_DATE = "locale_division.effective_start_date";
	public static final String EFFECTIVE_STOP_DATE = "locale_division.effective_stop_date";
	public static final String ADDRESS_ID = "locale_division.address_id";
	
	
	public LocaleDivisionLookupServlet() {
		super(Permission.TAX_READ);
		cols = new String[] { 
				
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

			LocaleLookupQuery lookupQuery = new LocaleLookupQuery(user.getUserId(), divisionList);
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
		private DecimalFormat rateFormatter = new DecimalFormat("#0.000%");

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date effectiveStartDate = (Date)arg0.get(EFFECTIVE_START_DATE);
			if ( effectiveStartDate != null ) {
				arg0.put(EFFECTIVE_START_DATE, dateFormatter.format(effectiveStartDate));
			}
			Date effectiveStopDate = (Date)arg0.get(EFFECTIVE_STOP_DATE);
			if ( effectiveStopDate != null ) {
				arg0.put(EFFECTIVE_STOP_DATE, dateFormatter.format(effectiveStopDate));
			}
			
//			BigDecimal rateValue = (BigDecimal)arg0.get("rate_value");
//			if ( rateValue != null ) {
//				arg0.put(DISPLAY_RATE, rateFormatter.format(rateValue.doubleValue()));
//			}
//			String jobFrequency = (String)arg0.get(JOB_FREQUENCY);
//			if ( ! StringUtils.isBlank(jobFrequency) ) {
//				JobFrequency freq = JobFrequency.lookup(jobFrequency);
//				arg0.put(FREQUENCY_DESC, freq.display());
//			}
			return arg0;
		}
	
	
	
	}
}
