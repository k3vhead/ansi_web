package com.ansi.scilla.web.job.query;

import com.ansi.scilla.common.ApplicationObject;

public class ContactItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer contactId;
	private String lastName;
	private String firstName;
	private String preferredContact;
	private String method;
	
	public Integer getContactId() {
		return contactId;
	}
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getPreferredContact() {
		return preferredContact;
	}
	public void setPreferredContact(String preferredContact) {
		this.preferredContact = preferredContact;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	
}
