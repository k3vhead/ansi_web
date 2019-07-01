package com.ansi.scilla.web.ticket.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
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
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class TicketLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "view_ticket_log.ticket_id";
	public static final String STATUS = "view_ticket_log.ticket_status";
	public static final String TYPE = "view_ticket_log.ticket_type";
	public static final String VIEW_TICKET_START_DATE = "view_ticket_log.start_date";
	public static final String DIV = "concat(division.division_nbr,'-',division.division_code)";
	public static final String BILL_TO = "a1.name";
	public static final String JOB_SITE = "a2.name";
	public static final String ADDRESS = "a2.address1";
	public static final String TICKET_START_DATE = "ticket.start_date";
	public static final String START_DATE = "view_ticket_log.start_date";
	public static final String FREQ = "job.job_frequency";
	public static final String PPC = "isnull(ticket.act_price_per_cleaning, job.price_per_cleaning)";
	public static final String JOB = "job.job_nbr";
	public static final String JOB_ID = "view_ticket_log.job_id";
	public static final String SERVICE_DESCRIPTION = "job.service_description";
	public static final String PROCES_DATE = "ticket.process_date";
	public static final String INVOICE = "ticket.invoice_id";
	public static final String AMOUNT_DUE = "case ticket.ticket_status "
			+ " when 'i' then isnull(isnull(ticket.act_price_per_cleaning,'0.00')-isnull(ticket_payment_totals.amount,'0.00'),'0.00') "
			+ " when 'p' then isnull(isnull(ticket.act_price_per_cleaning,'0.00')-isnull(ticket_payment_totals.amount,'0.00'),'0.00') "
			+ " else '0.00' "
			+ " end";
	public static final String DIVISION_ID = "division.division_id";
	
	public static final String sqlSelect = 
			"SELECT view_ticket_log.ticket_id as view_ticket_id, view_ticket_log.job_id as view_job_id, " 
			+ "\n\t view_ticket_log.start_date as view_start_date, "
			+ "\n\t view_ticket_log.ticket_status as view_ticket_status, view_ticket_log.ticket_type as view_ticket_type, "
			+ "\n\t concat(division.division_nbr,'-',division.division_code) as div,"
			+ "\n\t ticket.*, "
			+ "\n\t division.*,  "
			+ "\n\t job.job_status, job.service_description,  job.job_frequency, job.job_nbr, "
			+ "\n\t isnull(ticket.act_price_per_cleaning, job.price_per_cleaning) as price_per_cleaning, " 
			+ "\n\t quote.*,   "
			+ "\n\t a1.name as bill_to_name, "  
			+ "\n\t a2.name as job_site_name, a2.address1 as job_site_address,"
			+ "\n\t isnull(ticket_payment_totals.amount,'0.00') as paid_amount,"
			+ "\n\t isnull(ticket_payment_totals.tax_amt,'0.00') as paid_tax_amt,"
			+ "\n\t amount_due = case ticket.ticket_status "
			+ "\n\t\t when 'i' then isnull(isnull(ticket.act_price_per_cleaning,'0.00')-isnull(ticket_payment_totals.amount,'0.00'),'0.00') "
			+ "\n\t\t when 'p' then isnull(isnull(ticket.act_price_per_cleaning,'0.00')-isnull(ticket_payment_totals.amount,'0.00'),'0.00') "
			+ "\n\t\t else '0.00' "
			+ "\n\t\t end ";
//			+ "\n\t isnull(isnull(ticket.act_price_per_cleaning,job.price_per_cleaning)-isNull(ticket_payment_totals.amount,'0.00'),'0.00') as amount_due ";
	public static final String sqlFromClause = 
			"\n FROM view_ticket_log  "
			+ "\n LEFT JOIN ticket on ticket.ticket_id = view_ticket_log.ticket_id" 
			+ "\n JOIN job ON job.job_id = view_ticket_log.job_id "
			+ "\n JOIN quote ON quote.quote_id = job.quote_id  "
			+ "\n   and quote.division_id in ($USERFILTER$)"
			+ "\n JOIN division ON division.division_id = job.division_id  $DIVISIONFILTER$" 
			+ "\n JOIN address a1 ON quote.bill_to_address_id = a1.address_id  "
			+ "\n JOIN address a2 ON quote.job_site_address_id = a2.address_id "
			+ "\n left outer join (select ticket_id, sum(amount) as amount, sum(tax_amt) as tax_amt from ticket_payment group by ticket_id) "
			+ "\n   as ticket_payment_totals on ticket_payment_totals.ticket_id = ticket.ticket_id ";
	
	public static final String sqlWhereClause = "";
	
	
	private Integer jobId;
	private Integer divisionId;
	private Calendar startDate;
	private String status;
//	private List<SessionDivision> divisionList;
	
	public TicketLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
//		this.divisionList = divisionList;
	}
	
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
//		String divisionFilter = "";
//		if ( divisionId != null ) {
//			divisionFilter = " AND " + Ticket.TABLE + "." + Ticket.ACT_DIVISION_ID + "=?";
//		}
//		super.setSqlFromClause(makeFromClause(sqlFromClause, divisionList, divisionFilter));
	}
	public Calendar getStartDate() {
		return startDate;
	}
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}





	/** *********************** **/
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + TICKET_ID + " desc ";
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
//					"division.division_code", 
					"ticket.fleetmatics_id" ,
					"CONCAT(quote_number,revision)", 
					"CONCAT(division_nbr,'-',division_code)", 
					"a2.name" ,
					"a2.address1", 
					"a1.name" ,
					"service_description",
					"case ticket.ticket_status \r\n" + 
					"		when 'i' then isnull(isnull(ticket.act_price_per_cleaning,'0.00')-isnull(ticket_payment_totals.amount,'0.00'),'0.00')\r\n" + 
					"		when 'p' then isnull(isnull(ticket.act_price_per_cleaning,'0.00')-isnull(ticket_payment_totals.amount,'0.00'),'0.00')\r\n" + 
					"		else '0.00'\r\n" + 
					"		end"
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					"view_ticket_log.ticket_id", 
					"view_ticket_log.job_id" ,
//					"division.division_nbr", 
					"ticket.invoice_id" 
			});
			
			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);
			if ( StringUtils.isNumeric(searchTerm)) {
				whereFields.addAll(numericFields);
			}

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(searchTerm));
			whereClause = whereClause + joiner +  "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
		}

		List<ColumnFilter> filterList = new ArrayList<ColumnFilter>();
		if ( divisionId != null ) {
			filterList.add(new ColumnFilter(DIVISION_ID, divisionId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
		}
		if ( jobId != null ) {
			filterList.add(new ColumnFilter(JOB_ID, jobId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
		}
		if ( startDate != null ) {
			filterList.add(new ColumnFilter(TICKET_START_DATE, startDate, ColumnFilter.ComparisonType.EQUAL_DATE));			
		}
		if ( ! StringUtils.isBlank(status)) {
			filterList.add(new ColumnFilter(STATUS, status, ColumnFilter.ComparisonType.LIST_STRING));
		}
		super.setConstraintList(filterList);

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
		Calendar startDate = new GregorianCalendar(2019, Calendar.APRIL, 15);
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			TicketLookupQuery x = new TicketLookupQuery(5, sdList);
//			x.setSearchTerm("Chicago");
			x.setDivisionId(106);
//			x.setJobId(211660);
//			x.setStartDate(startDate);
//			x.setStatus("D,P");
			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
			ResultSet rs = x.select(conn,  0, 10);
			while ( rs.next() ) { 
				System.out.println(rs.getInt("ticket_id"));
			}
			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
