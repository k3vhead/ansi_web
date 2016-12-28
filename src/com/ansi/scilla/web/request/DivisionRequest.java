package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;
	private String name;
	private Integer parentId;
	private BigDecimal defaultDirectLaborPct;
	
	
	private Integer divisionNbr;
	private String divisionCode;
	private String description;
	private Integer status;
	
	public DivisionRequest() {
		super();
	}
	
	public DivisionRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
		this();
		DivisionRequest req = (DivisionRequest) AppUtils.json2object(jsonString, DivisionRequest.class);
		BeanUtils.copyProperties(this, req);
	}
	
	@RequiredForUpdate
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	@RequiredForAdd
	@RequiredForUpdate
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getDefaultDirectLaborPct(){
		return defaultDirectLaborPct;
	}
	
	public void setDefaultDirectLaborPct(BigDecimal defaultDirectLaborPct){
		this.defaultDirectLaborPct = defaultDirectLaborPct;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
