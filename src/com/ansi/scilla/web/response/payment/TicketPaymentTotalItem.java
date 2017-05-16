package com.ansi.scilla.web.response.payment;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.jsonFormat.AnsiCurrencyFormatter;
import com.ansi.scilla.common.queries.TicketPaymentTotals;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class TicketPaymentTotalItem extends ApplicationObject {

	private static final long serialVersionUID = 1L;

	private Integer divisionId;
	private String divisionCode;
	private BigDecimal totalVolPaid;
	private BigDecimal totalTaxPaid;
	private BigDecimal totalBalance;
	private String divisionDisplay;

	// this is copied from ansi_common/db/ticket
	private BigDecimal actDlAmt;
	private BigDecimal actDlPct;
	private BigDecimal actPricePerCleaning;
	private BigDecimal actTaxAmt;
	private Integer actTaxRateId;
	private Integer billSheet;
	private Integer customerSignature;
	private Integer invoiceId;
	private Integer jobId;
	private Integer mgrApproval;
	private Integer printCount;
	private Date processDate;
	private String processNotes;
	private Date startDate;
	private String status;
	private Integer ticketId;
	private String fleetmaticsId;
	private Date invoiceDate;
	private Integer actDivisionId;
	
	public TicketPaymentTotalItem() {
		super();
	}
	
	public TicketPaymentTotalItem(TicketPaymentTotals ticketPaymentTotals) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		this();
		PropertyUtils.copyProperties(this, ticketPaymentTotals);
		PropertyUtils.copyProperties(this, ticketPaymentTotals.getTicket());
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
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalVolPaid() {
		return totalVolPaid;
	}
	public void setTotalVolPaid(BigDecimal totalVolPaid) {
		this.totalVolPaid = totalVolPaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalTaxPaid() {
		return totalTaxPaid;
	}
	public void setTotalTaxPaid(BigDecimal totalTaxPaid) {
		this.totalTaxPaid = totalTaxPaid;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getTotalBalance() {
		return totalBalance;
	}
	public void setTotalBalance(BigDecimal totalBalance) {
		this.totalBalance = totalBalance;
	}
	public String getDivisionDisplay() {
		return divisionDisplay;
	}
	public void setDivisionDisplay(String divisionDisplay) {
		this.divisionDisplay = divisionDisplay;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActDlAmt() {
		return actDlAmt;
	}
	public void setActDlAmt(BigDecimal actDlAmt) {
		this.actDlAmt = actDlAmt;
	}
	public BigDecimal getActDlPct() {
		return actDlPct;
	}
	public void setActDlPct(BigDecimal actDlPct) {
		this.actDlPct = actDlPct;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActPricePerCleaning() {
		return actPricePerCleaning;
	}
	public void setActPricePerCleaning(BigDecimal actPricePerCleaning) {
		this.actPricePerCleaning = actPricePerCleaning;
	}
	@JsonSerialize(using=AnsiCurrencyFormatter.class)
	public BigDecimal getActTaxAmt() {
		return actTaxAmt;
	}
	public void setActTaxAmt(BigDecimal actTaxAmt) {
		this.actTaxAmt = actTaxAmt;
	}
	public Integer getActTaxRateId() {
		return actTaxRateId;
	}
	public void setActTaxRateId(Integer actTaxRateId) {
		this.actTaxRateId = actTaxRateId;
	}
	public Integer getBillSheet() {
		return billSheet;
	}
	public void setBillSheet(Integer billSheet) {
		this.billSheet = billSheet;
	}
	public Integer getCustomerSignature() {
		return customerSignature;
	}
	public void setCustomerSignature(Integer customerSignature) {
		this.customerSignature = customerSignature;
	}
	public Integer getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(Integer invoiceId) {
		this.invoiceId = invoiceId;
	}
	public Integer getJobId() {
		return jobId;
	}
	public void setJobId(Integer jobId) {
		this.jobId = jobId;
	}
	public Integer getMgrApproval() {
		return mgrApproval;
	}
	public void setMgrApproval(Integer mgrApproval) {
		this.mgrApproval = mgrApproval;
	}
	public Integer getPrintCount() {
		return printCount;
	}
	public void setPrintCount(Integer printCount) {
		this.printCount = printCount;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getProcessDate() {
		return processDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setProcessDate(Date processDate) {
		this.processDate = processDate;
	}
	public String getProcessNotes() {
		return processNotes;
	}
	public void setProcessNotes(String processNotes) {
		this.processNotes = processNotes;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getStartDate() {
		return startDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Integer getTicketId() {
		return ticketId;
	}
	public void setTicketId(Integer ticketId) {
		this.ticketId = ticketId;
	}
	public String getFleetmaticsId() {
		return fleetmaticsId;
	}
	public void setFleetmaticsId(String fleetmaticsId) {
		this.fleetmaticsId = fleetmaticsId;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public Date getInvoiceDate() {
		return invoiceDate;
	}
	@JsonFormat(shape=JsonFormat.Shape.STRING, pattern="MM/dd/yyyy")
	public void setInvoiceDate(Date invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public Integer getActDivisionId() {
		return actDivisionId;
	}
	public void setActDivisionId(Integer actDivisionId) {
		this.actDivisionId = actDivisionId;
	}

	
	
}
