package com.ansi.scilla.web.tax.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Used to request data from the TaxRate table
 * 
 * To get a list of all taxRates:
 * 
 * 	Request object is not used (GET /taxRate/list)
 *  A TaxRateListResponse is returned
 *   
 * To get a particluar tax Rate:
 * 	Request object is not used (GET /taxRate/&lt;taxRateId&gt;)
 *  A TaxRateListResponse is returned
 *  
 * To add a new tax Rate:
 *  POST /taxRate/add with POSTED json string like:
 *  	{"location":"Webthing HQ","rate":123.45,"taxAmount":0.03,"effectiveDate":"12/01/2016"}
 *  A TaxRateResponse is returned
 *  
 * To upate an existing tax Rate:
 * 	POST /taxRate/&lt;taxRateId&gt; with POSTED json string like:
 * 		{"taxRateId":123,"location":"Webthing HQ","rate":123.45,"taxAmount":0.03,"effectiveDate":"12/01/2016"}
 *  A TaxRateResponse is returned
 * 
 * @author gagroce
 *
 */
public class TaxRateRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	public static final String AMOUNT = "amount";
	public static final String EFFECTIVE_DATE = "effectiveDate";
	public static final String LOCATION = "location";
	public static final String RATE = "rate";
	public static final String TAX_RATE_ID = "taxRateId";
	
	private BigDecimal amount;
	private Date effectiveDate;
	private String location;
	private BigDecimal rate;
	private Integer taxRateId;
	
	public TaxRateRequest() {
		super();
	}
	
	/**
	 * Generate a TaxRateRequest from expected JSON string as defined above
	 * Exceptions are thrown when the JSON string does not match expectations
	 * 
	 * @param jsonString
	 * @throws InstantiationException 
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NoSuchMethodException 
	 */
	public TaxRateRequest(String jsonString) throws JsonParseException, JsonMappingException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, IOException  {
		this();
		TaxRateRequest req = new TaxRateRequest();
		AppUtils.json2object(jsonString, req);
		PropertyUtils.copyProperties(this, req);
	}

	/**
	 * Flat tax amount
	 * @return
	 */
	public BigDecimal getAmount() {
		return amount;
	}

	/**
	 * Flat tax amount.
	 * One of amount or rate is required for add/update
	 * @param amount
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Date that this tax rate becomes effective. Expecting "MM/DD/yyyy" in the json string.
	 * Required for adding a new rate, or updating an existing rate
	 * 
	 * @return
	 */
	@RequiredForAdd
	@RequiredForUpdate
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy", timezone="America/Chicago")
	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	/**
	 * Location where this tax rate is applied.
	 * Required for adding a new rate, or updating an existing rate
	 * 
	 * @return
	 */
	@RequiredForAdd
	@RequiredForUpdate
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * Tax rate as a percentage of the billing total
	 * One of amount or rate is required for add/update
	 * @return
	 */
	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	/**
	 * Unique identifier for this tax rate
	 * Required when updating an existing tax rate
	 * @return
	 */
	@RequiredForUpdate
	public Integer getTaxRateId() {
		return taxRateId;
	}

	public void setTaxRateId(Integer taxRateId) {
		this.taxRateId = taxRateId;
	}

	public WebMessages validateAdd() {
		WebMessages webMessages = new WebMessages();
		
		RequestValidator.validateBigDecimal(webMessages, AMOUNT, this.amount, BigDecimal.ZERO, null, true);
		RequestValidator.validateDate(webMessages, EFFECTIVE_DATE, this.effectiveDate, false, null, null);
		RequestValidator.validateString(webMessages, LOCATION, this.location, true);
		RequestValidator.validateBigDecimal(webMessages, RATE, this.rate, BigDecimal.ZERO, null, true);
		
		return webMessages;
	}
	
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = validateAdd();
		RequestValidator.validateId(conn, webMessages, "tax_rate", TaxRate.TAX_RATE_ID, TAX_RATE_ID, this.taxRateId, true);
		return webMessages;
	}

}
