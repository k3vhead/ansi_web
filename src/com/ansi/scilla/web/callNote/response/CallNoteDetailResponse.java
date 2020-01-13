package com.ansi.scilla.web.callNote.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class CallNoteDetailResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	public static final String CALL_LOG_ID = "call_log_id";
	public static final String ADDRESS_ID = "address_id";
	public static final String SUMMARY = "summary";
	public static final String CONTENT = "content"; 
	public static final String CONTACT_ID = "contact_id";
	public static final String USER_ID = "user_id"; 
	public static final String START_TIME = "start_time"; 
	public static final String CONTACT_TYPE = "contact_type"; 
	public static final String ADDRESS_NAME = "address_name";
	public static final String CONTACT_NAME = "contact_name";
	
	private final String sql = "select call_log.call_log_id, \n" + 
			"		call_log.content,\n" + 
			"		call_log.address_id, \n" + 
			"		address.name as address_name,\n" + 
			"		address.address1,\n" + 
			"		address.address2,\n" + 
			"		address.city,\n" + 
			"		address.state,\n" + 
			"		address.zip,\n" + 
			"		call_log.contact_id, \n" + 
			"		concat(contact.first_name, ' ', contact.last_name) as contact_name,\n" + 
			"		call_log.summary,\n" + 
			"		call_log.user_id, \n" + 
			"		concat(ansi_user.first_name, ' ', ansi_user.last_name) as ansi_contact,\n" + 
			"		ansi_user.phone as ansi_phone,\n" + 
			"		ansi_user.email as ansi_email,\n" + 
			"		call_log.start_time,\n" + 
			"		code.display_value as contact_type,\n" + 
			"       call_log_xref.xref_type,\n" + 
			"       call_log_xref.xref_id,\n" + 
			"       concat(call_log_xref.xref_type, ' ', call_log_xref.xref_id) as xref\n" + 
			"from call_log\n" + 
			"inner join address on address.address_id = call_log.address_id\n" + 
			"inner join contact on contact.contact_id = call_log.contact_id\n" + 
			"inner join ansi_user on ansi_user.user_id = call_log.user_id\n" + 
			"inner join code on code.table_name='call_log' and code.field_name='contact_type' and code.value=call_log.contact_type\n" + 
			"inner join call_log_xref on call_log_xref.call_log_id=call_log.call_log_id\n" + 
			"where call_log.call_log_id=?";
	
	private Integer callLogId;
	private String content; 
	private Integer addressId;
	private String addressName; 
	private String address1; 
	private String address2; 
	private String city; 
	private String state; 
	private String zip; 
	private Integer contactId; 
	private String contactName; 
	private String summary; 
	private Integer userId;
	private String ansiContact; 
	private String ansiPhone; 
	private String ansiEmail; 
	private String startTime; 
	private String contactType; 
	private String xrefType; 
	private Integer xrefId; 
	private String xref;
	
	public CallNoteDetailResponse() {
		super();
	}
	public CallNoteDetailResponse(Connection conn, Integer callLogId) throws SQLException, RecordNotFoundException {
		this();
		this.callLogId = callLogId;
		make(conn, callLogId);
	}
	

	public Integer getCallLogId() {
		return callLogId;
	}
	public void setCallLogId(Integer callLogId) {
		this.callLogId = callLogId;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getAddressId() {
		return addressId;
	}
	public void setAddressId(Integer addressId) {
		this.addressId = addressId;
	}
	public String getAddressName() {
		return addressName;
	}
	public void setAddressName(String addressName) {
		this.addressName = addressName;
	}
	public String getAddress1() {
		return address1;
	}
	public void setAddress1(String address1) {
		this.address1 = address1;
	}
	public String getAddress2() {
		return address2;
	}
	public void setAddress2(String address2) {
		this.address2 = address2;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public Integer getContactId() {
		return contactId;
	}
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	public String getContactName() {
		return contactName;
	}
	public void setContactName(String contactName) {
		this.contactName = contactName;
	}
	public String getSummary() {
		return summary;
	}
	public void setSummary(String summary) {
		this.summary = summary;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public String getAnsiContact() {
		return ansiContact;
	}
	public void setAnsiContact(String ansiContact) {
		this.ansiContact = ansiContact;
	}
	public String getAnsiPhone() {
		return ansiPhone;
	}
	public void setAnsiPhone(String ansiPhone) {
		this.ansiPhone = ansiPhone;
	}
	public String getAnsiEmail() {
		return ansiEmail;
	}
	public void setAnsiEmail(String ansiEmail) {
		this.ansiEmail = ansiEmail;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getContactType() {
		return contactType;
	}
	public void setContactType(String contactType) {
		this.contactType = contactType;
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
	public String getXref() {
		return xref;
	}
	public void setXref(String xref) {
		this.xref = xref;
	}
	private void make(Connection conn, Integer callLogId) throws SQLException, RecordNotFoundException {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a");
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, callLogId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			this.content = rs.getString("content");
			this.addressId = rs.getInt("address_id");
			this.addressName = rs.getString("address_name");
			this.address1 = rs.getString("address1");
			this.address2 = rs.getString("address2");
			this.city = rs.getString("city");
			this.state = rs.getString("state");
			this.zip = rs.getString("zip");
			this.contactId = rs.getInt("contact_id"); 
			this.contactName = rs.getString("contact_name");
			this.summary = rs.getString("summary");
			this.userId = rs.getInt("user_id");
			this.ansiContact = rs.getString("ansi_contact");
			this.ansiPhone = rs.getString("ansi_phone");
			this.ansiEmail = rs.getString("ansi_email");
			Timestamp startTime = rs.getTimestamp("start_time");
			this.startTime = sdf.format(startTime);
			this.contactType = rs.getString("contact_type"); 
			this.xrefType = rs.getString("xref_type");
			this.xrefId = rs.getInt("xref_id");
			this.xref = rs.getString("xref");
		} else {
			throw new RecordNotFoundException();
		}
		
	}
	
	
	
	
	
	
}
