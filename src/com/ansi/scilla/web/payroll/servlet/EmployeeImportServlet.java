package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.payroll.request.EmployeeImportRequest;
import com.ansi.scilla.web.payroll.request.EmployeeRequest;
import com.ansi.scilla.web.payroll.response.EmployeeImportResponse;

public class EmployeeImportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/employeeImport";
	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "Employee Import post");

		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			
			SessionData sessionData = AppUtils.validateSession(request, Permission.PAYROLL_WRITE);
			EmployeeImportRequest uploadRequest = new EmployeeImportRequest(request);
			ResponseCode responseCode = null;
			WebMessages webMessages = uploadRequest.validate(conn);
			EmployeeImportResponse data = new EmployeeImportResponse();
			data.setWebMessages(webMessages);
			
			if ( webMessages.isEmpty() ) {
				data = new EmployeeImportResponse(conn, uploadRequest);
				responseCode = ResponseCode.SUCCESS;
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}

			super.sendResponse(conn, response, responseCode, data);

			conn.close();
		}
		
		/*try {
			AppUtils.validateSession(request, Permission.CLAIMS_WRITE);  //make sure we're allowed to do this
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
		}*/ 
		catch (TimeoutException | NotAllowedException | ExpiredLoginException e1) {
			super.sendForbidden(response);
//		} catch ( FileUploadException e ) {
//			throw new ServletException(e);
		}	catch (Exception e) {
			throw new ServletException(e);
		}
		
		

		Enumeration<String> parmNames =  request.getParameterNames();
		while ( parmNames.hasMoreElements() ) {
			String parmName = parmNames.nextElement();
			logger.log(Level.DEBUG, parmName);
		}
	}

	

	

}
