package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**, ""
 * Enumerates permissions. These are the permissions that will be grouped to make a
 * permission group. Each function will have Read/Write/None privilege
 * @author DC Lewis
 *
 */
public enum Permission {
	QUOTE(null, false, "Functional area: Quotes"),  
	QUOTE_READ(QUOTE, false, "Read-only access to quotes"),		// this is for backwards compatibility
	QUOTE_WRITE(QUOTE_READ, false, "Allowed to create quotes (Backward Compatibility)"),		// this is for backwards compatibility
	QUOTE_CREATE(QUOTE_WRITE, false, "Create quotes; edit until quotes are proposed"),		// can create quotes and edit them until they have been proposed
	QUOTE_UPDATE(QUOTE_CREATE, false, "Edit quotes at any time, including after they are proposed"),    // can create quotes, edit them at any time (including after proposal)
	QUOTE_PROPOSE(QUOTE_UPDATE, false, "Can propose quotes"),
	
	JOB(null, true, "Functional area: Job"),
	JOB_READ(JOB, true, "Read-only access to Jobs  (Backward Compatibiity)"),		// this is for backwards compatibility
	JOB_WRITE(JOB_READ, true, "Update jobs"),		// this is for backwards compatibility
	
	TICKET(null, true, "Functional area: Tickets"),
	TICKET_READ(TICKET, true, "Read-only access to Tickets"),		// this is for backwards compatibility
	TICKET_WRITE(TICKET_READ, true, "Edit Tickets"),		// this is for backwards compatibility
	
	TICKET_SPECIAL_OVERRIDE(null, false, "Functional Area: Ticket Override"),    // specifically needed for invoice date
	TICKET_SPECIAL_OVERRIDE_READ(TICKET_SPECIAL_OVERRIDE, false, "Bakcward compatibility"),    // this is for backwards compatibility
	TICKET_SPECIAL_OVERRIDE_WRITE(TICKET_SPECIAL_OVERRIDE_READ, false, "Edit invoice date"),    // this is for backwards compatibility
	
	
	PAYMENT(null, false, "Functional Area: Payments"),
	PAYMENT_READ(PAYMENT, true, "Read only access to payments"),		// this is for backwards compatibility
	PAYMENT_WRITE(PAYMENT_READ, true, "Edit Payments"),		// this is for backwards compatibility
	
	INVOICE(null, false, "Functional Area: Invoices"),
	INVOICE_READ(INVOICE, true, "Read-only access to invoices"),		// this is for backwards compatibility
	INVOICE_WRITE(INVOICE_READ, true, "Edit invoices"),		// this is for backwards compatibility
	
	SYSADMIN(null, false, "Functional Area: System Admin"),
	SYSADMIN_READ(SYSADMIN, true, "Read-only acces to System Admin"),		// this is for backwards compatibility
	SYSADMIN_WRITE(SYSADMIN_READ, true, "Edit system parameters"),		// this is for backwards compatibility
	
	USER_ADMIN(null, false, "Functional Area: User Admin"),
	USER_ADMIN_READ(USER_ADMIN, true, "See user list and details"),		// this is for backwards compatibility
	USER_ADMIN_WRITE(USER_ADMIN_READ, true, "Add/Edit users; reset passwords"),		// this is for backwards compatibility
	
	TECH_ADMIN(null, false, "Functional Area: Technical Admin"),
	TECH_ADMIN_READ(TECH_ADMIN, true, "See technical system parameters"),		// this is for backwards compatibility
	TECH_ADMIN_WRITE(TECH_ADMIN_READ, true, "Add/Edit technical system parameters"),		// this is for backwards compatibility
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
	
	public List<Permission> makeParentList() {
		List<Permission> permissionTree = new ArrayList<Permission>();
		permissionTree.add(this);
		if ( this.parent != null ) {
			makePermissionTree(this.parent, permissionTree);
		}
		
		return permissionTree;
	}
	
	public List<Permission> makeChildList() {
		List<Permission> childList = new ArrayList<Permission>();
		for ( Permission permission : Permission.values() ) {
			if ( permission.getParent() != null && permission.getParent().equals(this)) {
				childList.add(permission);
			}
		}
		return childList;
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
