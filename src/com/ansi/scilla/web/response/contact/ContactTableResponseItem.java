package com.ansi.scilla.web.response.contact;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

import com.ansi.scilla.common.queries.ReportQuery;
import com.thewebthing.commons.db2.DBColumn;

public class ContactTableResponseItem extends ReportQuery {

	private static final long serialVersionUID = 1L;

	public static final String BUSINESS_PHONE = "business_phone";
	public static final String CONTACT_ID = "contact_id";
	public static final String FAX = "fax";
	public static final String FIRST_NAME = "first_name";
	public static final String LAST_NAME = "last_name";
	public static final String MOBILE_PHONE = "mobile_phone";
	public static final String PREFERRED_CONTACT = "preferred_contact";
	public static final String EMAIL = "email";

	private String businessPhone;
	private Integer contactId;
	private String fax;
	private String firstName;
	private String lastName;
	private String mobilePhone;
	private String preferredContact;
	private String email;
	
	public ContactTableResponseItem(ResultSetMetaData rsmd, ResultSet rs ) throws Exception {
		super();
		super.rs2Object(this, rsmd, rs);
	}
	@DBColumn(BUSINESS_PHONE)
	public String getBusinessPhone() {
		return businessPhone;
	}
	@DBColumn(BUSINESS_PHONE)
	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}
	@DBColumn(CONTACT_ID)
	public Integer getContactId() {
		return contactId;
	}
	@DBColumn(CONTACT_ID)
	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
	@DBColumn(FAX)
	public String getFax() {
		return fax;
	}
	@DBColumn(FAX)
	public void setFax(String fax) {
		this.fax = fax;
	}
	@DBColumn(FIRST_NAME)
	public String getFirstName() {
		return firstName;
	}
	@DBColumn(FIRST_NAME)
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	@DBColumn(LAST_NAME)
	public String getLastName() {
		return lastName;
	}
	@DBColumn(LAST_NAME)
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	@DBColumn(MOBILE_PHONE)
	public String getMobilePhone() {
		return mobilePhone;
	}
	@DBColumn(MOBILE_PHONE)
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	@DBColumn(PREFERRED_CONTACT)
	public String getPreferredContact() {
		return preferredContact;
	}
	@DBColumn(PREFERRED_CONTACT)
	public void setPreferredContact(String preferredContact) {
		this.preferredContact = preferredContact;
	}
	@DBColumn(EMAIL)
	public String getEmail() {
		return email;
	}
	@DBColumn(EMAIL)
	public void setEmail(String email) {
		this.email = email;
	}

	
}
