package com.ansi.scilla.web.specialOverride.common;

import java.lang.reflect.Method;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;

public class ParameterType extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	static Method integerValidatorMethod;
	
	private String label;
	private Class<?> type;
	private Method validateMethod;
	
	static {
		try {
			integerValidatorMethod = ParameterType.class.getMethod("validateInteger", new Class<?>[] {Integer.class});
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
	}
	public ParameterType(String label, Class<?> type, Method validateMethod) {
		super();
		this.label = label;
		this.type = type;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
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
	
}
