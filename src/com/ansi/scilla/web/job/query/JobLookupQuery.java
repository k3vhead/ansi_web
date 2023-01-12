package com.ansi.scilla.web.job.query;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;

public class JobLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	
	public static final String JOB_ID = "job.job_id";
	public static final String QUOTE_ID = "job.quote_id";
	public static final String JOB_NBR = "job.job_nbr";
	public static final String JOB_FREQUENCY = "job.job_frequency";
	public static final String JOB_STATUS = "job.job_status";
	public static final String PRICE_PER_CLEANING = "job.price_per_cleaning";
	public static final String PO_NUMBER = "job.po_number";
	public static final String SERVICE_DESCRIPTION = "job.service_description";
	public static final String ACTIVATION_DATE = "job.activation_date"; 
	public static final String START_DATE = "job.start_date"; 
	public static final String CANCEL_DATE = "job.cancel_date"; 
	public static final String CANCEL_REASON = "job.cancel_reason";
	public static final String QUOTE_NBR = "concat(quote.quote_number,quote.revision)";
	public static final String PROPOSAL_DATE = "quote.proposal_date";
	public static final String DIVISION_ID = "division.division_id";
	public static final String DIV = "concat(division.division_nbr,'-',division.division_code)";
	public static final String BILL_TO_NAME = "a1.name"; 
	public static final String SITE_NAME = "a2.name"; 
	public static final String JOB_SITE = "concat(a2.address1, ', ', a2.city,', ', a2.state)";
	public static final String JOB_CONTACT = "concat(job_contact.last_name, ', ', job_contact.first_name)"; 
	public static final String JOB_CONTACT_PREFERRED_CONTACT = "job_contact.preferred_contact"; 
	public static final String JOB_CONTACT_METHOD = "case job_contact.preferred_contact \n" + 
							" 	when 'business_phone' then job_contact.business_phone \n" + 
							" 	when 'email' then job_contact.email \n" + 
							" 	when 'fax' then job_contact.fax \n" + 
							" 	when 'mobile_phone' then job_contact.mobile_phone \n" + 
							" end"; 
	public static final String SITE_CONTACT = "concat(site_contact.last_name,', ',site_contact.first_name)";
	public static final String SITE_CONTACT_PREFERRED_CONTACT = "site_contact.preferred_contact";
	public static final String SITE_CONTACT_METHOD = "case site_contact.preferred_contact \n" + 
							" 	when 'business_phone' then site_contact.business_phone \n" + 
							" 	when 'email' then site_contact.email \n" + 
							" 	when 'fax' then site_contact.fax \n" + 
							" 	when 'mobile_phone' then site_contact.mobile_phone \n" + 
							" end"; 
	public static final String CONTRACT_CONTACT = "concat(contract_contact.last_name,', ',contract_contact.first_name)";
	public static final String CONTRACT_CONTACT_PREFERRED_CONTACT = "contract_contact.preferred_contact"; 
	public static final String CONTRACT_CONTACT_METHOD = "case contract_contact.preferred_contact \n" + 
							" 	when 'business_phone' then contract_contact.business_phone \n" + 
							" 	when 'email' then contract_contact.email \n" + 
							" 	when 'fax' then contract_contact.fax \n" + 
							" 	when 'mobile_phone' then contract_contact.mobile_phone \n" + 
							" end"; 
	public static final String BILLING_CONTACT = "concat(billing_contact.last_name,', ',billing_contact.first_name)"; 	
	public static final String BILLING_CONTACT_PREFERRED_CONTACT = "billing_contact.preferred_contact"; 
	public static final String BILLING_CONTACT_METHOD = "case billing_contact.preferred_contact \n" + 
							" 	when 'business_phone' then billing_contact.business_phone \n" + 
							" 	when 'email' then billing_contact.email \n" + 
							" 	when 'fax' then billing_contact.fax \n" + 
							" 	when 'mobile_phone' then billing_contact.mobile_phone \n" + 
							" end";
	public static final String TAG_LIST = "tag_count.tag_list";
	
	
	
	
	private static final String sqlSelectClause = "SELECT " + 
			JOB_ID + ",  \n" + 
			QUOTE_ID + ", \n" + 
			JOB_NBR + ", \n" + 
			JOB_FREQUENCY + ", \n" + 
			JOB_STATUS + ", \n" + 
			PRICE_PER_CLEANING + ", \n" + 
			PO_NUMBER + " , \n" + 
			SERVICE_DESCRIPTION + ", \n" + 
			ACTIVATION_DATE + ", \n" + 
			START_DATE + ", \n" + 
			CANCEL_DATE + ", \n" + 
			CANCEL_REASON + ",\n" + 
			QUOTE_NBR + " as quote_nbr, \n" +
			PROPOSAL_DATE + ", \n" + 
			DIVISION_ID + ", \n" + 
			DIV + " as div, \n" +
			BILL_TO_NAME + " as bill_to_name, \n" + 
			SITE_NAME + " as job_site_name, \n" + 
			JOB_SITE + " as job_site, \n" +
			JOB_CONTACT + " as job_contact, \n"  + 
			JOB_CONTACT_PREFERRED_CONTACT + "  as job_contact_preferred_contact, \n" + 
			JOB_CONTACT_METHOD + " as job_contact_method,  \n" + 
			SITE_CONTACT + " as site_contact, \n" + 
			SITE_CONTACT_PREFERRED_CONTACT + "  as site_contact_preferred_contact, \n" + 
			SITE_CONTACT_METHOD + " as site_contact_method,  \n" + 
			CONTRACT_CONTACT + " as contract_contact, \n" + 
			CONTRACT_CONTACT_PREFERRED_CONTACT + " as contract_contact_preferred_contact, \n" + 
			CONTRACT_CONTACT_METHOD + " as contract_contact_method,  \n" + 
			BILLING_CONTACT + " as billing_contact,\n" +  
			BILLING_CONTACT_PREFERRED_CONTACT + " as billing_contact_preferred_contact, \n" + 
			BILLING_CONTACT_METHOD + " as billing_contact_method, \n " +
			TAG_LIST + "\n"
			;			

	
	private static final String sqlFromClause = 
			"FROM job \n" + 
			"JOIN quote ON quote.quote_id = job.quote_id \n" + 
			"    AND quote.division_id in ( select division_id from division_user where user_id=$USERFILTER$) \n" + 
			"JOIN division ON job.division_id = division.division_id \n" + 
			"JOIN address a1 ON quote.bill_to_address_id = a1.address_id \n" + 
			"JOIN address a2 ON quote.job_site_address_id = a2.address_id \n" + 
			" inner join contact as job_contact on job_contact.contact_id=job.job_contact_id \n" + 
			" inner join contact as site_contact on site_contact.contact_id=job.site_contact  \n" + 
			" inner join contact as contract_contact on contract_contact.contact_id=job.contract_contact_id \n" + 
			" inner join contact as billing_contact on billing_contact.contact_id=job.billing_contact_id \n"; 
			

	private static final String baseWhereClause = "\n  ";

	
	
	
	public JobLookupQuery(Integer userId, List<SessionDivision> divisionList) throws Exception {
		super(sqlSelectClause, makeSqlFromClause(userId), "");		
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public JobLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) throws Exception {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}


	private static String makeSqlFromClause(Integer userId) throws Exception {
		Connection conn = AppUtils.getDBCPConn();
		String tagClause = sqlFromClause + "\nleft outer join (" + JobUtils.makeTagSql(conn, "my_tags") +") as tag_count on tag_count.job_id=job.job_id\n";
		String sql = tagClause.replaceAll("\\$USERFILTER\\$", String.valueOf(userId));
		conn.close();
		return sql;
	}
	

	
	

	

	
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {				
				orderBy = " order by " + JOB_ID + " desc";
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
	 */
	protected String makeWhereClause(String queryTerm)  {
		String whereClause = super.getBaseWhereClause();
		logger.log(Level.DEBUG, whereClause);
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		logger.log(Level.DEBUG, joiner);
		if (! StringUtils.isBlank(queryTerm)) { 
			whereClause =  whereClause + joiner + " (\n" +
					" job.job_id like '%" + queryTerm + "%' " +
							"\n OR CONCAT(quote_number,revision) like '%" + queryTerm + "%'" +
							"\n OR service_description like '%" + queryTerm + "%'" +
							"\n OR po_number like '%" + queryTerm + "%'" +
							"\n OR division.division_nbr like '%" + queryTerm + "%'" +
							"\n OR division.division_code like '%" + queryTerm + "%'" +
							"\n OR a2.name like '%" + queryTerm + "%'" +
							"\n OR a2.address1 like '%" + queryTerm + "%'" +
							"\n OR a2.address2 like '%" + queryTerm + "%'" +
							"\n OR a2.city like '%" + queryTerm + "%'" +
							"\n OR a2.state like '%" + queryTerm + "%'" +
							"\n OR a1.name like '%" + queryTerm + "%'" +
							"\n OR concat(job_contact.last_name,', ',job_contact.first_name) like '%" + queryTerm + "%'" +
							"\n OR job_contact.mobile_phone like '%" + queryTerm + "%'" +
							"\n OR job_contact.business_phone like '%" + queryTerm + "%'" +
							"\n OR job_contact.fax like '%" + queryTerm + "%'" +
							"\n OR job_contact.email like '%" + queryTerm + "%'" +
							"\n OR concat(site_contact.last_name,', ',site_contact.first_name) like '%" + queryTerm + "%'" +
							"\n OR site_contact.mobile_phone like '%" + queryTerm + "%'" +
							"\n OR site_contact.business_phone like '%" + queryTerm + "%'" +
							"\n OR site_contact.fax like '%" + queryTerm + "%'" +
							"\n OR site_contact.email like '%" + queryTerm + "%'" +
							"\n OR concat(contract_contact.last_name,', ',contract_contact.first_name) like '%" + queryTerm + "%'" +
							"\n OR contract_contact.mobile_phone like '%" + queryTerm + "%'" +
							"\n OR contract_contact.business_phone like '%" + queryTerm + "%'" +
							"\n OR contract_contact.fax like '%" + queryTerm + "%'" +
							"\n OR contract_contact.email like '%" + queryTerm + "%'" +
							"\n OR concat(billing_contact.last_name,', ',billing_contact.first_name) like '%" + queryTerm + "%'" +
							"\n OR billing_contact.mobile_phone like '%" + queryTerm + "%'" +
							"\n OR billing_contact.business_phone like '%" + queryTerm + "%'" +
							"\n OR billing_contact.fax like '%" + queryTerm + "%'" +
							"\n OR billing_contact.email like '%" + queryTerm + "%'" +
							"\n OR quote.proposal_date like '%" + queryTerm + "%'" +
							"\n OR job.activation_date like '%" + queryTerm + "%'" +
							"\n OR job.cancel_date like '%" + queryTerm + "%'" +
							"\n OR job.cancel_reason like '%" + queryTerm + "%'" +
						")" ;
		}
		logger.log(Level.DEBUG, whereClause);
		return whereClause;
	}
	
	
}
