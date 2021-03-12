package com.ansi.scilla.web.common.request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.callNote.CallNoteReference;
import com.ansi.scilla.common.claims.WorkHoursType;
import com.ansi.scilla.common.db.CallLog;
import com.ansi.scilla.common.db.Document;
import com.ansi.scilla.common.db.EmployeeExpense;
import com.ansi.scilla.common.db.MSTable;
import com.ansi.scilla.common.db.Ticket;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.common.invoice.InvoiceGrouping;
import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.JobTagStatus;
import com.ansi.scilla.common.jobticket.JobTagType;
import com.ansi.scilla.common.payment.PaymentMethod;
import com.ansi.scilla.web.claims.request.ClaimEntryRequestType;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.FieldMap;
import com.ansi.scilla.web.common.utils.Permission;
import com.thewebthing.commons.db2.DBTable;
import com.thewebthing.commons.db2.RecordNotFoundException;
import com.thewebthing.commons.lang.StringUtils;

public class RequestValidator {

	public static void checkForDuplicates(Connection conn, WebMessages webMessages, MSTable table,
			HashMap<String, Object> addRequest, List<FieldMap> fieldMap, SimpleDateFormat standardDateFormat)
			throws Exception {
		Logger logger = LogManager.getLogger(RequestValidator.class);
		String tableName = table.getClass().getAnnotation(DBTable.class).value();
		logger.log(Level.DEBUG, "Table: " + tableName);
		HashMap<String, List<String>> indexMap = new HashMap<String, List<String>>(); // index
																						// name
																						// ->
																						// list
																						// of
																						// column
																						// in
																						// that
																						// index
		DatabaseMetaData dbmd = conn.getMetaData();
		ResultSet rs = dbmd.getIndexInfo(null, null, tableName, true, false);
		while (rs.next()) {
			String indexName = rs.getString("INDEX_NAME");
			String columnName = rs.getString("COLUMN_NAME");
			if (!StringUtils.isBlank(indexName) && !StringUtils.isBlank(columnName)) {
				logger.log(Level.DEBUG, "Index: " + indexName + "\tColumn: " + columnName);
				List<String> columnList = indexMap.containsKey(indexName) ? indexMap.get(indexName)
						: new ArrayList<String>();
				columnList.add(columnName);
				indexMap.put(indexName, columnList);
			}
		}
		rs.close();

		DupeChecker dupeChecker = new DupeChecker(table, addRequest, fieldMap);
		for (Entry<String, List<String>> entry : indexMap.entrySet()) {
			logger.log(Level.DEBUG, "Checking: " + entry.getKey());
			dupeChecker.checkForDupes(conn, entry.getValue(), webMessages, standardDateFormat);
		}
	}

	public static void validateAccountType(Connection conn, WebMessages webMessages, String fieldName, String value,
			boolean required) throws Exception {
		validateCode(conn, webMessages, "quote", "account_type", fieldName, value, required, null);
	}

	public static void validateBigDecimal(WebMessages webMessages, String fieldName, BigDecimal value,
			BigDecimal minValue, BigDecimal maxValue, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (minValue != null && value.compareTo(minValue) < 0) {
				webMessages.addMessage(fieldName, "Must be at least " + minValue.doubleValue());
			} else if (maxValue != null && value.compareTo(maxValue) > 0) {
				webMessages.addMessage(fieldName, "Can be no more than " + maxValue.doubleValue());
			}
		}
	}

	public static void validateBoolean(WebMessages webMessages, String fieldName, Integer value, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (value != 1 && value != 0) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateBoolean(WebMessages webMessages, String fieldName, Boolean value, boolean required) {
		if (required) {
			if (value == null) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		}
	}

	public static void validateBuildingType(Connection conn, WebMessages webMessages, String fieldName, String value,
			boolean required) throws Exception {
		validateCode(conn, webMessages, "job", "building_type", fieldName, value, required, null);
	}

	
	public static void validateCallNoteXrefType(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				CallNoteReference callNoteReference = CallNoteReference.valueOf(value);
				if (callNoteReference == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}
	
	public static void validateClaimDetailRequestType(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				ClaimEntryRequestType invoiceGrouping = ClaimEntryRequestType.valueOf(value);
				if (invoiceGrouping == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateClaimWeek(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Claim Week is Required");
			} 
		} else {
			Pattern pattern = Pattern.compile("^([0-9][0-9][0-9][0-9])(-)([0-9][0-9])$",Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(value);
			if ( matcher.matches() ) {
				int weekNum = Integer.valueOf(matcher.group(3)).intValue();
				if ( weekNum < 1 || weekNum > 53 ) {
					webMessages.addMessage(fieldName, "Invalid claim week (week number)");
				}
			} else {
				webMessages.addMessage(fieldName, "Invalid claim week (format)");
			}
			
		}
	}

	/**
	 * 
	 * @param conn
	 * @param webMessages
	 * @param dbTableName
	 * @param dbFieldName
	 * @param fieldName
	 * @param value
	 * @param required
	 * @param label Optional String value to be incorporated into the error message. Useful for those cases where the message is not displayed next to the field being validated
	 * @throws Exception
	 */
	private static void validateCode(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, String value, boolean required, String label) throws Exception {
		if (StringUtils.isBlank(value) ) {
			if (required) {
				String message = StringUtils.isBlank(label) ? "Required Value" : label + " is required";
				webMessages.addMessage(fieldName, message);
			}
		} else {
			String sql = "select * from code where table_name=? and field_name=? and value=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, dbTableName);
			ps.setString(2, dbFieldName);
			ps.setString(3, value);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				String message = StringUtils.isBlank(label) ? "Invalid Value" : label + " is invalid";
				webMessages.addMessage(fieldName, message);
			}
		}
	}
	
	
	
	public static void validateContactType(Connection conn, WebMessages webMessages, String fieldName, String value, boolean required) throws Exception {
		validateCode(conn, webMessages, CallLog.TABLE, CallLog.CONTACT_TYPE, fieldName, value, required, null);
	}
	


	public static void validateDate(WebMessages webMessages, String fieldName, String value, String format,
			boolean required, Date minValue, Date maxValue) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		validateDate(webMessages, fieldName, value, sdf, required, minValue, maxValue);
	}

	public static void validateDate(WebMessages webMessages, String fieldName, String value, SimpleDateFormat format,
			boolean required, Date minValue, Date maxValue) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				Date date = format.parse(value);
				if (minValue != null && date.before(minValue)) {
					String minLabel = format.format(minValue);
					webMessages.addMessage(fieldName, "Date must be after " + minLabel);
				}
				if (maxValue != null && date.after(maxValue)) {
					String maxLabel = format.format(maxValue);
					webMessages.addMessage(fieldName, "Date must be before " + maxLabel);
				}
			} catch (ParseException e) {
				webMessages.addMessage(fieldName, "Invalid Date Format: " + format.toPattern());
			}
		}

	}

	
	public static void validateDate(WebMessages webMessages, String fieldName, Calendar value, boolean required,
			Date minValue, Date maxValue) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			if (minValue != null && value.before(minValue)) {
				String minLabel = format.format(minValue);
				webMessages.addMessage(fieldName, "Date must be after " + minLabel);
			}
			if (maxValue != null && value.after(maxValue)) {
				String maxLabel = format.format(maxValue);
				webMessages.addMessage(fieldName, "Date must be before " + maxLabel);
			}

		}
	}
	
	
	public static void validateDate(WebMessages webMessages, String fieldName, Date value, boolean required,
			Date minValue, Date maxValue) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
			if (minValue != null && value.before(minValue)) {
				String minLabel = format.format(minValue);
				webMessages.addMessage(fieldName, "Date must be after " + minLabel);
			}
			if (maxValue != null && value.after(maxValue)) {
				String maxLabel = format.format(maxValue);
				webMessages.addMessage(fieldName, "Date must be before " + maxLabel);
			}

		}
	}
	
	
	public static void validateDivisionUser(Connection conn, WebMessages webMessages, Integer divisionId, String divisionField, Integer userId, String userField, boolean required) throws SQLException {
		if ( divisionId == null ) {
			if ( required == true ) {
				webMessages.addMessage(divisionField, "Not authorized for this division");
			}
		} else if ( userId == null ) {
			if ( required == true ) {
				webMessages.addMessage(userField, "Not authorized for this division");
			}
		} else {
			PreparedStatement ps = conn.prepareStatement("select count(*) as record_count from division_user where division_id=? and user_id=?");
			ps.setInt(1,  divisionId);
			ps.setInt(2,  userId);
			ResultSet rs = ps.executeQuery();
			if ( rs.next() ) {
				if ( rs.getInt("record_count") == 0 ) {
					webMessages.addMessage(divisionField, "Not authorized for this division");
				}
			} else {
				webMessages.addMessage(divisionField, "Error checking authorization");
			}
			rs.close();
			
		}
		
	}

	public static void validateDocumentId(Connection conn, WebMessages webMessages, String fieldName, Integer value, boolean required) throws Exception {
		validateId(conn, webMessages, Document.TABLE, Document.DOCUMENT_ID, fieldName, value, required, null);
	}

	
	public static void validateDocumentType(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				DocumentType documentType = DocumentType.valueOf(value);
				if (documentType == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}


	public static void validateDocumentTypeXref(Connection conn, WebMessages webMessages, String fieldName, DocumentType documentType, Integer value, boolean required) throws SQLException {
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");			
			} 
		} else {
			if ( ! documentType.isValidXref(conn, value) ) {
				webMessages.addMessage(fieldName, "Invalid cross-reference");
			}
		}
	}

	
	
	public static void validateDouble(WebMessages webMessages, String fieldName, Double value, Double minValue,
			Double maxValue, boolean required, String label) {
		if (value == null) {
			if (required) {
				String message = StringUtils.isBlank(label) ? "Required Value" : label + " is required";
				webMessages.addMessage(fieldName, message);
			}
		} else {
			String message = StringUtils.isBlank(label) ? "" : label + " ";
			if (minValue != null && value < minValue) {
				webMessages.addMessage(fieldName, message + "Must be at least " + minValue.toString());
			} else if (maxValue != null && value > maxValue) {
				webMessages.addMessage(fieldName, message + "Cannot be more than " + maxValue.toString());
			}
		}
	}

	public static void validateFloat(WebMessages webMessages, String fieldName, Float value, Float minValue,
			Float maxValue, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (minValue != null && value < minValue) {
				webMessages.addMessage(fieldName, "Must be at least " + minValue.toString());
			} else if (maxValue != null && value > maxValue) {
				webMessages.addMessage(fieldName, "Cannot be more than " + maxValue.toString());
			}
		}
	}

	public static void validateFutureDate(WebMessages webMessages, String fieldName, String value, String format,
			boolean required) {
		Date minDate = new Date();
		validateDate(webMessages, fieldName, value, format, required, minDate, null);
	}

	public static void validateEmail(WebMessages webMessages, String fieldName, String value, boolean required) {
		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"; // "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$.";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);

		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			Matcher matcher = pattern.matcher(value);
			if (!matcher.matches()) {
				webMessages.addMessage(fieldName, "Invalid email address");
			}
		}

	}

	public static void validateExpenseType(Connection conn, WebMessages webMessages, String fieldName, String value,
			boolean required) throws Exception {
		validateCode(conn, webMessages, EmployeeExpense.TABLE, EmployeeExpense.EXPENSE_TYPE, fieldName, value, required, null);
	}

	
	public static void validateId(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, Integer value, boolean required) throws Exception {
		validateId(conn, webMessages, dbTableName, dbFieldName, fieldName, value, required, null);
	}
	
	
	/**
	 * Validate the unique key into a database table, typically generated by an auto-increment. (eg. ticket_id for the Ticket table)
	 * @param conn
	 * @param webMessages
	 * @param dbTableName
	 * @param dbFieldName
	 * @param fieldName
	 * @param value
	 * @param required
	 * @param label Optional String value to be incorporated into the error message. Useful for those cases where the message is not displayed next to the field being validated
	 * @throws Exception
	 */
	public static void validateId(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, Integer value, boolean required, String label) throws Exception {
		if (value == null) {
			if (required) {
				String message = StringUtils.isBlank(label) ? "Required Value" : label + " is required";
				webMessages.addMessage(fieldName, message);
			}
		} else {
			String sql = "select * from " + dbTableName + " where " + dbFieldName + "=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, value);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				String message = StringUtils.isBlank(label) ? "Invalid Value" : label + " is invalid";
				webMessages.addMessage(fieldName, message);
			}
		}
	}

	public static void validateInteger(WebMessages webMessages, String fieldName, Integer value, Integer minValue,
			Integer maxValue, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (minValue != null && value < minValue) {
				webMessages.addMessage(fieldName, "Minimum value is " + maxValue);
			}
			if (maxValue != null && value > maxValue) {
				webMessages.addMessage(fieldName, "Maximum value is " + maxValue);
			}
		}
	}

	public static void validateInvoiceGrouping(WebMessages webMessages, String fieldName, String value,
			boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				InvoiceGrouping invoiceGrouping = InvoiceGrouping.valueOf(value);
				if (invoiceGrouping == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateInvoiceStyle(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				InvoiceStyle invoiceStyle = InvoiceStyle.valueOf(value);
				if (invoiceStyle == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateInvoiceTerms(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				InvoiceTerm invoiceTerms = InvoiceTerm.valueOf(value);
				if (invoiceTerms == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateJobFrequency(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				JobFrequency jobFrequency = JobFrequency.lookup(value);
				if (jobFrequency == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateLeadType(Connection conn, WebMessages webMessages, String fieldName, String value,
			boolean required) throws Exception {
		validateCode(conn, webMessages, "quote", "lead_type", fieldName, value, required, null);
	}

	public static void validateNumber(WebMessages webMessages, String fieldName, Object value, Object minValue,
			Object maxValue, boolean required, String label) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (value instanceof Double) {
				validateDouble(webMessages, fieldName, (Double) value, (Double) minValue, (Double) maxValue, required, label);
			} else if (value instanceof BigDecimal) {
				validateBigDecimal(webMessages, fieldName, (BigDecimal) value, (BigDecimal) minValue, (BigDecimal) maxValue, required);
			} else if (value instanceof Integer) {
				validateInteger(webMessages, fieldName, (Integer) value, (Integer) minValue, (Integer) maxValue, required);
			} else if (value instanceof Float) {
				validateFloat(webMessages, fieldName, (Float) value, (Float) minValue, (Float) maxValue, required);
			} else {
				webMessages.addMessage(fieldName, "Invalid Format");
			}

		}
	}

	
	public static void validatePassthruExpenseType(Connection conn, WebMessages webMessages, String fieldName, String value, boolean required, String label) throws Exception {
		validateCode(conn, webMessages, "ticket_claim_passthru", "passthru_expense_type", fieldName, value, required, label);
	}

	
	public static void validatePaymentTerms(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Expense Type is required");
			}
		} else {
			try {
				PaymentMethod paymentTerms = PaymentMethod.valueOf(value);
				if (paymentTerms == null) {
					webMessages.addMessage(fieldName, "Invalid Passthru Expense");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Passthru Expense");
			}
		}
	}

	public static void validatePermissionName(WebMessages webMessages, String fieldName, String value,
			boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				Permission permission = Permission.valueOf(value);
				if (permission == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateString(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		}
	}

	/**
	 * 
	 * @param webMessages
	 * @param fieldName
	 * @param value
	 * @param maxLength
	 * @param required
	 * @param label Optional String value to be incorporated into the error message. Useful for those cases where the message is not displayed next to the field being validated
	 */
	public static void validateString(WebMessages webMessages, String fieldName, String value, Integer maxLength, boolean required, String label) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				String message = StringUtils.isBlank(label) ? "Required Value" : label + " is required";
				webMessages.addMessage(fieldName, message);
			}
		} else {
			if (value.length() > maxLength ) {
				String message = StringUtils.isBlank(label) ? "" : label + " ";
				webMessages.addMessage(fieldName, message + "Must be less than " + maxLength + " characters");
			}
		}
	}

	
	public static void validateTagAbbrev(Connection conn, WebMessages webMessages, String fieldName, String tagType,
			String value, boolean required) throws Exception {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				PreparedStatement ps = conn.prepareStatement("select count(*) as record_count from job_tag where tag_type=? and abbrev=?");
				ps.setString(1, tagType);
				ps.setString(2, value);
				ResultSet rs = ps.executeQuery();
				if ( rs.next() ) {
					if ( rs.getInt("record_count") > 0 ) {
						webMessages.addMessage(fieldName, "Duplicate Tag Abbreviation");
					}
				} else {
					throw new Exception("Error validating abbreviation. Contact Support");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
		
	}
	
	public static void validateTagCode(Connection conn, WebMessages webMessages, String fieldName, String tagType,
			String value, boolean required) throws Exception {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				PreparedStatement ps = conn.prepareStatement("select count(*) as record_count from job_tag where tag_type=? and long_code=?");
				ps.setString(1, tagType);
				ps.setString(2, value);
				ResultSet rs = ps.executeQuery();
				if ( rs.next() ) {
					if ( rs.getInt("record_count") > 0 ) {
						webMessages.addMessage(fieldName, "Duplicate Tag Code");
					}
				} else {
					throw new Exception("Error validating code. Contact Support");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
		
	}

	public static void validateTagStatus(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				JobTagStatus tagStatus = JobTagStatus.valueOf(value);
				if (tagStatus == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
		
	}

	public static void validateTagType(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				JobTagType tagType = JobTagType.valueOf(value);
				if (tagType == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
		
	}

	/**
	 * 
	 * @param conn
	 * @param webMessages
	 * @param fieldName
	 * @param value
	 * @param required
	 * @param label Optional String value to be incorporated into the error message. Useful for those cases where the message is not displayed next to the field being validated
	 * @throws Exception
	 */
	public static void validateTicketId(Connection conn, WebMessages webMessages, String fieldName, Integer value, boolean required, String label) throws Exception {
		if (value == null) {
			if (required) {
				String message = StringUtils.isBlank(label) ? "Required Value" : label + " is required";
				webMessages.addMessage(fieldName, message);
			}
		} else {
			validateId(conn, webMessages, Ticket.TABLE, Ticket.TICKET_ID, fieldName, value, required, label);
		}
	}
	
	
	
	public static void validateUserStatus(WebMessages webMessages, String fieldName, Integer value, boolean required) {
		List<Integer> validValues = Arrays
				.asList(new Integer[] { User.STATUS_IS_GOOD, User.STATUS_IS_INACTIVE, User.STATUS_IS_LOCKED });
		System.out.println("Value: ");
		System.out.println(value);

		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (!validValues.contains(value)) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	/**
	 * Make sure the user belongs to a permission group that contains
	 * "CAN_RUN_TICKETS" permission
	 * 
	 * @param conn
	 * @param webMessages
	 * @param fieldName
	 * @param value
	 * @param required
	 * @throws Exception
	 */
	public static void validateWasherId(Connection conn, WebMessages webMessages, String fieldName, Integer value,
			boolean required) throws Exception {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			String sql = "select ansi_user.user_id\n" + " from ansi_user\n"
					+ " inner join permission_group on permission_group.permission_group_id=ansi_user.permission_group_id\n"
					+ " inner join permission_group_level on permission_group_level.permission_group_id = permission_group.permission_group_id\n"
					+ " 		and permission_group_level.permission_name='CAN_RUN_TICKETS'\n"
					+ " where ansi_user.user_id=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, value);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
			rs.close();
		}
	}

	public static void validateWorkHoursType(WebMessages webMessages, String fieldName, String value,
			boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				WorkHoursType workHoursType = WorkHoursType.valueOf(value);
				if (workHoursType == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}


}
