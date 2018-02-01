package com.ansi.scilla.web.invoice.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.jsonFormat.AnsiFormat;
import com.ansi.scilla.common.queries.InvoiceSearch;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
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
 * 		/invoiceTypeAhead?term=					(returns not found)
 * 		/invoiceTypeAhead?term=<searchTerm>		(returns all records containing <searchTerm>)
 * 
 * The servlet will return 404 Not Found if there is no "term=" found.
 * 
 * @author gagroce
 *
 */
public class InvoiceTypeAheadServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	
	private final String PARAMETER_BILLTO = "billTo";
	private final String PARAMETER_TERM = "term";

	private Logger logger = LogManager.getLogger(this.getClass());
	
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
		try {
			AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			processRequest(request, response);

		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);						
		}

	}
	
	private void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String url = request.getRequestURI();
		logger.debug("InvoiceTypeAheadServlet(): doGet(): url =" + url);
		int idx = url.indexOf("/invoiceTypeAhead/");
		if ( idx > -1 ) {
			super.sendNotFound(response);
		} else {
			String billTo = request.getParameter(PARAMETER_BILLTO);
			if ( billTo != null ) {
				billTo = URLDecoder.decode(billTo, "UTF-8");
				billTo = StringUtils.trimToNull(billTo);
			}
			String queryTerm = request.getParameter(PARAMETER_TERM);
			if ( queryTerm != null ) {
				queryTerm = URLDecoder.decode(queryTerm, "UTF-8");
				queryTerm = StringUtils.trimToNull(queryTerm);
			}

			if ( StringUtils.isBlank(queryTerm)) {
				super.sendNotFound(response);
			} else {
				Connection conn = null;
				try {
					conn = AppUtils.getDBCPConn();					
					makeQueryResult(conn, queryTerm, billTo, response);
				} finally {
					AppUtils.closeQuiet(conn);
				}
			}		
		}
	}

	/**
	 * Returns json representation of invoices info where it matches the search term
	 * @param conn
	 * @param queryTerm
	 * @param response
	 * @throws Exception
	 */
	private void makeQueryResult(Connection conn, String queryTerm, String billTo, HttpServletResponse response) throws Exception {

		String term = queryTerm.toLowerCase();
		logger.log(Level.DEBUG, "InvoiceTypeAheadServlet(): doGet(): term =[" + term +"]  billTo=[" + billTo + "]" );
		List<ReturnItem> resultList = new ArrayList<ReturnItem>();

		Integer billToAddressId = StringUtils.isNumeric(billTo) ? Integer.valueOf(billTo) : null;

		String sql = InvoiceSearch.sql + InvoiceSearch.generateWhereClause(conn, term, billToAddressId);
		logger.log(Level.DEBUG, "******");
		logger.log(Level.DEBUG, "Invoice SQL:\n" + sql);
		logger.log(Level.DEBUG, "******");

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
	}



	public class ReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private final SimpleDateFormat sdf = new SimpleDateFormat(AnsiFormat.DATE.pattern());
		DecimalFormat currencyFormatter = new DecimalFormat(AnsiFormat.CURRENCY.pattern());
		
		private Integer id;
		private String label;
		private String value;
		
		public ReturnItem(ResultSet rs) throws SQLException {
			super();
			this.id = rs.getInt("invoice_id");
			//invoice_id:div:bill_to_name:invoice_date:invoice_amount:invoice_tax:invoice_total:invoice_balance
			int invoiceId = rs.getInt(InvoiceSearch.INVOICE_ID);
			String fmInvoiceNbr = rs.getString(InvoiceSearch.FLEETMATICS_INVOICE_NBR);
			String div = rs.getString(InvoiceSearch.DIV);
			String billToName = rs.getString(InvoiceSearch.BILL_TO_NAME);
			Date invoiceDate = rs.getDate(InvoiceSearch.INVOICE_DATE);
			String dateDisplay = invoiceDate == null ? " " : sdf.format(invoiceDate);
			BigDecimal invoiceAmount = rs.getBigDecimal(InvoiceSearch.INVOICE_AMOUNT);
			BigDecimal invoiceTax = rs.getBigDecimal(InvoiceSearch.INVOICE_TAX);
			BigDecimal invoiceTotal = rs.getBigDecimal(InvoiceSearch.INVOICE_TOTAL);
			BigDecimal invoiceBalance = rs.getBigDecimal(InvoiceSearch.INVOICE_BALANCE);
			String balanceDisplay = invoiceBalance == null ? " " : currencyFormatter.format(invoiceBalance);
			
			this.label = "Invoice " + invoiceId 
					+ ":" + "FM Invoice # " + fmInvoiceNbr
					+ ":" + "Div " + div
					+ ":" + "BT " + billToName
					+ ":" + "Date " + dateDisplay
					+ ":" + "Amount " + currencyFormatter.format(invoiceAmount)
					+ ":" + "Tax " + currencyFormatter.format(invoiceTax)
					+ ":" + "Total " + currencyFormatter.format(invoiceTotal)
					+ ":" + "Balance " + balanceDisplay;
			this.value = String.valueOf(invoiceId);
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
