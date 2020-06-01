package com.ansi.scilla.web.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.Calendar;
import java.util.List;

import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.common.invoice.InvoicePrinter;
import com.ansi.scilla.common.invoice.InvoiceTicket;
import com.ansi.scilla.common.invoice.InvoiceUtils;
import com.ansi.scilla.common.utils.AppUtils;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;

public class TestPDF {


	public static void main(String[] args) {
		try {
//			TesterUtils.makeLoggers();
			new TestPDF().go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	public void go() throws Exception {
		Connection conn = null;
		try {
			conn = AppUtils.getDevConn();
			conn.setAutoCommit(false);
			
			Calendar printDate = Calendar.getInstance();
			Calendar dueDate = Calendar.getInstance();
			dueDate.add(Calendar.MONTH, 1);

			InvoicePrinter maker = new InvoicePrinter();
			Integer divisionId=3;
			List<Division> divisionList = Division.cast(new Division().selectAll(conn));
//			for ( Division division : divisionList ) {
//				divisionId = division.getDivisionId();
//				System.out.println("***** " + divisionId + " *******");				
				List<List<InvoiceTicket>> ticketList = InvoiceUtils.makeDivisionTicketList(conn, divisionId);
				if ( ticketList.size()==0) {
					System.err.println("Skipping: " + divisionId);
				} else {
					ByteArrayOutputStream baos = maker.makeInvoices(conn, divisionId, printDate, dueDate, ticketList);
					
					
					//FileOutputStream os = new FileOutputStream(new File("/home/dclewis/Documents/projects/ANSI_Scheduling/invoice_test_" + divisionId + ".pdf"));
					FileOutputStream os = new FileOutputStream(new File("/home/Documents/pdfs/invoice_test_" + divisionId + ".pdf"));
					baos.writeTo(os);
					os.flush();
					os.close();
				}
//			}
			
			conn.rollback();
		} catch ( Exception e) {
			conn.rollback();
			throw e;
		} finally {
			conn.close();
		}
	}
	
	
	
	
	
	
	



	

	/*
	public void goRSMD() {
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
		//        cell.setBorder(Rectangle.NO_BORDER);

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
	}
	
	*/
	
	

	
	
	
	
	

	/*
	
	private ByteArrayOutputStream makeInvoice(Connection conn,  String localhost, Document document, Date statementDate, String webthingClientId) throws Exception {		
		Integer clientId = StringUtils.isBlank(webthingClientId) ? null : Integer.valueOf(webthingClientId);
		StatementPageData pageData = new StatementPageData(conn, statementDate);

//		Document document = new Document(PageSize.LETTER.rotate());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter pdfWriter = PdfWriter.getInstance(document, baos);

		document.open();

		PdfPTable returnAddress = makeReturnAddress(localhost);
		Paragraph invoiceDate = makeInvoiceDate(statementDate);
		
		PreparedStatement ps = conn.prepareStatement("update ledger set invoice_id=? where ledger_id=?");
		
		for ( CustomerLedgerItem customerLedgerItem : pageData.getCustomerLedgerItems()) {
			if ( clientId == null || customerLedgerItem.getClient().getClientId().equals(clientId)) {
				document.add(returnAddress);
				document.add(makeCustomerAddress(customerLedgerItem.getClient()));
				document.add(invoiceDate);
				document.add(makeItemTable(customerLedgerItem.getOpeningBalance(), customerLedgerItem.getLedgerList(), customerLedgerItem.getBalanceDue()));
				document.add(makeMakePayable());
				document.newPage();
				Integer invoiceId = insertInvoiceRecord(conn, customerLedgerItem);
				for ( LedgerItem ledgerItem : customerLedgerItem.getLedgerList()) {
					updateLedger(ps, ledgerItem, invoiceId);
				}
			}
		}

		document.close();

		return baos;
	}
	*/
	
	/*
	private void updateLedger(PreparedStatement ps, LedgerItem ledgerItem, Integer invoiceId) throws SQLException {
		ps.setInt(1, invoiceId);
		ps.setInt(2, ledgerItem.getLedger().getLedgerId());
		ps.executeUpdate();
	}
	*/
	
	
	private Paragraph makeMakePayable() {
		Font roman = FontFactory.getFont(FontFactory.TIMES, 10);
		Font romanItalic = FontFactory.getFont(FontFactory.TIMES_ITALIC, 10);
		Paragraph makePayable = new Paragraph();		
		Chunk text1 = new Chunk("Make checks payable to ", roman);
		Chunk text2 = new Chunk("theWebThing LLC", romanItalic);
		makePayable.add(text1);
		makePayable.add(text2);
		makePayable.setAlignment(Element.ALIGN_RIGHT);
		
		return makePayable;
	}

	/*
	private PdfPTable makeItemTable(Float openingBalance, List<LedgerItem> ledgerList, Float balanceDue) throws DocumentException {
		Font hdrFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
		hdrFont.setColor(BaseColor.WHITE);
		
		PdfPTable table = new PdfPTable(5); // 4 columns.
		table.setWidthPercentage(100);
		table.setSpacingBefore(10.0F);
//		table.setSpacingAfter(10.0F);
		table.setWidths(new float[] {12F, 20F, 40F, 10F, 10F}); // relative widths
		
		PdfPCell previousBalance = new PdfPCell(new Paragraph(new Chunk("Previous Balance", textFontBold)));
		previousBalance.setHorizontalAlignment(Element.ALIGN_RIGHT);
		PdfPCell balanceDueCell = new PdfPCell(new Paragraph(new Chunk("Balance Due", textFontBold)));
		balanceDueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		PdfPCell dashCell = new PdfPCell(new Paragraph("-"));
		dashCell.setHorizontalAlignment(Element.ALIGN_CENTER);
		PdfPCell blankCell = new PdfPCell(new Paragraph(""));
		
		
		for ( String header : new String[] {"Date","Type", "Description","Hours","Price"} ) {
			Paragraph headerPara = new Paragraph(new Chunk(header, hdrFont));
			PdfPCell headerCell = new PdfPCell(headerPara);
			headerCell.setBackgroundColor(BaseColor.BLACK);
			table.addCell( headerCell );
		}
		table.addCell( blankCell );
		table.addCell( blankCell );
		table.addCell( previousBalance );
		table.addCell( dashCell);
		PdfPCell prevBalanceAmtCell = new PdfPCell(new Paragraph(new Chunk(dollarFormat.format(openingBalance), textFont)));
		prevBalanceAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell( prevBalanceAmtCell );
		
		
		
		for ( LedgerItem item : ledgerList ) {
			
			table.addCell(new PdfPCell(new Paragraph(new Chunk(FORMAT_MMDDYYYY.format(item.getLedger().getEntryDate()), textFont))));
			table.addCell(new PdfPCell(new Paragraph(new Chunk(item.getEntryType(), textFont))));
			
			Paragraph description = new Paragraph();
			description.setLeading(0f, 0.9f); 
			description.add(new Chunk(item.getLedger().getDescription(), textFont));
			if ( ! StringUtils.isBlank(item.getLedger().getDescriptionDetail())) {
//				description.add(Chunk.NEWLINE);
				description.add(new Chunk(" ", textFont));
				description.add(new Chunk("(" + item.getLedger().getDescriptionDetail() + ")", returnFont));
			}
			table.addCell( new PdfPCell(description) );
			
			if ( item.getLedger().getHour() == null ) {
				table.addCell( dashCell );
			} else {
				PdfPCell hourCell = new PdfPCell(new Paragraph(new Chunk(item.getLedger().getHour().toString(), textFont)));
				hourCell.setHorizontalAlignment(Element.ALIGN_CENTER);
				table.addCell( hourCell );
			}
			PdfPCell amtCell = new PdfPCell(new Paragraph(new Chunk(dollarFormat.format(item.getLedger().getLedgerAmt().floatValue()), textFont)));
			amtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			table.addCell( amtCell );
		}
		
		
		
		table.addCell( blankCell );
		table.addCell( blankCell );
		table.addCell( balanceDueCell );
		table.addCell( dashCell);		
		PdfPCell balanceDueAmtCell = new PdfPCell(new Paragraph(new Chunk(dollarFormat.format(balanceDue), textFont)));
		balanceDueAmtCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.addCell( balanceDueAmtCell );
		return table;
	}
	*/
	
	/*
	private Paragraph makeInvoiceDate(Date statementDate) {
//		Calendar invoiceDate = new GregorianCalendar(2015, Calendar.JULY, 1);
		String invoiceDateString = FORMAT_MMMMDDYYYY.format(statementDate);
		Paragraph dateParagraph = new Paragraph(invoiceDateString, textFont);
		dateParagraph.setAlignment(Element.ALIGN_RIGHT);
		dateParagraph.setSpacingBefore(36.0F);
		return dateParagraph;
	}
	*/
	
	/*
	private PdfPTable makeReturnAddress(String localhost) throws DocumentException, MalformedURLException, IOException {
		Paragraph returnAddress = new Paragraph(12);
		returnAddress.setLeading(0f, 0.9f); 
		returnAddress.add(new Chunk("David C. Lewis", returnFontBold));
		returnAddress.add(Chunk.NEWLINE);
		returnAddress.add(new Chunk("theWebThing LLC", returnFont));
		returnAddress.add(Chunk.NEWLINE);
		returnAddress.add(new Chunk("232 Mistwood Lane", returnFont));
		returnAddress.add(Chunk.NEWLINE);
		returnAddress.add(new Chunk("North Aurora, Illinois 60542", returnFont));
		returnAddress.add(Chunk.NEWLINE);
		returnAddress.add(new Chunk("(630) 728-3852", returnFont));
		returnAddress.add(Chunk.NEWLINE);
		returnAddress.add(new Chunk("dclewis@thewebthing.com", returnFont));
		
//		Image image = Image.getInstance("/home/dcl/Documents/workspace/Webthing/webthing-invoices/WebContent/logo.gif");
		
		String url = localhost + "/logo.gif";
		System.out.println(url);
		Image image = Image.getInstance(new URL(url));
		
		PdfPTable returnTable = new PdfPTable(2);
		returnTable.setSpacingBefore(123.0F);
		returnTable.setWidthPercentage(100F);
		returnTable.setWidths(new float[] {10F, 90F});
		boolean fitImageToCell = true;
		PdfPCell logoCell = new PdfPCell(image, fitImageToCell );
		logoCell.setBorder(Rectangle.NO_BORDER);
		returnTable.addCell(logoCell);
		PdfPCell addressCell = new PdfPCell(returnAddress);
		addressCell.setBorder(Rectangle.NO_BORDER);
		returnTable.addCell(addressCell);
		
		return returnTable;
	}
	*/
	
	/*
	private Paragraph makeCustomerAddress(WebthingClient client) {
		Paragraph customerAddress = new Paragraph(12);
		customerAddress.setSpacingBefore(70.5F);
//		customerAddress.setIndentationLeft(121.5F);
//		customerAddress.setAlignment(Element.ALIGN_LEFT);
		customerAddress.setIndentationRight(310.828F);  //4.75 inches
		customerAddress.setAlignment(Element.ALIGN_RIGHT);
				
		if ( ! StringUtils.isBlank(client.getClientContactFirstName()) ) {
			customerAddress.add(new Chunk(client.getClientContactFirstName() + " " + client.getClientContactLastName(), textFontBold));
		}		
		customerAddress.add(Chunk.NEWLINE);
		customerAddress.add(new Chunk(client.getClientBusinessName(), textFont));
		customerAddress.add(Chunk.NEWLINE);
		customerAddress.add(new Chunk(client.getAddress1(), textFont));
		customerAddress.add(Chunk.NEWLINE);
		if ( ! StringUtils.isBlank(client.getAddress2())) {
			customerAddress.add(new Chunk(client.getAddress2(), textFont));
			customerAddress.add(Chunk.NEWLINE);
		}
		String address3 = client.getCity() + ",  " + client.getState() + "  " + client.getZip();
		customerAddress.add(new Chunk(address3, textFont));
		customerAddress.add(Chunk.NEWLINE);

		return customerAddress;
	}
	*/

	//      private static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	//      private static final DecimalFormat dollarFormat = new DecimalFormat("$0.00");
	
	
}
