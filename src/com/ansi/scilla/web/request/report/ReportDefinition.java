package com.ansi.scilla.web.request.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ApplicationWebObject;
import com.ansi.scilla.web.common.ReportType;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

/**
 * Uses URL path and quote string, along with posted JSON to completely define a report
 * type and parameters. 
 *  
 * @author dclewis
 *
 */
public class ReportDefinition extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;
	private ReportType reportType;
	private Calendar startDate;
	private Calendar endDate;
	private Integer divisionId;
	private Integer month;
	private Integer year;

	/*
	<forward name="reportByDivEnd" path="/reportByDivEnd.jsp" />
    <forward name="reportByDivMonthYear" path="/reportByDivMonthYear.jsp" />
    <forward name="reportByDivStartEnd" path="/reportByDivStartEnd.jsp" />
    <forward name="reportByStartEnd" path="/reportByStartEnd.jsp" />
	*/ 
	
	/**
	 * Parse the URI from the given request.
	 * Note that posted values (like the JSON in and "ADD") must be processed before invoking this constructor
	 * 
	 * @param request The unchanged HTTP request object, after posted values are processed 
	 * @param expectedRealm Typically the servlet name, eg "ticket" or "division"
	 * @param expectedCommandList A list of valid commands like "list" or "add". A null value means no commands are accepted
	 * @throws ResourceNotFoundException Thrown if the realm is incorrect or the URI format is wrong, or if the command 
	 * does not validate against the list of expected values.
	 * @throws IOException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public ReportDefinition(HttpServletRequest request) throws ResourceNotFoundException, UnsupportedEncodingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		super();
		
		String jsonString = makeJsonString(request);
		StandardReportRequest reportRequest = new StandardReportRequest();
		if ( ! StringUtils.isBlank(jsonString)) {
			AppUtils.json2object(jsonString, reportRequest);
		}

		Map<String, String[]> queryParameterMap = request.getParameterMap();

		
		int idx = request.getRequestURI().indexOf("/report/");
		if ( idx < 0 ) {			
			throw new ResourceNotFoundException("No Report");
		}
		String myString = request.getRequestURI().substring(idx + 1);
		String[] pieces = myString.split("/");
		String reportId = pieces[1];
		
		try {
			this.reportType = ReportType.valueOf(reportId);
		} catch ( IllegalArgumentException e ) {
			throw new ResourceNotFoundException(reportId);
		}
		
		if (reportRequest.getStartDate() != null ) {
			this.startDate = reportRequest.getStartDate();					
		}
		if ( reportRequest.getEndDate() != null ) {
			this.endDate = reportRequest.getEndDate();
		}
		if ( reportRequest.getDivisionId() != null ) {
			this.divisionId = reportRequest.getDivisionId();
		}
		if ( reportRequest.getMonth() != null ) {
			this.month = reportRequest.getMonth();
		}
		if ( reportRequest.getYear() != null ) {
			this.year = reportRequest.getYear();
		}
		
	}
	
	public ReportType getReportType() {
		return reportType;
	}
	public Calendar getStartDate() {
		return startDate;
	}
	public Calendar getEndDate() {
		return endDate;
	}
	public Integer getDivisionId() {
		return divisionId;
	}
	public Integer getMonth() {
		return month;
	}
	public Integer getYear() {
		return year;
	}


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


	
	
	
}
