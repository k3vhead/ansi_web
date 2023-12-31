package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.queries.QuoteSearch;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.response.QuoteTableJsonResponse;
import com.ansi.scilla.web.quote.response.QuoteTableReturnItem;

/**
 * The url for delete will return methodNotAllowed
 * 
 * The url for post will return methodNotAllowed
 * 
 * The url for get will be the following: 
 * 		/quoteTable  	See DataTables Table plug-in for jQuery at datatables.net for details
 * 
 * @author ggroce
 *
 */
@Deprecated
public class QuoteTableServlet extends AbstractServlet {

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
		int amount = 10;
		int start = 0;
		int draw = 0;
		int col = 8;
		String dir = "desc";
		String[] cols = { "quote_id", "quote_number", "division_nbr", "bill_to_name", "job_site_name", "job_site_address", "manager_name", "proposal_date", "quote_job_count", "quote_ppc_sum" };
		String sStart = request.getParameter("start");
	    String sAmount = request.getParameter("length");
	    String sDraw = request.getParameter("draw");
	    String sCol = request.getParameter("order[0][column]");
	    String sdir = request.getParameter("order[0][dir]");

	    Connection conn = null;
	    try {
	    	conn = AppUtils.getDBCPConn();
	    	SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_READ);
	    	SessionUser user = sessionData.getUser();

	    	String term = "";

	    	if(request.getParameter("search[value]") != null){
	    		term = request.getParameter("search[value]");
	    	}
	    	logger.log(Level.INFO, term);
	    	if (sStart != null) {
	    		start = Integer.parseInt(sStart);
	    		if (start < 0) {
	    			start = 0;
	    		}
	    	}
	    	if (sAmount != null) {
	    		amount = Integer.parseInt(sAmount);
	    		logger.log(Level.DEBUG, sAmount);
	    		if (amount < 10 ) {
	    			amount = 10;
	    		} else if (amount > 1000) {
	    			amount = 1000;
	    		}
	    	}
	    	if (sDraw != null) {
	    		draw = Integer.parseInt(sDraw);
	    	}
	    	if (sCol != null) {
	    		col = Integer.parseInt(sCol);
	    		if (col < 0 || col > 10) {
	    			col = 0;
	    		}
	    	}
	    	if (sdir != null) {
	    		if (sdir.equals("desc")) {
	    			dir = "desc";
	    		} else if (sdir.equals("asc")) {
	    			dir = "asc";
	    		}

	    	}

	    	String colName = cols[col];
	    	int total = Quote.selectCount(conn, user.getUserId());

	    	int totalAfterFilter = QuoteSearch.selectFilteredCount(conn, user.getUserId(), term);

	    	List<QuoteTableReturnItem> resultList = new ArrayList<QuoteTableReturnItem>();
	    	List<QuoteSearch> quoteSearchList = QuoteSearch.select(conn, user.getUserId(), term, start, amount, colName, dir);
	    	for ( QuoteSearch item : quoteSearchList ) {
	    		resultList.add(new QuoteTableReturnItem(item));
	    	}

//	    	sql = QuoteSearch.sql;
//
//	    	String search = QuoteSearch.generateWhereClause(term);
//
//	    	logger.log(Level.DEBUG, sql);
//	    	sql += search;
//	    	logger.log(Level.DEBUG, sql);
//	    	sql += " order by " + colName + " " + dir;
//	    	logger.log(Level.DEBUG, sql);
//	    	if ( amount != -1) {
//	    		sql += " OFFSET "+ start+" ROWS"
//	    				+ " FETCH NEXT " + amount + " ROWS ONLY";
//	    	}
//	    	logger.log(Level.DEBUG, sql);
//
//	    	s = conn.createStatement();
//	    	ResultSet rs = s.executeQuery(sql);
//	    	ResultSetMetaData rsmd = rs.getMetaData();
//	    	while ( rs.next() ) {
//	    		QuoteTableReturnItem item = new QuoteTableReturnItem();				
//	    		QuoteTableReturnItem.rs2Object(item, rsmd, rs);
//	    		resultList.add(item);
//	    		//				resultList.add(new QuoteTableReturnItem(rs));
//	    	}
//	    	rs.close();
//
//	    	
//	    	
//	    	String sql2 = "select count(*)" + QuoteSearch.sqlFromClause;
//
//	    	if (search != "") {
//	    		sql2 += search;
//	    	}
//	    	Statement s2 = conn.createStatement();
//	    	ResultSet rs2 = s2.executeQuery(sql2);
//	    	if(rs2.next()){
//	    		totalAfterFilter = rs2.getInt(1);
//	    	}
//	    	rs2.close();

	    	response.setStatus(HttpServletResponse.SC_OK);
	    	response.setContentType("application/json");

	    	QuoteTableJsonResponse quoteTableJsonResponse = new QuoteTableJsonResponse();
	    	quoteTableJsonResponse.setRecordsFiltered(totalAfterFilter);
	    	quoteTableJsonResponse.setRecordsTotal(total);
	    	quoteTableJsonResponse.setData(resultList);
	    	quoteTableJsonResponse.setDraw(draw);

	    	String json = AppUtils.object2json(quoteTableJsonResponse);




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
