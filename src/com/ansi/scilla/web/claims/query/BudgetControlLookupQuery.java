package com.ansi.scilla.web.claims.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jobticket.TicketType;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BudgetControlLookupQuery extends ClaimsQuery {

	public static final String JOB_ID = "job_id";
	public static final String ACT_DIVISION_ID = "act_division_id";

	public static final String DIVISION = "div";
	public static final String JOB_SITE_NAME  = "job_site_name"; 	//== "Account"
	public static final String TICKET_ID = "ticket_id";
	public static final String CLAIM_WEEK = "claim_week";
	public static final String CLAIMED_WEEKLY_DL_AMT = "claimed_weekly_dl_amt";
	public static final String CLAIMED_WEEKLY_DL_EXP = "claimed_weekly_dl_exp";
	public static final String CLAIMED_WEEKLY_DL_TOTAL = "claimed_weekly_dl_total";
	public static final String CLAIMED_WEEKLY_VOLUME = "claimed_weekly_dl_volume";
	public static final String CLAIMED_WEEKLY_RECORD_COUNT = "claimed_weekly_record_count";
	public static final String CLAIMED_DL_AMT = "claimed_dl_amt";
	public static final String CLAIMED_DL_EXP = "claimed_dl_exp";
	public static final String CLAIMED_DL_TOTAL = "claimed_dl_total";
	public static final String TOTAL_VOLUME = "total_volume";		//== job.price_per_cleaning
	public static final String CLAIMED_VOLUME = "claimed_volume";
	public static final String PASSTHRU_VOLUME = "passthru_volume";
	public static final String CLAIMED_VOLUME_TOTAL = "claimed_volume_total";
	public static final String REMAINING_VOLUME = "volume_remaining";
	public static final String BILLED_AMOUNT = "billed_amount";
	public static final String CLAIMED_VS_BILLED = "claimed_vs_billed";
	public static final String PAID_AMOUNT = "paid_amt";
	public static final String AMOUNT_DUE = "amount_due";
	public static final String TICKET_STATUS = "ticket_status";

//	public static final String INVOICE_ID = "invoice_id";
//	public static final String INVOICE_DATE = "invoice_date";
//	public static final String ACT_DL_AMT = "act_dl_amt";
//	public static final String ACT_PRICE_PER_CLEANING = "act_price_per_cleaning";
//	public static final String PAID_TAX_AMT = "paid_tax_amt";
//	public static final String CLAIMED_HOURS = "claimed_hours";
//	public static final String JOB_SITE_ADDRESS = "job_site_address";
//	public static final String JOB_SITE_CITY = "job_site_city";
	
	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
			"select "
			+ "CONCAT(division_nbr,'-',division_code) as div"
			+ " , ticket.act_division_id"
			+ " , ticket.ticket_id"
			+ " , ticket.ticket_status"
			+ " , ticket.job_id"
			+ "	, job_site.name as job_site_name"
			+ " , ticket_claim_weekly_totals.claim_week"
			+ " , isnull(ticket_claim_weekly_totals.claimed_weekly_dl_amt,0.00) as claimed_weekly_dl_amt\r\n" 
			+ " , isnull(ticket_claim_weekly_totals.claimed_weekly_dl_exp,0.00) as claimed_weekly_dl_exp\r\n"
			+ " , isnull(ticket_claim_weekly_totals.claimed_weekly_dl_amt,0.00)+ISNULL(ticket_claim_weekly_totals.claimed_weekly_dl_exp,0.00)\r\n" 
			+ "	    as claimed_weekly_dl_total"
			+ " , isnull(ticket_claim_weekly_totals.claimed_weekly_hours,0.00) as claimed_weekly_hours\r\n"
			+ " , isnull(ticket_claim_weekly_totals.claimed_weekly_volume,0.00) as claimed_weekly_volume\r\n"
			+ " , isnull(ticket_claim_weekly_totals.claimed_weekly_record_count,0) as claimed_weekly_record_count\r\n"
			+ "	, isnull(ticket_claim_totals.claimed_dl_amt,0.00) as claimed_dl_amt"
			+ "	, isnull(ticket_claim_totals.claimed_dl_exp,0.00) as claimed_dl_exp"
			+ "	, isnull(ticket_claim_totals.claimed_dl_amt,0.00)+ISNULL(ticket_claim_totals.claimed_dl_exp,0.00)"
			+ "		as claimed_dl_total"
			+ "	, job.price_per_cleaning as total_volume"
			+ "	, isnull(ticket_claim_totals.claimed_volume,0.00) as claimed_volume"
			+ "	, ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00) as passthru_volume"
			+ "	, isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)"
			+ "     as claimed_volume_total"
			+ "	, job.price_per_cleaning - "
			+ "(isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))"
			+ "		as volume_remaining"
			+ "	, isnull(invoice_totals.invoiced_amount,0.00) as billed_amount"
			+ "	, (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))"
			+ "		- isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed"
			+ "	, ISNULL(ticket_payment_totals.paid_amount,0.00) as paid_amt"
			+ "	, ISNULL(invoice_totals.invoiced_amount,0.00)-ISNULL(ticket_payment_totals.paid_amount,0.00) as amount_due";
		

	private static final String sqlFromClause = "\n  "
			+ "from ticket\n" + 			
			"join job on job.job_id = ticket.job_id\n" + 
			"join quote on quote.quote_id = job.quote_id\n" + 
			"join division on division.division_id = ticket.act_division_id and division.division_id in ($DIVISION_USER_FILTER$)\n" + 
			"join address job_site on job_site.address_id = quote.job_site_address_id\n" + 
			"left outer join (\n" + 
			"	select ticket_id, \n" + 
			"		sum(amount) as paid_amount, \n" + 
			"		sum(tax_amt) as paid_tax_amt \n" + 
			"		from ticket_payment group by ticket_id) as ticket_payment_totals \n" + 
			"	on ticket_payment_totals.ticket_id = ticket.ticket_id \n" +
			"left outer join (\r\n" + 
			"	select ticket_id\r\n" + 
			"		, concat(year(work_date),'-',right('00'+CAST(isnull(datepart(wk,work_date),0) as VARCHAR),2)) as claim_week\r\n" + 
			"		, sum(volume) as claimed_weekly_volume\r\n" + 
			"		, sum(dl_amt) as claimed_weekly_dl_amt\r\n" + 
			"		, sum(hours) as claimed_weekly_hours\r\n" + 
			"		, '0.00' as claimed_weekly_dl_exp\r\n" + 
			"		, COUNT(1) as claimed_weekly_record_count\r\n" + 
			"	from ticket_claim \r\n" + 
			"	group by ticket_id, concat(year(work_date),'-',right('00'+CAST(isnull(datepart(wk,work_date),0) as VARCHAR),2))\r\n" + 
			"	) as ticket_claim_weekly_totals on ticket_claim_weekly_totals.ticket_id = ticket.ticket_id\r\n" + 
			"left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, sum(volume) as claimed_volume\n" + 
			"		, sum(dl_amt) as claimed_dl_amt\n" + 
			"		, '0.00' as claimed_dl_exp\n" + 		//need to determine if we will be using claimed dl exp
			"		, sum(hours) as claimed_hours\n" + 
			"	from ticket_claim \n" + 
			"	group by ticket_id\n" + 
			"	) as ticket_claim_totals on ticket_claim_totals.ticket_id = ticket.ticket_id\n" + 
			"left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, sum(passthru_expense_volume) as passthru_volume\n" + 
			"	from ticket_claim_passthru\n" + 
			"	group by ticket_id\n" + 
			"	) as ticket_claim_passthru_totals on ticket_claim_passthru_totals.ticket_id = ticket.ticket_id\n" + 
			"left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, sum(act_price_per_cleaning) as invoiced_amount\n" + 
			"	from ticket\n" + 
			"	where invoice_date is not null\n" + 
			"	group by ticket_id\n" + 
			"	) as invoice_totals on invoice_totals.ticket_id = ticket.ticket_id";

	private static final String baseWhereClause = "where "
			+ "ticket.ticket_type in ('" + TicketType.RUN.code() + "','" + TicketType.JOB.code() + "') "
			+ "and ticket.ticket_status in ('" + TicketStatus.DISPATCHED.code() + "','" + TicketStatus.COMPLETED + "') \n  ";
	
	private Integer ticketFilter;
	
	
	public BudgetControlLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, makeFromClause(sqlFromClause, divisionList), baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		this.divisionList = divisionList;
	}

	public BudgetControlLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}

	

	

	public Integer getTicketFilter() {
		return ticketFilter;
	}

	public void setTicketFilter(Integer ticketFilter) {
		this.ticketFilter = ticketFilter;
	}

	
	
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + JOB_SITE_NAME + " asc, " + 
							"ticket."+TICKET_ID + " asc ";
			} else {
//				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
//				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}
	
	
	
	
	/**
	 * Return a where clause with the passed in queryTerm embedded
	 * 
	 * @param queryTerm
	 * @return 
	 * @throws Exception
	 */
	protected String makeWhereClause(String queryTerm)  {
		String whereClause = BudgetControlLookupQuery.baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		
		
		if ( StringUtils.isBlank(queryTerm) ) {
			if ( this.ticketFilter != null ) {
				whereClause = whereClause + joiner + " ticket.ticket_id=" + this.ticketFilter;
			}
		} else {
			String ticketClause = this.ticketFilter == null ? "" : "ticket.ticket_id=" + this.ticketFilter + " and ";
			whereClause =  whereClause + joiner + " (\n"
					+ ticketClause
					+ " lower(concat(job_site.name, ' ', job_site.address1, ' ', job_site.city)) like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR ticket.ticket_id like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		
		return whereClause;
	}

	
	
	
	
}
