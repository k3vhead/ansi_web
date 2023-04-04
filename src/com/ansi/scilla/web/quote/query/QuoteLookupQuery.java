package com.ansi.scilla.web.quote.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class QuoteLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String QUOTE_ID = "quote.quote_id";	
	public static final String QUOTE_NUMBER = "quote.quote_number"; 
	public static final String REVISION = "quote.revision"; 
	public static final String CONTACT_ID = "quote.signed_by_contact_id"; 
	public static final String JOB_SITE_ADDRESS_ID = "quote.job_site_address_id"; 
	public static final String BILL_TO_ADDRESS_ID = "quote.bill_to_address_id";
	public static final String PROPOSAL_DATE = "quote.proposal_date"; 
	public static final String MANAGER_ID = "quote.manager_id"; 
	public static final String DIVISION_ID = "division.division_id"; 
	public static final String DIV = "CONCAT(division.division_nbr, '-', division.division_code)"; 
	public static final String QUOTE_CODE = "CONCAT(quote.quote_number,revision)"; 
	public static final String MANAGER_NAME = "CONCAT(ansi_user.first_name,' ',ansi_user.last_name)"; 
	public static final String BILL_TO_NAME = "a1.name"; 
	public static final String JOB_SITE_NAME = "a2.name";
	public static final String JOB_SITE_ADDRESS = "a2.address1";
	public static final String QUOTE_JOB_COUNT = "jobs.job_count";
	public static final String QUOTE_PPC_SUM = "jobs.job_ppc_sum";
	public static final String PROPOSED_JOB_COUNT = "ISNULL(proposed_job.job_count, 0)";
	public static final String ACTIVE_JOB_COUNT = "ISNULL(active_job.job_count, 0)";
	public static final String CANCELED_JOB_COUNT = "ISNULL(canceled_job.job_count, 0)";
	public static final String NEW_JOB_COUNT = "ISNULL(new_job.job_count, 0)";
	public static final String DOCUMENT_COUNT = "ISNULL(documents.document_count,0)";
	public static final String PAC_STATUS = "CASE \n"
			+ " 		WHEN ISNULL(active_job.job_count, 0) > 0 THEN 'A'\n"
			+ " 		WHEN ISNULL(canceled_job.job_count, 0) > 0 THEN 'C'\n"
			+ " 		WHEN ISNULL(proposed_job.job_count, 0) > 0 THEN 'P'\n"
			+ " 		ELSE 'N'\n"
			+ " 	END";

	public static final String baseSelectClause = "SELECT quote.quote_id, quote.quote_number, quote.revision, quote.signed_by_contact_id, quote.job_site_address_id , quote.bill_to_address_id ,\n"
			+ "		quote.proposal_date , quote.manager_id , \n"
			+ "	    division.division_id, "
			+ "     CONCAT(division.division_nbr, '-', division.division_code) as div , \n"
			+ "	    CONCAT(quote.quote_number,revision) as quote_code, \n"
			+ "	    CONCAT(ansi_user.first_name,' ',ansi_user.last_name) as manager_name, \n"
			+ " 	a1.name as bill_to_name, \n"
			+ " 	a2.name as job_site_name, a2.address1 as job_site_address, \n"
			+ " 	jobs.job_count as quote_job_count, "
			+ "     ISNULL(jobs.job_ppc_sum, 0) as quote_ppc_sum,\n"
			+ " 	ISNULL(proposed_job.job_count, 0) as proposed_job_count,\n"
			+ " 	ISNULL(active_job.job_count, 0) as active_job_count,\n"
			+ " 	ISNULL(canceled_job.job_count, 0) as canceled_job_count,\n"
			+ " 	ISNULL(new_job.job_count, 0) as new_job_count,\n"
			+ " 	ISNULL(documents.document_count,0) as document_count, \n"
			+ " 	CASE \n"
			+ " 		WHEN ISNULL(active_job.job_count, 0) > 0 THEN 'A'\n"
			+ " 		WHEN ISNULL(canceled_job.job_count, 0) > 0 THEN 'C'\n"
			+ " 		WHEN ISNULL(proposed_job.job_count, 0) > 0 THEN 'P'\n"
			+ " 		ELSE 'N'\n"
			+ " 	END as pac_status\n";
	
	public static final String baseFromClause = "FROM quote \n"
			+ " LEFT OUTER JOIN (select document.xref_type, document.xref_id, count(document.document_id) as document_count from document group by document.xref_type, document.xref_id) documents on documents.xref_id=quote.quote_id and documents.xref_type='SIGNED_CONTRACT' \n"
			+ " LEFT OUTER JOIN (select quote_id, count(*) as job_count from job where job_status='P' group by quote_id) as proposed_job on proposed_job.quote_id=quote.quote_id\n"
			+ " LEFT OUTER JOIN (select quote_id, count(*) as job_count from job where job_status='A' group by quote_id) as active_job on active_job.quote_id=quote.quote_id\n"
			+ " LEFT OUTER JOIN (select quote_id, count(*) as job_count from job where job_status='C' group by quote_id) as canceled_job on canceled_job.quote_id=quote.quote_id\n"
			+ " LEFT OUTER JOIN (select quote_id, count(*) as job_count from job where job_status='N' group by quote_id) as new_job on new_job.quote_id=quote.quote_id\n"
			+ " JOIN division ON division.division_id = quote.division_id and quote.division_id in (select division_id from division_user where user_id=5)\n"
			+ " JOIN address a1 ON quote.bill_to_address_id = a1.address_id \n"
			+ " JOIN address a2 ON quote.job_site_address_id = a2.address_id \n"
			+ " JOIN ansi_user ON ansi_user.user_id = quote.manager_ID \n"
			+ " inner join ( \n"
			+ "	 select quote.quote_id, count(job.job_id) as job_count, sum(job.price_per_cleaning) as job_ppc_sum \n"
			+ "	 from quote \n"
			+ "	 left outer join job on job.quote_id=quote.quote_id \n"
			+ "	 group by quote.quote_id \n"
			+ " ) jobs on quote.quote_id=jobs.quote_id\n";
	
	public static final String baseWhereClause = "";
	
	public QuoteLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(baseSelectClause, baseFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(QuoteLookupQuery.class);
		this.userId = userId;			
	}
	
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by " + QUOTE_ID + " desc";
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
		String whereClause = baseWhereClause;
				
		String[] searchableFields = new String[] {
				PROPOSAL_DATE,
				DIV,
				QUOTE_CODE,
				MANAGER_NAME,
				BILL_TO_NAME,
				JOB_SITE_NAME,
				JOB_SITE_ADDRESS,
		};

		
		
		if ( StringUtils.isBlank(queryTerm) ) {
			whereClause = baseWhereClause;
		} else {
			List<String> searchStrings = new ArrayList<String>();
			for ( String field : searchableFields ) {
				searchStrings.add("lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			String filterClause = "(" + StringUtils.join(searchStrings, " \nOR ") + ")";
			whereClause = StringUtils.isBlank(baseWhereClause)?  "where " + filterClause : "where " + baseWhereClause + " and " + filterClause;
		}
		
		return whereClause;
	}

}
