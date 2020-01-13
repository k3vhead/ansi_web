package com.ansi.scilla.web.specialOverride.request;

import java.sql.Connection;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.response.WebMessages;

public abstract class SpecialOverrideRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	
	private Boolean confirmOverride;


	public Boolean getConfirmOverride() {
		return confirmOverride;
	}


	public void setConfirmOverride(Boolean confirmOverride) {
		this.confirmOverride = confirmOverride;
	}
	
	
	public abstract WebMessages validate(Connection conn) throws Exception;
	
}
