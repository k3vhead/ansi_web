package com.ansi.scilla.web.test.sample;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import au.com.bytecode.opencsv.CSVWriter;

import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class RosterServlet extends HttpServlet {
	private static final long serialVersionUID = -4517355213952698359L;

	private static HashMap<RosterReportType, ReportConfig> configMap = new HashMap<RosterReportType, ReportConfig>();
	
	static {
		configMap.put(RosterReportType.ACTIVE, new ReportConfig("Full Roster", "roster"));
		configMap.put(RosterReportType.APPLIED, new ReportConfig("Applications", "applied"));
		configMap.put(RosterReportType.BALANCE_DUE, new ReportConfig("Balance Due", "balance_due"));
		configMap.put(RosterReportType.MEMBERSHIP_REPORT, new ReportConfig("Membership Report", "membership_report"));
		configMap.put(RosterReportType.PUBLISHED, new ReportConfig("Published", "published"));
		configMap.put(RosterReportType.RENEWABLES, new ReportConfig("Renewables", "renewables", new String[] {
			"family_id", "family_name", "address1", "address2", "city", "state", "zip", "home_phone", "membership_date", "email"
		}));
		configMap.put(RosterReportType.WT_EMAIL, new ReportConfig("Waggin' Tales (Email)", "waggin_tales_email"));
		configMap.put(RosterReportType.WT_USPS, new ReportConfig("Waggin' Tales (USPS)", "waggin_tales_usps"));
		configMap.put(RosterReportType.AKC_REPORT, new ReportConfig("AKC Roster Report", "akc_roster"));
	}
	
	private static final Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
	private static final Font textFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
	private static final Font textFontBold = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
//	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
//	private static final DecimalFormat dollarFormat = new DecimalFormat("$0.00");

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		go(request, response);
	}
	


	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		go(request, response);
	}



	protected void go(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
		RosterFormat rosterFormat = RosterFormat.valueOf(request.getParameter("format"));
		RosterReportType rosterReport = RosterReportType.valueOf(request.getParameter("id"));
		
		ReportConfig reportConfig = configMap.get(rosterReport);
		if ( reportConfig == null ) {
			throw new ServletException("Invalid Report Type");
		}
		
		try {
			conn = AppUtils.getConn();
			
			if ( hasPermission(request) ) {
				OutputStreamWriter writer = new OutputStreamWriter(response.getOutputStream());
				response.setStatus(HttpServletResponse.SC_OK); // default to all is good
				
				ResultSet rs = null;
				if ( rosterReport.equals(RosterReportType.WT_EMAIL)) {
					rs = RosterUtils.getRosterWTEmailList(conn);
				} else if ( rosterReport.equals(RosterReportType.ACTIVE)) {
					rs = RosterUtils.getActiveMembersReport(conn);
				} else if ( rosterReport.equals(RosterReportType.WT_USPS)) {
					rs = RosterUtils.getRosterUSPSWagginTalesList(conn);
				} else if ( rosterReport.equals(RosterReportType.APPLIED)) {
					rs = RosterUtils.getAppliedMembersReport(conn);
				} else if ( rosterReport.equals(RosterReportType.PUBLISHED)) {
					rs = RosterUtils.getPublishedMembersReport(conn);
				} else if ( rosterReport.equals(RosterReportType.MEMBERSHIP_REPORT)) {
					rs = RosterUtils.getMembershipReport(conn);
				} else if ( rosterReport.equals(RosterReportType.RENEWABLES )) {
					rs = getRenewables(conn);
				} else if ( rosterReport.equals(RosterReportType.BALANCE_DUE)) {
					rs = RosterUtils.getBalanceDueReport(conn);
				} else if ( rosterReport.equals(RosterReportType.AKC_REPORT)) {
					rs = RosterUtils.getAkcRosterReport(conn);
				}
				ResultSetMetaData rsmd = rs.getMetaData();

				
				if ( rosterFormat.equals(RosterFormat.HTML)) {
					doHeader(writer);
										
					writer.write("<table>\n");
					writer.write("<tr>\n");
					for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
						if ( reportConfig.columnNames == null || reportConfig.columnNames.contains(rsmd.getColumnName(i))) {
							writer.write("<td class=\"titleRow\">" + prettify(rsmd.getColumnName(i)) + "</td>\n");
						}
					}
					writer.write("</tr>\n");
					while ( rs.next() ) {
						writer.write("<tr class=\"dataRow\">\n");
						for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
							if ( reportConfig.columnNames == null || reportConfig.columnNames.contains(rsmd.getColumnName(i))) {
								Object o = rs.getObject(i);
								String oo = o == null ? "" : String.valueOf(o);
								writer.write("<td class=\"dataTd\">" + oo + "</td>\n");
							}
						}
						writer.write("</tr>\n");
					}
					writer.write("</table>\n");
					doFooter(writer);
					writer.flush();
					writer.close();
					
				} else if ( rosterFormat.equals(RosterFormat.CSV) ) {
					CSVWriter csvWriter = null;
					csvWriter = new CSVWriter(writer);
					response.setContentType("text/csv");
					String dispositionHeader = "attachment; filename=" + reportConfig.downloadFileName + ".csv";
					// "attachment; filename=waggintales_email.csv"
					response.setHeader("Content-disposition",dispositionHeader);
									
					csvWriter.writeAll(rs, true);
					csvWriter.close();
				} else if ( rosterFormat.equals(RosterFormat.XLS) ) {
					XSSFWorkbook workbook = makeXLS(rs, rsmd);
					OutputStream out = response.getOutputStream();
					
		            response.setHeader("Expires", "0");
		            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		            response.setHeader("Pragma", "public");
		            // setting the content type
		            response.setContentType("application/vnd.ms-excel");
		            String dispositionHeader = "attachment; filename=" + reportConfig.downloadFileName + ".xlsx";
					response.setHeader("Content-disposition",dispositionHeader);
		            // the contentlength
//		            response.setContentLength(baos.size());

					workbook.write(out);
					out.close();
					
				} else if ( rosterFormat.equals(RosterFormat.PDF)) {
					
		            Document document = new Document(PageSize.LETTER.rotate());
		            ByteArrayOutputStream baos = new ByteArrayOutputStream();
		            PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);

		            document.open();
		            document.add(new Paragraph("Roster Report", titleFont));
					document.add(Chunk.NEWLINE);

					PdfPTable table = new PdfPTable(rsmd.getColumnCount());
			        table.setHorizontalAlignment(Element.ALIGN_LEFT);
			        table.setSpacingBefore(5F);
					// the cell object
			        PdfPCell cell = null;
			        
					// we add the four remaining cells with addCell()
			        cell = new PdfPCell();
			        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//			        cell.setBorder(Rectangle.NO_BORDER);
			        
			        for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
			        	cell.setPhrase(new Phrase(prettify(rsmd.getColumnName(i)), textFontBold));
						table.addCell(cell);
			        }
			        

			        while ( rs.next() ) {
			        	for ( int i = 0; i < rsmd.getColumnCount(); i++ ) {
			        		Object o = rs.getObject(i+1);
			        		if ( o == null ) {
			        			cell.setPhrase(new Phrase("",textFont));
			        		} else {
			        			cell.setPhrase(new Phrase(String.valueOf(o),textFont));
			        		}
							table.addCell(cell);
			        	}
			        	
			        }

					document.add(table);
					document.close();

					
					
		            // setting some response headers
		            response.setHeader("Expires", "0");
		            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
		            response.setHeader("Pragma", "public");
					String dispositionHeader = "attachment; filename=" + reportConfig.downloadFileName + ".pdf";

		            // setting the content type
		            response.setContentType("application/pdf");
		            // the contentlength
		            response.setContentLength(baos.size());
		            // write ByteArrayOutputStream to the ServletOutputStream
		            OutputStream os = response.getOutputStream();
		            baos.writeTo(os);
		            os.flush();
		            os.close();
		            
				} else {
					throw new ServletException("Invalid Roster Format");
				}



			} else {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			}
	
			
		} catch ( Exception e ) {
			AppUtils.logException(e);
			throw new ServletException(e);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				AppUtils.logException(e);
				throw new ServletException(e);
			}
		}
	
		
	}
	
	private XSSFWorkbook makeXLS(ResultSet rs, ResultSetMetaData rsmd) throws SQLException {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet();
		Row row = null;
		workbook.setSheetName(0,"FVDTC");
		row = sheet.createRow(0);
		Cell cell = null;

		for ( short cellnum = 0; cellnum < rsmd.getColumnCount(); cellnum++ ) {		
			cell = row.createCell(cellnum);
			int colNbr = Integer.valueOf(cellnum) + 1;
			cell.setCellValue(prettify(rsmd.getColumnName(colNbr)));
		}		
		Short rownum = 1;

		while ( rs.next() ) {
			row = sheet.createRow(rownum);
			for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
				cell = row.createCell(i-1);
				Object o = rs.getObject(i);
				if ( o == null ) {
					cell.setCellValue( "" );
				} else {
					cell.setCellValue( String.valueOf(rs.getObject(i)));
				}
				
			}
			rownum++;
		}

		for ( int i = 1; i < rsmd.getColumnCount() + 1; i++ ) {
			sheet.autoSizeColumn(i); 
		}

		return workbook;
	}



	protected boolean hasPermission(HttpServletRequest request) {
		boolean hasPermission = false;
		HttpSession session = request.getSession();
		SessionData sessionData = (SessionData)session.getAttribute(SessionData.KEY);
		if ( sessionData == null ) {
			hasPermission = false;
		} else {
			Member login = sessionData.getLogin();
			if ( login == null ) {
				hasPermission = false;
			} else {
				if ( sessionData.getPermissions().contains(AdminPermission.MEMBER_ADMIN)) {
					hasPermission = true;
				} else {
					hasPermission = false;
				}
			}
		}
		return hasPermission;
	}

	
	protected void doHeader(OutputStreamWriter writer) throws IOException {
		writer.write("<html>\n");
		writer.write("<head>\n");
		writer.write("<style type=\"text/css\">\n");
		writer.write(".titleRow { background-color:#CCCCCC; padding-top:4px; padding-bottom:4px; padding-left:4px; font-weight:bold; }\n");
		writer.write(".dataCol { padding-left:8px; }\n");
		writer.write(".dataTd { white-space:nowrap; }\n");
		writer.write("</style>\n");
		writer.write("<script type=\"text/javascript\">\n");
		writer.write("$(function() {\n");
		writer.write("$('.dataRow').mouseover(function() {\n");
		writer.write("$(this).css('background-color','#CCCCCC');\n");
		writer.write("});\n");
		writer.write("$('.dataRow').mouseout(function() {\n");
		writer.write("$(this).css('background-color','transparent');\n");
		writer.write("});\n");
		writer.write("});\n");
		writer.write("</script>\n");
		writer.write("</head>\n");
		writer.write("<body>");
	}

	
	protected void doFooter(OutputStreamWriter writer) throws IOException {
		writer.write("</body>\n");
		writer.write("</html>\n");
	}

	
	protected String prettify(String name) {
		String[] pieces = name.split("_");
		StringBuffer newName = new StringBuffer();
		for ( String piece : pieces ) {
			newName.append(WordUtils.capitalize(piece.toLowerCase()));
			newName.append(" ");
		}
		return StringUtils.trim(newName.toString());
	}
	
	
	
	private ResultSet getRenewables(Connection conn) throws RecordNotFoundException, Exception {
		MembershipYear membershipYear = MembershipYear.getCurrentYear(conn).get(0);
		Calendar lastDay = Calendar.getInstance();
		lastDay.clear();
		lastDay.setTime(membershipYear.getEndDate());
		lastDay.add(Calendar.DAY_OF_MONTH, 1);

		MembershipYear renewableYear = null;
		try {
			MembershipYear nextYear = MembershipYear.getMembershipYear(conn, lastDay.getTime()).get(0);
			renewableYear = nextYear;
		} catch ( RecordNotFoundException e) {
			renewableYear = membershipYear;
		}
		return getRenewables(conn, renewableYear.getMembershipYearId());
	}

	
	private ResultSet getRenewables(Connection conn, Integer membershipYearId) throws Exception {
		String sql = "select family.*, member.first_name, member.email " +
					" from family, family_year, member " +
					" where family_year.membership_status in (1,2,3) " + 
					" and family_year.family_id = family.family_id  " +
					" and family_year.membership_year_id=? " +
					" and  " +
					" ( family.family_id not in ( select family_id from family_year where membership_year_id=?) " +
					" or    " +
					" family.family_id in (select family_id from family_year where membership_year_id=? and membership_status=0) ) " + 
					" and member.family_id = family.family_id " +
					" order by upper(family_name)";
		
		MembershipYear oldYear = MembershipYear.getPreviousMembershipYear(conn, membershipYearId);

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, oldYear.getMembershipYearId());
		ps.setInt(2, membershipYearId);
		ps.setInt(3, membershipYearId);
		ResultSet rs = ps.executeQuery();
		return rs;
	}	
//	private String getMimeType(String fileName) {
//		String[] filePieces = fileName.split("\\.");
//		String suffix = filePieces[filePieces.length - 1].toLowerCase();
//		ResourceBundle rb = ResourceBundle.getBundle("resources/mimetypes");
//        String mimeType = rb.getString("default");
//
//        if ( rb.containsKey(suffix)) {
//            mimeType = rb.getString(suffix);
//        }
//        
//        return mimeType;
//
//	}
	
	public enum AdminPermission {
		MEMBER_ADMIN;		
	}
	public enum RosterReportType {
		ACTIVE,
		APPLIED,
		BALANCE_DUE,
		MEMBERSHIP_REPORT,
		PUBLISHED,
		RENEWABLES,
		WT_EMAIL,
		WT_USPS,
		AKC_REPORT;

	}
	public class Member {
		
	}
	public enum RosterFormat {
		HTML,
		PDF,
		CSV,
		XLS;
	}

}
