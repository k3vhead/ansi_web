package com.ansi.scilla.web.test.specialOverride;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;

public class TestSpecialOverride {

	public static void main(String[] args) {
		try {
			new TestSpecialOverride().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Integer paymentId = 47196;
			java.sql.Date oldPaymentDate = new java.sql.Date(new GregorianCalendar(2019, Calendar.AUGUST, 10).getTimeInMillis());
			java.sql.Date newPaymentDate = new java.sql.Date(new GregorianCalendar(2019, Calendar.AUGUST, 11).getTimeInMillis());
			
			PreparedStatement ps = conn.prepareStatement(SpecialOverrideType.UPDATE_PAYMENT_DATE.getSelectSql());
			ps.setInt(1, paymentId);
			ps.setDate(2, oldPaymentDate);
			
			
			
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

}
