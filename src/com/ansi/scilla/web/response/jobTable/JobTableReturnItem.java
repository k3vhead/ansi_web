package com.ansi.scilla.web.response.jobTable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class JobTableReturnItem extends ReportQuery {
	private static final long serialVersionUID = 1L;

	public static final String JOB_ID = "job_id";
	public static final String JOB_STATUS ="job_status";
	public static final String BILL_TO_NAME ="bill_to_name";
	public static final String JOB_SITE_NAME ="job_site_name";
	public static final String JOB_SITE_ADDRESS ="job_site_address";
	public static final String JOB_SITE_CITY = "job_site_city";
	public static final String JOB_SITE_STATE = "job_site_state";
	public static final String START_DATE ="start_date";
	public static final String JOB_NBR ="job_nbr";
	public static final String JOB_FREQUENCY ="job_frequency";
	public static final String PRICE_PER_CLEANING ="price_per_cleaning";
	public static final String SERVICE_DESCRIPTION ="service_description";
	public static final String PO_NUMBER ="po_number";
	public static final String BILL_TO_ADDRESS_ID = "bill_to_address_id";
	public static final String JOB_SITE_ADDRESS_ID = "job_site_address_id";
	public static final String DIVISION_ID = "division_id";
	public static final String DIVISION_NBR = "division_nbr";
	public static final String DIVISION_CODE = "division_code";
	public static final String QUOTE_ID = "quote_id";
	public static final String QUOTE_NUMBER = "quote_number";
	public static final String REVISION = "revision";

	private Integer jobId;
	private String jobStatus;
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
	private Integer billToAddressId;
	private Integer jobSiteAddressId;
	private Integer divisionId;
	private Integer divisionNbr;
	private String divisionCode;
	private Integer quoteId;
	private Integer quoteNumber;
	private String revision;
	private String jobSiteCity;
	private String jobSiteState;

	public JobTableReturnItem() throws SQLException {
		super();
	}
	public JobTableReturnItem(ResultSet rs) throws SQLException {
		super();
		this.jobId = rs.getInt(JOB_ID);
		this.jobStatus = rs.getString(JOB_STATUS);
		this.billToName = rs.getString(BILL_TO_NAME);
		this.jobSiteName = rs.getString(JOB_SITE_NAME);
		this.jobSiteAddress = rs.getString(JOB_SITE_ADDRESS);
		this.startDate = rs.getDate(START_DATE);
		this.jobFrequency = rs.getString(JOB_FREQUENCY);
		this.pricePerCleaning = rs.getBigDecimal(PRICE_PER_CLEANING);
		this.jobNbr = rs.getInt(JOB_NBR);
		this.serviceDescription = rs.getString(SERVICE_DESCRIPTION);
		this.poNumber = rs.getString(PO_NUMBER);
		this.billToAddressId = rs.getInt(BILL_TO_ADDRESS_ID);
		this.jobSiteAddressId = rs.getInt(JOB_SITE_ADDRESS_ID);
		this.divisionId = rs.getInt(DIVISION_ID);
		this.divisionNbr = rs.getInt(DIVISION_NBR);
		this.divisionCode = rs.getString(DIVISION_CODE);
		this.DT_RowId = rs.getInt(JOB_ID) + "";
		this.quoteId = rs.getInt(QUOTE_ID);
		this.quoteNumber = rs.getInt(QUOTE_NUMBER);
		this.revision = rs.getString(REVISION);
	}

	@DBColumn(JOB_ID)
	public Integer getJobId() {
		return jobId;
	}

	@DBColumn(JOB_ID)
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public String getDT_RowId() {
		return DT_RowId;
	}

	public void setDT_RowId(String DT_RowId) {
		this.DT_RowId = DT_RowId;
	}

	@DBColumn(JOB_STATUS)
	public String getJobStatus() {
		return jobStatus;
	}

	@DBColumn(JOB_STATUS)
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
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

	@DBColumn(SERVICE_DESCRIPTION)
	public String getServiceDescription() {
		return serviceDescription;
	}

	@DBColumn(SERVICE_DESCRIPTION)
	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	@DBColumn(PO_NUMBER)
	public String getPoNumber() {
		return poNumber;
	}

	@DBColumn(PO_NUMBER)
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
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
	@DBColumn(QUOTE_ID)
	public Integer getQuoteId() {
		return quoteId;
	}
	@DBColumn(QUOTE_ID)
	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}
	@DBColumn(QUOTE_NUMBER)
	public Integer getQuoteNumber() {
		return quoteNumber;
	}
	@DBColumn(QUOTE_NUMBER)
	public void setQuoteNumber(Integer quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	@DBColumn(REVISION)
	public String getRevision() {
		return revision;
	}
	@DBColumn(REVISION)
	public void setRevision(String revision) {
		this.revision = revision;
	}
	@DBColumn(JOB_SITE_CITY)
	public String getJobSiteCity() {
		return jobSiteCity;
	}
	@DBColumn(JOB_SITE_CITY)
	public void setJobSiteCity(String jobSiteCity) {
		this.jobSiteCity = jobSiteCity;
	}
	@DBColumn(JOB_SITE_STATE)
	public String getJobSiteState() {
		return jobSiteState;
	}
	@DBColumn(JOB_SITE_STATE)
	public void setJobSiteState(String jobSiteState) {
		this.jobSiteState = jobSiteState;
	}

}