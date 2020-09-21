package com.ansi.scilla.web.job.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

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
	public static final String QUOTE_NUMBER = "quote.quote_number"; 
	public static final String REVISION = "quote.revision";
	public static final String PROPOSAL_DATE = "quote.proposal_date";
	public static final String DIVISION_ID = "division.division_id"; 
	public static final String DIVISION_NBR = "division.division_nbr"; 
	public static final String DIVISION_CODE = "division.division_code";
	public static final String BILL_TO_NAME = "a1.name as bill_to_name"; 
	public static final String SITE_NAME = "a2.name as job_site_name"; 
	public static final String JOB_SITE_ADDRESS = "a2.address1 as job_site_address"; 
	public static final String JOB_SITE_CITY = "a2.city as job_site_city";
	public static final String JOB_SITE_STATE = "a2.state as job_site_state"; 
	public static final String JOB_CONTACT_LAST_NAME = "job_contact.last_name as job_contact_last_name"; 
	public static final String JOB_CONTACT_FIRST_NAME = "job_contact.first_name as job_contact_first_name"; 
	public static final String JOB_CONTACT_PREFERRED_CONTACT = "job_contact.preferred_contact as job_contact_preferred_contact"; 
	public static final String JOB_CONTACT_METHOD = "case job_contact.preferred_contact \n" + 
							" 	when 'business_phone' then job_contact.business_phone \n" + 
							" 	when 'email' then job_contact.email \n" + 
							" 	when 'fax' then job_contact.fax \n" + 
							" 	when 'mobile_phone' then job_contact.mobile_phone \n" + 
							" end as job_contact_method"; 
	public static final String SITE_CONACT_LAST_NAME = "site_contact.last_name as site_contact_last_name";
	public static final String SITE_CONTACT_FIRST_NAME = "site_contact.first_name as site_contact_first_name"; 
	public static final String SITE_CONTACT_PREFERRED_CONTACT = "site_contact.preferred_contact as site_contact_preferred_contact";
	public static final String SITE_CONTACT_METHOD = "case site_contact.preferred_contact \n" + 
	" 	when 'business_phone' then site_contact.business_phone \n" + 
	" 	when 'email' then site_contact.email \n" + 
	" 	when 'fax' then site_contact.fax \n" + 
	" 	when 'mobile_phone' then site_contact.mobile_phone \n" + 
	" end as site_contact_method"; 
	public static final String CONTRACT_CONTACT_LAST_NAME = "contract_contact.last_name as contract_contact_last_name";
	public static final String CONTRACT_CONTACT_FIRST_NAME = "contract_contact.first_name as contract_contact_first_name"; 
	public static final String CONTRACT_CONTACT_PREFERRED_CONTACT = "contract_contact.preferred_contact as contract_contact_preferred_contact"; 
	public static final String CONTRACT_CONTACT_METHOD = "case contract_contact.preferred_contact \n" + 
	" 	when 'business_phone' then contract_contact.business_phone \n" + 
	" 	when 'email' then contract_contact.email \n" + 
	" 	when 'fax' then contract_contact.fax \n" + 
	" 	when 'mobile_phone' then contract_contact.mobile_phone \n" + 
	" end as contract_contact_method"; 
	public static final String BILLING_CONTACT_LAST_NAME = "billing_contact.last_name as billing_contact_last_name"; 
	public static final String BILLING_CONTACT_FIRST_NAME = "billing_contact.first_name as billing_contact_first_name"; 
	public static final String BILLING_CONTACT_PREFERRED_CONTACT = "billing_contact.preferred_contact as billing_contact_preferred_contact"; 
	public static final String BILLING_CONTACT_METHOD = "case billing_contact.preferred_contact \n" + 
	" 	when 'business_phone' then billing_contact.business_phone \n" + 
	" 	when 'email' then billing_contact.email \n" + 
	" 	when 'fax' then billing_contact.fax \n" + 
	" 	when 'mobile_phone' then billing_contact.mobile_phone \n" + 
	" end as billing_contact_method";
	
	
	
	
	private static final String sqlSelectClause = "SELECT " + 
			"       job.job_id,  \n" + 
			"		job.quote_id, \n" + 
			"		job.job_nbr, \n" + 
			"		job.job_frequency , \n" + 
			"		job.job_status, \n" + 
			"		job.price_per_cleaning , \n" + 
//			"		job.division_id ,\n" + 
//			"		job.direct_labor_pct , \n" + 
//			"		job.budget , \n" + 
//			"		job.floors , \n" + 
//			"		job.invoice_terms , \n" + 
//			"		job.invoice_style , \n" + 
//			"		job.invoice_batch , \n" + 
//			"		job.invoice_grouping ,\n" + 
//			"		job.contract_contact_id , \n" + 
//			"		job.tax_exempt , \n" + 
//			"		job.tax_exempt_reason , \n" + 
			"		job.po_number , \n" + 
//			"		job.expiration_date , \n" + 
//			"		job.expiration_reason ,\n" + 
//			"		job.building_type , \n" + 
//			"		job.our_vendor_nbr , \n" + 
			"		job.service_description , \n" + 
//			"		job.equipment , \n" + 
//			"		job.washer_notes , \n" + 
//			"		job.billing_contact_id , \n" + 
//			"		job.billing_notes , \n" + 
//			"		job.om_notes , \n" + 
			"		job.activation_date , \n" + 
			"		job.start_date , \n" + 
			"		job.cancel_date , \n" + 
			"		job.cancel_reason ,\n" + 
//			"		job.site_contact , \n" + 
//			"		job.job_contact_id , \n" + 
//			"		job.last_review_date , \n" + 
//			"		job.last_price_change , \n" + 
//			"		job.job_type_id , \n" + 
//			"		job.request_special_scheduling ,\n" + 
//			"		job.repeat_schedule_annually , \n" + 
//			"		job.payment_terms ,\n" + 
//			"	quote.quote_id, \n" + 
			"		quote.quote_number, \n" + 
			"		quote.revision , \n" + 
//			"		quote.signed_by_contact_id , \n" + 
//			"		quote.job_site_address_id , \n" + 
//			"		quote.bill_to_address_id ,\n" + 
//			"--		quote.template_id , \n" + 
//			"		quote.quote_group_id ,\n" + 
			"		quote.proposal_date , \n" + 
//			"--		quote.copied_from_quote_id , \n" + 
//			"		quote.division_id , \n" + 
//			"--		quote.manager_id ,\n" + 
//			"--		quote.account_type , \n" + 
//			"--		quote.lead_type ,	\n" + 
			"	division.division_id, \n" + 
			"		division.division_nbr, \n" + 
			"		division.division_code, \n" + 
//			"		division.description, \n" + 
//			"--		division.parent_id, \n" + 
//			"--		division.default_direct_labor_pct ,\n" + 
//			"--		division.division_status , \n" + 
//			"--		division.group_id , \n" + 
//			"--		division.max_reg_hrs_per_day , \n" + 
//			"--		division.max_reg_hrs_per_week , \n" + 
//			"--		division.overtime_rate , \n" + 
//			"--		division.weekend_is_ot , \n" + 
//			"--		division.hourly_rate_is_fixed , \n" + 
//			"--		division.minimum_hourly_pay , \n" + 
//			"--		division.division_info , \n" + 
//			"--		division.division_info_fields , \n" + 
//			"--		division.act_close_date ,\n" + 
			"a1.name as bill_to_name, \n" + 
			"a2.name as job_site_name, \n" + 
			"a2.address1 as job_site_address, \n" + 
			"a2.city as job_site_city, \n" + 
			"a2.state as job_site_state, \n" + 
			" job_contact.last_name as job_contact_last_name,  \n" + 
			" job_contact.first_name as job_contact_first_name,  \n" + 
			" job_contact.preferred_contact as job_contact_preferred_contact, \n" + 
			" case job_contact.preferred_contact \n" + 
			" 	when 'business_phone' then job_contact.business_phone \n" + 
			" 	when 'email' then job_contact.email \n" + 
			" 	when 'fax' then job_contact.fax \n" + 
			" 	when 'mobile_phone' then job_contact.mobile_phone \n" + 
			" end as job_contact_method,  \n" + 
			" site_contact.last_name as site_contact_last_name,  \n" + 
			" site_contact.first_name as site_contact_first_name,  \n" + 
			" site_contact.preferred_contact as site_contact_preferred_contact, \n" + 
			" case site_contact.preferred_contact \n" + 
			" 	when 'business_phone' then site_contact.business_phone \n" + 
			" 	when 'email' then site_contact.email \n" + 
			" 	when 'fax' then site_contact.fax \n" + 
			" 	when 'mobile_phone' then site_contact.mobile_phone \n" + 
			" end as site_contact_method,  \n" + 
			" contract_contact.last_name as contract_contact_last_name,  \n" + 
			" contract_contact.first_name as contract_contact_first_name,  \n" + 
			" contract_contact.preferred_contact as contract_contact_preferred_contact, \n" + 
			" case contract_contact.preferred_contact \n" + 
			" 	when 'business_phone' then contract_contact.business_phone \n" + 
			" 	when 'email' then contract_contact.email \n" + 
			" 	when 'fax' then contract_contact.fax \n" + 
			" 	when 'mobile_phone' then contract_contact.mobile_phone \n" + 
			" end as contract_contact_method,  \n" + 
			" billing_contact.last_name as billing_contact_last_name,  \n" + 
			" billing_contact.first_name as billing_contact_first_name,  \n" + 
			" billing_contact.preferred_contact as billing_contact_preferred_contact, \n" + 
			" case billing_contact.preferred_contact \n" + 
			" 	when 'business_phone' then billing_contact.business_phone \n" + 
			" 	when 'email' then billing_contact.email \n" + 
			" 	when 'fax' then billing_contact.fax \n" + 
			" 	when 'mobile_phone' then billing_contact.mobile_phone \n" + 
			" end as billing_contact_method " 
			;			

	
	private static final String sqlFromClause = 
			"FROM job \n" + 
			"JOIN quote ON quote.quote_id = job.quote_id \n" + 
			"    AND quote.division_id in ( select division_id from division_user where user_id=5) \n" + 
			"JOIN division ON job.division_id = division.division_id \n" + 
			"JOIN address a1 ON quote.bill_to_address_id = a1.address_id \n" + 
			"JOIN address a2 ON quote.job_site_address_id = a2.address_id \n" + 
			" inner join contact as job_contact on job_contact.contact_id=job.job_contact_id \n" + 
			" inner join contact as site_contact on site_contact.contact_id=job.site_contact  \n" + 
			" inner join contact as contract_contact on contract_contact.contact_id=job.contract_contact_id \n" + 
			" inner join contact as billing_contact on billing_contact.contact_id=job.billing_contact_id \n"; 
			

	private static final String baseWhereClause = "\n  ";

	
	
	
	public JobLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, "");		
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public JobLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
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
	 * @throws Exception
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
