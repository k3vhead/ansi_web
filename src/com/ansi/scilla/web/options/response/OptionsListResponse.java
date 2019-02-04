package com.ansi.scilla.web.options.response;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.ansi.scilla.common.account.AccountType;
import com.ansi.scilla.common.address.Country;
import com.ansi.scilla.common.claims.WorkHoursType;
import com.ansi.scilla.common.employee.EmployeeHoursType;
import com.ansi.scilla.common.invoice.InvoiceGrouping;
import com.ansi.scilla.common.invoice.InvoiceStyle;
import com.ansi.scilla.common.invoice.InvoiceTerm;
import com.ansi.scilla.common.jobticket.JobFrequency;
import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.payment.PaymentMethod;
import com.ansi.scilla.web.common.response.MessageResponse;
import com.ansi.scilla.web.common.struts.SessionData;
import com.ansi.scilla.web.common.utils.Permission;
import com.ansi.scilla.web.report.common.ReportType;

public class OptionsListResponse extends MessageResponse {
	private static final long serialVersionUID = 1L;
	private List<JobFrequencyOption> jobFrequency;
	private List<JobStatusOption> jobStatus;
	private List<TicketStatusOption> ticketStatus;
	private List<CountryOption> country;
	private List<Permission> permission;
	private List<InvoiceGroupingOption> invoiceGrouping;
	private List<InvoiceTermOption> invoiceTerm;
	private List<InvoiceStyleOption> invoiceStyle;
	private List<AccountTypeOption> accountType;
	private List<PaymentMethodOption> paymentMethod;
	private List<ReportTypeOption> reportType;
	private List<WorkHoursTypeOption> workHoursType;
	private List<ExpenseTypeOption> expenseType;

	public OptionsListResponse(List<ResponseOption> options, SessionData sessionData) throws ClassNotFoundException, Exception {
		if ( options.contains(ResponseOption.JOB_FREQUENCY)) {
			makeJobFrequencyList();
		}
		if ( options.contains(ResponseOption.JOB_STATUS)) {
			makeJobStatusList();
		}
		if ( options.contains(ResponseOption.TICKET_STATUS)) {
			makeTicketStatusList();
		}
		if ( options.contains(ResponseOption.COUNTRY)) {
			makeCountryList();
		}
		if ( options.contains(ResponseOption.PERMISSION)) {
			makePermissionList();
		}
		if ( options.contains(ResponseOption.INVOICE_GROUPING)) {
			makeInvoiceGroupList();
		}
		if ( options.contains(ResponseOption.INVOICE_TERM)) {
			makeInvoiceTermList();
		}
		if ( options.contains(ResponseOption.INVOICE_STYLE)) {
			makeInvoiceStyleList();
		}
		if ( options.contains(ResponseOption.ACCOUNT_TYPE)) {
			makeAccountTypeList();
		}
		if ( options.contains(ResponseOption.PAYMENT_METHOD)) {
			makePaymentMethodList();
		}
		if ( options.contains(ResponseOption.REPORT_TYPE)) {
			makeReportTypeList(sessionData);
		}
		if ( options.contains(ResponseOption.WORK_HOURS_TYPE)) {
			makeWorkHoursTypeList(sessionData);
		}
		if ( options.contains(ResponseOption.EXPENSE_TYPE)) {
			makeExpenseTypeList(sessionData);
		}
	}

	private void makeInvoiceStyleList() {
		this.invoiceStyle = new ArrayList<InvoiceStyleOption>();
		for ( InvoiceStyle j : EnumSet.allOf(InvoiceStyle.class)) {
			this.invoiceStyle.add(new InvoiceStyleOption(j));
		}
		Collections.sort(this.invoiceStyle);
	}
	
	private void makeAccountTypeList() {
		this.accountType = new ArrayList<AccountTypeOption>();
		for (AccountType j : EnumSet.allOf(AccountType.class)) {
			this.accountType.add(new AccountTypeOption(j));
		}
		Collections.sort(this.accountType);
	}

	private void makePaymentMethodList() {
		this.paymentMethod = new ArrayList<PaymentMethodOption>();
		for (PaymentMethod j : EnumSet.allOf(PaymentMethod.class)) {
			this.paymentMethod.add(new PaymentMethodOption(j));
		}
		Collections.sort(this.paymentMethod);
	}

	private void makeInvoiceGroupList() {
		this.invoiceGrouping = new ArrayList<InvoiceGroupingOption>();
		for ( InvoiceGrouping j : EnumSet.allOf(InvoiceGrouping.class)) {
			this.invoiceGrouping.add(new InvoiceGroupingOption(j));
		}
		Collections.sort(this.invoiceGrouping);
	}

	private void makeInvoiceTermList() {
		this.invoiceTerm = new ArrayList<InvoiceTermOption>();
		for ( InvoiceTerm j : EnumSet.allOf(InvoiceTerm.class)) {
			this.invoiceTerm.add(new InvoiceTermOption(j));
		}
		Collections.sort(this.invoiceTerm);
	}

	private void makePermissionList() {
		this.permission = new ArrayList<Permission>();
		for(Permission j : EnumSet.allOf(Permission.class)) {
			this.permission.add(j);
		}
	}

	private void makeJobFrequencyList() {
		this.jobFrequency = new ArrayList<JobFrequencyOption>();
		for(JobFrequency j : EnumSet.allOf(JobFrequency.class)) {
			this.jobFrequency.add(new JobFrequencyOption(j));
		}
		Collections.sort(this.jobFrequency);
	}
	
	private void makeJobStatusList() {
		this.jobStatus = new ArrayList<JobStatusOption>();
		for(JobStatus j : EnumSet.allOf(JobStatus.class)) {
			this.jobStatus.add(new JobStatusOption(j));
		}
		Collections.sort(this.jobStatus);
	}
	
	private void makeTicketStatusList() {
		this.ticketStatus = new ArrayList<TicketStatusOption>();
		for(TicketStatus j : EnumSet.allOf(TicketStatus.class)) {
			this.ticketStatus.add(new TicketStatusOption(j));
		}
		Collections.sort(this.ticketStatus);
	}
	
	private void makeCountryList() throws IOException {
		this.country = new ArrayList<CountryOption>();
		
		for(Country j : EnumSet.allOf(Country.class)) {
			this.country.add(new CountryOption(j));
		}
		//don't sort country; we want USA first
	}
		
	
	private void makeReportTypeList(SessionData sessionData) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {
		this.reportType = new ArrayList<ReportTypeOption>();
		for ( ReportType reportType : ReportType.values()) {
			if ( sessionData.hasPermission(reportType.getPermission().name())) {
				String reportClassName = reportType.reportClassName();
				Class<?> reportClass = Class.forName(reportClassName);
				Field field = reportClass.getDeclaredField("REPORT_TITLE");
				String title = (String)field.get(null);
				Permission requiredPermission = reportType.getPermission();
				this.reportType.add(new ReportTypeOption(reportType.toString(), title, requiredPermission));
			}
		}
		Collections.sort(this.reportType);

	}
	
	
	
	private void makeWorkHoursTypeList(SessionData sessionData) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {
		this.workHoursType = new ArrayList<WorkHoursTypeOption>();
		for ( WorkHoursType workHoursType : WorkHoursType.values()) {
			this.workHoursType.add(new WorkHoursTypeOption(workHoursType));
		}
		Collections.sort(this.workHoursType);

	}
	
	private void makeExpenseTypeList(SessionData sessionData) throws ClassNotFoundException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException  {
		this.expenseType = new ArrayList<ExpenseTypeOption>();
		for ( EmployeeHoursType expenseType : EmployeeHoursType.values()) {
			this.expenseType.add(new ExpenseTypeOption(expenseType));
		}
		Collections.sort(this.expenseType);

	}
	
	public List<JobFrequencyOption> getJobFrequency() {
		return jobFrequency;
	}

	public void setJobFrequency(List<JobFrequencyOption> jobFrequency) {
		this.jobFrequency = jobFrequency;
	}

	public List<AccountTypeOption> getAccountType() {
		return accountType;
	}

	public void setAccountType(List<AccountTypeOption> accountType) {
		this.accountType = accountType;
	}
	
	public List<JobStatusOption> getJobStatus() {
		return jobStatus;
	}
	
	public void setJobStatus(List<JobStatusOption> jobStatus) {
		this.jobStatus = jobStatus;
	}
	
	public List<CountryOption> getCountry() {
		return country;
	}
	
	public void setCountry(List<CountryOption> country) {
		this.country = country;
	}
	
	public List<TicketStatusOption> getTicketStatus() {
		return ticketStatus;
	}

	public void setTicketStatus(List<TicketStatusOption> ticketStatus) {
		this.ticketStatus = ticketStatus;
	}

	public List<Permission> getPermission() {
		return permission;
	}

	public void setPermission(List<Permission> permission) {
		this.permission = permission;
	}

	public List<InvoiceGroupingOption> getInvoiceGrouping() {
		return invoiceGrouping;
	}

	public void setInvoiceGrouping(List<InvoiceGroupingOption> invoiceGrouping) {
		this.invoiceGrouping = invoiceGrouping;
	}

	public List<InvoiceTermOption> getInvoiceTerm() {
		return invoiceTerm;
	}

	public void setInvoiceTerm(List<InvoiceTermOption> invoiceTerm) {
		this.invoiceTerm = invoiceTerm;
	}

	public List<InvoiceStyleOption> getInvoiceStyle() {
		return invoiceStyle;
	}

	public List<PaymentMethodOption> getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(List<PaymentMethodOption> paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public void setInvoiceStyle(List<InvoiceStyleOption> invoiceStyle) {
		this.invoiceStyle = invoiceStyle;
	}

	public List<ReportTypeOption> getReportType() {
		return reportType;
	}

	public void setReportType(List<ReportTypeOption> reportType) {
		this.reportType = reportType;
	}

	public List<WorkHoursTypeOption> getWorkHoursType() {
		return workHoursType;
	}

	public void setWorkHoursType(List<WorkHoursTypeOption> workHoursType) {
		this.workHoursType = workHoursType;
	}

	public List<ExpenseTypeOption> getExpenseType() {
		return expenseType;
	}

	public void setExpenseType(List<ExpenseTypeOption> expenseType) {
		this.expenseType = expenseType;
	}


	
}
