package com.ansi.scilla.web.response.ticketTable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.fasterxml.jackson.annotation.JsonFormat;

public class TicketTableReturnItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
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

	public TicketTableReturnItem(ResultSet rs) throws SQLException {
		super();
		this.ticketId = rs.getInt("ticket_id");
		this.status = rs.getString("status");
		this.billToName = rs.getString("bill_to_name");
		this.jobSiteName = rs.getString("job_site_name");
		this.jobSiteAddress = rs.getString("job_site_address");
		this.startDate = rs.getDate("start_date");
		this.jobFrequency = rs.getString("job_frequency");
		this.pricePerCleaning = rs.getBigDecimal("price_per_cleaning");
		this.jobNbr = rs.getInt("job_nbr");
		this.jobId = rs.getInt("job_id");
		this.serviceDescription = rs.getString("service_description");
		this.invoiceId = rs.getInt("invoice_id");
//		this.processDate = rs.getDate("process_date"); - needs to be added to the database
		this.DT_RowId = rs.getInt("ticket_id") + "";

	}

	public Integer getTicketId() {
		return ticketId;
	}

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public String getDT_RowId() {
		return DT_RowId;
	}

	public void setDT_RowId(String DT_RowId) {
		this.DT_RowId = DT_RowId;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBillToName() {
		return billToName;
	}

	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}

	public String getJobSiteName() {
		return jobSiteName;
	}

	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}

	public String getJobSiteAddress() {
		return jobSiteAddress;
	}

	public void setJobSiteAddress(String jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}

	public Date getStartDate() {
		return startDate;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public BigDecimal getPricePerCleaning() {
		return pricePerCleaning;
	}

	public void setPricePerCleaning(BigDecimal pricePerCleaning) {
		this.pricePerCleaning = pricePerCleaning;
	}

	public Integer getJobNbr() {
		return jobNbr;
	}

	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public Integer getInvoiceId() {
		return invoiceId;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Date getProcessDate() {
		return processDate;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}

}