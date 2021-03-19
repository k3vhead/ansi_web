package com.ansi.scilla.web.quote.servlet;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.queries.QuotePrintHistory;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.address.response.AddressDetail;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.quote.response.QuotePrintHistoryResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class QuotePrintHistoryServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		AnsiURL ansiURL = null; 
		Connection conn = null;
		
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			ansiURL = new AnsiURL(request, "quotePrintHistory", (String[])null); 
			AppUtils.validateSession(request, Permission.QUOTE, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			Integer quoteId = ansiURL.getId();

			try {
				QuotePrintHistory history = new QuotePrintHistory();
				AddressDetail jobSite = new AddressDetail();
				if ( quoteId != 0 ) {
					Quote quote = validateQuote(conn, quoteId);
					jobSite = makeJobSite(conn, quote.getJobSiteAddressId());
					history = new QuotePrintHistory(conn, quoteId);
				}
				processGet(conn, response, history, jobSite);
			} catch ( RecordNotFoundException e) {
				super.sendNotFound(response);
			}
			conn.commit();
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			AppUtils.rollbackQuiet(conn);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}

	private AddressDetail makeJobSite(Connection conn, Integer jobSiteAddressId) throws RecordNotFoundException, Exception {
		Address address = new Address();
		address.setAddressId(jobSiteAddressId);
		address.selectOne(conn);
		AddressDetail addressDetail = new AddressDetail(address);
		return addressDetail;
	}

	private void processGet(Connection conn, HttpServletResponse response, QuotePrintHistory history, AddressDetail jobSite) throws Exception {		
		QuotePrintHistoryResponse data = new QuotePrintHistoryResponse(jobSite, history.getHistoryList());
		super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
	}

	private Quote validateQuote(Connection conn, Integer quoteId) throws RecordNotFoundException, Exception {
		Quote quote = new Quote();
		quote.setQuoteId(quoteId);
		quote.selectOne(conn);
		return quote;
	}
	
}
