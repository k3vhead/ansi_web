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
import java.util.Map;

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
 * This url searches the following address table fields for the search type ahead "term":
 * 		name
 * 		address 1
 * 		address 2
 * 		city
 * 		zip
 * and returns the following fields:
 * 		id = address_id
 * 		label = name:address 1:address 2:city:state:zip
 * 		value = name
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * The url for get will be one of:
 * 		/addressSearch?term=					(returns null list)
 * 		/addressSearch?term=&lt;searchTerm&gt;	(returns all records containing &lt;searchTerm&gt;)
 * 
 * The servlet will return 404 Not Found if there is no "term=" found.
 * 
 * @author gagroce
 *
 */
public class AddressTypeAheadServlet extends AbstractServlet {

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
		logger.log(Level.DEBUG, "AddressTypeAheadServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/addressTypeAhead/");
		if ( idx > -1 ) {
			super.sendNotFound(response);
		} else {
			Connection conn = null;
			String qs = request.getQueryString();
			logger.log(Level.DEBUG, "AddressTypeAheadServlet(): doGet(): qs =" + qs);
			String term = "";
			if ( StringUtils.isBlank(qs)) { // No query string
				super.sendNotFound(response);
			} else {
				idx = qs.indexOf("term="); 
				if ( idx > -1 ) { // There is a search term "term="
					Map<String, String> map = AppUtils.getQueryMap(qs);
					String queryTerm = map.get("term");
					logger.log(Level.DEBUG, "AddressTypeAheadServlet(): doGet(): map =" + map);
					logger.log(Level.DEBUG, "AddressTypeAheadServlet(): doGet(): term =" + queryTerm);
					if ( ! StringUtils.isBlank(queryTerm)) { // There is a term
						queryTerm = URLDecoder.decode(queryTerm, "UTF-8");
						queryTerm = StringUtils.trimToNull(queryTerm);
						List<ReturnItem> resultList = new ArrayList<ReturnItem>();
						if ( StringUtils.isBlank(queryTerm)) { // blank search term
							response.setStatus(HttpServletResponse.SC_OK);
							response.setContentType("application/json");
							
							String json = AppUtils.object2json(resultList);
							ServletOutputStream o = response.getOutputStream();
							OutputStreamWriter writer = new OutputStreamWriter(o);
							writer.write(json);
							writer.flush();
							writer.close();

						} else {
							term = queryTerm.toLowerCase();
							try {
								conn = AppUtils.getDBCPConn();
								AppUtils.validateSession(request, Permission.ADDRESS_READ);
								logger.log(Level.DEBUG, "AddresTypeAheadServlet(): doGet(): term =$" + term +"$");
								
								String sql = "select address_id, name, address1, address2, city, county, state, zip "
										+ " from address " 
										+ " where lower(name) like '%" + term + "%'"
										+ " OR lower(address1) like '%" + term + "%'"
										+ " OR lower(address2) like '%" + term + "%'"
										+ " OR lower(city) like '%" + term + "%'"
										+ " OR lower(state) like '%" + term + "%'"
										+ " OR lower(county) like '%" + term + "%'"
										+ " OR lower(zip) like '%" + term + "%'"
										+ " ORDER BY name "
										+ " OFFSET 0 ROWS"
										+ " FETCH NEXT 250 ROWS ONLY";
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
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.id = rs.getInt("address_id");
			this.label = rs.getString("name") 
					+ "::" + rs.getString("address1")
					+ ":" + rs.getString("address2")
					+ ":" + rs.getString("city")
					+ ":" + rs.getString("state")
					+ ":" + rs.getString("zip")
					+ ":" + rs.getString("county");
			this.value = rs.getString("name");
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
