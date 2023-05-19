package com.ansi.scilla.web.payroll.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.response.AbstractAutoCompleteItem;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteServlet;

public class EmployeeAutoCompleteServlet extends AbstractAutoCompleteServlet {

	private static final long serialVersionUID = 1L;

	public EmployeeAutoCompleteServlet() {
		super(Permission.PAYROLL_READ);
	}
	@Override
	protected List<AbstractAutoCompleteItem> makeResultList(Connection conn, HttpServletRequest request)
			throws Exception {
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		List<AbstractAutoCompleteItem> itemList = new ArrayList<AbstractAutoCompleteItem>();
		
		String term = parameterMap.get("term")[0].toLowerCase();
		String divisionId = parameterMap.get("divisionId")[0];
		String sql = "select payroll_employee.employee_code, payroll_employee.vendor_employee_code, payroll_employee.division_id, payroll_employee.employee_first_name, \n"
				+ "	payroll_employee.employee_last_name, payroll_employee.employee_mi\n"
				+ "from payroll_employee\n"
				+ "where  payroll_employee.division_id in (select division_id from division where division.group_id=(select group_id from division where division_id=?))\n"
				+ "and (\n"
				+ "	lower(concat(employee_first_name,' ', employee_mi,' ', employee_last_name)) like ? or\n"
				+ "	lower( concat(employee_first_name, ' ', employee_last_name) ) like ? or \n"
				+ "	lower( concat(employee_last_name,', ', employee_first_name,' ',employee_mi) ) like ?\n"
				+ ")";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, Integer.valueOf(divisionId));
		ps.setString(2, "%" + term.toLowerCase() + "%");
		ps.setString(3, "%" + term.toLowerCase() + "%");
		ps.setString(4, "%" + term.toLowerCase() + "%");
		
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			itemList.add(new PayrollEmployee(rs) );
		}
		rs.close();
		
		/**
		 * We sort this way because the database won't let us sort in the query:
		 * com.microsoft.sqlserver.jdbc.SQLServerException: ORDER BY items must appear in the select list if SELECT DISTINCT is specified
		 */
		Collections.sort(itemList, new Comparator<AbstractAutoCompleteItem>() {
			public int compare(AbstractAutoCompleteItem o1, AbstractAutoCompleteItem o2) {				
				return o1.getValue().compareTo(o2.getValue());
			}
		});
		return itemList;
	}

	
	
	
	public class PayrollEmployee extends AbstractAutoCompleteItem {

		private static final long serialVersionUID = 1L;
		private String vendorEmployeeCode;

		public PayrollEmployee(ResultSet rs) throws Exception {
			super(rs);
		}
		
		public String getVendorEmployeeCode() {
			return vendorEmployeeCode;
		}

		public void setVendorEmployeeCode(String vendorEmployeeCode) {
			this.vendorEmployeeCode = vendorEmployeeCode;
		}


		@Override
		protected void make(ResultSet rs) throws Exception {
			String fName = rs.getString("employee_first_name");
			String mi = rs.getString("employee_mi");
			String lName = rs.getString("employee_last_name");
			String employeeName = fName + (StringUtils.isBlank(mi) ? "" : " " + mi) + " " + lName;
			this.vendorEmployeeCode = rs.getString("vendor_employee_code");
			this.id = rs.getInt("employee_code");
			this.label = employeeName;
			this.value = employeeName;
		}
		
	}
	
	
	
}
