package com.ansi.scilla.web.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JobDetailRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer jobId;
	private String action;
	private Date cancelDate;
	private String cancelReason;
	
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Date getCancelDate() {
		return cancelDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
	
	public static enum JobDetailRequestAction {
		ACTIVATE_JOB,
		CANCEL_JOB;
	}
}
