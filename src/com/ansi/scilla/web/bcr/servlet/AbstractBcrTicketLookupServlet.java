package com.ansi.scilla.web.bcr.servlet;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.bcr.query.BcrTicketLookupQuery;
import com.ansi.scilla.web.bcr.query.BcrWeeklyTicketLookupQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.servlet.AbstractLookupServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;

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
	public static final String UNCLAIMED_EQUIPMENT = "unclaimed_equipment";
	
	
	


	// request parameter names:
	protected static final String WORK_YEAR = "workYear";
	protected static final String WORK_WEEKS = "workWeeks";
	protected static final String WORK_WEEK = "workWeek";
	protected static final String DIVISION_ID = "divisionId";
	protected static final String MONTHLY_FILTER = "monthlyFilter";

	public AbstractBcrTicketLookupServlet() {
		super(Permission.TICKET_READ);
		makeMyColumns();
		super.itemTransformer = new ItemTransformer();
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
		Integer divisionId = Integer.valueOf(request.getParameter(DIVISION_ID));
		Integer workYear = Integer.valueOf(request.getParameter(WORK_YEAR));
		String workWeeks = request.getParameter(WORK_WEEKS);  // comma-delimited list of work weeks.
		String workWeek = request.getParameter(WORK_WEEK);  // the single week we want to look at

		logger.log(Level.DEBUG, "Parms: " + divisionId + " " + workYear + " " + workWeeks + " " + workWeek);
		LookupQuery lookupQuery = null;
		if (StringUtils.isBlank(workWeek)) {	
			String monthlyParm = request.getParameter(MONTHLY_FILTER);
			Boolean monthlyFilter = ( ! StringUtils.isBlank(monthlyParm) ) && monthlyParm.equalsIgnoreCase("true");
			lookupQuery = new BcrTicketLookupQuery(user.getUserId(), divisionList, divisionId, workYear, workWeeks, monthlyFilter);
		} else {
			lookupQuery = new BcrWeeklyTicketLookupQuery(user.getUserId(), divisionList, divisionId, workYear, workWeeks, workWeek);
		}
		if ( searchTerm != null ) {
			lookupQuery.setSearchTerm(searchTerm);
		}
		return lookupQuery;

	}

	protected abstract void makeMyColumns();

	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			String notes = (String)arg0.get(NOTES);
			if ( StringUtils.isBlank(notes) ) {
				arg0.put(NOTES_DISPLAY, null);
			} else {
				arg0.put(NOTES_DISPLAY, StringUtils.abbreviate(notes, 10));
			}
			
			if ( arg0.get(CLAIM_WEEK).equals("-") ) {
				arg0.put(CLAIM_WEEK, null);
			}
			
			
			String equipmentTags = (String)arg0.get(BcrTicketSql.EQUIPMENT_TAGS);
			String claimedEquipment = (String)arg0.get(BcrTicketSql.CLAIMED_EQUIPMENT);
			List<String> equipmentTagList = StringUtils.isBlank(equipmentTags) ? new ArrayList<String>() : Arrays.asList(equipmentTags.split(","));
			List<String> claimedEquipmentList = StringUtils.isBlank(claimedEquipment) ? new ArrayList<String>() : Arrays.asList(claimedEquipment.split(","));
			Collection<String> unclaimedEquipmentList = CollectionUtils.subtract(equipmentTagList, claimedEquipmentList);
			String unclaimedEquipment = StringUtils.join(unclaimedEquipmentList, ",");
			arg0.put(UNCLAIMED_EQUIPMENT, unclaimedEquipment);
			
			return arg0;
		}

	}
		

}
