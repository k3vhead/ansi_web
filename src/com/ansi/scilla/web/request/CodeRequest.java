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
	
	public CodeRequest() {
		super();
	}
	
	public CodeRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException {
		this();
		CodeRequest req = (CodeRequest) JsonUtils.JSON2Object(jsonString, CodeRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
