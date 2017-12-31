package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;

import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.response.QuoteSearchListResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;


/**
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * The url for get will be one of:
 * 		/quoteSearch/List      				(retrieves everything)
 * 		/quoteSearch/<quoteId>					(retrieves a single record)
 *		/quoteSearch?term=					(retrieves everything)
 * 		/quoteSearch?term=<queryTerm>			(retrieves filtered selection)
 * 		/quoteSearch?sort=<sort>,<sort> 		(retrieve sorted selection)
 * 		/quoteSearch?term=<term>&sort=<sort> 	(retrieve sorted filtered selection)
 * 
 * 
 * @author ggroce
 */
public class QuoteSearchServlet extends AbstractServlet {

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
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String url = request.getRequestURI();
		logger.log(Level.DEBUG, "QuoteSearchServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/quoteSearch/");
		if ( idx > -1 ) {
			String queryString = request.getQueryString();
			
			logger.log(Level.DEBUG, "QuoteSearchServlet(): doGet(): queryString =" + queryString);
			// Figure out what we've got:
			// "myString" is the piece of the URL that we actually care about
			String myString = url.substring(idx + "/quoteSearch/".length());
			String[] urlPieces = myString.split("/");
			String command = urlPieces[0];

			if ( StringUtils.isBlank(command)) {
				super.sendNotFound(response);
			} else {
				if ( command.equals("list") || StringUtils.isNumeric(command)) {
					Connection conn = null;
					try {
						if ( StringUtils.isBlank(command)) {
							throw new RecordNotFoundException();
						}
						conn = AppUtils.getDBCPConn();
						AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_READ);

						QuoteSearchListResponse quoteSearchListResponse = doGetWork(conn, myString, queryString);
						super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteSearchListResponse);
					} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
						super.sendForbidden(response);
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
			logger.log(Level.DEBUG, "QuoteSearchServlet(): doGet(): queryString =" + queryString);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();
				AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_READ);

				QuoteSearchListResponse quoteSearchListQueryResponse = doGetWork(conn, queryString);
				super.sendResponse(conn, response, ResponseCode.SUCCESS, quoteSearchListQueryResponse);
			} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
				super.sendForbidden(response);
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
	
	private String getQueryString( String qs, String qm) throws UnsupportedEncodingException, Exception {
		String term = "";
		Map<String, String> map = AppUtils.getQueryMap(qs);
		term = map.get(qm);
		logger.log(Level.DEBUG, "getQueryString(): map =" + map);
		logger.log(Level.DEBUG, "getQueryString(): term =" + term);
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
		return term;
	}
	
	public QuoteSearchListResponse doGetWork(Connection conn, String qs) throws RecordNotFoundException, Exception {
		QuoteSearchListResponse quoteSearchListResponse = new QuoteSearchListResponse();
		String term = "";
		if ( ! StringUtils.isBlank(qs)) {
			int idx = qs.indexOf("term=");
			if ( idx > -1 ) {
				term = getQueryString(qs, "term");
				Map<String, String> map = AppUtils.getQueryMap(qs);
				String sort = map.get("sort");
				if (! StringUtils.isBlank(sort)){
					String[] sortParms = map.get("sort").split(",");
					quoteSearchListResponse = new QuoteSearchListResponse(conn, term, sortParms);
				} else {
					quoteSearchListResponse = new QuoteSearchListResponse(conn, term);
				}
			}
		}
		return quoteSearchListResponse;
		
	}
	
}
