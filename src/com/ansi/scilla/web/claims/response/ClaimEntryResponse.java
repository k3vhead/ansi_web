package com.ansi.scilla.web.claims.response;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.web.claims.query.ClaimDetailLookupQuery;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.utils.ColumnFilter;

public class ClaimEntryResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;

	private Integer ticketId;
	private Integer jobId;			
//	private Integer jobSiteAddressId;
	private String jobSiteAddress;
	private String ticketStatus;
	private String ticketStatusDesc;
	private Double totalVolume;
	private Double claimedVolume;
	private Double availableVolume;
	private Double budget;
	private Double claimedDirectLaborAmt;
	private Double availableDirectLabor;
	private String omNotes;	
	
	public ClaimEntryResponse(Connection conn, Integer ticketId, Integer userId) throws Exception {
		super();
		this.ticketId = ticketId;
		HashMap<String, Object> claimDetail = new ClaimEntryResponseMaker(userId).make(conn, ticketId);
		this.ticketStatus = (String)claimDetail.get(ClaimDetailLookupQuery.TICKET_STATUS);
		this.claimedVolume = ((BigDecimal)claimDetail.get(ClaimDetailLookupQuery.CLAIMED_VOLUME)).doubleValue();
		this.totalVolume = ((BigDecimal)claimDetail.get(ClaimDetailLookupQuery.TOTAL_VOLUME)).doubleValue();
		this.claimedDirectLaborAmt = ((BigDecimal)claimDetail.get(ClaimDetailLookupQuery.CLAIMED_DL_AMT)).doubleValue();
		this.availableDirectLabor = ((BigDecimal)claimDetail.get(ClaimDetailLookupQuery.DL_REMAINING)).doubleValue();
		this.jobSiteAddress = (String)claimDetail.get(ClaimDetailLookupQuery.JOB_SITE_NAME);
		this.jobId = (Integer)claimDetail.get(ClaimDetailLookupQuery.JOB_ID);
		this.budget = ((BigDecimal)claimDetail.get(ClaimDetailLookupQuery.BUDGET)).doubleValue();
		this.omNotes = (String)claimDetail.get(ClaimDetailLookupQuery.OM_NOTES);
		this.availableVolume = ((BigDecimal)claimDetail.get(ClaimDetailLookupQuery.VOLUME_REMAINING)).doubleValue();
		TicketStatus ticketStatus = TicketStatus.lookup(this.ticketStatus);
		this.ticketStatusDesc = ticketStatus.display();
	}
	
	
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public String getJobSiteAddress() {
		return jobSiteAddress;
	}
	public void setJobSiteAddress(String jobSiteAddress) {
		this.jobSiteAddress = jobSiteAddress;
	}
	public String getTicketStatus() {
		return ticketStatus;
	}
	public void setTicketStatus(String ticketStatus) {
		this.ticketStatus = ticketStatus;
	}
	public String getTicketStatusDesc() {
		return ticketStatusDesc;
	}
	public void setTicketStatusDesc(String ticketStatusDesc) {
		this.ticketStatusDesc = ticketStatusDesc;
	}
	public Double getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(Double totalVolume) {
		this.totalVolume = totalVolume;
	}
	public Double getClaimedVolume() {
		return claimedVolume;
	}
	public void setClaimedVolume(Double claimedVolume) {
		this.claimedVolume = claimedVolume;
	}
	public Double getAvailableVolume() {
		return availableVolume;
	}
	public void setAvailableVolume(Double availableVolume) {
		this.availableVolume = availableVolume;
	}
	public Double getBudget() {
		return budget;
	}
	public void setBudget(Double budget) {
		this.budget = budget;
	}
	public Double getClaimedDirectLaborAmt() {
		return claimedDirectLaborAmt;
	}
	public void setClaimedDirectLaborAmt(Double claimedDirectLaborAmt) {
		this.claimedDirectLaborAmt = claimedDirectLaborAmt;
	}
	public Double getAvailableDirectLabor() {
		return availableDirectLabor;
	}
	public void setAvailableDirectLabor(Double availableDirectLabor) {
		this.availableDirectLabor = availableDirectLabor;
	}
	public String getOmNotes() {
		return omNotes;
	}
	public void setOmNotes(String omNotes) {
		this.omNotes = omNotes;
	}

	
	
	
	public class ClaimEntryResponseMaker extends ClaimDetailLookupQuery {

		private static final long serialVersionUID = 1L;
		
		public ClaimEntryResponseMaker(Integer userId) {
			super(userId);
		}
		
		public HashMap<String, Object> make(Connection conn, Integer ticketId) throws Exception {
			
			List<ColumnFilter> columnFilterList = Arrays.asList(new ColumnFilter[] {
				new ColumnFilter("ticket.ticket_id",String.valueOf(ticketId))
			});
			this.setColumnFilter(columnFilterList);
			
			List<HashMap<String, Object>> dataList = new ArrayList<HashMap<String, Object>>();
			ResultSet rs = this.select(conn, 0, 100);   // the select amount doesn't matter; there should only be one
			ResultSetMetaData rsmd = rs.getMetaData();
			if ( rs.next() ) {
				dataList.add(makeDataItem(rs, rsmd));
			}
//			if ( itemTransformer != null ) {
//				CollectionUtils.transform(dataList, itemTransformer);
//			}
			return dataList.get(0);
		}


		private HashMap<String, Object> makeDataItem(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
			HashMap<String, Object> dataItem = new HashMap<String, Object>();
			for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
				int idx = i + 1;
				String column = rsmd.getColumnName(idx);
				Object value = rs.getObject(idx);
				
				dataItem.put(column, value);
			}
			return dataItem;
		}
	}
}
