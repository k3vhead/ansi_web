package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	private BigDecimal amount;
	private Date effectiveDate;
	private String location;
	private BigDecimal rate;
	private Integer taxRateId;
	
	public TaxRateRequest() {
		super();
	}
	
	public TaxRateRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
		this();
		TaxRateRequest req = (TaxRateRequest) AppUtils.json2object(jsonString, TaxRateRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
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

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
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
