package com.ansi.scilla.web.quote.response;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Contact;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.PrintHistory;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.address.query.AddressResponseQuery;
import com.ansi.scilla.web.address.response.AddressResponseItem;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.job.query.ContactItem;
import com.ansi.scilla.web.job.query.JobContact;
import com.ansi.scilla.web.job.query.JobHeader;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;


public class QuoteResponseItem extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private QuoteDetail quote;
	private AddressResponseItem billTo;
	private AddressResponseItem jobSite;
	private JobContact jobContact;
	private List<JobHeader> jobHeaderList;
	private Boolean canEdit;
	private ContactItem signedBy;

	public QuoteResponseItem(Connection conn, Quote quote, Boolean canEdit) throws Exception {
		super();
		makeQuoteDetail(conn, quote);
		this.canEdit = canEdit;
	}
	
	public QuoteResponseItem(Connection conn, Quote quote, List<UserPermission> permissionList) throws Exception {
		super();
		makeQuoteDetail(conn, quote);
		this.canEdit = makeEditFlag(permissionList);
	}
	
	private void makeQuoteDetail(Connection conn, Quote quote) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		logger.log(Level.DEBUG, "Making a detail");
		logger.log(Level.DEBUG, this);
		logger.log(Level.DEBUG, quote);
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
		job.selectOne(conn);
		this.quote = new QuoteDetail(quote, manager, division, printCount, job);
		
		this.jobContact = JobContact.getQuoteContact(conn, quote.getQuoteId());
		this.jobHeaderList = JobHeader.getJobHeaderList(conn, quote.getQuoteId());	
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
	
	
	public QuoteDetail getQuote() {
		return quote;
	}

	public void setQuote(QuoteDetail quote) {
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

	public Boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
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


	public class QuoteDetail extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		
		private String address;
		private Integer billToAddressId;
		private Integer copiedFromQuoteId;
		private Integer jobSiteAddressId;
		private String leadType;
		private Integer managerId;
		private String name;
		private String paymentTerms;
		private Date proposalDate;
		private Integer quoteId;
		private Integer divisionId;
		private String accountType;
		private Integer quoteNumber;
		private String revision;
		private Integer signedByContactId;
		private Integer status;
		private Integer templateId;
		private String managerFirstName;
		private String managerLastName;
		private String managerEmail;
		private String divisionCode;
//		private Date printDate;
//		private Date quoteDate;
		private Integer printCount;
		private String buildingType;
		private String invoiceTerms;
		private String invoiceStyle;
		private String invoiceGrouping;
		private Integer invoiceBatch;
		private Integer taxExempt;
		private String taxExemptReason;
		private String copiedFromQuoteNbrRev;
		
		public QuoteDetail() {
			super();
		}

		public QuoteDetail(Quote quote, User manager, Division division, Integer printCount, Job job) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
			this();
			PropertyUtils.copyProperties(this, quote);
			this.managerLastName = manager.getLastName();
			this.managerFirstName = manager.getFirstName();
			this.managerEmail = manager.getEmail();
			this.divisionCode = division.getDivisionCode();
			//				this.printDate = printHistory.getPrintDate();
			//				this.quoteDate = printHistory.getQuoteDate();
			this.printCount = printCount;
			this.buildingType = job.getBuildingType();
			this.invoiceTerms = job.getInvoiceTerms();
			this.invoiceStyle = job.getInvoiceStyle();
			this.invoiceGrouping = job.getInvoiceGrouping();
			this.invoiceBatch = job.getInvoiceBatch();
			this.taxExempt = job.getTaxExempt();
			this.taxExemptReason = job.getTaxExemptReason();
					
		}

		public void setAddress(String address) {
			this.address = address;
		}


		public String getAddress() {
			return this.address;
		}

		public void setAccountType(String accountType) {
			this.accountType = accountType;
		}
		//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		//@JsonSerialize(using=AnsiDateFormatter.class)
		//			public Date getPrintDate() {
		//				return this.printDate;
		//			}
		//			
		//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		//			public void setPrintDate(Date printDate) {
		//				this.printDate = printDate;
		//			}
		//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		//			public Date getQuoteDate() {
		//				return this.quoteDate;
		//			}
		//			
		//			@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		//			public void setQuoteDate(Date quoteDate) {
		//				this.quoteDate = quoteDate;
		//			}

		public String getDivisionCode() {
			return this.divisionCode;
		}
		public void setDivisionCode(String divisionCode) {
			this.divisionCode = divisionCode;
		}
		public String getAccountType() {
			return this.accountType;
		}
		public void setBillToAddressId(Integer billToAddressId) {
			this.billToAddressId = billToAddressId;
		}
		public Integer getBillToAddressId() {
			return this.billToAddressId;
		}
		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}
		public Integer getDivisionId() {
			return this.divisionId;
		}
		public void setCopiedFromQuoteId(Integer copiedFromQuoteId) {
			this.copiedFromQuoteId = copiedFromQuoteId;
		}
		public Integer getCopiedFromQuoteId() {
			return this.copiedFromQuoteId;
		}
		public void setJobSiteAddressId(Integer jobSiteAddressId) {
			this.jobSiteAddressId = jobSiteAddressId;
		}
		public Integer getJobSiteAddressId() {
			return this.jobSiteAddressId;
		}
		public void setLeadType(String leadType) {
			this.leadType = leadType;
		}
		public String getLeadType() {
			return this.leadType;
		}
		public void setManagerId(Integer managerId) {
			this.managerId = managerId;
		}
		public Integer getManagerId() {
			return this.managerId;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getName() {
			return this.name;
		}
		public void setPaymentTerms(String paymentTerms) {
			this.paymentTerms = paymentTerms;
		}
		public String getPaymentTerms() {
			return this.paymentTerms;
		}
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
		public Date getProposalDate() {
			return this.proposalDate;
		}
		public void setQuoteId(Integer quoteId) {
			this.quoteId = quoteId;
		}
		public Integer getQuoteId() {
			return this.quoteId;
		}
		public void setQuoteNumber(Integer quoteNumber) {
			this.quoteNumber = quoteNumber;
		}
		public Integer getQuoteNumber() {
			return this.quoteNumber;
		}
		public void setRevision(String revision) {
			this.revision = revision;
		}
		public String getRevision() {
			return this.revision;
		}
		public void setSignedByContactId(Integer signedByContactId) {
			this.signedByContactId = signedByContactId;
		}
		public Integer getSignedByContactId() {
			return this.signedByContactId;
		}
		public void setStatus(Integer status) {
			this.status = status;
		}
		public Integer getStatus() {
			return this.status;
		}
		public void setTemplateId(Integer templateId) {
			this.templateId = templateId;
		}
		public Integer getTemplateId() {
			return this.templateId;
		}
		public String getManagerFirstName() {
			return managerFirstName;
		}
		public void setManagerFirstName(String managerFirstName) {
			this.managerFirstName = managerFirstName;
		}
		public String getManagerLastName() {
			return managerLastName;
		}
		public void setManagerLastName(String managerLastName) {
			this.managerLastName = managerLastName;
		}
		public String getManagerEmail() {
			return managerEmail;
		}
		public void setManagerEmail(String managerEmail) {
			this.managerEmail = managerEmail;
		}

		public Integer getPrintCount() {
			return printCount;
		}

		public void setPrintCount(Integer printCount) {
			this.printCount = printCount;
		}

		public String getBuildingType() {
			return buildingType;
		}

		public void setBuildingType(String buildingType) {
			this.buildingType = buildingType;
		}

		public String getInvoiceTerms() {
			return invoiceTerms;
		}

		public void setInvoiceTerms(String invoiceTerms) {
			this.invoiceTerms = invoiceTerms;
		}

		public String getInvoiceStyle() {
			return invoiceStyle;
		}

		public void setInvoiceStyle(String invoiceStyle) {
			this.invoiceStyle = invoiceStyle;
		}

		public String getInvoiceGrouping() {
			return invoiceGrouping;
		}

		public void setInvoiceGrouping(String invoiceGrouping) {
			this.invoiceGrouping = invoiceGrouping;
		}

		public Integer getInvoiceBatch() {
			return invoiceBatch;
		}

		public void setInvoiceBatch(Integer invoiceBatch) {
			this.invoiceBatch = invoiceBatch;
		}

		public Integer getTaxExempt() {
			return taxExempt;
		}

		public void setTaxExempt(Integer taxExempt) {
			this.taxExempt = taxExempt;
		}

		public String getTaxExemptReason() {
			return taxExemptReason;
		}

		public void setTaxExemptReason(String taxExemptReason) {
			this.taxExemptReason = taxExemptReason;
		}

		public String getCopiedFromQuoteNbrRev() {
			return copiedFromQuoteNbrRev;
		}

		public void setCopiedFromQuoteNbrRev(String copiedFromQuoteNbrRev) {
			this.copiedFromQuoteNbrRev = copiedFromQuoteNbrRev;
		}


	}
	
	
}
