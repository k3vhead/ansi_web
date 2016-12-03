package com.ansi.scilla.web.request;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.thewebthing.commons.lang.JsonException;
import com.thewebthing.commons.lang.JsonUtils;

public class DivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private int divisionId;
	private String name;
	private int parentId;
	private float defaultDirectLaborPct;
	
	public DivisionRequest() {
		super();
	}
	
	public DivisionRequest(String jsonString) throws JsonException, IllegalAccessException, InvocationTargetException {
		this();
		DivisionRequest req = (DivisionRequest) JsonUtils.JSON2Object(jsonString, DivisionRequest.class);
		BeanUtils.copyProperties(this, req);
	}

	public int getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(int divisionId) {
		this.divisionId = divisionId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getParentId() {
		return parentId;
	}

	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	
	public float getDefaultDirectLaborPct(){
		return defaultDirectLaborPct;
	}
	
	public void setDefaultDirectLaborPct(float defaultDirectLaborPct){
		this.defaultDirectLaborPct = defaultDirectLaborPct;
	}

}
