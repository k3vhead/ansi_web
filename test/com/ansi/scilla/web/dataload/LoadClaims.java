package com.ansi.scilla.web.dataload;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;
import java.util.List;

import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.common.utils.AppUtils;

import au.com.bytecode.opencsv.CSVReader;

public class LoadClaims {

	private final Integer TICKET_ID=1;
	private final Integer WASHER_ID=2;
	private final Integer WORK_DATE = 3;
	private final Integer VOLUME = 4;
	private final Integer DL_AMT = 5;
	private final Integer HOURS = 6;
	private final Integer NOTES = 7;
	private final Integer ADDED_BY = 8;
	private final Integer ADDED_DATE = 9;
	private final Integer UPDATED_BY = 10;
	private final Integer UPDATED_DATE = 11;
	
	private void go() throws Exception {
		Connection conn = null;
		DateTimeFormatter workDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter auditDateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			PreparedStatement ps = conn.prepareStatement("insert into ticket_claim (ticket_id,service_type,claim_year,claim_week,volume,dl_amt,dl_expenses,passthru_expense_volume,passthru_expense_type,hours,notes,employee_name,added_by,added_date,updated_by,updated_date) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			PreparedStatement worker = conn.prepareStatement("select first_name, last_name from ansi_user where user_id=?");
			
			CSVReader reader = new CSVReader(new FileReader("/home/dclewis/Documents/webthing_v2/projects/ANSI/data/20201209_ticket_claims/20201208_ticket_claim_data.csv"));
			List<String[]> lines = reader.readAll();
			for ( int i=1; i < lines.size(); i++ ) {
				String[] line = lines.get(i);
				System.out.println(line[0]);
				LocalDate workDate = LocalDate.parse(line[WORK_DATE],workDateFormat);
				WorkYear workYear = new WorkYear(workDate.getYear());
				GregorianCalendar date = new GregorianCalendar(workDate.getYear(), workDate.getMonthValue()-1, workDate.getDayOfMonth(),0,0,0);
				Integer workWeek = workYear.getWeekOfYear(date);
				LocalDateTime addedDate = LocalDateTime.parse(line[ADDED_DATE],auditDateFormat);
				LocalDateTime updatedDate =LocalDateTime.parse(line[UPDATED_DATE],auditDateFormat);
				String workerName = makeWorker(worker, line[WASHER_ID]);
				
				ps.setInt(1, Integer.valueOf(line[TICKET_ID]));
				ps.setInt(2, 2);
				ps.setInt(3, workDate.getYear());
				ps.setInt(4, workWeek);
				ps.setDouble(5, Double.valueOf(line[VOLUME]));
				ps.setDouble(6, Double.valueOf(line[DL_AMT]));
				ps.setDouble(7, 0.0D);	//expenses
				ps.setNull(8, Types.DOUBLE);	// passthru
				ps.setNull(9,  Types.INTEGER);  // expense type
				ps.setDouble(10, Double.valueOf(line[HOURS]));
				ps.setString(11, line[NOTES]);
				ps.setString(12, workerName);
				ps.setInt(13, Integer.valueOf(line[ADDED_BY]));
				ps.setDate(14, makeDate(addedDate));
				ps.setInt(15, Integer.valueOf(line[UPDATED_BY]));
				ps.setDate(16, makeDate(updatedDate));
				ps.executeUpdate();
				
			}
			reader.close();
			
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	private Date makeDate(LocalDateTime updatedDate) {
		Instant instant = updatedDate.atZone(ZoneId.systemDefault()).toInstant();
		java.util.Date utilDate = java.util.Date.from(instant);
		java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
		return sqlDate;
	}

	private String makeWorker(PreparedStatement worker, String washerId) throws Exception {
		worker.setInt(1,Integer.valueOf(washerId));
		ResultSet rs = worker.executeQuery();
		String name = null;
		if  ( rs.next() ) {
			name = rs.getString("first_name") + " " + rs.getString("last_name");
		} else {
			throw new Exception("Bad userid: " + washerId);
		}
		return name;
	}

	public static void main(String[] args) {
		try {
			new LoadClaims().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
