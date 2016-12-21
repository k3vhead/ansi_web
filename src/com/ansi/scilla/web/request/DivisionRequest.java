package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public class DivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;
	private String name;
	private Integer parentId;
	private Float defaultDirectLaborPct;
	
	public DivisionRequest() {
		super();
	}
	
	public DivisionRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
		this();
		DivisionRequest req = (DivisionRequest) AppUtils.json2object(jsonString, DivisionRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

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
	
	public Float getDefaultDirectLaborPct(){
		return defaultDirectLaborPct;
	}
	
	public void setDefaultDirectLaborPct(Float defaultDirectLaborPct){
		this.defaultDirectLaborPct = defaultDirectLaborPct;
	}

}
