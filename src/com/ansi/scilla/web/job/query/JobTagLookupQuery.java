package com.ansi.scilla.web.job.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.jobticket.JobTagStatus;
import com.ansi.scilla.common.jobticket.JobTagType;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class JobTagLookupQuery extends LookupQuery {

	public static final String TAG_ID = "job_tag.tag_id";
	public static final String TYPE_DISPLAY = "type_display";
	public static final String ABBREV = "job_tag.abbrev";
	public static final String LONG_CODE = "job_tag.long_code";
	public static final String DESCRIPTION = "job_tag.description";
	public static final String TAG_STATUS = "tag_status";
	public static final String JOB_COUNT = "job_count";

	
	private static final long serialVersionUID = 1L;

	
//	private static final String sqlSelectClause = "select job_tag.tag_id, job_tag.tag_type, job_tag.name, job_tag.status" ;			
//
	private static final String sqlFromClause = 
			"\nfrom job_tag\n" + 
			"left outer join (select tag_id, count(*) as job_count from job_tag_xref group by tag_id) xref on xref.tag_id=job_tag.tag_id\n"; 
			

	private static final String baseWhereClause = "\n  ";

	
	
	
	public JobTagLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(makeSqlSelect(), sqlFromClause, "");		
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public JobTagLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}


	/**
	 * What we're trying to get to:
	 * select 
			job_tag.tag_id, 
			job_tag.tag_type, 
			CASE
				when job_tag.tag_type='EQUIPMENT' then 'Equipment'
				when job_tag.tag_type='SERVICE' then 'Service'
				else job_tag.tag_type
			END as type_display,
			job_tag.name, 
			job_tag.description,
			job_tag.status,
			CASE 
				when status='ACTIVE' then 'Active'
				when status='INACTIVE' then 'Inactive'
				else status
			END as tag_status,	
			isnull(xref.job_count,0) as job_count
	 *
	 * @return
	 */
	private static String makeSqlSelect() {
		List<String> sqlSelect = new ArrayList<String>();
		sqlSelect.add("select");
		sqlSelect.add("\tjob_tag.tag_id,"); 
		sqlSelect.add("\tjob_tag.tag_type,");
		sqlSelect.add(makeTypeSql() + " as type_display,");
		sqlSelect.add("\tjob_tag.long_code,");
		sqlSelect.add("\tjob_tag.abbrev,");
		sqlSelect.add("\tjob_tag.description,");
		sqlSelect.add("\tjob_tag.status,");
		sqlSelect.add(makeStatusSql() + " as tag_status,");		
		sqlSelect.add("\tisnull(xref.job_count,0) as job_count");

		return StringUtils.join(sqlSelect, "\n");
	}

	
	


	public static String makeTypeSql() {
		List<String> sqlSelect = new ArrayList<String>();
		sqlSelect.add("\tCASE");
		for ( JobTagType type : JobTagType.values() ) {
			sqlSelect.add("\t\twhen job_tag.tag_type='" + type.name() + "' then '" + type.display() + "'");
		}		
		sqlSelect.add("\t\telse job_tag.tag_type");
		sqlSelect.add("\tEND");
		return StringUtils.join(sqlSelect, "\n");
	}

	
	public static String makeStatusSql() {
		List<String> sqlSelect = new ArrayList<String>();
		sqlSelect.add("\tCASE");
		for ( JobTagStatus status : JobTagStatus.values() ) {
			sqlSelect.add("\t\twhen job_tag.status='" + status.name() + "' then '" + status.display() + "'");
		}
		sqlSelect.add("\t\telse job_tag.status");
		sqlSelect.add("\tEND");
		return StringUtils.join(sqlSelect, "\n");
	}

	
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {				
				orderBy = " order by " + TYPE_DISPLAY + " asc," + LONG_CODE + " asc";
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
			whereClause =  whereClause + joiner + " (\n"
						+ " lower(job_tag.long_code) like '%" + queryTerm.toLowerCase() + "%'" +	
						" or lower(job_tag.description) like '%" + queryTerm.toLowerCase() + "%'" +
						" or lower(job_tag.abbrev) like '%" + queryTerm.toLowerCase() + "%'" +
						")" ;
		}
		logger.log(Level.DEBUG, whereClause);
		return whereClause;
	}
	
	
}
