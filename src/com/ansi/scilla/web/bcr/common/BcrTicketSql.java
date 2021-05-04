package com.ansi.scilla.web.bcr.common;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class BcrTicketSql extends ApplicationObject {
	private static final long serialVersionUID = 1L;

	public static final String JOB_SITE_NAME = "job_site_name";
	public static final String TICKET_ID = "ticket_id";
	public static final String JOB_ID = "job_id";
	public static final String CLAIM_YEAR = "claim_year";
	public static final String CLAIM_WEEK = "claim_week";
	public static final String DL_EXPENSES = "dl_expenses";
	public static final String DL_AMT = "dl_amt";
	public static final String DL_TOTAL = "dl_total";
	public static final String TOTAL_VOLUME = "total_volume";
	public static final String VOLUME_CLAIMED = "volume_claimed";
	public static final String PASSTHRU_VOLUME = "passthru_volume";
	public static final String PASSTHRU_EXPENSE_TYPE = "passthru_expense_type";
	public static final String CLAIMED_VOLUME_TOTAL = "claimed_volume_total";
	public static final String VOLUME_REMAINING = "volume_remaining";
	public static final String AMOUNT_DUE = "amount_due";
	public static final String SERVICE_TAG_ID = "service_tag_id";
	public static final String NOTES = "notes";
	public static final String BILLED_AMOUNT = "billed_amount";
	public static final String CLAIMED_VS_BILLED = "claimed_vs_billed";
	public static final String TICKET_STATUS = "ticket_status";
	public static final String EMPLOYEE = "employee";
	public static final String EQUIPMENT_TAGS = "equipment_tags";
	public static final String CLAIM_ID = "claim_id";
	public static final String SERVICE_TYPE_ID = "service_type_id";
	public static final String CLAIMED_EQUIPMENT = "claimed_equipment";
	
	
	
	/**
	 * Using code freely stolen from:
	 * https://stackoverflow.com/questions/194852/how-to-concatenate-text-from-multiple-rows-into-a-single-text-string-in-sql-serv
	 * 
	 * This query creates a 2-column table containing a ticket id and a comma-delimited list of job_tag.abbrev where the
	 * job tag is of type 'EQUIPMENT'
	 * 
	 * eg:  12345, 'WFP,LPT'
	 * 
	 * It is highly inefficient because of 2 identical sub-selects within the subselect, but it has the advantage of actually working.
	 * 
	 */
	private static final String equipmentTagSubselect = "SELECT Main.ticket_id,\n" + 
			"       LEFT(Main.equipment_tags,Len(Main.equipment_tags)-1) As \"equipment_tags\"\n" + 
			"FROM\n" + 
			"    (\n" + 
			"        SELECT DISTINCT ST2.ticket_id, \n" + 
			"            (\n" + 
			"                SELECT ST1.equipment_tags + ',' AS [text()]\n" + 
			"                FROM (\n" + 
			"                	select distinct ticket_claim.ticket_id, job_tag.abbrev as equipment_tags\n" + 
			"					from ticket_claim\n" + 
			"					inner join ticket on ticket.ticket_id=ticket_claim.ticket_id\n" + 
			"					inner join job_tag_xref xref on xref.job_id=ticket.job_id\n" + 
			"					inner join job_tag on job_tag.tag_id=xref.tag_id and job_tag.tag_type='EQUIPMENT') ST1\n" + 
			"                WHERE ST1.ticket_id = ST2.ticket_id\n" + 
			"                ORDER BY ST1.ticket_id\n" + 
			"                FOR XML PATH ('')\n" + 
			"            ) [equipment_tags]\n" + 
			"        FROM (\n" + 
			"	        select distinct ticket_claim.ticket_id, job_tag.abbrev as equipment_tags\n" + 
			"			from ticket_claim\n" + 
			"			inner join ticket on ticket.ticket_id=ticket_claim.ticket_id\n" + 
			"			inner join job_tag_xref xref on xref.job_id=ticket.job_id\n" + 
			"			inner join job_tag on job_tag.tag_id=xref.tag_id and job_tag.tag_type='EQUIPMENT') ST2\n" + 
			"    ) [Main]";
	
	
	
	
	/**
	 * Using code freely stolen from:
	 * https://stackoverflow.com/questions/194852/how-to-concatenate-text-from-multiple-rows-into-a-single-text-string-in-sql-serv
	 * 
	 * This query creates a 2-column table containing a claim id and a comma-delimited list of job_tag.abbrev where the
	 * job tag is of type 'EQUIPMENT'
	 * 
	 * eg: where claim_equipment contains
	 * 		claim_id	equipment_id
	 * 		========	============
	 * 		30			7
	 * 		30			8
	 * 		2234		7
	 * 
	 * this subselect will return
	 * 		claim_id	claimed_equipment
	 * 		========	=================
	 * 		30			WFP,LPT
	 * 		2234		WFP
	 *
	 *	claims that have no equipment will return a null in the claimed_equipment column
	 * 
	 * It is highly inefficient because of 2 identical sub-selects within the subselect, but it has the advantage of actually working.
	 * 
	 */
	private static final String claimedEquipmentSubselect = 
			"SELECT Main.claim_id,\n" + 
			"	case len(main.claimed_equipment)\n" + 
			"	when 0 then null\n" + 
			"	else LEFT(Main.claimed_equipment,Len(Main.claimed_equipment)-1)\n" + 
			"	end as 'claimed_equipment'\n" + 
			"FROM\n" + 
			"    (\n" + 
			"        SELECT DISTINCT ST4.claim_id, \n" + 
			"            (\n" + 
			"                SELECT ST3.claimed_equipment + ',' AS [text()]\n" + 
			"                FROM (\n" + 
			"                	select ticket_claim.claim_id, ce.equipment_id, job_tag.abbrev as claimed_equipment \n" + 
			"					from ticket_claim\n" + 
			"					left outer join claim_equipment ce on ce.claim_id=ticket_claim.claim_id \n" + 
			"					left outer join job_tag on job_tag.tag_id=ce.equipment_id and job_tag.tag_type='EQUIPMENT') ST3\n" + 
			"                WHERE ST3.claim_id = ST4.claim_id\n" + 
			"                ORDER BY ST3.claim_id\n" + 
			"                FOR XML PATH ('')\n" + 
			"            ) [claimed_equipment]\n" + 
			"        FROM (\n" + 
			"	        select distinct ticket_claim.claim_id, job_tag.abbrev as claimed_equipment\n" + 
			"			from ticket_claim\n" + 
			"			inner join ticket on ticket.ticket_id=ticket_claim.ticket_id\n" + 
			"			inner join job_tag_xref xref on xref.job_id=ticket.job_id\n" + 
			"			inner join job_tag on job_tag.tag_id=xref.tag_id and job_tag.tag_type='EQUIPMENT') ST4\n" + 
			"    ) [Main]";
	
	
	
	
	public static final String ticketTotalSubselect = 
			"	select ticket_id\n" + 
					"		, sum(isnull(volume,0.00)) as claimed_volume\n" + 
					"		, sum(isnull(volume,0.00)+isnull(passthru_expense_volume,0.00)) as claimed_total_volume\n" + 
					"		, sum(isnull(dl_amt,0.00)) as claimed_dl_amt\n" + 
					"		, sum(isnull(dl_expenses,0.00)) as claimed_dl_exp\n" + 
					"		, sum(isnull(passthru_expense_volume,0.00)) as claimed_pt_exp\n" + 
					"--		, sum(hours) as claimed_hours\n" + 
					"	from ticket_claim \n" + 
					"	group by ticket_id" + 
					"\n"; 
	
	
	public static final String sqlSelectClause = 
			"select \n" + 
			"   job_site.name as " + JOB_SITE_NAME + "\n" + 
			" , ticket."+TICKET_ID+"\n" + 
			" , ticket." + JOB_ID + "\n" + 
			" , ticket_claim." + CLAIM_ID + "\n" +
			" , job_tag_xref.tag_id as " + SERVICE_TYPE_ID + "\n" + 
			" , ticket_claim.claim_year as " + CLAIM_YEAR + "\n" +
			" , concat(ticket_claim.claim_year,'-',ticket_claim.claim_week) as "+CLAIM_WEEK+"\n" + 
			" , isnull(ticket_claim.dl_amt,0.00) as "+DL_AMT+"\n" + 
			" , isnull(ticket_claim.dl_expenses,0.00) as " + DL_EXPENSES + "\n" + 
			"-- , isnull(ticket_claim.dl_amt,0.00)+ISNULL(ticket_claim.dl_exp,0.00) as dl_total\n" + 
			" , isnull(ticket_claim.dl_amt,0.00) as "+DL_TOTAL +"\n" + 
			" , ticket.act_price_per_cleaning as "+ TOTAL_VOLUME + "\n" + 
			" , isnull(ticket_claim.volume,0.00) as " + VOLUME_CLAIMED + "\n" + 
			"-- ** this part needs a passthru discussion **\n" + 
			" , ISNULL(ticket_claim.passthru_expense_volume,0.00) as " + PASSTHRU_VOLUME +"\n" + 
			" , ticket_claim." + PASSTHRU_EXPENSE_TYPE + "\n" + 
			" , isnull(ticket_claim.volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00) as "+CLAIMED_VOLUME_TOTAL+"\n" + 
	//gag		" , job.price_per_cleaning - isnull(ticket_claim_totals.claimed_total_volume,0.00)	as "+VOLUME_REMAINING + " \n" + 
			" , ticket.act_price_per_cleaning - isnull(rolling_totals.total_volume,0.00)	as "+VOLUME_REMAINING + " \n" + 
			"-- , isnull(invoice_totals.invoiced_amount,0.00) as billed_amount	, (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim_passthru_totals.passthru_volume,0.00))		- isnull(invoice_totals.invoiced_amount,0.00) as claimed_vs_billed	, ISNULL(ticket_payment_totals.paid_amount,0.00) as paid_amt	, ISNULL(invoice_totals.invoiced_amount,0.00)-ISNULL(ticket_payment_totals.paid_amount,0.00) as "+AMOUNT_DUE+"\n" + 
			"-- , job.price_per_cleaning - (isnull(ticket_claim_totals.claimed_volume,0.00))	as volume_remaining\n" + 
			" , job_tag.abbrev as " + SERVICE_TAG_ID + "\n" + 
			" , ticket_claim.notes as " + NOTES + "\n" + 
			"-- ** used a join to make sure only invoiced tickets get a billed amount **\n" + 
			" , isnull(invoice_totals.invoiced_amount,0.00) as " + BILLED_AMOUNT + "\n" + 
			"-- ** repeat of passthru question **\n" + 
			"-- , (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) as " + CLAIMED_VS_BILLED + " \n" +  
			" , (isnull(ticket_claim_totals.claimed_total_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) as " + CLAIMED_VS_BILLED + " \n" + 
			" , ticket.ticket_status as " + TICKET_STATUS + "\n" + 
			" , ticket_claim.employee_name as " + EMPLOYEE + "\n" + 
			"-- ** this is where the equipment tags would go **\n" + 
			"-- , \"concat equipment tags separated by commas\"\n" + 
			" , tag_list.equipment_tags as " + EQUIPMENT_TAGS + " \n" +
			" , claimed_equipment_list.claimed_equipment as " + CLAIMED_EQUIPMENT + "\n";
			
	
	public static final String sqlFromClause = 
			"from ticket\n" + 
			"join job on job.job_id = ticket.job_id\n" + 
			"join quote on quote.quote_id = job.quote_id\n" + 
			"join division on division.division_id = ticket.act_division_id and division.division_id in ($DIVISION_USER_FILTER$)\n" + 
			"join address job_site on job_site.address_id = quote.job_site_address_id\n" + 
//gag			"left outer join job_tag on job_tag.tag_id=ticket_claim.service_type \n" + 
			"left outer join job_tag_xref on job_tag_xref.job_id=ticket.job_id and job_tag_xref.tag_id in (select tag_id from job_tag where tag_type='SERVICE') \n" + 
			"left outer join job_tag on job_tag.tag_id=job_tag_xref.tag_id \n" + 
			"left outer join ticket_claim on ticket_claim.ticket_id = ticket.ticket_id and job_tag.tag_id=ticket_claim.service_type\n" + 
			"left outer join (\n" + 
			ticketTotalSubselect +
			"	) as ticket_claim_totals on ticket_claim_totals.ticket_id = ticket.ticket_id\n" + 
			"   left outer join (\n" + 
			"	select ticket_id\n" + 
			"		, claim_year\n" + 
			"		, claim_week\n" + 
			"		, (select sum(isnull(volume,0.00) + isnull(passthru_expense_volume,0.00)) from ticket_claim as t where t.ticket_id = ticket_claim.ticket_id\n" + 
			"			and (t.claim_year < ticket_claim.claim_year or (t.claim_year = ticket_claim.claim_year and t.claim_week <= ticket_claim.claim_week))\n" + 
			"			) as total_volume\n" + 
			"	from ticket_claim \n" + 
			"	group by ticket_claim.ticket_id, claim_year, claim_week\n" + 
			"	) as rolling_totals on rolling_totals.ticket_id = ticket.ticket_id \n" + 
			"		and rolling_totals.claim_year = ticket_claim.claim_year and rolling_totals.claim_week = ticket_claim.claim_week \n" + 
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
			"	) as invoice_totals on invoice_totals.ticket_id = ticket.ticket_id \n" +
			"left outer join (" + equipmentTagSubselect + ") tag_list on tag_list.ticket_id=ticket_claim.ticket_id\n" +
			"left outer join (" + claimedEquipmentSubselect + ") claimed_equipment_list on claimed_equipment_list.claim_id=ticket_claim.claim_id";
					
	
	public static final String baseWhereClause1 =
			"where ticket.ticket_type in ('run','job') \n" + 			
			"  and ticket.act_division_id=? \n" + 
			" and ((\n" +
			"     ticket_claim.claim_year=?\n" + 
			"     and ticket_claim.claim_week in ($CLAIMWEEKFILTER$)\n" +
			"   ) "
			; 
	public static final String baseWhereClause2 =
			" or (\n" +
			"     ticket.ticket_status in ('D','C')\n" +
			" ) or ((isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00)) - isnull(invoice_totals.invoiced_amount,0.00) <> 0.00)\n" + 
//gag		"		and (isnull(ticket_claim_totals.claimed_volume,0.00)+ISNULL(ticket_claim.passthru_expense_volume,0.00) <> 0.00)\n" + 
			"		and (ticket.start_date >= '2020-10-01')\n" + 
			" )  \n" 
			; 
	public static final String baseWhereClause3 =
			" and (ticket.start_date <= '2020-11-27')\n" // This is the last day (Friday) of the last week in the month
			; 
	
	
	public static String makeFilteredFromClause(List<SessionDivision> divisionList) {
		List<Integer> divisionIdList = new ArrayList<Integer>();
		divisionIdList = CollectionUtils.collect(divisionList.iterator(), new SessionDivisionTransformer(), divisionIdList);
		String divisionFilter = StringUtils.join(divisionIdList, ",");
		String whereClause = " " + BcrTicketSql.sqlFromClause.replaceAll("\\$DIVISION_USER_FILTER\\$", divisionFilter);
		return whereClause;
	}
	
	public static String makeBaseWhereClause(String workWeeks) {
		return makeBaseWhereClause(workWeeks, false);
	}
	
	public static String makeBaseWhereClause(String workWeeks, Boolean monthlyFilter) {
		String baseWhereClause = monthlyFilter ? BcrTicketSql.baseWhereClause1 + ")" : BcrTicketSql.baseWhereClause1 + BcrTicketSql.baseWhereClause2;		
//gag-this would be the current month filter		String baseWhereClause = monthlyFilter ? BcrTicketSql.baseWhereClause1 + BcrTicketSql.baseWhereClause2 + BcrTicketSql.baseWhereClause3 : BcrTicketSql.baseWhereClause1 + BcrTicketSql.baseWhereClause2;		
		String whereClause = " " + baseWhereClause.replaceAll("\\$CLAIMWEEKFILTER\\$", workWeeks);
		return whereClause;
	}
}
