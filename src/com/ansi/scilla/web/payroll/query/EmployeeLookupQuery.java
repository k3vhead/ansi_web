package com.ansi.scilla.web.payroll.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class EmployeeLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	
	
	private static final String sqlSelectClause = "select " +
			"   payroll_employee.employee_code,\n" + 
			"	payroll_employee.company_code,\n" + 
			"	payroll_employee.division,\n" + 
			"	payroll_employee.division_id,\n" + 
			"	payroll_employee.employee_first_name,\n" + 
			"	payroll_employee.employee_last_name,\n" + 
			"	payroll_employee.employee_mi,\n" + 
			"	payroll_employee.dept_description,\n" + 
			"	payroll_employee.employee_status,\n" + 
			"	payroll_employee.employee_termination_date,\n" + 
			"	payroll_employee.notes\n";
	private static final String sqlFromClause =
			"from payroll_employee ";
	private static final String sqlWhereClause = "";
			
	


	public EmployeeLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(EmployeeLookupQuery.class);
		this.userId = userId;	
//		super.setBaseFilterValue(Arrays.asList( new Object[] {divisionId, workYear}));
	}

	

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by payroll_employee.last_name, payroll_employee.first_name, payroll_employee.mi";
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
		
		/**
		 * Name matches:
		 * first last
		 * first mi last
		 * first mi. last
		 * last, first
		 * last, first mi
		 */
		String[] searchableFields = new String[] {
				"payroll_employee.employee_code",
				"payroll_employee.company_code",
				"payroll_employee.division",
				"payroll_employee.dept_description",
				"payroll_employee.employee_status",
				"payroll_employee.employee_termination_date",
				"payroll_employee.notes",
				"concat(payroll_employee.employee_first_name, ' ', payroll_employee.employee_last_name)",
				"concat(payroll_employee.employee_first_name, ' ', payroll_employee.employee_mi, ' ', payroll_employee.employee_last_name)",
				"concat(payroll_employee.employee_first_name, ' ', payroll_employee.employee_mi, '. ', payroll_employee.employee_last_name)",
				"concat(payroll_employee.employee_last_name, ', ', payroll_employee.employee_last_name)",
				"concat(payroll_employee.employee_last_name, ', ', payroll_employee.employee_last_name, ' ', payroll_employee.employee_mi)",
		};

		
		
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
