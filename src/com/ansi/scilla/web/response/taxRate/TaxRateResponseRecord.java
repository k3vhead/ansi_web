package com.ansi.scilla.web.response.taxRate;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.TaxRate;
import com.ansi.scilla.common.jsonFormat.AnsiDateFormatter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TaxRateResponseRecord extends ApplicationObject {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BigDecimal amount;
	private Date effectiveDate;
	private String location;
	private BigDecimal rate;
	private Integer taxRateId;

	public TaxRateResponseRecord(TaxRate taxRate) throws IllegalAccessException, InvocationTargetException {
		super();
		if(taxRate != null) {
			BeanUtils.copyProperties(this, taxRate);
		}
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@JsonSerialize(using = AnsiDateFormatter.class)
	public Date getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(Date effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

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

	public Integer getTaxRateId() {
		return taxRateId;
	}

	public void setTaxRateId(Integer taxRateId) {
		this.taxRateId = taxRateId;
	}
	

}
