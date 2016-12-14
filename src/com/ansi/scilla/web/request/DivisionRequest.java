package com.ansi.scilla.web.request;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.thewebthing.commons.lang.JsonException;
import com.thewebthing.commons.lang.JsonUtils;

public class DivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;
	private String name;
	private Integer parentId;
	private Float defaultDirectLaborPct;
	
	public DivisionRequest() {
		super();
	}
	
	public DivisionRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException {
		this();
		DivisionRequest req = (DivisionRequest) JsonUtils.JSON2Object(jsonString, DivisionRequest.class);
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
