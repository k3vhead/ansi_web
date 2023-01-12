package com.ansi.scilla.web.payroll.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class AliasLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	
	
	private static final String sqlSelectClause = "select employee_code, employee_name ";
	private static final String sqlFromClause = "from employee_alias ";
	private static final String sqlWhereClause = "where employee_code=?";
			
	


	public AliasLookupQuery(Integer userId, List<SessionDivision> divisionList, Integer employeeCode) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(AliasLookupQuery.class);
		this.userId = userId;	
		super.setBaseFilterValue(Arrays.asList( new Object[] {employeeCode}));
	}

	

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by employee_alias.employee_name";
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
		
		

		
		if ( ! StringUtils.isBlank(queryTerm) ) {
			StringBuffer searchFieldBuffer = new StringBuffer();
			searchFieldBuffer.append("\n lower(employee_alias.employee_name) like '%" + queryTerm.toLowerCase() + "%'");
			whereClause = StringUtils.isBlank(sqlWhereClause) ? searchFieldBuffer.toString() : sqlWhereClause + " and (" + searchFieldBuffer.toString() + ")";
		}
		
		return whereClause;
	}


	
	

}
