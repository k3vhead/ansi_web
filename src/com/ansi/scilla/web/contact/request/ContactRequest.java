package com.ansi.scilla.web.contact.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.db.Code;
import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.request.RequiredFormat;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.db2.DBTable;
import com.thewebthing.commons.db2.RecordNotFoundException;

/**
 * Used to request data from the TaxRate table
 * 
 * @author gagroce
 *
 */
public class ContactRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String BUSINESS_PHONE = "businessPhone";
	public static final String CONTACT_ID = "contactId";
	public static final String EMAIL = "email";
	public static final String FAX = "fax";
	public static final String FIRST_NAME = "firstName";
	public static final String LAST_NAME = "lastName";
	public static final String MOBILE_PHONE = "mobilePhone";
	public static final String PREFERRED_CONTACT = "preferredContact";
	public static final String CONTACT_STATUS = "contactStatus";

	private String businessPhone;
	private Integer contactId;
	private String email;
	private String fax;
	private String firstName;
	private String lastName;
	private String mobilePhone;
	private String preferredContact;
	private String contactStatus;
	
	public ContactRequest() {
		super();
	}
	
	public ContactRequest(String jsonString) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		ContactRequest req = (ContactRequest) AppUtils.json2object(jsonString, ContactRequest.class);
		PropertyUtils.copyProperties(this, req);
	}
	
	@RequiredFormat(PHONE_FORMAT)
	public String getBusinessPhone() {
		return businessPhone;
	}

	public void setBusinessPhone(String businessPhone) {
		this.businessPhone = businessPhone;
	}

	@RequiredForUpdate
	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}

	@RequiredFormat(EMAIL_FORMAT)
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	@RequiredFormat(PHONE_FORMAT)
	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}
	@RequiredForUpdate
	@RequiredForAdd
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
	@RequiredFormat(PHONE_FORMAT)
	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getPreferredContact() {
		return preferredContact;
	}

	public void setPreferredContact(String preferredContact) {
		this.preferredContact = preferredContact;
	}
	@RequiredForAdd
	@RequiredForUpdate
	public String getContactStatus() {
		return contactStatus;
	}

	public void setContactStatus(String contactStatus) {
		this.contactStatus = contactStatus;
	}

	public boolean isValidPreferredContact(Connection conn) throws Exception {
		boolean isValid = true;
		Code code = new Code();
		code.setTableName(Contact.class.getAnnotation(DBTable.class).value());
		code.setFieldName(Contact.PREFERRED_CONTACT);
		try {
			code.selectOne(conn);
		} catch ( RecordNotFoundException e ) {
			isValid = false;
		}
		return isValid;
	}

}
