package com.ansi.scilla.web.dataload;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.lang.StringUtils;

/**
 * Reads a spreadsheet of user info and populates passwords where necessary in the ansi_users table.
 * Outputs a list of names, logins, and un-encrypted passwords.
 * 
 * @author dclewis
 *
 */
public class LoadUsers {

	private boolean prodRun = true;
	private final String inputFileName = "/home/dclewis/Documents/projects/ANSI_Scheduling/ansi_users.xlsx";
	private final String outputFileName = "/home/dclewis/Documents/projects/ANSI_Scheduling/ansi_passwords.xlsx";
	private static String[] columns = new String[] {"user_id","status","first_name","last_name","title","email","password","phone","address1","address2","city","state","zip","permission_group_id","super_user","added_by","added_date","updated_by","updated_date"};
	private static HashMap<String, Integer> columnMap;
	private final Integer permssionGroupId = 2;
	private Random r = new Random();
	
	static {
		columnMap = new HashMap<String, Integer>();
		for ( int i = 0; i < columns.length; i++ ) {
			columnMap.put(columns[i], i);
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		new LoadUsers().go();
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			doWork(conn, inputFileName, outputFileName);
			
			if ( prodRun ) {
				conn.commit();
			} else {
				conn.rollback();
			}
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}

	private void doWork(Connection conn, String inputFileName, String outputFileName) throws FileNotFoundException, IOException, SQLException {
		PreparedStatement ps = conn.prepareStatement("update ansi_user set password=?,status=?,permission_group_id=?,updated_by=?,updated_date=? where user_id=?");
		Date today = new Date();
		
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet passwordSheet = workbook.createSheet();
		XSSFRow passwordRow = passwordSheet.createRow(0);
		workbook.setSheetName(0, "Passwords");
		XSSFCell cell = null;
		int cellnum = 1;
		for ( String hdr : new String[] {"ID", "Last","First","Email","Password"}) {
			cell = passwordRow.createCell(cellnum);
			cell.setCellValue(hdr);
			cellnum++;
		}
		Integer passwordRowNum = 1;
		
		XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(inputFileName));
		for ( int i = 1; i < wb.getNumberOfSheets(); i++) {
			XSSFSheet sheet = wb.getSheetAt(i);
			System.out.println("Processing sheet: " + sheet.getSheetName());
			for ( int rowNum = 1; rowNum < sheet.getLastRowNum()+1; rowNum++) {
				XSSFRow row = sheet.getRow(rowNum);
				Integer userId = new Double(row.getCell(columnMap.get("user_id")).getNumericCellValue()).intValue();
				String lastName = row.getCell(columnMap.get("last_name")).getStringCellValue();
				String firstName = row.getCell(columnMap.get("first_name")).getStringCellValue();
				String email = row.getCell(columnMap.get("email")).getStringCellValue();
				String password = row.getCell(columnMap.get("password")).getStringCellValue();
				if ( StringUtils.isBlank(password) || password.equals("NULL")) {
					System.out.println("\tProcessing " + lastName + ", " + firstName + " (" + email + ")");
					String newPassword = doUpdate(ps, today, row);
					passwordRow = passwordSheet.createRow(passwordRowNum);
					cell = passwordRow.createCell(0);
					cell.setCellValue(sheet.getSheetName());
					cell = passwordRow.createCell(1);
					cell.setCellValue(userId);
					cell = passwordRow.createCell(2);
					cell.setCellValue(lastName);
					cell = passwordRow.createCell(3);
					cell.setCellValue(firstName);
					cell = passwordRow.createCell(4);
					cell.setCellValue(email);
					cell = passwordRow.createCell(5);
					cell.setCellValue(newPassword);
				} else {
					System.out.println("\tSkipping " + lastName + ", " + firstName + " (" + email + ")");
					passwordRow = passwordSheet.createRow(passwordRowNum);
					cell = passwordRow.createCell(0);
					cell.setCellValue(sheet.getSheetName());
					cell = passwordRow.createCell(1);
					cell.setCellValue(userId);
					cell = passwordRow.createCell(2);
					cell.setCellValue(lastName);
					cell = passwordRow.createCell(3);
					cell.setCellValue(firstName);
					cell = passwordRow.createCell(4);
					cell.setCellValue(email);
					cell = passwordRow.createCell(5);
					cell.setCellValue("Skipped");
				}
				passwordRowNum++;
			}
		}
		
		workbook.write(new FileOutputStream(outputFileName));
	}

	private String doUpdate(PreparedStatement ps, Date today, XSSFRow row) throws SQLException {
		Integer userId = new Double(row.getCell(columnMap.get("user_id")).getNumericCellValue()).intValue();
		Integer passnum = r.nextInt(1000-100) + 100;
		String password = "password" + passnum;
		String encryptedPassword = AppUtils.encryptPassword(password, userId);	
		
		ps.setString(1, encryptedPassword);
		ps.setInt(2, 0); // status
		ps.setInt(3, permssionGroupId);
		ps.setInt(4, 5); // updated by
		ps.setDate(5, new java.sql.Date(today.getTime()));
		ps.setInt(6, userId);
		
		ps.executeUpdate();
		
		return password;
	}
}
