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
import java.util.Map;

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
 * and returns the following fields:
 * 		id = contact_id
 * 		label = contact name:business phone:mobile phone:emai:fax - to help user select the correct contact
 * 		value = "first_name last_name" - for display
 * 		preferredContactValue = the value of the field indicated by the preferredContact field - additional display value
 * 
 * The url for get will be one of:
 * 		/contactSearch?term=					(returns all records)
 * 		/contactSearch?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return 404 Not Found if there is no "term=" found.
 * 
 * @author gagroce
 *
 */
public class ContactTypeAheadServlet extends AbstractServlet {

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
		String url = request.getRequestURI();
		System.out.println("ContactTypeAheadServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/contactTypeAhead/");
		if ( idx > -1 ) {
			super.sendNotFound(response);
		} else {
			Connection conn = null;
			String qs = request.getQueryString();
			System.out.println("ContactTypeAheadServlet(): doGet(): qs =" + qs);
			String term = "";
			if ( StringUtils.isBlank(qs)) { // No query string
				super.sendNotFound(response);
			} else {
				idx = qs.indexOf("term="); 
				if ( idx > -1 ) { // There is a search term "term="
					Map<String, String> map = AppUtils.getQueryMap(qs);
					String queryTerm = map.get("term");
					System.out.println("ContactTypeAheadServlet(): doGet(): map =" + map);
					System.out.println("ContactTypeAheadServlet(): doGet(): term =" + queryTerm);
					if ( ! StringUtils.isBlank(queryTerm)) { // There is a term
						queryTerm = URLDecoder.decode(queryTerm, "UTF-8");
						queryTerm = StringUtils.trimToNull(queryTerm);
						if ( ! StringUtils.isBlank(queryTerm)) {
							term = queryTerm.toLowerCase();
						}
					}
					try {
						conn = AppUtils.getDBCPConn();
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
				} else { // There is no term "term="
					super.sendNotFound(response);
				}

			}

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
				this.preferredContactValue = preferredContact+":unexpected preference";
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
