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

public class LocaleLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String LOCALE_ID = "locale.locale_id";
	public static final String STATE_NAME = "locale.state_name";
	public static final String NAME = "locale.name";
	public static final String LOCALE_TYPE_ID = "locale.locale_type_id";
	public static final String LOCALE_ABBREVIATION = "locale.abbreviation";
	public static final String PARENT_NAME = "parent.name";
	public static final String PARENT_STATE = "parent.state_name";
	public static final String PARENT_TYPE_ID = "parent.locale_type_id";
	public static final String PAYROLL_TAX_PROFILE = "ptp.profile_desc";
	
	public static final String sqlSelect = 
			"select locale.locale_id, locale.state_name, locale.name, locale.locale_type_id, locale.abbreviation,\n" + 
			"		parent.name as parent_name, parent.state_name as parent_state, parent.locale_type_id as parent_type_id,\n"
			+ "		ptp.profile_desc,\n"
			+ "		case\n"
			+ "			when isnull(locale_count.worksheet_count,0)=0 then 1\n"
			+ "			else 0\n"
			+ "		end as can_delete ";

	public static final String sqlFromClause = 
			"\n FROM locale  " +
			"\n left outer join locale parent on parent.locale_id=locale.parent_locale_id" +
			"\n left outer join payroll_tax_profile ptp on locale.payroll_tax_profile = ptp.profile_id\n" +
			"\n left outer join (select locale_id, count(*) as worksheet_count from payroll_worksheet group by locale_id) locale_count on locale_count.locale_id=locale.locale_id";
	
	public static final String sqlWhereClause = "";
	
	
	
	public LocaleLookupQuery(Integer userId, List<SessionDivision> divisionList) {
		super(sqlSelect, makeFromClause(sqlFromClause, divisionList, ""), sqlWhereClause);
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	
		
	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + STATE_NAME + ", " + NAME + ", " + LOCALE_TYPE_ID;
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
					STATE_NAME,
					NAME,
					LOCALE_TYPE_ID,
					LOCALE_ABBREVIATION,
					PARENT_NAME,
					PARENT_STATE,
					PAYROLL_TAX_PROFILE,
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					LOCALE_ID,				
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
		String sql = StringUtils.replace(sqlFromClause, "$USERFILTER$", divisionString);
		System.out.println("adding " + divisionFilter + " to sql");
		sql = StringUtils.replace(sql, "$DIVISIONFILTER$", divisionFilter);
		
		return sql;
	}

	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			LocaleLookupQuery x = new LocaleLookupQuery(5, sdList);
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
