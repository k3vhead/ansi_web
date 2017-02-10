package com.ansi.scilla.web.response.ticketTable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class TicketTableReturnItem extends ReportQuery {
	private static final long serialVersionUID = 1L;

	public static final String TICKET_ID = "ticket_id";
	public static final String STATUS ="status";
	public static final String BILL_TO_NAME ="bill_to_name";
	public static final String JOB_SITE_NAME ="job_site_name";
	public static final String JOB_SITE_ADDRESS ="job_site_address";
	public static final String START_DATE ="start_date";
	public static final String JOB_FREQUENCY ="job_frequency";
	public static final String PRICE_PER_CLEANING ="price_per_cleaning";
	public static final String JOB_NBR ="job_nbr";
	public static final String JOB_ID = "job_id";
	public static final String SERVICE_DESCRIPTION ="service_description";
	public static final String INVOICE_ID = "invoice_id";
	public static final String PROCESS_DATE = "process_date";

	private Integer ticketId;
	private String status;
	private String billToName;
	private String jobSiteName;
	private String jobSiteAddress;
	private Date startDate;
	private String jobFrequency;
	private BigDecimal pricePerCleaning;
	private Integer jobNbr;
	private Integer jobId;
	private String serviceDescription;
	private Integer invoiceId;
	private Date processDate;
	private String DT_RowId;

	public TicketTableReturnItem() throws SQLException {
		super();
	}
	public TicketTableReturnItem(ResultSet rs) throws SQLException {
		super();
		this.ticketId = rs.getInt(TICKET_ID);
		this.status = rs.getString(STATUS);
		this.billToName = rs.getString(BILL_TO_NAME);
		this.jobSiteName = rs.getString(JOB_SITE_NAME);
		this.jobSiteAddress = rs.getString(JOB_SITE_ADDRESS);
		this.startDate = rs.getDate(START_DATE);
		this.jobFrequency = rs.getString(JOB_FREQUENCY);
		this.pricePerCleaning = rs.getBigDecimal(PRICE_PER_CLEANING);
		this.jobNbr = rs.getInt(JOB_NBR);
		this.jobId = rs.getInt(JOB_ID);
		this.serviceDescription = rs.getString(SERVICE_DESCRIPTION);
		this.invoiceId = rs.getInt(INVOICE_ID);
		this.processDate = rs.getDate(PROCESS_DATE); 
		this.DT_RowId = rs.getInt(TICKET_ID) + "";
		System.out.println(this);


	}

	@DBColumn(TICKET_ID)
	public Integer getTicketId() {
		return ticketId;
	}

	@DBColumn(TICKET_ID)
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public String getDT_RowId() {
		return DT_RowId;
	}

	public void setDT_RowId(String DT_RowId) {
		this.DT_RowId = DT_RowId;
	}

	@DBColumn(STATUS)
	public String getStatus() {
		return status;
	}

	@DBColumn(STATUS)
	public void setStatus(String status) {
		this.status = status;
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

	@DBColumn(PROCESS_DATE)
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

}