package com.ansi.scilla.web.bcr.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.bcr.common.BcrTicketSql;
import com.ansi.scilla.web.bcr.response.BudgetControlActualDlResponse.ActualDL;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class BudgetControlTotalsResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private static final String selectClause = "select \n" + 
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
	
	private static final String groupClause = "\n) as detail group by claim_week\norder by claim_week";

	private Integer divisionId;
	private List<BCRTotalsDetail> weekTotals;
	private BCRTotalsDetail monthTotal;
	private BudgetControlActualDlResponse actualDl;
	private BudgetControlEmployeesResponse employees;
	private List<String> claimWeeks;
	
	
	public BudgetControlTotalsResponse() {
		super();
	}

	public BudgetControlTotalsResponse(Connection conn, Integer userId, List<SessionDivision> divisionList, Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		this();
		this.divisionId = divisionId;
		this.actualDl = new BudgetControlActualDlResponse(conn, userId, divisionList, divisionId, workYear, workWeek);
		makeTotalsResponse(conn, userId, divisionList, divisionId, workYear, workWeek, this.actualDl);
		makeEmployeeResponse(conn, userId, divisionList, divisionId, workYear, workWeek);
		this.claimWeeks = new ArrayList<String>();
		for ( String week : StringUtils.split(workWeek,",")) {
			claimWeeks.add( String.valueOf(workYear)+"-"+week);
		}
		Collections.sort(claimWeeks);
	}
	
	
	public Integer getDivisionId() {
		return divisionId;
	}
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	public List<BCRTotalsDetail> getWeekTotals() {
		return weekTotals;
	}
	public void setWeekTotals(List<BCRTotalsDetail> weekTotals) {
		this.weekTotals = weekTotals;
	}
	public BCRTotalsDetail getMonthTotal() {
		return monthTotal;
	}
	public void setMonthTotal(BCRTotalsDetail monthTotal) {
		this.monthTotal = monthTotal;
	}
	public BudgetControlActualDlResponse getActualDl() {
		return actualDl;
	}
	public void setActualDl(BudgetControlActualDlResponse actualDl) {
		this.actualDl = actualDl;
	}
	public BudgetControlEmployeesResponse getEmployees() {
		return employees;
	}
	public void setEmployees(BudgetControlEmployeesResponse employees) {
		this.employees = employees;
	}
	public List<String> getClaimWeeks() {
		return claimWeeks;
	}
	public void setClaimWeeks(List<String> claimWeeks) {
		this.claimWeeks = claimWeeks;
	}


	private void makeTotalsResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek, BudgetControlActualDlResponse actualDl) throws SQLException {
		String baseSql = BcrTicketSql.sqlSelectClause + BcrTicketSql.makeFilteredFromClause(divisionList) + BcrTicketSql.makeBaseWhereClause(workWeek);
		String sql = selectClause + baseSql + groupClause;
		this.weekTotals = new ArrayList<BCRTotalsDetail>();
		this.monthTotal = new BCRTotalsDetail();
		
		List<Integer> weekFilter = new ArrayList<Integer>();
		for ( String weekNum : StringUtils.split(workWeek, ",")) {
			weekFilter.add(Integer.valueOf(weekNum));
		}
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, sql);
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, divisionId);
		ps.setInt(2, workYear);
		ResultSet rs = ps.executeQuery();
		while ( rs.next() ) {
			String claimWeek = rs.getString("claim_week");
			logger.log(Level.DEBUG, "claimWeek:<" +claimWeek+">");
			if (!(claimWeek.equals("-"))) {
				BCRTotalsDetail detail = new BCRTotalsDetail(rs, actualDl);
				this.weekTotals.add(detail);
				String[] workDate = claimWeek.split("-");
				if ( weekFilter.contains(Integer.valueOf(workDate[1]))) {
					this.monthTotal.add(detail);
				}
			}
		}
		rs.close();			
		
		if ( monthTotal.getVolumeClaimed() == null || monthTotal.getVolumeClaimed() == 0.0D ) {
			monthTotal.setDlPercentage(0.0D);
		} else {
			monthTotal.setDlPercentage((monthTotal.getDlTotal()/monthTotal.getVolumeClaimed())*100);
		}
		if ( monthTotal.getVolumeClaimed() == null || monthTotal.getTotalVolume() == null ) {
			monthTotal.setActualDlPercentage(0.0D);
		} else {
			monthTotal.setActualDlPercentage( (monthTotal.getTotalVolume() / monthTotal.getVolumeClaimed())*100);
		}
		
	}


	private void makeEmployeeResponse(Connection conn, Integer userId, List<SessionDivision> divisionList,
			Integer divisionId, Integer workYear, String workWeek) throws SQLException {
		this.employees = new BudgetControlEmployeesResponse(conn, userId, divisionList, divisionId, workYear, workWeek);		
	}


	public class BCRTotalsDetail extends ApplicationObject implements Comparable<BCRTotalsDetail> {
		private static final long serialVersionUID = 1L;
		private String claimWeek;
		private Double dlAmt = 0.0D;
		private Double dlTotal = 0.0D;
		private Double totalVolume = 0.0D;
		private Double volumeClaimed = 0.0D;
		private Double passthruVolume = 0.0D;
		private Double claimedVolumeTotal = 0.0D;
		private Double volumeRemaining = 0.0D;
		private Double billedAmount = 0.0D;
		private Double claimedVsBilled = 0.0D;
		private Double dlPercentage = 0.0D;
		private Double actualDlPercentage = 0.0D;
		
		public BCRTotalsDetail() {
			super();
		}
		public BCRTotalsDetail(ResultSet rs, BudgetControlActualDlResponse actualDl) throws SQLException {
			this();
			this.claimWeek = rs.getString("claim_week");
			this.dlAmt = rs.getDouble("dl_amt");
			this.dlTotal = rs.getDouble("dl_total");
			this.totalVolume = rs.getDouble("total_volume");
			this.volumeClaimed = rs.getDouble("volume_claimed");
			this.passthruVolume = rs.getDouble("passthru_volume");
			this.claimedVolumeTotal = rs.getDouble("claimed_volume_total");
			this.volumeRemaining = rs.getDouble("volume_remaining");
			this.billedAmount = rs.getDouble("billed_amount");
			this.claimedVsBilled = rs.getDouble("claimed_vs_billed");			
			this.dlPercentage = this.volumeClaimed == null || this.volumeClaimed == 0.0D ? 0.0D : (this.dlTotal/this.volumeClaimed)*100;
			
			if ( this.volumeClaimed == null ) {
				this.actualDlPercentage = 0.0D;
			} else {
				String[] claimWeekString = StringUtils.split(this.claimWeek, "-");
				Integer weekNum = Integer.valueOf(claimWeekString[1]);
				HashMap<Integer, ActualDL> map = actualDl.getWeekActualDL();
				ActualDL actualDL = map.get(weekNum);				
				this.actualDlPercentage = actualDL == null || actualDL.getTotalDL() == null ? 0.0D : (actualDL.getTotalDL() / this.volumeClaimed)*100;
			}	
		}

		public String getClaimWeek() {
			return claimWeek;
		}

		public void setClaimWeek(String claimWeek) {
			this.claimWeek = claimWeek;
		}

		public Double getDlAmt() {
			return dlAmt;
		}

		public void setDlAmt(Double dlAmt) {
			this.dlAmt = dlAmt;
		}

		public Double getDlTotal() {
			return dlTotal;
		}

		public void setDlTotal(Double dlTotal) {
			this.dlTotal = dlTotal;
		}

		public Double getTotalVolume() {
			return totalVolume;
		}

		public void setTotalVolume(Double totalVolume) {
			this.totalVolume = totalVolume;
		}

		public Double getVolumeClaimed() {
			return volumeClaimed;
		}

		public void setVolumeClaimed(Double volumeClaimed) {
			this.volumeClaimed = volumeClaimed;
		}

		public Double getPassthruVolume() {
			return passthruVolume;
		}

		public void setPassthruVolume(Double passthruVolume) {
			this.passthruVolume = passthruVolume;
		}

		public Double getClaimedVolumeTotal() {
			return claimedVolumeTotal;
		}

		public void setClaimedVolumeTotal(Double claimedVolumeTotal) {
			this.claimedVolumeTotal = claimedVolumeTotal;
		}

		public Double getVolumeRemaining() {
			return volumeRemaining;
		}

		public void setVolumeRemaining(Double volumeRemaining) {
			this.volumeRemaining = volumeRemaining;
		}

		public Double getBilledAmount() {
			return billedAmount;
		}

		public void setBilledAmount(Double billedAmount) {
			this.billedAmount = billedAmount;
		}

		public Double getClaimedVsBilled() {
			return claimedVsBilled;
		}

		public void setClaimedVsBilled(Double claimedVsBilled) {
			this.claimedVsBilled = claimedVsBilled;
		}
		public Double getDlPercentage() {
			return dlPercentage;
		}
		public void setDlPercentage(Double dlPercentage) {
			this.dlPercentage = dlPercentage;
		}
		public Double getActualDlPercentage() {
			return actualDlPercentage;
		}
		public void setActualDlPercentage(Double actualDlPercentage) {
			this.actualDlPercentage = actualDlPercentage;
		}
		
		public void add(BCRTotalsDetail detail) {
			this.dlAmt = this.dlAmt + detail.getDlAmt();
			this.dlTotal = this.dlTotal + detail.getDlTotal();
			this.totalVolume = this.totalVolume + detail.getTotalVolume();
			this.volumeClaimed = this.volumeClaimed + detail.getVolumeClaimed();
			this.passthruVolume = this.passthruVolume + detail.getPassthruVolume();
			this.claimedVolumeTotal = this.claimedVolumeTotal + detail.getClaimedVolumeTotal();
			this.volumeRemaining = this.volumeRemaining + detail.getVolumeRemaining();
			this.billedAmount = this.billedAmount + detail.getBilledAmount();
			this.claimedVsBilled = this.claimedVsBilled + detail.getClaimedVsBilled();
		}
		
		@Override
		public int compareTo(BCRTotalsDetail o) {
			return this.getClaimWeek().compareTo(o.getClaimWeek());
		}
		
		
	}
}
