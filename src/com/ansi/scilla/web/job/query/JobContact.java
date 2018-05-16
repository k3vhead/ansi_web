package com.ansi.scilla.web.job.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ansi.scilla.common.ApplicationObject;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobContact extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private static final String baseSQL = "select top(1) " +
					"\n\t job.quote_id, " +
					"\n\t job.job_id, " +
					"\n\t job_contact.contact_id as job_contact_id, " + 
					"\n\t job_contact.last_name as job_contact_last_name, " + 
					"\n\t job_contact.first_name as job_contact_first_name, " +
					"\n\t job_contact.preferred_contact as job_contact_preferred_contact, " +
					"\n\t case job_contact.preferred_contact " +
					"\n\t 	when 'business_phone' then job_contact.business_phone " +
					"\n\t 	when 'email' then job_contact.email " +
					"\n\t 	when 'fax' then job_contact.fax " +
					"\n\t 	when 'mobile_phone' then job_contact.mobile_phone " +
					"\n\t end as job_contact_method, " +
					"\n\t site_contact.contact_id as site_contact_id, " + 
					"\n\t site_contact.last_name as site_contact_last_name, " + 
					"\n\t site_contact.first_name as site_contact_first_name, " +
					"\n\t site_contact.preferred_contact as site_contact_preferred_contact, " +
					"\n\t case site_contact.preferred_contact " +
					"\n\t 	when 'business_phone' then site_contact.business_phone " +
					"\n\t 	when 'email' then site_contact.email " +
					"\n\t 	when 'fax' then site_contact.fax " +
					"\n\t 	when 'mobile_phone' then site_contact.mobile_phone " +
					"\n\t end as site_contact_method, " +
					"\n\t contract_contact.contact_id as contract_contact_id, " + 
					"\n\t contract_contact.last_name as contract_contact_last_name, " + 
					"\n\t contract_contact.first_name as contract_contact_first_name, " +
					"\n\t contract_contact.preferred_contact as contract_contact_preferred_contact, " +
					"\n\t case contract_contact.preferred_contact " +
					"\n\t 	when 'business_phone' then contract_contact.business_phone " +
					"\n\t 	when 'email' then contract_contact.email " +
					"\n\t 	when 'fax' then contract_contact.fax " +
					"\n\t 	when 'mobile_phone' then contract_contact.mobile_phone " +
					"\n\t end as contract_contact_method, " +
					"\n\t billing_contact.contact_id as billing_contact_id, " + 
					"\n\t billing_contact.last_name as billing_contact_last_name, " + 
					"\n\t billing_contact.first_name as billing_contact_first_name, " +
					"\n\t billing_contact.preferred_contact as billing_contact_preferred_contact, " +
					"\n\t case billing_contact.preferred_contact " +
					"\n\t 	when 'business_phone' then billing_contact.business_phone " +
					"\n\t 	when 'email' then billing_contact.email " +
					"\n\t 	when 'fax' then billing_contact.fax " +
					"\n\t 	when 'mobile_phone' then billing_contact.mobile_phone " +
					"\n\t end as billing_contact_method " +
					"\n from job " +
					"\n left outer join contact as job_contact on job_contact.contact_id=job.job_contact_id " +
					"\n left outer join contact as site_contact on site_contact.contact_id=job.site_contact " +
					"\n left outer join contact as contract_contact on contract_contact.contact_id=job.contract_contact_id " +
					"\n left outer join contact as billing_contact  on billing_contact.contact_id=job.billing_contact_id ";
					
	
	
	
	private Integer quoteId;
	private Integer jobId;
	
	private ContactItem jobContact;
	private ContactItem siteContact;
	private ContactItem contractContact;
	private ContactItem billingContact;
	

	
	private JobContact(ResultSet rs) throws SQLException {
		super();
		this.quoteId = rs.getInt("quote_id");
		this.jobId = rs.getInt("job_id");
		
		this.jobContact = new ContactItem();
		this.jobContact.setContactId(rs.getInt("job_contact_id"));
		this.jobContact.setLastName(rs.getString("job_contact_last_name"));
		this.jobContact.setFirstName(rs.getString("job_contact_first_name"));
		this.jobContact.setPreferredContact(rs.getString("job_contact_preferred_contact"));
		this.jobContact.setMethod(rs.getString("job_contact_method"));
		
		this.siteContact = new ContactItem();
		this.siteContact.setContactId(rs.getInt("site_contact_id"));
		this.siteContact.setLastName(rs.getString("site_contact_last_name"));
		this.siteContact.setFirstName(rs.getString("site_contact_first_name"));
		this.siteContact.setPreferredContact(rs.getString("site_contact_preferred_contact"));
		this.siteContact.setMethod(rs.getString("site_contact_method"));
		
		this.contractContact = new ContactItem();
		this.contractContact.setContactId(rs.getInt("contract_contact_id"));
		this.contractContact.setLastName(rs.getString("contract_contact_last_name"));
		this.contractContact.setFirstName(rs.getString("contract_contact_first_name"));
		this.contractContact.setPreferredContact(rs.getString("contract_contact_preferred_contact"));
		this.contractContact.setMethod(rs.getString("contract_contact_method"));
		
		this.billingContact = new ContactItem();
		this.billingContact.setContactId(rs.getInt("billing_contact_id"));
		this.billingContact.setLastName(rs.getString("billing_contact_last_name"));
		this.billingContact.setFirstName(rs.getString("billing_contact_first_name"));
		this.billingContact.setPreferredContact(rs.getString("billing_contact_preferred_contact"));
		this.billingContact.setMethod(rs.getString("billing_contact_method"));
	}
	
	public Integer getQuoteId() {
		return quoteId;
	}
	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public ContactItem getJobContact() {
		return jobContact;
	}
	public void setJobContact(ContactItem jobContact) {
		this.jobContact = jobContact;
	}
	public ContactItem getSiteContact() {
		return siteContact;
	}
	public void setSiteContact(ContactItem siteContact) {
		this.siteContact = siteContact;
	}
	public ContactItem getContractContact() {
		return contractContact;
	}
	public void setContractContact(ContactItem contractContact) {
		this.contractContact = contractContact;
	}
	public ContactItem getBillingContact() {
		return billingContact;
	}
	public void setBillingContact(ContactItem billingContact) {
		this.billingContact = billingContact;
	}

	/**
	 * Return contact information for a given job
	 * @param conn
	 * @param jobId
	 * @return
	 * @throws SQLException
	 * @throws RecordNotFoundException when the given job id is invalid
	 */
	public static JobContact getJobContact(Connection conn, Integer jobId) throws SQLException, RecordNotFoundException {
		String sql = baseSQL + "\n where job.job_id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  jobId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			return new JobContact(rs);
		} else {
			throw new RecordNotFoundException(String.valueOf(jobId));
		}
	}
	
	/**
	 * Returns job contact information for the first found job for a given quote
	 * @param conn
	 * @param quoteId
	 * @return
	 * @throws SQLException
	 * @throws RecordNotFoundException when the given quote id is invalid.
	 */
	public static JobContact getQuoteContact(Connection conn, Integer quoteId) throws SQLException, RecordNotFoundException {
		String sql = baseSQL + "\n where job.quote_id=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  quoteId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			return new JobContact(rs);
		} else {
			throw new RecordNotFoundException(String.valueOf(quoteId));
		}
	}
	
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
}
