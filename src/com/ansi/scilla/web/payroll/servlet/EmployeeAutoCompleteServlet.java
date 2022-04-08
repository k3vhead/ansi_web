package com.ansi.scilla.web.payroll.servlet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.response.AbstractAutoCompleteItem;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.Permission;

public class EmployeeAutoCompleteServlet extends AbstractAutoCompleteServlet {

	private static final long serialVersionUID = 1L;

	public EmployeeAutoCompleteServlet() {
		super(Permission.PAYROLL_READ);
	}
	@Override
	protected List<AbstractAutoCompleteItem> makeResultList(Connection conn, SessionUser user, HashMap<String, String> parameterMap) throws Exception {
		
		List<AbstractAutoCompleteItem> itemList = new ArrayList<AbstractAutoCompleteItem>();
		
		String term = parameterMap.get("term").toLowerCase();
		String sql = "select employee_code, division_id, employee_first_name, employee_last_name, employee_mi \n" + 
				"from payroll_employee\n" + 
				"where  lower(concat(employee_first_name,' ', employee_mi,' ', employee_last_name)) like ? or\n" + 
				"   lower( concat(employee_first_name, ' ', employee_last_name) ) like ? or \n" +
				"	lower( concat(employee_last_name,', ', employee_first_name,' ',employee_mi) ) like ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setString(1, "%" + term.toLowerCase() + "%");
		ps.setString(2, "%" + term.toLowerCase() + "%");
		ps.setString(3, "%" + term.toLowerCase() + "%");
		
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

		public PayrollEmployee(ResultSet rs) throws Exception {
			super(rs);
		}

		@Override
		protected void make(ResultSet rs) throws Exception {
			String fName = rs.getString("employee_first_name");
			String mi = rs.getString("employee_mi");
			String lName = rs.getString("employee_last_name");
			String employeeName = lName + ", " + fName + (StringUtils.isBlank(mi) ? "" : " " + mi);
			this.label = employeeName;
			this.value = employeeName;
			this.id = rs.getInt("employee_code");				
		}
		
	}
}
