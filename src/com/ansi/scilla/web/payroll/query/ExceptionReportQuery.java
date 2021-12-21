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
	private static final String sqlSelectClause = "select \n"
			+ "	company.group_id as company_id,\n"
			+ "	company.name as company_name,\n"
			+ "	division.division_id,\n"
			+ "	concat(division.division_nbr,'-',division.division_code) as div,\n"
			+ "	division.division_nbr, \n"
			+ "	division.minimum_hourly_pay,\n"
			+ "	employee_division.division_id as employee_div_id,\n"
			+ "	concat(employee_division.division_nbr,'-',employee_division.division_code) as employee_div,\n"
			+ "	employee_division.division_nbr as employee_division_nbr, \n"
			+ "	employee_division.minimum_hourly_pay as employee_minimum_hourly_pay,\n"
			+ "	payroll_worksheet.employee_code,\n"
			+ "	payroll_worksheet.employee_name,\n"
			+ "	payroll_worksheet.week_ending,\n"
			+ " FORMAT(payroll_worksheet.week_ending, 'd','en-US') as week_ending_display,\n" 
			+ "	payroll_employee.employee_status, \n"
			+ "	payroll_employee.employee_termination_date,\n"
			+ "	payroll_employee.union_member,\n"
			+ "	payroll_employee.union_code,\n"
			+ "	payroll_employee.union_rate,\n"
			+ "	payroll_worksheet.regular_hours,\n"
			+ "	payroll_worksheet.regular_pay,\n"
			+ "	payroll_worksheet.expenses,\n"
			+ "	payroll_worksheet.ot_hours,\n"
			+ "	payroll_worksheet.ot_pay,\n"
			+ "	payroll_worksheet.vacation_hours,\n"
			+ "	payroll_worksheet.vacation_pay,\n"
			+ "	payroll_worksheet.holiday_hours,\n"
			+ "	payroll_worksheet.holiday_pay,\n"
			+ "	payroll_worksheet.gross_pay,\n"
			+ "	payroll_worksheet.expenses_submitted,\n"
			+ "	payroll_worksheet.expenses_allowed,\n"
			+ "	payroll_worksheet.volume,\n"
			+ "	payroll_worksheet.direct_labor,	\n"
			+ "	ytd.ytd_regular_hours,\n"
			+ "	ytd.ytd_regular_pay,\n"
			+ "	ytd.ytd_expenses,\n"
			+ "	ytd.ytd_ot_hours,\n"
			+ "	ytd.ytd_ot_pay,\n"
			+ "	ytd.ytd_vacation_hours,\n"
			+ "	ytd.ytd_vacation_pay,\n"
			+ "	ytd.ytd_holiday_hours,\n"
			+ "	ytd.ytd_holiday_pay,\n"
			+ "	ytd.ytd_gross_pay,\n"
			+ "	ytd.ytd_expenses_submitted,\n"
			+ "	ytd.ytd_expenses_allowed,\n"
			+ "	ytd.ytd_volume,\n"
			+ "	ytd.ytd_direct_labor,\n"
			+ "	case\n"
			+ "		when isnull(payroll_worksheet.expenses_submitted,0) > isnull(payroll_worksheet.expenses_allowed,0) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as excess_expense_claim,\n"
			+ "	case\n"
			+ "		when isnull(payroll_worksheet.expenses_submitted,0) > (.2 * isnull(payroll_worksheet.gross_pay,0)) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as excess_expense_pct,\n"
			+ "	case\n"
			+ "		when isnull(ytd.ytd_expenses_submitted,0) > isnull(ytd.ytd_expenses_allowed,0) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as ytd_excess_expense_claim,\n"
			+ "	case\n"
			+ "		when isnull(ytd.ytd_expenses_submitted,0) > (.2 * isnull(ytd.ytd_gross_pay,0)) then 1\n"
			+ "		ELSE 0 \n"
			+ "	end as ytd_excess_expense_pct,\n"
			+ "	case\n"
			+ "		when week_ending > payroll_employee.employee_termination_date then 1\n"
			+ "		else 0\n"
			+ "	end as paid_late,\n"
			+ "	case\n"
			+ "		-- when direct_labor < (division.minimum_hourly_pay * (payroll_worksheet.regular_hours + payroll_worksheet.vacation_hours + payroll_worksheet.holiday_hours)) then 1\n"
			+ "		when (payroll_worksheet.gross_pay - payroll_worksheet.expenses) < (division.minimum_hourly_pay * (payroll_worksheet.regular_hours + payroll_worksheet.vacation_hours + payroll_worksheet.holiday_hours)) then 1\n"
			+ "		else 0\n"
			+ "	end as under_govt_min_pay,\n"
			+ "	case\n"
			+ "		-- when (payroll_employee.union_member = 1) and (direct_labor < (isnull(payroll_employee.union_rate,0) * (payroll_worksheet.regular_hours + payroll_worksheet.vacation_hours + payroll_worksheet.holiday_hours)) then 1\n"
			+ "		when (payroll_employee.union_member = 1) and (payroll_worksheet.gross_pay - payroll_worksheet.expenses) < (isnull(payroll_employee.union_rate,0) * (payroll_worksheet.regular_hours + payroll_worksheet.vacation_hours + payroll_worksheet.holiday_hours)) then 1\n"
			+ "		else 0\n"
			+ "	end as under_union_min_pay,\n"
			+ "	case\n"
			+ "		when (ytd.ytd_gross_pay - ytd.ytd_expenses) < (division.minimum_hourly_pay * (ytd.ytd_regular_hours + ytd.ytd_vacation_hours + ytd.ytd_holiday_hours)) then 1\n"
			+ "		else 0\n"
			+ "	end as ytd_under_govt_min_pay,\n"
			+ "	case\n"
			+ "		when (payroll_employee.union_member = 1) and (ytd.ytd_gross_pay - ytd.ytd_expenses) < (isnull(payroll_employee.union_rate,0) * (ytd.ytd_regular_hours + ytd.ytd_vacation_hours + ytd.ytd_holiday_hours)) then 1\n"
			+ "		else 0\n"
			+ "	end as ytd_under_union_min_pay\n";
			
			
	private static final String sqlFromClause =
			"from division_group as company\n"
			+ "inner join division on division.group_id=company.group_id \n"
			+ "inner join payroll_worksheet on payroll_worksheet.division_id = division.division_id -- and payroll_worksheet.week_ending='2021-12-10'\n"
			+ "inner join payroll_employee on payroll_employee.employee_code = payroll_worksheet.employee_code\n"
			+ "inner join division as employee_division on employee_division.division_id = payroll_employee.division_id\n"
			+ "inner join (select\n"
			+ "		employee_code,\n"
			+ "		year(week_ending) as pay_period,\n"
			+ "		sum(isnull(regular_hours,0)) as ytd_regular_hours,\n"
			+ "		sum(isnull(regular_pay,0)) as ytd_regular_pay,\n"
			+ "		sum(isnull(expenses,0)) as ytd_expenses,\n"
			+ "		sum(isnull(ot_hours,0)) as ytd_ot_hours,\n"
			+ "		sum(isnull(ot_pay,0)) as ytd_ot_pay,\n"
			+ "		sum(isnull(vacation_hours,0)) as ytd_vacation_hours,\n"
			+ "		sum(isnull(vacation_pay,0)) as ytd_vacation_pay,\n"
			+ "		sum(isnull(holiday_hours,0)) as ytd_holiday_hours,\n"
			+ "		sum(isnull(holiday_pay,0)) as ytd_holiday_pay,\n"
			+ "		sum(isnull(gross_pay,0)) as ytd_gross_pay,\n"
			+ "		sum(isnull(expenses_submitted,0)) as ytd_expenses_submitted,\n"
			+ "		sum(isnull(expenses_allowed,0)) as ytd_expenses_allowed,\n"
			+ "		sum(isnull(volume,0)) as ytd_volume,\n"
			+ "		sum(isnull(direct_labor,0)) as ytd_direct_labor\n"
			+ "	from payroll_worksheet\n"
			+ "	group by payroll_worksheet.employee_code, year(week_ending)) as ytd\n"
			+ "	on ytd.employee_code=payroll_worksheet.employee_code and ytd.pay_period=year(payroll_worksheet.week_ending)\n";			
	private static final String sqlWhereClause =
			"WHERE company.group_type = 'COMPANY' and company.company_code is not NULL and company.group_id=? \n";
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
