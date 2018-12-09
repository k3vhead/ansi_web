package com.ansi.scilla.web.common.request;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ansi.scilla.common.db.User;
import com.ansi.scilla.common.invoice.InvoiceGrouping;
import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.payment.PaymentMethod;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.user.request.AnsiUserRequest;
import com.thewebthing.commons.lang.StringUtils;

public class RequestValidator {

	public static void validateBigDecimal(WebMessages webMessages, String fieldName, BigDecimal value, boolean required) {
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		}		
	}



	public static void validateBoolean(WebMessages webMessages, String fieldName, Integer value, boolean required) {
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( value != 1 && value != 0 ) {
				webMessages.addMessage(fieldName,  "Invalid Value");
			}
		}
	}
	
	public static void validateBoolean(WebMessages webMessages, String fieldName, boolean value, boolean required) {
		if ( value == required ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( value != required ) {
				webMessages.addMessage(fieldName,  "Invalid Value");
			}
		}
	}



	public static void validateCode(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, String value, boolean required) throws Exception {
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			String sql = "select * from code where table_name=? and field_name=? and value=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setString(1,  dbTableName);
			ps.setString(2,  dbFieldName);
			ps.setString(3,  value);
			ResultSet rs = ps.executeQuery();
			if ( ! rs.next() ) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}



	public static void validateDate(WebMessages webMessages, String fieldName, Date value, boolean required, Date minValue, Date maxValue) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {			
			if ( minValue != null && value.before(minValue)) {
				String minLabel = sdf.format(minValue);
				webMessages.addMessage(fieldName, "Date must be after " + minLabel);
			}
			if ( maxValue != null && value.after(maxValue)) {
				String maxLabel = sdf.format(maxValue);
				webMessages.addMessage(fieldName, "Date must be before " + maxLabel);
			}
		}
		
	}



	public static void validateFutureDate(WebMessages webMessages, String fieldName, Date value, boolean required) {
		Date minDate = new Date();
		validateDate(webMessages, fieldName, value, required, minDate, null);
	}



	public static void validateEmail(WebMessages webMessages, String fieldName, String value, boolean required) {
		String regex = "^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$"; //"^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$.";
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			Matcher matcher = pattern.matcher(value);
			if ( ! matcher.matches() ) {
				webMessages.addMessage(fieldName, "Invalid email address");
			}
		}
		
	}



	public static void validateId(Connection conn, WebMessages webMessages, String dbTableName, String dbFieldName,
			String fieldName, Integer value, boolean required) throws Exception {
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			String sql = "select * from " + dbTableName + " where " + dbFieldName + "=?";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1,  value);
			ResultSet rs = ps.executeQuery();
			if ( ! rs.next() ) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}



	public static void validateInteger(WebMessages webMessages, String fieldName, Integer value, boolean required, Integer minValue, Integer maxValue) {
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( minValue !=null && value < minValue ) {
				webMessages.addMessage(fieldName, "Minimum value is " + maxValue);
			}
			if ( maxValue != null && value > maxValue ) {
				webMessages.addMessage(fieldName, "Maximum value is " + maxValue);
			}
		}
	}



	public static void validateInvoiceGrouping(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				InvoiceGrouping invoiceGrouping = InvoiceGrouping.valueOf(value);
				if ( invoiceGrouping == null ) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch ( IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}



	public static void validateInvoiceStyle(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				InvoiceStyle invoiceStyle = InvoiceStyle.valueOf(value);
				if ( invoiceStyle == null ) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch ( IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}



	public static void validateInvoiceTerms(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				InvoiceTerm invoiceTerms = InvoiceTerm.valueOf(value);
				if ( invoiceTerms == null ) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch ( IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}



	public static void validateJobFreqency(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				JobFrequency jobFrequency = JobFrequency.lookup(value);
				if ( jobFrequency == null ) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch ( IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}
	}



	public static void validatePaymentTerms(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				PaymentMethod paymentTerms = PaymentMethod.valueOf(value);
				if ( paymentTerms == null ) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch ( IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}
	
	public static void validatePermissionName(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			try {
				Permission permission = Permission.valueOf(value);
				if ( permission == null ) {
					webMessages.addMessage(fieldName, "Invalid Value");
				}
			} catch ( IllegalArgumentException e) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}



	public static void validateString(WebMessages webMessages, String fieldName, String value, boolean required) {
		if ( StringUtils.isBlank(value) ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		}		
	}



	public static void validateUserStatus(WebMessages webMessages, String fieldName, Integer value, boolean required) {
		List<Integer> validValues = Arrays.asList(new Integer[] { User.STATUS_IS_GOOD, User.STATUS_IS_INACTIVE, User.STATUS_IS_LOCKED });
		System.out.println("Value: ");
		System.out.println(value);
		
		if ( value == null ) {
			if ( required ) {
				webMessages.addMessage(fieldName, "Required Value");
			}
		} else {
			if ( ! validValues.contains(value) ) {
				webMessages.addMessage(fieldName, "Invalid Value");
			}
		}		
	}




}
