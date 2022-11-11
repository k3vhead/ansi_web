package com.ansi.scilla.web.contact.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.contact.ContactStatus;
import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.AppUtils;
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
	public static final String CONTACT_STATUS = "contact_status";
	


	
	private static final long serialVersionUID = 1L;
	
	private boolean validOnly = true;

	
	private static final String sqlSelectClause = 
			"select " + BUSINESS_PHONE + ", "
			+ CONTACT_ID + ", "
			+ FAX + ", "
			+ FIRST_NAME + ", "
			+ LAST_NAME + ", "
			+ MOBILE_PHONE + ", "
			+ CONTACT_STATUS + ", "
			+ PREFERRED_CONTACT + ", "
			+ EMAIL + " ";
		

	private static final String sqlFromClause = 
			"from " + AppUtils.makeTableName(Contact.class); 			

	private static final String sqlBaseWhereClause = "";
	
	
	
	public ContactLookupQuery(Integer userId, List<SessionDivision> divisionList, Boolean validOnly) {
		super(sqlSelectClause, sqlFromClause, makeBaseWhereClause(validOnly));
		this.logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, "Valid Only: " + validOnly);
		this.userId = userId;	
		this.validOnly = validOnly;
	}
		
	public ContactLookupQuery(Integer userId, List<SessionDivision> divisionList, Boolean validOnly, String searchTerm) {
		this(userId, divisionList, validOnly);
		this.searchTerm = searchTerm;
	}

	public boolean isValidOnly() {
		return validOnly;
	}
	public void setValidOnly(boolean validOnly) {
		this.validOnly = validOnly;
	}

	private static String makeBaseWhereClause(Boolean validOnly) {
		String contact = AppUtils.makeTableName(Contact.class);
		return validOnly ? "where "+contact+".contact_status='" + ContactStatus.VALID.name() +"'" : sqlBaseWhereClause;
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
		String whereClause = makeBaseWhereClause(this.validOnly);
		if (! StringUtils.isBlank(queryTerm)) {
			queryTerm = queryTerm.toLowerCase();
			String connector = StringUtils.isBlank(whereClause) ? " where " : " and ";
			whereClause = whereClause + connector + " (business_phone like '%" + queryTerm + "%' "
				+ " OR fax like '%" + queryTerm + "%'"
				+ " OR lower(concat(first_name,' ',last_name)) like '%" + queryTerm + "%'"
				+ " OR lower(concat(last_name,' ',first_name)) like '%" + queryTerm + "%'"
				+ " OR lower(concat(last_name,', ',first_name)) like '%" + queryTerm + "%'"
				+ " OR lower(contact_status) like '%" + queryTerm + "%'"
				+ " OR mobile_phone like '%" + queryTerm + "%'" //see if we can concatenate phone number to only numerals, eliminating "-"s. 
				+ " OR lower(email) like '%" + queryTerm + "%')";
		}
		return whereClause;
	}
	
	
}
