package com.ansi.scilla.web.division.query;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class DivisionLookupQuery extends LookupQuery {

	public static final String DIVISION_ID = "division_id";
	public static final String DIV = "div";
	public static final String DESCRIPTION = "description";
	public static final String DEFAULT_DIRECT_LABOR_PCT = "default_direct_labor_pct";
	public static final String MAX_REG_HRS_PER_DAY = "max_reg_hrs_per_day";
	public static final String MAX_REG_HRS_PER_WEEK = "max_reg_hrs_per_week";
	public static final String MINIMUM_HOURLY_PAY = "minimum_hourly_pay";
	public static final String OVERTIME_RATE = "overtime_rate";
	public static final String WEEKEND_IS_OT = "weekend_is_ot";
	public static final String HOURLY_RATE_IS_FIXED = "hourly_rate_is_fixed";
	public static final String DIVISION_STATUS = "division_status";
	public static final String USER_COUNT = "user_count";

	
	private static final long serialVersionUID = 1L;

	
	private static final String sqlSelectClause = 
			"select division.division_id as " + DIVISION_ID + ", \n" + 
			"	concat(division_nbr,'-',division_code) as " + DIV + ", \n" + 
			"	division.description as " + DESCRIPTION + ",\n" + 
			"	division.default_direct_labor_pct as " + DEFAULT_DIRECT_LABOR_PCT + ",\n" + 
			"	division.max_reg_hrs_per_day as " + MAX_REG_HRS_PER_DAY + ",\n" + 
			"	division.max_reg_hrs_per_week  as " +MAX_REG_HRS_PER_WEEK+ ",\n" + 
			"	division.minimum_hourly_pay  as " +MINIMUM_HOURLY_PAY+ ",\n" + 
			"	division.overtime_rate as " + OVERTIME_RATE + ",\n" + 
			"	division.weekend_is_ot as " + WEEKEND_IS_OT + ",\n" + 
			"	division.hourly_rate_is_fixed as " + HOURLY_RATE_IS_FIXED + ",\n" + 
			"	division.division_status as " + DIVISION_STATUS + ",	\n" + 
			"	users.user_count as " + USER_COUNT + "\n";
			
	private static final String sqlFromClause = 
			"from division \n" + 
			"inner join ( \n" + 
			"	select division.division_id, count(division_user.user_id) as user_count\n" + 
			"	from division \n" + 
			"	left outer join division_user on division_user.division_id=division.division_id\n" + 
			"	group by division.division_id \n" + 
			") users on division.division_id=users.division_id";

	private static final String baseWhereClause = "\n  ";

	
	
	
	public DivisionLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);		
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public DivisionLookupQuery(Integer userId, List<SessionDivision> divisionList, String searchTerm) {
		this(userId, divisionList);
		this.searchTerm = searchTerm;
	}

	
	
	

	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + DIV + " asc, " + DESCRIPTION + " asc ";
			} else {
//				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
//				String sortBy = StringUtils.join(sortList, sortDir + ", ");
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}
	
	
	
	
	/**
	 * Return a where clause with the passed in queryTerm embedded
	 * 
	 * @param queryTerm
	 * @return 
	 * @throws Exception
	 */
	protected String makeWhereClause(String queryTerm)  {
		String whereClause = super.getBaseWhereClause();
		logger.log(Level.DEBUG, whereClause);
		String joiner = StringUtils.isBlank(baseWhereClause) ? " where " : " and ";
		logger.log(Level.DEBUG, joiner);
		if (! StringUtils.isBlank(queryTerm)) {
				whereClause =  whereClause + joiner + " (\n"
						+ " lower(concat(division_nbr,'-',division_code)) like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR lower(" + DESCRIPTION + ") like '%" + queryTerm.toLowerCase() + "%'" +
						")" ;
		}
		logger.log(Level.DEBUG, whereClause);
		return whereClause;
	}
	
	
}
