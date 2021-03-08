package com.ansi.scilla.web.test.bcr;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.common.BcrTicketSpreadsheet;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestBcrSpreadsheet {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			String workWeeks = "41,42,43,44";
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, 5);
			Integer divisionId = 101;
			Integer claimYear = 2020;
			
			BcrTicketSpreadsheet spreadsheet = new BcrTicketSpreadsheet(conn, divisionList, divisionId, claimYear, workWeeks);
//			XSSFWorkbook workbook = spreadsheet.makeWorkbook();
//			workbook.write(new FileOutputStream("/Users/jwlewis/Documents/projects/pinpoint/google_cat/catdump_20131203.xlsx"));
			
			
		} finally {
			if ( conn != null ) {
				conn.close();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestBcrSpreadsheet().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
