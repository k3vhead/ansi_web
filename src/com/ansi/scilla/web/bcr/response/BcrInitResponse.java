package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.bcr.common.BcrUtils;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.division.response.DivisionCountRecord;

public class BcrInitResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private List<DivisionCountRecord> divisionList;
	private List<DisplayMonth> displayMonthList;
	private List<ExpenseType> expenseTypeList;

	
	public BcrInitResponse() {
		super();
	}
	
	public BcrInitResponse(Connection conn, List<DivisionCountRecord> divisionList, Calendar workDay) throws SQLException {
		this();
		this.divisionList = divisionList;
		this.displayMonthList = makeDisplayYear(workDay);
		this.expenseTypeList = makeExpenseTypeList(conn);
		
	}

	public List<DivisionCountRecord> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<DivisionCountRecord> divisionList) {
		this.divisionList = divisionList;
	}

	public List<DisplayMonth> getDisplayMonthList() {
		return displayMonthList;
	}

	public void setDisplayMonthList(List<DisplayMonth> displayMonthList) {
		this.displayMonthList = displayMonthList;
	}

	public List<ExpenseType> getExpenseTypeList() {
		return expenseTypeList;
	}

	public void setExpenseTypeList(List<ExpenseType> expenseTypeList) {
		this.expenseTypeList = expenseTypeList;
	}

	private List<DisplayMonth> makeDisplayYear(Calendar workDay) {
		return BcrUtils.makeDisplayYear(workDay);
	}

	private List<ExpenseType> makeExpenseTypeList(Connection conn) throws SQLException {
		List<ExpenseType> expenseTypeList = new ArrayList<ExpenseType>();
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("select value, display_value from code where table_name='ticket_claim_passthru' and field_name='passthru_expense_type' order by seq, display_value");
		while (rs.next()) {
			expenseTypeList.add(new ExpenseType(rs));
		}
		rs.close();
		return expenseTypeList;
	}

	public class ExpenseType extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private String value;
		private String displayValue;
		public ExpenseType(ResultSet rs) throws SQLException {
			super();
			this.value = rs.getString("value");
			this.displayValue = rs.getString("display_value");
		}
		public String getValue() {
			return value;
		}
		public String getDisplayValue() {
			return displayValue;
		}		
	}
	
}
