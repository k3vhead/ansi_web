package com.ansi.scilla.web.ticket.request;

import com.ansi.scilla.web.common.request.AbstractRequest;

public class TicketApprovalOverride extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	private Boolean customerSignature;
	private Boolean managerApproval;
	private Boolean billSheet;
	
	public Boolean getCustomerSignature() {
		return customerSignature;
	}
	public void setCustomerSignature(Boolean customerSignature) {
		this.customerSignature = customerSignature;
	}
	public Boolean getManagerApproval() {
		return managerApproval;
	}
	public void setManagerApproval(Boolean managerApproval) {
		this.managerApproval = managerApproval;
	}
	public Boolean getBillSheet() {
		return billSheet;
	}
	public void setBillSheet(Boolean billSheet) {
		this.billSheet = billSheet;
	}
	
}
