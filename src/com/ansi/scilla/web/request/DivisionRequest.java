package com.ansi.scilla.web.request;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
//import java.util.Date;

import org.apache.commons.beanutils.BeanUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author jwlewis
 *
 *
 *
 */

public class DivisionRequest extends AbstractRequest {

	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;	//Integer Division ID
	private Integer parentId;	//Integer Parent ID
	private BigDecimal defaultDirectLaborPct;	//Float Automatic Direct Labor Percentage
	private Integer divisionNbr;	//Integer Division Number
	private String divisionCode;	//String Division Code
	private String description;		//String Division Description
	private Integer status;			//Integer Division Status: 0-inactive, 1-active
	
	public DivisionRequest() {
		super();
	}
	
//	public DivisionRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
//		this();
//		DivisionRequest req = (DivisionRequest) AppUtils.json2object(jsonString, DivisionRequest.class);
//		BeanUtils.copyProperties(this, req);
//	}
	
	@RequiredForUpdate
	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}
	
	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}
	
	@RequiredForAdd
	@RequiredForUpdate
	@RequiredFormat("")
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
		if (status == 0 || status == 1){
			this.status = status;
		} else {
			throw new RuntimeException("Invalid Status");
		}
	}

}
