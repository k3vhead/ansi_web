package com.ansi.scilla.web.tax.servlet;

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
import com.ansi.scilla.web.tax.query.LocaleTaxRateLookupQuery;

public class LocaleTaxRateLookupServlet extends AbstractLookupServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String REALM = "taxRateLookup";
	
	public static final String LOCALE_ID = "locale.locale_id";
	public static final String STATE_NAME = "locale.state_name";
	public static final String NAME = "locale.name";	
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";	
	public static final String EFFECTIVE_DATE = "locale_tax_rate.effective_date";	
	public static final String RATE_VALUE = "locale_tax_rate.rate_value";	
	public static final String TYPE_ID = "locale_tax_rate.type_id";
	public static final String TYPE_NAME = "type_name";
	
	
	public LocaleTaxRateLookupServlet() {
		super(Permission.TAX_READ);
		cols = new String[] { 
				LocaleTaxRateLookupQuery.LOCALE_ID,
				LocaleTaxRateLookupQuery.NAME,
				LocaleTaxRateLookupQuery.LOCALE_TYPE_ID,
				LocaleTaxRateLookupQuery.TYPE_NAME,
				LocaleTaxRateLookupQuery.STATE_NAME,
				LocaleTaxRateLookupQuery.EFFECTIVE_DATE,
				LocaleTaxRateLookupQuery.RATE_VALUE,
				
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

			//TaxRateLookupQuery lookupQuery = new TaxRateLookupQuery(user.getUserId(), divisionList);
			LocaleTaxRateLookupQuery lookupQuery = new LocaleTaxRateLookupQuery();
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

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			Date effectiveDate = (Date)arg0.get(EFFECTIVE_DATE);
			if ( effectiveDate != null ) {
				arg0.put(EFFECTIVE_DATE, dateFormatter.format(effectiveDate));
			}
			
//			String jobFrequency = (String)arg0.get(JOB_FREQUENCY);
//			if ( ! StringUtils.isBlank(jobFrequency) ) {
//				JobFrequency freq = JobFrequency.lookup(jobFrequency);
//				arg0.put(FREQUENCY_DESC, freq.display());
//			}
			return arg0;
		}
		
	}
	
	
}
