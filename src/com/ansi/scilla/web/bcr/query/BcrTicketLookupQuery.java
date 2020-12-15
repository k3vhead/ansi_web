package com.ansi.scilla.web.bcr.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrTicketLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticket.ticket_id";
	public static final String NAME = "job_site.name";
	public static final String TICKET_STATUS = "ticket.ticket_status";
	public static final String PRICE_PER_CLEANING = "job.price_per_cleaning";
	public static final String BUDGET = "job.budget";
	public static final String TICKET_TYPE = "ticket.ticket_type";
	
	
	
			
	protected Integer divisionId;
	protected String workWeek;
	protected Integer workYear;


	public BcrTicketLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeek) {
		super(BcrTicketSql.sqlSelectClause, BcrTicketSql.makeFilteredFromClause(divisionList), BcrTicketSql.makeBaseWhereClause(workWeek));
		this.logger = LogManager.getLogger(BcrTicketLookupQuery.class);
		this.userId = userId;	
		this.workWeek = workWeek;
		this.workYear = workYear;
		this.divisionId = divisionId;
		super.setBaseFilterValue(Arrays.asList( new Object[] {divisionId, workYear}));
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}


	
	public String getWorkWeek() {
		return workWeek;
	}

	public void setWorkWeek(String workWeek) {
		this.workWeek = workWeek;
	}

	public Integer getWorkYear() {
		return workYear;
	}

	public void setWorkYear(Integer workYear) {
		this.workYear = workYear;
	}
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by job_site.name, ticket_id, claim_week";
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
		String whereClause = BcrTicketSql.makeBaseWhereClause(workWeek);
		String joiner = " and ";
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			whereClause =  whereClause + joiner + " (\n"
					+ " " + TICKET_ID + " like '%" + queryTerm.toLowerCase() +  "%'" +
					"\n OR lower( " + NAME + " ) ) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		return whereClause;
	}


	

	
	
	
	

}
