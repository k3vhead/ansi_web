package com.ansi.scilla.web.test.reports;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.report.response.SubscriptionAdminResponse;

public class TestSubscriptionQuery {

	public static void main(String[] args) {
		try {
			new TestSubscriptionQuery().go();
//			new TestSubscriptionQuery().goResponse();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void goResponse() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			SubscriptionAdminResponse response = new SubscriptionAdminResponse(conn, "PAC_WEEKLY");
			String json = AppUtils.object2json(response);
			System.out.println(json);
		} finally {
			conn.close();
		}
	}

	private void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			Statement s = conn.createStatement();
			ResultSet rsDivision = s.executeQuery("select division_id, division_nbr,division_code from division order by division_nbr");
			List<DivRow> divRowList =new ArrayList<DivRow>();
			while ( rsDivision.next() ) {
				divRowList.add(new DivRow(rsDivision) );
			}
			rsDivision.close();
			String sql = makeSql(divRowList, "PAC_WEEKLY");
			System.out.println(sql);
		} finally {
			conn.close();
		}
	}
	
	private String makeSql(List<DivRow> divRowList, String reportId) {
		StringBuffer sql = new StringBuffer("select subscriber.first_name, subscriber.last_name, subscriber.user_id");
		for ( DivRow divRow : divRowList) {
			sql.append( ",\n" + divRow.divisionCode + ".division_id as " + divRow.divisionCode);
		}
		sql.append("\nfrom ( select distinct ansi_user.first_name, ansi_user.last_name,report_subscription.user_id\n"
				+ "	from report_subscription\n"
				+ "	left outer join ansi_user on ansi_user.user_id=report_subscription.user_id\n"
				+ "	where report_subscription.report_id ='"+ reportId+"' ) subscriber\n");
		for ( DivRow divRow : divRowList ) {
			sql.append("left outer join report_subscription "+divRow.divisionCode + " on "+divRow.divisionCode + ".user_id=subscriber.user_id and "+divRow.divisionCode + ".division_id=" +divRow.divisionId + " and "+divRow.divisionCode + ".report_id='"+reportId +"'\n");
		}
		sql.append("order by subscriber.last_name, subscriber.first_name");
		return sql.toString();
	}

	public class DivRow extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public Integer divisionId;
		public Integer divisionNbr;
		public String divisionCode;
		public DivRow(ResultSet rs) throws SQLException {
			super();
			this.divisionId = rs.getInt("division_id");
			this.divisionNbr = rs.getInt("division_nbr");
			this.divisionCode = rs.getString("division_code");
		}
		
		public String div() { return divisionNbr + "-" + divisionCode; }
		
	}
}
