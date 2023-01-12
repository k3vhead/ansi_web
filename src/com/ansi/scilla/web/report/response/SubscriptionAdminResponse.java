package com.ansi.scilla.web.report.response;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.response.MessageResponse;

public class SubscriptionAdminResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private List<DivRow> divisionList;
	private List<HashMap<String, Object>> subscriptions;
	
	public SubscriptionAdminResponse() {
		super();
	}
	
	public SubscriptionAdminResponse(Connection conn, String reportId) throws SQLException {
		this();
		Statement s = conn.createStatement();
		ResultSet rsDivision = s.executeQuery("select division_id, division_nbr,division_code from division order by division_nbr");
		this.divisionList =new ArrayList<DivRow>();
		while ( rsDivision.next() ) {
			divisionList.add(new DivRow(rsDivision) );
		}
		rsDivision.close();
		subscriptions = new ArrayList<HashMap<String, Object>>(); 
		String sql = makeSql(divisionList, "PAC_WEEKLY");
		ResultSet rsSubscription = s.executeQuery(sql);
		ResultSetMetaData rsSubscriptionMetaData = rsSubscription.getMetaData();
		while ( rsSubscription.next() ) {
			HashMap<String, Object> row = new HashMap<String, Object>();
			for ( int i = 0; i < rsSubscriptionMetaData.getColumnCount(); i++ ) {
				int idx = i + 1;
				String key = rsSubscriptionMetaData.getColumnName(idx);
				Object value = rsSubscription.getObject(idx);
				row.put( key, value );
			}
			subscriptions.add(row);
		}
		rsSubscription.close();
	}

	
	public List<HashMap<String, Object>> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<HashMap<String, Object>> subscriptions) {
		this.subscriptions = subscriptions;
	}

	public List<DivRow> getDivisionList() {
		return divisionList;
	}

	public void setDivisionList(List<DivRow> divisionList) {
		this.divisionList = divisionList;
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
