package com.ansi.scilla.web.test.employeeLookup;

import java.sql.Connection;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.employeeExpense.query.EmployeeExpenseLookupQuery;
import com.ansi.scilla.web.employeeExpense.response.EmployeeExpenseLookupResponse;
import com.ansi.scilla.web.employeeExpense.response.EmployeeExpenseResponseItem;

public class TestEmployeeLookup {

	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			doTest(conn);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	
	private void doTest(Connection conn) throws Exception {
		String term = null;
		String dir = "asc";
		Logger logger = LogManager.getLogger(this.getClass());
		String colName = "work_date";
		int draw = 1;
		
		EmployeeExpenseLookupQuery lookup = new EmployeeExpenseLookupQuery();
		lookup.setSearchTerm(term);
		lookup.setSortBy(colName);
		lookup.setSortIsAscending(dir.equals("asc"));
		List<EmployeeExpenseResponseItem> itemList = lookup.select(conn);
		logger.log(Level.DEBUG, "Records: " + itemList.size());
		Integer filteredCount = lookup.selectCount(conn);
		Integer totalCount = lookup.countAll(conn);
		
//		response.setStatus(HttpServletResponse.SC_OK);
//		response.setContentType("application/json");

		EmployeeExpenseLookupResponse jsonResponse = new EmployeeExpenseLookupResponse();
		jsonResponse.setRecordsFiltered(filteredCount);
		jsonResponse.setRecordsTotal(totalCount);
		jsonResponse.setData(itemList);
		jsonResponse.setDraw(draw);

		String json = AppUtils.object2json(jsonResponse);	
		System.out.println(json);
	}

	public static void main(String[] args) {
		try {
			new TestEmployeeLookup().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
