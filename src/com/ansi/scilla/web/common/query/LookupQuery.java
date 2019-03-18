package com.ansi.scilla.web.common.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.SelectType;

public abstract class LookupQuery extends ApplicationObject {
	

	
	private static final long serialVersionUID = 1L;

	
	protected String sqlSelectClause;
	protected String sqlFromClause;
	protected String baseWhereClause;
	protected String sql;
	protected String sqlCount;
	
	protected String sortBy;
	protected Boolean sortIsAscending = true;
	protected Integer userId;
	protected String searchTerm;
	
	
	protected Logger logger;

	
	
	public LookupQuery(String sqlSelectClause, String sqlFromClause, String baseWhereClause) {
		super();
		this.sqlSelectClause = sqlSelectClause;
		this.sqlFromClause = sqlFromClause;
		this.baseWhereClause = baseWhereClause;
		this.sql = sqlSelectClause + sqlFromClause;
		this.sqlCount = "select count(*) as record_count " + sqlFromClause;
	}

	
	public String getSqlSelectClause() {
		return sqlSelectClause;
	}


	public void setSqlSelectClause(String sqlSelectClause) {
		this.sqlSelectClause = sqlSelectClause;
	}


	public String getSqlFromClause() {
		return sqlFromClause;
	}


	public void setSqlFromClause(String sqlFromClause) {
		this.sqlFromClause = sqlFromClause;
	}


	public String getBaseWhereClause() {
		return baseWhereClause;
	}


	public void setBaseWhereClause(String baseWhereClause) {
		this.baseWhereClause = baseWhereClause;
	}


	public String getSql() {
		return sql;
	}


	public void setSql(String sql) {
		this.sql = sql;
	}


	public String getSqlCount() {
		return sqlCount;
	}


	public void setSqlCount(String sqlCount) {
		this.sqlCount = sqlCount;
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


	public ResultSet select(Connection conn, Integer offset, Integer rowCount) throws Exception {
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

	private String makeSQL(SelectType selectType, Integer offset, Integer rowCount) {
		String searchSQL =	selectType.equals(SelectType.DATA) ? sql : sqlCount;

		String offsetPhrase = makeOffset(selectType, offset);
		String fetchPhrase = makeFetch(selectType, rowCount);
		String orderByPhrase = makeOrderBy(selectType);
		String wherePhrase = selectType.equals(SelectType.COUNTALL) ? baseWhereClause : makeWhereClause(this.searchTerm);
		
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
	
	
	
	private PreparedStatement makePreparedStatement(Connection conn, SelectType selectType, String searchSQL) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(searchSQL);
		return ps;
	}
	
	/**
	 * Create a string defining how results are to be sorted. Generally, if the select type is COUNT (ie, we're getting
	 * a count of records) a sort is a waste of processing and should be default to whatever the database likes.
	 * If we're returning a set of rows (select type is DATA), then the primary sort is defined by either a default value, or
	 * the col parameter along with whatever secondary sort makes sense for this particular query. Don't forget to include
	 * checking the sortIsAscending value.
	 * @param selectType
	 * @return
	 */
	protected abstract String makeOrderBy(SelectType selectType);
	
	/**
	 * Create a string defining how a search term is to be implemented. Typically this includes a bunch of terms looking
	 * something along the lines of "lower(field) like ('%searchterm%').toLowerCase()" that have been or'ed together.
	 * @param queryTerm
	 * @return
	 */
	protected abstract String makeWhereClause(String queryTerm);
}
