package com.ansi.scilla.web.payroll.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.payroll.common.EmployeeRecord;

import au.com.bytecode.opencsv.CSVReader;

public class EmployeeImportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/employeeImport";
	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "Employee Import post");

		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();		// this is a utility that we'll use to parse the input into objects
			factory.setRepository(new File("/tmp"));						// that tell us everything we need to know. Somebody else has done
			ServletFileUpload upload = new ServletFileUpload(factory);		// the work, so we don't need to recreate it.
	
			@SuppressWarnings("unchecked")
			List<FileItem> formItems = upload.parseRequest(request);		// here's the parsing.
			if ( formItems != null && formItems.size() > 0 ) {				// if we have some sort of input:
				for ( FileItem item : formItems ) {							// We don't know how much we have, so loop through them all
					logger.log(Level.DEBUG, item.getFieldName());			
					if ( item.isFormField() ) {
						String value = item.getString();					// if this is a regular field (not a file upload), process it differently
						logger.log(Level.DEBUG, value);						// get the value of the input. It will always be a string, but we can cast it to whatever makes sense
					} else {
						logger.log(Level.DEBUG, item.getContentType());		// get some info about the file we uploaded.
						logger.log(Level.DEBUG, item.getName());
						CSVReader reader = new CSVReader(new InputStreamReader(item.getInputStream()));		// this is where we start stealing stuff from the test code.
						List<String[]> recordList = reader.readAll();										// we're reading from an input stream instead of a file, but all else is the same
						recordList.remove(0);								// get rid of the column-header record
						reader.close();
						
						for ( int i = 0; i < 5; i++ ) {						// this is just proof-of-concept, so make sure we're getting the data that we expect
							EmployeeRecord rec = new EmployeeRecord(recordList.get(i));
							logger.log(Level.DEBUG,rec);					// this bit will be replaced with validation, and creating the response that we'll send back to the client
						}
					}
				}
			}
		} catch ( FileUploadException e ) {
			throw new ServletException(e);
		}
		
		

		Enumeration<String> parmNames =  request.getParameterNames();
		while ( parmNames.hasMoreElements() ) {
			String parmName = parmNames.nextElement();
			logger.log(Level.DEBUG, parmName);
		}
	}

	

	

}
