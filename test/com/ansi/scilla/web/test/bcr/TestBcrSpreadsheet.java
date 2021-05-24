package com.ansi.scilla.web.test.bcr;

import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.common.BcrTicketSpreadsheet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.test.TesterUtils;

public class TestBcrSpreadsheet extends AbstractBcrTest {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			Integer claimYear = 2020;
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, 5);
//			SessionData sessionData = null;
//			SessionUser sessionUser = sessionData.getUser();
//			Integer userId = sessionUser.getUserId();
			
			BcrTicketSpreadsheet spreadsheet = new BcrTicketSpreadsheet(conn, 1, divisionList, div_67oh07, claimYear, workWeekOctober2020);
			XSSFWorkbook workbook = spreadsheet.getWorkbook();
//			workbook.write(new FileOutputStream("/home/jwlewis/Documents/projects/BCR_Spreadsheet.xlsx"));
			workbook.write(new FileOutputStream("/home/dclewis/Documents/webthing_v2/projects/ANSI/testresults/BCR_Spreadsheet.xlsx"));
			
			
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
