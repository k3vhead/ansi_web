package com.ansi.scilla.web.ticket.query;

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
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class TicketAssignmentLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "view_ticket_log.ticket_id";
	public static final String DIV = "concat(division.division_nbr,'-',division.division_code)";
	public static final String JOB_ID = "view_ticket_log.job_id";
	public static final String START_DATE = "view_ticket_log.start_date";
	public static final String TICKET_STATUS = "view_ticket_log.ticket_status";
	public static final String TICKET_TYPE = "view_ticket_log.ticket_type"; 
	public static final String ACT_PRICE_PER_CLEANING = "ticket.act_price_per_cleaning";
	public static final String JOB_STATUS = "job.job_status";
	public static final String SERVICE_DESCRIPTION = "job.service_description";
	public static final String JOB_FREQUENCY = "job.job_frequency";
	public static final String JOB_NBR = "job.job_nbr"; 
	public static final String PRICE_PER_CLEANING = "isnull(ticket.act_price_per_cleaning, job.price_per_cleaning)"; 
	public static final String QUOTE_ID = "quote.quote_id";
	public static final String BILL_TO_NAME = "a1.name"; 
	public static final String JOB_SITE_NAME = "a2.name";
	public static final String JOB_SITE_ADDRESS = "a2.address1";
	public static final String WASHER_ID = "ansi_user.user_id";
	public static final String FIRST_NAME = "ansi_user.first_name";
	public static final String LAST_NAME = "ansi_user.last_name";	
	public static final String DIVISION_ID = "division.division_id";
	public static final String WASHER = "concat(ansi_user.last_name, ', ', ansi_user.first_name)";
	
	public static final String sqlSelect = 
		"SELECT view_ticket_log.ticket_id as view_ticket_id, view_ticket_log.job_id as view_job_id, "
		+ "\n\t	 view_ticket_log.start_date as view_start_date,  "
		+ "\n\t	 view_ticket_log.ticket_status as view_ticket_status, view_ticket_log.ticket_type as view_ticket_type, " 
		+ "\n\t  ticket.act_price_per_cleaning,  "
		+ "\n\t	 division.division_id,   "
		+ "\n\t concat(division.division_nbr,'-',division.division_code) as div,"
		+ "\n\t	 job.job_status, job.service_description,  job.job_frequency, job.job_nbr, " 
		+ "\n\t	 isnull(ticket.act_price_per_cleaning, job.price_per_cleaning) as price_per_cleaning, " 
		+ "\n\t	 quote.quote_id,    "
		+ "\n\t	 a1.name as bill_to_name, " 
		+ "\n\t	 a2.name as job_site_name, a2.address1 as job_site_address, "
		+ "\n\t	 ansi_user.user_id, "
		+ "\n\t  CASE\n" 
		+ "\n\t  	WHEN ansi_user.user_id is NULL then null" 
		+ "\n\t		ELSE concat(ansi_user.last_name, ', ', ansi_user.first_name) " 
		+ "\n\t	 END as washer";
		
	public static final String sqlFromClause = 
			"\n FROM view_ticket_log   "
			+ "\n LEFT JOIN ticket on ticket.ticket_id = view_ticket_log.ticket_id "
			+ "\n JOIN job ON job.job_id = view_ticket_log.job_id  "
			+ "\n JOIN quote ON quote.quote_id = job.quote_id   "
			+ "\n   $DIVISIONFILTER$ "
			+ "\n JOIN division ON division.division_id = job.division_id   "
			+ "\n JOIN address a1 ON quote.bill_to_address_id = a1.address_id "  
			+ "\n JOIN address a2 ON quote.job_site_address_id = a2.address_id  "
			+ "\n LEFT JOIN ticket_assignment on ticket_assignment.ticket_id=view_ticket_log.ticket_id "
			+ "\n LEFT JOIN ansi_user on ansi_user.user_id = ticket_assignment.washer_id ";
	
	public static final String sqlWhereClause = "\n WHERE view_ticket_log.ticket_status='"+TicketStatus.DISPATCHED.code()+"'";
	
	
//	private Integer jobId;
//	private Integer ticketId;
//	private Integer divisionId;
//	private Integer washerId;
	
	public TicketAssignmentLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	
//	public Integer getJobId() {
//		return jobId;
//	}
//	public void setJobId(Integer jobId) {
//		this.jobId = jobId;
//	}
//	public Integer getDivisionId() {
//		return divisionId;
//	}
//	public void setDivisionId(Integer divisionId) {
//		this.divisionId = divisionId;
//	}
//	public Integer getWasherId() {
//		return washerId;
//	}
//	public void setWasherId(Integer washerId) {
//		this.washerId = washerId;
//	}
//	public Integer getTicketId() {
//		return ticketId;
//	}
//	public void setTicketId(Integer ticketId) {
//		this.ticketId = ticketId;
//	}

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
					START_DATE,
					TICKET_STATUS,
					TICKET_TYPE,
					JOB_STATUS,
					SERVICE_DESCRIPTION,
					JOB_FREQUENCY,
					BILL_TO_NAME,
					JOB_SITE_NAME,
					JOB_SITE_ADDRESS,
					FIRST_NAME,
					LAST_NAME,
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					TICKET_ID,
					JOB_ID,
					ACT_PRICE_PER_CLEANING,
					JOB_NBR,
					PRICE_PER_CLEANING,
					QUOTE_ID,
					WASHER_ID,
					DIVISION_ID,
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
//		if ( washerId != null ) {
//			filterList.add(new ColumnFilter(WASHER_ID, washerId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
//		}
//		if ( ticketId != null ) {
//			filterList.add(new ColumnFilter(TICKET_ID, ticketId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
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
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			TicketAssignmentLookupQuery x = new TicketAssignmentLookupQuery(5, sdList);
//			x.setSearchTerm("Chicago");
//			x.setDivisionId(102);
//			x.setJobId(212137);
//			x.setTicketId(789391);
			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
			ResultSet rs = x.select(conn,  0, 50);
			while ( rs.next() ) { 
				System.out.println(rs.getInt("view_ticket_id") + "\t" + rs.getString("last_name"));
			}
			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
