package com.ansi.scilla.web.payroll.response;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.db.PayrollTaxProfile;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TaxProfileResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	public static final String PROFILE_ID = "profileId";
	public static final String PROFILE_DESC = "profileDesc";
	public static final String REGULAR_HOURS = "regularHours";
	public static final String REGULAR_PAY = "regularPay";
	public static final String OT_HOURS = "otHours";
	public static final String OT_PAY = "otPay";
	
	private Integer profileId;
	private String profileDesc;
	private String regularHours;
	private String regularPay;
	private String otHours;
	private String otPay;
	
	public TaxProfileResponse() {
		super();
	}
	
	public TaxProfileResponse(Connection conn, Integer profileId) throws RecordNotFoundException, Exception {
		this();
		PayrollTaxProfile profile = new PayrollTaxProfile();
		profile.setProfileId(profileId);
		profile.selectOne(conn);
		BeanUtils.copyProperties(this, profile);
		this.profileDesc = profile.getDesc();
	}

	public Integer getProfileId() {
		return profileId;
	}

	public void setProfileId(Integer profileId) {
		this.profileId = profileId;
	}

	public String getProfileDesc() {
		return profileDesc;
	}

	public void setProfileDesc(String profileDesc) {
		this.profileDesc = profileDesc;
	}

	public String getRegularHours() {
		return regularHours;
	}

	public void setRegularHours(String regularHours) {
		this.regularHours = regularHours;
	}

	public String getRegularPay() {
		return regularPay;
	}

	public void setRegularPay(String regularPay) {
		this.regularPay = regularPay;
	}

	public String getOtHours() {
		return otHours;
	}

	public void setOtHours(String otHours) {
		this.otHours = otHours;
	}

	public String getOtPay() {
		return otPay;
	}

	public void setOtPay(String otPay) {
		this.otPay = otPay;
	}

	
	
	
	
	
	
}
