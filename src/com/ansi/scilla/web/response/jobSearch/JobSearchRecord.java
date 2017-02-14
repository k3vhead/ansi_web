package com.ansi.scilla.web.response.jobSearch;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.queries.JobSearch;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.lang.BeanUtils;

/**
 * 
 * @author ggroce
 *
 */

public class JobSearchRecord extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	private Integer divisionId;
	private String jobFrequency;
	private Integer jobId;
	private Integer jobNbr;
	private String poNumber;
	private BigDecimal pricePerCleaning;
	private Integer quoteId;
	private String serviceDescription;
	private Date startDate;
	private String status;
	
	private String billToName;
	private String divisionCode;
	private String jobSiteAddress;
	private String jobSiteName;
	private String quoteNumber;
	private String revision;
	
	public JobSearchRecord(JobSearch jobSearch) throws IllegalAccessException, InvocationTargetException {
		this();
		BeanUtils.copyProperties(this, jobSearch.getJob());
		BeanUtils.copyProperties(this, jobSearch.getQuote());
		BeanUtils.copyProperties(this,  jobSearch.getDivision());
		this.billToName = jobSearch.getBillToName();			
		this.jobSiteAddress = jobSearch.getJobSiteAddress();	
		this.jobSiteName = jobSearch.getJobSiteName();			
		
	}

	public JobSearchRecord() {
		super();
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getDivisionId() {
		return this.divisionId;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}
	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getJobId() {
		return this.jobId;
	}

	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	public Integer getJobNbr() {
		return this.jobNbr;
	}

	public String getPoNumber() {
		return poNumber;
	}
	public void setPoNumber(String poNumber) {
		this.poNumber = poNumber;
	}
   public void setPricePerCleaning(BigDecimal pricePerCleaning) {
        this.pricePerCleaning = pricePerCleaning;
    }

    public BigDecimal getpricePerCleaning() {
        return this.pricePerCleaning;
    }


	public void setQuoteId(Integer quoteId) {
		this.quoteId = quoteId;
	}

	public Integer getQuoteId() {
		return this.quoteId;
	}

	public void setServiceDescription(String serviceDescription) {
		this.serviceDescription = serviceDescription;
	}

	public String getServiceDescription() {
		return this.serviceDescription;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public String getBillToName() {
		return billToName;
	}
	public void setBillToName(String billToName) {
		this.billToName = billToName;
	}
	public String getDivisionCode() {
		return divisionCode;
	}
	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}
	public String getJobSiteAddress() {
		return jobSiteAddress;
	}
	public void setJobSiteAddress(String jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}
	public String getJobSiteName() {
		return jobSiteName;
	}
	public void setJobSiteName(String jobSiteName) {
		this.jobSiteName = jobSiteName;
	}
	public String getQuoteNumber() {
		return quoteNumber;
	}
	public void setQuoteNumber(String quoteNumber) {
		this.quoteNumber = quoteNumber;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	
}