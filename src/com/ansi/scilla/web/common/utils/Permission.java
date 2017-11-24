package com.ansi.scilla.web.common.utils;

/**
 * Enumerates permissions. These are the permissions that will be grouped to make a
 * permission group. Each function will have Read/Write/None privilege
 * @author DC Lewis
 *
 */
public enum Permission {
	QUOTE(false),
	JOB(true),
	TICKET(true),
	PAYMENT(false),
	INVOICE(false),
	SYSADMIN(false),
	USER_ADMIN(false),
	TECH_ADMIN(false);

	private final Boolean divisionSpecific;
	Permission(Boolean divisionSpecific) {
		this.divisionSpecific = divisionSpecific;
	}
	public Boolean isDivisionSpecific() {
		return divisionSpecific;
	}
}
