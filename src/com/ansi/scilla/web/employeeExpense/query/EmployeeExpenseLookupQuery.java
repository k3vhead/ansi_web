package com.ansi.scilla.web.employeeExpense.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.WhereFieldLikeTransformer;
import com.ansi.scilla.web.employeeExpense.response.EmployeeExpenseResponseItem;

/**
 * Return list of all employee expense. The join with user is left outer in case
 * a user gets deleted. (Shouldn't happen because of foreign key in the expense
 * table)
 * 
 * @author dclewis
 *
 */
public class EmployeeExpenseLookupQuery extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	public static final String sqlSelect = "SELECT employee_expense.expense_id, " + " employee_expense.washer_id, "
			+ " employee_expense.work_date, " + " employee_expense.expense_type, " + " employee_expense.amount, "
			+ " employee_expense.detail, " + " employee_expense.notes, " + " ansi_user.first_name, "
			+ " ansi_user.last_name ";

	public static final String sqlFromClause = "\n from employee_expense "
			+ "\n left outer join ansi_user on ansi_user.user_id=employee_expense.washer_id " + "\n $WHERECLAUSE$ "
			+ "\n $ORDERBY$ " + "\n $OFFSET$ " + "\n $FETCH$ ";

	public static final String sql = sqlSelect + sqlFromClause;
	public static final String sqlCount = "select count(*) as record_count " + sqlFromClause;

	private Integer offset = 0;
	private Integer rowCount = 10;
	private String sortBy;
	private Boolean sortIsAscending = true;
	private String searchTerm;

	private Logger logger;

	public EmployeeExpenseLookupQuery() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.offset = null;
		this.rowCount = null;
	}

	public EmployeeExpenseLookupQuery(Integer offset, Integer rowCount) {
		super();
		this.logger = LogManager.getLogger(this.getClass());
		this.offset = offset;
		this.rowCount = rowCount;
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
		String searchSQL = selectType.equals(SelectType.DATA) ? sql : sqlCount;

		String offsetPhrase = makeOffset(selectType);
		String fetchPhrase = makeFetch(selectType);

		String wherePhrase = "";
		String orderBy = makeOrderBy(selectType);

		if (!selectType.equals(SelectType.COUNTALL)) {
			wherePhrase = StringUtils.isBlank(searchTerm) ? "" : "\n\twhere " + generateWhereClause();
		}

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
		if (selectType.equals(SelectType.DATA)) {
			if (StringUtils.isBlank(sortBy)) {
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
		if (rowCount != null) {
			fetchPhrase = selectType.equals(SelectType.DATA) ? "FETCH NEXT " + rowCount + " ROWS ONLY" : "";
		}
		return fetchPhrase;
	}

	private String makeOffset(SelectType selectType) {
		String offsetPhrase = "";
		if (offset != null) {
			offsetPhrase = selectType.equals(SelectType.DATA) ? "OFFSET " + offset + " ROWS" : "";
		}
		return offsetPhrase;
	}

	protected String generateWhereClause() throws Exception {
		List<String> whereClause = new ArrayList<String>();
		String filterClause = "";
		// String permissionClause = "";
		if (!StringUtils.isBlank(this.searchTerm)) {
			Collection<String> stringFields = Arrays.asList(new String[] { "ansi_user.first_name",
					"ansi_user.last_name", "employee_expense.amount", "employee_expense.work_date",
					"employee_expense.expense_type", "employee_expense.detail", "employee_expense.notes", });

			List<String> whereFields = new ArrayList<String>();
			whereFields.addAll(stringFields);

			Collection<?> whereClauseList = CollectionUtils.collect(whereFields.iterator(), new WhereFieldLikeTransformer(this.searchTerm));
			filterClause = "(" + StringUtils.join(whereClauseList, " \n\tOR ") + ")";
			whereClause.add(filterClause);

		}

		return StringUtils.join(whereClause, " and ");
	}

	/** ******* **/

	private PreparedStatement makePreparedStatement(Connection conn, SelectType selectType, String searchSQL)
			throws SQLException {
		PreparedStatement ps = conn.prepareStatement(searchSQL);
		return ps;
	}

	/**
	 * Returns data, based on stuff that has been entered into the object
	 * 
	 * @param conn
	 * @return
	 */
	public List<EmployeeExpenseResponseItem> select(Connection conn) throws Exception {
		SelectType selectType = SelectType.DATA;
		List<EmployeeExpenseResponseItem> items = new ArrayList<EmployeeExpenseResponseItem>();
		String searchSQL = makeSQL(selectType);

		PreparedStatement ps = makePreparedStatement(conn, selectType, searchSQL);
		ResultSet rs = ps.executeQuery();
		// ResultSetMetaData rsmd = rs.getMetaData();
		while (rs.next()) {
			EmployeeExpenseResponseItem item = new EmployeeExpenseResponseItem();

			java.sql.Date workDate = rs.getDate("work_date");
			item.setAmount(rs.getBigDecimal("amount"));
			item.setDate(new Date(workDate.getTime()));
			item.setExpenseId(rs.getInt("expense_id"));
			item.setExpenseType(rs.getString("expense_type"));
			item.setNotes(rs.getString("notes"));
			item.setFirstName(rs.getString("first_name"));

			item.setLastName(rs.getString("last_name"));			

			items.add(item);
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
		if (rs.next()) {
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
		// ps.setInt(1, this.userId);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			returnCount = rs.getInt("record_count");
		}
		rs.close();
		return returnCount;
	}

	public enum SelectType {
		DATA, COUNT, COUNTALL
	}

	/*
	 * public static void main(String[] args) { Connection conn = null; try {
	 * conn = AppUtils.getDevConn(); EmployeeExpenseLookupQuery query = new
	 * EmployeeExpenseLookupQuery(10,10); List<EmployeeExpenseResponseItem>
	 * items = query.select(conn); Integer count = query.selectCount(conn);
	 * Integer countAll = query.countAll(conn); query.setSearchTerm("xyz");
	 * query.select(conn); query.selectCount(conn); query.countAll(conn); }
	 * catch (Exception e) { e.printStackTrace(); } finally {
	 * AppUtils.closeQuiet(conn); } }
	 */

}
