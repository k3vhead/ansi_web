package com.ansi.scilla.web.division.query;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.SessionDivisionTransformer;

/**
 * List all users assigned to a division 
 * @author dclewis
 *
 */
public class UserDivisionLookupQuery extends LookupQuery {

	private static final long serialVersionUID = 1L;

	public static final String USER_ID = "ansi_user.user_id";
	public static final String FIRST_NAME = "ansi_user.first_name";
	public static final String LAST_NAME = "ansi_user.last_name";
	public static final String TITLE = "ansi_user.title";
	public static final String PHONE = "ansi_user.phone";
	public static final String EMAIL = "ansi_user.email";
	public static final String DIVISION_ID = "division_user.division_id";
	
	public static final String sqlSelect = 
			"select ansi_user.user_id, ansi_user.first_name, ansi_user.last_name, ansi_user.title, ansi_user.phone, ansi_user.email ";		
	public static final String sqlFromClause = 
			"from ansi_user \n";
	public static final String sqlWhereClause =
			"where ansi_user.user_status=1\n" + 
			"and ansi_user.permission_group_id in (\n" + 
			"    select permission_group_level.permission_group_id\n" + 
			"        from permission_group_level \n" + 
			"        where permission_group_level.permission_name='CAN_RUN_TICKETS' ) \n" + 
			"and ansi_user.user_id in (\n" + 
			"    select user_id from division_user where division_id in ($DIVISIONFILTER$)\n" + 
			")";
	
	private Integer divisionId;
	private List<SessionDivision> divisionList;
	
	public UserDivisionLookupQuery(Integer userId, List<SessionDivision> divisionList) {		
		super(sqlSelect, sqlFromClause, makeBaseWhereClause(divisionList, (Integer)null));
		this.divisionList = divisionList;
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@Override
	protected String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + LAST_NAME + " asc, " + FIRST_NAME + " asc ";
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
//		logger.log(Level.DEBUG, "makeWhereClause");
		String whereClause = makeBaseWhereClause(this.divisionList, this.divisionId);
		String joiner = StringUtils.isBlank(whereClause) ? " where " : " and ";
		
		
		if (! StringUtils.isBlank(queryTerm)) {
			String searchTerm = queryTerm.toLowerCase();
			Collection<String> stringFields= Arrays.asList(new String[] {
					FIRST_NAME,
					LAST_NAME,
					TITLE,
					EMAIL,
					PHONE
			});
			Collection<String> numericFields = Arrays.asList(new String[] {
					USER_ID, 
					DIVISION_ID
			});
			
			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);
			if ( StringUtils.isNumeric(searchTerm)) {
				whereFields.addAll(numericFields);
			}

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(searchTerm));
			whereClause = whereClause + joiner +  "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
		}

//		List<ColumnFilter> filterList = new ArrayList<ColumnFilter>();
//		if ( divisionId != null ) {
//			filterList.add(new ColumnFilter(DIVISION_ID, divisionId, ColumnFilter.ComparisonType.EQUAL_NUMBER));
//		}
//		
//		super.setConstraintList(filterList);

		return whereClause;
	}

	
	
	private static String makeBaseWhereClause(List<SessionDivision> divisionList, Integer divisionId) {
		List<Integer> divisionIdList = (List<Integer>) CollectionUtils.collect(divisionList, new SessionDivisionTransformer());
		String divisionString = divisionId == null ? StringUtils.join(divisionIdList, ",") : String.valueOf(divisionId);
		String sql = StringUtils.replace(sqlWhereClause, "$DIVISIONFILTER$", divisionString);
		return sql;
	}

	

	
	public static void main(String[] args) {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			List<SessionDivision> sdList = new ArrayList<SessionDivision>();
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
			for ( Division d : divisionList) { sdList.add(new SessionDivision(d)); }
			UserDivisionLookupQuery x = new UserDivisionLookupQuery(5, sdList);
//			x.setSearchTerm("Chicago");
			x.setDivisionId(102);
//			System.out.println("Countall: " + x.countAll(conn));
			System.out.println("Count some: " + x.selectCount(conn));
//			ResultSet rs = x.select(conn,  0, 100);
//			while ( rs.next() ) { 
//				System.out.println(rs.getInt("user_id") + "\t" + rs.getString("last_name"));
//			}
//			rs.close();
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	
}
