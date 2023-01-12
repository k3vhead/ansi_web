package com.ansi.scilla.web.batch.query;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;

public class BatchLogQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	public static final String ID = "batch_log_id";
	public static final String TYPE = "batch_type";
	public static final String TYPE_DETAIL = "batch_type_detail";
	public static final String PARAMETERS = "parameters";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	public static final String STATUS = "status";
	
	protected static final String[] FIELD_LIST = new String[] {ID, TYPE, TYPE_DETAIL, PARAMETERS, START_TIME, END_TIME, STATUS};
	
	protected static final String sqlSelectClause = "select " + StringUtils.join(Arrays.asList(FIELD_LIST), ",");
	protected static final String sqlFromClause = " from batch_log";
	protected static final String sqlWhereClause = "";
				

	public BatchLogQuery() {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
	}


	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by job_site.name, ticket_id, claim_week";
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
		String whereClause = "";
		
		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			StringBuffer searchFieldBuffer = new StringBuffer();
			for ( String field : FIELD_LIST ) {
				searchFieldBuffer.append("\n OR lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			
			whereClause =   "where (\n" +
					searchFieldBuffer.toString() + "\n" +
					")" ;
		}
		
		return whereClause;
	}


	
	

}
