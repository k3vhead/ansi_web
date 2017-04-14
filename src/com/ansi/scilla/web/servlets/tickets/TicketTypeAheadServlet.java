package com.ansi.scilla.web.servlets.tickets;

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
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.thewebthing.commons.lang.StringUtils;

/**
 * This url searches the following ticket related fields for the search type ahead "term":
 * 		ticket_id
 * and returns the following fields:
 * 		id = ticket_id
 * 		label = ticket_id:ticket_status:division_code:job_nbr:frequency:act_price_per_cleaning:job site:address 1
 * 		value = ticket_id
 * 
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * The url for get will be one of:
 * 		/ticketTypeAhead?term=					(returns not found)
 * 		/ticketTypeAhead?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return 404 Not Found if there is no "term=" found.
 * 
 * @author gagroce
 *
 */
public class TicketTypeAheadServlet extends AbstractServlet {

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
		System.out.println("TicketTypeAheadServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/ticketTypeAhead/");
		if ( idx > -1 ) {
			super.sendNotFound(response);
		} else {
			Connection conn = null;
			String qs = request.getQueryString();
			System.out.println("TicketTypeAheadServlet(): doGet(): qs =" + qs);
			String term = "";
			if ( StringUtils.isBlank(qs)) { // No query string
				super.sendNotFound(response);
			} else {
				idx = qs.indexOf("term="); 
				if ( idx > -1 ) { // There is a search term "term="
					Map<String, String> map = AppUtils.getQueryMap(qs);
					String queryTerm = map.get("term");
					System.out.println("TicketTypeAheadServlet(): doGet(): map =" + map);
					System.out.println("TicketTypeAheadServlet(): doGet(): term =" + queryTerm);
					if ( StringUtils.isBlank(queryTerm)) { // There is no term
						super.sendNotFound(response);
					} else { // There is a term
						queryTerm = URLDecoder.decode(queryTerm, "UTF-8");
						queryTerm = StringUtils.trimToNull(queryTerm);
						if (StringUtils.isBlank(queryTerm)) { // Search term is blank
							super.sendNotFound(response);
						} else {
							term = queryTerm.toLowerCase();
							try {
								conn = AppUtils.getDBCPConn();
								AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_READ);
								System.out.println("TicketTypeAheadServlet(): doGet(): term =$" + term +"$");
								List<ReturnItem> resultList = new ArrayList<ReturnItem>();
								//ticket_id:ticket_status:division_code:job_nbr:frequency:act_price_per_cleaning:job site:address 1
								String sql = "select ticket_id, ticket_status, division.division_code, job_nbr, job_frequency"
										+ ", act_price_per_cleaning, address.name, address.address1, fleetmatics_id "
										+ " from ticket " 
										+ " join job on job.job_id = ticket.job_id " 
										+ " join quote on quote.quote_id = job.quote_id " 
										+ " join division on division.division_id = ticket.act_division_id " 
										+ " join address on address.address_id = quote.job_site_address_id " 
										+ " where ticket_id like '%" + term + "%'"
										+ " or fleetmatics_id like '%" + term + "%'"
										//+ " and ticket_status in ('N','D')"
										;
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
			this.id = rs.getInt("ticket_id");
			//ticket_id:ticket_status:division_code:job_nbr:frequency:act_price_per_cleaning:job site:address 1
			this.label = rs.getString("ticket_id") 
					+ ":" + "Status " + rs.getString("ticket_status")
					+ ":" + rs.getString("division_code")
					+ ":" + "J# " + rs.getString("job_nbr")
					+ ":" + "Freq " + rs.getString("job_frequency")
					+ ":" + "PPC " + rs.getString("act_price_per_cleaning")
					+ ":" + "FM " + rs.getString("fleetmatics_id")
					+ ":" + "Site " + rs.getString("name")
					+ ":" + rs.getString("address1");
			this.value = rs.getString("ticket_id");
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
