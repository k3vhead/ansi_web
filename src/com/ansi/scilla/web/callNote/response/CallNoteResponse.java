package com.ansi.scilla.web.callNote.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.callNote.CallNoteReference;
import com.ansi.scilla.common.callNote.DefaultAddress;
import com.ansi.scilla.web.callNote.request.CallNoteRequest;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class CallNoteResponse extends MessageResponse {

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
	
	private final String sql = "select call_log.call_log_id, call_log.address_id, call_log.summary, call_log.content,\n" + 
			"	call_log.contact_id, call_log.user_id, call_log.start_time, call_log.contact_type,\n" + 
			"	address.name as address_name, \n" + 
			"	concat(contact.first_name, ' ', contact.last_name) as contact_name\n" + 
			"from call_log_xref\n" + 
			"inner join call_log on call_log.call_log_id=call_log_xref.call_log_id\n" + 
			"inner join address on address.address_id=call_log.address_id\n" + 
			"inner join contact on contact.contact_id = call_log.contact_id\n" + 
			"inner join ansi_user on ansi_user.user_id = call_log.user_id\n" + 
			"where call_log_xref.xref_type=? and call_log_xref.xref_id=?\n" + 
			"order by call_log.start_time desc";
	
	private String xrefType;
	private Integer xrefId;	
	private List<CallNoteResponseItem> noteList;
	private CallNoteRequest defaultVals;
	private String defaultAddressName;
	private String ansiContactName;
	
	public CallNoteResponse() {
		super();
	}
	
	public CallNoteResponse(Connection conn, String xrefType, Integer xrefId, SessionUser user) throws SQLException, RecordNotFoundException {
		this();
		this.xrefType = xrefType;
		this.xrefId = xrefId;
		this.noteList = makeNoteList(conn);
		this.setDefaultVals(makeDefaultVals(conn, xrefType, xrefId, user));
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

	public List<CallNoteResponseItem> getNoteList() {
		return noteList;
	}

	public void setNoteList(List<CallNoteResponseItem> noteList) {
		this.noteList = noteList;
	}
	
	public CallNoteRequest getDefaultVals() {
		return defaultVals;
	}

	public void setDefaultVals(CallNoteRequest defaultVals) {
		this.defaultVals = defaultVals;
	}
	
	public String getDefaultAddressName() {
		return defaultAddressName;
	}
	
	public void setDefaultAddressName(String defaultAddressName) {
		this.defaultAddressName = defaultAddressName;
	}
	
	public String getAnsiContactName() {
		return ansiContactName;
	}
	
	public void setAnsiContactName(String ansiContactName) {
		this.ansiContactName = ansiContactName;
	}
	
	private List<CallNoteResponseItem> makeNoteList(Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(this.sql);
		ps.setString(1, this.xrefType);
		ps.setInt(2, this.xrefId);
		ResultSet rs = ps.executeQuery();
		
		List<CallNoteResponseItem> noteList = new ArrayList<CallNoteResponseItem>();
		while ( rs.next() ) {
			noteList.add(new CallNoteResponseItem(rs));
		}
		return noteList;
	}
	
	private CallNoteRequest makeDefaultVals(Connection conn, String xrefType, Integer xrefId, SessionUser user) throws SQLException, RecordNotFoundException {
		CallNoteReference xref = CallNoteReference.valueOf(xrefType);
		DefaultAddress defaultAddress = xref.defaultAddress(conn, xrefId);
		CallNoteRequest callNoteRequest = new CallNoteRequest();
		callNoteRequest.setUserId(user.getUserId());
		callNoteRequest.setAddressId(defaultAddress.addressId);
		this.defaultAddressName = defaultAddress.name;
		this.ansiContactName = user.getFirstName() + " " + user.getLastName();
		callNoteRequest.setContactId(xrefId);
		callNoteRequest.setXrefId(xrefId);
		callNoteRequest.setXrefType(xrefType);
		return callNoteRequest;
	}

	
	
	
}
