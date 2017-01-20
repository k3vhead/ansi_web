package com.ansi.scilla.web.response.jobTable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.fasterxml.jackson.annotation.JsonFormat;

public class JobTableReturnItem extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer jobId;
	private String status;
	private String billToName;
	private String jobSiteName;
	private String jobSiteAddress;
	private Date startDate;
	private Integer jobNbr;
	private String jobFrequency;
	private BigDecimal pricePerCleaning;
	private String serviceDescription;
	private String poNumber;
	private String DT_RowId;

	public JobTableReturnItem(ResultSet rs) throws SQLException {
		super();
		this.jobId = rs.getInt("job_id");
		this.status = rs.getString("status");
		this.billToName = rs.getString("bill_to_name");
		this.jobSiteName = rs.getString("job_site_name");
		this.jobSiteAddress = rs.getString("job_site_address");
		this.startDate = rs.getDate("start_date");
		this.jobFrequency = rs.getString("job_frequency");
		this.pricePerCleaning = rs.getBigDecimal("price_per_cleaning");
		this.jobNbr = rs.getInt("job_nbr");
		this.serviceDescription = rs.getString("service_description");
		this.poNumber = rs.getString("po_number");
		this.DT_RowId = rs.getInt("job_id") + "";

	}

	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
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

	public String getServiceDescription() {
		return serviceDescription;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getPoNumber() {
		return poNumber;
	}

	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}

}