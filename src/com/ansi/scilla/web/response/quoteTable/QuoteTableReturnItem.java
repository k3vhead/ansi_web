package com.ansi.scilla.web.response.quoteTable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.ApplicationObject;
import com.fasterxml.jackson.annotation.JsonFormat;

public class QuoteTableReturnItem extends ApplicationObject {
		private static final long serialVersionUID = 1L;
		private Integer quoteId;
		private String quoteCode;
		private String billToName;
		private String jobSiteName;
		private String jobSiteAddress;
		private String managerName;
		private Date proposalDate;
		private Integer quoteJobCount;
		private BigDecimal quotePpcSum;
		private String DT_RowId;
		
		
		public QuoteTableReturnItem(ResultSet rs) throws SQLException {
			super();
			this.quoteId = rs.getInt("quote_id");
			this.quoteCode = rs.getString("quote_code");
			this.billToName = rs.getString("bill_to_name");
			this.jobSiteName = rs.getString("job_site_name");
			this.jobSiteAddress = rs.getString("job_site_address");
			this.managerName = rs.getString("manager_name");
			this.proposalDate = rs.getDate("proposal_date");
			this.quoteJobCount = rs.getInt("quote_job_count");
			this.quotePpcSum = rs.getBigDecimal("quote_ppc_sum");
			this.DT_RowId = rs.getInt("quote_id")+"";

		}

		public Integer getQuoteId() {
			return quoteId;
		}

		public void setQuoteId(Integer quoteId) {
			this.quoteId = quoteId;
		}
		
		public String getDT_RowId() {
			return DT_RowId;
		}

		public void setDT_RowId(String DT_RowId) {
			this.DT_RowId = DT_RowId;
		}

		public String getQuoteCode() {
			return quoteCode;
		}

		public void setQuoteCode(String quoteCode) {
			this.quoteCode = quoteCode;
		}

		public String getBillToName() {
			return billToName;
		}

		public void setBillToName(String billToName) {
			this.billToName = billToName;
		}

		public String getJobSiteName() {
			return jobSiteName;
		}

		public void setJobSiteName(String jobSiteName) {
			this.jobSiteName = jobSiteName;
		}

		public String getJobSiteAddress() {
			return jobSiteAddress;
		}

		public void setJobSiteAddress(String jobSiteAddress) {
			this.jobSiteAddress = jobSiteAddress;
		}

		public String getManagerName() {
			return managerName;
		}

		public void setManagerName(String managerName) {
			this.managerName = managerName;
		}
		
		public Date getProposalDate() {
			return proposalDate;
		}

		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}

		public Integer getQuoteJobCount() {
			return quoteJobCount;
		}

		public void setQuoteJobCount(Integer quoteJobCount) {
			this.quoteJobCount = quoteJobCount;
		}
		public BigDecimal getQuotePpcSum() {
			return quotePpcSum;
		}

		public void setQuotePpcSum(BigDecimal quotePpcSum) {
			this.quotePpcSum = quotePpcSum;
		}

	}