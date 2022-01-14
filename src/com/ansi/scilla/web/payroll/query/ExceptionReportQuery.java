package com.ansi.scilla.web.payroll.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.payroll.validator.EmployeeValidationBaseSql;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class ExceptionReportQuery extends LookupQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
			
	private static final String sqlWhereClause =
			"WHERE company.group_type = 'COMPANY' and company.company_code is not NULL and company.group_id=? \n";
//			"ORDER BY company_code, group_name";
			
	

	public ExceptionReportQuery(Integer userId, List<SessionDivision> divisionList, Integer groupId) {
		super(EmployeeValidationBaseSql.sqlSelectClause, EmployeeValidationBaseSql.sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(ExceptionReportQuery.class);
		this.userId = userId;	
		super.setBaseFilterValue(Arrays.asList( new Object[] {groupId}));
	}


	

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by CONCAT(division.division_nbr, '-', division.division_code), payroll_employee.employee_first_name, payroll_employee.employee_last_name";
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
		
		
		String[] searchableFields = new String[] {
				"division_group.name" ,
				"division.division_nbr" ,
				"division.description" ,
				"payroll_employee.employee_code" ,
				"payroll_employee.employee_first_name" ,
				"payroll_employee.employee_last_name" ,
				"payroll_employee_mi" ,
		};

		
		
		if ( StringUtils.isBlank(queryTerm) ) {
			whereClause = sqlWhereClause;
		} else {
			List<String> searchStrings = new ArrayList<String>();
			for ( String field : searchableFields ) {
				searchStrings.add("lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			String filterClause = "(" + StringUtils.join(searchStrings, " \nOR ") + ")";
			whereClause = StringUtils.isBlank(sqlWhereClause)?  "where " + filterClause : sqlWhereClause + " and " + filterClause;
		}
		
		return whereClause;
	}


	
	
	
}
