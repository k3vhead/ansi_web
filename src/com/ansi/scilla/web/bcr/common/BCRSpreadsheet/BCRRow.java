package com.ansi.scilla.web.bcr.common.BCRSpreadsheet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;

public class BCRRow extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	public String jobSiteName; 
	public Integer ticketId;
	public String claimWeek;
	public Double dlAmt;
	public Double totalVolume;
	public Double volumeClaimed;
	public Double expenseVolume;
	public Double volumeRemaining;
	public String notes;
	public Double billedAmount;
	public Double diffClmBld;
	public String ticketStatus;
	public String service;
	public String equipment;
	public String employee;
	public String claimedEquipment;
	public String unclaimedEquipment;
	
	public BCRRow(ResultSet rs) throws SQLException {
		super();
		this.jobSiteName = rs.getString("job_site_name");
		this.ticketId = rs.getInt("ticket_id");
		this.claimWeek = rs.getString("claim_week");
		this.dlAmt = rs.getDouble("dl_amt");
		this.totalVolume = rs.getDouble("total_volume");
		this.volumeClaimed = rs.getDouble("volume_claimed");
		this.expenseVolume = rs.getDouble("passthru_volume");
		this.volumeRemaining = rs.getDouble("volume_remaining");
		this.notes = rs.getString("notes");
		this.billedAmount = rs.getDouble("billed_amount");
		this.diffClmBld = rs.getDouble("claimed_vs_billed");
		this.ticketStatus = rs.getString("ticket_status");
		this.service = rs.getString("service_tag_id");
		this.equipment = rs.getString("equipment_tags");
		this.employee = rs.getString("employee");
		this.claimedEquipment = rs.getString("claimed_equipment");
		this.unclaimedEquipment = makeUnclaimedEquipment();
	}

	private String makeUnclaimedEquipment() {
		List<String> allEquipment = StringUtils.isBlank(this.equipment) ? new ArrayList<String>() : Arrays.asList(StringUtils.split(this.equipment,",")); 
		List<String> claimedEquipment = StringUtils.isBlank(this.claimedEquipment) ? new ArrayList<String>() : Arrays.asList(StringUtils.split(this.claimedEquipment,","));
		List<String> unclaimedEquipment = IterableUtils.toList(CollectionUtils.subtract(allEquipment, claimedEquipment));
		
		return StringUtils.join(unclaimedEquipment, ",");
	}
}
