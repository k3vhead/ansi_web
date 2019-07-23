package com.ansi.scilla.web.locale.query;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.ColumnFilter;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

public class LocaleDivisionLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String DIVISION_ID = "locale_division.division_id";
	public static final String LOCALE_ID = "locale_division.locale_id";
	public static final String EFF_START_DATE = "locale_division.effective_start_date";
	public static final String EFF_STOP_DATE = "locale_division.effective_stop_date"; 
	public static final String ADDRESS_ID = "locale_division.address_id"; 
	public static final String NAME = "locale.name";
	public static final String LOCALE_STATE_NAME = "locale.state_name";
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";
	public static final String DIVISION_CODE = "division.division_code";
	public static final String DIVISION_NBR = "division.division_nbr";
	public static final String DIVISION_DISPLAY = "concat(division_nbr,'-',division_code)";
	public static final String DESCRIPTION = "division.description"; 
	public static final String ADDRESS1 = "address.address1";
	public static final String ADDRESS2 = "address.address2";
	public static final String CITY = "address.city";
	public static final String STATE = "address.state";
	public static final String ZIP = "address.zip";
	
	public static final String sqlSelect = 
			"select locale_division.division_id, locale_division.locale_id, \n" + 
			"			locale_division.effective_start_date, locale_division.effective_stop_date,\n" + 
			"			locale_division.address_id,\n" + 
			"		locale.name, locale.state_name, locale.locale_type_id,\n" + 
			"		division.division_code, division.division_nbr, \n" +
			"		concat(division_nbr,'-',division_code) as division_display, division.description,\n" + 
			"		address.address1, address.address2, address.city, address.state, address.zip\n";
			

	public static final String sqlFromClause = 
			"from locale_division\n" + 
			"inner join locale on locale.locale_id=locale_division.locale_id\n" + 
			"inner join division on division.division_id=locale_division.division_id and division.division_id in ($DIVISIONFILTER$)\n" + 
			"inner join address on address.address_id=locale_division.address_id";
	
	public static final String sqlWhereClause = "";
	
	
	
	public LocaleDivisionLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	
		
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + DIVISION_DISPLAY;
			} else {
				String sortDir = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
				orderBy = " order by " + sortBy + " " + sortDir;
			}
		}

		return "\n" + orderBy;
	}

	@Override
	protected String makeWhereClause(String queryTerm) {
		logger.log(Level.DEBUG, "makeWhereClause");
		String whereClause = sqlWhereClause;
		String joiner = StringUtils.isBlank(sqlWhereClause) ? " where " : " and ";
		
		
		if (! StringUtils.isBlank(queryTerm)) {
			String searchTerm = queryTerm.toLowerCase();
			Collection<String> stringFields= Arrays.asList(new String[] {
					EFF_START_DATE,
					EFF_STOP_DATE,
					NAME,
					LOCALE_STATE_NAME,
					LOCALE_TYPE_ID,
					DIVISION_DISPLAY,
					DESCRIPTION,
					ADDRESS1,
					ADDRESS2,
					CITY,
					STATE,
					ZIP,	
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
			});
			
			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);
			if ( StringUtils.isNumeric(searchTerm)) {
				whereFields.addAll(numericFields);
			}

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(searchTerm));
			whereClause = whereClause + joiner +  "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
		}

		List<ColumnFilter> filterList = new ArrayList<ColumnFilter>();		
		super.setConstraintList(filterList);

		return whereClause;
	}

	
	
	private static String makeFromClause(String sqlfromclause, List<SessionDivision> divisionList, String divisionFilter) {
		List<Integer> divisionIdList = (List<Integer>) CollectionUtils.collect(divisionList, new SessionDivisionTransformer());
		String divisionString = StringUtils.join(divisionIdList, ",");
		String sql = StringUtils.replace(sqlFromClause, "$DIVISIONFILTER$", divisionString);
		System.out.println("adding " + divisionFilter + " to sql");
		
		return sql;
	}

	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			LocaleDivisionLookupQuery x = new LocaleDivisionLookupQuery(5, sdList);
			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
			ResultSet rs = x.select(conn,  0, 10);
			while ( rs.next() ) { 
				System.out.println(rs.getString("name"));
			}
			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
