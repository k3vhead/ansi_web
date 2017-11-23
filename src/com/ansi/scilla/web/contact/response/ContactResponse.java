package com.ansi.scilla.web.contact.response;

import java.io.Serializable;

import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.common.response.MessageResponse;

/**
 * Used to return a single contact to the client
 * 
 * @author ggroce
 *
 */
public class ContactResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Contact contact;

	public ContactResponse() {
		super();
	}

	public ContactResponse(Contact contact, WebMessages webMessages) {
		super(webMessages);
		this.contact = contact;
		
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	
	
}
