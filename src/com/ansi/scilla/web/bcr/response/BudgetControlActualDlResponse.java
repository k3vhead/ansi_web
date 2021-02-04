package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.utils.QMarkTransformer;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BudgetControlActualDlResponse extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private Integer workYear;
	private HashMap<Integer, ActualDL> weekActualDL;
	private ActualDL totalActualDL;
	
	private static final String baseSql = "select dl.division_id, dl.claim_year, dl.claim_week, dl.actual_dl, om_dl\n" + 
			"from actual_direct_labor_totals dl\n" + 
			"where division_id=?\n" + 
			"and division_id in (select division_id from division_user where user_id=?)\n" + 
			"and dl.claim_year=?\n" + 
			"and dl.claim_week in $WEEKFILTER$";
	
	public BudgetControlActualDlResponse() {
		super();
	}
	
	public BudgetControlActualDlResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		this();
		this.workYear = workYear;
		makeActualDL(conn, userId, divisionList, divisionId, workYear, workWeek);
	}

	
	private void makeActualDL(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		
		this.weekActualDL = new HashMap<Integer, ActualDL>();
		this.totalActualDL = new ActualDL();
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
			Integer claimWeek = rs.getInt("claim_week");
			ActualDL weeklyDL = new ActualDL(rs);
			this.weekActualDL.put(claimWeek, weeklyDL);
			this.totalActualDL.add(weeklyDL);
		}
		rs.close();
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

	public ActualDL getTotalActualDL() {
		return totalActualDL;
	}

	public void setTotalActualDL(ActualDL totalActualDL) {
		this.totalActualDL = totalActualDL;
	}





	public class ActualDL extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer claimWeek;
		private Double actualDL;
		private Double omDL;
		private Double totalDL;
		
		public ActualDL() {
			super();
			this.actualDL = 0.0D;
			this.omDL = 0.0D;
			this.totalDL = 0.0D;
		}
		
		

		public ActualDL(ResultSet rs) throws SQLException {
			this();
			this.claimWeek = rs.getInt("claim_week");
			this.actualDL = rs.getDouble("actual_dl");
			this.omDL = rs.getDouble("om_dl");
			this.totalDL = this.actualDL + this.omDL;
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

		public Double getTotalDL() {
			return totalDL;
		}

		public void add(ActualDL weeklyDL) {
			this.actualDL = this.actualDL == null ? weeklyDL.getActualDL() : this.actualDL + weeklyDL.getActualDL();
			this.omDL = this.omDL == null ? weeklyDL.getOmDL() : this.omDL + weeklyDL.getOmDL();
			this.totalDL = this.totalDL == null ? weeklyDL.getTotalDL() : this.totalDL + weeklyDL.getTotalDL();			
		}

		
		
	}
}
