package com.ansi.scilla.web.specialOverride.response;

import com.ansi.scilla.web.specialOverride.common.SpecialOverrideType;

public class NameDescriptionResponseItem extends SpecialOverrideResponseItem implements Comparable<NameDescriptionResponseItem> {
	
	private static final long serialVersionUID = 1L;
	String name, description;
	
	public NameDescriptionResponseItem() {
		super();
	}
	
	public NameDescriptionResponseItem(SpecialOverrideType type) {
		this();
		this.name = type.name();
		this.description = type.getDisplay();
	}

	public NameDescriptionResponseItem(String name, String description) {
		super();
		this.name = name;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int compareTo(NameDescriptionResponseItem o) {
		return this.name.compareTo(o.getName());
	}
	
}
