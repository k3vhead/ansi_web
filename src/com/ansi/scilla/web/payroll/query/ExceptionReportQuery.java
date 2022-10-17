package com.ansi.scilla.web.payroll.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		this.logger.log(Level.DEBUG, "makeOrderBY(): " + selectType.name());
		this.logger.log(Level.DEBUG, "makeOrderBY(): " + sortBy);
		
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy =  " order by div, employee_name";
			} else {
				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}
		return "\n" + orderBy;
	}


	

	

	@Override
	protected String makeWhereClause(String queryTerm) {
//		String whereClause = sqlWhereClause;
		
		
		String[] searchableFields = new String[] {
				"company_name",  //"company.name" ,
				"div", // "concat(division.division_nbr,'-',division.division_code)" ,
				"employee_code", // "payroll_worksheet.employee_code" ,
				"employee_name", //"payroll_worksheet.employee_name" ,
		};


		
//		String myBaseWhereClause = makeBaseWhereClause(this.errorsOnly);
		
//		if ( StringUtils.isBlank(queryTerm) ) {
//			whereClause = myBaseWhereClause;
//		} else {
			List<String> searchStrings = new ArrayList<String>();
			for ( String field : searchableFields ) {
				searchStrings.add("lower(" + field + ") like '%" + queryTerm.toLowerCase() + "%'");
			}
			String filterClause = "(" + StringUtils.join(searchStrings, " \nOR ") + ")";
//			String whereClause = StringUtils.isBlank(myBaseWhereClause)?  "where " + filterClause : myBaseWhereClause + " and " + filterClause;
//		}
		
		return StringUtils.isBlank(queryTerm) ? "" : "where " + filterClause;
	}


	@Override
	protected String makeSQL(SelectType selectType, Integer offset, Integer rowCount) {
//		String searchSQL =	selectType.equals(SelectType.DATA) ? sql : sqlCount;

		String searchSQL = null;
		
		if ( this.errorsOnly ) {
			searchSQL = "select * from (" + 
					EmployeeValidationBaseSql.sqlSelectClause +
					EmployeeValidationBaseSql.sqlFromClause + 
					errorsOnlyClause + " ) as exception_list";						
		} else {
			searchSQL = "select * from (" + 
					EmployeeValidationBaseSql.sqlSelectClause +
					EmployeeValidationBaseSql.sqlFromClause +
					sqlWhereClause + " ) as exception_list";	
		}
		
		switch (selectType) {
		case COUNT:
			searchSQL = "select count(*) as record_count from (\n" + searchSQL + "\n" + makeFilterPhrase("") + ")\n as exception_count";
			break;
		case COUNTALL:
			searchSQL = "select count(*) as record_count from (\n" + searchSQL + ")\n as exception_count";
			break;
		case DATA:		
			searchSQL = searchSQL + "\n" + makeWhereClause(getSearchTerm()) + "\n" + makeFilterPhrase("") + "\n" + makeOrderBy(selectType);
			break;
		default:
			throw new RuntimeException("Unknown selectType: " + selectType.name());		
		}
		
		this.logger.log(Level.DEBUG, searchSQL);
		
		return searchSQL;
		
		
		/*
		String offsetPhrase = makeOffset(selectType, offset);
		String fetchPhrase = makeFetch(selectType, rowCount);
		String orderByPhrase = makeOrderBy(selectType);
		String searchPhrase = this.searchTerm.indexOf("'") > -1 ? this.searchTerm.replaceAll("'", "''") : this.searchTerm;
		String internalWhereClause = selectType.equals(SelectType.COUNTALL) ? makeBaseWhereClause(this.errorsOnly) : makeWhereClause(searchPhrase);
		String filterPhrase = makeFilterPhrase("");
		
		this.logger.log(Level.DEBUG, "internalWhereClause: " + internalWhereClause);
		this.logger.log(Level.DEBUG, "filterPhrase: " + filterPhrase);
		this.logger.log(Level.DEBUG, "orderByPhrase: " + orderByPhrase);
		this.logger.log(Level.DEBUG, "fetchPhrase: " + fetchPhrase);
		
		String searchSQL = "select * from (\n " + sql + "\n" + internalWhereClause + ") as exception_list " + 
					filterPhrase + " ";
					
		if ( selectType.equals(SelectType.DATA) ) {
			searchSQL = searchSQL + "\n" + orderByPhrase + " " + offsetPhrase + " " + fetchPhrase;
		} else {
			searchSQL = "select count(*) as record_count from (\n" + searchSQL + ") as exception_count";
		}
		this.logger.log(Level.DEBUG, searchSQL);
				
		return sql;
		*/
	}
	
	
	public ResultSet select(Connection conn, Integer offset, Integer rowCount) throws Exception {
		this.logger.log(Level.DEBUG, "select()");
		SelectType selectType = SelectType.DATA;
		String searchSQL = makeSQL(selectType, offset, rowCount);
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();		

		return rs;
	}
	
	/**
	 * Returns count of filtered list
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Integer selectCount(Connection conn) throws Exception {
		this.logger.log(Level.DEBUG, "selectCount()");
		SelectType selectType = SelectType.COUNT;
		Integer returnCount = 0;
		String searchSQL = makeSQL(selectType, null, null);
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			returnCount = rs.getInt("record_count");
		}
		rs.close();
		return returnCount;
	}
	
	/**
	 * Returns count of unfiltered list
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Integer countAll(Connection conn) throws Exception {
		this.logger.log(Level.DEBUG, "countAll()");

		SelectType selectType = SelectType.COUNTALL;
		Integer returnCount = 0;
		String searchSQL = makeSQL(selectType, null, null);
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			returnCount = rs.getInt("record_count");
		}
		rs.close();
		return returnCount;
	}
	
	
	

	
	
	

	@Override
	protected String makeFilterPhrase(String wherePhrase) {
		String filterPhrase = "";
		String joiner = StringUtils.isBlank(wherePhrase) ? " where " : " and ";
		FilterTransformer filterTransformer = new FilterTransformer();
		List<String> likeList = new ArrayList<String>();
		List<String> constraints = new ArrayList<String>();

		if ( this.columnFilter != null && this.columnFilter.size() > 0 ) {
//			logger.log(Level.DEBUG, "working column filters now");
			likeList = CollectionUtils.collect(this.columnFilter.iterator(), filterTransformer, likeList);
			filterPhrase = "\n" + joiner + " " + StringUtils.join(likeList, " and " );
		}
//		logger.log(Level.DEBUG, "likeList: " + likeList);
//		logger.log(Level.DEBUG, "filterPhrase 1: " + filterPhrase);
		if ( this.constraintList != null && this.constraintList.size() > 0 ) {
			constraints = CollectionUtils.collect(this.constraintList.iterator(), filterTransformer, constraints);
			String joiner2 = likeList.isEmpty() && StringUtils.isBlank(this.baseWhereClause) ? " where " : " and ";
			filterPhrase = filterPhrase + "\n" + joiner2 + " " + StringUtils.join(constraints, " and " );

		}
//		logger.log(Level.DEBUG, "filterPhrase 2: " + filterPhrase);
		return filterPhrase;
	}
	
	
	
	
	
	
	protected PreparedStatement makePreparedStatement(Connection conn, SelectType selectType, String searchSQL) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(searchSQL);
		Logger myLogger = LogManager.getLogger(ExceptionReportQuery.class);
		myLogger.log(Level.DEBUG, "SelectType: " + selectType.name());
		myLogger.log(Level.DEBUG, "SearchSQL: " + searchSQL);
		if ( this.baseFilterValue != null && this.baseFilterValue.size() > 0 ) {
			int idx = 1;
			for ( Object o : this.baseFilterValue ) {
				if ( o instanceof Integer ) {
					ps.setInt(idx,(Integer)o);
					this.logger.log(Level.DEBUG, "Index: " + idx + " Integer: " + (Integer)o);
				} else if ( o instanceof String ) {
					ps.setString(idx, (String)o);
					this.logger.log(Level.DEBUG, "Index: " + idx + " String: " + (String)o);
				} else if ( o instanceof java.util.Date) {
					java.util.Date date = (java.util.Date)o;
					ps.setDate(idx, new java.sql.Date(date.getTime()));
					this.logger.log(Level.DEBUG, "Index: " + idx + " Date: " + new java.sql.Date(date.getTime()));
				} else if ( o instanceof java.util.GregorianCalendar) {
					GregorianCalendar date = (GregorianCalendar)o;
					ps.setDate(idx, new java.sql.Date(date.getTime().getTime()));
					this.logger.log(Level.DEBUG, "Index: " + idx + " Date: " + new java.sql.Date(date.getTime().getTime()));
				} else {
					throw new RuntimeException("Add another value to the else for " + o.getClass().getName());
				}
				idx++;
			}
		}
		return ps;
	}	
}
