package com.ansi.scilla.web.quote.response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.PrintHistory;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.address.query.AddressResponseQuery;
import com.ansi.scilla.web.address.response.AddressResponseItem;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.job.query.ContactItem;
import com.ansi.scilla.web.job.query.JobContact;
import com.ansi.scilla.web.job.query.JobHeader;
import com.ansi.scilla.web.job.response.JobDetailResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;


public class QuoteResponseItem extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private QuoteResponseItemDetail quote;
	private AddressResponseItem billTo;
	private AddressResponseItem jobSite;
	private JobContact jobContact;
	private List<JobHeader> jobHeaderList;
	private JobDetailResponse jobDetail;
	private ContactItem signedBy;
	private Boolean canEdit;
	private Boolean canAddJob;
	private Boolean canPropose;

	
//	public QuoteResponseItem(Connection conn, Quote quote, Boolean canEdit) throws Exception {
//		super();
//		makeQuoteDetail(conn, quote);
//		this.canEdit = canEdit;
//	}
	public QuoteResponseItem() {
		super();
	}
	
	public QuoteResponseItem(Connection conn, Quote quote, List<UserPermission> permissionList) throws Exception {
		this();
		makeQuoteDetail(conn, quote, permissionList);
		this.canEdit = makeEditFlag(permissionList);
		this.canAddJob = this.canEdit;
		if ( this.quote.getProposalDate() != null ) {
			this.canAddJob = false;
		}
		this.canPropose = makeProposeFlag(permissionList);
	}
	
	private void makeQuoteDetail(Connection conn, Quote quote, List<UserPermission> permissionList) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, "Making a detail");
		PropertyUtils.copyProperties(this, quote);
		
		User manager = new User();
		manager.setUserId(quote.getManagerId());
		manager.selectOne(conn);
		
//		PrintHistory printHistory = new PrintHistory();
//		printHistory.setQuoteId(quote.getQuoteId());
//		printHistory.selectOne(conn);
		
		Division division = new Division();
		division.setDivisionId(quote.getDivisionId());
		division.selectOne(conn);
		
		Address jobSiteAddress = new Address();
		jobSiteAddress.setAddressId(quote.getJobSiteAddressId());
		jobSiteAddress.selectOne(conn);
		this.jobSite = AddressResponseQuery.selectOne(conn, jobSiteAddress.getAddressId());  //new AddressResponseItem(jobSiteAddress);
		
		Address billToAddress = new Address();
		billToAddress.setAddressId(quote.getBillToAddressId());
		billToAddress.selectOne(conn);
		this.billTo = AddressResponseQuery.selectOne(conn, billToAddress.getAddressId());  //new AddressResponseItem(billToAddress);
		
		Integer printCount = makePrintCount(conn, quote.getQuoteId());
		
		Job job = new Job();
		job.setQuoteId(quote.getQuoteId());
		try {
			job.selectOne(conn);
			this.quote = new QuoteResponseItemDetail(quote, manager, division, printCount, job);
			this.jobContact = JobContact.getQuoteContact(conn, quote.getQuoteId());
		} catch ( RecordNotFoundException e) {
			// this happens when there are no jobs for a quote, like with a new quote
			this.quote = new QuoteResponseItemDetail(quote, manager, division, printCount);
		}
		
		this.jobHeaderList = JobHeader.getJobHeaderList(conn, quote, permissionList);	
		if ( this.jobHeaderList.size() == 1 ) {
			this.jobHeaderList.get(0).setCanDelete(false); // don't delete the only job you've got
		}
		
		if ( quote.getSignedByContactId() != null ) {
			makeSignedBy(conn, quote.getSignedByContactId());
		}
		
		if ( quote.getCopiedFromQuoteId() != null ) {
			this.quote.setCopiedFromQuoteNbrRev(makeCopiedFrom(conn, quote.getCopiedFromQuoteId()));
		}
	}
	
	
	public QuoteResponseItemDetail getQuote() {
		return quote;
	}

	public void setQuote(QuoteResponseItemDetail quote) {
		this.quote = quote;
	}
	
	public AddressResponseItem getBillTo() {
		return billTo;
	}
	public void setBillTo(AddressResponseItem billTo) {
		this.billTo = billTo;
	}
	public AddressResponseItem getJobSite() {
		return jobSite;
	}
	public void setJobSite(AddressResponseItem jobSite) {
		this.jobSite = jobSite;
	}
	public JobContact getJobContact() {
		return jobContact;
	}
	public void setJobContact(JobContact jobContact) {
		this.jobContact = jobContact;
	}

	
	public List<JobHeader> getJobHeaderList() {
		return jobHeaderList;
	}

	public void setJobHeaderList(List<JobHeader> jobHeaderList) {
		this.jobHeaderList = jobHeaderList;
	}

	public JobDetailResponse getJobDetail() {
		return jobDetail;
	}

	public void setJobDetail(JobDetailResponse jobDetail) {
		this.jobDetail = jobDetail;
	}

	public Boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}

	public Boolean getCanAddJob() {
		return canAddJob;
	}

	public void setCanAddJob(Boolean canAddJob) {
		this.canAddJob = canAddJob;
	}

	public Boolean getCanPropose() {
		return canPropose;
	}

	public void setCanPropose(Boolean canPropose) {
		this.canPropose = canPropose;
	}

	public ContactItem getSignedBy() {
		return signedBy;
	}

	public void setSignedBy(ContactItem signedBy) {
		this.signedBy = signedBy;
	}

	private Integer makePrintCount(Connection conn, Integer quoteId) throws SQLException {
		Integer printCount = 0;
		String sql = "select count(*) as print_count from print_history where " + PrintHistory.QUOTE_ID + "=?";
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1,  quoteId);
		ResultSet rs = ps.executeQuery();
		if ( rs.next() ) {
			printCount = rs.getInt("print_count");
		}
		return printCount;
	}


	private Boolean makeEditFlag(List<UserPermission> permissionList) {
		Boolean canEdit = false;
		if ( this.quote.getProposalDate() == null ) {
			// proposal is still a work in progress; go ahead and make changes
			canEdit = hasPermission(permissionList, Permission.QUOTE_CREATE);
		} else {
			// this quote has been sent to a customer; only special people can edit it
			canEdit = hasPermission(permissionList, Permission.QUOTE_UPDATE);
		}
		return canEdit;
	}


	private Boolean makeProposeFlag(List<UserPermission> permissionList) {
		Boolean canPropose = false;
		if ( this.quote.getProposalDate() == null ) {
			if ( hasPermission(permissionList, Permission.QUOTE_PROPOSE) ) {
				if ( this.jobHeaderList.size() > 0 ) {
					canPropose = true;
				}
			}
		}
		return canPropose;
	}

	private void makeSignedBy(Connection conn, Integer signedByContactId) throws Exception {
		Contact contact = new Contact();
		contact.setContactId(signedByContactId);
		try {
			contact.selectOne(conn);
			this.signedBy = new ContactItem();
			this.signedBy.setContactId(signedByContactId);
			this.signedBy.setFirstName(contact.getFirstName());
			this.signedBy.setLastName(contact.getLastName());
			if ( contact.getPreferredContact().equalsIgnoreCase("business_phone")) {
				this.signedBy.setMethod(contact.getBusinessPhone());
			} else if ( contact.getPreferredContact().equalsIgnoreCase("email")) {
				this.signedBy.setMethod(contact.getEmail());
			} else if ( contact.getPreferredContact().equalsIgnoreCase("fax")) {
				this.signedBy.setMethod(contact.getFax());
			} else if ( contact.getPreferredContact().equalsIgnoreCase("mobile_phone")) {
				this.signedBy.setMethod(contact.getMobilePhone());
			}
			this.signedBy.setPreferredContact(contact.getPreferredContact());
		} catch ( RecordNotFoundException e ) {
			this.signedBy.setLastName("Invalid Contact Id");
		}
		
	}

	
	private String makeCopiedFrom(Connection conn, Integer copiedFromQuoteId) throws Exception {
		Quote copyFrom = new Quote();
		copyFrom.setQuoteId(copiedFromQuoteId);
		String copiedFrom = null;
		try {
			copyFrom.selectOne(conn);
			copiedFrom = copyFrom.getQuoteNumber() + copyFrom.getRevision();
		} catch ( RecordNotFoundException e) {
			copiedFrom = null;
		}
		return copiedFrom;
	}

	private Boolean hasPermission(List<UserPermission> permissionList, Permission requiredPermission) {
		boolean hasPermission = false;
		for ( UserPermission p : permissionList ) {
			if (p.getPermissionName().equals(requiredPermission.name())) {
				hasPermission = true;
			}
		}
		return hasPermission;
	}



	
	
}
