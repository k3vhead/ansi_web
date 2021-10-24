package com.ansi.scilla.web.contact.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class ContactLookupQuery extends LookupQuery {
	public static final String BUSINESS_PHONE = "business_phone";
	public static final String CONTACT_ID = "contact_id";
	public static final String FAX = "fax";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String PREFERRED_CONTACT = "preferred_contact";
	public static final String EMAIL = "email";
	


	
	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
			"select " + BUSINESS_PHONE + ", "
			+ CONTACT_ID + ", "
			+ FAX + ", "
			+ FIRST_NAME + ", "
			+ LAST_NAME + ", "
			+ MOBILE_PHONE + ", "
			+ PREFERRED_CONTACT + ", "
			+ EMAIL + " ";
		

	private static final String sqlFromClause = 
			"from contact"; 			

	private static final String baseWhereClause = "";
	
	
	
	public ContactLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;	
	}

	public ContactLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}

	
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + LAST_NAME + " asc ";
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
//	protected String makeWhereClause(String queryTerm)  {
//		String whereClause = ContactLookupQuery.baseWhereClause;
//		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
//		
//		if ( ! StringUtils.isBlank(queryTerm) ) {
//			whereClause =  whereClause + joiner + " (\n"
//					+ " lower(address.name) like '%" + queryTerm.toLowerCase() +  "%'" +
//					"\n OR lower( concat(contact.first_name, ' ', contact.last_name) ) like '%" + queryTerm.toLowerCase() + "%'" +
//					"\n OR lower( call_log.summary ) like '%" + queryTerm.toLowerCase() + "%'" +
//					"\n OR lower( concat(ansi_user.first_name, ' ', ansi_user.last_name) ) like '%" + queryTerm.toLowerCase() + "%'" +
//					"\n OR lower( call_log.content ) like '%" + queryTerm.toLowerCase() + "%'" +
//					")" ;
//		}
//		
//		return whereClause;
//	}

	
	protected String makeWhereClause(String queryTerm) {
		String whereClause = "";
		if (! StringUtils.isBlank(queryTerm)) {
			whereClause = "where business_phone like '%" + queryTerm + "%' "
				+ " OR fax like '%" + queryTerm + "%'"
				+ " OR lower(concat(first_name,' ',last_name)) like '%" + queryTerm + "%'"
				+ " OR lower(concat(last_name,' ',first_name)) like '%" + queryTerm + "%'"
				+ " OR lower(concat(last_name,', ',first_name)) like '%" + queryTerm + "%'"
				+ " OR mobile_phone like '%" + queryTerm + "%'" //see if we can concatinate phone number to only numerals, eliminating "-"s. 
				+ " OR lower(email) like '%" + queryTerm + "%'";
		}
		return whereClause;
	}
	
	
}
