package com.ansi.scilla.web.test.ticket;

import java.sql.Connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.ticket.response.TicketDetail;

public class TestTicketResponse {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
//			TicketReturnResponse x = new TicketReturnResponse(conn, 849596);
			TicketDetail x = new TicketDetail(conn , 849596);
			Logger logger=LogManager.getLogger(this.getClass());
			logger.log(Level.DEBUG, x);
		} finally {
			conn.close();
		}
	}
	public static void main(String[] args) {
		try {
			new TestTicketResponse().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
