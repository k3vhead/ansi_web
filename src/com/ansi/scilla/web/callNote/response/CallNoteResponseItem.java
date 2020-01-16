package com.ansi.scilla.web.callNote.response;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;

public class CallNoteResponseItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	

	private Integer callLogId;
	private Integer addressId;
	private String summary;	
	private String content; 
	private Integer contactId;
	private Integer userId; 
	private Date startTime; 
	private String contactType; 
	private String addressName;
	private String contactName;
	 
	public CallNoteResponseItem() {
		super();
	}
	public CallNoteResponseItem(ResultSet rs) throws SQLException {
		this();
		this.callLogId = rs.getInt(CallNoteResponse.CALL_LOG_ID);
		this.addressId = rs.getInt(CallNoteResponse.ADDRESS_ID);
		this.summary = rs.getString(CallNoteResponse.SUMMARY);
		this.content = rs.getString(CallNoteResponse.CONTENT);
		this.contactId = rs.getInt(CallNoteResponse.CONTACT_ID);
		this.userId = rs.getInt(CallNoteResponse.USER_ID);
		this.startTime = (Date)rs.getDate(CallNoteResponse.START_TIME);
		this.contactType = rs.getString(CallNoteResponse.CONTACT_TYPE);
		this.addressName = rs.getString(CallNoteResponse.ADDRESS_NAME);
		this.contactName = rs.getString(CallNoteResponse.CONTACT_NAME);
	}

	public Integer getCallLogId() {
		return callLogId;
	}
	public void setCallLogId(Integer callLogId) {
		this.callLogId = callLogId;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getContactId() {
		return contactId;
	}
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public String getContactType() {
		return contactType;
	}
	public void setContactType(String contactType) {
		this.contactType = contactType;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
}
