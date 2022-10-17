package com.ansi.scilla.web.claims.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.SelectType;

public class NonDirectLaborLookupQuery extends ReportQuery {

	private static final long serialVersionUID = 1L;

	private String sortBy;
	private Boolean sortIsAscending = true;
	private Integer userId;
	private String searchTerm;
	private Logger logger;
	
	
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
	
	
	private static final String sql = sqlSelectClause + sqlFromClause;
	private static final String sqlCount = "select count(*) as record_count " + sqlFromClause;
	
	
	public NonDirectLaborLookupQuery(Integer userId) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.userId = userId;		
	}

	public NonDirectLaborLookupQuery(Integer userId, String searchTerm) {
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

	
	
	
	public List<NonDirectLaborSearchResult> select(Connection conn, Integer offset, Integer rowCount) throws Exception {
		SelectType selectType = SelectType.DATA;
		List<NonDirectLaborSearchResult> resultList = new ArrayList<NonDirectLaborSearchResult>();
		String searchSQL = makeSQL(selectType, offset, rowCount);
		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			resultList.add(new NonDirectLaborSearchResult(rs));
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
		String wherePhrase = selectType.equals(SelectType.COUNTALL) ? NonDirectLaborLookupQuery.baseWhereClause : makeWhereClause(this.searchTerm);
		
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
		String whereClause = NonDirectLaborLookupQuery.baseWhereClause;
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
