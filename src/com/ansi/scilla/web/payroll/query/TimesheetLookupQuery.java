package com.ansi.scilla.web.payroll.query;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class TimesheetLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;
	
	
	
	private static final String sqlSelectClause = "select \n"
			+ "	payroll_worksheet.division_id,\n"
			+ "	concat(division.division_nbr, '-', division.division_code) as div,\n"
			+ "	payroll_worksheet.week_ending,\n"
			+ "	locale.state_name as state,\n"
			+ "	case\n"
			+ "	    when locale.locale_type_id='STATE' then null\n"
			+ "	    else locale.name \n"
			+ "	end as city,\n"
			+ "	payroll_worksheet.employee_code,\n"
			+ "	-- payroll_employee.employee_first_name,\n"
			+ "	-- payroll_employee.employee_last_name,\n"
			+ "	-- payroll_employee.employee_mi,\n"
			+ "	-- concat(payroll_employee.employee_last_name, ', ', payroll_employee.employee_first_name, ' ', payroll_employee.employee_mi) as employee_name,\n"
			+ "	payroll_worksheet.employee_name,\n"
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
			+ "	payroll_worksheet.direct_labor,\n"
			+ "	payroll_worksheet.productivity\n";
	private static final String sqlFromClause =
			"from payroll_worksheet \n" + 
			"inner join division on division.division_id = payroll_worksheet.division_id\n" + 
			"-- inner join payroll_employee on payroll_employee.employee_code = payroll_worksheet.employee_code \n" +
			"left outer join locale on locale.locale_id=payroll_worksheet.locale_id\n";
	private static final String sqlWhereClause = "";
			
	


	public TimesheetLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, sqlWhereClause);
		this.logger = LogManager.getLogger(TimesheetLookupQuery.class);
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
		
		/*
			"	payroll_worksheet.division_id,\n" +
			"	payroll_worksheet.regular_hours,\n" + 
			"	payroll_worksheet.regular_pay,\n" + 
			"	payroll_worksheet.expenses,\n" + 
			"	payroll_worksheet.ot_hours,\n" + 
			"	payroll_worksheet.ot_pay,\n" + 
			"	payroll_worksheet.vacation_hours,\n" + 
			"	payroll_worksheet.vacation_pay,\n" + 
			"	payroll_worksheet.holiday_hours,\n" + 
			"	payroll_worksheet.holiday_pay,\n" + 
			"	payroll_worksheet.gross_pay,\n" + 
			"	payroll_worksheet.expenses_submitted,\n" + 
			"	payroll_worksheet.expenses_allowed,\n" + 
			"	payroll_worksheet.volume,\n" + 
			"	payroll_worksheet.direct_labor,\n" + 
			"	payroll_worksheet.productivity\n";		
			 
			"	,\n" + 
			 
			"	payroll_worksheet.week_ending,\n" + 

			
		 */
		
		String[] searchableFields = new String[] {
				"concat(division.division_nbr, '-', division.division_code)",
				"payroll_worksheet.state", 
				"payroll_worksheet.city",
				"payroll_worksheet.employee_code", 
				"payroll_worksheet.employee_name",
				"payroll_worksheet.week_ending",
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
