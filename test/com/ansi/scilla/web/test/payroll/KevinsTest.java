package com.ansi.scilla.web.test.payroll;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Date;

//import org.apache.tomcat.util.http.fileupload.FileItem;
//import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;

public class KevinsTest{

//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/design/20210629_payroll/rollout_mtg/Payroll 77 08.13.21.ods";
//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/design/20210629_payroll/rollout_mtg/Payroll 77 08.13.21.ods";
	
	public void go() throws Exception {
		InputStream inputStream;
		//FileItem fileItem;
		
		//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20210711_bcr_spreadsheet_examples/indy/work/content.xml";
		//  private final String filePath = "test/com/ansi/scilla/web/test/bcr/content.xml";
		//private final String filePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/extracted/Payroll 77 09.24.2021/content.xml";

		String odsFilePath1;
		odsFilePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/ods/Payroll 77 09.24.2021.ods";
								
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);	

			//test using InputStream
			inputStream = new FileInputStream(odsFilePath1);
			TimesheetImportResponse timesheetImportResponse = new TimesheetImportResponse(conn, inputStream);

			//test using FileItem
				// ...commented this out because I couldn't get it to work... 
				//DiskFileItemFactory factory = new DiskFileItemFactory();
				//fileItem = (FileItem)factory.createItem("formFieldName", "application/zip", false,
				//		odsFilePath1);				
			
			//TimesheetImportResponse x = new TimesheetImportResponse(conn, fileItem);

			inputStream = new FileInputStream(odsFilePath1);
			timesheetImportResponse  = new TimesheetImportResponse(conn, inputStream);
			System.out.println(timesheetImportResponse);
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






