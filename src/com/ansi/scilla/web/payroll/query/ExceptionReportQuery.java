package com.ansi.scilla.web.payroll.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class ExceptionReportQuery extends LookupQuery {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String sqlSelectClause = "select 	division_group.name as group_name, \n"
			+ "	division_group.company_code,\n"
			+ "	division.division_id, \n"
			+ "	division.description, \n"
			+ "	CONCAT(division.division_nbr, '-', division.division_code) as div,\n"
			+ "	division.minimum_hourly_pay,\n"
			+ "	payroll_employee.employee_code, \n"
			+ "	payroll_employee.employee_first_name, \n"
			+ "	payroll_employee.employee_last_name, \n"
			+ "	payroll_employee.employee_mi, \n"
			+ "	payroll_employee.employee_status, \n"
			+ "	payroll_employee.employee_termination_date,\n"
			+ "	payroll_worksheet.week_ending, \n"
			+ "	payroll_worksheet.state, \n"
			+ "	payroll_worksheet.city, \n"
			+ "	payroll_worksheet.employee_name, \n"
			+ "	payroll_worksheet.regular_hours, \n"
			+ "	payroll_worksheet.regular_pay,	\n"
			+ "	payroll_worksheet.expenses, \n"
			+ "	payroll_worksheet.ot_hours,	\n"
			+ "	payroll_worksheet.ot_pay,	\n"
			+ "	payroll_worksheet.vacation_hours, \n"
			+ "	payroll_worksheet.vacation_pay, \n"
			+ "	payroll_worksheet.holiday_hours, \n"
			+ "	payroll_worksheet.holiday_pay, \n"
			+ "	payroll_worksheet.gross_pay,	\n"
			+ "	payroll_worksheet.expenses_submitted,	\n"
			+ "	payroll_worksheet.expenses_allowed, \n"
			+ "	payroll_worksheet.volume, \n"
			+ "	payroll_worksheet.direct_labor,	\n"
			+ "	payroll_worksheet.productivity,\n"
			+ "	case\n"
			+ "		when isnull(payroll_worksheet.expenses_submitted,0) > isnull(payroll_worksheet.expenses_allowed,0) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as excess_expense_claim,\n"
			+ "	case\n"
			+ "		when isnull(payroll_worksheet.expenses_submitted,0) > (.2 * isnull(payroll_worksheet.gross_pay,0)) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as excess_expense_pct,\n"
			+ "	ytd_expense.ytd_pay_period,\n"
			+ "	isnull(ytd_expense.ytd_gross_pay,0) as ytd_gross_pay,\n"
			+ "	isnull(ytd_expense.ytd_expenses_submitted,0) as ytd_expenses_submitted,\n"
			+ "	isnull(ytd_expense.ytd_expenses_allowed,0) as ytd_expenses_allowed,\n"
			+ "	case\n"
			+ "		when isnull(ytd_expense.ytd_expenses_submitted,0) > isnull(ytd_expense.ytd_expenses_allowed,0) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as ytd_excess_expense_claim,\n"
			+ "	case\n"
			+ "		when isnull(ytd_expense.ytd_expenses_submitted,0) > (.2 * isnull(ytd_expense.ytd_gross_pay,0)) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as ytd_excess_expense_pct,\n"
			+ "	case\n"
			+ "		when week_ending > payroll_employee.employee_termination_date then 1\n"
			+ "		else 0\n"
			+ "	end as paid_late \n";
			
			
	private static final String sqlFromClause =
			"FROM division_group\n"
			+ "INNER JOIN division on division.group_id = division_group.group_id\n"
			+ "LEFT OUTER JOIN payroll_employee on payroll_employee.division_id = division.division_id\n"
			+ "LEFT OUTER JOIN payroll_worksheet on payroll_worksheet.division_id = division.division_id \n"
			+ "	and payroll_worksheet.employee_code = payroll_employee.employee_code\n"
			+ "	and (payroll_worksheet.week_ending is null or payroll_worksheet.week_ending='08-13-2021') \n"
			+ "LEFT OUTER JOIN (\n"
			+ "	select \n"
			+ "		employee_code,\n"
			+ "		year(week_ending) as ytd_pay_period,\n"
			+ "		sum(isnull(gross_pay,0)) as ytd_gross_pay, \n"
			+ "		sum(isnull(expenses_submitted,0)) as ytd_expenses_submitted, \n"
			+ "		sum(isnull(expenses_allowed,0)) as ytd_expenses_allowed\n"
			+ "	from payroll_worksheet\n"
			+ "	group by payroll_worksheet.employee_code, year(week_ending)	\n"
			+ "	) as ytd_expense\n"
			+ "	on ytd_expense.employee_code = payroll_employee.employee_code \n"
			+ "	--and payroll_worksheet.division_id = ytd_expense.division_id\n"
			+ "	and ytd_expense.ytd_pay_period = year(payroll_worksheet.week_ending) \n";			
	private static final String sqlWhereClause =
			"WHERE group_type = 'COMPANY' and division_group.company_code is not NULL and division_group.group_id=? \n";
//			"ORDER BY company_code, group_name";
			
	

	public ExceptionReportQuery(Integer userId, List<SessionDivision> divisionList, Integer groupId) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
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
			whereClause = StringUtils.isBlank(sqlWhereClause)?  "where " + filterClause : "where " + sqlWhereClause + " and " + filterClause;
		}
		
		return whereClause;
	}


	
	
	
}
