package com.ansi.scilla.web.test.payroll;

import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.ansi.scilla.web.payroll.common.EmployeeRecord;

import au.com.bytecode.opencsv.CSVReader;

public class TestEmployeeFileParse {

	public void go() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		Date x = sdf.parse("12/25/2021");
		System.out.println(x);
		
		
		CSVReader reader = new CSVReader(new FileReader("xxxxxxxx"));
		List<String[]> recordList = reader.readAll();
		reader.close();
		for ( String[] row : recordList ) {
			EmployeeRecord rec = new EmployeeRecord(row);
			System.out.println(rec);
		}
		
	}

	public static void main(String[] args) {
		try {
			new TestEmployeeFileParse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
