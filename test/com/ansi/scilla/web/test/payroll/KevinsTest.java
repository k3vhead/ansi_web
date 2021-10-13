package com.ansi.scilla.web.test.payroll;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;

public class KevinsTest{

	
	public void go() throws Exception {
		InputStream inputStream;

		String odsFilePath1;;
		//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20210711_bcr_spreadsheet_examples/indy/work/content.xml";
		//  private final String filePath = "test/com/ansi/scilla/web/test/bcr/content.xml";
		//private final String filePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/extracted/Payroll 77 09.24.2021/content.xml";
		odsFilePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/ods/Payroll 77 09.24.2021.ods";
		

		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);		
		} 
		finally {
			if ( conn != null ) {
				conn.close();
			}
		}
		inputStream = new FileInputStream(odsFilePath1);
		TimesheetImportResponse x = new TimesheetImportResponse(conn, inputStream);
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






