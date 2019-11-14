package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;

public enum Menu {
	DASHBOARD("Dashboard", null, null,"dashboard.html"),
	
	
	LOOKUPS("Lookups", null, null,"#"),
	ADDRESSES(			"Addresses", 			LOOKUPS, Permission.ADDRESS_READ, 	"addressMaintenance.html"),
	CONTACTS(			"Contacts", 			LOOKUPS, Permission.CONTACT_READ, 	"contactMaintenance.html"),
	QUOTES(				"Quotes",				LOOKUPS, Permission.QUOTE_READ, 	"quoteLookup.html"),
	JOBS(				"Jobs", 				LOOKUPS, Permission.QUOTE_READ, 	"jobLookup.html"),
	JOBS_PAC(			"Jobs (PAC)", 			LOOKUPS, Permission.QUOTE_READ,		"jobLookup.html?type=PAC"),
	JOBS_CONTACT(		"Jobs (Contact)", 		LOOKUPS, Permission.QUOTE_READ,		"jobLookup.html?type=CONTACT"),
	TICKETS(			"Tickets", 				LOOKUPS, Permission.TICKET_READ,	"ticketLookup.html"),
	TICKET_ASSIGNMENT(	"Ticket Assignments",	LOOKUPS, Permission.TICKET_READ,	"ticketAssignmentLookup.html"),
	INVOICES(			"Invoices", 			LOOKUPS, Permission.INVOICE_READ, 	"invoiceLookup.html"),
	PAYMENTS(			"Payments", 			LOOKUPS, Permission.PAYMENT_READ, 	"paymentLookup.html"),
	NON_DIRECT_LABOR(	"Non-Direct Labor", 	LOOKUPS, Permission.CLAIMS_READ, 	"nonDirectLaborLookup.html"),
	EMPLOYEE_EXPENSE(	"Employee Expense", 	LOOKUPS, Permission.CLAIMS_READ, 	"employeeExpenseLookup.html"),
	TICKET_STATUS(		"Ticket Status", 		LOOKUPS, Permission.CLAIMS_READ, 	"ticketStatusLookup.html"),
	BUDGET_CONTROL(		"Budget Control", 		LOOKUPS, Permission.CLAIMS_READ, 	"budgetControlLookup.html"),
	CLAIM_DETAIL(		"Claim Detail", 		LOOKUPS, Permission.CLAIMS_READ, 	"claimDetailLookup.html"),
	LOCALE(             "Locale",               LOOKUPS, Permission.TAX_READ,       "localeLookup.html"),
	TAXRATE_LOOKUP(	    "Tax Rate Lookup",		LOOKUPS, Permission.TAX_READ,	    "taxRateLookup.html"),
	LOCALE_DIVISION(	"Nexus Taxed Lookup",	LOOKUPS, Permission.TAX_READ,		"localeDivisionLookup.html"),
	
	
	REPORTS("Reports", null, null,"#"),
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
	CLAIM_ENTRY(		"Claim Entry", 			QUICK_LINKS, Permission.CLAIMS_WRITE,	"claimEntry.html"),
	
	
	SETTINGS("Settings", null, Permission.SYSADMIN_READ,"#"),

	TAX_MAINTENANCE(	"Tax Rate Archive",		SETTINGS,	Permission.SYSADMIN_READ,	"taxRateMaintenance.html"),
	CODES(				"Codes",				SETTINGS,	Permission.SYSADMIN_READ,	"codeMaintenance.html"),
	DIVISIONS(			"Divisions",			SETTINGS,	Permission.SYSADMIN_READ,	"divisionAdmin.html"),
	USER_LOOKUP(		"User Lookup",			SETTINGS,	Permission.SYSADMIN_READ,	"userLookup.html"),
	PERMISSIONS(		"Permissions",			SETTINGS,	Permission.SYSADMIN_READ,	"permissionGroup.html"),

	
	MY_ANSI("My ANSI", null, null,"#"),
	MY_ACCOUNT("My Account", MY_ANSI,null,"myAccount.html"),
	LOGOFF("Logoff", MY_ANSI,null,"logoff.html"),
	
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
