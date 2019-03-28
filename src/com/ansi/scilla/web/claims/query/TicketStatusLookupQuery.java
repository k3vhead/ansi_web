package com.ansi.scilla.web.claims.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;

public class TicketStatusLookupQuery extends LookupQuery {

	public static final String JOB_ID = "job_id";
	public static final String ACT_DIVISION_ID = "act_division_id";

	public static final String DIVISION = "div";
	public static final String JOB_SITE_NAME  = "job_site_name"; 	//== "Account"
	public static final String TICKET_ID = "ticket_id";
	public static final String CLAIMED_DL_AMT = "claimed_dl_amt";
	public static final String CLAIMED_DL_EXP = "claimed_dl_exp";
	public static final String CLAIMED_DL_TOTAL = "claimed_dl_total";
	public static final String TOTAL_VOLUME = "total_volume";		//== job.price_per_cleaning
	public static final String CLAIMED_VOLUME = "claimed_volume";
	public static final String PASSTHRU_VOLUME = "passthru_volume";
	public static final String CLAIMED_VOLUME_TOTAL = "claimed_volume_total";
	public static final String REMAINING_VOLUME = "remaining_volume";
	public static final String INVOICED_AMOUNT = "invoiced_amount";
	public static final String CLAIMED_VS_BILLED = "claimed_vs_billed";
	public static final String PAID_AMOUNT = "paid_amount";
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
//					"select ticket.ticket_id, ticket.job_id, ticket.act_division_id, ticket.ticket_status, ticket.invoice_id, ticket.invoice_date, ticket.act_dl_amt, ticket.act_price_per_cleaning\n" + 
//					", ticket_payment_totals.paid_amount, ticket_payment_totals.paid_tax_amt\n" + 
//					", invoice_totals.invoiced_amount\n" + 
//					", ticket_claim_totals.claimed_volume, ticket_claim_totals.claimed_dl_amt, ticket_claim_totals.claimed_hours\n" + 
//					", ticket_claim_passthru_totals.passthru_volume\n" + 
//					", job_site.name as job_site_name, job_site.address1 as job_site_address, job_site.city as job_site_city\n" + 
//					", job.price_per_cleaning\n" + 
//					"--, quote.*";
			"select "
			+ "CONCAT(division_code,'-',division_nbr) as div"
			+ " , ticket.act_division_id"
			+ " , ticket.ticket_id"
			+ " , ticket.ticket_status"
			+ " , ticket.job_id"
			+ "	, job_site.name as job_site_name"
			+ "	, ticket.ticket_id"
			+ "	, isnull(ticket_claim_totals.claimed_dl_amt,0.00) as claimed_dl_amt"
			+ "	, isnull(ticket_claim_totals.claimed_dl_exp,0.00) as claimed_dl_exp"
			+ "	, isnull(ticket_claim_totals.claimed_dl_amt,0.00)-ISNULL(ticket_claim_totals.claimed_dl_exp,0.00)"
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
			+ "	, ISNULL(invoice_totals.invoiced_amount,0.00)-ISNULL(ticket_payment_totals.paid_amount,0.00)"
			+ "		as amount_due";
		

	private static final String sqlFromClause = "\n  "
			+ "from ticket\n" + 
			"join job on job.job_id = ticket.job_id\n" + 
			"join quote on quote.quote_id = job.quote_id\n" + 
			"join division on division.division_id = ticket.act_division_id\n" + 
			"join address job_site on job_site.address_id = quote.job_site_address_id\n" + 
			"left outer join (\n" + 
			"	select ticket_id, \n" + 
			"		sum(amount) as paid_amount, \n" + 
			"		sum(tax_amt) as paid_tax_amt \n" + 
			"		from ticket_payment group by ticket_id) as ticket_payment_totals \n" + 
			"	on ticket_payment_totals.ticket_id = ticket.ticket_id \n" + 
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

	private static final String baseWhereClause = "\n  ";
	
	
	
	
	public TicketStatusLookupQuery(Integer userId) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public TicketStatusLookupQuery(Integer userId, String searchTerm) {
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
				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
				String sortBy = StringUtils.join(sortList, sortDir + ", ");
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
		String whereClause = TicketStatusLookupQuery.baseWhereClause;
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
