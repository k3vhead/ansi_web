package com.ansi.scilla.web.ticket.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class TicketLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	
	public static final String sqlSelect = 
			"SELECT view_ticket_log.ticket_id as view_ticket_id, view_ticket_log.job_id as view_job_id, " 
			+ "\n\t view_ticket_log.start_date as view_start_date, "
			+ "\n\t view_ticket_log.ticket_status as view_ticket_status, view_ticket_log.ticket_type as view_ticket_type, "
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
	private List<SessionDivision> divisionList;
			
	
	public TicketLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
		this.divisionList = divisionList;
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
		String divisionFilter = "";
		if ( divisionId != null ) {
			divisionFilter = " AND " + Ticket.TABLE + "." + Ticket.ACT_DIVISION_ID + "=?";
		}
		super.setSqlFromClause(makeFromClause(sqlFromClause, divisionList, divisionFilter));
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String makeWhereClause(String queryTerm) {
		// TODO Auto-generated method stub
		return null;
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
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			TicketLookupQuery x = new TicketLookupQuery(5, sdList);
			x.setDivisionId(112);
			ResultSet rs = x.select(conn,  0, 10);
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
