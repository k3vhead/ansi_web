package com.ansi.scilla.web.test.payroll;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.payroll.common.EmployeeStatus;
import com.ansi.scilla.web.common.query.LookupQuery;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.payroll.query.ExceptionReportQuery;
import com.ansi.scilla.web.payroll.servlet.ExceptionReportServlet;
import com.ansi.scilla.web.test.common.AbstractTester;

public class TestExceptionReport2 extends AbstractTester {

	
	
	public static void main(String[] args) {
		try {
			new TestExceptionReport2().run();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void go(Connection conn) throws Exception {
		Transformer<HashMap<String, Object>, HashMap<String, Object>> itemTransformer = new ItemTransformer();
		List<SessionDivision> divisionList = super.makeDivisionList(conn);
		Boolean errorsOnly = false;
		LookupQuery lookup = new ExceptionReportQuery(5, divisionList, 12, errorsOnly);
		lookup.setSearchTerm("");
		List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
		ResultSet rs = lookup.select(conn, 0, 100);
		ResultSetMetaData rsmd = rs.getMetaData();
		while ( rs.next() ) {
			dataList.add(makeDataItem(rs, rsmd));
		}
		if ( itemTransformer != null ) {
			CollectionUtils.transform(dataList, itemTransformer);
		}
		
//		String json = AppUtils.object2json(dataList);
//		System.out.println(json);
		for ( HashMap<String, Object> row : dataList ) {
			System.out.print( row.get("employee_name") + "\t" );
			System.out.print( row.get("employee_code") + "\t");
			System.out.print( row.get("division_id") + "\t");
			System.out.print( row.get("week_ending_display") + "\t");
			System.out.println( row.get(ExceptionReportServlet.ROW_ID));
		}
	}

	
	protected HashMap<String, Object> makeDataItem(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		HashMap<String, Object> dataItem = new HashMap<String, Object>();
		for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
			int idx = i + 1;
			String column = rsmd.getColumnName(idx);
			Object value = rs.getObject(idx);
			
			dataItem.put(column, value);
		}
		return dataItem;
	}
	
	
	public class ItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

		@Override
		public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");

			java.sql.Date terminationDate = (java.sql.Date)arg0.get(ExceptionReportServlet.TERMINATION_DATE);			
			String formattedDate = terminationDate == null ? null : sdf.format(terminationDate);
			arg0.put(ExceptionReportServlet.FORMATTED_TERMINATION_DATE, formattedDate);

			if ( arg0.containsKey(ExceptionReportServlet.EMPLOYEE_STATUS) && arg0.get(ExceptionReportServlet.EMPLOYEE_STATUS) != null ) {
				EmployeeStatus employeeStatus = EmployeeStatus.valueOf((String)arg0.get(ExceptionReportServlet.EMPLOYEE_STATUS));
				arg0.put(ExceptionReportServlet.EMPLOYEE_STATUS, employeeStatus.display());
			}
			
			java.sql.Date processDate = (java.sql.Date)arg0.get(ExceptionReportServlet.PROCESS_DATE);
			String formattedProcessDate = processDate == null ? null : sdf.format(processDate);
			arg0.put(ExceptionReportServlet.FORMATTED_PROCESS_DATE, formattedProcessDate);
			
			
			
			Integer employeeCode = (Integer)arg0.get("employee_code");
			Integer divisionId = (Integer)arg0.get("division_id");
			String weekEnding = (String)arg0.get("week_ending_display");

			System.out.println("====" + String.valueOf(employeeCode) + "\t" + String.valueOf(divisionId) + "\t" + weekEnding + "======");
			
			
			List<String> source = new ArrayList<String>();
			source.add(employeeCode == null ? "" : String.valueOf(employeeCode));
			source.add(divisionId == null ? "" : String.valueOf(divisionId));
			source.add(weekEnding == null ? "" : weekEnding);
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] messageDigest = md.digest(StringUtils.join(source, "").getBytes());
				BigInteger number = new BigInteger(1, messageDigest);
				arg0.put(ExceptionReportServlet.ROW_ID, number.toString(16));
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException(e);
			}
					
			return arg0;
		}

	}
}
