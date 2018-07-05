package com.ansi.scilla.web.quote.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.HashMap;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.web.common.fieldValidator.AbstractPlaceHolder;
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
		validatorMap.put(QuoteRequest.ACCOUNT_TYPE, new AccountTypeValidator());
		validatorMap.put(QuoteRequest.PAYMENT_TERMS, new PaymentTermsValidator());		// com/ansi/scilla/common/invoice/InvoiceTerm.java
		validatorMap.put(QuoteRequest.LEAD_TYPE, new LeadTypeValidator());
		validatorMap.put(QuoteRequest.INVOICE_STYLE, new InvoiceStyleValidator());		// com/ansi/scilla/common/invoice/InvoiceStyle.java
		validatorMap.put(QuoteRequest.SIGNED_BY_CONTACT_ID, new ContactValidator());
		validatorMap.put(QuoteRequest.BUILDING_TYPE, new BuildingTypeValidator());			// code table
		validatorMap.put(QuoteRequest.INVOICE_GROUPING, new InvoiceGroupingValidator());	// com/ansi/scilla/common/invoice/InvoiceGrouping.java
		validatorMap.put(QuoteRequest.INVOICE_BATCH, new AbstractPlaceHolder());
		validatorMap.put(QuoteRequest.TAX_EXEMPT, new AbstractPlaceHolder());
		validatorMap.put(QuoteRequest.TAX_EXEMPT_REASON, new AbstractPlaceHolder());
	}
	
	public static void validate(Connection conn, QuoteRequest quoteRequest, String fieldName, WebMessages webMessages ) throws Exception {
		FieldValidator validator = validatorMap.get(fieldName);
		try {
			Object value = getValue(quoteRequest, fieldName);
			validator.validate(conn, fieldName, value, webMessages);
		} catch ( NoSuchMethodException e) {
			Logger logger = LogManager.getLogger(QuoteValidator.class);
			logger.log(Level.INFO, "Extra field passed in Quote Update: " + fieldName);
		}
	}

	private static Object getValue(QuoteRequest quoteRequest, String fieldName) throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String getterName = "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
		Method getter = quoteRequest.getClass().getMethod(getterName, (Class[])null);
		Object value = getter.invoke(quoteRequest, (Object[])null);
		return value;
	}
	
	
	
	
	
}
