package com.ansi.scilla.web.payroll.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class TaxProfileRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;

	public static final String PROFILE_DESC = "profileDesc";
	public static final String PROFILE_ID = "profileId";
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
	public WebMessages validateAdd() {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateString(webMessages, PROFILE_DESC, this.profileDesc, 128, true, "Description");
		RequestValidator.validateString(webMessages, REGULAR_HOURS, regularHours, 4, true, null);
		RequestValidator.validateString(webMessages, REGULAR_PAY, regularPay, 4, true, null);
		RequestValidator.validateString(webMessages, OT_HOURS, otHours, 4, true, null);
		RequestValidator.validateString(webMessages, OT_PAY, otPay, 4, true, null);
		return webMessages;
	}
	
	public WebMessages validateUpdate(Connection conn, Integer profileId) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "payroll_tax_profile", "profile_id", PROFILE_ID, profileId, true);
		if ( webMessages.isEmpty() ) {
			webMessages = validateAdd();
		}
		return webMessages;
	}
	
	public WebMessages validateDelete(Connection conn, Integer profileId) throws Exception {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateId(conn, webMessages, "payroll_tax_profile", "profile_id", PROFILE_ID, profileId, true);
		return webMessages;
	}


	
	
	
}
