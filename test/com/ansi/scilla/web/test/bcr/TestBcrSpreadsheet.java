package com.ansi.scilla.web.test.bcr;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.common.BCRSpreadsheet.BcrTicketSpreadsheet;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestBcrSpreadsheet extends AbstractBcrTest {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getUATConn();
			conn.setAutoCommit(false);
			
			
			Integer claimYear = 2021;
			Integer userId = USER_IS_DAVE;
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, userId);
			
			BcrTicketSpreadsheet spreadsheet = new BcrTicketSpreadsheet(conn, userId, divisionList, div_12il02, claimYear, workWeekJanuary2021);
			XSSFWorkbook workbook = spreadsheet.getWorkbook();
			if ( userId == USER_IS_DAVE) {
				workbook.write(new FileOutputStream("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/BCR_Spreadsheet2.xlsx"));
			} else if ( userId == USER_IS_JOSHUA ) {
				workbook.write(new FileOutputStream("/home/jwlewis/Documents/projects/BCR_Spreadsheet.xlsx"));
			} else {
				throw new Exception("Where do I write the file?");
			}

			
			
			
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
