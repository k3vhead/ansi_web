package com.ansi.scilla.web.bcr.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;

public abstract class AbstractBcrLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String TICKET_ID = "ticket.ticket_id";
	public static final String NAME = "job_site.name";
	public static final String TICKET_STATUS = "ticket.ticket_status";
	public static final String PRICE_PER_CLEANING = "job.price_per_cleaning";
	public static final String BUDGET = "job.budget";
	public static final String TICKET_TYPE = "ticket.ticket_type";
	
	
	protected static final String sqlSelectClause = 
			"select \n" + 
			"   job_site.name as job_site_name\n" + 
			" , ticket.ticket_id\n" + 
			" , concat(year(work_date),'-',right('00'+CAST(isnull(datepart(wk,work_date),0) as VARCHAR),2)) as claim_week\n" + 
			"-- ** thinking we really need a claim week in ticket_claim - calculating it in the mean time **\n" + 
			"-- , ticket_claim.week\n" + 
			" , isnull(ticket_claim.dl_amt,0.00) as dl_amt\n" + 
			"-- ** ignoring dl_exp until we know what we are doing with it **\n" + 
			"-- , isnull(ticket_claim.dl_exp,0.00) as dl_exp\n" + 
			"-- , isnull(ticket_claim.dl_amt,0.00)+ISNULL(ticket_claim.dl_exp,0.00) as dl_total\n" + 
			" , isnull(ticket_claim.dl_amt,0.00) as dl_total\n" + 
			" , job.price_per_cleaning as total_volume\n" + 
			" , isnull(ticket_claim.volume,0.00) as volume_claimed	\n" + 
			"-- ** this is where service tag goes when we add it **\n" + 
			"-- , ticket_claim.service_tag_id\n" + 
			" , 'WW' as service_tag_id\n" + 
			"-- ** this part needs a passthru discussion **\n" + 
			"-- , ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00) as passthru_volume	\n" + 
			"-- , isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00) as claimed_volume_total\n" + 
			"-- , job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))	as volume_remaining	, isnull(invoice_totals.invoiced_amount,0.00) as billed_amount	, (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))		- isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	, ISNULL(ticket_payment_totals.paid_amount,0.00) as paid_amt	, ISNULL(invoice_totals.invoiced_amount,0.00)-ISNULL(ticket_payment_totals.paid_amount,0.00) as amount_due\n" + 
			" , job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00))	as volume_remaining\n" + 
			" , ticket_claim.notes\n" + 
			"-- ** used a join to make sure only invoiced tickets get a billed amount **\n" + 
			" , isnull(invoice_totals.invoiced_amount,0.00) as billed_amount\n" + 
			"-- ** repeat of passthru question **\n" + 
			"-- , (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	\n" + 
			" , (isnull(ticket_claim_totals.claimed_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	\n" + 
			" , ticket.ticket_status\n" + 
			"-- ** using washer_id to get user name until employee field is available and populated\n" + 
			"-- , ticket_claim.employee\n" + 
			" , concat(ansi_user.last_name,', ',ansi_user.first_name) as employee\n" + 
			"-- ** this is where the equipment tags would go **\n" + 
			"-- , \"concat equipment tags separated by commas\"\n" + 
			" , 'GSS' as equipment_tags\n";
			
	
	protected static final String sqlFromClause = 
			"  from ticket\n" + 
			"join job on job.job_id = ticket.job_id\n" + 
			"left outer join ticket_claim on ticket_claim.ticket_id = ticket.ticket_id\n" + 
			"left outer join ansi_user on ansi_user.user_id = ticket_claim.washer_id\n" + 
			"join quote on quote.quote_id = job.quote_id\n" + 
			"join division on division.division_id = ticket.act_division_id and division.division_id in (100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,118,117)\n" + 
			"join address job_site on job_site.address_id = quote.job_site_address_id\n" + 
			"left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, sum(volume) as claimed_volume\n" + 
			"		, sum(dl_amt) as claimed_dl_amt\n" + 
			"		, '0.00' as claimed_dl_exp\n" + 
			"		, sum(hours) as claimed_hours\n" + 
			"	from ticket_claim \n" + 
			"	group by ticket_id\n" + 
			"	) as ticket_claim_totals on ticket_claim_totals.ticket_id = ticket.ticket_id\n" + 
			"-- ** part of the passthru discussion **\n" + 
			"--left outer join (\n" + 
			"--	select ticket_id\n" + 
			"--		, sum(passthru_expense_volume) as passthru_volume\n" + 
			"--	from ticket_claim_passthru\n" + 
			"--	group by ticket_id\n" + 
			"--	) as ticket_claim_passthru_totals on ticket_claim_passthru_totals.ticket_id = ticket.ticket_id\n" + 
			"left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, sum(act_price_per_cleaning) as invoiced_amount\n" + 
			"	from ticket\n" + 
			"	where invoice_date is not null\n" + 
			"	group by ticket_id\n" + 
			"	) as invoice_totals on invoice_totals.ticket_id = ticket.ticket_id \n";
					
	
	protected static final String baseWhereClause =
			" where ticket.ticket_type in ('run','job') \n" + 
			" -- and ticket.ticket_status in ('D','COMPLETED') \n" + 
			"  and ticket_claim.work_date is not null    \n" +
			"  and ticket.act_division_id=? \n"; 
			

	


	protected AbstractBcrLookupQuery(String sqlSelectClause, String sqlFromClause, String baseWhereClause) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		Logger myLogger = LogManager.getLogger(BcrWeeklyLookupQuery.class);
		myLogger.log(Level.DEBUG, baseWhereClause);
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
	protected abstract String makeWhereClause(String queryTerm);


	

	
	
	
	

}
