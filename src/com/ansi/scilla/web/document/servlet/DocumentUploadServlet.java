package com.ansi.scilla.web.document.servlet;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.logging.log4j.Level;

import com.ansi.scilla.web.common.servlet.AbstractServlet;

public class DocumentUploadServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;


	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if ( !ServletFileUpload.isMultipartContent(request) ) {
			request.setAttribute("ansi_servlet_message","Not a MultiPart");
			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
			return;
		}
		
		
		DiskFileItemFactory factory = new DiskFileItemFactory();
		factory.setRepository(new File("/tmp"));
		ServletFileUpload upload = new ServletFileUpload(factory);

		try {
			List<FileItem> formItems = upload.parseRequest(request);
			if ( formItems != null && formItems.size() > 0 ) {
				for ( FileItem item : formItems ) {
					if ( ! item.isFormField()) {
						String fileName = new File(item.getName()).getName();
						String filePath = "/tmp/" + fileName;
						File storeFile = new File(filePath);
						item.write(storeFile);
						request.setAttribute("ansi_servlet_message",fileName + " uploaded");
						getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
					}
				}
			}
		} catch ( Exception e) {
			request.setAttribute("ansi_servlet_message",e.getMessage());
			getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
		}
	}

	protected void doX(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		logger.log(Level.DEBUG, request.getContentType());
		Enumeration<String> parameterNames = request.getParameterNames();
		while ( parameterNames.hasMoreElements() ) {
			String p = parameterNames.nextElement();
			logger.log(Level.DEBUG, "parameter: " + p);
			logger.log(Level.DEBUG, request.getParameter(p));
		}
		Enumeration<String> attributeNames = request.getAttributeNames();
		while ( attributeNames.hasMoreElements() ) {
			String p = attributeNames.nextElement();
			logger.log(Level.DEBUG, "attribute: " + p);
			logger.log(Level.DEBUG, request.getAttribute(p));
		}

		
		for ( Part part : request.getParts() ) {
			String fileName = extractFileName(part);
			logger.log(Level.DEBUG, "File: " + fileName);
			// write to file:
			String filePath = new File(fileName).getName();
			part.write(filePath);
			// write to DB		
			// ???
		}
		
		
		request.setAttribute("ansi_servlet_message","Success");
		getServletContext().getRequestDispatcher("/message.jsp").forward(request, response);
	}

	private String extractFileName(Part part) {
		String contentDisposition = part.getHeader("content-disposition");
		String[] items = contentDisposition.split(";");
		String fileName = "";
		for ( String s : items ) {
			if ( s.trim().startsWith("filename")) {
				fileName = s.substring(s.indexOf("=")+2, s.length()-1);
			}
		}
		return fileName;
	}
	
	

	
}
