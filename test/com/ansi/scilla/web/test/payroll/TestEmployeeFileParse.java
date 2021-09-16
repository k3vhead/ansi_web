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
		
		String davesFile = "/home/dclewis/Documents/webthing_v2/projects/ANSI/design/20210629_payroll/rollout_mtg/PAYCOM Import Template-NEW-GEN.csv";
		String krisFile = "/home/klandeck/Documents/payroll/PAYCOM Import Template-NEW-GEN.csv";
		
		CSVReader reader = new CSVReader(new FileReader(krisFile));
		List<String[]> recordList = reader.readAll();		
		recordList.remove(0);
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
