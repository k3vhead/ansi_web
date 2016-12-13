package com.ansi.scilla.web.request;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.thewebthing.commons.lang.JsonException;
import com.thewebthing.commons.lang.JsonUtils;

/**
 * Used to request data from the TaxRate table
 * 
 * @author gagroce
 *
 */
public class TaxRateRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Float amount;
	private Date effectiveDate;
	private String location;
	private Float rate;
	private Integer taxCodeId;
	
	public TaxRateRequest() {
		super();
	}
	
	public TaxRateRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException {
		this();
		TaxRateRequest req = (TaxRateRequest) JsonUtils.JSON2Object(jsonString, TaxRateRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public Float getAmount() {
		return amount;
	}

	public void setAmount(Float amount) {
		this.amount = amount;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Float getRate() {
		return rate;
	}

	public void setRate(Float rate) {
		this.rate = rate;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getTaxRateId() {
		return taxRateId;
	}

	public void setTaxRateId(Integer taxRateId) {
		this.taxRateId = taxRateId;
	}

}
