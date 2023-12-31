package com.ansi.scilla.web.job.request;

import java.util.Date;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Use JobRequest instead (which has a superset of these attributes/methods)
 * @author dclewis
 *
 */
@Deprecated
public class JobDetailRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Integer jobId;
	private String action;
	private Date cancelDate;
	private String cancelReason;
	private Date activationDate;
	private Date startDate;
	private Date proposalDate;
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
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setProposalDate(Date proposalDate) {
		this.proposalDate = proposalDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getProposalDate() {
		return proposalDate;
	}

	public Boolean getAnnualRepeat() {
		return annualRepeat;
	}
	public void setAnnualRepeat(Boolean annualRepeat) {
		this.annualRepeat = annualRepeat;
	}

	/**
	 * Use JobReqeustAction enum instead
	 * @author dclewis
	 *
	 */
	@Deprecated
	public static enum JobDetailRequestAction {
		ACTIVATE_JOB,
		CANCEL_JOB,
		DELETE_JOB,
		SCHEDULE_JOB,
		PROPOSE_JOB,
		REPEAT_JOB,
		UPDATE_JOB,
		ADD_JOB;
		
	}
}
