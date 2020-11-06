package com.ansi.scilla.web.bcr.query;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BcrLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticket.ticket_id";
	public static final String NAME = "address.name";
	public static final String TICKET_STATUS = "ticket.ticket_status";
	public static final String PRICE_PER_CLEANING = "job.price_per_cleaning";
	public static final String BUDGET = "job.budget";
	public static final String TICKET_TYPE = "ticekt.ticket_type";
	
	
	private static final String sqlSelectClause = 
			"SELECT         \n" + 
			"--	  division_nbr\n" + 
			"--	, 'fm:' + ticket.fleetmatics_id\n" + 
			"	  ticket.ticket_id \n" + 
			"	, address.name \n" + 
			"--	, address.address1 \n" + 
			"--	, address.city \n" + 
			"--	, '' as item_name_spacer\n" + 
			"--	, job.job_nbr \n" + 
			"--	, ticket.start_date \n" + 
			"	, ticket.ticket_status \n" + 
			"--	, '' as item_part_no_spacer\n" + 
			"--	, job.job_frequency \n" + 
			"--	, job.price_per_cleaning as item_price_ex_tax\n" + 
			"--	, '' as item_cost_price_spacer\n" + 
			"	, job.price_per_cleaning \n" + 
			"--	, job.invoice_style \n" + 
			"--	, (	\n" + 
			"--		select top 1 process_date \n" + 
			"--		from ticket \n" + 
			"--		where ticket.job_id = job.job_id \n" + 
			"--		and ticket.process_date is not null \n" + 
			"--		order by ticket.process_date desc\n" + 
			"--	  ) as last_run     \n" + 
			"--	, cast(division_nbr as varchar) + '-' + division_code as division\n" + 
			"--	, ticket.ticket_type\n" + 
			"--	, '11/02/2020' as PriorToDate \n" + 
			"--	, GetDate() as ReportCreatedDate \n" + 
			"--	, '03/28/2020' as FirstWorkDateInMonth\n" + 
			"--	, 5 as WeeksInMonth\n" + 
			"--	, '05/01/2020' as LastWorkDateInMonth\n" + 
			"--	, 1 as ValidWeek01\n" + 
			"--	, 2 as ValidWeek02\n" + 
			"--	, 3 as ValidWeek03\n" + 
			"--	, 4 as ValidWeek04\n" + 
			"--	, 5 as ValidWeek05\n" + 
			"	, job.budget \n";
	
	private static final String sqlFromClause = 
			"FROM ticket \n" + 
			"INNER JOIN job ON job.job_id = ticket.job_id \n" + 
			"INNER JOIN quote ON quote.quote_id = job.quote_id \n" + 
			"INNER JOIN address ON address.address_id = quote.job_site_address_id \n" + 
			"INNER JOIN division ON division.division_id = ticket.act_division_id \n";
	
	private static final String baseWhereClause =
			"where \n" + 
			"	-- ticket.start_date <= EOMONTH(getdate())\n" + 
			"       ticket.start_date < ?\n" + 
			"   -- and division.division_nbr >= 22 AND DIVISION.division_nbr <= 89\n" + 
			"      and division.division_id=? \n" +
			"   and ticket_type = 'job' \n" + 
			"and ticket_status in ('"+ TicketStatus.DISPATCHED.code()+"','"+TicketStatus.NOT_DISPATCHED.code()+"')\n" + 
			"--order by division_nbr, ticket.start_date asc, address.name \n"; 
			
	
	

	public BcrLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Calendar startDate) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		super.setBaseFilterValue(Arrays.asList( new Object[] {startDate, divisionId}));
	}

	public BcrLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer divisionId, Calendar startDate, String searchTerm) {
		this(userId, divisionList, divisionId, startDate);
		this.searchTerm = searchTerm;
	}


	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + NAME + " desc ";
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
		String whereClause = baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			whereClause =  whereClause + joiner + " (\n"
					+ " " + TICKET_ID + " like '%" + queryTerm.toLowerCase() +  "%'" +
					"\n OR lower( " + NAME + " ) ) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		return whereClause;
	}
	
	
	

}
