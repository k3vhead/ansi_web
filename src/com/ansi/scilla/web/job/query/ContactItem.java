package com.ansi.scilla.web.job.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.thewebthing.commons.db2.RecordNotFoundException;

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
	
	public static ContactItem makeContactItem(Connection conn, Integer contactId) throws RecordNotFoundException, SQLException {
		ContactItem item = new ContactItem();
		String sql = "select top(1) " + 
				"\n\t contact.contact_id as contact_id, " + 
				"\n\t contact.last_name as contact_last_name,  " + 
				"\n\t contact.first_name as contact_first_name, " + 
				"\n\t contact.preferred_contact as contact_preferred_contact, " + 
				"\n\t case contact.preferred_contact " + 
				"\n\t 	when 'business_phone' then contact.business_phone " + 
				"\n\t 	when 'email' then contact.email " + 
				"\n\t 	when 'fax' then contact.fax " + 
				"\n\t 	when 'mobile_phone' then contact.mobile_phone " + 
				"\n\t end as job_contact_method " +
				"\n from contact where contact_id=?";
		Logger logger = LogManager.getLogger(ContactItem.class);
		logger.log(Level.DEBUG, sql + "\t\t" + contactId);
		
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, contactId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			item.setContactId(rs.getInt("contact_id"));
			item.setLastName(rs.getString("contact_last_name"));
			item.setFirstName(rs.getString("contact_first_name"));
			item.setPreferredContact(rs.getString("contact_preferred_contact"));
			item.setMethod(rs.getString("job_contact_method"));
		} else {
			throw new RecordNotFoundException();
		}
		rs.close();
		return item;
	}
}
