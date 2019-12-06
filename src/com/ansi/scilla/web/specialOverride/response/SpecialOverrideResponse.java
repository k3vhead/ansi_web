package com.ansi.scilla.web.specialOverride.response;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.permission.response.PermissionDisplayItem;
import com.ansi.scilla.web.specialOverride.common.ParameterType;

public class SpecialOverrideResponse extends MessageResponse{

	private static final long serialVersionUID = 1L;
	private List<List<ParameterType>> parameterList;
	
	public SpecialOverrideResponse() {
		super();
		makeMasterList(new ArrayList<ParameterType>());
	}
	
	private void makeMasterList(List<ParameterType> groupParameterList) {
		parameterList = new ArrayList<List<ParameterType>>();

		for ( Permission p : Permission.values() ) {
			if ( p.getParent() == null ) {
				List<PermissionDisplayItem> sublist = new ArrayList<PermissionDisplayItem>();
				sublist.add(new PermissionDisplayItem(p, false));
				
			}
		}
	}
	
	
}
