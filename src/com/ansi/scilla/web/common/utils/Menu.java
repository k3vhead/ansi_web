package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;
import com.ansi.scilla.common.utils.Permission;

public enum Menu {
	DASHBOARD("Dashboard", null, null,"dashboard.html"),
	
	
	LOOKUPS("Lookups", null, null,"#"),
	ADDRESSES(			"Addresses", 			LOOKUPS, Permission.ADDRESS_READ, 	"addressMaintenance.html"),
	CONTACTS(			"Contacts", 			LOOKUPS, Permission.CONTACT_READ, 	"contactMaintenance.html"),
	QUOTES(				"Quotes",				LOOKUPS, Permission.QUOTE_READ, 	"quoteLookup.html"),
	JOBS(				"Jobs", 				LOOKUPS, Permission.QUOTE_READ, 	"jobLookup.html"),
	JOBS_PAC(			"Jobs (PAC)", 			LOOKUPS, Permission.QUOTE_READ,		"jobLookup.html?type=PAC"),
	JOBS_CONTACT(		"Jobs (Contact)", 		LOOKUPS, Permission.QUOTE_READ,		"jobLookup.html?type=CONTACT"),
	JOBTAGS(			"Job Tags", 			LOOKUPS, Permission.QUOTE_READ,		"jobTagLookup.html"),
	TICKETS(			"Tickets", 				LOOKUPS, Permission.TICKET_READ,	"ticketLookup.html"),
//	TICKET_ASSIGNMENT(	"Ticket Assignments",	LOOKUPS, Permission.TICKET_READ,	"ticketAssignmentLookup.html"),
	TICKET_CLAIMS(      "Ticket Claims",        LOOKUPS, Permission.CLAIMS_READ,    "claimLookup.html"),
	INVOICES(			"Invoices", 			LOOKUPS, Permission.INVOICE_READ, 	"invoiceLookup.html"),
	INVOICE_DETAIL(     "Invoice Detail",       LOOKUPS, Permission.INVOICE_READ,   "invoiceDetailLookup.html"),
	PAYMENTS(			"Payments", 			LOOKUPS, Permission.PAYMENT_READ, 	"paymentLookup.html"),
	LOCALE(             "Locale",               LOOKUPS, Permission.TAX_READ,       "localeLookup.html"),
	//TAXRATE_LOOKUP(	    "Tax Rate Lookup",		LOOKUPS, Permission.TAX_READ,	    "taxRateLookup.html"),
	//LOCALE_DIVISION(	"Nexus Taxed Lookup",	LOOKUPS, Permission.TAX_READ,		"localeDivisionLookup.html"),
	CALL_NOTES(         "Call Notes",           LOOKUPS, Permission.CALL_NOTE_READ, "callNoteLookup.html"),
	CALENDAR(           "ANSI Calendar",        LOOKUPS, Permission.CALENDAR_READ,  "calendarLookup.html"),
	DOCUMENT_LOOKUP(	"Document", 			LOOKUPS, Permission.DOCUMENT_READ, "documentLookup.html"),

	
	REPORTS("Reports", null, null,"#"),
	BATCH_LOG(          "Batch Log",            REPORTS, Permission.BATCH_LOG_READ, "batchLog.html"),
	DRV30(				"30 Day DRV",			REPORTS, Permission.TICKET_READ,	"ticketView.html"),
	PAST_DUE(			"Past Due",				REPORTS, Permission.TECH_ADMIN,		"pastDueReport.html"),
	
	
	
	QUICK_LINKS("Quick Links", null, null,"#"),
	NEW_CONTACT(		"New Contact", 			QUICK_LINKS, Permission.CONTACT_WRITE,	"newContact.html"),
	NEW_ADDRESS(		"New Address", 			QUICK_LINKS, Permission.ADDRESS_WRITE,	"newAddress.html"),
	NEW_QUOTE(			"New Quote", 			QUICK_LINKS, Permission.QUOTE_CREATE,	"newQuote.html"),
	GENERATE_TICKETS(	"Generate Tickets", 	QUICK_LINKS, Permission.TICKET_WRITE,	"ticketGeneration.html"),
	PRINT_TICKETS(		"Print Tickets", 		QUICK_LINKS, Permission.TICKET_WRITE,	"ticketPrint.html"),
	ASSIGN_TICKETS(		"Assign Tickets", 		QUICK_LINKS, Permission.TICKET_WRITE,	"ticketAssignment.html"),
	TICKET_RETURN(		"Ticket Return", 		QUICK_LINKS, Permission.TICKET_WRITE,	"ticketReturn.html"),
	GENERATE_INVOICES(	"Generate Invoices", 	QUICK_LINKS, Permission.INVOICE_WRITE,	"invoiceGeneration.html"),
	PRINT_INVOICES(		"Print Invoices", 		QUICK_LINKS, Permission.INVOICE_WRITE,	"invoicePrint.html"),
	ENTER_PAYMENT(		"Enter Payment", 		QUICK_LINKS, Permission.PAYMENT_WRITE,	"payment.html"),
	TICKET_OVERRIDE(	"Ticket Override", 		QUICK_LINKS, Permission.TICKET_WRITE,	"ticketOverride.html"),
//	CLAIM_ENTRY(		"Claim Entry", 			QUICK_LINKS, Permission.CLAIMS_WRITE,	"claimEntry.html"),
	SPECIAL_OVERRIDE(	"Special Override",		QUICK_LINKS, Permission.SPECIAL_OVERRIDE_READ, "specialOverride.html"),
	DIVISION_CLOSE(     "Division Close",       QUICK_LINKS, Permission.DIVISION_CLOSE_READ, "divisionClose.html"),
	BCR(                "Budget Control",       QUICK_LINKS, Permission.CLAIMS_WRITE,   "budgetControl.html"),
	
	
	PAYROLL("Payroll", null, Permission.PAYROLL_READ, "#"),
	PAYROLL_EMPLOYEE_LOOKUP("Employee Lookup",   PAYROLL,    Permission.PAYROLL_READ,   "payrollEmployeeLookup.html"),
	PAYROLL_TIMESHEET_LOOKUP("Timesheet Lookup", PAYROLL,    Permission.PAYROLL_READ,   "payrollTimesheetLookup.html"),
	PAYROLL_TAX_PROFILE_LOOKUP("Tax Profile Lookup", PAYROLL, Permission.PAYROLL_READ,  "payrollTaxProfileLookup.html"),
	PAYROLL_EMPLOYEE_IMPORT("Employee Import",   PAYROLL,    Permission.PAYROLL_WRITE,  "payrollEmployeeImport.html"),
	PAYROLL_WORKSHEET_IMPORT("Timesheet Import",   PAYROLL,    Permission.PAYROLL_WRITE,  "payrollTimesheetImport.html"),
	PAYROLL_EXCEPTION_REPORT("Exception Report", PAYROLL,    Permission.PAYROLL_WRITE,  "payrollExceptionReport.html"),
	PAYROLL_EXPORT("Payroll Export",             PAYROLL,    Permission.PAYROLL_WRITE,  "payrollExport.html"),
	
	
	SETTINGS("Settings", null, Permission.SYSADMIN_READ,"#"),
	// TAX_MAINTENANCE(	"Tax Rate Archive",		SETTINGS,	Permission.SYSADMIN_READ,	"taxRateMaintenance.html"),
	TAX_MAINTENANCE(	"Tax Maintenance",		SETTINGS,	Permission.SYSADMIN_READ,	"taxRateMaintenance.html"),
	CODES(				"Codes",				SETTINGS,	Permission.SYSADMIN_READ,	"codeMaintenance.html"),
	DIVISIONS(			"Divisions",			SETTINGS,	Permission.SYSADMIN_READ,	"divisionAdmin.html"),
	COMPANIES(          "Companies",            SETTINGS,   Permission.SYSADMIN_READ,   "companies.html"),
	REGIONS(            "Regions",              SETTINGS,   Permission.SYSADMIN_READ,   "regions.html"),
	DIVISION_GROUPS(    "Division Groups",      SETTINGS,   Permission.SYSADMIN_READ,   "groups.html"),
	USER_LOOKUP(		"User Lookup",			SETTINGS,	Permission.SYSADMIN_READ,	"userLookup.html"),
	PERMISSIONS(		"Permissions",			SETTINGS,	Permission.SYSADMIN_READ,	"permissionGroup.html"),
	REPORT_SUBSCRIPTION("Report Subscriptions", SETTINGS,   Permission.REPORT_SUBSCRIPTION_OVERRIDE, "reportSubscriptionAdmin.html"),
	APP_PROPERTY(       "App Properties",       SETTINGS,   Permission.SYSADMIN_READ,   "applicationPropertiesLookup.html"),
	MOTD(               "Message of the Day",   SETTINGS,   Permission.SYSADMIN_WRITE,  "motd.html"),

	
	MY_ANSI("My ANSI", null, null,"#"),
	MY_ACCOUNT(         "My Account",           MY_ANSI,    null,                       "myAccount.html"),
//	MY_REPORTS(         "Report Subscriptions", MY_ANSI,    Permission.REPORT_SUBSCRIPTION_WRITE, "reportSubscription.html"),
	LOGOFF(             "Logoff",               MY_ANSI,    null,                       "logoff.html"),
	
	;
	
	private final String displayText;
	private final Menu parent;
	private final Permission permissionRequired;
	private final String link;
	
	private Menu(String displayText, Menu parent, Permission permissionRequired, String link) {
		this.displayText = displayText;
		this.parent = parent;
		this.permissionRequired = permissionRequired;
		this.link = link; 
	}
	
	public String getDisplayText() { return this.displayText; }
	public Menu getParent() { return this.parent; }
	public Permission getPermissionRequired() { return this.permissionRequired; }
	public String getLink() { return this.link; }
	
	public static List<Menu> topLevel() {
		List<Menu> topLevel = new ArrayList<Menu>();
		for ( Menu menu : Menu.values()) {
			if ( menu.getParent() == null ) {
				topLevel.add(menu);
			}
		}
		return topLevel;
	}
	
	public static List<Menu> makeSubMenu(Menu menu) {
		List<Menu> subMenu = new ArrayList<Menu>();
		for ( Menu m : Menu.values() ) {
			if (m.getParent() != null && m.getParent().equals(menu)) {
				subMenu.add(m);
			}
		}
		return subMenu;
	}
}
