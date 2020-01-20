package com.ansi.scilla.web.specialOverride.common;

import java.math.BigDecimal;

import com.ansi.scilla.web.common.utils.Permission;

public enum SpecialOverrideType {
	
	UPDATE_PAYMENT_DATE(
		"Update Payment Date",
		"select * from payment where payment_id=? and payment_date=?",
		new ParameterType[] { 
				new ParameterType("Payment Id", "payment_id", Integer.class), 
				new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
			},
		"update payment set payment_date=? where payment_date=? and payment_id=?",
		new ParameterType[] { 
				new ParameterType("New Payment Date", "new_payment_date", java.sql.Date.class), 
				new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				new ParameterType("Payment Id", "payment_id", Integer.class), 
			},
		"select * from payment where payment_date in (?,?) and payment_id=?",
		new ParameterType[] { 
				new ParameterType("New Payment Date", "new_payment_date", java.sql.Date.class), 
				new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				new ParameterType("Payment Id", "payment_id", Integer.class), 
			},
		Permission.PAYMENT_OVERRIDE
	),
	
	UPDATE_TICKET_PAYMENT_TICKET(
			"Update Ticket Payment Ticket",
			"select * from ticket_payment where ticket_id in (?,?) and payment_id=?",
			new ParameterType[] { 
					new ParameterType("Old Ticket ID", "ticket_id", Integer.class), 
					new ParameterType("New Ticket ID", "new_ticket_id", Integer.class),
					new ParameterType("Payment ID", "payment_id", Integer.class), 
				},
			"update ticket_payment set ticket_id=? where ticket_id=? and payment_id=?",
			new ParameterType[] { 
					new ParameterType("New Ticket ID", "new_ticket_id", Integer.class),
					new ParameterType("Old Ticket ID", "ticket_id", Integer.class),
					new ParameterType("Payment ID", "payment_id", Integer.class), 
				},
			"select * from ticket_payment where ticket_id in (?,?) and payment_id=?",
			new ParameterType[] { 
					new ParameterType("Old Ticket ID", "ticket_id", Integer.class), 
					new ParameterType("New Ticket ID", "new_ticket_id", Integer.class),
					new ParameterType("Payment ID", "payment_id", Integer.class), 
				},
			Permission.PAYMENT_OVERRIDE
		),

	UPDATE_TICKET_PAYMENT_AMOUNT(
			"Update Ticket Payment Amount",
			"select * from ticket_payment where ticket_id=? and payment_id=?",
			new ParameterType[] { 
					new ParameterType("Ticket ID", "ticket_id", Integer.class), 
					new ParameterType("Payment ID", "payment_id", Integer.class), 
				},
			"update ticket_payment set amount=?, tax_amt=? where ticket_id=? and payment_id=?",
			new ParameterType[] { 
					new ParameterType("Amount", "amount", BigDecimal.class),
					new ParameterType("Tax Amount", "tax_amt", BigDecimal.class),
					new ParameterType("Ticket ID", "ticket_id", Integer.class),
					new ParameterType("Payment ID", "payment_id", Integer.class), 
				},
			"select * from ticket_payment where ticket_id=? and payment_id=?",
			new ParameterType[] { 
					new ParameterType("Ticket ID", "ticket_id", Integer.class), 
					new ParameterType("Payment ID", "payment_id", Integer.class), 
				},
			Permission.PAYMENT_OVERRIDE
		),
	;

	

	private final String display;
	private final String selectSql;
	private final String updateSql;
	private final String updateSelectSql;
	private final ParameterType[] selectParms;
	private final ParameterType[] updateParms;
	private final ParameterType[] updateSelectParms;
	private final Permission permission;
	
	private SpecialOverrideType(
			String display, 
			String selectSql, 
			ParameterType[] selectParms, 
			String updateSql, 
			ParameterType[] updateParms, 
			String updateSelectSql, 
			ParameterType[] updateSelectParms, 
			Permission permission) {
		this.display = display;
		this.selectSql = selectSql;
		this.updateSql = updateSql;
		this.updateSelectSql = updateSelectSql;
		this.selectParms = selectParms;
		this.updateParms = updateParms;
		this.permission = permission;
		this.updateSelectParms = updateSelectParms;
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

	public String getUpdateSelectSql() {
		return updateSelectSql;
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
	
	public ParameterType[] getUpdateSelectParms() {
		return updateSelectParms;
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