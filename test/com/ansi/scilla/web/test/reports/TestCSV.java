package com.ansi.scilla.web.test.reports;

import java.io.FileWriter;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.report.servlet.ReportSubscriptionCSVServlet;

import au.com.bytecode.opencsv.CSVWriter;

public class TestCSV {
	
	public void go() throws Exception {
		System.out.println("Start " + new Date());
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			ReportSubscriptionCSVServlet x = new ReportSubscriptionCSVServlet();
			List<String[]> csv = x.makeCSV(conn);
			CSVWriter writer = new CSVWriter(new FileWriter("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/report_tests/subscription.csv"));
			writer.writeAll(csv);
			writer.flush();
			writer.close();
		} finally {
			conn.close();
		}
		System.out.println("Done " + new Date());
	}

	public static void main(String[] args) {
		try {
			new TestCSV().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
