package com.ansi.scilla.web.callNote.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.claims.query.ClaimsQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class CallNoteLookupQuery extends ClaimsQuery {
	public static final String CALL_LOG_ID = "call_log.call_log_id"; 
	public static final String ADDRESS_ID = "call_log.address_id";
	public static final String ADDRESS_NAME = "address.name as address_name";
	public static final String CONTACT_ID = "call_log.contact_id";
	public static final String CONTACT_NAME = "concat(contact.first_name, ' ', contact.last_name) as contact_name";
	public static final String SUMMARY = "call_log.summary";
	public static final String USER_ID = "call_log.user_id";
	public static final String ANSI_CONTACT = "concat(ansi_user.first_name, ' ', ansi_user.last_name) as ansi_contact";
	public static final String START_TIME = "call_log.start_time";
	public static final String CONTACT_TYPE = "code.description as contact_type";
	


	
	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
			"select call_log.call_log_id, \n" + 
			"		call_log.address_id, \n" + 
			"		address.name as address_name,\n" + 
			"		call_log.contact_id, \n" + 
			"		concat(contact.first_name, ' ', contact.last_name) as contact_name,\n" + 
			"		call_log.summary,\n" + 
			"		call_log.user_id, \n" + 
			"		concat(ansi_user.first_name, ' ', ansi_user.last_name) as ansi_contact,\n" + 
			"		call_log.start_time,\n" + 
			"		code.description as contact_type\n";
		

	private static final String sqlFromClause = 
			"from call_log\n" + 
			"inner join address on address.address_id = call_log.address_id\n" + 
			"inner join contact on contact.contact_id = call_log.contact_id\n" + 
			"inner join ansi_user on ansi_user.user_id = call_log.user_id\n" + 
			"inner join code on code.table_name='call_note' and code.field_name='call_xref' and code.value=call_log.contact_type\n"; 			

	private static final String baseWhereClause = "";
	
	
	
	public CallNoteLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, makeFromClause(sqlFromClause, divisionList), baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
		this.divisionList = divisionList;
	}

	public CallNoteLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}

	
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( ! StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + START_TIME + " desc ";
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
		String whereClause = CallNoteLookupQuery.baseWhereClause;
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			whereClause =  whereClause + joiner + " (\n"
					+ " lower(address.name) like '%" + queryTerm.toLowerCase() +  "%'" +
					"\n OR lower( concat(contact.first_name, ' ', contact.last_name) ) like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR lower( call_log.summary ) like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR lower( concat(ansi_user.first_name, ' ', ansi_user.last_name) ) like '%" + queryTerm.toLowerCase() + "%'" +
					"\n OR lower( call_log.content ) like '%" + queryTerm.toLowerCase() + "%'" +
					")" ;
		}
		
		return whereClause;
	}

	
	
	
	
}
