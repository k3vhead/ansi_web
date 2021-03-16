package com.ansi.scilla.web.bcr.common;

import com.ansi.scilla.common.ApplicationObject;

public class PassthruExpense extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private Integer claimId;
	private Double passthruVolume;
	private String passthruExpenseCode;
	private String passthruExpenseType;
	private String notes;
	
	private PassthruExpense() {
		super();
	}

	public PassthruExpense(Integer claimId, Double passthruVolume, String passthruExpenseCode, String passthruExpenseType, String notes) {
		this();
		this.claimId = claimId;
		this.passthruVolume = passthruVolume;
		this.passthruExpenseCode = passthruExpenseCode;
		this.passthruExpenseType = passthruExpenseType;
		this.notes = notes;
	}

	public Integer getClaimId() {
		return claimId;
	}

	public Double getPassthruVolume() {
		return passthruVolume;
	}

	public String getPassthruExpenseCode() {
		return passthruExpenseCode;
	}

	public String getPassthruExpenseType() {
		return passthruExpenseType;
	}

	public String getNotes() {
		return notes;
	}
}
