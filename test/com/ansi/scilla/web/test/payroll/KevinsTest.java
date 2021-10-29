package com.ansi.scilla.web.test.payroll;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;

public class KevinsTest{

//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/design/20210629_payroll/rollout_mtg/Payroll 77 08.13.21.ods";
	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/design/20210629_payroll/rollout_mtg/Payroll 77 08.13.21.ods";
	
	public void go() throws Exception {
		InputStream inputStream;

		String odsFilePath1;
		odsFilePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/ods/Payroll 77 09.24.2021.ods";
		

		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);	
			inputStream = new FileInputStream(filePath);
			TimesheetImportResponse x = new TimesheetImportResponse(conn, inputStream);
			System.out.println(x);
		} 
		finally {
			if ( conn != null ) {
				conn.close();
			}
		}
		
	}
	

	public static void main(String[] args) {
		System.out.println(new Date());
		
		try {
			new KevinsTest().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("end");
	}	
}






