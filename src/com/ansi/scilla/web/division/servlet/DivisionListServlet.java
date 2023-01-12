package com.ansi.scilla.web.division.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.TimeoutException;

/**
 * Returns a list of divisions -- because it's useful sometimes
 * 
 * @author dclewis
 *
 */
public class DivisionListServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request);  // anybody who is logged in is OK
			List<HashMap<String, Object>> divisionList = makeDivisionList(conn);
			super.sendResponse(conn, response, ResponseCode.SUCCESS, new DivisionListResponse(divisionList));
		} catch (TimeoutException e) {
			super.sendForbidden(response);
		} catch ( ServletException | IOException e ) {
			throw e;
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}
	

	private List<HashMap<String, Object>> makeDivisionList(Connection conn) throws SQLException {
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("select division_id, division_nbr, division_code, description, concat(division_nbr,'-',division_code) as div, division_status  from division order by div");
		ResultSetMetaData rsmd = rs.getMetaData();
		List<HashMap<String, Object>> divisionList = new ArrayList<HashMap<String, Object>>();
		while ( rs.next() ) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
				int idx = i+1;
				String key = rsmd.getColumnName(idx);
				Object value = rs.getObject(idx);
				row.put(key, value);
			}
			divisionList.add(row);
		}
		return divisionList;
	}


	public class DivisionListResponse extends MessageResponse {

		private static final long serialVersionUID = 1L;
		private List<HashMap<String, Object>> divisionList;
		
		private DivisionListResponse() {
			super();
		}
		public DivisionListResponse(List<HashMap<String, Object>> divisionList) {
			this();
			this.divisionList = divisionList;
		}
		public List<HashMap<String, Object>> getDivisionList() {
			return divisionList;
		}
		public void setDivisionList(List<HashMap<String, Object>> divisionList) {
			this.divisionList = divisionList;
		}
		
		
	}
}
