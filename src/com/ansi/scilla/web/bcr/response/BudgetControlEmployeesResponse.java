package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BudgetControlEmployeesResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private static final String selectClause = "select \n" + 
			"   employee, \n" +
			"	claim_week, \n" + 
			"	sum(detail.dl_amt) as dl_amt,\n" + 
			"	sum(detail.dl_total) as dl_total,\n" + 
			"	sum(detail.total_volume) as total_volume,\n" + 
			"	sum(detail.volume_claimed) as volume_claimed,\n" + 
			"	sum(detail.passthru_volume) as passthru_volume,\n" + 
			"	sum(detail.claimed_volume_total) as claimed_volume_total,\n" + 
			"	sum(detail.volume_remaining) as volume_remaining,\n" + 
			"	sum(detail.billed_amount) as billed_amount,\n" + 
			"	sum(detail.claimed_vs_billed) as claimed_vs_billed\n" + 
			"from (\n";
	
	private static final String groupClause = "\n) as detail \n group by detail.employee, detail.claim_week\norder by detail.employee, detail.claim_week";

	private Integer claimYear;
	private List<String> claimWeeks;
	private List<EmployeeClaim> employees;
	private EmployeeClaim monthlyTotal;
	private Logger logger = LogManager.getLogger(BudgetControlEmployeesResponse.class);
	
	public BudgetControlEmployeesResponse(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		super();
		this.claimYear = workYear;
		this.claimWeeks = new ArrayList<String>();
		for ( String week : StringUtils.split(workWeek,",")) {
			claimWeeks.add( String.valueOf(workYear)+"-"+week);
		}
		Collections.sort(claimWeeks);
		this.employees = new ArrayList<EmployeeClaim>();
		this.monthlyTotal = new EmployeeClaim((String)null, workYear, workWeek);
		makeTotalsResponse(conn, userId, divisionList, divisionId, workYear, workWeek);
		for ( EmployeeClaim claim : employees ) {
			monthlyTotal.add(claim);
		}
	}
	
	public Integer getClaimYear() {
		return claimYear;
	}
	public void setClaimYear(Integer claimYear) {
		this.claimYear = claimYear;
	}
	public List<String> getClaimWeeks() {
		return claimWeeks;
	}
	public void setClaimWeeks(List<String> claimWeeks) {
		this.claimWeeks = claimWeeks;
	}
	public List<EmployeeClaim> getEmployees() {
		return employees;
	}
	public void setEmployees(List<EmployeeClaim> employees) {
		this.employees = employees;
	}
	public EmployeeClaim getMonthlyTotal() {
		return monthlyTotal;
	}
	public void setMonthlyTotal(EmployeeClaim monthlyTotal) {
		this.monthlyTotal = monthlyTotal;
	}

	private void makeTotalsResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeek);
		String sql = selectClause + baseSql + groupClause;
		
		List<Integer> weekFilter = new ArrayList<Integer>();
		for ( String weekNum : StringUtils.split(workWeek, ",")) {
			weekFilter.add(Integer.valueOf(weekNum));
		}
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, sql);
		
		HashMap<String, EmployeeClaim> workMap = new HashMap<String, EmployeeClaim>();  // "string" is employee
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ps.setInt(2, workYear);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			String employee = rs.getString("employee");
			String claimWeek = rs.getString("claim_week");
			Double directLabor = rs.getDouble("dl_amt");
			String[] workDate = claimWeek.split("-");
			if ( weekFilter.contains(Integer.valueOf(workDate[1]))) {
				EmployeeClaim dl = workMap.containsKey(employee) ? workMap.get(employee) : new EmployeeClaim(employee, workYear, workWeek);
				HashMap<String, Double> weeklyDL = dl.getWeeklyClaimedDL();
				weeklyDL.put(claimWeek, directLabor);
				dl.setWeeklyClaimedDL(weeklyDL);
				dl.setTotalClaimedDL(dl.getTotalClaimedDL() + directLabor);
				workMap.put(employee, dl);
			}
		}
		rs.close();
		
		for ( EmployeeClaim employeeDL : workMap.values() ) {
			this.employees.add(employeeDL);
		}
		Collections.sort(this.employees);
	}




	public class EmployeeClaim extends ApplicationObject implements Comparable<EmployeeClaim> {
		private static final long serialVersionUID = 1L;

		private String employee;
		private HashMap<String, Double> weeklyClaimedDL = new HashMap<String, Double>();
		private HashMap<String, Double> weeklyClaimedVolume = new HashMap<String, Double>();
		private Double totalClaimedDL;
		private Double totalClaimedVolume;
		
		public EmployeeClaim(String employee, Integer workYear, String workWeekList) {
			super();
			String[] weekList = workWeekList.split(",");
			this.employee = employee;
			for ( String week : weekList ) {
				weeklyClaimedDL.put( String.valueOf(workYear)+"-"+week, 0.0D);
				weeklyClaimedVolume.put( String.valueOf(workYear)+"-"+week, 0.0D);
			}
			this.totalClaimedDL = 0.0D;
			this.totalClaimedVolume = 0.0D;
		}
		
		public String getEmployee() {
			return employee;
		}




		public void setEmployee(String employee) {
			this.employee = employee;
		}
		public HashMap<String, Double> getWeeklyClaimedDL() {
			return weeklyClaimedDL;
		}
		public void setWeeklyClaimedDL(HashMap<String, Double> weeklyClaimedDL) {
			this.weeklyClaimedDL = weeklyClaimedDL;
		}
		public HashMap<String, Double> getWeeklyClaimedVolume() {
			return weeklyClaimedVolume;
		}
		public void setWeeklyClaimedVolume(HashMap<String, Double> weeklyClaimedVolume) {
			this.weeklyClaimedVolume = weeklyClaimedVolume;
		}
		public Double getTotalClaimedDL() {
			return totalClaimedDL;
		}
		public void setTotalClaimedDL(Double totalClaimedDL) {
			this.totalClaimedDL = totalClaimedDL;
		}
		public Double getTotalClaimedVolume() {
			return totalClaimedVolume;
		}
		public void setTotalClaimedVolume(Double totalClaimedVolume) {
			this.totalClaimedVolume = totalClaimedVolume;
		}

		public void add(EmployeeClaim dl) {
			for ( Map.Entry<String, Double> week : dl.getWeeklyClaimedDL().entrySet() ) {
				String claimWeek = week.getKey();
				Double currentDL = this.weeklyClaimedDL.get(claimWeek) == null ? 0.0D : this.weeklyClaimedDL.get(claimWeek);
				Double newDL = currentDL + dl.getWeeklyClaimedDL().get(claimWeek);
				this.weeklyClaimedDL.put(claimWeek, newDL);				
				Double currentVolume = this.weeklyClaimedVolume.get(claimWeek) == null ? 0.0D : this.weeklyClaimedVolume.get(claimWeek);
				Double newVolume = currentVolume + dl.getWeeklyClaimedVolume().get(claimWeek);
				this.weeklyClaimedVolume.put(claimWeek, newVolume);				
			}
			
			this.totalClaimedDL = 0.0D;
			for ( Double weeklyDL : this.getWeeklyClaimedDL().values() ) {
				this.totalClaimedDL = this.totalClaimedDL + weeklyDL;
			}
			this.totalClaimedVolume = 0.0D;
			for ( Double weeklyVolume : this.getWeeklyClaimedVolume().values() ) {
				this.totalClaimedVolume = this.totalClaimedVolume + weeklyVolume;
			}
		}

		@Override
		public int compareTo(EmployeeClaim o) {
			return this.employee.compareTo(o.getEmployee());
		}
		
		
	}
	
		
		
}
