package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.utils.PropertyNames;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.ticket.response.TicketReturnResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketOverrideServlet extends TicketServlet {

	private static final long serialVersionUID = 1L;
	
	private final String REALM = "ticketOverride";
	
	public static final String FIELDNAME_START_DATE = "startDate";
	public static final String FIELDNAME_TYPE = "type";
	public static final String FIELDNAME_OVERRIDE = "override";
	public static final String FIELDNAME_PROCESS_DATE = "processDate";
	public static final String FIELDNAME_PROCESS_NOTE = "processNote";
	
	private final String MESSAGE_SUCCESS = "Success";
	private final String MESSAGE_INVALID_FORMAT = "Invalid Format";
	private final String MESSAGE_INVALID_OVERRIDE = "Invalid Override";
	private final String MESSAGE_NOT_PROCESSED = "Not Processed";
	private final String MESSAGE_INVALID_DATE = "Invalid Date";
	private final String MESSAGE_MISSING_START_DATE = "Missing required value: start date";
	private final String MESSAGE_MISSING_PROCESS_NOTE = "Missing required values: process date/process notes";
	
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processGetRequest(request, response, "ticketOverride");
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		AnsiURL ansiURL = null; 
		Connection conn = null;
		//TicketReturnResponse ticketReturnResponse = null;
		try {
			conn = AppUtils.getDBCPConn();
			String jsonString = super.makeJsonString(request);
			System.out.println("jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET, PermissionLevel.PERMISSION_LEVEL_IS_WRITE);

			Ticket ticket = new Ticket();
			try{
				ansiURL = new AnsiURL(request, REALM, (String[])null); //  .../ticket/etc

				SessionUser sessionUser = sessionData.getUser(); 
				ticket.setTicketId(ansiURL.getId());
				ticket.selectOne(conn);
				
				OverrideResult result = processUpdateRequest(conn, ticket, jsonString, sessionUser);
				WebMessages messages = new WebMessages();
				messages.addMessage(WebMessages.GLOBAL_MESSAGE, result.message);

				if ( result.success == true ) {
					TicketReturnResponse data = new TicketReturnResponse(conn, ansiURL.getId());
					data.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, data);
				} else {
					TicketReturnResponse data = new TicketReturnResponse();
					data.setWebMessages(messages);
					super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
				}
			}  catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				TicketReturnResponse data = new TicketReturnResponse();
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, MESSAGE_INVALID_FORMAT);
				data.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, data);
			} catch (RecordNotFoundException e) {
				//send a Bad Ticket message back
				super.sendNotFound(response);
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	private OverrideResult processUpdateRequest(Connection conn, Ticket ticket, String jsonString, SessionUser sessionUser) throws Exception {
		HashMap<String,String> values = makeValues(jsonString);
		OverrideResult result = new OverrideResult(false, MESSAGE_NOT_PROCESSED, ticket);
		OverrideType overrideType = OverrideType.lookup(values.get(FIELDNAME_TYPE));
		if ( overrideType == null ) {			
			result = new OverrideResult(false, MESSAGE_INVALID_OVERRIDE + ": " + values.get(FIELDNAME_TYPE), ticket);
		} else {
			String processor = overrideType.processor();
			Method method = this.getClass().getMethod(processor, new Class[] { Ticket.class, HashMap.class});
			
			result = (OverrideResult)method.invoke(this, new Object[]{ticket, values});
			if ( result.success == true ) {
				Ticket key = new Ticket();
				key.setTicketId(ticket.getTicketId());
				result.ticket.update(conn, key);
				conn.commit();
			}
		}		
		
		return result;
	}

	
	private HashMap<String, String> makeValues(String json) throws JsonProcessingException, IOException {
		HashMap<String, String> values = new HashMap<String, String>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(json);
		JsonNode typeNode = rootNode.get(FIELDNAME_TYPE);
		values.put(FIELDNAME_TYPE, typeNode.asText());
		JsonNode overrideNode = rootNode.get(FIELDNAME_OVERRIDE);
		
		for ( Iterator<JsonNode> nodeIterator = overrideNode.iterator(); nodeIterator.hasNext(); ) {
			JsonNode node = nodeIterator.next();
			
			for ( Iterator<String> fieldIterator = node.fieldNames(); fieldIterator.hasNext(); ) {
				String fieldName = fieldIterator.next();				
				JsonNode valueNode = node.get(fieldName);
				String value = valueNode.asText(); // use asText instead of textValue so we get numbers and booleans, too
				values.put(fieldName, value);
			}
		}
		
		
		return values;
	}
	
	public OverrideResult doStartDate(Ticket ticket, HashMap<String, String> values) {
		Boolean success = null;
		String message = null;
		if ( values.containsKey(FIELDNAME_START_DATE)) {
			String commonDateFormat = AppUtils.getProperty(PropertyNames.COMMON_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat(commonDateFormat);
			try {
				Date newStartDate = sdf.parse(values.get(FIELDNAME_START_DATE));
				Calendar start = Calendar.getInstance(new AnsiTime());
				start.setTime(newStartDate);
				System.out.println(newStartDate);
				ticket.setStartDate(start.getTime()); // do it this way for timezone weirdness
				success = true;
				message = MESSAGE_SUCCESS;
			} catch (ParseException e) {
				success = false;
				message = MESSAGE_INVALID_DATE;
			}
		} else {
			success = false;
			message = MESSAGE_MISSING_START_DATE;
		}
		return new OverrideResult(success, message, ticket);
	}
	
	public OverrideResult doProcessDate(Ticket ticket, HashMap<String, String> values) {
		Boolean success = null;
		String message = null;
		if ( values.containsKey(FIELDNAME_PROCESS_DATE) && values.containsKey(FIELDNAME_PROCESS_NOTE)) {
			String commonDateFormat = AppUtils.getProperty(PropertyNames.COMMON_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat(commonDateFormat);
			try {
				Date newStartDate = sdf.parse(values.get(FIELDNAME_PROCESS_DATE));
				Calendar date = Calendar.getInstance(new AnsiTime());
				date.setTime(newStartDate);
				ticket.setProcessDate(date.getTime()); // do it this way for timezone weirdness
				ticket.setProcessNotes(values.get(FIELDNAME_PROCESS_NOTE));
				success = true;
				message = MESSAGE_SUCCESS;
			} catch (ParseException e) {
				success = false;
				message = MESSAGE_INVALID_DATE;
			}
		} else {
			success = false;
			message = MESSAGE_MISSING_PROCESS_NOTE;
		}
		return new OverrideResult(success, message, ticket);
	}
	
	public OverrideResult doInvoice(Ticket ticket, HashMap<String, String> values) {
		System.out.println("processing invoice");
		Boolean success = false;
		String message = MESSAGE_INVALID_OVERRIDE;
		return new OverrideResult(success, message, ticket);
	}
	
	/**
	 * id - the string identifying what kind of override we're doing
	 * processor - the method in the servlet that process the request. Must have signature: methodName(Integer ticketId, HashMap<String, String> values)
	 * 
	 * @author dclewis
	 *
	 */
	public enum OverrideType {
		START_DATE("startDate", "doStartDate"),
		PROCESS_DATE("processDate", "doProcessDate"),
		INVOICE("invoice", "doInvoice");
		
		private final String id;
		private final String processor;
		private static final Map<String, OverrideType> lookup = new HashMap<String, OverrideType>();
		
		static {
			for ( OverrideType type : OverrideType.values()) {
				lookup.put(type.id(), type);
			}
		}
		
		OverrideType(String id, String processor) {
			this.id = id;
			this.processor = processor;
		}
		
		public String id() { return id; }
		public String processor() { return processor; }
		public static OverrideType lookup(String id) { return lookup.get(id); }
	}
	
	public class OverrideResult extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public Boolean success;
		public String message;
		public Ticket ticket;
		public OverrideResult(Boolean success, String message, Ticket ticket) {
			super();
			this.success = success;
			this.message = message;
			this.ticket = ticket;
		}
	}

}
