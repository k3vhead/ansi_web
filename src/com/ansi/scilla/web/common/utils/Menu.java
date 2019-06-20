package com.ansi.scilla.web.common.utils;

import java.util.ArrayList;
import java.util.List;

public enum Menu {
	DASHBOARD("Dashboard", null, null,"#"),
	
	
	LOOKUPS("Lookups", null, null,"#"),
	
	
	REPORTS("Reports", null, null,"#"),
	
	
	QUICK_LINKS("Quick Links", null, null,"#"),
	
	
	SETTINGS("Settings", null, Permission.SYSADMIN_READ,"#"),
	TAX_MAINTENANCE("Tax Maintenance",SETTINGS,Permission.SYSADMIN_READ,"taxRateMaintenance.html"),
	CODES("Codes",SETTINGS,Permission.SYSADMIN_READ,"taxRatcodeMaintenanceeMaintenance.html"),
	DIVISIONS("Divisions",SETTINGS,Permission.SYSADMIN_READ,"divisionAdmin.html"),
	USER_LOOKUP("User Lookup",SETTINGS,Permission.SYSADMIN_READ,"userLookup.html"),
	PERMISSIONS("Permissions",SETTINGS,Permission.SYSADMIN_READ,"permissionGroup.html"),
	LOCALE("Locale", SETTINGS, Permission.SYSADMIN_READ, "localeLookup.html"),

	
	MY_ANSI("My ANSI", null, null,"#"),
	
	
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
}
