package com.ansi.scilla.web.bcr.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.bcr.query.BcrTicketLookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class BcrTicketSql extends ApplicationObject {
	private static final long serialVersionUID = 1L;

	public static final String sqlSelectClause = 
			"select \n" + 
			"   job_site.name as job_site_name\n" + 
			" , ticket.ticket_id\n" + 
			" , concat(ticket_claim.claim_year,'-',ticket_claim.claim_week) as claim_week\n" + 
			" , isnull(ticket_claim.dl_amt,0.00) as dl_amt\n" + 
			"-- ** ignoring dl_exp until we know what we are doing with it **\n" + 
			"-- , isnull(ticket_claim.dl_exp,0.00) as dl_exp\n" + 
			"-- , isnull(ticket_claim.dl_amt,0.00)+ISNULL(ticket_claim.dl_exp,0.00) as dl_total\n" + 
			" , isnull(ticket_claim.dl_amt,0.00) as dl_total\n" + 
			" , job.price_per_cleaning as total_volume\n" + 
			" , isnull(ticket_claim.volume,0.00) as volume_claimed	\n" + 
			"-- ** this part needs a passthru discussion **\n" + 
			" , ISNULL(ticket_claim.passthru_expense_volume,0.00) as passthru_volume	\n" + 
			" , ticket_claim.passthru_expense_type\n" + 
			" , isnull(ticket_claim.volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00) as claimed_volume_total\n" + 
			" , job.price_per_cleaning - isnull(ticket_claim_totals.claimed_total_volume,0.00)	as volume_remaining	\n" + 
			"-- , isnull(invoice_totals.invoiced_amount,0.00) as billed_amount	, (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))		- isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	, ISNULL(ticket_payment_totals.paid_amount,0.00) as paid_amt	, ISNULL(invoice_totals.invoiced_amount,0.00)-ISNULL(ticket_payment_totals.paid_amount,0.00) as amount_due\n" + 
			"-- , job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00))	as volume_remaining\n" + 
			" , job_tag.abbrev as service_tag_id\n" + 
			" , ticket_claim.notes\n" + 
			"-- ** used a join to make sure only invoiced tickets get a billed amount **\n" + 
			" , isnull(invoice_totals.invoiced_amount,0.00) as billed_amount\n" + 
			"-- ** repeat of passthru question **\n" + 
			"-- , (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	\n" + 
			" , (isnull(ticket_claim_totals.claimed_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	\n" + 
			" , ticket.ticket_status\n" + 
			" , ticket_claim.employee_name as employee\n" + 
			"-- ** this is where the equipment tags would go **\n" + 
			"-- , \"concat equipment tags separated by commas\"\n" + 
			" , 'GSS' as equipment_tags\n";
			
	
	public static final String sqlFromClause = 
			"from ticket\n" + 
			"join job on job.job_id = ticket.job_id\n" + 
			"left outer join ticket_claim on ticket_claim.ticket_id = ticket.ticket_id\n" + 
			"join quote on quote.quote_id = job.quote_id\n" + 
			"join division on division.division_id = ticket.act_division_id and division.division_id in ($DIVISION_USER_FILTER$)\n" + 
			"join address job_site on job_site.address_id = quote.job_site_address_id\n" + 
			"join job_tag on job_tag.tag_id=ticket_claim.service_type \n" + 
			"left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, sum(isnull(volume,0.00)) as claimed_volume\n" + 
			"		, sum(isnull(volume,0.00)+isnull(passthru_expense_volume,0.00)) as claimed_total_volume\n" + 
			"		, sum(isnull(dl_amt,0.00)) as claimed_dl_amt\n" + 
			"		, sum(isnull(dl_expenses,0.00)) as claimed_dl_exp\n" + 
			"		, sum(isnull(passthru_expense_volume,0.00)) as claimed_pt_exp\n" + 
			"--		, sum(hours) as claimed_hours\n" + 
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
					
	
	public static final String baseWhereClause =
			"where ticket.ticket_type in ('run','job') \n" + 			
			"  and ticket.act_division_id=? \n" + 
			" and ((\n" +
			"     ticket_claim.claim_year=?\n" + 
			"     and ticket_claim.claim_week in ($CLAIMWEEKFILTER$)\n" +
			"   ) or (\n" +
			"     ticket.ticket_status in ('D','C')\n" +
			" ))"; 
	
	
	public static String makeFilteredFromClause(List<SessionDivision> divisionList) {
		List<Integer> divisionIdList = new ArrayList<Integer>();
		divisionIdList = CollectionUtils.collect(divisionList.iterator(), new SessionDivisionTransformer(), divisionIdList);
		String divisionFilter = StringUtils.join(divisionIdList, ",");
		String whereClause = BcrTicketSql.sqlFromClause.replaceAll("\\$DIVISION_USER_FILTER\\$", divisionFilter);
		return whereClause;
	}
	
	public static String makeBaseWhereClause(String workWeeks) {
		String whereClause = BcrTicketSql.baseWhereClause.replaceAll("\\$CLAIMWEEKFILTER\\$", workWeeks);
		return whereClause;
	}
}
