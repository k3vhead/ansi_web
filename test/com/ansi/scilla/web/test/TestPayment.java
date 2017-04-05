package com.ansi.scilla.web.test;

import java.sql.Connection;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.response.payment.PaymentResponse;

public class TestPayment {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
			new TestPayment().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws Exception {

		Connection conn = null;
		try {
			conn = AppUtils.getConn();
			PaymentResponse data = new PaymentResponse(conn, 128217);
			System.out.println(data);
		} finally {
			if(conn !=null){
				conn.close();
			}
		}
		
	}

}
