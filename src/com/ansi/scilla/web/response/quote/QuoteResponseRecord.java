package com.ansi.scilla.web.response.quote;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.PrintHistory;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.web.address.response.AddressResponseRecord;
import com.ansi.scilla.web.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


public class QuoteResponseRecord extends MessageResponse {

	private static final long serialVersionUID = 1L;
	
	private QuoteDetail quote;
	private AddressResponseRecord billTo;
	private AddressResponseRecord jobSite;

	public QuoteResponseRecord(Connection conn, Quote quote) throws Exception {
		super();
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
		this.jobSite = new AddressResponseRecord(jobSiteAddress);
		
		Address billToAddress = new Address();
		billToAddress.setAddressId(quote.getBillToAddressId());
		billToAddress.selectOne(conn);
		this.billTo = new AddressResponseRecord(billToAddress);
		
		Integer printCount = makePrintCount(conn, quote.getQuoteId());
		this.quote = new QuoteDetail(quote, manager, division, printCount);
	}
	
	public QuoteDetail getQuote() {
		return quote;
	}

	public void setQuote(QuoteDetail quote) {
		this.quote = quote;
	}
	
	public AddressResponseRecord getBillTo() {
		return billTo;
	}
	public void setBillTo(AddressResponseRecord billTo) {
		this.billTo = billTo;
	}
	public AddressResponseRecord getJobSite() {
		return jobSite;
	}
	public void setJobSite(AddressResponseRecord jobSite) {
		this.jobSite = jobSite;
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

		
			public QuoteDetail() {
				super();
			}
			
			public QuoteDetail(Quote quote, User manager, Division division, Integer printCount) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
				this();
				PropertyUtils.copyProperties(this, quote);
				this.managerLastName = manager.getLastName();
				this.managerFirstName = manager.getFirstName();
				this.managerEmail = manager.getEmail();
				this.divisionCode = division.getDivisionCode();
//				this.printDate = printHistory.getPrintDate();
//				this.quoteDate = printHistory.getQuoteDate();
				this.printCount = printCount;
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
		

		}
	
	
}
