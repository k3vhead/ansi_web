package com.ansi.scilla.web.servlets;

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
import com.ansi.scilla.web.common.AppUtils;
import com.thewebthing.commons.lang.StringUtils;

/**
 * This url searches the following contact table fields for the search type ahead "term":
 * 		business_phone
 * 		fax
 * 		first_name
 * 		last_name
 * 		mobile_phone
 * 		email
 * and returns these same fields plus:
 * 		id = contact_id
 * 		label = contact name:business phone:mobile phone:emai:fax - to help user select the correct contact
 * 		value = "first_name last_name" - for display
 * 		preferredContactValue = the value of the field indicated by the preferredContact field - additional display value
 * 
 * The url for get will be one of:
 * 		/contactSearch    						(retrieves all records from contact table)
 * 		/contactSearch?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return all records if there is no "term=" is found.
 * 
 * @author gagroce
 *
 */
public class ContactTypeAheadServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			String qs = request.getQueryString();
			System.out.println("ContactTypeAheadServlet(): doGet(): qs =" + qs);
			String term = "";
	        if ( ! StringUtils.isBlank(qs)) {
	            int idx = qs.indexOf("term=");
	            if ( idx > -1 ) {
	                term = qs.substring(idx+"term=".length());
	    			System.out.println("ContactTypeAheadServlet(): doGet(): term =$" + term +"$");
	                idx = term.indexOf("&");
	                if ( idx > -1 ) {
	                    term = term.substring(0, idx);
	        			System.out.println("ContactTypeAheadServlet(): doGet(): term =$" + term +"$");
	                }
	                if ( ! StringUtils.isBlank(term)) {
	                    term = URLDecoder.decode(term, "UTF-8");
	        			System.out.println("ContactTypeAheadServlet(): doGet(): term =$" + term +"$");
	                    term = term.toLowerCase();
	        			System.out.println("ContactTypeAheadServlet(): doGet(): term =$" + term +"$");
	                }
	            }
	        }
			System.out.println("ContactTypeAheadServlet(): doGet(): term =$" + term +"$");
			List<ReturnItem> resultList = new ArrayList<ReturnItem>();
			String sql = "select contact_id, concat(first_name, ' ', last_name) as name, business_phone, mobile_phone, email, fax, preferred_contact "
					+ " from contact where lower(business_phone) like '%" + term + "%'"
					+ " OR lower(fax) like '%" + term + "%'"
					+ " OR lower(concat(first_name,' ',last_name)) like '%" + term + "%'"
					+ " OR lower(concat(last_name,' ',first_name)) like '%" + term + "%'"
					+ " OR lower(concat(last_name,', ',first_name)) like '%" + term + "%'"
					+ " OR lower(mobile_phone) like '%" + term + "%'"
					+ " OR lower(email) like '%" + term + "%'";
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
		private String preferredContactValue;
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.id = rs.getInt("contact_id");
			this.label = rs.getString("name") 
					+ "::" + rs.getString("business_phone")
					+ ":" + rs.getString("mobile_phone")
					+ ":" + rs.getString("email")
					+ ":" + rs.getString("fax");
			this.value = rs.getString("name");
			String preferredContact = rs.getString("preferred_contact");
			if (preferredContact == null) {
				this.preferredContactValue = "no preferred contact";
			} else if (preferredContact.indexOf("mobile_phone") != -1) {
				this.preferredContactValue = "mobile:"+rs.getString("mobile_phone");
			} else if (preferredContact.indexOf("business_phone") != -1) {
				this.preferredContactValue = "business:"+rs.getString("business_phone");
			} else if (preferredContact.indexOf("email") != -1) {
				this.preferredContactValue = "email:"+rs.getString("email");
			} else if (preferredContact.indexOf("fax") != -1) {
				this.preferredContactValue = "fax:"+rs.getString("fax");
			} else {
				this.preferredContactValue = "unexpected preferrence";
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

		public String getPreferredContactValue() {
			return preferredContactValue;
		}

		public void setPreferredContactValue(String preferredContactValue) {
			this.preferredContactValue = preferredContactValue;
		}


		
	}
}
