package com.ansi.scilla.web.specialOverride.response;

import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.specialOverride.common.ParameterType;
import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;

public class SpecialOverrideResponse extends MessageResponse{

	private static final long serialVersionUID = 1L;
	private List<SpecialOverrideResponseItem> itemList;
	
	
	
	public SpecialOverrideResponse() {
		super();
		this.itemList = new ArrayList<SpecialOverrideResponseItem>();
		for ( SpecialOverrideType reference : SpecialOverrideType.values() ) {
			this.itemList.add(new NameDescriptionResponseItem(reference));
		}
	}
	
	public SpecialOverrideResponse(ParameterType[] parameterTypes) {
		super();
		this.itemList = new ArrayList<SpecialOverrideResponseItem>();
		for ( ParameterType reference : parameterTypes ) {
			this.itemList.add(new SpecialOverrideSelectItem(reference));
		}
	}


	public List<SpecialOverrideResponseItem> getItemList() {
		return itemList;
	}

	public void setItemList(List<SpecialOverrideResponseItem> itemList) {
		this.itemList = itemList;
	}
	
	
	
}
