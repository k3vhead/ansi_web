package com.ansi.scilla.web.test.bcr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.struts.SessionDivision;
import com.ansi.scilla.web.test.TesterUtils;

public class TestGenericBcrSql {

	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			String workWeek = "45,46,47,48";
			List<SessionDivision> divisionList = TesterUtils.makeSessionDivisionList(conn, 5);
			Integer divisionId = 101;
			Integer claimYear = 2020;
			
			String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeek);
			System.out.println(baseSql);
			PreparedStatement ps = conn.prepareStatement(baseSql);
			ps.setInt(1,  divisionId);
			ps.setInt(2, claimYear);
			ResultSet rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			while ( rs.next() ) {
				Integer ticketId = rs.getInt("ticket_id");
				if ( ticketId.intValue() == 506645 ) {
					for ( int i = 1; i <= rsmd.getColumnCount(); i++ ) {
//						System.out.println(rsmd.getColumnName(i) + "\t" + rs.getObject(i));
					}
//					System.out.println("***************");
				}
			}
			rs.close();
			
		} finally {
			if(conn != null) {
				conn.close();
			}
		}
	}
	
	public static void main(String[] args) {
		try {
			new TestGenericBcrSql().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
