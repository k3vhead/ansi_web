package com.ansi.scilla.web.payroll.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class TaxProfileLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	private static final String sqlSelectClause = "SELECT \n"
			+ "	payroll_tax_profile.profile_id, \n"
			+ "	payroll_tax_profile.profile_desc, \n"
			+ "	payroll_tax_profile.regular_hours, \n"
			+ "	payroll_tax_profile.regular_pay, \n"
			+ "	payroll_tax_profile.ot_hours, \n"
			+ "	payroll_tax_profile.ot_pay, \n"
			+ "	(select count(*) from locale where locale.payroll_tax_profile = payroll_tax_profile.profile_id) as locale_count\n";			
	private static final String sqlFromClause = "FROM payroll_tax_profile\n"; 
	private static final String sqlWhereClause = "";
	
	
	public TaxProfileLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(TimesheetLookupQuery.class);
		this.userId = userId;
	}
	
	
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by profile_id";
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
		String whereClause = sqlWhereClause;

		String [] searchableFields = new String[] { "profile_id","profile_desc" };
		
		if ( StringUtils.isBlank(queryTerm) ) {
			whereClause = sqlWhereClause;
		} else {
			List<String> searchStrings = new ArrayList<String>();
			for ( String field : searchableFields ) {
				searchStrings.add("lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			String filterClause = "(" + StringUtils.join(searchStrings, " \nOR ") + ")";
			whereClause = StringUtils.isBlank(sqlWhereClause)?  "where " + filterClause : "where " + sqlWhereClause + " and " + filterClause;
		}
		
		return whereClause;
	}

}
