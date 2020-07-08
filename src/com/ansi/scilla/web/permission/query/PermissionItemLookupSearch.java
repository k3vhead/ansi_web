package com.ansi.scilla.web.permission.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.common.db.PermissionGroupLevel;
//import com.ansi.scilla.web.test.TestServlet;
//import com.ansi.scilla.common.db.PermissionGroupLevel;
import com.ansi.scilla.common.db.PermissionLevel;

public class PermissionItemLookupSearch extends ReportQuery {

	private static final long serialVersionUID = 1L;

	private String sortBy;
	private Boolean sortIsAscending = true;
	private Integer permissionGroupId;
	private String searchTerm;
	private Logger logger;
	
	// To return permission_level records..
	
//	private static final String sqlSelectClause = 
//			"select " + PermissionLevel.PERMISSION_NAME   
//			   + ", " + PermissionLevel.LEVEL;
//
//	private static final String sqlFromClause = "\n  " 
//					+ " from permission_level   ";  
//	private static final String baseWhereClause = "Where " + PermissionLevel.PERMISSION_NAME + " = "; 
		
	
	
	// To return permission_group_level records.. 	
	private static final String sqlSelectClause = 
				"select " + PermissionGroupLevel.PERMISSION_GROUP_ID 
  	     		   + ", " + PermissionGroupLevel.PERMISSION_NAME    
			       + ", " + PermissionGroupLevel.PERMISSION_LEVEL;

	private static final String sqlFromClause = "\n  " 
					+ " from permission_group_level   ";   

	private static final String baseWhereClause = "Where " + PermissionGroupLevel.PERMISSION_GROUP_ID + " = "; 
		
	private static final String sql = sqlSelectClause + sqlFromClause;
	private static final String sqlCount = "select count(*) as record_count " + sqlFromClause;
		
	public PermissionItemLookupSearch() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
	}
	
	public PermissionItemLookupSearch(Integer permissionGroupId) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.searchTerm = permissionGroupId.toString();
	}

	public PermissionItemLookupSearch(Integer permissionGroupId, String permissionName) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.permissionGroupId = permissionGroupId;
		this.searchTerm = permissionName;
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

//	public Integer getUserId() {
//		return userId;
//	}
//
//	public void setUserId(Integer userId) {
//		this.userId = userId;
//	}

	public String getSearchTerm() {
		return searchTerm;
	}

	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	public Integer getPermissionGroupId() {
		return this.permissionGroupId;
	}

	public void setPermissionGroupId(Integer permissionGroupId) {
		this.permissionGroupId = permissionGroupId;
	}

	
	public List<PermissionItemSearchResponse> select(Connection conn, Integer offset, Integer rowCount) throws Exception {
		SelectType selectType = SelectType.DATA;

		List<PermissionItemSearchResponse> resultList = new ArrayList<PermissionItemSearchResponse>();
		
		String searchSQL = makeSQL(selectType, offset, rowCount);
		logger.log(Level.DEBUG, searchSQL);
		
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		
		while ( rs.next() ) {
			resultList.add(new PermissionItemSearchResponse(rs, rsmd));
		}
		rs.close();

		return resultList;
	}
	
	
	/*
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
	*/
	
	/*
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
	*/
	
	private String makeSQL(SelectType selectType, Integer offset, Integer rowCount) {
		String searchSQL =	selectType.equals(SelectType.DATA) ? sql : sqlCount;
		String offsetPhrase = makeOffset(selectType, offset);
		String fetchPhrase = makeFetch(selectType, rowCount);
		String orderByPhrase = makeOrderBy(selectType);
		
		String wherePhrase = selectType.equals(SelectType.COUNTALL) ? PermissionItemLookupSearch.baseWhereClause : makeWhereClause(this.searchTerm);
		
		//if(this.searchTerm != null) {			
		//	if(this.searchTerm.isEmpty()) wherePhrase = " ";
		//}
			
		String sql = searchSQL + wherePhrase + orderByPhrase + offsetPhrase + fetchPhrase;
		
		this.logger.log(Level.DEBUG, "Search Term was - " + this.searchTerm);
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
				orderBy = " order by " + PermissionLevel.PERMISSION_NAME + " asc ";
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
		String whereClause = " "; 
		if(queryTerm != null) {
			whereClause = PermissionItemLookupSearch.baseWhereClause;
			if (! StringUtils.isBlank(queryTerm)) {
					whereClause =  whereClause + queryTerm;
			}
		}
		return whereClause;
	}
}
