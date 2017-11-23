package com.ansi.scilla.web.job.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author ggroce
 *
 *
 *
 */

public class JobSearchRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;
	private String jobCode;
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
	
	public JobSearchRequest() {
		super();
	}
	
	public JobSearchRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, NoSuchMethodException {
		this();
		JobSearchRequest req = (JobSearchRequest) AppUtils.json2object(jsonString, JobSearchRequest.class);
		PropertyUtils.copyProperties(this, req);
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Integer getDivisionId() {
		return this.divisionId;
	}

	public String getJobCode() {
		return jobCode;
	}
	public void setJobCode(String jobCode) {
		this.jobCode = jobCode;
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

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
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
