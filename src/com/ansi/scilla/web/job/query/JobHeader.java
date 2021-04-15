package com.ansi.scilla.web.job.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Quote;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.common.jobticket.JobUtils;
import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.common.utils.UserPermission;
import com.ansi.scilla.web.exceptions.NotAllowedException;
import com.thewebthing.commons.lang.StringUtils;

public class JobHeader extends ApplicationObject implements Comparable<JobHeader> {

	private static final long serialVersionUID = 1L;
	private static final Integer descriptionLength = 50;
	
	private static final String sql = "select job.job_id, " +  
		"\n\t job.job_nbr,  " +
		"\n\t job.service_description, " + 
		"\n\t job.division_id,  " +
		"\n\t job.job_status, " +
		"\n\t job.job_frequency, " +
		"\n\t job.price_per_cleaning, " + 
		"\n\t job.activation_date, " +
		"\n\t job.start_date, " +
		"\n\t division.division_nbr,  " +
		"\n\t division.division_code " +
		"\n from job " +
		"\n inner join division on division.division_id=job.division_id " +
		"\n where job.quote_id=? " +
		"\n order by job.job_nbr";
	
	
	private Integer jobId;
	private Integer jobNbr;
	private String abbrDescription;
	private Integer divisionId;
	private String divisionCode;
	private Integer divisionNbr;
	private String jobStatus;
	private String jobFrequency;
	private Double pricePerCleaning;
	private Boolean canDelete;
	private Boolean canActivate;
	private Boolean canCancel;
	private Boolean canSchedule;
	private Boolean canEdit;

	
	private JobHeader(ResultSet rs, Quote quote, List<UserPermission> permissionList) throws Exception {
		Logger logger = LogManager.getLogger(this.getClass());
		this.jobId = rs.getInt("job_id");
		this.jobNbr = rs.getInt("job_nbr");
		String description = rs.getString("service_description");
		this.abbrDescription = StringUtils.abbreviate(description, descriptionLength);
		this.divisionId = rs.getInt("division_id");
		this.divisionCode = rs.getString("division_code");
		this.divisionNbr = rs.getInt("division_nbr");
		this.jobStatus = rs.getString("job_status");
		this.jobFrequency = rs.getString("job_frequency");
		this.pricePerCleaning = rs.getBigDecimal("price_per_cleaning").doubleValue();
		
		try {
			validateStateTransition(quote, permissionList);
			logger.log(Level.DEBUG, "User has permission to change job");
			this.canDelete = JobUtils.canDeleteJob(this.jobStatus);
			this.canActivate = JobUtils.canActivateJob(this.jobStatus);
			this.canCancel = JobUtils.canCancelJob(this.jobStatus);
			this.canEdit = true;
			
			JobStatus jobStatus = JobStatus.lookup(this.jobStatus);
			JobFrequency jobFrequency  = JobFrequency.lookup(this.jobFrequency);
			Object activationDateRS = rs.getObject("activation_date");
			Object startDateRS = rs.getObject("start_date");
			if ( activationDateRS != null && startDateRS != null ) {
				java.sql.Date activationDateRsDate = rs.getDate("activation_date");
				java.util.Date activationDate = new java.util.Date( activationDateRsDate.getTime() );
				java.sql.Date startDateRsDate = rs.getDate("start_date");
				java.util.Date startDate = new java.util.Date( startDateRsDate.getTime() );
				this.canSchedule = JobUtils.canReschedule(this.jobId, jobStatus, jobFrequency, activationDate, startDate);
			} else {
				this.canSchedule = false;
			}
		} catch ( NotAllowedException e) {
			logger.log(Level.DEBUG, "User does not have permission to change job");
			this.canDelete = false;
			this.canActivate = false;
			this.canCancel = false;
			this.canSchedule = false;
			this.canEdit = false;
		}
		
		
	}
	
	public Integer getJobId() {
		return jobId;
	}

	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}

	public Integer getJobNbr() {
		return jobNbr;
	}

	public void setJobNbr(Integer jobNbr) {
		this.jobNbr = jobNbr;
	}

	public String getAbbrDescription() {
		return abbrDescription;
	}

	public void setAbbrDescription(String abbrDescription) {
		this.abbrDescription = abbrDescription;
	}

	public Integer getDivisionId() {
		return divisionId;
	}

	public void setDivisionId(Integer divisionId) {
		this.divisionId = divisionId;
	}

	public String getDivisionCode() {
		return divisionCode;
	}

	public void setDivisionCode(String divisionCode) {
		this.divisionCode = divisionCode;
	}

	public Integer getDivisionNbr() {
		return divisionNbr;
	}

	public void setDivisionNbr(Integer divisionNbr) {
		this.divisionNbr = divisionNbr;
	}

	public String getJobStatus() {
		return jobStatus;
	}

	public void setJobStatus(String jobStatus) {
		this.jobStatus = jobStatus;
	}

	public String getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(String jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public Double getPricePerCleaning() {
		return pricePerCleaning;
	}

	public void setPricePerCleaning(Double pricePerCleaning) {
		this.pricePerCleaning = pricePerCleaning;
	}

	public Boolean getCanDelete() {
		return canDelete;
	}

	public void setCanDelete(Boolean canDelete) {
		this.canDelete = canDelete;
	}

	public Boolean getCanActivate() {
		return canActivate;
	}

	public void setCanActivate(Boolean canActivate) {
		this.canActivate = canActivate;
	}

	public Boolean getCanCancel() {
		return canCancel;
	}

	public void setCanCancel(Boolean canCancel) {
		this.canCancel = canCancel;
	}

	public Boolean getCanSchedule() {
		return canSchedule;
	}

	public void setCanSchedule(Boolean canSchedule) {
		this.canSchedule = canSchedule;
	}

	public Boolean getCanEdit() {
		return canEdit;
	}

	public void setCanEdit(Boolean canEdit) {
		this.canEdit = canEdit;
	}

	@Override
	public int compareTo(JobHeader o) {
		return this.jobNbr.compareTo(o.getJobNbr());
	}

	private void validateStateTransition(Quote quote, List<UserPermission> permissionList) throws NotAllowedException {
		if ( quote.getProposalDate() == null ) {
			AppUtils.checkPermission(Permission.QUOTE_CREATE, permissionList);
		} else {
			AppUtils.checkPermission(Permission.QUOTE_UPDATE, permissionList);
		}
	}

	public static String getSql() {
		return sql;
	}

	/**
	 * Get basic job information for display on quote maintenance screen.
	 * @param conn
	 * @param quoteId
	 * @param permissionList 
	 * @return
	 * @throws SQLException
	 */
	public static List<JobHeader> getJobHeaderList(Connection conn, Quote quote, List<UserPermission> permissionList) throws Exception {
		Integer quoteId = quote.getQuoteId();
		List<JobHeader> jobHeaderList = new ArrayList<JobHeader>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, quoteId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			jobHeaderList.add(new JobHeader(rs, quote, permissionList));
		}
		rs.close();
		return jobHeaderList;
	}
}
