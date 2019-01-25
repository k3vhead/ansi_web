package com.ansi.scilla.web.common.request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.claims.WorkHoursType;
import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.employee.EmployeeHoursType;
import com.ansi.scilla.common.invoice.InvoiceGrouping;
import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.payment.PaymentMethod;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.Permission;
import com.thewebthing.commons.lang.StringUtils;

public class RequestValidator {

	public static void validateBigDecimal(WebMessages webMessages, String fieldName, BigDecimal value, BigDecimal minValue, BigDecimal maxValue, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( minValue != null && value.compareTo(minValue) < 0 ) {
				webMessages.addMessage(fieldName, "Must be at least " + minValue.doubleValue());
			} else if ( maxValue != null && value.compareTo(maxValue) > 0 ) {
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

	public static void validateBoolean(WebMessages webMessages, String fieldName, boolean value, boolean required) {
		if (value == required) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if (value != required) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateCode(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, String value, boolean required) throws Exception {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			String sql = "select * from code where table_name=? and field_name=? and value=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1, dbTableName);
			ps.setString(2, dbFieldName);
			ps.setString(3, value);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateDate(WebMessages webMessages, String fieldName, String value, String format, boolean required, Date minValue, Date maxValue) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		validateDate(webMessages, fieldName, value, sdf, required, minValue, maxValue);
	}
	
	
	public static void validateDate(WebMessages webMessages, String fieldName, String value, SimpleDateFormat format, boolean required, Date minValue, Date maxValue) {
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
			} catch ( ParseException e ) {
				webMessages.addMessage(fieldName, "Invalid Date Format: " + format.toPattern());
			}
		}

	}

	
	public static void validateDate(WebMessages webMessages, String fieldName, Date value, boolean required, Date minValue, Date maxValue) {
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
	
	public static void validateDouble(WebMessages webMessages, String fieldName, Double value, Double minValue, Double maxValue, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( minValue != null && value < minValue ) {
				webMessages.addMessage(fieldName, "Must be at least " + minValue.toString());
			} else if ( maxValue != null && value > maxValue ) {
				webMessages.addMessage(fieldName, "Cannot be more than " + maxValue.toString());
			}
		}
	}
	
	
	
	public static void validateFloat(WebMessages webMessages, String fieldName, Float value, Float minValue, Float maxValue, boolean required) {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( minValue != null && value < minValue ) {
				webMessages.addMessage(fieldName, "Must be at least " + minValue.toString());
			} else if ( maxValue != null && value > maxValue ) {
				webMessages.addMessage(fieldName, "Cannot be more than " + maxValue.toString());
			}
		}
	}
	
	
	
	
	
	public static void validateFutureDate(WebMessages webMessages, String fieldName, String value, String format, boolean required) {
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

	public static void validateExpenseType(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				EmployeeHoursType employeeHoursType = EmployeeHoursType.valueOf(value);
				if (employeeHoursType == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateId(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, Integer value, boolean required) throws Exception {
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			String sql = "select * from " + dbTableName + " where " + dbFieldName + "=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, value);
			ResultSet rs = ps.executeQuery();
			if (!rs.next()) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}

	public static void validateInteger(WebMessages webMessages, String fieldName, Integer value, Integer minValue, Integer maxValue, boolean required) {
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

	public static void validateJobFreqency(WebMessages webMessages, String fieldName, String value, boolean required) {
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

	
	
	public static void validateNumber(WebMessages webMessages, String fieldName, Object value, Object minValue, Object maxValue, boolean required) {
		Logger logger = LogManager.getLogger(RequestValidator.class);
		if (value == null) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			logger.log(Level.DEBUG, fieldName + "\t" + value + "\t" + value.getClass().getName());
			if ( value instanceof Double ) {
				validateDouble(webMessages, fieldName, (Double)value, (Double)minValue, (Double)maxValue, required);
			} else if ( value instanceof BigDecimal ) {
				validateBigDecimal(webMessages, fieldName, (BigDecimal)value, (BigDecimal)minValue, (BigDecimal)maxValue, required);
			} else if ( value instanceof Integer ) {
				validateInteger(webMessages, fieldName, (Integer)value, (Integer)minValue, (Integer)maxValue, required);
			} else if ( value instanceof Float ) {
				validateFloat(webMessages, fieldName, (Float)value, (Float)minValue, (Float)maxValue, required);
			} else {
				webMessages.addMessage(fieldName, "Invalid Format");
			}
				
		}
	}
	
	
	
	public static void validatePaymentTerms(WebMessages webMessages, String fieldName, String value, boolean required) {
		if (StringUtils.isBlank(value)) {
			if (required) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				PaymentMethod paymentTerms = PaymentMethod.valueOf(value);
				if (paymentTerms == null) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch (IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
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
