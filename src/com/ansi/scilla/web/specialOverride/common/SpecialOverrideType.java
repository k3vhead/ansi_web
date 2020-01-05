package com.ansi.scilla.web.specialOverride.common;

import com.ansi.scilla.web.common.utils.Permission;

public enum SpecialOverrideType {
	
	UPDATE_PAYMENT_DATE(
		"Update Payment Date",
		"select * from payment where payment_id=? and payment_date=?",
		"update payment set payment_date=? where payment_id=?",
		new ParameterType[] { 
				new ParameterType("Payment Id", "payment_id", Integer.class), 
				new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
			},
		new ParameterType[] { 
				new ParameterType("New Date", "new_payment_date", java.sql.Date.class), 
				new ParameterType("Payment Id", "payment_id", Integer.class), 
			},
		Permission.OVERRIDE_UPDATE_PAYMENTS
	);

	

	private final String display;
	private final String selectSql;
	private final String updateSql;
	private final ParameterType[] selectParms;
	private final ParameterType[] updateParms;
	private final Permission permission;
	
	private SpecialOverrideType(String display, String selectSql, String updateSql, ParameterType[] selectParms, 
			ParameterType[] updateParms, Permission permission) {
		this.display = display;
		this.selectSql = selectSql;
		this.updateSql = updateSql;
		this.selectParms = selectParms;
		this.updateParms = updateParms;
		this.permission = permission;
	}

	public String getDisplay() {
		return display;
	}

	public String getSelectSql() {
		return selectSql;
	}

	public String getUpdateSql() {
		return updateSql;
	}

	public ParameterType[] getSelectParms() {
		return selectParms;
	}

	public ParameterType[] getUpdateParms() {
		return updateParms;
	}
	
	public Permission getPermission() {
		return permission;
	}
	
	public static String[] names() {
		String[] names = new String[SpecialOverrideType.values().length];
		int i = 0;
		for ( SpecialOverrideType reference : SpecialOverrideType.values() ) {
			names[i] = reference.name();
			i++;
		}
		return names;
	}
	
}
