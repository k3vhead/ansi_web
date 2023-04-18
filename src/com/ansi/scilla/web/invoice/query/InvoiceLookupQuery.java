package com.ansi.scilla.web.invoice.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class InvoiceLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String INVOICE_ID = "invoice.invoice_id";
	public static final String FLEETMATICS_INVOICE_NBR = "invoice.fleetmatics_invoice_nbr";
	public static final String INVOICE_AMOUNT = "invoice_totals.inv_ppc";
	public static final String INVOICE_TAX = "invoice_totals.inv_tax";
	public static final String INVOICE_TOTAL = "invoice_totals.inv_ppc + invoice_totals.inv_tax";
	public static final String INVOICE_PAID = "isnull(invoice_payment_totals.amount+invoice_payment_totals.tax_amt, '0.00')";
	public static final String INVOICE_BALANCE = "invoice_totals.inv_ppc + invoice_totals.inv_tax - isnull(invoice_payment_totals.amount+invoice_payment_totals.tax_amt, '0.00')";
	public static final String TICKET_COUNT = "invoice_totals.inv_ticket_count";
	public static final String INVOICE_DATE = "invoice_bill_to.invoice_date";
	public static final String BILL_TO_NAME = "bill_to.name";
	public static final String DIV = "concat(division.division_nbr, '-', division.division_code)";
	public static final String PRINT_COUNT = "invoice_print_count.print_count";

	public static final String sqlSelectClause = 
			"select invoice.invoice_id \n"
			+ ", invoice.fleetmatics_invoice_nbr \n"
			+ ", concat(division.division_nbr, '-', division.division_code) as div\n"  
			+ ", bill_to.name as bill_to_name\n" 
			+ ", invoice_totals.inv_ticket_count  as ticket_count\n"
			+ ", invoice_bill_to.invoice_date\n"  
			+ ", invoice_totals.inv_ppc as invoice_amount\n" 
			+ ", invoice_totals.inv_tax as invoice_tax\n"
			+ ", invoice_totals.inv_ppc + invoice_totals.inv_tax as invoice_total\n"
			+ ", isnull(invoice_payment_totals.amount+invoice_payment_totals.tax_amt, '0.00') as invoice_paid\n"
			+ ", invoice_totals.inv_ppc + invoice_totals.inv_tax - isnull(invoice_payment_totals.amount+invoice_payment_totals.tax_amt, '0.00') as invoice_balance\n"
			+ ", invoice_print_count.print_count as print_count\n" 		
			;

	/**
	 * If you change the "sqlFromClause" or the "sqlGroupClause" make sure you check the InvoiceLookupServlet. 
	 * It looks for specific syntax to filter for new invoices within a division
	 */
	public static final String sqlFromClause = 
			" from invoice " + "\n" +
			" join (select distinct invoice_id, act_division_id, bill_to_address_id, invoice_date as invoice_date\n" + 
			"	from ticket\n" + 
			"   join job on job.job_id = ticket.job_id\n" + 
			"   join quote on quote.quote_id = job.quote_id) as invoice_bill_to \n" + 
			"	on invoice_bill_to.invoice_id = invoice.invoice_id" + "\n" +
			" inner join division on division.division_id = invoice_bill_to.act_division_id " + "\n" +
			"   AND division.division_id in ( select division_id from division_user where user_id=?) " + "\n" +
			" join address as bill_to on bill_to.address_id=invoice_bill_to.bill_to_address_id\n" + 
			" left outer join (select invoice_id, bill_to_address_id, act_division_id, invoice_date, sum(amount) as amount, sum(tax_amt) as tax_amt \n" + 
			"	from ticket_payment\n" + 
			"	join ticket on ticket.ticket_id = ticket_payment.ticket_id \n" +
			"   join job on job.job_id = ticket.job_id\n" + 
			"   join quote on quote.quote_id = job.quote_id \n" + 
			"   group by invoice_id, bill_to_address_id, act_division_id, invoice_date)  \n" + 
			"	as invoice_payment_totals on invoice_payment_totals.invoice_id = invoice.invoice_id\n" + 
			"	and invoice_payment_totals.act_division_id = invoice_bill_to.act_division_id\n" + 
			"	and invoice_payment_totals.invoice_date = invoice_bill_to.invoice_date\n" +
			"	and invoice_payment_totals.bill_to_address_id = invoice_bill_to.bill_to_address_id\n" +
			" left outer join (select invoice_id, bill_to_address_id, act_division_id, invoice_date\n" + 
			"	, sum(act_price_per_cleaning) as inv_ppc, sum(act_tax_amt) as inv_tax, count(ticket_id) as inv_ticket_count\n" + 
			"	from ticket \n" + 
			"   join job on job.job_id = ticket.job_id\n" + 
			"   join quote on quote.quote_id = job.quote_id \n" + 
			"   group by invoice_id, bill_to_address_id, act_division_id, invoice_date, bill_to_address_id)  \n" + 
			"	as invoice_totals on invoice_totals.invoice_id = invoice.invoice_id \n" + 
			"	and invoice_totals.act_division_id = invoice_bill_to.act_division_id \n" + 
			"	and invoice_totals.invoice_date = invoice_bill_to.invoice_date\n" +
			"	and invoice_totals.bill_to_address_id = invoice_bill_to.bill_to_address_id\n" +
			" left outer join (select invoice_id, count(print_date) as print_count from invoice_print\n" + 
			"	group by invoice_id) " +
			"   as invoice_print_count on invoice_print_count.invoice_id = invoice.invoice_id\n" + 
			"\n" 
			;

	public static final String sqlWhereClause = "";
	
	

	
	public InvoiceLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
//		this.divisionList = divisionList;
	}
	
	

	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + INVOICE_ID + " desc ";
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
		logger.log(Level.DEBUG, "makeWhereClause");
		String whereClause = sqlWhereClause;
		String joiner = StringUtils.isBlank(sqlWhereClause) ? " where " : " and ";
		
		
		if (! StringUtils.isBlank(queryTerm)) {
			String searchTerm = queryTerm.toLowerCase();
			Collection<String> stringFields= Arrays.asList(new String[] {
				"concat(division.division_nbr, '-', division.division_code)",
				"bill_to.name",
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
				"invoice.invoice_id",
				"invoice.fleetmatics_invoice_nbr",
			});
			
			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);
			if ( StringUtils.isNumeric(searchTerm)) {
				whereFields.addAll(numericFields);
			}

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(searchTerm));
			whereClause = whereClause + joiner +  "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
		}

//		List<ColumnFilter> filterList = new ArrayList<ColumnFilter>();
//		if ( divisionId != null ) {
//			filterList.add(new ColumnFilter(DIVISION_ID, divisionId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
//		}
//		if ( jobId != null ) {
//			filterList.add(new ColumnFilter(JOB_ID, jobId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
//		}
//		if ( startDate != null ) {
//			filterList.add(new ColumnFilter(TICKET_START_DATE, startDate, ColumnFilter.ComparisonType.EQUAL_DATE));			
//		}
//		if ( ! StringUtils.isBlank(status)) {
//			filterList.add(new ColumnFilter(STATUS, status, ColumnFilter.ComparisonType.LIST_STRING));
//		}
//		super.setConstraintList(filterList);

		return whereClause;
	}

	
	
		
	
	
	
}
