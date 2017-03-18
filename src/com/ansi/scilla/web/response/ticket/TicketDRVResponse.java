package com.ansi.scilla.web.response.ticket;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HeaderFooter;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Footer;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.jobticket.TicketStatus;
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
		CreationHelper createHelper = workbook.getCreationHelper();
		//Date today = new Date();
		int rowNum = 0;
		XSSFRow row = null;
		
		//DateFormat
		CellStyle cellStyleDate = workbook.createCellStyle();
		cellStyleDate.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));	//military time

		
		//Date/Time Format		
		CellStyle cellStyleDateTime = workbook.createCellStyle();
		cellStyleDateTime.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy hh:mm:ss a"));	//military time

		//Decimal Format: 2 digits and commas
		CellStyle cellStyleDecimal = workbook.createCellStyle();
		DataFormat format = workbook.createDataFormat();
		cellStyleDecimal.setDataFormat(format.getFormat("#,##0.00"));
		
		//Header White and Black
		CellStyle cellStyleBackColor = workbook.createCellStyle();
		cellStyleBackColor.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
	    cellStyleBackColor.setFillPattern(CellStyle.ALIGN_FILL);
	    
	    CellStyle cellStyleFontColor = workbook.createCellStyle();
	    XSSFFont font = workbook.createFont();
	    font.setColor(HSSFColor.WHITE.index);
	    cellStyleFontColor.setFont(font);

		//Bold and Underline
	    XSSFFont fontStyleBold = workbook.createFont();
	    XSSFFont fontStyleItalicBold = workbook.createFont();
	    
	    fontStyleBold.setBold(true);
	    
	    fontStyleItalicBold.setItalic(true);
	    fontStyleItalicBold.setBold(true);
	    
	    XSSFCellStyle cellLeftBold = workbook.createCellStyle();
	    cellLeftBold.setAlignment(CellStyle.ALIGN_LEFT);
	    cellLeftBold.setFont(fontStyleBold);
	    
	    XSSFCellStyle cellRightBold = workbook.createCellStyle();
	    cellRightBold.setAlignment(CellStyle.ALIGN_RIGHT);
	    cellRightBold.setFont(fontStyleBold);
	    
	    XSSFCellStyle cellCenterBold = workbook.createCellStyle();
	    XSSFCellStyle cellCenterBoldItalic = workbook.createCellStyle();
	    
	    cellCenterBold.setAlignment(CellStyle.ALIGN_CENTER);
	    cellCenterBold.setFont(fontStyleBold);
	    
	    cellCenterBoldItalic.setAlignment(CellStyle.ALIGN_CENTER);
	    cellCenterBoldItalic.setFont(fontStyleItalicBold);
	    
	    //Cell Alignment
	    CellStyle cellStyleLeftAll = workbook.createCellStyle();
	    cellStyleLeftAll.setAlignment(CellStyle.ALIGN_LEFT);
	    
	    CellStyle cellStyleCenterAll = workbook.createCellStyle();
	    cellStyleCenterAll.setAlignment(CellStyle.ALIGN_CENTER);
	    
	    CellStyle cellStyleRightAll = workbook.createCellStyle();
	    cellStyleRightAll.setAlignment(CellStyle.ALIGN_RIGHT);
	    
	    //Date and Alignment
	    CellStyle cellLeftDateTime = workbook.createCellStyle();
	    cellLeftDateTime.setAlignment(CellStyle.ALIGN_LEFT);
	    cellLeftDateTime.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy hh:mm:ss"));
	    
	    CellStyle cellLeftDate = workbook.createCellStyle();
	    cellLeftDate.setAlignment(CellStyle.ALIGN_LEFT);
	    cellLeftDate.setDataFormat(createHelper.createDataFormat().getFormat("mm/dd/yyyy"));
	    
	    //Decimal and Alignment
	    CellStyle cellRightDecimal = workbook.createCellStyle();
	    cellRightDecimal.setAlignment(CellStyle.ALIGN_RIGHT);
	    cellRightDecimal.setDataFormat(format.getFormat("#,##0.00"));
	    
		//Vertical Script
	    CellStyle vertical = workbook.createCellStyle();
	    vertical.setRotation((short)90);
	    
		//Footer(page number)
	    Footer footer = sheet.getFooter();
	    footer.setCenter("Page " + HeaderFooter.page() + " of " + HeaderFooter.numPages());
	    
		workbook.setSheetName(0,"DRV");
		row = sheet.createRow(rowNum);
		XSSFCell cell = null;
		
		cell = row.createCell(0);
		cell.setCellValue("Ruin");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(0);
		cell = row.createCell(1);
		cell.setCellValue("Reject/Reschedule");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(1);
		cell = row.createCell(2);
		cell.setCellValue("Cancel Job");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(2);
		cell = row.createCell(3);
		cell.setCellValue("Created: ");
		cell.setCellStyle(cellLeftBold);
		cell = row.createCell(4);
		cell.setCellValue(this.getRunDate());
		cell.setCellStyle(cellLeftDateTime);
		cell = row.createCell(6);
		cell.setCellValue("American National Skyline, Inc.");
		cell.setCellStyle(cellCenterBoldItalic);
		cell = row.createCell(12);
		cell.setCellValue("Division: ");
		cell.setCellStyle(cellRightBold);
		cell = row.createCell(14);
		cell.setCellValue(this.getDivision().getDivisionNbr() + "-" + this.getDivision().getDivisionCode());
		cell.setCellStyle(cellStyleRightAll);
		cell = row.createCell(15);
		cell.setCellValue("Received");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(15);
		cell = row.createCell(16);
		cell.setCellValue("Ran");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(16);
		cell = row.createCell(17);
		cell.setCellValue("Submitted");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(17);
		cell = row.createCell(18);
		cell.setCellValue("Invoiced");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(18);
		cell = row.createCell(19);
		cell.setCellValue("Paid");
		cell.setCellStyle(vertical);
		sheet.autoSizeColumn(19);
		rowNum++;	//row=1
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(3);
		cell.setCellValue("Start Date Used");
		cell.setCellStyle(cellLeftBold);
		cell = row.createCell(6);
		cell.setCellValue("Detailed Rolling Volume Check List");
		cell.setCellStyle(cellCenterBold);
		cell = row.createCell(12);
		cell.setCellValue("Total Volume for the Month: ");
		cell.setCellStyle(cellRightBold);
		cell = row.createCell(14);
		cell.setCellValue(this.getTotalVolume().intValue());
		cell.setCellStyle(cellRightDecimal);
		rowNum++;	//row=2
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(3);
		cell.setCellValue("From: ");
		cell.setCellStyle(cellLeftBold);
		cell = row.createCell(4);
		cell.setCellValue(this.getStartDate());
		cell.setCellStyle(cellLeftDate);
		cell = row.createCell(12);
		cell.setCellValue("Total D/L for the Month: ");
		cell.setCellStyle(cellRightBold);
		cell = row.createCell(14);
		cell.setCellValue(this.getTotalDL().intValue());
		cell.setCellStyle(cellRightDecimal);
		rowNum++;	//row=3
		
		row = sheet.createRow(rowNum);
		sheet.addMergedRegion(new CellRangeAddress(0,3,0,0));
		sheet.addMergedRegion(new CellRangeAddress(0,3,1,1));
		sheet.addMergedRegion(new CellRangeAddress(0,3,2,2));
		cell = row.createCell(3);
		cell.setCellValue("To: ");
		cell.setCellStyle(cellLeftBold);
		cell = row.createCell(4);
		cell.setCellValue(this.getEndDate());
		cell.setCellStyle(cellLeftDate);
		cell = row.createCell(12);
		cell.setCellValue("Tickets: ");
		cell.setCellStyle(cellRightBold);
		cell = row.createCell(14);
		cell.setCellValue(this.getTicketCount());
		cell.setCellStyle(cellStyleRightAll);
		sheet.addMergedRegion(new CellRangeAddress(0,3,15,15));
		sheet.addMergedRegion(new CellRangeAddress(0,3,16,16));
		sheet.addMergedRegion(new CellRangeAddress(0,3,17,17));
		sheet.addMergedRegion(new CellRangeAddress(0,3,18,18));
		sheet.addMergedRegion(new CellRangeAddress(0,3,19,19));
		rowNum++;	//row=4
		
		row = sheet.createRow(rowNum);
		cell = row.createCell(0);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(1);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(2);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(3);
		cell.setCellValue("Ticket");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(4);
		cell.setCellValue("Status");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(5);
		cell.setCellValue("Site");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(6);
		cell.setCellValue("Street 1");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(7);
		cell.setCellValue("City");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(8);
		cell.setCellValue("Last Done");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(9);
		cell.setCellValue("Start Date");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(10);
		cell.setCellValue("J#");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(11);
		cell.setCellValue("FRQ");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(12);
		cell.setCellValue("Budget");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(13);
		cell.setCellValue("PPC");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(14);
		cell.setCellValue("COD");
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(15);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(16);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(17);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(18);
		cell.setCellStyle(cellStyleBackColor);
		cell = row.createCell(19);
		cell.setCellStyle(cellStyleBackColor);
		rowNum++;	//row=5
		
//		//Repeat above rows on printsheets
		sheet.setRepeatingRows(CellRangeAddress.valueOf("1:5"));
//		
//		//Freezing above rows
		sheet.createFreezePane(19, 5, 0, 0);
		
		for (TicketDRVResponseItem item : this.getResponseItemList()) {
			row = sheet.createRow(rowNum);
			//Column Width Fit
		    sheet = workbook.getSheetAt(0);
			int colNum = 3;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//3
			cell.setCellValue(item.getTicketId());
			cell.setCellStyle(cellStyleLeftAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//4
			cell.setCellValue(TicketStatus.lookup(item.getStatus()).display());
			cell.setCellStyle(cellStyleLeftAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//5
			cell.setCellValue(item.getName());	//Site
			cell.setCellStyle(cellStyleLeftAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//6
			cell.setCellValue(item.getAddress1());	//Street
			cell.setCellStyle(cellStyleLeftAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//7
			cell.setCellValue(item.getCity());
			cell.setCellStyle(cellStyleLeftAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//8
			if(item.getLastDone() != null){
				cell.setCellValue(item.getLastDone());
				cell.setCellStyle(cellLeftDate);
			}
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//9
			cell.setCellValue(item.getStartDate());
			cell.setCellStyle(cellLeftDate);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//10
			cell.setCellValue(item.getJobNum());	// J#
			cell.setCellStyle(cellStyleCenterAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//11
			cell.setCellValue(item.getFrequency());
			cell.setCellStyle(cellStyleCenterAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//12
			cell.setCellValue(item.getBudget().doubleValue());
			cell.setCellStyle(cellRightDecimal);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//13
			cell.setCellValue(item.getPpc().doubleValue());
			cell.setCellStyle(cellRightDecimal);
			colNum++;
			sheet.autoSizeColumn(colNum);
			cell = row.createCell(colNum);	//14
			cell.setCellValue(item.getCod());
			cell.setCellStyle(cellStyleRightAll);
			colNum++;
			sheet.autoSizeColumn(colNum);
			
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
