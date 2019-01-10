package com.ansi.scilla.web.claims.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.db.NonDirectLabor;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractCrudServlet;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.JsonFieldFormat;
import com.ansi.scilla.web.common.utils.Permission;
import com.fasterxml.jackson.databind.JsonNode;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class NonDirectLaborServlet extends AbstractCrudServlet {

	private static final long serialVersionUID = 1L;

	private static final String REALM = "nonDirectLabor";
	
	private static List<FieldMap> fieldMap;
	
	static {
		fieldMap = new ArrayList<FieldMap>();
		fieldMap.add(new FieldMap("laborId",NonDirectLabor.LABOR_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap("washerId", NonDirectLabor.WASHER_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap("divisionId", NonDirectLabor.DIVISION_ID, JsonFieldFormat.INTEGER, true));
		fieldMap.add(new FieldMap("workDate", NonDirectLabor.WORK_DATE, JsonFieldFormat.DATE, true));
		fieldMap.add(new FieldMap("hours", NonDirectLabor.HOURS, JsonFieldFormat.DECIMAL, true));
		fieldMap.add(new FieldMap("hoursType", NonDirectLabor.HOURS_TYPE, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap("notes", NonDirectLabor.NOTES, JsonFieldFormat.STRING, true));
		fieldMap.add(new FieldMap("firstName", User.FIRST_NAME, JsonFieldFormat.STRING, false));
		fieldMap.add(new FieldMap("lastName", User.LAST_NAME, JsonFieldFormat.STRING, false));
	}

	
	public NonDirectLaborServlet() {
		super();
		final String displaySql = "select ndl.labor_id, ndl.washer_id, ndl.work_date, ndl.division_id, ndl.hours, ndl.hours_type, ndl.notes,\n" + 
				"	ansi_user.first_name, ansi_user.last_name\n" + 
				"from non_direct_labor ndl\n" + 
				"left outer join ansi_user on ansi_user.user_id=ndl.washer_id";
		
		super.setDisplaySql(displaySql);
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
	protected WebMessages validateAdd(Connection conn, JsonNode addRequest) throws Exception {
		WebMessages webMessages = new WebMessages();
		Integer divisionId = addRequest.get("divisionId").asInt();
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, "divisionId", divisionId, true);
		
		try {
			Division division = new Division();
			division.setDivisionId(addRequest.get("divisionId").asInt());
			division.selectOne(conn);
			logger.log(Level.DEBUG, division);
			RequestValidator.validateInteger(webMessages, "hours", addRequest.get("hours").asInt(), true, 0, division.getMaxRegHrsPerDay());;
		} catch ( RecordNotFoundException e) {
			// this would have been caught with the divisionid validation
			logger.log(Level.DEBUG, "Hours validation was skipped b/c of Record Not Found");
		}
		
		String workDateText = addRequest.get("workDate").asText();
		Date workDate = StringUtils.isBlank(workDateText) ? null : standardDateFormat.parse(workDateText);
		
		RequestValidator.validateDate(webMessages, "washerId", workDate, true, null, null);
		RequestValidator.validateWorkHoursType(webMessages, "hoursType", addRequest.get("hoursType").asText(), true);
		RequestValidator.validateString(webMessages, "notes", addRequest.get("notes").asText(), false);
		RequestValidator.validateWasherId(conn, webMessages, "washerId", addRequest.get("washerId").asInt(), true);
		
		return webMessages;
	}


	@Override
	protected WebMessages validateUpdate(Connection conn, JsonNode updateRequest) throws Exception {
		WebMessages webMessages = validateAdd(conn, updateRequest);
		return webMessages;
	}


	
	
	
	
	
	
}
