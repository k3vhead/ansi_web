package com.ansi.scilla.web.claims.query;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.query.LookupQuery;

public class BudgetControlLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	
	
	private static final String sqlSelectClause = 
					"select division.division_id, "
					+ "\n concat(division.division_nbr,'-',division.division_code) as div, "
					+ "\n non_direct_labor.labor_id, "
					+ "\n non_direct_labor.washer_id, "
					+ "\n ansi_user.last_name, "
					+ "\n ansi_user.first_name, "
					+ "\n non_direct_labor.work_date, "
					+ "\n non_direct_labor.hours, "
					+ "\n non_direct_labor.hours_type, "
					+ "\n non_direct_labor.notes, "
					+ "\n non_direct_labor.act_payout_amt, "
					+ "\n non_direct_labor.calc_payout_amt "; 
		

	private static final String sqlFromClause = "\n  "
			+ "\n from non_direct_labor "
			+ "\n inner join ansi_user on ansi_user.user_id=non_direct_labor.washer_id "
			+ "\n inner join division on division.division_id=non_direct_labor.division_id";

	private static final String baseWhereClause = "\n  ";
	
	
	
	
	public BudgetControlLookupQuery(Integer userId) {
		super(sqlSelectClause, sqlFromClause, baseWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public BudgetControlLookupQuery(Integer userId, String searchTerm) {
		this(userId);
		this.searchTerm = searchTerm;
	}

	



	
	
	

	
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + NonDirectLaborSearchResult.DIV + "asc, " + 
							NonDirectLaborSearchResult.WORK_DATE + " asc, " +
							NonDirectLaborSearchResult.LAST_NAME + " asc," + 
							NonDirectLaborSearchResult.FIRST_NAME + " asc ";
			} else {
				List<String> sortList = Arrays.asList(StringUtils.split(sortBy, ","));
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
				String sortBy = StringUtils.join(sortList, sortDir + ", ");
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
		String whereClause = BudgetControlLookupQuery.baseWhereClause;
		if (! StringUtils.isBlank(queryTerm)) {
				whereClause =  whereClause + " and (\n"
						+ " lower(concat(ansi_user.last_name, ', ', ansi_user.first_name)) like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR lower(concat(ansi_user.first_name, ' ', ansi_user.last_name)) like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR lower(concat(division.division_nbr, '-', division.division_code)) like '%" + queryTerm.toLowerCase() + "%'" +
						"\n OR lower(non_direct_labor.notes) like '%" + queryTerm.toLowerCase() + "%'" +
						")" ;
		}
		return whereClause;
	}
	
	
	
	
}
