package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.DivisionUser;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;

import com.ansi.scilla.web.request.DivisionRequest;
import com.ansi.scilla.web.response.division.DivisionListResponse;
import com.ansi.scilla.web.response.division.DivisionResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * The url for delete will be of the form /code/<table>/<field>/<value>
 * 
 * The url for get will be one of:
 * 		/code    (retrieves everything)
 * 		/code/<table>      (filters code table by tablename)
 * 		/code/<table>/<field>	(filters code table tablename and field
 * 		/code/<table>/<field>/<value>	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/code/new   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/code/<table>/<field>/<value> with parameters in the JSON
 * 
 * 
 * @author dclewis
 *
 */
public class DivisionServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doDelete(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		
		String url = request.getRequestURI();
		int idx = url.indexOf("/division/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				conn.setAutoCommit(false);
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/division/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
					super.sendNotFound(response);
				} else {
					try {
						doDeleteWork(conn, url);
						DivisionResponse divisionResponse = new DivisionResponse();
						super.sendResponse(conn, response, ResponseCode.SUCCESS, divisionResponse);
					} catch(RecordNotFoundException recordNotFoundEx) {
						super.sendNotFound(response);
					}
				}
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response);
		}
		
	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		int idx = url.indexOf("/division/");
		if ( idx > -1 ) {
			System.out.println("Url:" + url);
			String queryString = request.getQueryString();
			System.out.println("Query String: " + queryString);
			
			// we're in the right place
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				
				// Figure out what we've got:				
				String myString = url.substring(idx + "/division/".length());
				
				String[] urlPieces = myString.split("/");
				String command = urlPieces[0];
				
				if ( StringUtils.isBlank(command)) {
					super.sendNotFound(response);
				} else {
						try{
							DivisionListResponse divisionListResponse = doGetWork(conn, url, queryString);
							super.sendResponse(conn, response, ResponseCode.SUCCESS, divisionListResponse);
						} catch(RecordNotFoundException recordNotFoundEx) {
							super.sendNotFound(response);
						}
				}
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
			
		} else {
			super.sendNotFound(response);
		}
	}

	public DivisionListResponse doGetWork(Connection conn, String url, String qs) throws RecordNotFoundException, Exception {
		DivisionListResponse divisionListResponse = new DivisionListResponse();
		String[] x = url.split("/");
		
		if(x[0].equals("list")){
			divisionListResponse = new DivisionListResponse(conn);
		} else if (StringUtils.isNumeric(x[0])){
			Division div = new Division();
			div.setDivisionId(Integer.valueOf(x[0]));
			System.out.println("Hello World: " + x[0]);
			div.selectOne(conn);
			divisionListResponse.setDivisionList(Arrays.asList(new Division[] {div} ));
		} else {
			throw new RecordNotFoundException();
		}
		return divisionListResponse;
		
	}
	
	public void doDeleteWork(Connection conn, String url) throws RecordNotFoundException, Exception {
		
		String[] x = url.split("/");
		
		if (StringUtils.isNumeric(x[0])){
			Division div = new Division();
			div.setDivisionId(Integer.valueOf(x[0]));
			
			try {
				DivisionUser divisionUser = new DivisionUser();
				divisionUser.setDivisionId(Integer.valueOf(x[0]));
				divisionUser.selectOne(conn);
				System.out.println("Hello Delete: " + x[0]);
				System.out.println("Cannot Delete, Users Inside");
			} catch (RecordNotFoundException e) {
				System.out.println("Hello Delete: " + x[0]);
				try {
					div.delete(conn);
					System.out.println("Deleted!");
				} catch(RecordNotFoundException er) {
					System.out.println("Error! Division Not Found!");
				}
			}
			
			
		} else {
			throw new RecordNotFoundException();
		}
		
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		throw new ServletException("Not Yet Coded");
	}

	/*private DivisionListResponse makeDivisionListResponse(Connection conn) throws Exception {
		DivisionListResponse divisionListResponse = new DivisionListResponse(conn);
		return divisionListResponse;
	}*/

	/*private DivisionListResponse makeFilteredListResponse(Connection conn, String[] urlPieces) throws Exception {
		Integer divisionId = null;
		
		try {
			divisionId = Integer.valueOf(urlPieces[0]);
		} catch (ArrayIndexOutOfBoundsException e) {
			// this is OK, just means we ran out of filters
		} catch (NumberFormatException e) {
			
		}
		Division division = new Division();
		
		division.setDivisionId(divisionId);
		
		
		division.selectOne(conn);
		List<Division> divisionList = new ArrayList<Division>();
		divisionList.add(division);
		DivisionListResponse divisionListResponse = new DivisionListResponse();
		divisionListResponse.setDivisionList(divisionList);
		return divisionListResponse;
	}*/

	
}
