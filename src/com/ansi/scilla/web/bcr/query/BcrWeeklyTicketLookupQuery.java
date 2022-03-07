package com.ansi.scilla.web.bcr.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrWeeklyTicketLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticket.ticket_id";
	public static final String NAME = "job_site_name";
	public static final String TICKET_STATUS = "ticket.ticket_status";
	public static final String PRICE_PER_CLEANING = "job.price_per_cleaning";
	public static final String BUDGET = "job.budget";
	public static final String TICKET_TYPE = "ticket.ticket_type";
	
	
	
			
	protected Integer divisionId;
	protected String workWeeks;
	protected Integer workYear;
	protected String workWeek;

	public BcrWeeklyTicketLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeeks, String workWeek) {
		super(makeWeeklySelectClause(), makeWeeklyFromClause(userId, divisionList, divisionId, workYear, workWeeks, (String)null), makeWeeklyWhereClause(workYear, workWeek));
		this.logger = LogManager.getLogger(BcrWeeklyTicketLookupQuery.class);
		this.userId = userId;	
		this.workWeeks = workWeeks;
		this.workYear = workYear;
		this.divisionId = divisionId;
		this.workWeek = workWeek;
		super.setBaseFilterValue(Arrays.asList( new Object[] {divisionId, workYear}));
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public String getWorkWeeks() {
		return workWeeks;
	}

	public void setWorkWeeks(String workWeeks) {
		this.workWeeks = workWeeks;
	}

	public Integer getWorkYear() {
		return workYear;
	}

	public void setWorkYear(Integer workYear) {
		this.workYear = workYear;
	}
	

	private static String makeWeeklySelectClause() {
		return "select *\n";
	}

	private static String makeWeeklyFromClause(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeeks, String queryTerm) {
		BcrTicketLookupQuery lookup = new BcrTicketLookupQuery(userId, divisionList, divisionId, workYear, workWeeks, false);
		String sql = lookup.getSqlSelectClause() + lookup.getSqlFromClause() + lookup.makeWhereClause(queryTerm);
		Logger logger = LogManager.getLogger(BcrWeeklyTicketLookupQuery.class);
		logger.log(Level.DEBUG, sql);
		return "from (\n" + sql + "\n) as basequery";
	}


	private static String makeWeeklyWhereClause(Integer workYear, String workWeek) {
		String weekNum = Integer.valueOf(workWeek) < 10 ? "0" + workWeek : workWeek;
		return "where basequery.claim_week='" + workYear + "-" + weekNum + "'";
	}
	
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		Logger logger = LogManager.getLogger(this.getClass());
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by basequery.job_site_name, basequery.ticket_id, basequery.claim_week";
			} else {
//				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
//				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}
		return "\n" + orderBy;
	}


	

	

	@Override
	protected String makeWhereClause(String queryTerm) {
		String whereClause = makeWeeklyWhereClause(workYear, workWeek);
		String joiner = " and ";

		// these are different than in BcrTicketLookupQuery because we're using a subquery here
		String[] searchableFields = new String[] {
				"basequery.job_site_name",
				"basequery.claim_week",
				"basequery.passthru_expense_type",
				"basequery.service_tag_id",
				"basequery.notes",
				"basequery.ticket_status",
				"basequery.employee",
				// equipment tags
		};


		if ( ! StringUtils.isBlank(queryTerm) ) {
			StringBuffer searchFieldBuffer = new StringBuffer();
			for ( String field : searchableFields ) {
				searchFieldBuffer.append("\n OR lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}

			whereClause =  whereClause + joiner + " (\n" +
					" basequery.ticket_id like '%" + queryTerm.toLowerCase() +  "%'" +
					searchFieldBuffer.toString() + "\n" +
					")" ;
		}
		
		return whereClause;
	}


	

	
	
	
	

}
