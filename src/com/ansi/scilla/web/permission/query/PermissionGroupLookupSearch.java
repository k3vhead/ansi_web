package com.ansi.scilla.web.permission.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.queries.PaymentSearchResult;
import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.SelectType;

public class PermissionGroupLookupSearch extends ReportQuery {

	private static final long serialVersionUID = 1L;

	private String sortBy;
	private Boolean sortIsAscending = true;
	private Integer userId;
	private String searchTerm;
	private Logger logger;
	
	
	/*
	private static final String sql = "select permission_group.*, users.user_count " +
							" from permission_group   " +
							"  inner join (  " +
							"  select permission_group.permission_group_id, count(ansi_user.user_id) as user_count " + 
							"  from permission_group  " +
							"  left outer join ansi_user on ansi_user.permission_group_id=permission_group.permission_group_id " + 
							"  group by permission_group.permission_group_id " +
							"  ) users on permission_group.permission_group_id=users.permission_group_id";
	 */
	
	
	
	
	private static final String sqlSelectClause = 
					"select permission_group.permission_group_id, "
					+ "permission_group.name, "
					+ "permission_group.description, "
					+ "permission_group.permission_group_status, "
					+ "users.user_count "; 
			;

	private static final String sqlFromClause = "\n  "
			+ " from permission_group   " 
			+ "  inner join (  " 
			+ "  select permission_group.permission_group_id, count(ansi_user.user_id) as user_count " 
			+ "  from permission_group  "
			+ "  left outer join ansi_user on ansi_user.permission_group_id=permission_group.permission_group_id " 
			+ "  group by permission_group.permission_group_id "
			+ "  ) users on permission_group.permission_group_id=users.permission_group_id";

	private static final String baseWhereClause = "\n  ";
	
	
	private static final String sql = sqlSelectClause + sqlFromClause;
	private static final String sqlCount = "select count(*) as record_count " + sqlFromClause;
	
	
	public PermissionGroupLookupSearch(Integer userId) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public PermissionGroupLookupSearch(Integer userId, String searchTerm) {
		this(userId);
		this.searchTerm = searchTerm;
	}

	

	public String getSortBy() {
		return sortBy;
	}

	public void setSortBy(String sortBy) {
		this.sortBy = sortBy;
	}

	public Boolean getSortIsAscending() {
		return sortIsAscending;
	}

	public void setSortIsAscending(Boolean sortIsAscending) {
		this.sortIsAscending = sortIsAscending;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	
	
	
	public List<PermissionGroupSearchResult> select(Connection conn, Integer offset, Integer rowCount) throws Exception {
		SelectType selectType = SelectType.DATA;
		List<PermissionGroupSearchResult> resultList = new ArrayList<PermissionGroupSearchResult>();
		String searchSQL = makeSQL(selectType, offset, rowCount);
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			resultList.add(new PermissionGroupSearchResult(rs, rsmd));
		}
		rs.close();

		return resultList;
	}
	
	
	
	public Integer selectCount(Connection conn) throws SQLException {
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
	
	public Integer selectCountAll(Connection conn) throws SQLException {
		SelectType selectType = SelectType.COUNTALL;
		Integer returnCount = 0;
		String searchSQL = makeSQL(selectType, null, null);
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
//		ps.setInt(1, this.userId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			returnCount = rs.getInt("record_count");
		}
		rs.close();
		return returnCount;
	}
	
	
	private String makeSQL(SelectType selectType, Integer offset, Integer rowCount) {
		String searchSQL =	selectType.equals(SelectType.DATA) ? sql : sqlCount;

		String offsetPhrase = makeOffset(selectType, offset);
		String fetchPhrase = makeFetch(selectType, rowCount);
		String orderByPhrase = makeOrderBy(selectType);
		String wherePhrase = selectType.equals(SelectType.COUNTALL) ? PermissionGroupLookupSearch.baseWhereClause : makeWhereClause(this.searchTerm);
		
		String sql = searchSQL + wherePhrase + orderByPhrase + offsetPhrase + fetchPhrase;
		this.logger.log(Level.DEBUG, sql);
				
		return sql;
	}
	
	
	
	private String makeFetch(SelectType selectType, Integer rowCount) {
		return selectType.equals(SelectType.DATA) ? "\n FETCH NEXT " + rowCount + " ROWS ONLY " : "";
	}

	private String makeOffset(SelectType selectType, Integer offset) {
		return selectType.equals(SelectType.DATA) ? "\n OFFSET " + offset + " ROWS " :"";
	}
	
	private String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = " order by " + PaymentSearchResult.PAYMENT_ID + " asc ";
			} else {
				orderBy = " order by " + sortBy;
				orderBy = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
			}
		}

		return "\n" + orderBy;
	}
	
	private PreparedStatement makePreparedStatement(Connection conn, SelectType selectType, String searchSQL) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(searchSQL);
//		int idx = 1;  // because JDBC is not zero-based
//		ps.setInt(idx, this.userId);
		return ps;
	}

	
	
	
	

	

	/**
	 * Return a where clause with the passed in queryTerm embedded
	 * 
	 * @param queryTerm
	 * @return 
	 * @throws Exception
	 */
	private String makeWhereClause(String queryTerm)  {
		String whereClause = PermissionGroupLookupSearch.baseWhereClause;
		if (! StringUtils.isBlank(queryTerm)) {
				whereClause =  whereClause + " and (\n"
						+ " permission_group.permission_group_id like '%" + queryTerm + "%' " +
						"\n OR permission_group.name like '%" + queryTerm + "%'" +
						"\n OR permission_group.description like '%" + queryTerm + "%'" +
						")" ;
		}
		return whereClause;
	}
	
	
	
	
}
