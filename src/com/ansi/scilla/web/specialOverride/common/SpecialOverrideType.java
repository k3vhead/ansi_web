package com.ansi.scilla.web.specialOverride.common;

import java.math.BigDecimal;

import com.ansi.scilla.common.jobticket.JobStatus;
import com.ansi.scilla.common.jobticket.TicketStatus;
import com.ansi.scilla.common.utils.Permission;


public enum SpecialOverrideType {
	
	
	/*
	CLAIM_ZERO_DOLLAR_TICKET(
		"Claim $0 Ticket",
		"select * from ticket_claim where ticket_id=? and ticket_id in "
		+ "(select * from ticket t join division d on d.division_id = t.act_division_id and division_nbr=?)",
		new ParameterType[] { 
				new ParameterType("Ticket Id", "ticket_id", Integer.class), 
				new ParameterType("Division Nbr", "division_nbr", Integer.class), 
		},
		"This needs to be verified -- asana task looks weird",
		new ParameterType[] { 
				new ParameterType("New Payment Date", "new_payment_date", java.sql.Date.class), 
				new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				new ParameterType("Payment Id", "payment_id", Integer.class), 
		},
		"select * from job where job_id=?",
		new ParameterType[] { 
				new ParameterType("Job Id", "job_id", Integer.class), 
		},
		"Success",
		Permission.QUOTE_CREATE
	),
	*/
	
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
			"Success",
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
			"Success",
			Permission.PAYMENT_OVERRIDE
		),
	
	UPDATE_PAYMENT_NOTE(
			"Update Payment Notes",
			"select * from payment where payment_id=? and payment_date=?",
			new ParameterType[] { 
					new ParameterType("Payment Id", "payment_id", Integer.class), 
					new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				},
			"update payment set payment_note=? where payment_id=? and payment_date=?",
			new ParameterType[] { 
					new ParameterType("Payment Note", "payment_note", String.class),
					new ParameterType("Payment Id", "payment_id", Integer.class),
					new ParameterType("Payment Date", "payment_date", java.sql.Date.class),
				},
			"select * from payment where payment_id=? and payment_date=?",
			new ParameterType[] { 
					new ParameterType("Payment Id", "payment_id", Integer.class), 
					new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				},
			"Success",
			Permission.PAYMENT_OVERRIDE
		),
	
	UPDATE_PAYMENT_AMOUNT(
			"Update Payment Amount",
			"select * from payment where payment_id=? and payment_date=?",
			new ParameterType[] { 
					new ParameterType("Payment Id", "payment_id", Integer.class), 
					new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				},
			"update payment set amount=? where payment_id=? and payment_date=?",
			new ParameterType[] { 
					new ParameterType("Payment Amount", "amount", BigDecimal.class),
					new ParameterType("Payment Id", "payment_id", Integer.class), 
					new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				},
			"select * from payment where payment_id=? and payment_date=?",
			new ParameterType[] { 
					new ParameterType("Payment Id", "payment_id", Integer.class), 
					new ParameterType("Payment Date", "payment_date", java.sql.Date.class), 
				},
			"Success",
			Permission.PAYMENT_OVERRIDE
		),
	
	UNCOMPLETE_TICKET(
			"Uncomplete a Ticket",
			"select * from ticket where ticket_id=? and ticket_status='"+TicketStatus.COMPLETED.code()+"'",
			new ParameterType[] { 
					new ParameterType("Ticket Id", "ticket_id", Integer.class), 
				},
			"update ticket set "
					+ "ticket_status='"+ TicketStatus.DISPATCHED.code() + "', "
					+ "process_date=null, "
					+ "process_notes='completed in error', "
					+ "customer_signature=0, "
					+ "bill_sheet=0, "
					+ "mgr_approval=0 "
				+ "where ticket_status='" + TicketStatus.COMPLETED.code() + "' and ticket_id=?",
			new ParameterType[] { 
					new ParameterType("Ticket Id", "ticket_id", Integer.class), 
				},
			"select * from ticket where ticket_id=?",
			new ParameterType[] { 
					new ParameterType("Ticket Id", "ticket_id", Integer.class), 
				},
			"Success",
			Permission.TICKET_OVERRIDE
		),
	
	UPDATE_TICKET_TO_JOB(
			"Update a Ticket to a Different Job",
			"select * from ticket where ticket_id in (?,?)",
			new ParameterType[] { 
					new ParameterType("Ticket to Change", "chg_ticket_id", Integer.class), 
					new ParameterType("Ticket to Match", "match_ticket_id", Integer.class), 
				},
			"update ticket set job_id = (select job_id from ticket where ticket_id=?), "//ticket to match
			+ "act_division_id = (select act_division_id from ticket where ticket_id=?) "//ticket to match
			+ " where ticket_id=?",//ticket to change
			new ParameterType[] { 
					new ParameterType("Ticket to Match", "match_ticket_id", Integer.class), 
					new ParameterType("Ticket to Match", "match_ticket_id", Integer.class), 
					new ParameterType("Ticket to Change", "chg_ticket_id", Integer.class), 
				},
			"select * from ticket where ticket_id in (?,?)",
			new ParameterType[] { 
					new ParameterType("Ticket to Change", "chg_ticket_id", Integer.class), 
					new ParameterType("Ticket to Match", "match_ticket_id", Integer.class), 
				},
			"Success",
			Permission.PAYMENT_OVERRIDE
		),
	
	UNCANCEL_JOB(
			"Uncancel a Job",
			"select * from job where job_id=? and job_status='"+ JobStatus.CANCELED.code() +"'",
			new ParameterType[] { 
					new ParameterType("Job Id", "job_id", Integer.class),  
				},
			"update job set job_status='"+ JobStatus.ACTIVE.code() +"', "
					+ "cancel_date=null, cancel_reason=? where job_id=? and "
					+ "job_status='"+ JobStatus.CANCELED.code() +"'",
			new ParameterType[] { 
					new ParameterType("Cancel Reason", "cancel_reason", String.class),
					new ParameterType("Job Id", "job_id", Integer.class), 
				},
			"select * from job where job_id=?",
			new ParameterType[] { 
					new ParameterType("Job Id", "job_id", Integer.class), 
				},
			"Make sure to reschedule the job using the existing start date to get the tickets back.",
			Permission.QUOTE_OVERRIDE
		),


	SET_DIVISION_ACTUAL_CLOSE_DATE(
			"Set Division Actual Close Date",
			"select division_id, concat(division_nbr,'-',division_code) as div, description, act_close_date from division where division_id=?",
			new ParameterType[] { 
					new ParameterType("Division Id", "division_id", Integer.class),  
				},
			"update division set act_close_date=? where division_id=?",
			new ParameterType[] { 
					new ParameterType("Actual Close Date", "act_close_date", java.sql.Date.class), 
					new ParameterType("Division Id", "division_id", Integer.class),  
				},
			"select division_id, concat(division_nbr,'-',division_code) as div, description, act_close_date from division where division_id=?",
			new ParameterType[] { 
					new ParameterType("Division Id", "division_id", Integer.class),  
				},
			"Success",
			Permission.DIVISION_CLOSE_OVERRIDE
		),

	REMOVE_DIVISION_ACTUAL_CLOSE_DATE(
			"Remove Division Actual Close Date",
			"select division_id, concat(division_nbr,'-',division_code) as div, description, act_close_date from division where division_id=?",
			new ParameterType[] { 
					new ParameterType("Division Id", "division_id", Integer.class),  
				},
			"update division set act_close_date=null where division_id=?",
			new ParameterType[] { 
					new ParameterType("Division Id", "division_id", Integer.class),  
				},
			"select division_id, concat(division_nbr,'-',division_code) as div, description, act_close_date from division where division_id=?",
			new ParameterType[] { 
					new ParameterType("Division Id", "division_id", Integer.class),  
				},
			"Date Removed - Division has never been closed",
			Permission.DIVISION_CLOSE_OVERRIDE
		),

	
	UNREJECT_TICKETS(
			"Unreject a Ticket",
			"select * from ticket where ticket_status='" + TicketStatus.REJECTED.code() + "' and ticket_id=?",
			new ParameterType[] { 
					new ParameterType("Ticket Id", "ticket_id", Integer.class),  
				},
			"update ticket set ticket_status='" + TicketStatus.NOT_DISPATCHED.code() + "', process_notes=?, process_date=null"
			+ " where ticket_status='" + TicketStatus.REJECTED.code() + "' and ticket_id=?",
			new ParameterType[] { 
					new ParameterType("Process Notes", "process_notes", String.class),
					new ParameterType("Ticket Id", "ticket_id", Integer.class), 
				},
			"select * from ticket where ticket_id=?",
			new ParameterType[] { 
					new ParameterType("Ticket Id", "ticket_id", Integer.class), 
				},
			"Success",
			Permission.TICKET_OVERRIDE
		),
	
	;

	

	private final String display;
	private final String selectSql;
	private final String updateSql;
	private final String updateSelectSql;
	private final ParameterType[] selectParms;
	private final ParameterType[] updateParms;
	private final ParameterType[] updateSelectParms;
	private final String successMessage;
	private final Permission permission;
	
	private SpecialOverrideType(
			String display, 
			String selectSql, 
			ParameterType[] selectParms, 
			String updateSql, 
			ParameterType[] updateParms, 
			String updateSelectSql, 
			ParameterType[] updateSelectParms, 
			String successMessage,
			Permission permission) {
		this.display = display;
		this.selectSql = selectSql;
		this.updateSql = updateSql;
		this.updateSelectSql = updateSelectSql;
		this.selectParms = selectParms;
		this.updateParms = updateParms;
		this.successMessage = successMessage;
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
	
	public String getSuccessMessage() {
		return successMessage;
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
