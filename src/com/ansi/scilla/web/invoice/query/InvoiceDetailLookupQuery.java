package com.ansi.scilla.web.invoice.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class InvoiceDetailLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String INVOICE_ID = "ticket.invoice_id"; 
	public static final String INVOICE_PPC = "invoice_totals.amount"; 
	public static final String INVOICE_TAX = "invoice_totals.tax_amt"; 
	public static final String DIV = "concat(division_nbr,'-',division_code)"; 
	public static final String BILL_TO_NAME = "bill_to.name"; 
	public static final String JOB_SITE_NAME = "job_site.name"; 
	public static final String JOB_SITE_ADDRESS = "job_site.address1"; 
	public static final String TICKET_ID = "ticket.ticket_id"; 
	public static final String TICKET_STATUS = "ticket_status"; 
	public static final String TICKET_TYPE = "ticket_type"; 
	public static final String COMPLETED_DATE = "process_date"; 
	public static final String INVOICE_DATE = "invoice_date"; 
	public static final String PPO = "ppo"; 
	public static final String PPC = "act_price_per_cleaning"; 
	public static final String TAXES = "act_tax_amt"; 
	public static final String TOTAL = "isnull(act_price_per_cleaning,0.00) + isnull(act_tax_amt,0.00)"; 
	public static final String PAID = "isnull(ticket_payment_totals.total,0.00)"; 
	public static final String DUE = "(isnull(act_price_per_cleaning,0.00) + isnull(act_tax_amt,0.00)) - isnull(ticket_payment_totals.total,0.00)";
	
	public static final String sqlSelect = 
			"select \n" + 
			"	ticket.invoice_id\n" + 
			"	, invoice_totals.amount as invoice_ppc\n" + 
			"	, invoice_totals.tax_amt as invoice_tax\n" + 
			"	, concat(division_nbr,'-',division_code) as div\n" + 
			"	, bill_to.name as bill_to_name\n" + 
			"	, job_site.name as job_site_name\n" + 
			"	, job_site.address1 as job_site_address\n" + 
			"	, ticket.ticket_id\n" + 
			"	, ticket_status\n" + 
			"	, ticket_type\n" + 
			"	, process_date as completed_date\n" + 
			"	, invoice_date \n" +
			"	, ppo \n" + 
			"	, act_price_per_cleaning as ppc\n" + 
			"	, act_tax_amt as taxes\n" + 
			"	, isnull(act_price_per_cleaning,0.00) + isnull(act_tax_amt,0.00) as total\n" + 
			"	, isnull(ticket_payment_totals.total,0.00) as paid\n" + 
			"	, (isnull(act_price_per_cleaning,0.00) + isnull(act_tax_amt,0.00)) - isnull(ticket_payment_totals.total,0.00) as due\n";

	public static final String sqlFromClause = 
			"from ticket\n" + 
			"	join job on ticket.job_id = job.job_id \n" + 
			"	join quote on job.quote_id = quote.quote_id \n" + 
			"	join division on division.division_id = act_division_id \n" + 
			"	join address as bill_to on bill_to.address_id = bill_to_address_id \n" + 
			"	join address as job_site on job_site.address_id = quote.job_site_address_id \n" + 
			"	left outer join (\n" + 
			"		select ticket_id, \n" + 
			"			sum(ticket_payment.amount) as amount, \n" + 
			"			sum(tax_amt) as tax_amt,\n" + 
			"			sum(isnull(ticket_payment.amount,0.00) + isnull(ticket_payment.tax_amt,0.00)) as total\n" + 
			"			from ticket_payment \n" + 
			"			join payment on payment.payment_id = ticket_payment.payment_id\n" + 
			"			group by ticket_id) as ticket_payment_totals \n" + 
			"		on ticket_payment_totals.ticket_id = ticket.ticket_id \n" + 
			"	left outer join (\n" + 
			"		select invoice_id, \n" + 
			"			sum(ticket.act_price_per_cleaning) as amount, \n" + 
			"			sum(ticket.act_tax_amt) as tax_amt,\n" + 
			"			sum(ticket.act_price_per_cleaning + ticket.act_tax_amt) as total\n" + 
			"			from ticket \n" + 
			"			group by invoice_id) as invoice_totals \n" + 
			"		on invoice_totals.invoice_id = ticket.invoice_id  ";
	
	public static final String sqlWhereClause = "\n where invoice_date is not null ";
	
	

//	private List<SessionDivision> divisionList;
	
	public InvoiceDetailLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
//		this.divisionList = divisionList;
	}
	
	

	/** *********************** **/
	
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
					"bill_to.name" ,
					"job_site.name", 
					"job_site.address1", 
					"concat(division_nbr,'-',division_code)" ,
					"process_date",
					"invoice_date",
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					"ticket.invoice_id", 
					"invoice_totals.amount",
					"invoice_totals.tax_amt",
					"ticket.ticket_id",
					"act_price_per_cleaning",
					"act_tax_amt",
					"ticket_payment_totals.total",
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

	
	
	private static String makeFromClause(String sqlfromclause, List<SessionDivision> divisionList, String divisionFilter) {
		List<Integer> divisionIdList = (List<Integer>) CollectionUtils.collect(divisionList, new SessionDivisionTransformer());
		String divisionString = StringUtils.join(divisionIdList, ",");
		String sql = StringUtils.replace(sqlFromClause, "$USERFILTER$", divisionString);
		System.out.println("adding " + divisionFilter + " to sql");
		sql = StringUtils.replace(sql, "$DIVISIONFILTER$", divisionFilter);
		
		return sql;
	}

	
	public static void main(String[] args) {
		Connection conn = null;
//		Calendar startDate = new GregorianCalendar(2019, Calendar.APRIL, 15);
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			InvoiceDetailLookupQuery x = new InvoiceDetailLookupQuery(5, sdList);
			x.setSearchTerm("Hospital");
//			x.setDivisionId(106);
//			x.setJobId(211660);
//			x.setStartDate(startDate);
//			x.setStatus("D,P");
			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
			ResultSet rs = x.select(conn,  0, 10);
			while ( rs.next() ) { 
				System.out.println(rs.getInt("invoice_id"));
			}
			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
