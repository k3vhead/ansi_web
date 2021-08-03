package com.ansi.scilla.web.ticket.response;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.JobTagDisplay;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.jobticket.TicketType;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.ansi.scilla.web.address.response.AddressDetail;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDetail extends ApplicationObject { //TicketPaymentTotal populate from
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer ticketId;
	private Integer invoiceId;
	private String status;
	private Integer divisionId;
	private String divisionCode;
	private Date processDate;
	private String processNotes;
	private BigDecimal actDlAmt;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private Boolean billSheet;
	private Boolean customerSignature;
	private Boolean mgrApproval;
	private List<TicketStatus> nextAllowedStatusList;
	private Integer jobId; // - passed to job panels;
	private BigDecimal actTax;
	private BigDecimal totalVolPaid; // - sum(ticket_payment.amount);
	private BigDecimal totalTaxPaid; // - sum(ticket_payment.tax_amt);
	private BigDecimal balance; // (actPpc + actTax - (sumTcktPpcPaid + sumTktTaxPaid));
	private Integer daysToPay; //(today, invoiceDate, balance);
//	  					if balance == 0, daysToPay = max(paymentDate)-invoiceDate;
//	  					if balance <> 0, daysToPay = today - invoiceDate;
//	  			**ticket write off amount - stub for v 2.0;
	private String divisionDisplay;
	private BigDecimal defaultDirectLaborPct;
	private AddressDetail jobSiteAddress;
	private AddressDetail billToAddress;
	private String serviceDescription;
	private String jobFrequency;
	private String jobFrequencyDesc;
	private String invoiceTerms;
	private String invoiceStyle;
	private Date invoiceDate;

	
	
	private HashMap<String,List<JobTagDisplay>> jobTags;/*string is the job tag type*/
	
	
	/* ******************************************** */
	/* ******************************************** */
	
	private BigDecimal actTaxAmt;
	private Integer actTaxRateId;
	private Integer printCount;
	private Date startDate;
	private String fleetmaticsId;
//	private Integer actDivisionId;
	private String ticketType;
	// Tax stuff:
	private BigDecimal taxRateAmount;
	private Date taxRateEffectiveDate;
	private String taxRateLocation;
	private BigDecimal taxRate;
	private String poNumber;
	private String actPoNumber;


	
	/* ******************************************** */
	/* ******************************************** */
	
	public TicketDetail(){
		super();
	}
	
	public TicketDetail(Connection conn, Integer ticketId) throws RecordNotFoundException, Exception {
		Ticket ticket = new Ticket();
		ticket.setTicketId(ticketId);
		ticket.selectOne(conn);
		TicketPaymentTotals ticketPaymentTotals = TicketPaymentTotals.select(conn, ticketId);
		
		Division division = new Division();
		division.setDivisionId(ticketPaymentTotals.getDivisionId());
		division.selectOne(conn);
		
		Job job = new Job();
		job.setJobId(ticket.getJobId());
		job.selectOne(conn);
		
		
		this.defaultDirectLaborPct = division.getDefaultDirectLaborPct();
		this.ticketId = ticketId;
		if ( ticket.getInvoiceDate() != null ) {
			this.invoiceDate = ticket.getInvoiceDate();
		}
		this.invoiceId = ticketPaymentTotals.getTicket().getInvoiceId();
		this.status = ticketPaymentTotals.getTicket().getStatus();
		this.divisionId = ticketPaymentTotals.getDivisionId();
		this.divisionCode = ticketPaymentTotals.getDivisionCode();
		this.processDate = ticketPaymentTotals.getTicket().getProcessDate();
		this.processNotes = ticketPaymentTotals.getTicket().getProcessNotes();
		this.actDlAmt = ticketPaymentTotals.getTicket().getActDlAmt();
		this.actDlPct = ticketPaymentTotals.getTicket().getActDlPct();
		this.actPricePerCleaning = ticketPaymentTotals.getTicket().getActPricePerCleaning();
		this.billSheet = ticketPaymentTotals.getTicket().getBillSheet() == 1;
		this.customerSignature = ticketPaymentTotals.getTicket().getCustomerSignature() == 1;
		this.mgrApproval = ticketPaymentTotals.getTicket().getMgrApproval() == 1;
		this.nextAllowedStatusList = TicketStatus.lookup(ticketPaymentTotals.getTicket().getStatus()).nextValues();
		this.jobId = ticketPaymentTotals.getTicket().getJobId();
		this.actTax = ticketPaymentTotals.getTicket().getActTaxAmt();
		this.totalVolPaid = ticketPaymentTotals.getTotalVolPaid();
		this.totalTaxPaid = ticketPaymentTotals.getTotalTaxPaid();
		this.balance = actPricePerCleaning.add(actTax).subtract(totalVolPaid.add(totalTaxPaid));
		//daysToPay insert HERE:***
		this.divisionDisplay = ticketPaymentTotals.getDivisionDisplay();
		this.serviceDescription = ticketPaymentTotals.getServiceDescription();
		if ( ! StringUtils.isBlank(ticketPaymentTotals.getJobFrequency() )) {
//			this.jobFrequency = JobFrequency.get(ticketPaymentTotals.getJobFrequency()).display();
			this.jobFrequency = JobFrequency.lookup(ticketPaymentTotals.getJobFrequency()).abbrev();
			this.jobFrequencyDesc = JobFrequency.lookup(ticketPaymentTotals.getJobFrequency()).display();
		}
		if ( ! StringUtils.isBlank(ticketPaymentTotals.getInvoiceStyle())) {
			this.invoiceStyle = InvoiceStyle.valueOf(ticketPaymentTotals.getInvoiceStyle()).display();
		}
		if ( ! StringUtils.isBlank(ticketPaymentTotals.getInvoiceTerms())) {
			this.invoiceTerms = InvoiceTerm.valueOf(ticketPaymentTotals.getInvoiceTerms()).display();
		}
		this.actTaxAmt = ticket.getActTaxAmt();
		this.printCount = ticket.getPrintCount();
		this.startDate = ticket.getStartDate();
		this.fleetmaticsId = ticket.getFleetmaticsId();
//		this.actDivisionId = ticket.getActDivisionId();
		this.ticketType = TicketType.lookup(ticket.getTicketType()).display();
		this.actTaxRateId = ticket.getActTaxRateId();
		 
		
		this.billToAddress = new AddressDetail(conn, ticketPaymentTotals.getBillToAddressId());
		this.jobSiteAddress = new AddressDetail(conn, ticketPaymentTotals.getJobSiteAddressId());
		
		TaxRate taxRate = new TaxRate();
		taxRate.setTaxRateId(ticket.getActTaxRateId());
		taxRate.selectOne(conn);
		this.taxRateAmount = taxRate.getAmount();
		this.taxRateEffectiveDate = taxRate.getEffectiveDate();
		this.taxRateLocation = taxRate.getLocation();
		this.taxRate = taxRate.getRate();
		this.poNumber = job.getPoNumber();
		// populate actual po with actual if we have one.
		// else populate with job's po if we have one
		if ( StringUtils.isBlank(ticket.getActPoNumber())) {
			if ( ! StringUtils.isBlank(job.getPoNumber())) {
				this.actPoNumber = job.getPoNumber();
			}
		} else {
			this.actPoNumber = ticket.getActPoNumber();
		}
		makeJobTagList(conn, this.jobId);
	}
	
	
	
	private BigDecimal makeActualDl(Connection conn, Integer ticketId) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("select sum(dl_amt) as actuaDL from ticket_claim where ticket_claim.ticket_id=?");
		ps.setInt(1,  ticketId);
		ResultSet rs = ps.executeQuery();
		BigDecimal actuaDl = rs.next() ? rs.getBigDecimal("actuaDL") : BigDecimal.ZERO;
		return actuaDl;
	}

	
	
	private void makeJobTagList(Connection conn, Integer jobId) throws SQLException {
		this.jobTags = new HashMap<String,List<JobTagDisplay>>();
		
		// initialize the tag display list
		Statement s = conn.createStatement();
		ResultSet rs = s.executeQuery("select distinct tag_type from job_tag order by tag_type");
		while (rs.next() ) {
			this.jobTags.put(rs.getString("tag_type"), new ArrayList<JobTagDisplay>());
		}
		
		// get the tag display values
		List<JobTagDisplay> tagList = JobTagDisplay.getTags(conn, jobId);
		// make sure we've got tags to display
		if ( tagList != null && tagList.size() > 0 ) {
			Collections.sort(tagList, new Comparator<JobTagDisplay>() {
				public int compare(JobTagDisplay o1, JobTagDisplay o2) {
					return o1.getTagType().compareTo(o2.getTagType());
				}
			});
			List<JobTagDisplay> tagNameList = new ArrayList<JobTagDisplay> ();
			String previousTagType = null;
			for (JobTagDisplay tagDisplay : tagList) {
				if (!StringUtils.isBlank(previousTagType) && !tagDisplay.getTagType().equals(previousTagType)) {
					this.jobTags.put(previousTagType,tagNameList);
					tagNameList = new ArrayList<JobTagDisplay>();
				}
				tagNameList.add(tagDisplay);
				previousTagType = tagDisplay.getTagType();
			}
			this.jobTags.put(previousTagType, tagNameList);
		}
		
	}
	
	
	
	public Integer getTicketId() {
		return ticketId;
	}
	
	public HashMap<String, List<JobTagDisplay>> getJobTags() {
		return jobTags;
	}

	public void setJobTags(HashMap<String, List<JobTagDisplay>> jobTags) {
		this.jobTags = jobTags;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	
	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Integer getDivisionId() {
		return divisionId;
	}
	
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getProcessDate() {
		return processDate;
	}
	
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	
	public String getProcessNotes() {
		return processNotes;
	}
	
	public void setProcessNotes(String processNotes) {
		this.processNotes = processNotes;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActDlAmt() {
		return actDlAmt;
	}
	
	public void setActDlAmt(BigDecimal actDlAmt) {
		this.actDlAmt = actDlAmt;
	}
	
	public BigDecimal getActDlPct() {
		return actDlPct;
	}
	
	public void setActDlPct(BigDecimal actDlPct) {
		this.actDlPct = actDlPct;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActPricePerCleaning() {
		return actPricePerCleaning;
	}
	
	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}
	
	public Boolean getBillSheet() {
		return billSheet;
	}
	
	public void setBillSheet(Boolean billSheet) {
		this.billSheet = billSheet;
	}
	
	public Boolean getCustomerSignature() {
		return customerSignature;
	}
	
	public void setCustomerSignature(Boolean customerSignature) {
		this.customerSignature = customerSignature;
	}
	
	public Boolean getMgrApproval() {
		return mgrApproval;
	}
	
	public void setMgrApproval(Boolean mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	
	public List<TicketStatus> getNextAllowedStatusList() {
		return nextAllowedStatusList;
	}
	
	public void setNextAllowedStatusList(List<TicketStatus> nextAllowedStatusList) {
		this.nextAllowedStatusList = nextAllowedStatusList;
	}
	
	public Integer getJobId() {
		return jobId;
	}
	
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActTax() {
		return actTax;
	}
	
	public void setActTax(BigDecimal actTax) {
		this.actTax = actTax;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalVolPaid() {
		return totalVolPaid;
	}
	
	public void setTotalVolPaid(BigDecimal totalVolPaid) {
		this.totalVolPaid = totalVolPaid;
	}
	
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalTaxPaid() {
		return totalTaxPaid;
	}
	
	public void setTotalTaxPaid(BigDecimal totalTaxPaid) {
		this.totalTaxPaid = totalTaxPaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	public Integer getDaysToPay() {
		return daysToPay;
	}
	
	public void setDaysToPay(Integer daysToPay) {
		this.daysToPay = daysToPay;
	}

	public String getDivisionDisplay() {
		return divisionDisplay;
	}

	public void setDivisionDisplay(String divisionDisplay) {
		this.divisionDisplay = divisionDisplay;
	}

	public BigDecimal getDefaultDirectLaborPct() {
		return defaultDirectLaborPct;
	}

	public void setDefaultDirectLaborPct(BigDecimal defaultDirectLaborPct) {
		this.defaultDirectLaborPct = defaultDirectLaborPct;
	}

	public AddressDetail getJobSiteAddress() {
		return jobSiteAddress;
	}

	public void setJobSiteAddress(AddressDetail jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}

	public AddressDetail getBillToAddress() {
		return billToAddress;
	}

	public void setBillToAddress(AddressDetail billToAddress) {
		this.billToAddress = billToAddress;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
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
	
	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	@JsonSerialize(using=AnsiDateFormatter.class)
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public BigDecimal getActTaxAmt() {
		return actTaxAmt;
	}

	public void setActTaxAmt(BigDecimal actTaxAmt) {
		this.actTaxAmt = actTaxAmt;
	}

	public Integer getActTaxRateId() {
		return actTaxRateId;
	}

	public void setActTaxRateId(Integer actTaxRateId) {
		this.actTaxRateId = actTaxRateId;
	}

	public Integer getPrintCount() {
		return printCount;
	}

	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}

	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getFleetmaticsId() {
		return fleetmaticsId;
	}

	public void setFleetmaticsId(String fleetmaticsId) {
		this.fleetmaticsId = fleetmaticsId;
	}

	public String getTicketType() {
		return ticketType;
	}

	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}



	public BigDecimal getTaxRateAmount() {
		return taxRateAmount;
	}

	public void setTaxRateAmount(BigDecimal taxRateAmount) {
		this.taxRateAmount = taxRateAmount;
	}
	
	@JsonSerialize(using=AnsiDateFormatter.class)
	public Date getTaxRateEffectiveDate() {
		return taxRateEffectiveDate;
	}

	public void setTaxRateEffectiveDate(Date taxRateEffectiveDate) {
		this.taxRateEffectiveDate = taxRateEffectiveDate;
	}

	public String getTaxRateLocation() {
		return taxRateLocation;
	}

	public void setTaxRateLocation(String taxRateLocation) {
		this.taxRateLocation = taxRateLocation;
	}

	public BigDecimal getTaxRate() {
		return taxRate;
	}

	public void setTaxRate(BigDecimal taxRate) {
		this.taxRate = taxRate;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

	public String getActPoNumber() {
		return actPoNumber;
	}

	public void setActPoNumber(String actPoNumber) {
		this.actPoNumber = actPoNumber;
	}

	public String getJobFrequencyDesc() {
		return jobFrequencyDesc;
	}

	public void setJobFrequencyDesc(String jobFrequencyDesc) {
		this.jobFrequencyDesc = jobFrequencyDesc;
	}

	
}
