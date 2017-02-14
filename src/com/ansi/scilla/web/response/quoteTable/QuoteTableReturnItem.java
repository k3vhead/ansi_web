package com.ansi.scilla.web.response.quoteTable;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.ReportQuery;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.thewebthing.commons.db2.DBColumn;

public class QuoteTableReturnItem extends ReportQuery {
		private static final long serialVersionUID = 1L;

		public static final String QUOTE_ID = "quote_id";
		public static final String QUOTE_CODE ="quote_code";
		public static final String BILL_TO_NAME ="bill_to_name";
		public static final String JOB_SITE_NAME ="job_site_name";
		public static final String JOB_SITE_ADDRESS ="job_site_address";
		public static final String MANAGER_NAME ="manager_name";
		public static final String PROPOSAL_DATE ="proposal_date";
		public static final String QUOTE_JOB_COUNT ="quote_job_count";
		public static final String QUOTE_PPC_SUM ="quote_ppc_sum";
		public static final String BILL_TO_ADDRESS_ID = "bill_to_address_id";
		public static final String JOB_SITE_ADDRESS_ID = "job_site_address_id";
		public static final String DIVISION_ID = "division_id";
		public static final String DIVISION_NBR = "division_nbr";
		public static final String DIVISION_CODE = "division_code";

		
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
		private Integer billToAddressId;
		private Integer jobSiteAddressId;
		private Integer divisionId;
		private Integer divisionNbr;
		private String divisionCode;
		
		public QuoteTableReturnItem() throws SQLException {
			super();
		}
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
			this.billToAddressId = rs.getInt(BILL_TO_ADDRESS_ID);
			this.jobSiteAddressId = rs.getInt(JOB_SITE_ADDRESS_ID);
			this.divisionId = rs.getInt(DIVISION_ID);
			this.divisionNbr = rs.getInt(DIVISION_NBR);
			this.divisionCode = rs.getString(DIVISION_CODE);
			this.DT_RowId = rs.getInt("quote_id")+"";

		}

		@DBColumn(QUOTE_ID)
		public Integer getQuoteId() {
			return quoteId;
		}
		@DBColumn(QUOTE_ID)
		public void setQuoteId(Integer quoteId) {
			this.quoteId = quoteId;
		}
		
		public String getDT_RowId() {
			return DT_RowId;
		}

		public void setDT_RowId(String DT_RowId) {
			this.DT_RowId = DT_RowId;
		}
		@DBColumn(QUOTE_CODE)
		public String getQuoteCode() {
			return quoteCode;
		}
		@DBColumn(QUOTE_CODE)
		public void setQuoteCode(String quoteCode) {
			this.quoteCode = quoteCode;
		}
		@DBColumn(BILL_TO_NAME)
		public String getBillToName() {
			return billToName;
		}
		@DBColumn(BILL_TO_NAME)
		public void setBillToName(String billToName) {
			this.billToName = billToName;
		}
		@DBColumn(JOB_SITE_NAME)
		public String getJobSiteName() {
			return jobSiteName;
		}
		@DBColumn(JOB_SITE_NAME)
		public void setJobSiteName(String jobSiteName) {
			this.jobSiteName = jobSiteName;
		}
		@DBColumn(JOB_SITE_ADDRESS)
		public String getJobSiteAddress() {
			return jobSiteAddress;
		}
		@DBColumn(JOB_SITE_ADDRESS)
		public void setJobSiteAddress(String jobSiteAddress) {
			this.jobSiteAddress = jobSiteAddress;
		}
		@DBColumn(MANAGER_NAME)
		public String getManagerName() {
			return managerName;
		}
		@DBColumn(MANAGER_NAME)
		public void setManagerName(String managerName) {
			this.managerName = managerName;
		}
		@DBColumn(PROPOSAL_DATE)
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
		public Date getProposalDate() {
			return proposalDate;
		}
		@DBColumn(PROPOSAL_DATE)
		@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
		public void setProposalDate(Date proposalDate) {
			this.proposalDate = proposalDate;
		}
		@DBColumn(QUOTE_JOB_COUNT)
		public Integer getQuoteJobCount() {
			return quoteJobCount;
		}
		@DBColumn(QUOTE_JOB_COUNT)
		public void setQuoteJobCount(Integer quoteJobCount) {
			this.quoteJobCount = quoteJobCount;
		}
		@DBColumn(QUOTE_PPC_SUM)
		@JsonSerialize(using = AnsiCurrencyFormatter.class)
		public BigDecimal getQuotePpcSum() {
			return quotePpcSum;
		}
		@DBColumn(QUOTE_PPC_SUM)
		public void setQuotePpcSum(BigDecimal quotePpcSum) {
			this.quotePpcSum = quotePpcSum;
		}

		@DBColumn(BILL_TO_ADDRESS_ID)
		public Integer getBillToAddressId() {
			return billToAddressId;
		}

		@DBColumn(BILL_TO_ADDRESS_ID)
		public void setBillToAddressId(Integer billToAddressId) {
			this.billToAddressId = billToAddressId;
		}

		@DBColumn(JOB_SITE_ADDRESS_ID)
		public Integer getJobSiteAddressId() {
			return jobSiteAddressId;
		}

		@DBColumn(JOB_SITE_ADDRESS_ID)
		public void setJobSiteAddressId(Integer jobSiteAddressId) {
			this.jobSiteAddressId = jobSiteAddressId;
		}

		@DBColumn(DIVISION_ID)
		public Integer getDivisionId() {
			return divisionId;
		}

		@DBColumn(DIVISION_ID)
		public void setDivisionId(Integer divisionId) {
			this.divisionId = divisionId;
		}

		@DBColumn(DIVISION_NBR)
		public Integer getDivisionNbr() {
			return divisionNbr;
		}

		@DBColumn(DIVISION_NBR)
		public void setDivisionNbr(Integer divisionNbr) {
			this.divisionNbr = divisionNbr;
		}

		@DBColumn(DIVISION_CODE)
		public String getDivisionCode() {
			return divisionCode;
		}

		@DBColumn(DIVISION_CODE)
		public void setDivisionCode(String divisionCode) {
			this.divisionCode = divisionCode;
		}

	}