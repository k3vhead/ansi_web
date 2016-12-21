package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;

/**
 * Used to request data from the TaxRate table
 * 
 * @author gagroce
 *
 */
public class ContactRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private String businessPhone;
	private Integer contactId;
	private String fax;
	private String firstName;
	private String lastName;
	private String mobilePhone;
	private String preferredContact;
	
	public ContactRequest() {
		super();
	}
	
	public ContactRequest(String jsonString) throws IOException, IllegalAccessException, InvocationTargetException {
		this();
		ContactRequest req = (ContactRequest) AppUtils.json2object(jsonString, ContactRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String tableName) {
		this.businessPhone = businessPhone;
	}

	@RequiredForUpdate
	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getPreferredContact() {
		return preferredContact;
	}

	public void setPreferredContact(String preferredContact) {
		this.preferredContact = preferredContact;
	}

}
