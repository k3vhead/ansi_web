package com.ansi.scilla.web.job.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Transformer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.Midnight;
import com.ansi.scilla.common.db.Job;
import com.ansi.scilla.common.db.JobSchedule;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.TicketDateGenerator;
import com.ansi.scilla.web.common.response.ResponseCode;
import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.common.servlet.AbstractServlet;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AnsiURL;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.common.utils.Permission;
import com.ansi.scilla.web.exceptions.ExpiredLoginException;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.ansi.scilla.web.exceptions.ResourceNotFoundException;
import com.ansi.scilla.web.exceptions.TimeoutException;
import com.ansi.scilla.web.job.request.JobScheduleRequest;
import com.ansi.scilla.web.job.response.JobScheduleResponse;
import com.ansi.scilla.web.job.response.JobScheduleResponseItem;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.thewebthing.commons.db2.RecordNotFoundException;
import com.thewebthing.commons.lang.CalendarUtils;

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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;

		JobScheduleResponse jobScheduleResponse = new JobScheduleResponse();
		
		try {
			Calendar theFirstOfTheMonth = Calendar.getInstance(new AnsiTime());
			theFirstOfTheMonth.set(Calendar.DAY_OF_MONTH, 1);
			conn = AppUtils.getDBCPConn();
			AppUtils.validateSession(request, Permission.QUOTE_READ);
			AnsiURL url = new AnsiURL(request, REALM, (String[])null);	
			
			if( url.getId() == null || ! StringUtils.isBlank(url.getCommand())) {	
				throw new ResourceNotFoundException();
			} else if (url.getId() != null) {
				Job job = new Job();
				job.setJobId(url.getId());
				try {
					job.selectOne(conn);
					jobScheduleResponse = new JobScheduleResponse(conn, url.getId(), theFirstOfTheMonth);
					super.sendResponse(conn, response, ResponseCode.SUCCESS, jobScheduleResponse);
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
		
		Connection conn = null;

		JobScheduleResponse jobScheduleResponse = new JobScheduleResponse();
		
		try {
			Calendar theFirstOfTheMonth = Calendar.getInstance(new AnsiTime());
			theFirstOfTheMonth.set(Calendar.DAY_OF_MONTH, 1);
			conn = AppUtils.getDBCPConn();
			SessionData sessionData = AppUtils.validateSession(request, Permission.QUOTE_UPDATE);
			
			try {
				String jsonString = super.makeJsonString(request);
				JobScheduleRequest jobScheduleRequest = new JobScheduleRequest();
				AppUtils.json2object(jsonString, jobScheduleRequest);
				AnsiURL url = new AnsiURL(request, REALM, (String[])null);	
				
				
				if( url.getId() == null || ! StringUtils.isBlank(url.getCommand())) {	
					throw new ResourceNotFoundException();
				} else if (url.getId() != null) {
					WebMessages webMessages = new WebMessages();
					ResponseCode responseCode = null;
					try {
						Calendar displayDate = new Midnight(new AnsiTime());
						displayDate.set(Calendar.DAY_OF_MONTH, 1);
						Job job = new Job();
						job.setJobId(url.getId());
						job.selectOne(conn);
						jobScheduleResponse = new JobScheduleResponse(conn, url.getId(), displayDate);						
						DateAction dateAction = insertOrDelete(conn, jobScheduleRequest, jobScheduleResponse);						
						List<String> messageList = validateInput(conn, job, jobScheduleRequest, jobScheduleResponse, dateAction);
						
						if ( messageList.isEmpty() ) {
							try {
								if ( dateAction.equals(DateAction.INSERT)) {
									doInsert(conn, url.getId(), jobScheduleRequest.getJobDate(), sessionData.getUser());
								}
								if ( dateAction.equals(DateAction.DELETE)) {
									doDelete(conn, url.getId(), jobScheduleRequest.getJobDate(), jobScheduleResponse);
								}
								webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Success!");
								responseCode = ResponseCode.SUCCESS;
							} catch ( Exception e) {
								webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "System Error: " + e.getLocalizedMessage());
								responseCode = ResponseCode.EDIT_FAILURE;
							}
						} else {
							for ( String message : messageList ) {
								webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, message);
							}
							responseCode = ResponseCode.EDIT_FAILURE;
						}
						jobScheduleResponse.setWebMessages(webMessages);
						super.sendResponse(conn, response, responseCode, jobScheduleResponse);
					} catch ( DateAlreadyScheduledException e ) {
						webMessages.addMessage(WebMessages.GLOBAL_MESSAGE, "Ticket has already been dispatched");
						responseCode = ResponseCode.EDIT_FAILURE;
						jobScheduleResponse.setWebMessages(webMessages);
						super.sendResponse(conn, response, responseCode, jobScheduleResponse);
					} catch ( RecordNotFoundException e) {
						throw new ResourceNotFoundException();
					}
				} else {
					// according to the URI parsing, this shouldn't happen, but it gives me warm fuzzies
					throw new ResourceNotFoundException();
				}
			} catch ( InvalidFormatException e ) {
				String badField = super.findBadField(e.toString());
				WebMessages messages = new WebMessages();
				messages.addMessage(badField, "Invalid Format");
				jobScheduleResponse.setWebMessages(messages);
				super.sendResponse(conn, response, ResponseCode.EDIT_FAILURE, jobScheduleResponse);
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

	private DateAction insertOrDelete(Connection conn, JobScheduleRequest jobScheduleRequest, JobScheduleResponse jobScheduleResponse) throws DateAlreadyScheduledException {
		DateAction dateAction = DateAction.INSERT; // we're adding a date (default)
		for ( JobScheduleResponseItem item : jobScheduleResponse.getTicketList() ) {
			if ( DateUtils.isSameDay(jobScheduleRequest.getJobDate().getTime(), item.getStartDate()) ) {
				dateAction = DateAction.DELETE; // nope we're deleting a date
				if ( item.getTicketId() != null ) {
					throw new DateAlreadyScheduledException();
				}
			}
		}
		return dateAction;
	}

	private void doInsert(Connection conn, Integer jobId, Calendar jobDate, SessionUser user) throws Exception {
			Calendar today = Calendar.getInstance(new AnsiTime());
			JobSchedule jobSchedule = new JobSchedule();
			jobSchedule.setAddedBy(user.getUserId());
			jobSchedule.setAddedDate(today.getTime());
			jobSchedule.setEndDate(jobDate.getTime());
			jobSchedule.setJobId(jobId);
			jobSchedule.setManual(JobSchedule.MANUAL_IS_TRUE);
			jobSchedule.setStartDate(jobDate.getTime());
			//jobSchedule.setTicketId(ticketId);    Don't have one of these yet
			jobSchedule.setUpdatedBy(user.getUserId());
			jobSchedule.setUpdatedDate(today.getTime());
			jobSchedule.insertWithKey(conn);
	}
	
	private void doDelete(Connection conn, Integer jobId, Calendar jobDate, JobScheduleResponse jobScheduleResponse ) throws Exception {
		Date keyDate = null;
		for ( JobScheduleResponseItem item : jobScheduleResponse.getTicketList() ) {
			if ( CalendarUtils.sameDay(jobDate.getTime(), item.getStartDate())) {
				keyDate = item.getStartDate();
			}
		}
		JobSchedule jobSchedule = new JobSchedule();
		jobSchedule.setJobId(jobId);
		jobSchedule.setStartDate(keyDate);
		jobSchedule.delete(conn);
	}

	/**
	 * 	if toggle on:
	 *	 		validate the date (not in the past; not too many for job frequency; too far in the future?)
	 *	 if toggle off:
	 *			validate the date (not in the past)
	 *
	 * @param conn
	 * @param job
	 * @param jobScheduleRequest
	 * @param jobScheduleResponse
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<String> validateInput(Connection conn, Job job, JobScheduleRequest jobScheduleRequest, JobScheduleResponse jobScheduleResponse, DateAction dateAction) throws Exception {
		List<String> messageList = new ArrayList<String>();
		List<String> badFields = super.validateRequiredUpdateFields(jobScheduleRequest);
		if ( badFields != null && badFields.isEmpty() ) {
			messageList = (List<String>) CollectionUtils.collect(badFields, new MissingFieldTransformer());
		}
		if ( messageList.isEmpty() ) {
			validateFutureDate(jobScheduleRequest.getJobDate(), messageList);
		}
		if ( messageList.isEmpty() ) {
			if ( dateAction.equals(DateAction.INSERT) ) {
				validateNumberOfDates(conn, job, jobScheduleRequest, jobScheduleResponse.getTicketList(), messageList);
			}
		}
		return messageList;
	}


	private void validateNumberOfDates(Connection conn, Job job, JobScheduleRequest jobScheduleRequest, List<JobScheduleResponseItem> currentTicketList, List<String> messageList) throws Exception {
		boolean tooManyDates = false;
		JobFrequency jobFrequency = JobFrequency.get(job.getJobFrequency());
		Midnight startDate = Midnight.getInstance(new AnsiTime());
		startDate.set(Calendar.DAY_OF_MONTH, 1);
		Midnight endDate = (Midnight)startDate.clone();
		endDate.add(Calendar.MONTH, 12);
		Integer allowedNumber = TicketDateGenerator.generateTicketDates(jobFrequency, startDate, endDate).size();
		Integer currentNumberOfDates = 0;
		for ( JobScheduleResponseItem item :currentTicketList ) {
			if ( item.getStartDate().before(endDate.getTime())) {
				currentNumberOfDates++;
			}
		}
		tooManyDates = currentNumberOfDates + 1 > allowedNumber;
		if ( tooManyDates ) {
			messageList.add("Only " + allowedNumber + " dates can be selected for a " + jobFrequency.display() + " job");
		}
	}

	private void validateFutureDate(Calendar jobDate, List<String> messageList) {
		Midnight today = Midnight.getInstance(new AnsiTime());
		if ( jobDate.before(today) ) {
			messageList.add("Cannot schedule a date in the past");
		}
	}

	

	private class MissingFieldTransformer implements Transformer {
		@Override
		public Object transform(Object arg0) {
			String fieldName = (String)arg0;
			return "Missing required field: " + fieldName;
		}
	}

	private class DateAlreadyScheduledException extends Exception {
		private static final long serialVersionUID = 1L;
	}
	
	private enum DateAction {
		INSERT,
		DELETE
	}
}
