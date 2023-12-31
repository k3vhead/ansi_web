package com.ansi.scilla.web.ticket.response;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.ansi.scilla.common.queries.TicketLookupSearchItem;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class TicketTableReturnItem extends ReportQuery {
	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticket_id";
	public static final String TICKET_STATUS ="ticket_status";
	public static final String BILL_TO_NAME ="bill_to_name";
	public static final String JOB_SITE_NAME ="job_site_name";
	public static final String JOB_SITE_ADDRESS ="job_site_address";
	public static final String START_DATE ="start_date";
	public static final String JOB_FREQUENCY ="job_frequency";
	public static final String PRICE_PER_CLEANING ="price_per_cleaning";
	public static final String ACT_PRICE_PER_CLEANING ="act_price_per_cleaning";
	public static final String JOB_NBR ="job_nbr";
	public static final String JOB_ID = "job_id";
	public static final String SERVICE_DESCRIPTION ="service_description";
	public static final String INVOICE_ID = "invoice_id";
	public static final String INVOICE_DATE = "invoice_date";
	public static final String PROCESS_DATE = "process_date";
	public static final String BILL_TO_ADDRESS_ID = "bill_to_address_id";
	public static final String JOB_SITE_ADDRESS_ID = "job_site_address_id";
	public static final String DIVISION_ID = "division_id";
	public static final String DIVISION_NBR = "division_nbr";
	public static final String DIVISION_CODE = "division_code";
	public static final String FLEETMATICS_ID = "fleetmatics_id";
	/* Ticket Payment Totals */
	public static final String PAID_AMOUNT = "paid_amount";
	public static final String PAID_TAX_AMT = "paid_tax_amt";
	public static final String AMOUNT_DUE = "amount_due";

	

	private Integer ticketId;
	private String ticketStatus;
	private String ticketType;
	private String billToName;
	private String jobSiteName;
	private String jobSiteAddress;
	private Date startDate;
	private String jobFrequency;
	private BigDecimal pricePerCleaning;
	private BigDecimal actPricePerCleaning;
	private Integer jobNbr;
	private Integer jobId;
	private String serviceDescription;
	private Integer invoiceId;
	private Date invoiceDate;
	private Date processDate;
	private String DT_RowId;
	private Integer billToAddressId;
	private Integer jobSiteAddressId;
	private Integer divisionId;
	private Integer divisionNbr;
	private String divisionCode;
	private String fleetmaticsId;

	private BigDecimal paidAmount;
	private BigDecimal paidTaxAmt;
	private BigDecimal amountDue;

	public TicketTableReturnItem()  {
		super();
	}
	public TicketTableReturnItem(ResultSet rs) throws SQLException {
		super();
		this.ticketId = rs.getInt(TICKET_ID);
		this.ticketStatus = rs.getString(TICKET_STATUS);
		this.billToName = rs.getString(BILL_TO_NAME);
		this.jobSiteName = rs.getString(JOB_SITE_NAME);
		this.jobSiteAddress = rs.getString(JOB_SITE_ADDRESS);
		this.startDate = rs.getDate(START_DATE);
		this.jobFrequency = rs.getString(JOB_FREQUENCY);
		this.pricePerCleaning = rs.getBigDecimal(PRICE_PER_CLEANING);
		this.actPricePerCleaning = rs.getBigDecimal(ACT_PRICE_PER_CLEANING);
		this.jobNbr = rs.getInt(JOB_NBR);
		this.jobId = rs.getInt(JOB_ID);
		this.serviceDescription = rs.getString(SERVICE_DESCRIPTION);
		this.invoiceId = rs.getInt(INVOICE_ID);
		this.invoiceDate = rs.getDate(INVOICE_DATE); 
		this.processDate = rs.getDate(PROCESS_DATE); 
		this.billToAddressId = rs.getInt(BILL_TO_ADDRESS_ID);
		this.jobSiteAddressId = rs.getInt(JOB_SITE_ADDRESS_ID);
		this.divisionId = rs.getInt(DIVISION_ID);
		this.divisionNbr = rs.getInt(DIVISION_NBR);
		this.divisionCode = rs.getString(DIVISION_CODE);
		this.fleetmaticsId = rs.getString(FLEETMATICS_ID);
		this.DT_RowId = rs.getInt(TICKET_ID) + "";
		this.paidAmount = rs.getBigDecimal(PAID_AMOUNT);
		this.paidTaxAmt = rs.getBigDecimal(PAID_TAX_AMT);
		this.amountDue = rs.getBigDecimal(AMOUNT_DUE);
	}

	public TicketTableReturnItem(TicketLookupSearchItem item) throws Exception {
		this();
		this.ticketId = item.getViewTicketLog().getTicketId();
		this.ticketStatus = item.getViewTicketLog().getStatus();
		this.ticketType = item.getTicket().getTicketType();
		this.billToName = item.getBillToName();
		this.jobSiteName = item.getJobSiteName();
		this.jobSiteAddress = item.getJobSiteAddress();
		this.startDate = item.getViewTicketLog().getStartDate();
		this.jobFrequency = item.getJobFrequency();
		this.pricePerCleaning = item.getPricePerCleaning();
		this.actPricePerCleaning = item.getTicket().getActPricePerCleaning();
		this.jobNbr = item.getJobNbr();
		this.jobId = item.getViewTicketLog().getJobId();
		this.serviceDescription = item.getServiceDescription();
		this.invoiceId = item.getTicket().getInvoiceId();
		this.invoiceDate =  item.getTicket().getInvoiceDate();
		this.processDate =  item.getTicket().getProcessDate();
		this.billToAddressId = item.getQuote().getBillToAddressId();
		this.jobSiteAddressId = item.getQuote().getJobSiteAddressId();
		this.divisionId = item.getDivision().getDivisionId();
		this.divisionNbr = item.getDivision().getDivisionNbr();
		this.divisionCode = item.getDivision().getDivisionCode();
		this.fleetmaticsId = item.getTicket().getFleetmaticsId();
		this.DT_RowId = String.valueOf(item.getTicket().getTicketId());
		this.paidAmount = item.getPaidAmount();
		this.paidTaxAmt = item.getPaidTaxAmt();
		this.amountDue = item.getAmountDue();

	}
	
	@DBColumn(TICKET_ID)
	public Integer getTicketId() {
		return ticketId;
	}

	@DBColumn(TICKET_ID)
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public String getTicketType() {
		return this.ticketType;
	}
	
	public void setTicketType(String ticketType) {
		this.ticketType = ticketType;
	}
	public String getDT_RowId() {
		return DT_RowId;
	}

	public void setDT_RowId(String DT_RowId) {
		this.DT_RowId = DT_RowId;
	}

	@DBColumn(TICKET_STATUS)
	public String getTicketStatus() {
		return ticketStatus;
	}

	@DBColumn(TICKET_STATUS)
	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	@DBColumn(BILL_TO_NAME)
	public String getBillToName() {
		return billToName;
	}

	@DBColumn(BILL_TO_NAME)
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}

	@DBColumn(JOB_SITE_NAME)
	public String getJobSiteName() {
		return jobSiteName;
	}

	@DBColumn(JOB_SITE_NAME)
	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}

	@DBColumn(JOB_SITE_ADDRESS)
	public String getJobSiteAddress() {
		return jobSiteAddress;
	}

	@DBColumn(JOB_SITE_ADDRESS)
	public void setJobSiteAddress(String jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}

	@DBColumn(START_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getStartDate() {
		return startDate;
	}

	@DBColumn(START_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@DBColumn(JOB_FREQUENCY)
	public String getJobFrequency() {
		return jobFrequency;
	}

	@DBColumn(JOB_FREQUENCY)
	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	@DBColumn(ACT_PRICE_PER_CLEANING)
	@JsonSerialize(using = AnsiCurrencyFormatter.class)
	public BigDecimal getActPricePerCleaning() {
		return actPricePerCleaning;
	}

	@DBColumn(ACT_PRICE_PER_CLEANING)
	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}

	@DBColumn(PRICE_PER_CLEANING)
	@JsonSerialize(using = AnsiCurrencyFormatter.class)
	public BigDecimal getPricePerCleaning() {
		return pricePerCleaning;
	}

	@DBColumn(PRICE_PER_CLEANING)
	public void setPricePerCleaning(BigDecimal pricePerCleaning) {
		this.pricePerCleaning = pricePerCleaning;
	}

	@DBColumn(JOB_NBR)
	public Integer getJobNbr() {
		return jobNbr;
	}

	@DBColumn(JOB_NBR)
	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	@DBColumn(JOB_ID)
	public Integer getJobId() {
		return jobId;
	}

	@DBColumn(JOB_ID)
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	@DBColumn(SERVICE_DESCRIPTION)
	public String getServiceDescription() {
		return serviceDescription;
	}

	@DBColumn(SERVICE_DESCRIPTION)
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@DBColumn(INVOICE_ID)
	public Integer getInvoiceId() {
		return invoiceId;
	}

	@DBColumn(INVOICE_ID)
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	@DBColumn(PROCESS_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getProcessDate() {
		return processDate;
	}

	@DBColumn(INVOICE_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	@DBColumn(INVOICE_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public Date getInvoiceDate() {
		return invoiceDate;
	}

	@DBColumn(PROCESS_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

	@DBColumn(BILL_TO_ADDRESS_ID)
	public Integer getBillToAddressId() {
		return billToAddressId;
	}

	@DBColumn(BILL_TO_ADDRESS_ID)
	public void setBillToAddressId(Integer billToAddressId) {
		this.billToAddressId = billToAddressId;
	}

	@DBColumn(JOB_SITE_ADDRESS_ID)
	public Integer getJobSiteAddressId() {
		return jobSiteAddressId;
	}

	@DBColumn(JOB_SITE_ADDRESS_ID)
	public void setJobSiteAddressId(Integer jobSiteAddressId) {
		this.jobSiteAddressId = jobSiteAddressId;
	}

	@DBColumn(DIVISION_ID)
	public Integer getDivisionId() {
		return divisionId;
	}

	@DBColumn(DIVISION_ID)
	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	@DBColumn(DIVISION_NBR)
	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	@DBColumn(DIVISION_NBR)
	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}

	@DBColumn(DIVISION_CODE)
	public String getDivisionCode() {
		return divisionCode;
	}

	@DBColumn(DIVISION_CODE)
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	@DBColumn(FLEETMATICS_ID)
	public String getFleetmaticsId() {
		return fleetmaticsId;
	}

	@DBColumn(FLEETMATICS_ID)
	public void setFleetmaticsId(String fleetmaticsId) {
		this.fleetmaticsId = fleetmaticsId;
	}

	@DBColumn(PAID_AMOUNT)
	@JsonSerialize(using = AnsiCurrencyFormatter.class)
	public BigDecimal getPaidAmount() {
		return paidAmount;
	}

	@DBColumn(PAID_AMOUNT)
	public void setPaidAmount(BigDecimal paidAmount) {
		this.pricePerCleaning = paidAmount;
	}
	
	@DBColumn(PAID_TAX_AMT)
	@JsonSerialize(using = AnsiCurrencyFormatter.class)
	public BigDecimal getPaidTaxAmt() {
		return paidTaxAmt;
	}

	@DBColumn(PAID_TAX_AMT)
	public void setPaidTaxAmt(BigDecimal paidTaxAmt) {
		this.paidTaxAmt = paidTaxAmt;
	}
	
	@DBColumn(AMOUNT_DUE)
	@JsonSerialize(using = AnsiCurrencyFormatter.class)
	public BigDecimal getAmountDue() {
		return amountDue;
	}

	@DBColumn(AMOUNT_DUE)
	public void setAmountDue(BigDecimal amountDue) {
		this.amountDue = amountDue;
	}
	
}