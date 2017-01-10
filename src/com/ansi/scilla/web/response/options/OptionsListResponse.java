package com.ansi.scilla.web.response.options;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import com.ansi.scilla.common.address.Country;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.common.Permission;
import com.ansi.scilla.web.response.MessageResponse;

public class OptionsListResponse extends MessageResponse {
	private static final long serialVersionUID = 1L;
	private List<JobFrequencyOption> jobFrequency;
	private List<JobStatusOption> jobStatus;
	private List<TicketStatusOption> ticketStatus;
	private List<CountryOption> country;
	private List<Permission> permission;

	public OptionsListResponse(List<ResponseOption> options) throws ClassNotFoundException, Exception {
		if ( options.contains(ResponseOption.JOB_FREQUENCY)) {
			makeJobFrequencyList();
		}
		if ( options.contains(ResponseOption.JOB_STATUS)) {
			makeJobStatusList();
		}
		if ( options.contains(ResponseOption.TICKET_STATUS)) {
			makeTicketStatusList();
		}
		if ( options.contains(ResponseOption.COUNTRY)) {
			makeCountryList();
		}
		if ( options.contains(ResponseOption.PERMISSION)) {
			makePermissionList();
		}

	}

	private void makePermissionList() {
		this.permission = new ArrayList<Permission>();
		for(Permission j : EnumSet.allOf(Permission.class)) {
			this.permission.add(j);
		}
	}

	private void makeJobFrequencyList() {
		this.jobFrequency = new ArrayList<JobFrequencyOption>();
		for(JobFrequency j : EnumSet.allOf(JobFrequency.class)) {
			this.jobFrequency.add(new JobFrequencyOption(j));
		}
	}
	
	private void makeJobStatusList() {
		this.jobStatus = new ArrayList<JobStatusOption>();
		for(JobStatus j : EnumSet.allOf(JobStatus.class)) {
			this.jobStatus.add(new JobStatusOption(j));
		}
	}
	
	private void makeTicketStatusList() {
		this.ticketStatus = new ArrayList<TicketStatusOption>();
		for(TicketStatus j : EnumSet.allOf(TicketStatus.class)) {
			this.ticketStatus.add(new TicketStatusOption(j));
		}
	}
	
	private void makeCountryList() throws IOException {
		this.country = new ArrayList<CountryOption>();
		
		for(Country j : EnumSet.allOf(Country.class)) {
			this.country.add(new CountryOption(j));
		}
	}
		
	public List<JobFrequencyOption> getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(List<JobFrequencyOption> jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public List<JobStatusOption> getJobStatus() {
		return jobStatus;
	}
	
	public void setJobStatus(List<JobStatusOption> jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public List<CountryOption> getCountry() {
		return country;
	}
	
	public void setCountry(List<CountryOption> country) {
		this.country = country;
	}
	
	public List<TicketStatusOption> getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(List<TicketStatusOption> ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public List<Permission> getPermission() {
		return permission;
	}

	public void setPermission(List<Permission> permission) {
		this.permission = permission;
	}
	
}
