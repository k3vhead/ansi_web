package com.ansi.scilla.web.claims.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;

public class ClaimDetailLookupQuery extends LookupQuery {

	public static final String JOB_ID = "job_id";
	public static final String ACT_DIVISION_ID = "act_division_id";

	public static final String DIVISION = "div";
	public static final String JOB_SITE_NAME  = "job_site_name"; 	//== "Account"
	public static final String TICKET_ID = "ticket_id";
	public static final String TICKET_STATUS = "ticket_status";
	public static final String CLAIM_WEEK = "claim_week";
	public static final String WORK_DATE = "work_date";
	public static final String WASHER_NAME = "washer_name";
	public static final String BUDGET = "budget";
	public static final String TICKET_CLAIM_DL_AMT = "ticket_claim_dl_amt";
	public static final String TICKET_CLAIM_DL_EXP = "ticket_claim_dl_exp";
	public static final String TICKET_CLAIM_DL_TOTAL = "ticket_claim_dl_total";
	public static final String TICKET_CLAIM_DL_HOURS = "ticket_claim_dl_hours";
	public static final String TICKET_CLAIM_DL_VOLUME = "ticket_claim_dl_volume";
	public static final String CLAIMED_DL_AMT = "claimed_dl_amt";
	public static final String CLAIMED_DL_EXP = "claimed_dl_exp";
	public static final String CLAIMED_DL_TOTAL = "claimed_dl_total";
	public static final String TOTAL_VOLUME = "total_volume";		//== job.price_per_cleaning
	public static final String CLAIMED_VOLUME = "claimed_volume";
	public static final String PASSTHRU_VOLUME = "passthru_volume";
	public static final String CLAIMED_VOLUME_TOTAL = "claimed_volume_total";
	public static final String VOLUME_REMAINING = "volume_remaining";
	public static final String DL_REMAINING = "dl_remaining";

	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
			"select \r\n" + 
			"CONCAT(division_nbr,'-',division_code) as div\r\n" +
			", ticket.act_division_id\r\n" + 
			", ticket.job_id\r\n" + 
			", job_site.name as job_site_name\r\n" + 
			", concat(year(work_date),'-',right('00'+CAST(isnull(datepart(wk,work_date),0) as VARCHAR),2)) as claim_week\r\n" + 
			", ticket_claim.work_date\r\n" + 
			", ticket.ticket_id\r\n" + 
			", ticket.ticket_status\r\n" + 
			", concat(ansi_user.last_name,', ', ansi_user.first_name) as washer_name\r\n" + 
			", job.price_per_cleaning as total_volume\r\n" + 
			", job.budget\r\n" + 
			", isnull(ticket_claim.volume,0.00) as ticket_claim_dl_volume\r\n" + 
			", isnull(ticket_claim.dl_amt,0.00) as ticket_claim_dl_amt\r\n" + 
			", 0.00 as ticket_claim_dl_exp\r\n" + 
			", isnull(ticket_claim.dl_amt,0.00) + 0.00 as ticket_claim_dl_total\r\n" + 
			", isnull(ticket_claim.hours,0.00) as ticket_claim_dl_hours\r\n" + 
			", isnull(ticket_claim_totals.claimed_volume,0.00) as claimed_volume\r\n" + 
			", isnull(ticket_claim_passthru_totals.passthru_volume,0.00) as passthru_volume\r\n" + 
			", isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)\r\n" + 
			"	as claimed_volume\r\n" + 
			", isnull(ticket_claim_totals.claimed_dl_amt,0.00) as claimed_dl_amt\r\n" + 
			", isnull(ticket_claim_totals.claimed_dl_exp,0.00) as claimed_dl_exp\r\n" + 
			", isnull(ticket_claim_totals.claimed_dl_amt,0.00)+ISNULL(ticket_claim_totals.claimed_dl_exp,0.00)\r\n" + 
			"	as claimed_dl_total\r\n" + 
			", job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))\r\n" + 
			"	as volume_remaining\r\n" + 
			", job.budget - (isnull(ticket_claim_totals.claimed_dl_amt,0.00)+ISNULL(ticket_claim_totals.claimed_dl_exp,0.00)) as dl_remaining\r\n" ;
		

	private static final String sqlFromClause = "\n  "
			+ "from ticket\r\n" + 
			"join job on job.job_id = ticket.job_id\r\n" + 
			"join quote on quote.quote_id = job.quote_id\r\n" + 
			"join division on division.division_id = ticket.act_division_id\r\n" + 
			"join address job_site on job_site.address_id = job_site_address_id\r\n" + 
			"left outer join ticket_claim on ticket_claim.ticket_id = ticket.ticket_id\r\n" + 
			"left outer join ansi_user on ansi_user.user_id = ticket_claim.washer_id \r\n" + 
			"left outer join (\r\n" + 
			"	select ticket_id, \r\n" + 
			"		sum(amount) as paid_amount, \r\n" + 
			"		sum(tax_amt) as paid_tax_amt \r\n" + 
			"		from ticket_payment group by ticket_id) as ticket_payment_totals \r\n" + 
			"	on ticket_payment_totals.ticket_id = ticket.ticket_id \r\n" + 
			"left outer join (\r\n" + 
			"	select ticket_id\r\n" + 
			"		, sum(volume) as claimed_volume\r\n" + 
			"		, sum(dl_amt) as claimed_dl_amt\r\n" + 
			"		, '0.00' as claimed_dl_exp\r\n" + 
			"		, sum(hours) as claimed_hours\r\n" + 
			"	from ticket_claim \r\n" + 
			"	group by ticket_id\r\n" + 
			"	) as ticket_claim_totals on ticket_claim_totals.ticket_id = ticket.ticket_id\r\n" + 
			"left outer join (\r\n" + 
			"	select ticket_id\r\n" + 
			"		, sum(passthru_expense_volume) as passthru_volume\r\n" + 
			"	from ticket_claim_passthru\r\n" + 
			"	group by ticket_id\r\n" + 
			"	) as ticket_claim_passthru_totals on ticket_claim_passthru_totals.ticket_id = ticket.ticket_id\r\n";

	private static final String baseWhereClause = "\n  ";
	
	
	
	
	public ClaimDetailLookupQuery(Integer userId) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public ClaimDetailLookupQuery(Integer userId, String searchTerm) {
		this(userId);
		this.searchTerm = searchTerm;
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
		String whereClause = ClaimDetailLookupQuery.baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		if (! StringUtils.isBlank(queryTerm)) {
				whereClause =  whereClause + joiner + " (\n"
						+ " lower(concat(job_site.name, ' ', job_site.address1, ' ', job_site.city)) like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR ticket.ticket_id like '%" + queryTerm.toLowerCase() + "%'" +
						")" ;
		}
		return whereClause;
	}
	
	
	
	
}
