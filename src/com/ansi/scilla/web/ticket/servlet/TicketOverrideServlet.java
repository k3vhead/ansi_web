package com.ansi.scilla.web.ticket.servlet;

import java.io.IOException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Address;
import com.ansi.scilla.common.db.Invoice;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.common.jobticket.TicketUtils;
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
	public static final String FIELDNAME_INVOICE_ID = "invoiceId";
	public static final String FIELDNAME_INVOICE_DATE = "invoiceDate";
	public static final String FIELDNAME_ACT_PRICE_PER_CLEANING = "actPricePerCleaning";
	public static final String FIELDNAME_ACT_PO_NUMBER = "actPoNumber";
	
	private final String MESSAGE_SUCCESS = "Success";
	private final String MESSAGE_NOT_PROCESSED = "Not Processed";
	private final String MESSAGE_INSUFFICIENT_PERMISSION = "You do not have permission to do this";
	private final String MESSAGE_BILLTO_MISMATCH = "New Invoice does not have the same bill-to";
	private final String MESSAGE_INVALID_DATE = "Invalid Date";
	private final String MESSAGE_INVALID_INVOICE_ID = "Invalid Invoice ID";
	private final String MESSAGE_INVALID_FORMAT = "Invalid Format";
	private final String MESSAGE_INVALID_OVERRIDE = "Invalid Override";
	private final String MESSAGE_MISSING_INVOICE_ID = "Missing required value: Invoice ID";
	private final String MESSAGE_MISSING_INVOICE_DATE = "Missing required value: Invoice Date";
	private final String MESSAGE_MISSING_PROCESS_NOTE = "Missing required values: process date/process notes";
	private final String MESSAGE_MISSING_START_DATE = "Missing required value: start date";
	private final String MESSAGE_MISSING_VALUE_PPC = "Missing required value: Actual Price Per Cleaning";
	private final String MESSAGE_MISSING_VALUE_PO = "Missing required value: PO Number";
	
	
	
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
			conn.setAutoCommit(false);
			String jsonString = super.makeJsonString(request);
			logger.log(Level.DEBUG, "jsonstring:"+jsonString);

			SessionData sessionData = AppUtils.validateSession(request, Permission.TICKET_OVERRIDE);
			Ticket ticket = new Ticket();
			try{
				ansiURL = new AnsiURL(request, REALM, (String[])null); //  .../ticket/etc

				ticket.setTicketId(ansiURL.getId());
				ticket.selectOne(conn);
				
				OverrideResult result = processUpdateRequest(conn, ticket, jsonString, sessionData);
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

	private OverrideResult processUpdateRequest(Connection conn, Ticket ticket, String jsonString, SessionData sessionData) throws Exception {
		SessionUser sessionUser = sessionData.getUser(); 

		HashMap<String,String> values = makeValues(jsonString);
		OverrideResult result = new OverrideResult(false, MESSAGE_NOT_PROCESSED, ticket);
		OverrideType overrideType = OverrideType.lookup(values.get(FIELDNAME_TYPE));
		if ( overrideType == null ) {			
			result = new OverrideResult(false, MESSAGE_INVALID_OVERRIDE + ": " + values.get(FIELDNAME_TYPE), ticket);
		} else {
			if ( sessionData.hasPermission(overrideType.permission().name())) {
				 
				String processor = overrideType.processor();
				Method method = this.getClass().getMethod(processor, new Class[] { Connection.class, Ticket.class, HashMap.class, SessionUser.class });
				
				result = (OverrideResult)method.invoke(this, new Object[]{conn, ticket, values, sessionUser});
				if ( result.success == true ) {
					// In some cases (eg New Invoice), ticket update is done in the process method
					// we don't want to update the ticket twice and confuse things.
					// But, the process worked so we commit the DB transaction
					if ( result.doTicketUpdate) {
						Ticket key = new Ticket();
						key.setTicketId(ticket.getTicketId());
						Logger logger = AppUtils.getLogger();
						logger.debug(ticket);
						result.ticket.update(conn, key);
					}
					conn.commit();
				} else {
					conn.rollback();
				}
			} else {
				Boolean success = false;
				Boolean doUpdate = false;
				result = new OverrideResult(success, MESSAGE_INSUFFICIENT_PERMISSION, ticket, doUpdate);
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
	
	public OverrideResult doStartDate(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) {
		Boolean success = null;
		String message = null;
		if ( values.containsKey(FIELDNAME_START_DATE)) {
			String commonDateFormat = AppUtils.getProperty(PropertyNames.COMMON_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat(commonDateFormat);
			try {
				Date newStartDate = sdf.parse(values.get(FIELDNAME_START_DATE));
				Calendar start = Calendar.getInstance(new AnsiTime());
				start.setTime(newStartDate);
				logger.log(Level.DEBUG, newStartDate);
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
	
	public OverrideResult doProcessDate(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) {
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
	
	public OverrideResult doInvoice(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "processing invoice");
		Boolean success = null;
		String message = null;
		if ( values.containsKey(FIELDNAME_INVOICE_ID) ) {
			Integer newInvoiceId = Integer.valueOf(values.get(FIELDNAME_INVOICE_ID));
			if ( isSameBillTo(conn, ticket.getTicketId(), newInvoiceId) ) {
				try {				
					logger.log(Level.DEBUG, "TicketOverrideServlet 233: ");
					ticket.setInvoiceId(newInvoiceId);
					success = true;
					message = MESSAGE_SUCCESS;
				} catch (Exception e) {
					logger.log(Level.DEBUG, "TicketOverrideServlet 237: ");
					success = false;
					message = MESSAGE_INVALID_INVOICE_ID;
				}
			} else {
				logger.log(Level.DEBUG, "TicketOverrideServlet 241: ");
				success = false;
				message = MESSAGE_BILLTO_MISMATCH;
			}
		} else {
			success = false;
			message = MESSAGE_MISSING_INVOICE_ID;
		}
		return new OverrideResult(success, message, ticket);
	}
	
	
	/**
	 * Ticket Override to create a new invoice for this ticket
	 * @param conn
	 * @param ticket
	 * @param values
	 * @return
	 * @throws Exception
	 */
	public OverrideResult doNewInvoice(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "processing new invoice");
		Boolean success = null;
		String message = null;
		
		if ( values.containsKey(FIELDNAME_INVOICE_DATE) ) {
			String dateForamt = AppUtils.getProperty(PropertyNames.COMMON_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat(dateForamt);
			Date date = sdf.parse(values.get(FIELDNAME_INVOICE_DATE));
			Calendar invoiceDate = Calendar.getInstance(new AnsiTime());
			invoiceDate.setTime(date);
			Integer invoiceId = InvoiceUtils.generateInvoiceForTicket(conn, invoiceDate, ticket.getTicketId(), sessionUser.getUserId());
			ticket.setInvoiceDate(date);
			ticket.setInvoiceId(invoiceId);		
			success = true;
			message = MESSAGE_SUCCESS;
		} else {
			success = false;
			message = MESSAGE_MISSING_INVOICE_DATE;
		}
		return new OverrideResult(success, message, ticket, false);
	}
	
	
	public OverrideResult doInvoiceDate(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "processing invoice date");
		Boolean success = null;
		String message = null;
		

		if ( values.containsKey(FIELDNAME_INVOICE_DATE) ) {
			String dateFormat = AppUtils.getProperty(PropertyNames.COMMON_DATE_FORMAT);
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
			Date date = sdf.parse(values.get(FIELDNAME_INVOICE_DATE));
			Calendar invoiceDate = Calendar.getInstance(new AnsiTime());
			invoiceDate.setTime(date);
			ticket.setInvoiceDate(date);
			success = true;
			message = MESSAGE_SUCCESS;
		} else {
			success = false;
			message = MESSAGE_MISSING_INVOICE_DATE;
		}
		return new OverrideResult(success, message, ticket, true);
	}
	
	
	
	public OverrideResult doPricePerCleaning(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "processing PPC");
		Boolean success = null;
		String message = null;
		

		if ( values.containsKey(FIELDNAME_ACT_PRICE_PER_CLEANING) ) {
			try {
				String value = values.get(FIELDNAME_ACT_PRICE_PER_CLEANING);
				Double actPricePerCleaning = Double.valueOf(value);
				ticket.setActPricePerCleaning(new BigDecimal(actPricePerCleaning));
				Double actDlAmt = actPricePerCleaning * ticket.getActDlPct().doubleValue() / 100;
				ticket.setActDlAmt( new BigDecimal(actDlAmt) );
				Job job = new Job();
				job.setJobId(ticket.getJobId());
				job.selectOne(conn);
				if (job.getTaxExempt().equals( Job.TAX_EXEMPT_IS_NO)) {
					BigDecimal taxRate = null;
					if ( ticket.getProcessDate() == null ) {
						taxRate = JobUtils.getTaxRate( conn, ticket.getJobId(), ticket.getStartDate(), sessionUser.getUserId());
					} else {
						taxRate = JobUtils.getTaxRate( conn, ticket.getJobId(), ticket.getProcessDate(), sessionUser.getUserId());
					}
					ticket.setActTaxAmt(ticket.getActPricePerCleaning().multiply(taxRate));
//					ticket.setActTaxRateId(taxRate.getTaxRateId());
					ticket.setActTaxRate(taxRate);
				} else {
					ticket.setActTaxAmt(new BigDecimal( "0.00" ));
//					ticket.setActTaxRateId(0);
					ticket.setActTaxRate(BigDecimal.ZERO);
				}
				success = true;
				message = MESSAGE_SUCCESS;
			} catch ( NumberFormatException e ) {
				success = false;
				message = MESSAGE_INVALID_FORMAT + ": Must be numeric";
			}
		} else {
			success = false;
			message = MESSAGE_MISSING_VALUE_PPC;
		}
		return new OverrideResult(success, message, ticket, true);
	}
	
	
	
	
	public OverrideResult doPoNumber(Connection conn, Ticket ticket, HashMap<String, String> values, SessionUser sessionUser) throws Exception {
		logger.log(Level.DEBUG, "processing PO Number");
		Boolean success = null;
		String message = null;
		

		if ( values.containsKey(FIELDNAME_ACT_PO_NUMBER) ) {
			String value = values.get(FIELDNAME_ACT_PO_NUMBER);
			ticket.setActPoNumber(value);
			success = true;
			message = MESSAGE_SUCCESS;
		} else {
			success = false;
			message = MESSAGE_MISSING_VALUE_PO;
		}
		return new OverrideResult(success, message, ticket, true);
	}
	
	
	private boolean isSameBillTo(Connection conn, Integer ticketId, Integer newInvoiceId) throws Exception {
		try {
			Address oldBillTo = TicketUtils.getBillToForTicket(conn, ticketId);
			List<Invoice> invoiceList = InvoiceUtils.getInvoicesForBillTo(conn, oldBillTo.getAddressId());
			boolean isSameBillTo = false;
			for ( Invoice invoice : invoiceList ) {
				if ( invoice.getInvoiceId().equals(newInvoiceId)) {
					isSameBillTo = true;
				}
			}
			logger.log(Level.DEBUG, "TicketOverrideServlet 255: " + isSameBillTo);
			return isSameBillTo;			
		} catch ( RecordNotFoundException e ) {
			logger.log(Level.DEBUG, "TicketOverrideServlet 257: ");
			return false;
		}
	}

	/**
	 * This enum acts as the traffic cop for valid ticket overrides. If the type isn't here, then it itsn't valid. It
	 * also determines the method that is called (via reflection, that's why everything is "public") to do the actual
	 * ticket update.
	 * 
	 * id - the string identifying what kind of override we're doing
	 * processor - the method in the servlet that process the request. Must have signature: methodName(Integer ticketId, HashMap<String, String> values)
	 * permission - any permission required beyond the TICKET that is minimum required to do this update. (Yes, we're checking
	 * 		for "ticket" twice, but it's at minimal cost, and it makes the code easier)
	 * 
	 * @author dclewis
	 *
	 */
	public enum OverrideType {
		START_DATE("startDate", "doStartDate", Permission.TICKET),
		PROCESS_DATE("processDate", "doProcessDate", Permission.TICKET),
		INVOICE("invoice", "doInvoice", Permission.TICKET),
		NEW_INVOICE("newInvoice","doNewInvoice", Permission.TICKET),
//		INVOICE_DATE("invoiceDate","doInvoiceDate", Permission.TICKET_SPECIAL_OVERRIDE),
//		ACT_PRICE_PER_CLEANING("actPricePerCleaning","doPricePerCleaning", Permission.TICKET_SPECIAL_OVERRIDE),
		INVOICE_DATE("invoiceDate","doInvoiceDate", Permission.TICKET_OVERRIDE),
		ACT_PRICE_PER_CLEANING("actPricePerCleaning","doPricePerCleaning", Permission.TICKET_OVERRIDE),
		ACT_PO_NUMBER("actPoNumber","doPoNumber", Permission.TICKET),
		;
		
		private final String id;
		private final String processor;
		private final Permission permission;
		private static final Map<String, OverrideType> lookup = new HashMap<String, OverrideType>();
		
		static {
			for ( OverrideType type : OverrideType.values()) {
				lookup.put(type.id(), type);
			}
		}
		
		OverrideType(String id, String processor, Permission permission) {
			this.id = id;
			this.processor = processor;
			this.permission = permission;
		}
		
		public String id() { return id; }
		public String processor() { return processor; }
		public Permission permission() { return permission; }
		public static OverrideType lookup(String id) { return lookup.get(id); }
	}
	
	
	public class OverrideResult extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		public Boolean success;
		public String message;
		public Ticket ticket;
		public boolean doTicketUpdate;
		public OverrideResult(Boolean success, String message, Ticket ticket) {
			this(success, message,ticket, true);
		}
		public OverrideResult(Boolean success, String message, Ticket ticket, boolean doTicketUpdate) {
			super();
			this.success = success;
			this.message = message;
			this.ticket = ticket;
			this.doTicketUpdate = doTicketUpdate;
		}
	}

}
