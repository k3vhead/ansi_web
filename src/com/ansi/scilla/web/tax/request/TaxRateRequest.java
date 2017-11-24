package com.ansi.scilla.web.tax.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * Used to request data from the TaxRate table<br />
 * <br />
 * To get a list of all taxRates:<br />
 * <br />
 * 	Request object is not used (GET /taxRate/list)<br />
 *  A TaxRateListResponse is returned<br />
 *   <br />
 * To get a particluar tax Rate:<br />
 * 	Request object is not used (GET /taxRate/&lt;taxRateId&gt;)<br />
 *  A TaxRateListResponse is returned<br />
 *  <br />
 * To add a new tax Rate:<br />
 *  POST /taxRate/add with POSTED json string like:<br />
 *  	{"location":"Webthing HQ","rate":123.45,"taxAmount":0.03,"effectiveDate":"12/01/2016"}<br />
 *  A TaxRateResponse is returned<br />
 *  <br />
 * To upate an existing tax Rate:<br />
 * 	POST /taxRate/&lt;taxRateId&gt; with POSTED json string like:<br />
 * 		{"taxRateId":123,"location":"Webthing HQ","rate":123.45,"taxAmount":0.03,"effectiveDate":"12/01/2016"}<br />
 *  A TaxRateResponse is returned<br />
 * <br />
 * @author gagroce
 *
 */
public class TaxRateRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private BigDecimal amount;
	private Date effectiveDate;
	private String location;
	private BigDecimal rate;
	private Integer taxRateId;
	
	public TaxRateRequest() {
		super();
	}
	
	/**
	 * Generate a TaxRateRequest from expected JSON string as defined above<br />
	 * Exceptions are thrown when the JSON string does not match expectations<br />
	 * <br />
	 * @param jsonString
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws NoSuchMethodException 
	 */
	public TaxRateRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException, NoSuchMethodException {
		this();
		TaxRateRequest req = (TaxRateRequest) AppUtils.json2object(jsonString, TaxRateRequest.class);
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
	 * One of amount or rate is required for add/update<br />
	 * @param amount
	 */
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/**
	 * Date that this tax rate becomes effective. Expecting "MM/DD/yyyy" in the json string.
	 * Required for adding a new rate, or updating an existing rate
	 * <br />
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

}
