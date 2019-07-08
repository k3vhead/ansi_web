package com.ansi.scilla.web.report.request;

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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.report.reportBuilder.AnsiReport;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.ApplicationWebObject;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.report.common.ReportJsp;
import com.ansi.scilla.web.report.common.ReportType;

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
	
	
	protected ReportType reportType;
	protected Calendar startDate;
	protected Calendar endDate;
	private Integer divisionId;
	private Integer month;
	private Integer year;
	private HashMap<String, String> reportDisplay;

	/*
	<forward name="reportByDiv" path="/reportByDiv.jsp" />
	<forward name="reportByDivEnd" path="/reportByDivEnd.jsp" />
    <forward name="reportByDivMonthYear" path="/reportByDivMonthYear.jsp" />
    <forward name="reportByDivStartEnd" path="/reportByDivStartEnd.jsp" />
    <forward name="reportByStartEnd" path="/reportByStartEnd.jsp" />
	*/ 
	
	protected ReportDefinition() {
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
			AppUtils.json2object(jsonString, reportRequest);
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
			this.startDate = Calendar.getInstance(new AnsiTime());
			this.startDate.setTime(reportRequest.getStartDate());					
		}
		if ( reportRequest.getEndDate() != null ) {
			this.endDate = Calendar.getInstance(new AnsiTime());
			this.endDate.setTime(reportRequest.getEndDate());
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
		if ( reportRequest.getReportDisplay() != null ) {
			this.reportDisplay = reportRequest.getReportDisplay();
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


	public HashMap<String, String> getReportDisplay() {
		return reportDisplay;
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
        String jsonString = writer.toString();
        AppUtils.logTransaction(request, jsonString);
        return jsonString;        
	}

	/**
	 * Based on the report type, effective dates and as of, make a filename.
	 * @return
	 */
	public String makeReportFileName(Connection conn) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		SimpleDateFormat yyyymmdd = new SimpleDateFormat("yyyy-MM-dd");
//		SimpleDateFormat yyyymm = new SimpleDateFormat("yyyy-MM");
		DecimalFormat nn = new DecimalFormat("00");
		
		String asOf = yyyymmdd.format(new Date());
		String startDate = this.startDate == null ? null : yyyymmdd.format(this.startDate.getTime());
		String endDate = this.endDate == null ? null : yyyymmdd.format(this.endDate.getTime());
		String monthYear = null;
		if ( this.month != null && this.year != null ) {
			monthYear = year + "-" + nn.format(this.month);
		}
	
		List<String> names = new ArrayList<String>();
		names.add(this.reportType.downloadFileName());		
		if ( reportType.reportJsp().equals(ReportJsp.reportNoInput)) {
			names.add("as of " + asOf);
		} else if ( reportType.reportJsp().equals(ReportJsp.reportByDiv)) {
			String div = makeDiv(conn, this.divisionId);
			names.add("for Div " + div);
			names.add("as of " + asOf);
		} else if ( reportType.reportJsp().equals(ReportJsp.reportByStartEnd)) {
			names.add("for " + startDate);
			names.add("to " + endDate);
			names.add("as of " + asOf);
		} else if ( reportType.reportJsp().equals(ReportJsp.reportByDivEnd)) {
			String div = makeDiv(conn, this.divisionId);
			names.add("for Div " + div);
			names.add("to " + endDate);
			names.add("as of " + asOf);
		} else if ( reportType.reportJsp().equals(ReportJsp.reportByDivMonthYear)) {
			String div = makeDiv(conn, this.divisionId);
			names.add("for Div " + div);
			names.add("for " + monthYear);
			names.add("as of " + asOf);
		} else if ( reportType.reportJsp().equals(ReportJsp.reportByDivStartEnd)) {
			String div = makeDiv(conn, this.divisionId);
			names.add("for Div " + div);
			names.add("for " + startDate);
			names.add("to " + endDate);
			names.add("as of " + asOf);
		} else {
			logger.log(Level.DEBUG, "No jsp match");
		}
		
		String reportFileName = StringUtils.join(names, " ");
		logger.log(Level.DEBUG, "Filename: " + reportFileName);
		// the attachment header sees "end of name" when it finds a space
		return reportFileName.replaceAll(" ", "_");
	}
	private String makeDiv(Connection conn, Integer divisionId) throws Exception {
		Division division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		return division.getDivisionDisplay();
	}
	/**
	 * Validate the current report definition against the validator class defined in the ReportType enum
	 * @param conn
	 * @return List of error messages. Empty list (not null) if no validation errors
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

	
}
