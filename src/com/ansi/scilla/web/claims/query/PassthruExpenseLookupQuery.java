package com.ansi.scilla.web.claims.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.TicketClaimPassthru;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class PassthruExpenseLookupQuery extends ClaimsQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String WORK_DATE="work_date";
	public static final String PASSTHRU_EXPENSE_VOLUME ="passthru_expense_volume";
	public static final String NOTES = "notes";
	public static final String PASSTHRU_EXPENSE_TYPE = "passthru_expense_type";
	public static final String WASHER_FIRST_NAME = "washer_first_name";
	public static final String WASHER_LAST_NAME = "washer_last_name";
		
	protected static final String sqlSelectClause = "select convert(VARCHAR,ticket_claim_passthru.work_date,101) as work_date, \n" + 
			"	ticket_claim_passthru.passthru_expense_volume,\n" + 
			"	ticket_claim_passthru.notes,\n" + 
			"	code.display_value as passthru_expense_type,\n" + 
			"	ansi_user.first_name as washer_first_name, \n" + 
			"	ansi_user.last_name as washer_last_name\n";
			
				
	private static final String sqlFromClause = "from ticket_claim_passthru\n" + 
			"left outer join code on code.table_name='"+TicketClaimPassthru.TABLE+"' \n" + 
			"	and code.field_name='"+TicketClaimPassthru.PASSTHRU_EXPENSE_TYPE+"' \n" + 
			"	and code.value=ticket_claim_passthru.passthru_expense_type\n" + 
			"left outer join ansi_user on ansi_user.user_id=ticket_claim_passthru.washer_id\n";

	private static final String baseWhereClause = "where ticket_claim_passthru.ticket_id=?\n";
	
	
	
	public PassthruExpenseLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer ticketFilter) {
		super(sqlSelectClause, makeFromClause(sqlFromClause, divisionList), baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		this.divisionList = divisionList;
		super.addBaseFilter(ticketFilter);
	}

	public PassthruExpenseLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer ticketFilter, String searchTerm) {
		this(userId, divisionList, ticketFilter);
		this.searchTerm = searchTerm;
	}

	

	

	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = "order by ticket_claim_passthru.work_date asc";
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
		String whereClause = PassthruExpenseLookupQuery.baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			String searchTerm = queryTerm.toLowerCase();
			whereClause =  whereClause + joiner + " (\n" +
					"\n lower(ticket_claim_passthru.notes) like '%" + searchTerm + "%'" +
					"\n OR lower(ansi_user.first_name) like '%" + searchTerm + "%'" +
					"\n OR lower(ansi_user.last_name) like '%" + searchTerm + "%'" +
					"\n OR lower(CONCAT(ansi_user.last_name, ', ', ansi_user.first_name)) like '%" + searchTerm + "%'" +
					"\n OR lower(CONCAT(ansi_user.first_name, ' ', ansi_user.last_name)) like '%"  + searchTerm + "%'" +
					")" ;
		}
		
		
		return whereClause;
	}

	
	
	
	
}
