package com.ansi.scilla.web.bcr.common;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;

/**
 * Result from the base query in BcrTicketSql
 * 
 * @author dclewis
 *
 */
public class BcrTicket extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private String jobSiteName;
	private Integer ticketId;
	private Integer claimId;
	private String claimWeek;
	private Double dlAmt;
	private Double dlTotal;
	private Double totalVolume;
	private Double volumeClaimed;
	private Double passthruVolume;
	private String passthruExpenseType;
	private Double claimedVolumeTotal;
	private Double volumeRemaining;
	private String serviceTagId;
	private String notes;
	private Double billedAmount;
	private Double claimedVsBilled;
	private String ticketStatus;
	private String employee;
	private String equipmentTags;
	
	public BcrTicket() {
		super();
	}
	
	public BcrTicket(ResultSet rs) throws SQLException {
		this();
		this.jobSiteName = rs.getString(BcrTicketSql.JOB_SITE_NAME);
		this.ticketId = rs.getInt(BcrTicketSql.TICKET_ID);
		this.claimWeek = rs.getString(BcrTicketSql.CLAIM_WEEK);
		this.dlAmt = rs.getDouble(BcrTicketSql.DL_AMT);
		this.dlTotal = rs.getDouble(BcrTicketSql.DL_TOTAL);
		this.totalVolume = rs.getDouble(BcrTicketSql.TOTAL_VOLUME);
		this.volumeClaimed = rs.getDouble(BcrTicketSql.VOLUME_CLAIMED);
		this.passthruVolume = rs.getDouble(BcrTicketSql.PASSTHRU_VOLUME);
		this.passthruExpenseType = rs.getString(BcrTicketSql.PASSTHRU_EXPENSE_TYPE);
		this.claimedVolumeTotal = rs.getDouble(BcrTicketSql.CLAIMED_VOLUME_TOTAL);
		this.volumeRemaining = rs.getDouble(BcrTicketSql.VOLUME_REMAINING);
		this.serviceTagId = rs.getString(BcrTicketSql.SERVICE_TAG_ID);
		this.notes = rs.getString(BcrTicketSql.NOTES);
		this.billedAmount = rs.getDouble(BcrTicketSql.BILLED_AMOUNT);
		this.claimedVsBilled = rs.getDouble(BcrTicketSql.CLAIMED_VS_BILLED);
		this.ticketStatus = rs.getString(BcrTicketSql.TICKET_STATUS);
		this.employee = rs.getString(BcrTicketSql.EMPLOYEE);
		// display claimed equipment instead of equipment assigned to the job 
		this.equipmentTags = rs.getString(BcrTicketSql.CLAIMED_EQUIPMENT); //rs.getString(BcrTicketSql.EQUIPMENT_TAGS);
		this.claimId = rs.getInt(BcrTicketSql.CLAIM_ID);
	}
	
	
	public String getJobSiteName() {
		return jobSiteName;
	}

	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}

	public Integer getTicketId() {
		return ticketId;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
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

	public String getPassthruExpenseType() {
		return passthruExpenseType;
	}

	public void setPassthruExpenseType(String passthruExpenseType) {
		this.passthruExpenseType = passthruExpenseType;
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

	public String getServiceTagId() {
		return serviceTagId;
	}

	public void setServiceTagId(String serviceTagId) {
		this.serviceTagId = serviceTagId;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
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

	public String getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public String getEmployee() {
		return employee;
	}

	public void setEmployee(String employee) {
		this.employee = employee;
	}

	public String getEquipmentTags() {
		return equipmentTags;
	}

	public void setEquipmentTags(String equipmentTags) {
		this.equipmentTags = equipmentTags;
	}

	public Integer getClaimId() {
		return claimId;
	}

	public void setClaimId(Integer claimId) {
		this.claimId = claimId;
	}


	
}
