package com.ansi.scilla.web.servlets.job;

import java.io.IOException;
import java.sql.Connection;
import java.util.Calendar;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.PermissionLevel;
import com.ansi.scilla.web.common.AnsiURL;
import com.ansi.scilla.web.common.AppUtils;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.common.ResponseCode;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.response.job.JobScheduleResponse;
import com.ansi.scilla.web.servlets.AbstractServlet;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class JobScheduleServlet extends AbstractServlet {

	private static final long serialVersionUID = 1L;

	public static final String REALM = "jobSchedule";
	
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		super.doDelete(request, response);
	}

	@Override
	/**
	 * Provides a list of scheduled job dates between the first of the current month and
	 * the end of eternity, inclusive
	 * 
	 * Errors: Not a valid job ID (returns 404)
	 *         Not a manually-scheduled job (returns 200 with message)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Connection conn = null;

		JobScheduleResponse jobDetailResponse = new JobScheduleResponse();
		
		try {
			Calendar theFirstOfTheMonth = Calendar.getInstance(TimeZone.getTimeZone("America/Chicago"));
			theFirstOfTheMonth.set(Calendar.DAY_OF_MONTH, 1);
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.JOB, PermissionLevel.PERMISSION_LEVEL_IS_READ);
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);	
			
			if( url.getId() == null || ! StringUtils.isBlank(url.getCommand())) {	
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				Job job = new Job();
				job.setJobId(url.getId());
				try {
					job.selectOne(conn);
					jobDetailResponse = new JobScheduleResponse(conn, url.getId(), theFirstOfTheMonth);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, jobDetailResponse);
				} catch ( RecordNotFoundException e) {
					throw new ResourceNotFoundException();
				}
			} else {
				// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
				throw new ResourceNotFoundException();
			}
		} catch (TimeoutException | NotAllowedException | ExpiredLoginException e) {
			super.sendForbidden(response);
		} catch(RecordNotFoundException | ResourceNotFoundException e) {
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
		
		throw new ServletException("Code this soon");
		
	}

	
	
	
}
