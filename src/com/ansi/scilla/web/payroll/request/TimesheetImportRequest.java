package com.ansi.scilla.web.payroll.request;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.RequestValidator;
import com.ansi.scilla.web.common.request.UploadParser;
import com.ansi.scilla.web.common.response.WebMessages;

public class TimesheetImportRequest extends AbstractRequest implements UploadParser {

	private static final long serialVersionUID = 1L;
	
	public static final String DIVISION_ID = "divisionId";
	public static final String PAYROLL_DATE = "payrollDate";
	public static final String STATE = "state";
	public static final String CITY = "city";
	public static final String TIMESHEET_FILE = "timesheetFile";
	
	protected final SimpleDateFormat payrollDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	private Integer divisionId;
	private Calendar payrollDate;
	private String state;
	private String city;
	private FileItem timesheetFile;
	private Logger logger = LogManager.getLogger(TimesheetImportRequest.class);
	private WebMessages webMessages = new WebMessages();
	
	public TimesheetImportRequest(HttpServletRequest request) throws FileUploadException, IOException {
		super();
		DiskFileItemFactory factory = new DiskFileItemFactory();		
		factory.setRepository(new File("/tmp"));						
		ServletFileUpload upload = new ServletFileUpload(factory);		
		
		@SuppressWarnings("unchecked")
		List<FileItem> formItems = upload.parseRequest(request);		
		if ( formItems != null && formItems.size() > 0 ) {				
			for ( FileItem item : formItems ) {							
				logger.log(Level.DEBUG, item.getFieldName());			
				if ( item.isFormField() ) {
					switch ( item.getFieldName() ) {
					/*
					case DIVISION_ID:
						this.divisionId = makeInteger(item);
						break;
					case PAYROLL_DATE:
						try {
							this.payrollDate = makeCalendar(item, payrollDateFormat);
						} catch (ParseException e) {
							webMessages.addMessage(PAYROLL_DATE, "Invalid Date");
						}
						break;
					case STATE:
						this.state = makeString(item);
						break;
					case CITY:
						this.city = makeString(item);
						break;
					*/
					default:
						logger.log(Level.ERROR, "Unexpected field: " + item.getFieldName());
						break;
					}
				} else {
					switch ( item.getFieldName() ) {
					case TIMESHEET_FILE:
						logger.log(Level.DEBUG, "Upload file made it to servlet: " + item.getFieldName());
						this.timesheetFile = item;
						break;
					default:
						logger.log(Level.ERROR, "Unexpected file upload: " + item.getFieldName() + " = " + item.getName());
						break;
					}
					logger.log(Level.DEBUG, item.getContentType());	// for ods, expect:  application/vnd.oasis.opendocument.spreadsheet
					logger.log(Level.DEBUG, item.getName());  // this is the filename
						

//					CSVReader reader = new CSVReader(new InputStreamReader(item.getInputStream()));		
//					List<String[]> recordList = reader.readAll();										
//					recordList.remove(0);								
//					reader.close();
//					
//					for ( int i = 0; i < 5; i++ ) {						
//						EmployeeRecord rec = new EmployeeRecord(recordList.get(i));
//						logger.log(Level.DEBUG,rec);					
//					}
				}
			}
		}
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public Calendar getPayrollDate() {
		return payrollDate;
	}

	public void setPayrollDate(Calendar payrollDate) {
		this.payrollDate = payrollDate;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public FileItem getTimesheetFile() {
		return timesheetFile;
	}

	public void setTimesheetFile(FileItem timesheetFile) {
		this.timesheetFile = timesheetFile;
	}

	public WebMessages getWebMessages() {
		return webMessages;
	}

	public void setWebMessages(WebMessages webMessages) {
		this.webMessages = webMessages;
	}

	public SimpleDateFormat getPayrollDateFormat() {
		return payrollDateFormat;
	}
	
	public WebMessages validate(Connection conn) throws Exception {
		RequestValidator.validateId(conn, webMessages, Division.TABLE, Division.DIVISION_ID, DIVISION_ID, this.divisionId, true);
		if ( ! webMessages.containsKey(PAYROLL_DATE)) {
			RequestValidator.validateDay(webMessages, PAYROLL_DATE, this.payrollDate, true, null, null, Calendar.FRIDAY);
		}
		//RequestValidator.validateState(webMessages, STATE, this.state, true, null);
		//RequestValidator.validateString(webMessages, CITY, this.city, false);
		if ( this.timesheetFile == null ) {
			webMessages.addMessage(TIMESHEET_FILE, "Required Value");
		} else {
			String fileName = this.timesheetFile.getName();
			String extension = StringUtils.substringAfterLast(fileName, ".");
			if ( ! extension.equalsIgnoreCase("ods")) {
				webMessages.addMessage(TIMESHEET_FILE, "Invalid file format. Must be ODS");
			}
		}
		logger.log(Level.DEBUG, "From Validate: " + webMessages);
		return webMessages;
	}
}
