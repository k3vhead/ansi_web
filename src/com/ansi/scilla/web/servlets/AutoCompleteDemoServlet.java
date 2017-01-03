package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.AppUtils;

public class AutoCompleteDemoServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String qs = request.getQueryString();
			String term = qs.substring("term=".length());
			System.out.println(qs);
			List<ReturnItem> resultList = new ArrayList<ReturnItem>();
			String sql = "select division_id, division_code, name from division where lower(name) like '%" + term + "%'";
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery(sql);
			while ( rs.next() ) {
				resultList.add(new ReturnItem(rs));
			}
			rs.close();
			
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/json");
			
			String json = AppUtils.object2json(resultList);
			ServletOutputStream o = response.getOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(o);
			writer.write(json);
			writer.flush();
			writer.close();
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public class ReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer id;
		private String label;
		private String value;
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.id = rs.getInt("division_id");
			this.label = rs.getString("name");
			this.value = rs.getString("division_code");
		}

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getLabel() {
			return label;
		}

		public void setLabel(String label) {
			this.label = label;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}



		
	}
}
