package com.ansi.scilla.web.request.report;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.reportBuilder.AnsiReport;
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
	private final SimpleDateFormat dateParmFormat = new SimpleDateFormat("MM/dd/yyyy");
	private final String PARM_DIVISION_ID = "divisionId";
	private final String PARM_START_DATE = "startDate";
	private final String PARM_END_DATE = "endDate";
	private final String PARM_MONTH = "month";
	private final String PARM_YEAR = "year";
	
	
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
	
	private ReportDefinition() {
		super();
	}
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
	 * @throws ParseException 
	 */
	public ReportDefinition(HttpServletRequest request) throws ResourceNotFoundException, UnsupportedEncodingException, IOException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ParseException {
		super();
		
		String jsonString = makeJsonString(request);
		StandardReportRequest reportRequest = new StandardReportRequest();
		if ( ! StringUtils.isBlank(jsonString)) {
			System.out.println("ReportDef 75: " + jsonString);
			AppUtils.json2object(jsonString, reportRequest);
			System.out.println("ReportDef 75: " + reportRequest);
		}
		int idx = request.getRequestURI().indexOf("/report/");
		if ( idx < 0 ) {			
			throw new ResourceNotFoundException("No Report");
		}
		
		parseParameters(request.getParameterMap());

		
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
	
	private void parseParameters(Map<String, String[]> queryParameterMap) throws ParseException {
		if ( queryParameterMap.containsKey(PARM_START_DATE)) {
			Date startDate = dateParmFormat.parse(queryParameterMap.get(PARM_START_DATE)[0]);
			this.startDate = Calendar.getInstance(new AnsiTime());
			this.startDate.setTime(startDate);
		}
		if ( queryParameterMap.containsKey(PARM_END_DATE)) {
			Date endDate = dateParmFormat.parse(queryParameterMap.get(PARM_END_DATE)[0]);
			this.endDate = Calendar.getInstance(new AnsiTime());
			this.endDate.setTime(endDate);
		}
		if ( queryParameterMap.containsKey(PARM_DIVISION_ID)) {
			this.divisionId = Integer.valueOf(queryParameterMap.get(PARM_DIVISION_ID)[0]);
		}
		if ( queryParameterMap.containsKey(PARM_MONTH)) {
			this.month = Integer.valueOf(queryParameterMap.get(PARM_MONTH)[0]);
		}
		if ( queryParameterMap.containsKey(PARM_YEAR)) {
			this.year = Integer.valueOf(queryParameterMap.get(PARM_YEAR)[0]);
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


	private String makeJsonString(HttpServletRequest request) throws UnsupportedEncodingException, IOException {
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

	public String makeReportFileName() {
		List<String> names = new ArrayList<String>();
		String[] pieces = this.reportType.name().split("_");
		for ( String piece : pieces ) {
			names.add(StringUtils.capitalize(piece.toLowerCase()));
		}
		return StringUtils.join(names, "");
	}
	/**
	 * Validate the current report definition against the validator class defined in the ReportType enum
	 * @param conn
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("unchecked")
	public List<String> validate(Connection conn) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String validatorClassName = this.reportType.validatorClassName();
		Class<?> validatorClass = Class.forName(validatorClassName);
		Method method = validatorClass.getMethod("validate", new Class<?>[] {Connection.class, ReportDefinition.class});
		List<String> messageList = (List<String>)method.invoke(null, new Object[]{conn, this});
		return messageList;
	}

	
	/**
	 * Uses the parameter list from the ReportType enum to generate a list of class types (based on the
	 * return type of the getter methods) and values (based on executing the method) to call the static
	 * buildReport method for the specified report. If the stars align, and we've followed all the rules
	 * we'll return a report object that can be fed to an HTML/XLS/PDF builder for display.
	 * @param conn
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public AnsiReport build(Connection conn) throws ClassNotFoundException, NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> reportClass = Class.forName(reportType.reportClassName());
		int arrayLength = reportType.builderParms().length + 1;   // connection + all parms
		Class<?>[] classList = new Class<?>[arrayLength];
		Object[] objectList = new Object[arrayLength];
		
		classList[0] = Connection.class;
		objectList[0] = conn;
		
		for ( int i = 0; i < reportType.builderParms().length; i++ ) {
			int idx = i + 1;
			String methodName = "get" + StringUtils.capitalize(reportType.builderParms()[i]);
			Method getter = this.getClass().getMethod(methodName, (Class<?>[])null);
			classList[idx] = getter.getReturnType();
			objectList[idx] = getter.invoke(this, (Object[])null);
		}
		
		Method builderMethod = reportClass.getMethod("buildReport", classList);
		AnsiReport report = (AnsiReport)builderMethod.invoke(null, objectList);
		return report;
	}

	public static void main(String[] args) {
		Connection conn = null;
		Calendar startDate = new GregorianCalendar(2017, Calendar.AUGUST, 8);
		Calendar endDate = new GregorianCalendar(2017, Calendar.SEPTEMBER, 10);
		try {
			conn = AppUtils.getDevConn();
			ReportDefinition def = new ReportDefinition();
			def.startDate = startDate;
			def.endDate = endDate;
			def.reportType = ReportType.CASH_RECEIPTS_REGISTER;
			AnsiReport report = def.build(conn);
			
		} catch ( Exception e) {
			e.printStackTrace();
		} finally {
			AppUtils.closeQuiet(conn);			
		}
	}
}