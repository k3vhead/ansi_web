package com.ansi.scilla.web.job.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.common.jobticket.JobUtils;
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

	
	private JobHeader(ResultSet rs) throws Exception {
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
		this.canDelete = this.jobStatus.equals(JobStatus.NEW.code());
		this.canActivate = this.jobStatus.equals(JobStatus.PROPOSED.code());
		this.canCancel = this.jobStatus.equals(JobStatus.ACTIVE.code());
		JobStatus jobStatus = JobStatus.lookup(this.jobStatus);
		JobFrequency jobFrequency  = JobFrequency.get(this.jobFrequency);
		java.util.Date activationDate = new java.util.Date(rs.getDate("activation_date").getTime());
		java.util.Date startDate = new java.util.Date(rs.getDate("start_date").getTime());
		this.canSchedule = JobUtils.canReschedule(this.jobId, jobStatus, jobFrequency, activationDate, startDate);
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

	public static String getSql() {
		return sql;
	}

	@Override
	public int compareTo(JobHeader o) {
		return this.jobNbr.compareTo(o.getJobNbr());
	}

	/**
	 * Get basic job information for display on quote maintenance screen.
	 * @param conn
	 * @param quoteId
	 * @return
	 * @throws SQLException
	 */
	public static List<JobHeader> getJobHeaderList(Connection conn, Integer quoteId) throws Exception {
		List<JobHeader> jobHeaderList = new ArrayList<JobHeader>();
		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, quoteId);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			jobHeaderList.add(new JobHeader(rs));
		}
		rs.close();
		return jobHeaderList;
	}
}
