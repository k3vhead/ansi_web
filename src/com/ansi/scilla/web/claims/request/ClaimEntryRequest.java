package com.ansi.scilla.web.claims.request;

import java.sql.Connection;
import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class ClaimEntryRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String TYPE="type";
	public static final String WORK_DATE = "workDate";
	public static final String WASHER_NAME = "washerName";
	public static final String WASHER_ID = "washerId";
	public static final String VOLUME = "volume";
	public static final String DL_AMT = "dlAmt";
	public static final String HOURS = "hours";
	public static final String NOTES = "notes";
	
	private String type;
	private Date workDate;
	private String washerName;
	private Integer washerId;
	private Double volume;
	private Double dlAmt;
	private Double hours;
	private String notes;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getWorkDate() {
		return workDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setWorkDate(Date workDate) {
		this.workDate = workDate;
	}
	public String getWasherName() {
		return washerName;
	}
	public void setWasherName(String washerName) {
		this.washerName = washerName;
	}
	public Integer getWasherId() {
		return washerId;
	}
	public void setWasherId(Integer washerId) {
		this.washerId = washerId;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getDlAmt() {
		return dlAmt;
	}
	public void setDlAmt(Double dlAmt) {
		this.dlAmt = dlAmt;
	}
	public Double getHours() {
		return hours;
	}
	public void setHours(Double hours) {
		this.hours = hours;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	
	
	
	protected WebMessages validateAddDirectLabor(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateDate(webMessages, ClaimEntryRequest.WORK_DATE, this.workDate, true, null, null);
		RequestValidator.validateWasherId(conn, webMessages, ClaimEntryRequest.WASHER_ID, this.washerId, true);
		RequestValidator.validateDouble(webMessages, ClaimEntryRequest.VOLUME, this.volume, null, null, true);
		RequestValidator.validateDouble(webMessages, ClaimEntryRequest.DL_AMT, this.dlAmt, null, null, true);
		RequestValidator.validateDouble(webMessages, ClaimEntryRequest.HOURS, this.hours, null, null, true);
		RequestValidator.validateString(webMessages, ClaimEntryRequest.NOTES, this.notes, 1024, false);
		return webMessages;
	}
	

}
