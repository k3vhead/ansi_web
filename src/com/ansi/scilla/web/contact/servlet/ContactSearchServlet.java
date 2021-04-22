package com.ansi.scilla.web.contact.servlet;

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

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.lang.StringUtils;

/**
 * This url searches the following contact table fields for the search term:
 * 		business_phone
 * 		fax
 * 		"first_name last_name"
 * 		"last_name first_name"
 * 		mobile_phone
 * 		email
 * and returns these same fields plus:
 * 		contact_id
 * 		preferred_contact
 * 
 * The url for get will be one of:
 * 		/contactSearch    						(retrieves all records from contact table)
 * 		/contactSearch?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return all records if there is no "term=" is found.
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * @author gagroce
 *
 */
public class ContactSearchServlet extends AbstractServlet {

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
			AppUtils.validateSession(request, Permission.CONTACT_READ);
			String qs = request.getQueryString();
			logger.log(Level.DEBUG, "ContactSearchServlet(): doGet(): qs =" + qs);
			String term = "";
			if (qs != null) {
				if ( qs.indexOf("term=") != -1) {
					term = StringUtils.trimToNull(URLDecoder.decode(qs.substring("term=".length()),"UTF-8"));
				}
			}
			logger.log(Level.DEBUG, "ContactSearchServlet(): doGet(): term =$" + term +"$");
			List<ReturnItem> resultList = new ArrayList<ReturnItem>();
			String sql = "select business_phone, contact_id, fax, first_name, last_name, mobile_phone, preferred_contact, email"
					+ " from contact where lower(business_phone) like '%" + term + "%'"
					+ " OR lower(fax) like '%" + term + "%'"
					+ " OR lower(concat(first_name,' ',last_name)) like '%" + term + "%'"
					+ " OR lower(concat(last_name,' ',first_name)) like '%" + term + "%'"
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
		private String businessPhone;
		private Integer contactId;
		private String fax;
		private String firstName;
		private String lastName;
		private String mobilePhone;
		private String preferredContact;
		private String email;
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.contactId = rs.getInt("contact_id");
			this.businessPhone = rs.getString("business_phone");
			this.fax = rs.getString("fax");
			this.firstName = rs.getString("first_name");
			this.lastName = rs.getString("last_name");
			this.mobilePhone = rs.getString("mobile_phone");
			this.preferredContact = rs.getString("preferred_contact");
			this.email = rs.getString("email");
		}

		public String getBusinessPhone() {
			return businessPhone;
		}

		public void setBusinessPhone(String businessPhone) {
			this.businessPhone = businessPhone;
		}

		public Integer getContactId() {
			return contactId;
		}

		public void setContactId(Integer id) {
			this.contactId = contactId;
		}

		public String getFax() {
			return fax;
		}

		public void setFax(String fax) {
			this.fax = fax;
		}

		public String getFirstName() {
			return firstName;
		}

		public void setFirstName(String firstName) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName(String lastName) {
			this.lastName = lastName;
		}

		public String getMobilePhone() {
			return mobilePhone;
		}

		public void setMobilePhone(String mobilePhone) {
			this.mobilePhone = mobilePhone;
		}

		public String getPreferredContact() {
			return preferredContact;
		}

		public void setPreferredContact(String preferredContact) {
			this.preferredContact = preferredContact;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

	}
}
