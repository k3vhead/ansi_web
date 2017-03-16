package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.queries.TicketDRVQuery;
import com.ansi.scilla.web.response.MessageResponse;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class TicketDRVResponse extends MessageResponse {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Date startDate;
	private Date endDate;
	private Integer ticketCount;
	private Division division;
	private Date runDate;
	private BigDecimal totalVolume;
	private BigDecimal totalDL;
	private List<TicketDRVResponseItem> responseItemList;
	
	
	/*
	public TicketDRVResponse(Connection conn, Integer divisionId, Date startDate) throws RecordNotFoundException, Exception{
		division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
	}
	*/
	
	public TicketDRVResponse() {
		super();
	}
	
	public TicketDRVResponse(Connection conn, Integer divisionId, Integer month, Integer year) throws RecordNotFoundException, Exception{
		this();
		division = new Division();
		division.setDivisionId(divisionId);
		division.selectOne(conn);
		
		Calendar calendar = Calendar.getInstance();
		runDate = calendar.getTime();
		calendar.clear();
		calendar.set(year, month, 1);
		startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getMaximum(Calendar.DAY_OF_MONTH));
		endDate = calendar.getTime();
		
		responseItemList = new ArrayList<TicketDRVResponseItem>();
		List<TicketDRVQuery> queryList = TicketDRVQuery.makeMonthlyReport(conn, divisionId, month, year);
		ticketCount = queryList.size();
		totalVolume = new BigDecimal(0);
		totalDL = new BigDecimal(0);
		for(TicketDRVQuery query : queryList){
			TicketDRVResponseItem item = new TicketDRVResponseItem(query);
			responseItemList.add(item);
			totalVolume = totalVolume.add(query.getPricePerCleaning());
			totalDL = totalDL.add(query.getBudget());
		}
		
	}
	
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getEndDate() {
		return endDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Integer getTicketCount() {
		return ticketCount;
	}
	public void setTicketCount(Integer ticketCount) {
		this.ticketCount = ticketCount;
	}
	public Division getDivision() {
		return division;
	}
	public void setDivision(Division division) {
		this.division = division;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy hh:mm:ss")
	public Date getRunDate() {
		return runDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy hh:mm:ss")
	public void setRunDate(Date runDate) {
		this.runDate = runDate;
	}
	public BigDecimal getTotalVolume() {
		return totalVolume;
	}
	public void setTotalVolume(BigDecimal totalVolume) {
		this.totalVolume = totalVolume;
	}
	public BigDecimal getTotalDL() {
		return totalDL;
	}
	public void setTotalDL(BigDecimal totalDL) {
		this.totalDL = totalDL;
	}

	public List<TicketDRVResponseItem> getResponseItemList() {
		return responseItemList;
	}

	public void setResponseItemList(List<TicketDRVResponseItem> responseItemList) {
		this.responseItemList = responseItemList;
	}

	public XSSFWorkbook toXLSX(){
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Date today = new Date();
		int rowNum = 0;
		XSSFRow row = null;
		
		workbook.setSheetName(0,"DRV");
		row = sheet.createRow(rowNum);
		XSSFCell cell = null;
		cell = row.createCell(3);
		cell.setCellValue("Created: ");
		cell = row.createCell(4);
		cell.setCellValue(this.getRunDate());
		cell = row.createCell(12);
		cell.setCellValue("Division: ");
		cell = row.createCell(13);
		cell.setCellValue(this.getDivision().getDivisionNbr() + "-" + this.getDivision().getDivisionCode());
		rowNum++;
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(3);
		cell.setCellValue("Start Date Used");
		cell = row.createCell(12);
		cell.setCellValue("Total Volume for the Month: ");
		cell = row.createCell(13);
		cell.setCellValue(this.getTotalVolume().intValue());
		rowNum++;
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(3);
		cell.setCellValue("From: ");
		cell = row.createCell(4);
		cell.setCellValue(this.getStartDate());
		cell = row.createCell(12);
		cell.setCellValue("Total D/L for the Month: ");
		cell = row.createCell(13);
		cell.setCellValue(this.getTotalDL().intValue());
		rowNum++;
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(3);
		cell.setCellValue("To: ");
		cell = row.createCell(4);
		cell.setCellValue(this.getEndDate());
		cell = row.createCell(12);
		cell.setCellValue("Tickets: ");
		cell = row.createCell(13);
		cell.setCellValue(this.getTicketCount());
		rowNum++;
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(3);
		cell.setCellValue("Ticket");
		cell = row.createCell(4);
		cell.setCellValue("Status");
		cell = row.createCell(5);
		cell.setCellValue("Site");
		cell = row.createCell(6);
		cell.setCellValue("Street 1");
		cell = row.createCell(7);
		cell.setCellValue("City");
		cell = row.createCell(8);
		cell.setCellValue("Last Done");
		cell = row.createCell(9);
		cell.setCellValue("Start Date");
		cell = row.createCell(10);
		cell.setCellValue("J#");
		cell = row.createCell(11);
		cell.setCellValue("FRQ");
		cell = row.createCell(12);
		cell.setCellValue("Budget");
		cell = row.createCell(13);
		cell.setCellValue("PPC");
		cell = row.createCell(14);
		cell.setCellValue("COD");
		rowNum++;
		
		for (TicketDRVResponseItem item : this.getResponseItemList()) {
			int colNum = 3;
			cell = row.createCell(colNum);	//3
			cell.setCellValue(item.getTicketId());
			colNum++;
			cell = row.createCell(colNum);	//4
			cell.setCellValue(item.getStartDate());
			colNum++;
			cell = row.createCell(colNum);	//5
			cell.setCellValue(item.getName());	//Site
			colNum++;
			cell = row.createCell(colNum);	//6
			cell.setCellValue(item.getAddress1());	//Street
			colNum++;
			cell = row.createCell(colNum);	//7
			cell.setCellValue(item.getCity());
			colNum++;
			cell = row.createCell(colNum);	//8
			if(item.getLastDone() != null){
				cell.setCellValue(item.getLastDone());
			}
			colNum++;
			cell = row.createCell(colNum);	//9
			cell.setCellValue(item.getStartDate());
			colNum++;
			cell = row.createCell(colNum);	//10
			cell.setCellValue(item.getJobNum());	// J#
			colNum++;
			cell = row.createCell(colNum);	//11
			cell.setCellValue(item.getFrequency());
			colNum++;
			cell = row.createCell(colNum);	//12
			cell.setCellValue(item.getBudget().doubleValue());
			colNum++;
			cell = row.createCell(colNum);	//13
			cell.setCellValue(item.getPpc().doubleValue());
			colNum++;
			cell = row.createCell(colNum);	//14
			cell.setCellValue(item.getCod());
			colNum++;
			
			rowNum++;
		}
		
//		List<TicketDRVQuery> queryList = TicketDRVQuery.makeMonthlyReport(conn, divisionId, month, year);
//		for(TicketDRVQuery query : queryList){
//			TicketDRVResponseItem item = new TicketDRVResponseItem(query);
//			responseItemList.add(item);
//		}
//		Short rownum = 1;
//
//		while ( rs.next() ) {
//			row = sheet.createRow(rownum);
//			for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
//				cell = row.createCell(i-1);
//				Object o = rs.getObject(i);
//				if ( o == null ) {
//					cell.setCellValue( "" );
//				} else {
//					cell.setCellValue( String.valueOf(rs.getObject(i)));
//				}
//				
//			}
//			rownum++;
//		}
//
//		for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
//			sheet.autoSizeColumn(i); 
//		}
		
		return workbook;
	}
	
}
