package com.ansi.scilla.web.user.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;

/**
 * Return list of all users in a division based on ansi_user, division_user, division tables
 * 
 * @author dclewis
 *
 */
public class DivisionUserLookup extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	public static final String sqlSelect = 
			"SELECT ansi_user.user_id, "
			+ "ansi_user.user_status, "
			+ "ansi_user.first_name, "
			+ "ansi_user.last_name, "
			+ "ansi_user.title, "
			+ "ansi_user.email, "
			+ "ansi_user.phone, "
			+ "ansi_user.city, "
			+ "ansi_user.state, "
			+ "ansi_user.permission_group_id, "
			+ "ansi_user.address1, "
			+ "ansi_user.address2, "
			+ "ansi_user.zip, "
			+ "ansi_user.minimum_hourly_pay, "
			+ "null as permission_group_name";
	public static final String sqlFromClause = 
			"\n FROM division "
			+ "\n INNER JOIN division_user ON division_user.division_id=division.division_id " 
			+ "\n INNER JOIN ansi_user ON ansi_user.user_id=division_user.user_id" 
			+ "\n $WHERECLAUSE$ "
			+ "\n $ORDERBY$ "
			+ "\n $OFFSET$ "
			+ "\n $FETCH$ ";

	public static final String sql = sqlSelect + sqlFromClause;
	public static final String sqlCount = "select count(*) as record_count " + sqlFromClause;
		
	private Integer divisionId;
	private Integer offset = 0;
	private Integer rowCount = 10;	
	private String	sortBy;
	private Boolean sortIsAscending = true;
	private String searchTerm;
			
	private Logger logger;
	
	public DivisionUserLookup(Integer divisionId) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.divisionId = divisionId;
		this.offset = null;
		this.rowCount = null;
	}
	
	public DivisionUserLookup(Integer divisionId, Integer offset, Integer rowCount) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.divisionId = divisionId;
		this.offset = offset;
		this.rowCount = rowCount;
	}

	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public Integer getOffset() {
		return offset;
	}
	public void setOffset(Integer offset) {
		this.offset = offset;
	}
	public Integer getRowCount() {
		return rowCount;
	}
	public void setRowCount(Integer rowCount) {
		this.rowCount = rowCount;
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
	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}

	
	protected String makeSQL(SelectType selectType) throws Exception {
		String searchSQL =	selectType.equals(SelectType.DATA) ? sql : sqlCount;

		String offsetPhrase = makeOffset(selectType);
		String fetchPhrase = makeFetch(selectType);

		String wherePhrase = "";
		String orderBy = makeOrderBy(selectType);

//		if (! selectType.equals(SelectType.COUNTALL)) {
			wherePhrase = StringUtils.isBlank(searchTerm) && this.divisionId == null ? "" : "\n\twhere " + generateWhereClause(); 			
//		}

		searchSQL = searchSQL.replaceAll("\\$ORDERBY\\$", orderBy);
		searchSQL = searchSQL.replaceAll("\\$WHERECLAUSE\\$", wherePhrase);
		searchSQL = searchSQL.replaceAll("\\$OFFSET\\$", offsetPhrase);
		searchSQL = searchSQL.replaceAll("\\$FETCH\\$", fetchPhrase);
		
		logger.log(Level.DEBUG, "Select type: " + selectType);
		logger.log(Level.DEBUG, searchSQL);
		
		return searchSQL;
	}


	private String makeOrderBy(SelectType selectType) {
		String orderBy = "";
		if ( selectType.equals(SelectType.DATA)) {
			if ( StringUtils.isBlank(sortBy)) {
				orderBy = "order by ansi_user." + User.LAST_NAME + " asc, ansi_user." + User.FIRST_NAME + " asc ";
			} else {
				orderBy = "order by " + sortBy;
				orderBy = sortIsAscending ? orderBy + " asc " : orderBy + " desc ";
			}
		}

		return orderBy;
	}

	private String makeFetch(SelectType selectType) {
		String fetchPhrase = "";
		if ( rowCount != null ) {
			fetchPhrase = selectType.equals(SelectType.DATA) ? "FETCH NEXT " + rowCount + " ROWS ONLY" : "";
		}
		return fetchPhrase;
	}

	private String makeOffset(SelectType selectType) {
		String offsetPhrase = "";
		if ( offset != null ) {
			offsetPhrase = selectType.equals(SelectType.DATA) ? "OFFSET " + offset + " ROWS" :"";
		}
		return offsetPhrase;
	}


	
	
	/** ******* **/
	protected String generateWhereClause() throws Exception {
		List<String> whereClause = new ArrayList<String>();
		String filterClause = "";
		String permissionClause = "";
		if (! StringUtils.isBlank(this.searchTerm)) {
			Collection<String> stringFields= Arrays.asList(new String[] {
				"ansi_user.user_status",
				"ansi_user.first_name",
				"ansi_user.last_name",
				"ansi_user.title",
				"ansi_user.email",
				"ansi_user.phone",
				"ansi_user.city",
				"ansi_user.state"
			});
			
			
			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields, new WhereFieldLikeTransformer(this.searchTerm));
			filterClause = "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
			whereClause.add(filterClause);

		}
		if ( this.divisionId != null ) {
			permissionClause = "(division.division_id=?)";
			whereClause.add(permissionClause);
		}
		
		
		return StringUtils.join(whereClause, " and ");
	}
	
	/** ******* **/
	
	private PreparedStatement makePreparedStatement(Connection conn, SelectType selectType, String searchSQL) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(searchSQL);
//		if (selectType != SelectType.COUNTALL) {
			int idx = 1;  // because JDBC is not zero-based
//			ps.setInt(idx, userId);
//			idx++;
			if ( this.divisionId != null ) {
				ps.setInt(idx, this.divisionId);
			}
//		}
		return ps;
	}



	/**
	 * Returns data, based on stuff that has been entered into the object
	 * @param conn
	 * @param offset
	 * @param rowCount
	 * @return
	 */
	public List<UserLookupItem> select(Connection conn) throws Exception {
		SelectType selectType = SelectType.DATA;
		List<UserLookupItem> items = new ArrayList<UserLookupItem>();
		String searchSQL = makeSQL(selectType);
		
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			items.add(new UserLookupItem(rs, rsmd));
		}
		rs.close();
		return items;
	}
	
	public Integer selectCount(Connection conn) throws Exception {
		SelectType selectType = SelectType.COUNT;
		Integer returnCount = 0;
		String searchSQL = makeSQL(selectType);

		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			returnCount = rs.getInt("record_count");
		}
		rs.close();
		return returnCount;
	}
	
	public Integer countAll(Connection conn) throws Exception {
		SelectType selectType = SelectType.COUNTALL;
		Integer returnCount = 0;
		String searchSQL = makeSQL(selectType);

		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
//		ps.setInt(1, this.userId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			returnCount = rs.getInt("record_count");
		}
		rs.close();
		return returnCount;
	}
	
	public enum SelectType {
		DATA,
		COUNT,
		COUNTALL
	}

	
}
