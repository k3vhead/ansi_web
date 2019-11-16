package com.ansi.scilla.web.specialOverride.common;

public enum SpecialOverrideType {
	
	UPDATE_PAYMENT_DATE(
		"Update Payment Date",
		"select * from payment where payment_id=? and payment_date=?",
		"update payment set payment_date=? where payment_id=?",
		new ParameterType[] { 
				new ParameterType("Payment Id", "payment_id", Integer.class, ParameterType.integerValidatorMethod),
				new ParameterType("Payment Date", "payment_date", java.sql.Date.class, ParameterType.dateValidatorMethod)
			},
		new ParameterType[] { 
				new ParameterType("New Date", "new_payment_date", java.sql.Date.class, ParameterType.dateValidatorMethod),
				new ParameterType("Payment Id", "payment_id", Integer.class, ParameterType.integerValidatorMethod)
			}
		);

	

	private final String display;
	private final String selectSql;
	private final String updateSql;
	private final ParameterType[] selectParms;
	private final ParameterType[] updateParms;
	
	private SpecialOverrideType(String display, String selectSql, String updateSql, ParameterType[] selectParms, ParameterType[] updateParms) {
		this.display = display;
		this.selectSql = selectSql;
		this.updateSql = updateSql;
		this.selectParms = selectParms;
		this.updateParms = updateParms;
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
	
	
}
