package com.ansi.scilla.web.test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.ansi.scilla.common.db.TicketPayment;
import com.ansi.scilla.web.common.utils.AppUtils;

public class TestMyMath {

	public static void main(String[] args) {
		
		try {
//			new TestMyMath().go();
			new TestMyMath().goRound();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void goRound() {
		Double x = 5.75D;
		Double y = 5.76D;
		
		
		BigDecimal xd = (new BigDecimal(x)).round(MathContext.DECIMAL32);
		BigDecimal yd = (new BigDecimal(y)).round(MathContext.DECIMAL32);
		
		
		System.out.println(xd);
		System.out.println(yd);
	}
	
	
	public void go() throws Exception {
		BigDecimal smaller = new BigDecimal(100);
		BigDecimal bigger = new BigDecimal(200);
		
		System.out.println(smaller.compareTo(bigger));
		System.out.println(bigger.compareTo(smaller));
		
		
		
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			Statement s = conn.createStatement();
			ResultSet rs = s.executeQuery("select sum(amount) as xyz from ticket_payment where ticket_id=671544");
			if ( rs.next() ) {
				BigDecimal bd = rs.getBigDecimal("xyz");
				System.out.println(bd);
			}
			rs.close();
			
			TicketPayment tp = new TicketPayment();
			tp.setAddedBy(5);
			tp.setAmount(new BigDecimal(125));
			tp.setPaymentId(1);
			tp.setStatus(0);
			tp.setTaxAmt(BigDecimal.ZERO);
			tp.setTicketId(671544);
			tp.setUpdatedBy(5);
			tp.insertWithNoKey(conn);
			
			rs = s.executeQuery("select sum(amount) as xyz from ticket_payment where ticket_id=671544");
			if ( rs.next() ) {
				BigDecimal bd = rs.getBigDecimal("xyz");
				System.out.println(bd);
			}
			rs.close();
			
			conn.rollback();
		} finally {
			conn.close();
		}
	}

}
