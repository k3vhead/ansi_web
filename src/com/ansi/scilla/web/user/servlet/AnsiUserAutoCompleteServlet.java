package com.ansi.scilla.web.user.servlet;

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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.lang.StringUtils;


public class AnsiUserAutoCompleteServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.sendNotAllowed(response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			AppUtils.validateSession(request, Permission.USER_ADMIN_READ);
			
			Logger logger = LogManager.getLogger(this.getClass());
			logger.log(Level.DEBUG, "Query string: " + request.getQueryString());
			logger.log(Level.DEBUG, "TERM: " + request.getParameter("term"));
			
			String term = request.getParameter("term");
			if ( StringUtils.isBlank(term)) {
				super.sendNotFound(response);
			} else {
				conn = AppUtils.getDBCPConn();
				String json = doSearch(conn, term);
				response.setStatus(HttpServletResponse.SC_OK);
				response.setContentType("application/json");
				
				ServletOutputStream o = response.getOutputStream();
				OutputStreamWriter writer = new OutputStreamWriter(o);
				writer.write(json);
				writer.flush();
				writer.close();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}

	private String doSearch(Connection conn, String term) throws SQLException, IOException {
		String sql = "select ansi_user.user_id, concat(ansi_user.first_name, ' ', ansi_user.last_name) as user_name, city, state\n" + 
				"from ansi_user\n" + 
				"where lower(concat(ansi_user.first_name, ' ', ansi_user.last_name)) like '%" + term.toLowerCase() + "%'";
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery(sql);
		List<ReturnItem> resultList = new ArrayList<ReturnItem>();
		while ( rs.next() ) {
			resultList.add(new ReturnItem(rs));
		}
		rs.close();
		
		String json = AppUtils.object2json(resultList);
		
		return json;
	}
	
	
	

	public class ReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer id;
		private String label;
		private String value;
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.id = rs.getInt("user_id");
			this.value = rs.getString("user_name");
			String city = rs.getString("city");
			String state = rs.getString("state");
			if ( StringUtils.isBlank(city) ) {
				if ( StringUtils.isBlank(state)) {
					this.label = this.value;
				} else {
					this.label = this.value + " (" + state + ")";
				}
			} else {
				if ( StringUtils.isBlank(state) ) {
					this.label = this.value + " (" + city + ")";
				} else {
					this.label = this.value + " (" + city + ", " + state + ")";
				}
			}
			
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
