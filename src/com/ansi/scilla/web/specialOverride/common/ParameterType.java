package com.ansi.scilla.web.specialOverride.common;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.exception.InvalidFormatException;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.thewebthing.commons.lang.StringUtils;

public class ParameterType extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private String label;
	private String fieldName;
	private Class<?> type;

	
	public ParameterType(String label, String fieldName, Class<?> type) { //, Method validateMethod) {
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
	
	
	public void validate(String value) throws InvalidFormatException {
		if ( this.type.equals(Integer.class) ) {
			validateInteger(value);
		} else if ( this.type.equals(Date.class)) {
			validateDate(value);
		} else if ( this.type.equals(String.class)) {
			validateString(value);
		} else {
			throw new RuntimeException("Invalid parameter type");
		}
	}
	
	
	public void validateInteger(String value) throws InvalidFormatException {
		WebMessages webMessages = new WebMessages();
		if(StringUtils.isBlank(value)) {
			throw new InvalidFormatException();
		}
		try {
			Integer intVal = Integer.valueOf(value);
			RequestValidator.validateInteger(webMessages, getLabel(), intVal, 0, null, true);
			if ( ! webMessages.isEmpty() ) {
				throw new InvalidFormatException();
			}
		} catch ( NumberFormatException e) {
			throw new InvalidFormatException();
		}
		
	}
	
	public void validateDate(String value) throws InvalidFormatException {
		WebMessages webMessages = new WebMessages();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		if(StringUtils.isBlank(value)) {
			throw new InvalidFormatException();
		}
		try {
			java.util.Date dateVal = sdf.parse(value);
		 
			RequestValidator.validateDate(webMessages, getLabel(), dateVal, true, (Date)null, (Date)null);
			if ( ! webMessages.isEmpty() ) {
				throw new InvalidFormatException();
			}
		} catch (ParseException e) {
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
