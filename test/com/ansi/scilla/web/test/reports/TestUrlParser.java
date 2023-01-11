package com.ansi.scilla.web.test.reports;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.report.common.BatchReports;

public class TestUrlParser {

	public static void main(String[] args) {
		

		for (String x : new String[] {"PAC_WEEKLY","DO_LIST","PD45","XXX"} ) {
			String url = "http://127.0.0.1:8080/ansi_web/reports/subscriptionAdmin/" + x;
			String[] path = StringUtils.split(url, "/");
			String reportId = path[path.length -1 ];
			try {
				BatchReports report = BatchReports.valueOf(reportId);
				if ( ! report.adminSubscription() ) {
					System.out.println(x + "\tnope");
				} else {
					System.out.println(x + "\tgood");
				}
			} catch ( IllegalArgumentException e ) {
				System.out.println(x + "\tno no");
			}
		}
		
		
	}

}
