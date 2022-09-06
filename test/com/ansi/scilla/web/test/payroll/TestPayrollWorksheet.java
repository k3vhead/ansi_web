package com.ansi.scilla.web.test.payroll;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.utils.AppUtils;

public class TestPayrollWorksheet {

	public void go() throws Exception {
		Connection conn = null;
		Calendar today = Calendar.getInstance(new AnsiTime());
		java.sql.Date todayDate =new java.sql.Date(today.getTime().getTime());
		Calendar friday = new GregorianCalendar(2021, Calendar.NOVEMBER, 19);
		java.sql.Date fridayDate = new java.sql.Date(friday.getTime().getTime());
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			/*
			PayrollWorksheet pw = new PayrollWorksheet();
			pw.setAddedBy(5);
			pw.setAddedDate(today.getTime());
			pw.setCity(null);
			pw.setDirectLabor(new BigDecimal(12.3D));
			pw.setDivisionId(102);
			pw.setEmployeeCode(1869);
			pw.setEmployeeName("AARON CROPPER");
			pw.setExpenses(new BigDecimal(0.0D));
			pw.setExpensesAllowed(BigDecimal.ZERO);
			pw.setGrossPay(new BigDecimal(12.3D));
			pw.setProductivity(new BigDecimal(1.00D));
			pw.setState("OH");
			pw.setUpdatedBy(5);
			pw.setUpdatedDate(today.getTime());
//			private BigDecimal expensesAllowed;
//			private BigDecimal expensesSubmitted;
//			private BigDecimal holidayHours;
//			private BigDecimal holidayPay;
//			private BigDecimal otHours;
//			private BigDecimal otPay;
//			private BigDecimal regularHours;
//			private BigDecimal regularPay;
//			private BigDecimal vacationHours;
//			private BigDecimal vacationPay;
//			private BigDecimal volume;
			private Date weekEnding;
			pw.insertWithNoKey(conn);
			*/
			
			final String sql = "INSERT INTO ansi_sched.dbo.payroll_worksheet " +
			"(division_id, week_ending, state, city, employee_code, employee_name, regular_hours, regular_pay, expenses, ot_hours, ot_pay, vacation_hours, vacation_pay, holiday_hours, holiday_pay, gross_pay, expenses_submitted, expenses_allowed, volume, direct_labor, productivity, added_by, added_date, updated_by, updated_date) " +
			" VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, 102);//			division_id, 
			ps.setDate(2, fridayDate);//			week_ending
			ps.setString(3, "OH");//			state
			ps.setString(4, "");//			city
			ps.setInt(5, 1869);// employee_code
			ps.setString(6, "Aaron Cropper"); // employee_name
			ps.setDouble(7, 0.0D); // regular_hours
			ps.setDouble(8, 0.0D); // regular_pay
			ps.setDouble(9, 0.0D); // expenses
			ps.setDouble(10, 0.0D); // ot_hours
			ps.setDouble(11, 0.0D); // ot_pay
			ps.setDouble(12, 0.0D); // vacation_hours
			ps.setDouble(13, 0.0D); // vacation_pay
			ps.setDouble(14, 0.0D); // holiday_hours
			ps.setDouble(15, 0.0D); // holiday_pay
			ps.setDouble(16, 0.0D); // gross_pay
			ps.setDouble(17, 0.0D); // expenses_submitted
			ps.setDouble(18, 0.0D); // expenses_allowed
			ps.setDouble(19, 0.0D); // volume
			ps.setDouble(20, 0.0D); // direct_labor
			ps.setDouble(21, 0.0D); // productivity
			ps.setInt(22, 5); // added_by
			ps.setDate(23, todayDate); // added_date
			ps.setInt(24, 5); // updated_by
			ps.setDate(25, todayDate); // updated_date
			ps.executeUpdate();
			conn.commit();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestPayrollWorksheet().go();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
