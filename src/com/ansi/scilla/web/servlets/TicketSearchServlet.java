package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

//import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.queries.TicketSearch;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
//import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.request.TicketSearchRequest;
//import com.ansi.scilla.web.response.code.CodeResponse;
import com.ansi.scilla.web.response.ticketSearch.TicketSearchListResponse;
import com.ansi.scilla.web.response.ticketSearch.TicketSearchResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * The url for get will be one of:
 * 		/ticketSearch/List      		(retrieves everything)
 * 		/ticketSearch/<ticketId>		(retrieves a single record)
 *		/ticketSearch?term=				(retrieves everything)
 * 		/ticketSearch?term=<queryTerm>	(retrieves filtered selection)
 * 		/ticketSearch?sort=<sort>,<sort> (retrieve sorted selection)
 * 		/ticketSearch?term=<term>&sort=<sort> (retrieve sorted filtered selection)
 * 
 * @author ggroce
 */
public class TicketSearchServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String url = request.getRequestURI();
		System.out.println("TicketSearchServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/ticketSearch/");
		if ( idx > -1 ) {
			String queryString = request.getQueryString();
			
			System.out.println("TicketSearchServlet(): doGet(): queryString =" + queryString);
			// Figure out what we've got:
			// "myString" is the piece of the URL that we actually care about
			String myString = url.substring(idx + "/ticketSearch/".length());
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];
			if ( StringUtils.isBlank(command)) {
				super.sendNotFound(response);
			} else {
				if(command.equals("list") || StringUtils.isNumeric(command)){
					System.out.println("TicketSearchServlet(): doGet(): processing list");
					Connection conn = null;
					try {
						conn = AppUtils.getDBCPConn();

						TicketSearchListResponse ticketSearchListResponse = doGetWork(conn, myString, queryString);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, ticketSearchListResponse);
					} catch(RecordNotFoundException recordNotFoundEx) {
						super.sendNotFound(response);
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
		} else {
			String queryString = request.getQueryString();
			System.out.println("TicketSearchServlet(): doGet(): queryString =" + queryString);
			Connection conn = null;
			System.out.println("TicketSearchServlet(): doGet(): queryString =" + queryString);

			try {
				conn = AppUtils.getDBCPConn();
				TicketSearchListResponse ticketSearchListQueryResponse = doGetWork(conn, queryString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, ticketSearchListQueryResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		}
	}

	public TicketSearchListResponse doGetWork(Connection conn, String url, String qs) throws RecordNotFoundException, Exception {
		TicketSearchListResponse ticketSearchListResponse = new TicketSearchListResponse();
		String[] x = url.split("/");
		if(x[0].equals("list")){
			System.out.println("TicketSearchServlet(): doGetWork(): processing list");
			ticketSearchListResponse = new TicketSearchListResponse(conn);
		} else if (StringUtils.isNumeric(x[0])) {
			Integer ticketId = Integer.valueOf(x[0]);
			System.out.println("TicketSearchServlet(): doGetWork(): processing ticketId:" + ticketId);
			ticketSearchListResponse = new TicketSearchListResponse(conn, ticketId);
		} else {
			throw new RecordNotFoundException();
		}
		return ticketSearchListResponse;
		
	}
	
	public TicketSearchListResponse doGetWork(Connection conn, String qs) throws RecordNotFoundException, Exception {
		TicketSearchListResponse ticketSearchListResponse = new TicketSearchListResponse();
		String term = "";
		if ( ! StringUtils.isBlank(qs)) {
			int idx = qs.indexOf("term=");
			if ( idx > -1 ) {
				Map<String, String> map = AppUtils.getQueryMap(qs);
				term = map.get("term");
				System.out.println("TicketSearchServlet(): doGetWork(): map =" + map);
				System.out.println("TicketSearchServlet(): doGetWork(): term =" + term);
				if ( StringUtils.isBlank(term)) {
					term = "";
				} else {
					term = URLDecoder.decode(term, "UTF-8");
					term = StringUtils.trimToNull(term);
					if ( StringUtils.isBlank(term)) {
						term = "";
					}
				} 
				term = term.toLowerCase();
				String sort = map.get("sort");
				if (StringUtils.isBlank(sort)){
					ticketSearchListResponse = new TicketSearchListResponse(conn, term);
				} else {
					String[] sortParms = map.get("sort").split(",");
					ticketSearchListResponse = new TicketSearchListResponse(conn, term, sortParms);
				}
			}
		}
		return ticketSearchListResponse;
		
	}
	
}
