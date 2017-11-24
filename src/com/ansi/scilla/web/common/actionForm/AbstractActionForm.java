package com.ansi.scilla.web.common.actionForm;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMessage;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.thewebthing.commons.lang.StringUtils;

public abstract class AbstractActionForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	public static final String FORMAT_IS_JSON = "json";
    public static final String FORMAT_IS_HTML = "html";

    public static final String FORMAT = "format";

    private String format;

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	
	protected ActionErrors validateFields(String[] fieldNames, String errorKey) throws Exception {
		ActionErrors errors = new ActionErrors();
		Class<?> myClass = this.getClass();
		for ( String fieldName : fieldNames ) {
			String getterName = "get" + WordUtils.capitalize(fieldName);
			Method getter = myClass.getMethod(getterName);
			Object obj = getter.invoke(this, (Object[])null);
			if ( obj == null || StringUtils.isBlank(String.valueOf(obj))) {
				errors.add(fieldName, new ActionMessage(errorKey));
			}
		}
		
		return errors;
	}
	
	protected boolean validateOneOf(String[] fieldNames) throws Exception {
		boolean inputFound = false;
		
		Class<?> myClass = this.getClass();
		for ( String fieldName : fieldNames ) {
			String getterName = "get" + WordUtils.capitalize(fieldName);
			Method getter = myClass.getMethod(getterName);
			Object obj = getter.invoke(this, (Object[])null);
			if ( obj != null && ! StringUtils.isBlank(String.valueOf(obj))) {
				inputFound = true;
			}
		}
		
		return inputFound;
	}

	
	protected ActionErrors validateNumbers(String[] fieldNames, String errorKey) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		ActionErrors errors = new ActionErrors();
		
		Class<?> myClass = this.getClass();
		for ( String fieldName : fieldNames ) {
			String getterName = "get" + WordUtils.capitalize(fieldName);
			Method getter = myClass.getMethod(getterName);
			String obj = (String)getter.invoke(this, (Object[])null);
			
			if ( ! StringUtils.isBlank(obj) && ! NumberUtils.isNumber(obj)) {
				errors.add(fieldName, new ActionMessage(errorKey));
			}
		}
		
		return errors;		
	}
	
	
	public void doReset() throws Exception {
		Field[] fields = this.getClass().getDeclaredFields();
		for ( Field field : fields ) {
			int modifier = field.getModifiers();
			if ( Modifier.isPrivate(modifier)) {
				String setterName = "set" + WordUtils.capitalize(field.getName());
				try {
					Method setter = this.getClass().getMethod(setterName, new Class[] { String.class } );
					setter.invoke(this, new Object[] {null});
				} catch ( NoSuchMethodException e) {
					// we don't care, just don't try to set value
				}
			}
		}
	}

	
	public void doReset(String[] fieldNames) throws Exception {
		for ( String fieldName : fieldNames ) {
			String setterName = "set" + WordUtils.capitalize(fieldName);
			try {
				Method setter = this.getClass().getMethod(setterName, new Class[] { String.class } );
				setter.invoke(this, new Object[] {""});
			} catch ( NoSuchMethodException e) {
				// we don't care, just don't try to set value
			}
		}
	}

	
	public String toJson() throws Exception {
		return AppUtils.object2json(this);
	}
	
	@Override
	public String toString() {
		return StringUtils.prettyPrint(this);
	}
}
