package com.ansi.scilla.web.ticket.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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

public class TicketSearchRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private String completionNotes;
	private Integer invoiceId;
	private Integer jobId;
    private Integer printCount;
	private Date startDate;
	private String status;
	private Integer ticketId;
	private Integer ticketNbr;

	private String billToName;
	private String jobSiteAddress;
	private String jobSiteName;
	private String quoteNumber;
	private String revision;
	private String jobStatus;
	
	public TicketSearchRequest() {
		super();
	}
	
	public TicketSearchRequest(String jsonString) throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException  {
		this();
		TicketSearchRequest req = new TicketSearchRequest();
		AppUtils.json2object(jsonString, req);
		PropertyUtils.copyProperties(this, req);
	}

	public void setCompletionNotes(String completionNotes) {
		this.completionNotes = completionNotes;
	}

	public String getCompletionNotes() {
		return this.completionNotes;
	}

	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}

	public Integer getInvoiceId() {
		return this.invoiceId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getJobId() {
		return this.jobId;
	}

    public void setPrintCount(Integer printCount) {
        this.printCount = printCount;
    }

    public Integer getPrintCount() {
        return this.printCount;
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

	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}

	public Integer getTicketId() {
		return this.ticketId;
	}

	public void setTicketNbr(Integer ticketNbr) {
		this.ticketNbr = ticketNbr;
	}

	public Integer getTicketNbr() {
		return this.ticketNbr;
	}

	public String getBillToName() {
		return billToName;
	}
	public void setBillToName(String billToName) {
		this.billToName = billToName;
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
	public String getJobStatus() {
		return jobStatus;
	}
	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}
	
}
