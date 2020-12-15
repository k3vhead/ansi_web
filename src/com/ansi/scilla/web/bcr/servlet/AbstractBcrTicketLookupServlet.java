package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.bcr.query.BcrTicketLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.utils.Permission;

public abstract class AbstractBcrTicketLookupServlet extends AbstractLookupServlet {
	
	private static final long serialVersionUID = 1L;

	public static final String CLAIM_WEEK = "claim_week";
	public static final String DIRECT_LABOR = "direct_labor";
	public static final String EXPENSES = "expenses";
	public static final String TOTAL_DIRECT_LABOR = "total_direct_labor";
	public static final String TOTAL_VOLUME = "total_volume";
	public static final String TOTAL_CLAIMED = "total_claimed";
	public static final String VOLUME_REMAINING = "volume_remaining";
	public static final String DIFF_CLM_BLD = "diff_clm_bld";
	public static final String EMPLOYEE = "employee";
	
	public static final String NOTES = "notes";
	public static final String NOTES_DISPLAY = "notes_display";
	
	
	public AbstractBcrTicketLookupServlet() {
		super(Permission.TICKET_READ);
		cols = new String[] { 
				BcrTicketLookupQuery.NAME,
				BcrTicketLookupQuery.TICKET_ID,
				CLAIM_WEEK,
				BcrTicketLookupQuery.TICKET_TYPE,
				DIRECT_LABOR,
				EXPENSES,
				TOTAL_DIRECT_LABOR,
				TOTAL_VOLUME,
				TOTAL_CLAIMED,
				VOLUME_REMAINING,
				NOTES,
				BcrTicketLookupQuery.PRICE_PER_CLEANING,
				DIFF_CLM_BLD,
				BcrTicketLookupQuery.TICKET_STATUS,
				EMPLOYEE
				};
		super.itemTransformer = new ItemTransformer();
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, request.getRequestURI());
		super.doGet(request, response);
	}


	@Override
	public abstract LookupQuery makeQuery(Connection conn, HttpServletRequest request);

	
	
	


	
	
	
	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String notes = (String)arg0.get(NOTES);
			if ( StringUtils.isBlank(notes) ) {
				arg0.put(NOTES_DISPLAY, null);
			} else {
				arg0.put(NOTES_DISPLAY, StringUtils.abbreviate(notes, 10));
			}
			return arg0;
		}
		
	}

}
