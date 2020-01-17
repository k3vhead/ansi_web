package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.list.SetUniqueList;

/**, ""
 * Enumerates permissions. These are the permissions that will be grouped to make a
 * permission group. Each function will have Read/Write/None privilege
 * @author DC Lewis
 *
 */
public enum Permission {
	/**
	 * 	QUOTE
	 * 		QUOTE_READ
	 * 			QUOTE_CREATE
	 * 				QUOTE_PROPOSE
	 * 					QUOTE_UPDATE
	 */
	
	QUOTE(null, false, "Functional area: Quotes"),  
	QUOTE_READ(QUOTE, false, "Read-only access to quotes"),		// this is for backwards compatibility
	QUOTE_CREATE(QUOTE_READ, false, "Create quotes; edit until quotes are proposed"),		// can create quotes and edit them until they have been proposed
	QUOTE_PROPOSE(QUOTE_CREATE, false, "Can propose quotes"),
	QUOTE_UPDATE(QUOTE_PROPOSE, false, "Edit quotes at any time, including after they are proposed"),    // can create quotes, edit them at any time (including after proposal)
	QUOTE_OVERRIDE(QUOTE_UPDATE, false, "Edit locked quote fields at any time, including after they are proposed"),    // can do things like uncancel/unactivate/etc.
	
	/**
	 * 11/5/2018 - Job permissions are merged into quote permissions. (You need quote permission to do anything with jobs)
	 * 
	JOB(null, true, "Functional area: Job"),
	JOB_READ(JOB, true, "Read-only access to Jobs  (Backward Compatibility)"),		// this is for backwards compatibility
	JOB_WRITE(JOB_READ, true, "Can Edit jobs"),   // this is for backwards compatibility
	**/
	
	TICKET(null, true, "Functional area: Tickets"),
	TICKET_READ(TICKET, true, "Read-only access to Tickets"),		// this is for backwards compatibility
	TICKET_WRITE(TICKET_READ, true, "Edit Tickets"),		// this is for backwards compatibility
	TICKET_OVERRIDE(TICKET_WRITE, true, "Change locked fields"),		// this is for backwards compatibility
	
	TICKET_SPECIAL_OVERRIDE(null, false, "Functional Area: Ticket Override"),    // specifically needed for invoice date
	TICKET_SPECIAL_OVERRIDE_READ(TICKET_SPECIAL_OVERRIDE, false, "Bakcward compatibility"),    // this is for backwards compatibility
	TICKET_SPECIAL_OVERRIDE_WRITE(TICKET_SPECIAL_OVERRIDE_READ, false, "Edit invoice date"),    // this is for backwards compatibility
	
	
	PAYMENT(null, false, "Functional Area: Payments"),
	PAYMENT_READ(PAYMENT, true, "Read only access to payments"),		// this is for backwards compatibility
	PAYMENT_WRITE(PAYMENT_READ, true, "Edit Payments"),		// this is for backwards compatibility
	PAYMENT_OVERRIDE(PAYMENT_WRITE, true, "Override Payments"),
	
	INVOICE(null, false, "Functional Area: Invoices"),
	INVOICE_READ(INVOICE, true, "Read-only access to invoices"),		// this is for backwards compatibility
	INVOICE_WRITE(INVOICE_READ, true, "Edit invoices"),		// this is for backwards compatibility
	
	SYSADMIN(null, false, "Functional Area: System Admin"),
	SYSADMIN_READ(SYSADMIN, true, "Read-only acces to System Admin"),		// this is for backwards compatibility
	SYSADMIN_WRITE(SYSADMIN_READ, true, "Edit system parameters"),		// this is for backwards compatibility
	
	USER_ADMIN(null, false, "Functional Area: User Admin"),
	USER_ADMIN_READ(USER_ADMIN, true, "See user list and details"),		// this is for backwards compatibility
	USER_ADMIN_WRITE(USER_ADMIN_READ, true, "Add/Edit users; reset passwords"),		// this is for backwards compatibility
	USER_ADMIN_PAYROLL(USER_ADMIN_WRITE, true, "Administer payroll info for users"),
	
	TAX_READ(null, false, "Read-only access to Taxes"),
	TAX_WRITE(TAX_READ, true, "Can write and Update Taxes"),
	TAX_OVERRIDE(TAX_WRITE, true, "Can delete Taxes"),
	
	TECH_ADMIN(null, false, "Functional Area: Technical Admin"),
	TECH_ADMIN_READ(TECH_ADMIN, true, "See technical system parameters"),		// this is for backwards compatibility
	TECH_ADMIN_WRITE(TECH_ADMIN_READ, true, "Add/Edit technical system parameters"),		// this is for backwards compatibility
	
	ADDRESS(null, false, "Functional Area: Customer Addresses"),
	ADDRESS_READ(ADDRESS, false, "Read customer addresses"),
	ADDRESS_WRITE(ADDRESS_READ, false, "Add/Update customer addresses"),
	
	CONTACT(null, false, "Functional Area: Customer Contact"),
	CONTACT_READ(CONTACT, false, "Read customer contact info"),
	CONTACT_WRITE(CONTACT_READ, false, "Write customer contact info"),
	
	CUSTOMER(null, false, "Functional Area: Customer Interaction"),
	CAN_WRITE_QUOTE(CUSTOMER, false, "Can request a quote to be written (can be in the manager field on a quote)"),
	
	ACTIVITIES(null, false, "Can Perform External Activities"),
	CAN_RUN_TICKETS(ACTIVITIES, true, "Can wash windows, etc"),
	CAN_COMPLETE_TICKETS(CAN_RUN_TICKETS, true, "Can set tickets to ready-to-bill"),
	
	PERMISSIONS(null, false, "Functional Area: Permissions"),
	PERMISSIONS_READ(PERMISSIONS, false, "Can read permission group assignments"),
	PERMISSIONS_WRITE(PERMISSIONS_READ, false, "Can edit permission groups, assign permissions to group"),
	
	CLAIMS(null, false, "Functional Area: Claims"),
	CLAIMS_READ(CLAIMS, false, "Can read ticket claims"),
	CLAIMS_WRITE(CLAIMS_READ, false, "Can edit ticket claims"),

	REPORTS_AGING(null, false, "Functional Area: Aging Reports"),
	REPORTS_AGING_READ(REPORTS_AGING, false, "Can read aging reports"),
	
	REPORTS_SIX_MONTH_ROLLING_VOLUME(null, false, "Functional Area: Six Month Rolling Volume Reports"),
	REPORTS_SIX_MONTH_ROLLING_VOLUME_READ(REPORTS_SIX_MONTH_ROLLING_VOLUME, false, "Can read Six Month Rolling Volume Reports"),
	
	REPORTS_CASH_RECEIPTS_REGISTER(null, false, "Functional Area: Cash Receipts Register Reports"),
	REPORTS_CASH_RECEIPTS_REGISTER_READ(REPORTS_CASH_RECEIPTS_REGISTER, false, "Can read aging reports"),
	
	REPORTS_DISPATCHED_AND_OUTSTANDING(null, false, "Functional Area: Dispatched and Outstanding Reports"),
	REPORTS_DISPATCHED_AND_OUTSTANDING_READ(REPORTS_DISPATCHED_AND_OUTSTANDING, false, "Can read DO lists"),
	
	REPORTS_INVOICE_REGISTER(null, false, "Functional Area: Invoice Register Reports"),
	REPORTS_INVOICE_REGISTER_READ(REPORTS_INVOICE_REGISTER, false, "Can read Invoice Register Reports"),
	
	REPORTS_JOB_SCHEDULE(null, false, "Functional Area: Job Schedule Reports"),
	REPORTS_JOB_SCHEDULE_READ(REPORTS_JOB_SCHEDULE, false, "Can read Job Schedule reports"),
	
	REPORTS_PAC(null, false, "Functional Area: PAC Reports"),
	REPORTS_PAC_READ(REPORTS_PAC, false, "Can read PAC reports"),
	
	REPORTS_TICKET_STATUS(null, false, "Functional Area: Ticket Status Reports"),
	REPORTS_TICKET_STATUS_READ(REPORTS_TICKET_STATUS, false, "Can read Ticket Status reports"),
	
	REPORTS_ADDRESS_USAGE(null, false, "Functional Area: Address Usage Reports"),
	REPORTS_ADDRESS_USAGE_READ(REPORTS_ADDRESS_USAGE, false, "Can read address usage reports"),
	
	REPORTS_CLIENT_CONTACT(null, false, "Functional Area: Client Contact Reports"),
	REPORTS_CLIENT_CONTACT_READ(REPORTS_CLIENT_CONTACT, false, "Can read Client Contact reports"),
	
	
	CALL_NOTES(null, false, "Functional Area: Call Notes"),
	CALL_NOTE_READ(CALL_NOTES, false, "Can Read Call Notes"),
	CALL_NOTE_WRITE(CALL_NOTE_READ, false, "Can create notes"),
	CALL_NOTE_UPDATE(CALL_NOTE_WRITE, false, "Can revise your own notes"),
	CALL_NOTE_OVERRIDE(CALL_NOTE_UPDATE, false, "Can revise other's notes"),
	
	
	SPECIAL_OVERRIDE(null, false, "Functional Area: Special Override"),
	SPECIAL_OVERRIDE_READ(SPECIAL_OVERRIDE, false, "Special Override"),
	//OVERRIDE_UPDATE_PAYMENTS(SPECIAL_OVERRIDE_READ, false, "Override payment date"),
	
	DEVELOPMENT(null, false, "Functional Area: System Developers"),
	DEV(DEVELOPMENT, false, "Can Access Developer Areas"),


	DOCUMENTS(null, false, "Functional Area: Documents"),
	DOCUMENTS_READ(DOCUMENTS, false, "Can read stored documents"),
	DOCUMENTS_WRITE(DOCUMENTS_READ, false, "Can store documents"),
	;
	
	private final Boolean divisionSpecific;
	private final Permission parent;
	private final String description;
	
	Permission(Permission parent, Boolean divisionSpecific, String description) {
		this.parent = parent;
		this.divisionSpecific = divisionSpecific;
		this.description = description;
	}
	public Permission getParent() {
		return parent;
	}
	public Boolean isDivisionSpecific() {
		return divisionSpecific;
	}
	public String getDescription() {
		return description;
	}
	
	
	/**
	 * Make a list of permissions that you get because you have this one (ie, this permission,
	 * its parent, its parent's parent, etc)
	 * @return
	 */
	public List<Permission> makeParentList() {
		List<Permission> permissionTree = new ArrayList<Permission>();
		permissionTree.add(this);
		if ( this.parent != null ) {
			makePermissionTree(this.parent, permissionTree);
		}
		
		return permissionTree;
	}
	
	
	/**
	 * Make a list of Permissions that have this one as a parent
	 * @return
	 */
	public List<Permission> makeChildList() {
		List<Permission> childList = new ArrayList<Permission>();
		for ( Permission permission : Permission.values() ) {
			if ( permission.getParent() != null && permission.getParent().equals(this)) {
				childList.add(permission);
			}
		}
		return childList;
	}
	
	/**
	 * List of all permissions that include this one. For example, if this is
	 * QUOTE_CREATE, this list will include QUOTE_PROPOSE and QUOTE_UPDATE,
	 * but not QUOTE_READ 
	 * @return
	 */
	public List<Permission> makeChildTree() {
		List<Permission> permissionList = new ArrayList<Permission>();
		makeChildTree(this, permissionList);
		return permissionList;
	}
	
	private void makeChildTree(Permission permission, List<Permission> permissionList) {
		permissionList.add(permission);
		List<Permission> childList = permission.makeChildList();
		if ( childList != null ) {
			for ( Permission p : childList ) {
				makeChildTree(p, permissionList);
			}
		}
	}
	
	
	
	private void makePermissionTree(Permission permission, List<Permission> permissionTree) {
		if ( permission!= null && ! permissionTree.contains(permission)) {
			permissionTree.add(permission);
			Permission parent = permission.getParent();
			if ( parent != null && ! permissionTree.contains(parent)) {				
				List<Permission> parentList = new ArrayList<Permission>();
				parentList.add(parent);
				makePermissionTree(parent.getParent(), parentList);
				CollectionUtils.addAll(permissionTree, parentList.iterator());
			}
		}
	}
	
	
	/**
	 * Make a list of all permissions in this functional area
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Permission> makeFunctionalAreaList() {
		List<Permission> functionalAreaList = new ArrayList<Permission>();
		CollectionUtils.addAll(functionalAreaList, makeChildTree().iterator());
		CollectionUtils.addAll(functionalAreaList, makeParentList().iterator());
		// remove duplicate values (this permission will be in both lists)
		List<Permission> uniqueList = SetUniqueList.decorate(new ArrayList<Permission>()); 
		uniqueList.addAll(functionalAreaList);
		return uniqueList;
	}
	
//	public static void main(String[] args) {
//		for ( Permission p : Permission.values() ) {
//			if ( p.getParent() == null ) {
//				printPerm(p,0);
//				
//			}
//		}
//	}
//	private static void printPerm(Permission p, int indent) {
//		
//		for ( int i = 0; i < indent; i++ ) {
//			System.out.print("\t");
//		}
//		System.out.println(p);
//	
//		for ( Permission x : p.makeChildList() ) {
//			printPerm(x, indent+1);
//		}
//	
//		
//	}
}