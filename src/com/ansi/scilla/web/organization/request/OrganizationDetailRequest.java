package com.ansi.scilla.web.organization.request;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.DivisionGroup;
import com.ansi.scilla.common.exceptions.InvalidValueException;
import com.ansi.scilla.common.organization.OrganizationType;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class OrganizationDetailRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String STATUS = "status";
	public static final String NAME = "name";
	public static final String PARENT_ID = "parentId";
	public static final String COMPANY_CODE = "companyCode";

	private Boolean status;
	private String name;
	private Integer parentId;
	private String companyCode;
	
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
	public Integer getParentId() {
		return parentId;
	}
	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	public String getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public void validate(Connection conn, WebMessages webMessages, Integer organizationId) throws Exception {
		if ( status == null && StringUtils.isBlank(name) && parentId == null && StringUtils.isBlank(companyCode)) {
			//No data got here -- that's a bad thing
			//We don't know what they were trying to do, so mark everything an error and let the 
			//front end figure it out.
			webMessages.addMessage(STATUS, "Status is Required");
			webMessages.addMessage(NAME, "Name is required");
			webMessages.addMessage(PARENT_ID, "Parent is required");
			webMessages.addMessage(COMPANY_CODE, "Company Code is required");
		} else {
			// something is entered, so validate whatever is entered.
			if ( ! StringUtils.isBlank(this.name) ) {
				RequestValidator.validateString(webMessages, NAME, this.name, 45, true, "Name");
			}
			if ( status != null ) {
				RequestValidator.validateBoolean(webMessages, STATUS, this.status, true);
			}
			if ( this.parentId != null ) {
				RequestValidator.validateId(conn, webMessages, "division_group", DivisionGroup.GROUP_ID, PARENT_ID, this.parentId, true, "Parent");
			}
			if ( ! StringUtils.isBlank(this.companyCode) ) {
				RequestValidator.validateString(webMessages, COMPANY_CODE, this.companyCode, 45, false, "Company Code");
			}
		}
	}
	
	
	public void validateAdd(Connection conn, WebMessages webMessages, String organizationType) throws Exception {
		OrganizationType orgType = OrganizationType.valueOf(organizationType);
		
		
		switch ( orgType ) {
		case COMPANY:
			RequestValidator.validateString(webMessages, NAME, this.name, 45, true, "Name");
			RequestValidator.validateId(conn, webMessages, "division_group", DivisionGroup.GROUP_ID, PARENT_ID, this.parentId, true, "Parent");
			RequestValidator.validateString(webMessages, COMPANY_CODE, this.companyCode, 45, false, "Company Code");
			RequestValidator.validateBoolean(webMessages, STATUS, this.status, true);
			break;
		case DIVISION:
			throw new InvalidValueException("Invalid org type; divisison is elsewhere");
		case GROUP:
			RequestValidator.validateString(webMessages, NAME, this.name, 45, true, "Name");
			RequestValidator.validateBoolean(webMessages, STATUS, this.status, true);
			break;
		case REGION:
			RequestValidator.validateString(webMessages, NAME, this.name, 45, true, "Name");
			RequestValidator.validateBoolean(webMessages, STATUS, this.status, true);
			break;
		default:
			throw new InvalidValueException("Invalid org type: " + orgType);		
		}
		
		
	}
}
