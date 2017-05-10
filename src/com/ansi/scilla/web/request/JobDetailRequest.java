package com.ansi.scilla.web.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class JobDetailRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer jobId;
	private String action;
	private Date cancelDate;
	private String cancelReason;
	private Date activationDate;
	private Date startDate;
	private Boolean annualRepeat;
	
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getCancelDate() {
		return cancelDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setCancelDate(Date cancelDate) {
		this.cancelDate = cancelDate;
	}
	public String getCancelReason() {
		return cancelReason;
	}
	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")	
	public Date getActivationDate() {
		return activationDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setActivationDate(Date activationDate) {
		this.activationDate = activationDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Boolean getAnnualRepeat() {
		return annualRepeat;
	}
	public void setAnnualRepeat(Boolean annualRepeat) {
		this.annualRepeat = annualRepeat;
	}

	public static enum JobDetailRequestAction {
		ACTIVATE_JOB,
		CANCEL_JOB,
		SCHEDULE_JOB,
		REPEAT_JOB,
		UPDATE_JOB,
		ADD_JOB;
		
	}
}
