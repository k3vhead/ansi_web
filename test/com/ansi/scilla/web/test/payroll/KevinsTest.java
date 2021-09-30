package com.ansi.scilla.web.test.payroll;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;







/*
 * 		DIVISION			L3
 * 		OM NAME				O3:S3
 * 		WEEK ENDING			X3
 * 
 *     rows 6-37
 *     
 *     Columns				(Row 38 = SUM(col))
 *     ===================	===================
 *     'Row'				B		
 *     Employee Name 		D
 *     Regular  Hours		F
 *     Regular  Pay			H
 *     Expenses				J
 *     O.T.  Hours			L
 *     O.T.  Pay			N
 *     Vac  Hours			P
 *     Vac Pay				R
 *     Hol  Hours			T
 *     Hol Pay				V
 *     Gross Pay			X
 *     Exp Smt'd			Z		
 *     Exp Allw'd			AB
 *     VOL					AD
 *     D/L					AF
 *     Prod					AH
 *
 *     
 *     
 */


public class KevinsTest{

	//	private final String filePath = "/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20210711_bcr_spreadsheet_examples/indy/work/content.xml";
	//  private final String filePath = "test/com/ansi/scilla/web/test/bcr/content.xml";
	//private final String filePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/extracted/Payroll 77 09.24.2021/content.xml";
	private final String odsFilePath1 = "test/com/ansi/scilla/web/test/payroll/data/1/ods/Payroll 77 09.24.2021.ods";

	@SuppressWarnings({ "deprecation", "unused" })
	public void go() throws Exception {
		File odsFile = new File(odsFilePath1);
		OdfDocument oDoc = OdfDocument.loadDocument(odsFile);
		//org.odftoolkit.simple.table

		@SuppressWarnings("deprecation")
		List<OdfTable> oTables = oDoc.getTableList();

		System.out.println("Tables Found " + oTables.size() );

		for(int i=0; i <oTables.size();i++) {
			@SuppressWarnings("deprecation")
			OdfTable tab = oTables.get(i);	
			System.out.println("Tab Name - " + tab.getTableName().toString());
		}
		OdfTable smry = oTables.get(2);
		System.out.println("Divsion - " + smry.getCellByPosition("L3").getDisplayText());
		System.out.println("OM Name - " + smry.getCellByPosition("O3").getDisplayText());
		System.out.println("Week Ending - " + smry.getCellByPosition("X3").getDisplayText());

		String Col01Row = new String("B");		
		String Col02EmployeeName = new String("D");
		String Col03RegularHours = new String("F");
		String Col04RegularPay = new String("H");
		String Col05Expenses = new String("J");
		String Col06OTHours = new String("L");
		String Col07OTPay = new String("N");
		String Col08VacHours = new String("P");
		String Col09VacPay = new String("R");
		String Col10HolHours = new String("T");
		String Col11HolPay = new String("V");
		String Col12GrossPay = new String("X");
		String Col13ExpSubmitted = new String("Z");		
		String Col14ExpAllowed = new String("AB");
		String Col15Volume = new String("AD");
		String Col16DL = new String("AF");
		String Col17Prod = new String("AH");

		String s01, s02, s03, s04, s05, s06, s07, s08, s09, s10, s11, s12, s13, s14, s15, s16, s17;
		String v01, v02, v03, v04, v05, v06, v07, v08, v09, v10, v11, v12, v13, v14, v15, v16, v17;

		for(int r=6; r<39;r++) {
			s01 = Col01Row.concat(Integer.toString(r));		
			s02 = Col02EmployeeName.concat(Integer.toString(r));
			s03 = Col03RegularHours.concat(Integer.toString(r));
			s04 = Col04RegularPay.concat(Integer.toString(r));
			s05 = Col05Expenses.concat(Integer.toString(r));
			s06 = Col06OTHours.concat(Integer.toString(r));
			s07 = Col07OTPay.concat(Integer.toString(r));
			s08 = Col08VacHours.concat(Integer.toString(r));
			s09 = Col09VacPay.concat(Integer.toString(r));
			s10 = Col10HolHours.concat(Integer.toString(r));
			s11 = Col11HolPay.concat(Integer.toString(r));
			s12 = Col12GrossPay.concat(Integer.toString(r));
			s13 = Col13ExpSubmitted.concat(Integer.toString(r));
			s14 = Col14ExpAllowed.concat(Integer.toString(r));
			s15 = Col15Volume.concat(Integer.toString(r));
			s16 = Col16DL.concat(Integer.toString(r));
			s17 = Col17Prod.concat(Integer.toString(r));

			v01 = smry.getCellByPosition(s01).getDisplayText(); 
			v02 = smry.getCellByPosition(s02).getDisplayText();
			v03 = smry.getCellByPosition(s03).getDisplayText();
			v04 = smry.getCellByPosition(s04).getDisplayText();
			v05 = smry.getCellByPosition(s05).getDisplayText();
			v06 = smry.getCellByPosition(s06).getDisplayText();
			v07 = smry.getCellByPosition(s07).getDisplayText();
			v08 = smry.getCellByPosition(s08).getDisplayText();
			v09 = smry.getCellByPosition(s09).getDisplayText();
			v10 = smry.getCellByPosition(s10).getDisplayText();
			v11 = smry.getCellByPosition(s11).getDisplayText();
			v12 = smry.getCellByPosition(s12).getDisplayText();
			v13 = smry.getCellByPosition(s13).getDisplayText();
			v14 = smry.getCellByPosition(s14).getDisplayText();
			v15 = smry.getCellByPosition(s15).getDisplayText();
			v16 = smry.getCellByPosition(s16).getDisplayText();
			v17 = smry.getCellByPosition(s17).getDisplayText();

			System.out.println(
					v01.toString() + "\t" + 
							v02.toString() + "\t" + 
							v03.toString() + "\t" +
							v04.toString() + "\t" +
							v05.toString() + "\t" +
							v06.toString() + "\t" +
							v07.toString() + "\t" +
							v08.toString() + "\t" +
							v09.toString() + "\t" +
							v10.toString() + "\t" +
							v11.toString() + "\t" +
							v12.toString() + "\t" +
							v13.toString() + "\t" +
							v14.toString() + "\t" +
							v15.toString() + "\t" +
							v16.toString() + "\t" + v17.toString());
		}

		/*
		 * 		DIVISION			L3
		 * 		OM NAME				O3:S3
		 * 		WEEK ENDING			X3		
		 */

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




