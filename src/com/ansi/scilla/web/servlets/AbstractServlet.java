package com.ansi.scilla.web.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.common.WebMessages;
import com.ansi.scilla.web.request.AbstractRequest;
import com.ansi.scilla.web.request.MinMax;
import com.ansi.scilla.web.request.RequiredForAdd;
import com.ansi.scilla.web.request.RequiredForUpdate;
import com.ansi.scilla.web.request.RequiredFormat;
import com.ansi.scilla.web.response.AnsiResponse;
import com.ansi.scilla.web.response.MessageResponse;


public class AbstractServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	public static final String ACTION_IS_ADD = "add";
	

	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doGet(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	/**
	 * Return a 404 not found response
	 * @param response
	 */
	protected void sendNotFound(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
		response.setContentType("application/json");
	}
	
	/**
	 * Return a 403 forbidden response
	 * @param response
	 */
	protected void sendForbidden(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType("application/json");
	}
	
	/**
	 * Return a 405 Not Allowed response
	 * @param response
	 */
	protected void sendNotAllowed(HttpServletResponse response) {
		response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		response.setContentType("application/json");
	}
	
	/**
	 * Send a json response to the client.
	 * 
	 * @param conn Database connection object
	 * @param response HTTPServlet response object
	 * @param responseCode ANSI Response Code, indicating the HTTP Servlet response that should be returned
	 * @param data Transaction-specific ANSI response data
	 * @throws Exception
	 */
	protected void sendResponse(Connection conn, HttpServletResponse response, ResponseCode responseCode, MessageResponse data) throws Exception {
		AnsiResponse ansiResponse = new AnsiResponse(conn, responseCode, data);
		response.setStatus(responseCode.statusCode());
		response.setContentType("application/json");
		
		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		String jsonString = AppUtils.object2json(ansiResponse);
		writer.write(jsonString);
		writer.flush();
		writer.close();
	}

	/**
	 * Take the posted values from an HttpServletRequest object and convert into a string. 
	 * For our purposes, we're expecting this to be JSON
	 * 
	 * @param request
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public String makeJsonString(HttpServletRequest request) throws UnsupportedEncodingException, IOException {
		Writer writer = new StringWriter();
		 
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
        	request.getInputStream().close();
        }
        return writer.toString();        
	}

	/**
	 * Convert a query string into a map of name/value pairs
	 * 
	 * @param queryString
	 * @return
	 */
	protected HashMap<String, String> makeParamMap(String queryString) {
		HashMap<String, String> paramMap = new HashMap<String, String>();
		String[] pairs = queryString.split("\\&");
		for ( String pair : pairs ) {
			String[] pieces = pair.split("=");
			if ( pieces.length == 2 ) {
				paramMap.put(pieces[0], pieces[1]);
			}
		}
		return paramMap;
	}
	
	/**
	 * Given a request object, parse the "getters" for required fields for an "add" action
	 * 
	 * @param request
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public List<String> validateRequiredAddFields(AbstractRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<String> missingValues = new ArrayList<String>();
		
		for ( Method method : request.getClass().getMethods() ) {
			if ( method.getAnnotation(RequiredForAdd.class) != null ) {
				Object value = method.invoke(request, (Object[])null);
				if ( value == null ) {
					String fieldName = method.getName().substring(3);
					missingValues.add(fixFieldName(fieldName));
				} else if ( value instanceof String && StringUtils.isBlank((String)value)) {
					String fieldName = method.getName().substring(3);
					missingValues.add(fixFieldName(fieldName));
				}
			}
		}
		
		return missingValues;
	}
	
	/**
	 * Given a request object, parse the "getters" for required fields for an "update" action
	 * 
	 * @param request
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public List<String> validateRequiredUpdateFields(AbstractRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<String> missingValues = new ArrayList<String>();
		
		for ( Method method : request.getClass().getMethods() ) {
			if ( method.getAnnotation(RequiredForUpdate.class) != null ) {
				Object value = method.invoke(request, (Object[])null);
				String fieldName = method.getName().substring(3);
				if ( value == null ) {
					missingValues.add(fixFieldName(fieldName));
				} else {
					String fieldValue = String.valueOf(value);
					if ( StringUtils.isBlank(fieldValue)) {
						missingValues.add(fixFieldName(fieldName));
					}
				}
			}
		}
		
		return missingValues;
	}
	

	/**
	 * Given a request object, parse the "getters" for required formatted values
	 * 
	 * @param request
	 * @return
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public List<String> validateFormat(AbstractRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<String> nonMatchingValues = new ArrayList<String>();
		
		for ( Method method : request.getClass().getMethods() ) {
			RequiredFormat requiredFormat = method.getAnnotation(RequiredFormat.class);
			if ( requiredFormat != null ) {
				String regex = requiredFormat.value();
				Pattern pattern = Pattern.compile(regex);
				Object value = method.invoke(request, (Object[])null);
				if ( value != null && value instanceof String) {
					Matcher m = pattern.matcher((String)value);
					if ( ! m.matches() ) {
						String fieldName = method.getName().substring(3);
						nonMatchingValues.add(fixFieldName(fieldName));
					}
				}
			}
		}
		
		return nonMatchingValues;
	}
	
	
	public List<String> validateValues(AbstractRequest request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		List<String> nonMatchingValues = new ArrayList<String>();
		
		for ( Method method : request.getClass().getMethods() ) {
			MinMax minMax = method.getAnnotation(MinMax.class);
			if ( minMax != null ) {
				String fieldName = method.getName().substring(3);
				
				float min = minMax.min();
				float max = minMax.max();
				Object value = method.invoke(request, (Object[])null);
				if ( value != null && value instanceof BigDecimal) {
					BigDecimal decimalValue = (BigDecimal)value;
					BigDecimal minValue = new BigDecimal(min);
					BigDecimal maxValue = new BigDecimal(max);
					
					if ( minValue.compareTo(decimalValue) > 0 ) {
						nonMatchingValues.add(fixFieldName(fieldName));
					} else if ( maxValue.compareTo(decimalValue) < 0 ) {
						nonMatchingValues.add(fixFieldName(fieldName));
					}
				} else if ( value != null && value instanceof Float) {
					Float decimalValue = (Float)value;
					Float minValue = new Float(min);
					Float maxValue = new Float(max);
					
					if ( minValue.compareTo(decimalValue) > 0 ) {
						nonMatchingValues.add(fixFieldName(fieldName));
					} else if ( maxValue.compareTo(decimalValue) < 0 ) {
						nonMatchingValues.add(fixFieldName(fieldName));
					}
				} else if ( value != null && value instanceof Integer) {
					Integer decimalValue = (Integer)value;
					Integer minValue = (new Float(min)).intValue();
					Integer maxValue = (new Float(max)).intValue();
					
					if ( minValue.compareTo(decimalValue) > 0 ) {
						nonMatchingValues.add(fixFieldName(fieldName));
					} else if ( maxValue.compareTo(decimalValue) < 0 ) {
						nonMatchingValues.add(fixFieldName(fieldName));
					}
				}
			}
		}
		
		return nonMatchingValues;
	}
	
	
	
	protected String fixFieldName(String fieldName) {
		return fieldName.substring(0,1).toLowerCase() + fieldName.substring(1);
	}
	
	
	/**
	 * The JSON parser throws an InvalidFormatException when it can't populate a field with the
	 * provided data. For instance, trying to populate an Integer field with a string will cause an exception.
	 * The print representation of the exception looks like:
	 * 
	 * at [Source: {"defaultDirectLaborPct":"xxxx","divisionNbr":"","divisionCode":"","description":"","status":"1"}; line: 1, column: 26] (through reference chain: com.ansi.scilla.web.request.DivisionRequest["defaultDirectLaborPct"])
	 * 
	 * This method will find the field name in that mess so that an error message can be displayed in
	 * the right place in the UI. If the field name cannot be determined, the message goes into the 
	 * Global Message.
	 *  
	 * @param e
	 * @return
	 */
	protected String findBadField(String e) {
		String[] pieces = e.split("\n");
		String matchString = StringUtils.join(pieces, " ");
		Pattern pattern = Pattern.compile("^(.*\\[)(.*)(\\]\\))$");
		Matcher matcher = pattern.matcher(matchString);
		String field = matcher.matches() ? matcher.group(2).substring(1, matcher.group(2).length()-1) : WebMessages.GLOBAL_MESSAGE;
		return field;
	}


}
