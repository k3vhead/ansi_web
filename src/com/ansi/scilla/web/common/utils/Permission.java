package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

/**
 * Enumerates permissions. These are the permissions that will be grouped to make a
 * permission group. Each function will have Read/Write/None privilege
 * @author DC Lewis
 *
 */
public enum Permission {
	QUOTE(null, false),  
	QUOTE_READ(QUOTE, false),		// this is for backwards compatibility
	QUOTE_WRITE(QUOTE_READ, false),		// this is for backwards compatibility
	QUOTE_CREATE(QUOTE_WRITE, false),		// can create quotes and edit them until they have been proposed
	QUOTE_UPDATE(QUOTE_CREATE, false),    // can create quotes, edit them at any time (including after proposal)
	QUOTE_PREVIEW(QUOTE_READ, false),
	QUOTE_PROPOSE(QUOTE_UPDATE, false),
	
	JOB(null, true),
	JOB_READ(JOB, true),		// this is for backwards compatibility
	JOB_WRITE(JOB_READ, true),		// this is for backwards compatibility
	
	TICKET(null, true),
	TICKET_READ(TICKET, true),		// this is for backwards compatibility
	TICKET_WRITE(TICKET_READ, true),		// this is for backwards compatibility
	
	TICKET_SPECIAL_OVERRIDE(null, false),    // specifically needed for invoice date
	TICKET_SPECIAL_OVERRIDE_READ(TICKET_SPECIAL_OVERRIDE, false),    // this is for backwards compatibility
	TICKET_SPECIAL_OVERRIDE_WRITE(TICKET_SPECIAL_OVERRIDE_READ, false),    // this is for backwards compatibility
	
	
	PAYMENT(null, false),
	PAYMENT_READ(PAYMENT, true),		// this is for backwards compatibility
	PAYMENT_WRITE(PAYMENT_READ, true),		// this is for backwards compatibility
	
	INVOICE(null, false),
	INVOICE_READ(INVOICE, true),		// this is for backwards compatibility
	INVOICE_WRITE(INVOICE_READ, true),		// this is for backwards compatibility
	
	SYSADMIN(null, false),
	SYSADMIN_READ(SYSADMIN, true),		// this is for backwards compatibility
	SYSADMIN_WRITE(SYSADMIN_READ, true),		// this is for backwards compatibility
	
	USER_ADMIN(null, false),
	USER_ADMIN_READ(USER_ADMIN, true),		// this is for backwards compatibility
	USER_ADMIN_WRITE(USER_ADMIN_READ, true),		// this is for backwards compatibility
	
	TECH_ADMIN(null, false),
	TECH_ADMIN_READ(TECH_ADMIN, true),		// this is for backwards compatibility
	TECH_ADMIN_WRITE(TECH_ADMIN_READ, true),		// this is for backwards compatibility
	;
	
	
	private final Boolean divisionSpecific;
	private final Permission parent;
	
	Permission(Permission parent, Boolean divisionSpecific) {
		this.parent = parent;
		this.divisionSpecific = divisionSpecific;
	}
	public Permission getParent() {
		return parent;
	}
	public Boolean isDivisionSpecific() {
		return divisionSpecific;
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
