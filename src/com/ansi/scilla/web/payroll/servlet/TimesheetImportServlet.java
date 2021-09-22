package com.ansi.scilla.web.payroll.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.payroll.request.TimesheetImportRequest;
import com.ansi.scilla.web.payroll.response.TimesheetImportResponse;


public class TimesheetImportServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;
	public static final String REALM = "payroll/timesheetImport";
	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		logger.log(Level.DEBUG, "Employee Import post");

		Connection conn = null;
		try {
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);
			TimesheetImportRequest uploadRequest = new TimesheetImportRequest(request);
			ResponseCode responseCode = null;
			WebMessages webMessages = uploadRequest.validate(conn);
			TimesheetImportResponse data = new TimesheetImportResponse();
			data.setWebMessages(webMessages);
			
			if ( webMessages.isEmpty() ) {
				data = new TimesheetImportResponse(conn, uploadRequest);
				responseCode = ResponseCode.SUCCESS;
			} else {
				responseCode = ResponseCode.EDIT_FAILURE;
			}

			super.sendResponse(conn, response, responseCode, data);
//			DiskFileItemFactory factory = new DiskFileItemFactory();		
//			factory.setRepository(new File("/tmp"));						
//			ServletFileUpload upload = new ServletFileUpload(factory);		
//	
//			@SuppressWarnings("unchecked")
//			List<FileItem> formItems = upload.parseRequest(request);		
//			if ( formItems != null && formItems.size() > 0 ) {				
//				for ( FileItem item : formItems ) {							
//					logger.log(Level.DEBUG, item.getFieldName());			
//					if ( item.isFormField() ) {
//						String value = item.getString();					
//						logger.log(Level.DEBUG, value);						
//					} else {
//						logger.log(Level.DEBUG, item.getContentType());		
//						logger.log(Level.DEBUG, item.getName());
//						CSVReader reader = new CSVReader(new InputStreamReader(item.getInputStream()));		
//						List<String[]> recordList = reader.readAll();										
//						recordList.remove(0);								
//						reader.close();
//						
//						for ( int i = 0; i < 5; i++ ) {						
//							EmployeeRecord rec = new EmployeeRecord(recordList.get(i));
//							logger.log(Level.DEBUG,rec);					
//						}
//					}
//				}
//			}
			conn.close();
		} catch ( FileUploadException e ) {
			throw new ServletException(e);
		} catch (NamingException e) {
			throw new ServletException(e);
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
		

		Enumeration<String> parmNames =  request.getParameterNames();
		while ( parmNames.hasMoreElements() ) {
			String parmName = parmNames.nextElement();
			logger.log(Level.DEBUG, parmName);
		}
	}

	

	

}
