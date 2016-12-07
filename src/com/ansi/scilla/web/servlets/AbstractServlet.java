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

import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.request.AbstractRequest;
import com.ansi.scilla.web.request.RequiredForAdd;
import com.ansi.scilla.web.request.RequiredForUpdate;
import com.ansi.scilla.web.request.RequiredFormat;
import com.ansi.scilla.web.response.AnsiResponse;
import com.ansi.scilla.web.response.MessageResponse;
import com.thewebthing.commons.lang.JsonUtils;


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
	
	
	protected void sendResponse(Connection conn, HttpServletResponse response, ResponseCode responseCode, MessageResponse data) throws Exception {
		AnsiResponse ansiResponse = new AnsiResponse(conn, responseCode, data);
		response.setStatus(responseCode.statusCode());
		response.setContentType("application/json");
		
		ServletOutputStream o = response.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(o);
		String jsonString = JsonUtils.object2JSON(ansiResponse);
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
					missingValues.add(fieldName);
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
				if ( value == null ) {
					String fieldName = method.getName().substring(3);
					missingValues.add(fieldName);
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
						nonMatchingValues.add(fieldName);
					}
				}
			}
		}
		
		return nonMatchingValues;
	}
	

}
