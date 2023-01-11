package com.ansi.scilla.web.division.request;

import java.math.BigDecimal;
import java.sql.Connection;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.request.RequiredForAdd;
import com.ansi.scilla.web.common.request.RequiredForUpdate;
import com.ansi.scilla.web.common.request.RequiredFormat;
import com.ansi.scilla.web.common.response.WebMessages;

/**
 * 
 * @author jwlewis
 *
 *
 *
 */

public class DivisionRequest extends AbstractRequest {

	public static final String MIN_HOURLY_RATE = "minHourlyRate";
	
	
	private static final long serialVersionUID = 1L;
	
	private Integer divisionId;	//Integer Division ID
	private Integer parentId;	//Integer Parent ID
	private BigDecimal defaultDirectLaborPct;	//Float Automatic Direct Labor Percentage
	private Integer divisionNbr;	//Integer Division Number
	private String divisionCode;	//String Division Code
	private String description;		//String Division Description
	private Integer status;			//Integer Division Status: 0-inactive, 1-active
	private Integer maxRegHrsPerDay;
    private Integer maxRegHrsPerWeek;
    private BigDecimal overtimeRate;
    private Integer weekendIsOt;
    private Integer hourlyRateIsFixed;
    private BigDecimal minHourlyRate;
	
	public DivisionRequest() {
		super();
	}
	
//	public DivisionRequest(String jsonString) throws IllegalAccessException, InvocationTargetException, JsonParseException, JsonMappingException, IOException {
//		this();
//		DivisionRequest req = (DivisionRequest) AppUtils.json2object(jsonString, DivisionRequest.class);
//		PropertyUtils.copyProperties(this, req);
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
	@RequiredFormat("^.{1,4}$")
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

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getMaxRegHrsPerDay() {
		return maxRegHrsPerDay;
	}

	public void setMaxRegHrsPerDay(Integer maxRegHrsPerDay) {
		this.maxRegHrsPerDay = maxRegHrsPerDay;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getMaxRegHrsPerWeek() {
		return maxRegHrsPerWeek;
	}

	public void setMaxRegHrsPerWeek(Integer maxRegHrsPerWeek) {
		this.maxRegHrsPerWeek = maxRegHrsPerWeek;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getOvertimeRate() {
		return overtimeRate;
	}

	public void setOvertimeRate(BigDecimal overtimeRate) {
		this.overtimeRate = overtimeRate;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getWeekendIsOt() {
		return weekendIsOt;
	}

	public void setWeekendIsOt(Integer weekendIsOt) {
		this.weekendIsOt = weekendIsOt;
	}

	@RequiredForAdd
	@RequiredForUpdate
	public Integer getHourlyRateIsFixed() {
		return hourlyRateIsFixed;
	}

	public void setHourlyRateIsFixed(Integer hourlyRateIsFixed) {
		this.hourlyRateIsFixed = hourlyRateIsFixed;
	}	

	@RequiredForAdd
	@RequiredForUpdate
	public BigDecimal getMinHourlyRate() {
		return minHourlyRate;
	}

	public void setMinHourlyRate(BigDecimal minHourlyRate) {
		this.minHourlyRate = minHourlyRate;
	}

	public WebMessages validateAdd() {
		WebMessages webMessages = new WebMessages();
		RequestValidator.validateBoolean(webMessages, "weekendIsOt", this.weekendIsOt, true);
		RequestValidator.validateBoolean(webMessages, "hourlyRateIsFixed", this.hourlyRateIsFixed, true);
		RequestValidator.validateInteger(webMessages, "maxRegHrsPerDay", this.maxRegHrsPerDay, 0, 24, true);
		RequestValidator.validateInteger(webMessages, "maxRegHrsPerWeek", this.maxRegHrsPerWeek, 0, 24*7, true);
		RequestValidator.validateBigDecimal(webMessages, MIN_HOURLY_RATE, this.minHourlyRate, BigDecimal.ZERO, null, true);
		
		return webMessages;		
	}
	
	public WebMessages validateUpdate(Connection conn) throws Exception {
		WebMessages webMessages = validateAdd();
		RequestValidator.validateId(conn, webMessages, "division", Division.DIVISION_ID, "divisionId", this.divisionId, true);		
		return webMessages;		
	}
}
