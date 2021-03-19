package com.ansi.scilla.web.claims.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.claims.WorkHoursType;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.NonDirectLabor;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.fasterxml.jackson.databind.JsonNode;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class NonDirectLaborServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;

	private static final String REALM = "nonDirectLabor";
	
	private static final String ACT_PAYOUT_AMT = "actPayoutAmt";
	private static final String LABOR_ID = "laborId";
	private static final String WASHER_ID = "washerId";
	private static final String DIVISION_ID =  "divisionId";
	private static final String WORK_DATE =  "workDate";
	private static final String HOURS =  "hours";
	private static final String HOURS_TYPE =  "hoursType";
	private static final String NOTES =  "notes";
	
	
	private static List<FieldMap> fieldMap;
		
	static {
		fieldMap = new ArrayList<FieldMap>();
		fieldMap.add(new FieldMap(LABOR_ID,NonDirectLabor.LABOR_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap(WASHER_ID, NonDirectLabor.WASHER_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap(DIVISION_ID, NonDirectLabor.DIVISION_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap(WORK_DATE, NonDirectLabor.WORK_DATE, JsonFieldFormat.DATE, true));
		fieldMap.add(new FieldMap(HOURS, NonDirectLabor.HOURS, JsonFieldFormat.DECIMAL, true));
		fieldMap.add(new FieldMap(HOURS_TYPE, NonDirectLabor.HOURS_TYPE, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap(NOTES, NonDirectLabor.NOTES, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap(ACT_PAYOUT_AMT, NonDirectLabor.ACT_PAYOUT_AMT, JsonFieldFormat.DECIMAL, true));
		fieldMap.add(new FieldMap("firstName", User.FIRST_NAME, JsonFieldFormat.STRING, false));
		fieldMap.add(new FieldMap("lastName", User.LAST_NAME, JsonFieldFormat.STRING, false));
	}

	
	public NonDirectLaborServlet() {
		super();
		try {
			final String displaySql = "select ndl.labor_id, ndl.washer_id, ndl.work_date, ndl.division_id, ndl.hours, "
					+ " ndl.hours_type, ndl.notes, ndl.act_payout_amt, ndl.calc_payout_amt, \n"
					+ "	ansi_user.first_name, ansi_user.last_name\n"
					+ "from non_direct_labor ndl\n"
					+ "left outer join ansi_user on ansi_user.user_id=ndl.washer_id";
			
			super.setDisplaySql(displaySql);
			Method overrideAdd = NonDirectLaborServlet.class.getMethod("overrideAdd", new Class[] {Connection.class, HttpServletResponse.class, SessionUser.class, JsonNode.class} );
			super.setOverrideAdd(overrideAdd);
		} catch ( Exception e) {
			throw new RuntimeException(e);
		}
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		processGet(
				request, 
				response, 
				Permission.CLAIMS_READ, 
				REALM, 
				new String[] {ACTION_IS_LIST}, 
				new NonDirectLabor(),
				fieldMap
				);
	}


	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processDelete(
				request, 
				response, 
				Permission.CLAIMS_WRITE, 
				REALM, 
				new String[] {ACTION_IS_ADD}, 
				new NonDirectLabor(),
				fieldMap
				);
		}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processPost(
				request, 
				response, 
				Permission.CLAIMS_WRITE, 
				REALM, 
				new String[] {ACTION_IS_ADD}, 
				new NonDirectLabor(),
				fieldMap
				);
	}


	@Override
	protected WebMessages validateAdd(Connection conn, HashMap<String, Object> addRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		Integer divisionId = (Integer)addRequest.get("divisionId");
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, "divisionId", divisionId, true);
		
		try {
			Division division = new Division();
			logger.log(Level.DEBUG, "Getting division");
			division.setDivisionId(divisionId);
			division.selectOne(conn);
			logger.log(Level.DEBUG, division);
			Double maxRegHrsPerDay = division.getMaxRegHrsPerDay() == null ? 24.0D : division.getMaxRegHrsPerDay() * 1.0D;  // make java 11 happy because they deprecated all the good constructors
			RequestValidator.validateNumber(webMessages, "hours", addRequest.get("hours"), 0.0D, maxRegHrsPerDay, true);
		} catch ( RecordNotFoundException e) {
			// this would have been caught with the divisionid validation
			logger.log(Level.DEBUG, "Hours validation was skipped b/c of Record Not Found");
		}
		
		RequestValidator.validateDate(webMessages, WORK_DATE,(String)addRequest.get(WORK_DATE), standardDateFormat, true, null, null);		
		RequestValidator.validateWorkHoursType(webMessages, HOURS_TYPE, (String)addRequest.get(HOURS_TYPE), true);
		RequestValidator.validateString(webMessages, NOTES, (String)addRequest.get("notes"), false);
		RequestValidator.validateWasherId(conn, webMessages, WASHER_ID, (Integer)addRequest.get(WASHER_ID), true);
		
		if ( webMessages.isEmpty() ) {
			// we can only check the pay amount if the other fields are valid
			WorkHoursType workType = WorkHoursType.valueOf((String)addRequest.get(HOURS_TYPE));
			Integer workerId = (Integer)addRequest.get(WASHER_ID);
			Double hours = (Double)addRequest.get(HOURS);
			Date workDate = standardDateFormat.parse((String)addRequest.get(WORK_DATE));
			Double minimumPay = workType.calculateDefaultPay(conn, divisionId, workerId, workDate, hours);
			RequestValidator.validateNumber(webMessages, ACT_PAYOUT_AMT, addRequest.get(ACT_PAYOUT_AMT), minimumPay, null, true);
		}
		
		return webMessages;
	}


	@Override
	protected WebMessages validateUpdate(Connection conn, HashMap<String, Object> updateRequest) throws Exception {
		WebMessages webMessages = validateAdd(conn, updateRequest);
		return webMessages;
	}


	public static Integer overrideAdd(Connection conn, HttpServletResponse response, SessionUser sessionUser, JsonNode jsonNode) throws Exception {
		Date today = new Date();
		String dateString = jsonNode.get(WORK_DATE).asText();
		java.util.Date workDate = standardDateFormat.parse(dateString);
		Integer divisionId = jsonNode.get("divisionId").asInt();
		Integer washerId = jsonNode.get(WASHER_ID).asInt();
		Double workHours = jsonNode.get(HOURS).asDouble();		
		
		String workHoursString = jsonNode.get(HOURS_TYPE).asText();
		WorkHoursType workHoursType = WorkHoursType.valueOf(workHoursString);
		Double calcPayoutAmt = workHoursType.calculateDefaultPay(conn, divisionId, washerId, workDate, workHours);
		
		NonDirectLabor nonDirectLabor = new NonDirectLabor();
		nonDirectLabor.setActPayoutAmt(new BigDecimal(jsonNode.get(ACT_PAYOUT_AMT).asDouble()));
		nonDirectLabor.setAddedBy(sessionUser.getUserId());
		nonDirectLabor.setCalcPayoutAmt(new BigDecimal(calcPayoutAmt));
		nonDirectLabor.setEntryDate(today);
		nonDirectLabor.setHours(new BigDecimal(workHours));
		nonDirectLabor.setHoursType(workHoursString);
		nonDirectLabor.setNotes(jsonNode.get(NOTES).asText());
		nonDirectLabor.setUpdatedBy(sessionUser.getUserId());
		nonDirectLabor.setWasherId(washerId);
		nonDirectLabor.setWorkDate(workDate);
		nonDirectLabor.setDivisionId(divisionId);
		Integer laborId = nonDirectLabor.insertWithKey(conn);
		
		conn.commit();
		
		return laborId;
		
		
	}




	
	
	
	
	
	
}
