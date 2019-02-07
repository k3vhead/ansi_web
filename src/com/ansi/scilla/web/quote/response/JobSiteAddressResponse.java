package com.ansi.scilla.web.quote.response;

import java.io.Serializable;
import java.sql.Connection;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.address.query.AddressResponseQuery;
import com.ansi.scilla.web.address.response.AddressResponseItem;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.job.query.ContactItem;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobSiteAddressResponse extends MessageResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private AddressResponseItem jobSiteAddress;
	private AddressResponseItem billToAddress;
	private ContactItem jobsiteJobContact;
	private ContactItem jobsiteSiteContact;	
	private ContactItem billtoContractContact;
	private ContactItem billtoBillingContact;
	private Logger logger;
	
	public JobSiteAddressResponse() {
		super();
		this.logger = LogManager.getLogger(this.getClass());
	}
	
	public JobSiteAddressResponse(Connection conn, Integer jobSiteAddressId) throws RecordNotFoundException, Exception {
		this();
		makeJobSiteAddressResponse(conn, jobSiteAddressId);
	}
	
	private void makeJobSiteAddressResponse(Connection conn, Integer jobSiteAddressId) throws RecordNotFoundException, Exception {
		this.jobSiteAddress = AddressResponseQuery.selectOne(conn, jobSiteAddressId);
		logger.log(Level.DEBUG, this.jobSiteAddress);
		if ( this.jobSiteAddress.getJobsiteBilltoAddressDefault() != null ) {
			logger.log(Level.DEBUG, "Getting billtodefault: " + this.jobSiteAddress.getJobsiteBilltoAddressDefault());
			// if it's populated, the address must exist because of foreign key restraints
			this.billToAddress = AddressResponseQuery.selectOne(conn, this.jobSiteAddress.getJobsiteBilltoAddressDefault());
		}
		if ( this.jobSiteAddress.getJobsiteJobContactDefault() != null ) {
			this.jobsiteJobContact = ContactItem.makeContactItem(conn, this.jobSiteAddress.getJobsiteJobContactDefault());
		}
		if ( this.jobSiteAddress.getJobsiteSiteContactDefault() != null ) {
			this.jobsiteSiteContact = ContactItem.makeContactItem(conn, this.jobSiteAddress.getJobsiteSiteContactDefault());
		}
		if ( this.jobSiteAddress.getBilltoContractContactDefault() != null ) {
			this.billtoContractContact = ContactItem.makeContactItem(conn, this.jobSiteAddress.getBilltoContractContactDefault());
		}
		if ( this.jobSiteAddress.getBilltoBillingContactDefault() != null ) {
			this.billtoBillingContact = ContactItem.makeContactItem(conn, this.jobSiteAddress.getBilltoBillingContactDefault());
		}
	}

	public AddressResponseItem getBillToAddress() {
		return billToAddress;
	}
	public void setBillToAddress(AddressResponseItem billToAddress) {
		this.billToAddress = billToAddress;
	}
	public AddressResponseItem getJobSiteAddress() {
		return jobSiteAddress;
	}

	public void setJobSiteAddress(AddressResponseItem jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}

	public ContactItem getJobsiteJobContact() {
		return jobsiteJobContact;
	}
	public void setJobsiteJobContact(ContactItem jobsiteJobContact) {
		this.jobsiteJobContact = jobsiteJobContact;
	}
	public ContactItem getJobsiteSiteContact() {
		return jobsiteSiteContact;
	}
	public void setJobsiteSiteContact(ContactItem jobsiteSiteContact) {
		this.jobsiteSiteContact = jobsiteSiteContact;
	}
	public ContactItem getBilltoContractContact() {
		return billtoContractContact;
	}
	public void setBilltoContractContact(ContactItem billtoContractContact) {
		this.billtoContractContact = billtoContractContact;
	}
	public ContactItem getBilltoBillingContact() {
		return billtoBillingContact;
	}
	public void setBilltoBillingContact(ContactItem billtoBillingContact) {
		this.billtoBillingContact = billtoBillingContact;
	}
	
	
	
}
