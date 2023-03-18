package com.ansi.scilla.web.bcr.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class ClaimLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticket.ticket_id";
	public static final String NAME = "job_site.name";
	public static final String TICKET_STATUS = "ticket.ticket_status";
	public static final String PRICE_PER_CLEANING = "job.price_per_cleaning";
	public static final String BUDGET = "job.budget";
	public static final String TICKET_TYPE = "ticket.ticket_type";
	
	
	
			
	protected static final String baseWhereClause = "where ticket_claim.claim_id is not null";
	
	private Integer ticketId;

	public ClaimLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(BcrTicketSql.sqlSelectClause, BcrTicketSql.makeFilteredFromClause(divisionList), baseWhereClause);
		this.logger = LogManager.getLogger(ClaimLookupQuery.class);
		this.userId = userId;	
	}

	
	public Integer getTicketId() {
		return ticketId;
	}


	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
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
		// we don't need to worry about sql injection here because we've already checked that the ticket id is numeric
		String whereClause = this.ticketId == null ? baseWhereClause : baseWhereClause + " and ticket_claim.ticket_id=" + this.ticketId;
		String joiner = " and ";
		
		String[] searchableFields = new String[] {
			"job_site.name",
			"concat(ticket_claim.claim_year,'-',ticket_claim.claim_week)",
			"ticket_claim.passthru_expense_type",
			"job_tag.abbrev",
			"ticket_claim.notes",
			"ticket.ticket_status",
			"ticket_claim.employee_name",
			// equipment tags
		};
				
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			StringBuffer searchFieldBuffer = new StringBuffer();
			for ( String field : searchableFields ) {
				searchFieldBuffer.append("\n OR lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			
			whereClause =  whereClause + joiner + " (\n" +
					" " + TICKET_ID + " like '%" + queryTerm.toLowerCase() +  "%'" +
					searchFieldBuffer.toString() + "\n" +
					")" ;
		}
		
		return whereClause;
	}


	
	

}
