package com.ansi.scilla.web.test.payroll;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;
import com.ansi.scilla.web.payroll.common.EmployeeRecordStatus;
import com.ansi.scilla.web.payroll.response.EmployeeImportResponse;

public class TestEmployeeFileParse {

	public void go() throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy");
		Date x = sdf.parse("12/25/2021");
		System.out.println(x);
		
		String davesFile = "/home/dclewis/Documents/webthing_v2/projects/ANSI/design/20210629_payroll/rollout_mtg/PAYCOM Import Template-NEW-GEN.csv";
		String krisFile = "/home/klandeck/Documents/payroll/PAYCOM Import Template-NEW-GEN.csv";
				
//		CSVReader reader = new CSVReader(new FileReader(davesFile));
//		List<String[]> recordList = reader.readAll();		
//		recordList.remove(0);
//		reader.close();
//		
//		for ( String[] row : recordList ) {
//			EmployeeRecord rec = new EmployeeRecord(row);
//			System.out.println(rec);
//		}
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			File file = new File(davesFile);
			PreparedStatement ps = conn.prepareStatement("select * from payroll_employee where employee_code=? or (lower(employee_first_name)=? and lower(employee_last_name)=?)");
//			EmployeeImportResponse data = new EmployeeImportResponse(conn, file);
//			CollectionUtils.transform(data.getEmployeeRecords(), new EmployeeRecordTransformer(ps));
//			for ( EmployeeRecord record : data.getEmployeeRecords() ) {
//				if ( record.getRecordStatus().equalsIgnoreCase("MODIFIED")) {
//					System.out.println(record.getEmployeeCode() + "\t" + record.getRecordStatus());
//					for ( String q : record.getFieldList() ) {
//						System.out.println("\t" + q);
//					}
//				}
//			}
		} finally {
			conn.close();
		}
		
	}

	public static void main(String[] args) {
		try {
			new TestEmployeeFileParse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class EmployeeRecordTransformer implements Transformer<EmployeeRecord, EmployeeRecord> {

		private PreparedStatement statement;		
		

		public EmployeeRecordTransformer(PreparedStatement statement) {
			super();
			this.statement = statement;
		}


		@Override
		public EmployeeRecord transform(EmployeeRecord arg0) {
			try {
				statement.setInt(1, Integer.valueOf(arg0.getEmployeeCode()));
				statement.setString(2, arg0.getFirstName().toLowerCase());
				statement.setString(3, arg0.getLastName().toLowerCase());
				ResultSet rs = statement.executeQuery();
				if ( rs.next() ) {
					EmployeeRecord record = new EmployeeRecord(rs);
					if (arg0.equals(record)) {
						arg0.setRecordStatus(EmployeeRecordStatus.EXISTS.toString());
					} else {
						arg0.setRecordStatus(EmployeeRecordStatus.MODIFIED.toString());
//						System.out.println(arg0);
//						System.out.println(record);
//						System.out.println("******************");
					}
				} else {
					arg0.setRecordStatus(EmployeeRecordStatus.NEW.toString());
				}
//				System.out.println(arg0.getEmployeeCode() + "\t" + arg0.getRecordStatus());
			} catch ( Exception e) {
				throw new RuntimeException(e);
			}
			return arg0;
		}
		
	}
}
