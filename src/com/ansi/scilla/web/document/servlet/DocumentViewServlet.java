package com.ansi.scilla.web.document.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;

import com.ansi.scilla.common.db.Document;
import com.ansi.scilla.common.document.DocumentType;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.document.util.DocumentPermission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;

public class DocumentViewServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "view";
	
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		Connection conn = null;
		try {
			AnsiURL url = null;
			try {
				url = new AnsiURL(request, REALM, (String[]) null);
			} catch ( Exception e ) {
				AppUtils.logException(e);
				throw e;
			}

			logger.log(Level.DEBUG, "doc id: " + url.getId());
			conn = AppUtils.getDBCPConn();
			conn.setAutoCommit(false);

			if ( url.getId() == null ) {
				throw new ResourceNotFoundException();
			}
			
			String sql = "select " + Document.XREF_TYPE + ", " + Document.XREF_ID + "," + Document.CONTENT + " from document where document_id=?";
			logger.log(Level.DEBUG, sql);
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, url.getId());
			ResultSet rs = ps.executeQuery();
			if ( rs.next()) {
				logger.log(Level.DEBUG, "found it");
				String xrefType = rs.getString(Document.XREF_TYPE);
				Integer xrefId = rs.getInt(Document.XREF_ID);
				String filepath = xrefType + "." + xrefId + ".pdf";
				Blob content = rs.getBlob(Document.CONTENT);
				
				DocumentType docType = DocumentType.valueOf(xrefType);
				Permission permission = DocumentPermission.get(docType);
				AppUtils.validateSession(request, permission);
				
				InputStream inputStream = content.getBinaryStream();
				int fileLength = inputStream.available();
				
				response.setContentType("application/pdf");
				response.setHeader("Content-Disposition","inline; filename='"+ filepath + "'");
				response.setContentLength(fileLength);
				
				OutputStream outStream = response.getOutputStream();
				byte[] buffer = new byte[4096];
				int bytesRead = -1;
				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outStream.write(buffer, 0, bytesRead);
				}
				inputStream.close();
				outStream.flush();
				outStream.close();	
			} else {
				logger.log(Level.DEBUG, "didn't found it");
				throw new ResourceNotFoundException();
			}
		} catch (ResourceNotFoundException e1) {
			super.sendNotFound(response);
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch ( Exception e ) {
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
	}

	
}
