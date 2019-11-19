package com.ansi.scilla.web.specialOverride.common;

import java.lang.reflect.Method;
import java.sql.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class ParameterType extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	static Method integerValidatorMethod;
	static Method dateValidatorMethod;
	static Method stringValidatorMethod;
	
	private String label;
	private String fieldName;
	private Class<?> type;
	private Method validateMethod;
	
	
	static {
		try {
			integerValidatorMethod = ParameterType.class.getMethod("validateInteger", new Class<?>[] {Integer.class});		
			dateValidatorMethod = ParameterType.class.getMethod("validateDate", new Class<?>[] {Date.class});		
			stringValidatorMethod = ParameterType.class.getMethod("validateString", new Class<?>[] {String.class});
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public ParameterType(String label, String fieldName, Class<?> type, Method validateMethod) {
		super();
		this.label = label;
		this.fieldName = fieldName;
		this.type = type;
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
	public Class<?> getType() {
		return type;
	}
	public void setType(Class<?> type) {
		this.type = type;
	}
	public Method getValidateMethod() {
		return validateMethod;
	}
	public void setValidateMethod(Method validateMethod) {
		this.validateMethod = validateMethod;
	}
	
	public void validateInteger(Integer value) throws InvalidFormatException {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateInteger(webMessages, getLabel(), value, 0, null, true);
		if ( ! webMessages.isEmpty() ) {
			throw new InvalidFormatException();
		}
	}
	
	public void validateDate(Date value) throws InvalidFormatException {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateDate(webMessages, getLabel(), value, true, (Date)null, (Date)null);
		if ( ! webMessages.isEmpty() ) {
			throw new InvalidFormatException();
		}
	}
	
	public void validateString(String value) throws InvalidFormatException {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateString(webMessages, getLabel(), value, true);
		if ( ! webMessages.isEmpty() ) {
			throw new InvalidFormatException();
		}
	}
	
}
