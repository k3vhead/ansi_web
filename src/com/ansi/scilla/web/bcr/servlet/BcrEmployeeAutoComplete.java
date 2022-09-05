package com.ansi.scilla.web.bcr.servlet;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.response.AbstractAutoCompleteItem;
import com.ansi.scilla.web.common.servlet.AbstractAutoCompleteServlet;
import com.ansi.scilla.web.common.utils.Permission;

public class BcrEmployeeAutoComplete extends AbstractAutoCompleteServlet {

	private static final long serialVersionUID = 1L;

	public BcrEmployeeAutoComplete() {
		super(Permission.CLAIMS_READ);
	}
	
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}



	@Override
	protected List<AbstractAutoCompleteItem> makeResultList(Connection conn, HttpServletRequest request) throws Exception {

		HashMap<String, String> parameterMap = new HashMap<String, String>();
		Enumeration<String> parameterNames = request.getParameterNames();
		while ( parameterNames.hasMoreElements() ) {
			String parameter = parameterNames.nextElement();
			String value = request.getParameter(parameter);
			parameterMap.put( parameter, StringUtils.trimToNull(URLDecoder.decode(value, "UTF-8")));
		}
		
		
		List<AbstractAutoCompleteItem> itemList = new ArrayList<AbstractAutoCompleteItem>();
		
		String term = parameterMap.get("term").toLowerCase();
		String divisionId = parameterMap.get("divisionId");
		String sql = "select distinct employee_name \n" + 
				"from ticket_claim \n" + 
				"inner join ticket on ticket.ticket_id = ticket_claim.ticket_id\n" + 
				"inner join division on division.division_id = ticket.act_division_id and ticket.act_division_id = ?\n" + 
				"where lower(employee_name) like ?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, Integer.valueOf(divisionId));
		ps.setString(2, "%" + term.toLowerCase() + "%");
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			itemList.add(new EmployeeName(rs) );
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

	
	
	public class EmployeeName extends AbstractAutoCompleteItem {

		private static final long serialVersionUID = 1L;

		public EmployeeName(ResultSet rs) throws Exception {
			super(rs);
		}

		@Override
		protected void make(ResultSet rs) throws Exception {
			this.label = rs.getString("employee_name");
			this.value = rs.getString("employee_name");			
		}
		
	}
}
