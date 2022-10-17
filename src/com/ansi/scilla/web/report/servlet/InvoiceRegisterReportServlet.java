package com.ansi.scilla.web.report.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.report.invoiceRegisterReport.InvoiceRegisterReport;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.thewebthing.commons.db2.RecordNotFoundException;
import com.thewebthing.commons.lang.StringUtils;

public class InvoiceRegisterReportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "invoiceRegisterReport";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null;
		try {
			ansiURL = new AnsiURL(request, REALM, (String[])null); 
			AppUtils.validateSession(request, Permission.INVOICE, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			Connection conn = null;
			try {
				conn = AppUtils.getDBCPConn();

				Integer divisionId = ansiURL.getId();
				Division division = validateDivision(conn, divisionId);
				Calendar startDate = makeStartDate(ansiURL);
				String fileName = makeFileName(ansiURL, division, startDate);
				
				XSSFWorkbook workbook = InvoiceRegisterReport.makeReport(conn, divisionId, startDate);
				
				response.setStatus(HttpServletResponse.SC_OK);
				OutputStream out = response.getOutputStream();
				
			    response.setHeader("Expires", "0");
			    response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
			    response.setHeader("Pragma", "public");
			    // setting the content type
//			    response.setContentType("application/vnd.ms-excel");
			    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			    String dispositionHeader = "attachment; filename=" + fileName + ".xlsx";
			    logger.log(Level.DEBUG, "Invoice register 63: " + fileName);
				response.setHeader("Content-disposition",dispositionHeader);
			    // the contentlength
//			    response.setContentLength(baos.size());

				workbook.write(out);
				out.close();

			} catch(RecordNotFoundException recordNotFoundEx) {
				super.sendNotFound(response);
			} catch ( Exception e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			} finally {
				AppUtils.closeQuiet(conn);
			}
		} catch (ResourceNotFoundException e1) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		}
	}

	private Division validateDivision(Connection conn, Integer divisionId) throws RecordNotFoundException, Exception {
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		return division;
	}

	private Calendar makeStartDate(AnsiURL ansiURL) {
		Calendar startDate = Calendar.getInstance(new AnsiTime());
		String month = ansiURL.getQueryParameterMap().get("month")[0];
		if ( ! StringUtils.isBlank(month)) {
			startDate.set(Calendar.MONTH, Integer.valueOf(month));
		}		
		String year = ansiURL.getQueryParameterMap().get("year")[0];
		if ( ! StringUtils.isBlank(month)) {
			startDate.set(Calendar.YEAR, Integer.valueOf(year));
		}		
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		startDate.set(Calendar.HOUR_OF_DAY, 0);
		startDate.set(Calendar.MINUTE, 0);
		startDate.set(Calendar.SECOND, 0);
		startDate.set(Calendar.MILLISECOND, 0);
		return startDate;
	}

	private String makeFileName(AnsiURL ansiURL, Division division, Calendar startDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM-yyyy");
		List<String> fileName = new ArrayList<String>();
		fileName.add("InvoiceRegister");
		fileName.add(division.getDivisionDisplay());
		fileName.add(sdf.format(startDate.getTime()));
		return StringUtils.join(fileName, "_");
	}	
}

