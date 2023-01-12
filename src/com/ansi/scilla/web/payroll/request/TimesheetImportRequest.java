package com.ansi.scilla.web.payroll.request;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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

import com.ansi.scilla.web.common.request.AbstractRequest;
import com.ansi.scilla.web.common.request.UploadParser;
import com.ansi.scilla.web.common.response.WebMessages;

public class TimesheetImportRequest extends AbstractRequest implements UploadParser {

	private static final long serialVersionUID = 1L;
	
	public static final String TIMESHEET_FILE = "timesheetFile";
	
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
					logger.log(Level.ERROR, "Unexpected field: " + item.getFieldName());
				} else {
					if ( item.getFieldName().equals( TIMESHEET_FILE )) {
						logger.log(Level.DEBUG, "Upload file made it to servlet: " + item.getFieldName());
						this.timesheetFile = item;
					} else {
						logger.log(Level.ERROR, "Unexpected file upload: " + item.getFieldName() + " = " + item.getName());
					}
					logger.log(Level.DEBUG, item.getContentType());	// for ods, expect:  application/vnd.oasis.opendocument.spreadsheet
					logger.log(Level.DEBUG, item.getName());  // this is the filename
				}
			}
		}
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

	
	public WebMessages validate(Connection conn) throws Exception {
		if ( this.timesheetFile == null ) {
			webMessages.addMessage(TIMESHEET_FILE, "Required Value");
		} else {
			String fileName = this.timesheetFile.getName();
			String extension = StringUtils.substringAfterLast(fileName, ".");
			if ( ! extension.equalsIgnoreCase("ods")) {
				webMessages.addMessage(TIMESHEET_FILE, "Invalid file format. Must be ODS");
			}
		}
		return webMessages;
	}
}
