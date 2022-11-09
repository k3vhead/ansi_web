package com.ansi.scilla.web.job.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.queries.TicketDRVQuery;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class JobNotesLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String OM_NOTES = "job.om_notes";
	public static final String BILLING_NOTES = "job.billing_notes";
	public static final String WASHER_NOTES = "job.washer_notes";

	private Integer divisionId;
	private Integer startMonth;
	private Integer startYear;
	
	private final String[] searchableFields = new String[] {
			TicketDRVQuery.QUOTE_ID,
			TicketDRVQuery.JOB_ID,
			TicketDRVQuery.TICKET_ID,
			TicketDRVQuery.STATUS,
			TicketDRVQuery.NAME,
			TicketDRVQuery.ADDRESS1,
			TicketDRVQuery.CITY,
			TicketDRVQuery.LAST_DONE,
			TicketDRVQuery.START_DATE,
			TicketDRVQuery.JOB_NBR,
			TicketDRVQuery.JOB_FREQUENCY,
			TicketDRVQuery.BUDGET,
			TicketDRVQuery.PRICE_PER_CLEANING,
			TicketDRVQuery.INVOICE_STYLE	
	};

	public JobNotesLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer startMonth, Integer startYear, Boolean notesOnly, Boolean includeBilling) throws Exception {
		super(makeSelectClause(includeBilling), TicketDRVQuery.SQL_FROM_CLAUSE, makeBaseWhereClause(notesOnly, includeBilling));		
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
		this.startMonth = startMonth;
		this.startYear = startYear;
		super.setBaseFilterValue(Arrays.asList( new Object[] {startMonth, startYear, divisionId}));
	}
	
	public JobNotesLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer startMonth, Integer startYear, Boolean notesOnly, Boolean includeBilling, String searchTerm) throws Exception {
		this(userId, divisionList, divisionId, startMonth, startYear, notesOnly, includeBilling);
		this.searchTerm = searchTerm;
	}
	
	public Integer getStartMonth() {
		return startMonth;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public void setStartMonth(Integer startMonth) {
		this.startMonth = startMonth;
	}
	public Integer getStartYear() {
		return startYear;
	}
	public void setStartYear(Integer startYear) {
		this.startYear = startYear;
	}

	
	private static String makeSelectClause(Boolean includeBilling) {
		String selectClause = TicketDRVQuery.SQL_SELECT_CLAUSE + ", job.om_notes, job.washer_notes";
		if ( includeBilling ) {
			selectClause = selectClause + ", job.billing_notes"; 
		}
		return selectClause + "\n";
	}


	private static String makeBaseWhereClause(Boolean notesOnly, Boolean includeBilling) {
		String whereClause = "";
		if ( notesOnly ) {
			whereClause = " job.om_notes is not null or job.washer_notes is not null ";
			if ( includeBilling ) {
				whereClause = whereClause + " or job.billing_notes is not null ";
			}
		}
		return StringUtils.isBlank(whereClause) ? TicketDRVQuery.SQL_WHERE_CLAUSE : TicketDRVQuery.SQL_WHERE_CLAUSE + " and (" + whereClause + ")\n";
	}

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {				
				orderBy = " order by " + TicketDRVQuery.NAME + " desc";
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
		String whereClause = super.getBaseWhereClause();
		if ( ! StringUtils.isBlank(queryTerm) ) {
			List<String> likeList = new ArrayList<String>();
			for ( String field : this.searchableFields ) {
				likeList.add("lower(" + field + ") like %" + queryTerm.toLowerCase() + "%)");
			}
			whereClause = whereClause + "\n and (\n" + StringUtils.join(likeList, "\n or ") + ")";
		}
		
		return whereClause;
	}

}
