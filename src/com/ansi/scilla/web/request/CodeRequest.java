package com.ansi.scilla.web.request;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.thewebthing.commons.lang.JsonException;
import com.thewebthing.commons.lang.JsonUtils;

public class CodeRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private String tableName;
	private String fieldName;
	private String value;
	private String description;
	private String displayValue;
	private Integer seq;
	private Integer status;

	
	public CodeRequest() {
		super();
	}
	
	public CodeRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException {
		this();
		CodeRequest req = (CodeRequest) JsonUtils.JSON2Object(jsonString, CodeRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDisplayValue() {
		return displayValue;
	}

	public void setDisplayValue(String displayValue) {
		this.displayValue = displayValue;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
