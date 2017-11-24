package com.ansi.scilla.web.address.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
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
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.lang.StringUtils;

/**
 * This url searches the following contact table fields for the search term:
 * 		name
 * 		address1
 * 		address2
 * 		city
 * 		country
 * 		state
 * 		zip
 * 		country_code
 * 
 * The url for get will be one of:
 * 		/addressSearch    						(retrieves all records from address table)
 * 		/addressSearch?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return all records if there is no "term=" is found.
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 *
 */
public class AddressSearchServlet extends AbstractServlet {

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
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			String qs = request.getQueryString();

			String term = "";
			if (qs != null) {
				if ( qs.indexOf("term=") != -1) {
					term = StringUtils.trimToNull(URLDecoder.decode(qs.substring("term=".length()),"UTF-8"));
				}
			}

			List<ReturnItem> resultList = new ArrayList<ReturnItem>();
			String sql = "select address_id, name, status, address1, address2, city, county, state, zip, country_code"
					+ " from address where lower(name) like '%" + term + "%'"
					+ " OR lower(address1) like '%" + term + "%'"
					+ " OR lower(address2) like '%" + term + "%'"
					+ " OR lower(city) like '%" + term + "%'"
					+ " OR lower(county) like '%" + term + "%'"
					+ " OR lower(state) like '%" + term + "%'"
					+ " OR lower(zip) like '%" + term + "%'"
					+ " OR lower(country_code) like '%" + term + "%'";
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
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	public class ReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer address_id;
		private String name;
		private String status;
		private String address1;
		private String address2;
		private String city;
		private String county;
		private String state;
		private String zip;
		private String country_code;
	
		
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.address_id = rs.getInt("address_id");
			this.name = rs.getString("name");
			this.status = rs.getString("status");
			this.address1 = rs.getString("address1");
			this.address2 = rs.getString("address2");
			this.county = rs.getString("county");
			this.state = rs.getString("state");
			this.zip = rs.getString("zip");
			this.country_code = rs.getString("country_code");

		}

		public Integer getAddressId() {
			return address_id;
		}

		public void setAddressId(Integer address_id) {
			this.address_id = address_id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getAddress1() {
			return address1;
		}

		public void setAddress1(String address1) {
			this.address1 = address1;
		}

		public String getAddress2() {
			return address2;
		}

		public void setAddress2(String address2) {
			this.address2 = address2;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}
		
		public String getCounty() {
			return county;
		}

		public void setCounty(String county) {
			this.county = county;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
		public String getZip() {
			return zip;
		}

		public void setZip(String zip) {
			this.zip = zip;
		}
		public String getCountryCode() {
			return country_code;
		}

		public void setCountryCode(String country_code) {
			this.country_code = country_code;
		}

	}
}
