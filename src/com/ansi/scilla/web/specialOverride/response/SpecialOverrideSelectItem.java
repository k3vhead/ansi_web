package com.ansi.scilla.web.specialOverride.response;

import com.ansi.scilla.web.specialOverride.common.ParameterType;

public class SpecialOverrideSelectItem extends SpecialOverrideResponseItem{

	private static final long serialVersionUID = 1L;

	private String label;
	private String fieldName;
	private String fieldType;
	
	public SpecialOverrideSelectItem() {
		super();
	}
	
	public SpecialOverrideSelectItem(ParameterType type) {
		this();
		this.fieldName = type.getFieldName();
		this.label = type.getLabel();
		this.fieldType = type.getType().getSimpleName();
	}
	
	
	public SpecialOverrideSelectItem(String label, String fieldName, String fieldType) {
		this();
		this.label = label;
		this.fieldName = fieldName;
		this.fieldType = fieldType;
	}

	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	
	
}
