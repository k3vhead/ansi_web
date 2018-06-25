package com.ansi.scilla.web.quote.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.fieldValidator.FieldValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.quote.request.QuoteRequest;

public class QuoteValidator extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private static final HashMap<String, FieldValidator> validatorMap;
	
	static {
		validatorMap = new HashMap<String, FieldValidator>();
		validatorMap.put(QuoteRequest.QUOTE_ID, new QuoteIdValidator());
		validatorMap.put(QuoteRequest.CONTRACT_CONTACT_ID, new ContactValidator());
		validatorMap.put(QuoteRequest.BILLING_CONTACT_ID, new ContactValidator());
		validatorMap.put(QuoteRequest.JOB_CONTACT_ID, new ContactValidator());
		validatorMap.put(QuoteRequest.SITE_CONTACT, new ContactValidator());
		
		validatorMap.put(QuoteRequest.JOB_SITE_ADDRESS_ID, new AddressValidator());
		validatorMap.put(QuoteRequest.BILL_TO_ADDRESS_ID, new AddressValidator());

		validatorMap.put(QuoteRequest.MANAGER_ID, new ManagerIdValidator());
		validatorMap.put(QuoteRequest.DIVISION_ID, new DivisionIdValidator());
		validatorMap.put(QuoteRequest.LEAD_TYPE, new LeadTypeValidator());
		validatorMap.put(QuoteRequest.ACCOUNT_TYPE, new AccountTypeValidator());
	}
	
	public static void validate(Connection conn, QuoteRequest quoteRequest, String fieldName, WebMessages webMessages ) throws Exception {
		FieldValidator validator = validatorMap.get(fieldName);
		Object value = getValue(quoteRequest, fieldName);
		validator.validate(conn, fieldName, value, webMessages);
	}

	private static Object getValue(QuoteRequest quoteRequest, String fieldName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method getter = quoteRequest.getClass().getMethod(getterName, (Class[])null);
		Object value = getter.invoke(quoteRequest, (Object[])null);
		return value;
	}
	
	
	
	
	
}
