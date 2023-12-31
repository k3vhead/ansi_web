package com.ansi.scilla.web.common.query;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.SelectType;
import com.ansi.scilla.web.common.utils.ColumnFilter;
// import com.ansi.scilla.web.payroll.query.ExceptionReportQuery;

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
	protected List<ColumnFilter> columnFilter;
	protected List<ColumnFilter> constraintList;
	protected List<Object> baseFilterValue;
	
	
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
		this.sqlCount = "select count(*) as record_count " + sqlFromClause;
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


	public List<ColumnFilter> getColumnFilter() {
		return columnFilter;
	}


	public void setColumnFilter(List<ColumnFilter> columnFilter) {
		this.columnFilter = columnFilter;
	}

	public void addColumnFilter(ColumnFilter columnFilter) {
		if (this.columnFilter == null ) {
			this.columnFilter = new ArrayList<ColumnFilter>();
		}
		this.columnFilter.add(columnFilter);
	}
	
	public List<Object> getBaseFilterValue() {
		return baseFilterValue;
	}

	public void setBaseFilterValue(List<Object> baseFilterValue) {
		this.baseFilterValue = baseFilterValue;
	}

	/**
	 * Add a value for the base where clause. Values must be added in the order in which they appear in the "where" clause.
	 * @param filterValue
	 */
	public void addBaseFilter(Object filterValue) {
		if ( this.baseFilterValue == null ) {
			this.baseFilterValue = new ArrayList<Object>();
		}
		this.baseFilterValue.add(filterValue);
	}

	public List<ColumnFilter> getConstraintList() {
		return constraintList;
	}


	public void setConstraintList(List<ColumnFilter> constraintList) {
		this.constraintList = constraintList;
	}


	public ResultSet select(Connection conn, Integer offset, Integer rowCount) throws Exception {
		SelectType selectType = SelectType.DATA;
		String searchSQL = makeSQL(selectType, offset, rowCount);
//		logger.log(Level.DEBUG, searchSQL);
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

	protected String makeSQL(SelectType selectType, Integer offset, Integer rowCount) {
		String searchSQL =	selectType.equals(SelectType.DATA) ? sql : sqlCount;

		String offsetPhrase = makeOffset(selectType, offset);
		String fetchPhrase = makeFetch(selectType, rowCount);
		String orderByPhrase = makeOrderBy(selectType);
		String searchPhrase = this.searchTerm.indexOf("'") > -1 ? this.searchTerm.replaceAll("'", "''") : this.searchTerm;
		String wherePhrase = selectType.equals(SelectType.COUNTALL) ? baseWhereClause : makeWhereClause(searchPhrase);
		String filterPhrase = makeFilterPhrase(wherePhrase);
		
		if ( this.logger == null ) {
			this.logger = LogManager.getLogger(LookupQuery.class);
		}
		this.logger.log(Level.DEBUG, "wherePhrase: " + wherePhrase);
		this.logger.log(Level.DEBUG, "filterPhrase: " + filterPhrase);
		this.logger.log(Level.DEBUG, "orderByPhrase: " + orderByPhrase);
		this.logger.log(Level.DEBUG, "fetchPhrase: " + fetchPhrase);
		
		String sql = searchSQL + " \n " + wherePhrase + " " + filterPhrase + " " + orderByPhrase + " " + offsetPhrase + " " + fetchPhrase;
		this.logger.log(Level.DEBUG, sql);
				
		return sql;
	}
	
	


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
	
	
	/**
	 * 
	 * @param selectType
	 * @param rowCount Number of rows to return. -1 indicates "all of them"
	 * @return
	 */
	protected String makeFetch(SelectType selectType, Integer rowCount) {
		return selectType.equals(SelectType.DATA) && rowCount > 0 ? "\n FETCH NEXT " + rowCount + " ROWS ONLY " : "";
	}

	
	protected String makeOffset(SelectType selectType, Integer offset) {
		return selectType.equals(SelectType.DATA) ? "\n OFFSET " + offset + " ROWS " :"";
	}
	
	
	
	protected PreparedStatement makePreparedStatement(Connection conn, SelectType selectType, String searchSQL) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(searchSQL);
//		Logger myLogger = LogManager.getLogger(ExceptionReportQuery.class);
		Logger myLogger = LogManager.getLogger(LookupQuery.class);
		myLogger.log(Level.DEBUG, "SelectType: " + selectType.name());
		myLogger.log(Level.DEBUG, "SearchSQL: " + searchSQL);
		if ( this.baseFilterValue != null && this.baseFilterValue.size() > 0 ) {
			int idx = 1;
			for ( Object o : this.baseFilterValue ) {
				if ( o instanceof Integer ) {
					ps.setInt(idx,(Integer)o);
					this.logger.log(Level.DEBUG, "Index: " + idx + "Integer: " + (Integer)o);
				} else if ( o instanceof String ) {
					ps.setString(idx, (String)o);
					this.logger.log(Level.DEBUG, "Index: " + idx + "String: " + (String)o);
				} else if ( o instanceof java.util.Date) {
					java.util.Date date = (java.util.Date)o;
					ps.setDate(idx, new java.sql.Date(date.getTime()));
					this.logger.log(Level.DEBUG, "Index: " + idx + "Date: " + new java.sql.Date(date.getTime()));
				} else if ( o instanceof java.util.GregorianCalendar) {
					GregorianCalendar date = (GregorianCalendar)o;
					ps.setDate(idx, new java.sql.Date(date.getTime().getTime()));
					this.logger.log(Level.DEBUG, "Index: " + idx + "Date: " + new java.sql.Date(date.getTime().getTime()));
				} else {
					throw new RuntimeException("Add another value to the else for " + o.getClass().getName());
				}
				idx++;
			}
		}
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
	
	
	
	public class FilterTransformer implements Transformer<ColumnFilter, String> {

		@Override
		public String transform(ColumnFilter filter) {
			Method method;
			try {
				method = ColumnFilter.class.getMethod(filter.getComparisonType().getMethodName());
				String value = (String)method.invoke(filter);				
				return value;
			} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	
}
