package com.ansi.scilla.web.test;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.report.cashReceiptsRegister.CashReceiptsRegisterReport;
import com.ansi.scilla.report.reportBuilder.reportType.AbstractReport;
import com.ansi.scilla.report.reportBuilder.reportType.AnsiReport;
import com.ansi.scilla.report.reportBuilder.reportType.CompoundReport;
import com.ansi.scilla.report.reportBuilder.reportType.StandardReport;
import com.ansi.scilla.report.reportBuilder.reportType.StandardSummaryReport;
import com.ansi.scilla.report.reportBuilder.xlsBuilder.XLSBuilder;

public class TestRptBuilder {

	public static void main(String[] args) {
		try {
			new TestRptBuilder().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Calendar startDate = new GregorianCalendar(2017, Calendar.JULY, 5);
			Calendar endDate = new GregorianCalendar(2017, Calendar.JULY, 5);
			CashReceiptsRegisterReport crr = CashReceiptsRegisterReport.buildReport(conn, startDate, endDate);
			for ( AbstractReport report : crr.getReports() ) {
				printDate("37: ", report);
				printSub("38: ", report);
			}
//			System.out.println( crr instanceof CashReceiptsRegisterReport);
//			System.out.println( crr instanceof CompoundReport);
//			Class<?> superclass = crr.getClass().getSuperclass();
//			System.out.println(superclass.getName());
//			
//			Method[] methodList = this.getClass().getMethods();
//			for ( Method method : methodList ) {
//				System.out.println(method.getName());
//				for ( Class<?> parmClass : method.getParameterTypes() ) {
//					System.out.println("\t" + parmClass.getName());
//				}
//			}
//			Method method = findABuilder(crr);
//			String html = (String)method.invoke(this, new Object[] { crr });
//			System.out.println(html);
			
//			XSSFWorkbook workbook = new XSSFWorkbook();
//			checkme((StandardReport)crr.getReports()[1], workbook);
		} finally {
			conn.close();
		}
	}
	
	private void printSub(String string, AbstractReport report) {
		try {
			Method method = report.getClass().getMethod("getSubtitle", (Class<?>[])null);
			String startDate = (String)method.invoke(report, (Object[])null);
			System.out.println(string + " " + startDate);
		} catch ( Exception e ) {
			System.out.println(string + " " + e);
		}
	}

	private void printDate(String string, AnsiReport report) {
		try {
			Method method = report.getClass().getMethod("getStartDate", (Class<?>[])null);
			Calendar startDate = (Calendar)method.invoke(report, (Object[])null);
			System.out.println(string + " " + startDate);
		} catch ( Exception e ) {
			System.out.println(string + " " + e);
		}
	}
	public void checkme(StandardReport report, XSSFWorkbook workbook) throws Exception {
		System.out.println("StandardREportServlet 223");
		System.out.println(report.getSubtitle());
		XLSBuilder.build(report, workbook);
		System.out.println("StandardReportServlet 259");
	}
	
	
	public Method findABuilder(CashReceiptsRegisterReport report) throws NoSuchMethodException, SecurityException {
		Method method = null;
		Class<?> reportClass = report.getClass();
		System.out.println("ReportClass: " + reportClass.getName());
		try {
			method = this.getClass().getMethod("buildReport", new Class<?>[] { reportClass });
		} catch ( NoSuchMethodException e ) {
			Class<?> superclass = reportClass.getSuperclass();
			System.out.println("superclass: " + superclass.getName());
			try {
				method = this.getClass().getMethod("buildReport", new Class<?>[] { superclass });
			} catch ( NoSuchMethodException e2 ) {
				Class<?> superduperclass = superclass.getSuperclass();
				System.out.println("superduperclass: " +superduperclass.getName());
				method = this.getClass().getMethod("buildReport", new Class<?>[] { superduperclass });
			} 
		}
		return method;
	}

	@SuppressWarnings("unused")
	public String buildReport(CompoundReport report) {
		return "Here's a compound report";
	}

	@SuppressWarnings("unused")
	public String buildReport(StandardSummaryReport report) {
		return "Here's a StandardSummaryReport report";
	}

	@SuppressWarnings("unused")
	public String buildReport(StandardReport report) {
		return "Here's a StandardReport report";
	}
}
