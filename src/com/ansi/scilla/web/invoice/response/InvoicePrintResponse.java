package com.ansi.scilla.web.invoice.response;

import com.ansi.scilla.web.response.MessageResponse;

public class InvoicePrintResponse extends MessageResponse {

	private static final long serialVersionUID = 1L;
	private String invoiceFile;
	public String getInvoiceFile() {
		return invoiceFile;
	}
	public void setInvoiceFile(String invoiceFile) {
		this.invoiceFile = invoiceFile;
	}
	
}
