package com.ansi.scilla.web.payroll.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.payroll.validator.employee.EmployeeValidationBaseSql;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class ExceptionReportQuery extends LookupQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private boolean errorsOnly;
			
	private static final String sqlWhereClause =
			"WHERE company.group_type = 'COMPANY' and company.company_code is not NULL and company.group_id=? \n";
//			"ORDER BY company_code, group_name";
			
	
	/**
	 * Note to the next person who changes this:
	 * If you need to change the .2 for excess expenses, make sure you change it in the base sql (ansi.common.payroll.validateor.employee)
	 */
	private static final String errorsOnlyClause = 
			sqlWhereClause +
			"and (\n"
			+ "	   isnull(payroll_worksheet.expenses_submitted,0) > isnull(payroll_worksheet.expenses_allowed,0) --excess_expense_claim,\n"
			+ "	or isnull(payroll_worksheet.expenses_submitted,0) > (.2 * isnull(payroll_worksheet.gross_pay,0)) --excess_expense_pct,\n"
			+ "	or isnull(ytd.ytd_expenses_submitted,0) > isnull(ytd.ytd_expenses_allowed,0) --ytd_excess_expense_claim,\n"
			+ "	or isnull(ytd.ytd_expenses_submitted,0) > (.2 * isnull(ytd.ytd_gross_pay,0)) --ytd_excess_expense_pct,\n"
			+ "	or week_ending > payroll_employee.employee_termination_date --paid_late,\n"
			+ "	or (payroll_worksheet.gross_pay - payroll_worksheet.expenses) < (division.minimum_hourly_pay * (payroll_worksheet.regular_hours + payroll_worksheet.vacation_hours + payroll_worksheet.holiday_hours)) --under_govt_min_pay,\n"
			+ "	or (payroll_employee.union_member = 1) and (payroll_worksheet.gross_pay - payroll_worksheet.expenses) < (isnull(payroll_employee.union_rate,0) * (payroll_worksheet.regular_hours + payroll_worksheet.vacation_hours + payroll_worksheet.holiday_hours)) --under_union_min_pay,\n"
			+ "	or (ytd.ytd_gross_pay - ytd.ytd_expenses) < (division.minimum_hourly_pay * (ytd.ytd_regular_hours + ytd.ytd_vacation_hours + ytd.ytd_holiday_hours)) --ytd_under_govt_min_pay,\n"
			+ "	or (payroll_employee.union_member = 1) and (ytd.ytd_gross_pay - ytd.ytd_expenses) < (isnull(payroll_employee.union_rate,0) * (ytd.ytd_regular_hours + ytd.ytd_vacation_hours + ytd.ytd_holiday_hours)) --ytd_under_union_min_pay,\n"
			+ "	or payroll_worksheet.division_id != employee_division.division_id --foreign_division,\n"
			+ "	or company.group_id != employee_company.group_id --foreign_company\n"
			+ " )";

	public ExceptionReportQuery(Integer userId, List<SessionDivision> divisionList, Integer groupId, Boolean errorsOnly) {
		super(EmployeeValidationBaseSql.sqlSelectClause, EmployeeValidationBaseSql.sqlFromClause, makeBaseWhereClause(errorsOnly));
		this.logger = LogManager.getLogger(ExceptionReportQuery.class);
		this.errorsOnly = errorsOnly;
		this.userId = userId;
		super.setBaseFilterValue(Arrays.asList( new Object[] {groupId}));
	}


	

	private static String makeBaseWhereClause(Boolean errorsOnly) {
		return errorsOnly.booleanValue() ? errorsOnlyClause : sqlWhereClause;
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

		
		String myBaseWhereClause = makeBaseWhereClause(this.errorsOnly);
		
		if ( StringUtils.isBlank(queryTerm) ) {
			whereClause = myBaseWhereClause;
		} else {
			List<String> searchStrings = new ArrayList<String>();
			for ( String field : searchableFields ) {
				searchStrings.add("lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			String filterClause = "(" + StringUtils.join(searchStrings, " \nOR ") + ")";
			whereClause = StringUtils.isBlank(myBaseWhereClause)?  "where " + filterClause : myBaseWhereClause + " and " + filterClause;
		}
		
		return whereClause;
	}


	
	
	
}
