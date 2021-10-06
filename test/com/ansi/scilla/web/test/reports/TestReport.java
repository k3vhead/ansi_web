package com.ansi.scilla.web.test.reports;

import java.lang.reflect.Method;
import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.report.reportBuilder.AbstractReport;
import com.ansi.scilla.web.report.common.ReportType;
import com.ansi.scilla.web.report.request.ReportDefinition;

public class TestReport {

	public static void main(String[] args) {
		try {
//			new TestReport().go();
//			new TestReport().build();
			new TestReport().reflect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void reflect() throws Exception {
		String methodName = "getStartDate";
		Method getter = ReportDefinition.class.getMethod(methodName, (Class<?>[])null);
	}
	public void build() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			build(conn);		
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	
	
	public AbstractReport build(Connection conn) throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		ReportType reportType = ReportType.CASH_RECEIPTS_REGISTER;
		Class<?> reportClass = Class.forName(reportType.reportClassName());
		int arrayLength = reportType.builderParms().length + 1;   // connection + all parms
		Class<?>[] classList = new Class<?>[arrayLength];
		Object[] objectList = new Object[arrayLength];
		classList[0] = conn.getClass();
		objectList[0] = conn;
		
		for ( int i = 0; i < reportType.builderParms().length; i++ ) {
			int idx = i + 1;
			String methodName = "get" + StringUtils.capitalize(reportType.builderParms()[i]);
			System.out.println(methodName);
		}
		
//		Method method = reportClass.getMethod("buildReport", reportType.builderSignature());
		AbstractReport report = null; 
		return report;
	}
	public void go() throws Exception {
		for ( ReportType reportType : ReportType.values()) {
//			ReportType reportType = ReportType.valueOf("TICKET_STATUS_REPORT");
			System.out.println(reportType.toString());
			String reportClassName = reportType.reportClassName();
			Class<?> reportClass = Class.forName(reportClassName);
			AbstractReport report = (AbstractReport)reportClass.newInstance();
			Method method = reportClass.getMethod("getTitle", (Class<?>[])null);
			String title = (String)method.invoke(report, (Object[])null);
			System.out.println(title);
		}
	}

}
