package com.ansi.scilla.web.callNote.request;

import java.sql.Connection;
import java.util.Calendar;
import java.util.Date;

import com.ansi.scilla.common.callNote.CallNoteReference;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.CallLog;
import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.fasterxml.jackson.annotation.JsonFormat;

public class CallNoteRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String XREF_TYPE = "xrefType";
	public static final String XREF_ID = "xrefId";
	public static final String CALL_NOTE_ID = "callNoteId";
	public static final String ADDRESS_ID = "addressId";
	public static final String USER_ID = "userId";
  	public static final String CONTACT_ID = "contactId";
  	public static final String START_DATE = "startDate";
  	public static final String START_TIME = "startTime";
  	public static final String CONTACT_TYPE = "contactType";
  	public static final String SUMMARY = "summary";
  	public static final String NOTES = "notes";

  	
  	
  	
	private String xrefType;
	private Integer xrefId;
	private Integer callNoteId;
  	private Integer addressId;
  	private Integer userId;
  	private Integer contactId;
  	private Calendar startDate;
  	private Calendar startTime;
  	private String contactType;
  	private String summary;
  	private String notes;

	public CallNoteRequest() {
		super();
	}

	public String getXrefType() {
		return xrefType;
	}
	public void setXrefType(String xrefType) {
		this.xrefType = xrefType;
	}
	public Integer getXrefId() {
		return xrefId;
	}
	public void setXrefId(Integer xrefId) {
		this.xrefId = xrefId;
	}
	public Integer getCallNoteId() {
		return callNoteId;
	}
	public void setCallNoteId(Integer callNoteId) {
		this.callNoteId = callNoteId;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getContactId() {
		return contactId;
	}
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public Calendar getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="America/Chicago")
	public void setStartDate(Calendar startDate) {
		this.startDate = startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="America/Chicago")
	public Calendar getStartTime() {
		return startTime;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="HH:mm", timezone="America/Chicago")
	public void setStartTime(Calendar startTime) {
		this.startTime = startTime;
	}
	public String getContactType() {
		return contactType;
	}
	public void setContactType(String contactType) {
		this.contactType = contactType;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}


	


	public WebMessages validateAdd(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateCallNoteXrefType(webMessages, XREF_TYPE, xrefType, true);
		RequestValidator.validateId(conn, webMessages, Address.TABLE, Address.ADDRESS_ID, ADDRESS_ID, addressId, true);
		RequestValidator.validateId(conn, webMessages, Contact.TABLE, Contact.CONTACT_ID, CONTACT_ID, contactId, true);
		RequestValidator.validateId(conn, webMessages, User.TABLE, User.USER_ID, USER_ID, userId, true);
		RequestValidator.validateString(webMessages, SUMMARY, summary, 300, true);
		RequestValidator.validateString(webMessages, NOTES, notes, 4000, true);
		RequestValidator.validateContactType(conn, webMessages, CONTACT_TYPE, contactType, true);
		CallNoteReference callNoteReference = CallNoteReference.valueOf(xrefType);
		RequestValidator.validateId(conn, webMessages, callNoteReference.table(), callNoteReference.idField(), XREF_ID, xrefId, true);
		RequestValidator.validateDate(webMessages, START_DATE, startDate, true, (Date)null, (Date)null);
		RequestValidator.validateDate(webMessages, START_TIME, startTime, true, (Date)null, (Date)null);
		return webMessages;
	}

	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateCallNoteXrefType(webMessages, XREF_TYPE, xrefType, true);
		RequestValidator.validateId(conn, webMessages, Address.TABLE, Address.ADDRESS_ID, ADDRESS_ID, addressId, true);
		RequestValidator.validateId(conn, webMessages, Contact.TABLE, Contact.CONTACT_ID, CONTACT_ID, contactId, true);
		RequestValidator.validateId(conn, webMessages, User.TABLE, User.USER_ID, USER_ID, userId, true);
		RequestValidator.validateId(conn, webMessages, CallLog.TABLE, CallLog.CALL_LOG_ID, CALL_NOTE_ID, callNoteId, true);
		RequestValidator.validateString(webMessages, SUMMARY, summary, 300, true);
		RequestValidator.validateString(webMessages, NOTES, notes, 4000, true);
		RequestValidator.validateContactType(conn, webMessages, CONTACT_TYPE, contactType, true);
		CallNoteReference callNoteReference = CallNoteReference.valueOf(xrefType);
		RequestValidator.validateId(conn, webMessages, callNoteReference.table(), callNoteReference.idField(), XREF_ID, xrefId, true);
		RequestValidator.validateDate(webMessages, START_DATE, startDate, true, (Date)null, (Date)null);
		RequestValidator.validateDate(webMessages, START_TIME, startTime, true, (Date)null, (Date)null);
		return webMessages;
	}

}
