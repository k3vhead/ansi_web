package com.ansi.scilla.web.claims.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class DirectLaborLookupQuery extends ClaimsQuery {

	public static final String WORK_DATE = "work_date";
	public static final String WASHER_LAST_NAME = "washer_last_name";
	public static final String WASHER_FIRST_NAME = "washer_first_name";
	public static final String VOLUME = "volume";
	public static final String DL_AMT = "dl_amt";
	public static final String HOURS= "hours";
	public static final String NOTES = "notes";


	
	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
				"select ticket_claim.work_date,\n" + 
				"		ansi_user.last_name as washer_last_name,\n" + 
				"		ansi_user.first_name as washer_first_name,\n" + 
				"		ticket_claim.volume,\n" + 
				"		ticket_claim.dl_amt,\n" + 
				"		ticket_claim.hours,\n" + 
				"		ticket_claim.notes\n";
				
	private static final String sqlFromClause = "from "+TicketClaim.TABLE+"\n" + 
			"left outer join "+User.TABLE+" on ansi_user.user_id=ticket_claim.washer_id\n";

	private static final String baseWhereClause = "where ticket_claim.ticket_id=?\n";
	
	
	
	public DirectLaborLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer ticketFilter) {
		super(sqlSelectClause, makeFromClause(sqlFromClause, divisionList), baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		this.divisionList = divisionList;
		super.addBaseFilter(ticketFilter);
	}

	public DirectLaborLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer ticketFilter, String searchTerm) {
		this(userId, divisionList, ticketFilter);
		this.searchTerm = searchTerm;
	}

	

	

	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = "order by ticket_claim.work_date asc, ansi_user.last_name asc, ansi_user.first_name asc";
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
		String whereClause = DirectLaborLookupQuery.baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		
		
		if ( StringUtils.isBlank(queryTerm) ) {
//			if ( this.ticketFilter != null ) {
//				whereClause = whereClause + joiner + " ticket.ticket_id=" + this.ticketFilter;
//			}
		} else {
//			String ticketClause = this.ticketFilter == null ? "" : "ticket.ticket_id=" + this.ticketFilter + " and ";
			whereClause =  whereClause + joiner + " (\n" +
//					ticketClause +
					"\n ticket_claim.work_date like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR ansi_user.first_name like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR ansi_user.last_name like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR CONCAT(ansi_user.last_name, ', ', ansi_user.last_name) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		
		return whereClause;
	}

	
	
	
	
}
