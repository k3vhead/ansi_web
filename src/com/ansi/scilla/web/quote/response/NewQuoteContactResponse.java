package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.sql.Connection;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.job.query.ContactItem;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class NewQuoteContactResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private ContactItem contact;
	
	public NewQuoteContactResponse() {
		super();
	}
	
	public NewQuoteContactResponse(Connection conn, Integer contactId) throws RecordNotFoundException, Exception {
		this.contact = ContactItem.makeContactItem(conn, contactId);
	}

	public ContactItem getContact() {
		return contact;
	}

	public void setContact(ContactItem contact) {
		this.contact = contact;
	}
	
	
	
}
