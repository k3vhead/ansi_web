package com.ansi.scilla.web.callNote.response;

import java.sql.Connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;

public class ContactNoteResponse extends CallNoteResponse {

	private static final long serialVersionUID = 1L;

	private Integer contactId;
	private String firstName;
	private String lastName;
	
	public ContactNoteResponse(Connection conn, String xrefType, Integer xrefId, SessionUser user) throws Exception {
		super(conn, xrefType, xrefId, user);
		Contact contact = new Contact();
		contact.setContactId(xrefId);
		contact.selectOne(conn);
		this.firstName = contact.getFirstName();
		this.lastName = contact.getLastName();
		this.contactId = contact.getContactId();
		Logger logger = AppUtils.getLogger();
		logger.log(Level.DEBUG, this);
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	
}
