package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.utils.QMarkTransformer;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BudgetControlActualDlResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Integer workYear;
	private HashMap<Integer, ActualDL> weekActualDL;
	
	private static final String baseSql = "select dl.division_id, dl.claim_year, dl.claim_week, dl.actual_dl, om_dl\n" + 
			"from actual_direct_labor_totals dl\n" + 
			"where division_id=?\n" + 
			"and division_id in (select division_id from division_user where user_id=?)\n" + 
			"and dl.claim_year=?\n" + 
			"and dl.claim_week in $WEEKFILTER$";
	
	private BudgetControlActualDlResponse() {
		super();
	}
	
	public BudgetControlActualDlResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		this();
		this.workYear = workYear;
		this.weekActualDL = makeActualDL(conn, userId, divisionList, divisionId, workYear, workWeek);
		
	}

	
	private HashMap<Integer, ActualDL> makeActualDL(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		
		HashMap<Integer, ActualDL> weekActualDL = new HashMap<Integer, ActualDL>();		
		String[] workWeeks = StringUtils.split(workWeek, ",");		
		
		String sql = baseSql.replaceAll("\\$WEEKFILTER\\$", QMarkTransformer.makeQMarkWhereClause(workWeek));
		int n = 1;
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(n, divisionId);
		n++;
		ps.setInt(n, userId);
		n++;
		ps.setInt(n, workYear);
		n++;
		for ( String week : workWeeks ) {
			ps.setInt(n, Integer.valueOf(week));
			n++;
		}
		ResultSet rs  = ps.executeQuery();
		while ( rs.next() ) {
			weekActualDL.put(rs.getInt("claim_week"), new ActualDL(rs));
		}
		rs.close();
		return weekActualDL;
	}

	
	public Integer getWorkYear() {
		return workYear;
	}

	public void setWorkYear(Integer workYear) {
		this.workYear = workYear;
	}

	public HashMap<Integer, ActualDL> getWeekActualDL() {
		return weekActualDL;
	}

	public void setWeekActualDL(HashMap<Integer, ActualDL> weekActualDL) {
		this.weekActualDL = weekActualDL;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}


	public class ActualDL extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer claimWeek;
		private Double actualDL;
		private Double omDL;
		
		public ActualDL(ResultSet rs) throws SQLException {
			this.claimWeek = rs.getInt("claim_week");
			this.actualDL = rs.getDouble("actual_dl");
			this.omDL = rs.getDouble("om_dl");
		}

		public Integer getClaimWeek() {
			return claimWeek;
		}

		public Double getActualDL() {
			return actualDL;
		}

		public Double getOmDL() {
			return omDL;
		}
		
	}
}
