package com.ansi.scilla.web.bcr.common;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.report.reportBuilder.XLSBuilder;
import com.ansi.scilla.web.bcr.servlet.AbstractBcrTicketLookupServlet;

public class BcrTicketSpreadsheet extends AbstractBcrTicketLookupServlet{
	/**
	 * @author jwlewis
	 */
	private static final long serialVersionUID = 1L;
	
	public BcrTicketSpreadsheet() {
		//this.BcrTicketSpreadsheet(conn, year, weeks);
	}
	
	public BcrTicketSpreadsheet(Connection conn, Calendar date, List<String> workWeekList) {
		String sql = BcrTicketSql.makeBaseWhereClause(workWeekList.get(0));
		Integer year = date.YEAR;
//		XSSFWorkbook workbook = XLSBuilder.build(report); 
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		logger.log(Level.DEBUG, request.getRequestURI());
		String divisionString = request.getParameter(DIVISION_ID);
		String workYearString = request.getParameter(WORK_YEAR);
		String workWeeks = request.getParameter(WORK_WEEKS);  // comma-delimited list of work weeks.
		String workWeek = request.getParameter(WORK_WEEK);  // the single week we want to look at
		boolean errorFound = false;
		if(StringUtils.isBlank(divisionString)) {
			errorFound = true;
		} 
		if(!StringUtils.isNumeric(divisionString) || !StringUtils.isNumeric(workYearString)) {
			errorFound = true;
		}
		if(StringUtils.isBlank(workWeeks)) {
			errorFound = true;
		}
		if(StringUtils.isBlank(workWeek)) {
			errorFound = true;
		}
		if(errorFound) {
			super.sendNotFound(response);
		} else {
			super.doGet(request, response);
		}
		
	}
	@Override
	protected void makeMyColumns() {
		
		
	}
}
