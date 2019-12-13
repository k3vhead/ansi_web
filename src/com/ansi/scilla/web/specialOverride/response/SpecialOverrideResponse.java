package com.ansi.scilla.web.specialOverride.response;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;

public class SpecialOverrideResponse extends MessageResponse{

	private static final long serialVersionUID = 1L;
	SpecialOverrideType[] sot = SpecialOverrideType.values();
	
	public SpecialOverrideResponse() {
		super();
		
	}
	
	
	
}
