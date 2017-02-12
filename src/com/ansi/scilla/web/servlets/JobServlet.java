package com.ansi.scilla.web.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.response.job.JobDetailResponse;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "job";
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doDelete(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		JobDetailResponse jobDetailResponse = new JobDetailResponse();
		
		try {
			conn = AppUtils.getDBCPConn();
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);	
			
			if( url.getId() == null || ! StringUtils.isBlank(url.getCommand())) {	
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				jobDetailResponse = new JobDetailResponse(conn, url.getId());
				super.sendResponse(conn, response, ResponseCode.SUCCESS, jobDetailResponse);
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
		} catch(RecordNotFoundException e) {
			super.sendNotFound(response);
		} catch(ResourceNotFoundException e) {
			super.sendNotFound(response);
		} catch ( Exception e) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			AppUtils.closeQuiet(conn);
		}
		
	}


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doPost(request, response);
	}

	
}
