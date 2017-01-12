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
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.queries.QuoteSearch;
import com.ansi.scilla.common.exceptions.DuplicateEntryException;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.MessageKey;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
//import com.ansi.scilla.web.request.CodeRequest;
import com.ansi.scilla.web.request.QuoteSearchRequest;
//import com.ansi.scilla.web.response.code.CodeResponse;
import com.ansi.scilla.web.response.quoteSearch.QuoteSearchListResponse;
import com.ansi.scilla.web.response.quoteSearch.QuoteSearchResponse;
import com.ansi.scilla.web.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * The url for delete will be of the form /division/<table>/<field>/<value>
 * 
 * The url for get will be one of:
 * 		/division    (retrieves everything)
 * 		/division/<table>      (filters division table by tablename)
 * 		/division/<table>/<field>	(filters division table tablename and field
 * 		/division/<table>/<field>/<value>	(retrieves a single record)
 * 
 * The url for adding a new record will be a POST to:
 * 		/division/new   with parameters in the JSON
 * 
 * The url for update will be a POST to:
 * 		/division/<table>/<field>/<value> with parameters in the JSON
 * 
 * 
 * @author dclewis
 * editer: jwlewis
 */
public class QuoteSearchServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		System.out.println("QuoteSearchServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/quoteSearch/");
		if ( idx > -1 ) {
			String queryString = request.getQueryString();
			
			System.out.println("QuoteSearchServlet(): doGet(): queryString =" + queryString);
			// Figure out what we've got:
			// "myString" is the piece of the URL that we actually care about
			String myString = url.substring(idx + "/quoteSearch/".length());
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			Connection conn = null;
			try {
				if ( StringUtils.isBlank(command)) {
					throw new RecordNotFoundException();
				}
				conn = AppUtils.getDBCPConn();

				QuoteSearchListResponse quoteSearchListResponse = doGetWork(conn, myString, queryString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteSearchListResponse);
			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}

		} else {
			String queryString = request.getQueryString();
			System.out.println("QuoteSearchServlet(): doGet(): queryString =" + queryString);
			Connection conn = null;
			try {
				if ( StringUtils.isBlank(queryString)) {
					throw new RecordNotFoundException();
				}
				conn = AppUtils.getDBCPConn();

				QuoteSearchListResponse quoteSearchListQueryResponse = doGetWork(conn, queryString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteSearchListQueryResponse);
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

	public QuoteSearchListResponse doGetWork(Connection conn, String url, String qs) throws RecordNotFoundException, Exception {
		QuoteSearchListResponse quoteSearchListResponse = new QuoteSearchListResponse();
		String[] x = url.split("/");
		if(x[0].equals("list")){
			quoteSearchListResponse = new QuoteSearchListResponse(conn);
		} else if (StringUtils.isNumeric(x[0])) {
			Integer quoteId = Integer.valueOf(x[0]);
			quoteSearchListResponse = new QuoteSearchListResponse(conn, quoteId);
		} else {
			throw new RecordNotFoundException();
		}
		return quoteSearchListResponse;
		
	}
	
	public QuoteSearchListResponse doGetWork(Connection conn, String qs) throws RecordNotFoundException, Exception {
		QuoteSearchListResponse quoteSearchListResponse = new QuoteSearchListResponse();
		String term = "";
		if ( ! StringUtils.isBlank(qs)) {
			Map<String, String> map = AppUtils.getQueryMap(qs);
			term = map.get("term");
			System.out.println("QuoteSearchServlet(): doGetWork(): map =" + map);
			System.out.println("QuoteSearchServlet(): doGetWork(): term =" + term);
			if ( ! StringUtils.isBlank(term)) {
				term = URLDecoder.decode(term, "UTF-8");
				term = StringUtils.trimToNull(term);
				term = term.toLowerCase();
				String sort = map.get("sort");
				if (! StringUtils.isBlank(sort)){
					String[] sortParms = map.get("sort").split(",");
					quoteSearchListResponse = new QuoteSearchListResponse(conn, term, sortParms);
				} else {
					quoteSearchListResponse = new QuoteSearchListResponse(conn, term);
				}
			} else {
				throw new RecordNotFoundException();
			}
		} else {
			throw new RecordNotFoundException();
		}
		return quoteSearchListResponse;
		
	}
	
}
