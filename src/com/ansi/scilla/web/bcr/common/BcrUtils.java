package com.ansi.scilla.web.bcr.common;

import java.math.BigDecimal;
import java.math.MathContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.AnsiTime;
import com.ansi.scilla.common.db.ClaimEquipment;
import com.ansi.scilla.common.db.TicketClaim;
import com.ansi.scilla.common.utils.WorkWeek;
import com.ansi.scilla.common.utils.WorkYear;
import com.ansi.scilla.web.bcr.request.BcrExpenseRequest;
import com.ansi.scilla.web.bcr.request.BcrTicketClaimRequest;
import com.ansi.scilla.web.bcr.response.DisplayMonth;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class BcrUtils {
	public static List<DisplayMonth> makeDisplayYear(Calendar workDay) {
		// if today is october, november, december, so we start october this year
		// if today is January - September, so we start last october last year
		// in either case, we end 3 months from today
		Integer startYear = workDay.get(Calendar.MONTH) > Calendar.SEPTEMBER ? workDay.get(Calendar.YEAR) : workDay.get(Calendar.YEAR) - 1;
		Calendar workDate = new GregorianCalendar(startYear, Calendar.OCTOBER, 15);
		Calendar endDate = new GregorianCalendar(workDay.get(Calendar.YEAR), workDay.get(Calendar.MONTH), workDay.get(Calendar.DAY_OF_MONTH),0,0,0);
		endDate.add(Calendar.MONTH, 4);

		Integer previousYear = workDate.get(Calendar.YEAR);
		WorkYear workYear = new WorkYear(workDate.get(Calendar.YEAR));
		List<DisplayMonth> displayYear = new ArrayList<DisplayMonth>();
		while ( workDate.before(endDate) ) {
			if ( workDate.get(Calendar.YEAR)!=previousYear ) {
				previousYear = workDate.get(Calendar.YEAR);
				workYear = new WorkYear(workDate.get(Calendar.YEAR));
			}
			displayYear.add(new DisplayMonth(workYear, workDate));
			workDate.add(Calendar.MONTH, 1);
		}
		return displayYear;
	}
	
	
	
	/**
	 * Calculate the workweeks for a given month
	 * @param firstOfMonth
	 * @param lastOfMonth
	 * @return
	 */
	public static List<WorkWeek> makeWorkCalendar(Calendar firstOfMonth, Calendar lastOfMonth) {
		List<WorkWeek> workCalendar = new ArrayList<WorkWeek>();
		Calendar calendar = new GregorianCalendar(firstOfMonth.get(Calendar.YEAR), firstOfMonth.get(Calendar.MONTH), firstOfMonth.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
		while ( calendar.before(lastOfMonth) ) {
			workCalendar.add( new WorkWeek(calendar) );
			calendar.add(Calendar.DAY_OF_YEAR, 7);
		}
		return workCalendar;
	}



	public static Integer addNewLaborClaim(Connection conn, BcrTicketClaimRequest bcrRequest, SessionUser sessionUser) throws Exception {
		Logger logger = LogManager.getLogger(BcrUtils.class);
		String[] claimWeek = bcrRequest.getClaimWeek().split("-");
		
		WorkYear workYear = new WorkYear(bcrRequest.getWorkYear());

		Calendar today = Calendar.getInstance(new AnsiTime());
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setAddedBy(sessionUser.getUserId());
		ticketClaim.setAddedDate(today.getTime());
//		ticketClaim.setClaimId(claimId);
		ticketClaim.setClaimWeek(Integer.valueOf(claimWeek[1]));
		ticketClaim.setClaimMonth(workYear.getWorkMonth(Integer.valueOf(claimWeek[1])));
		ticketClaim.setClaimYear(bcrRequest.getWorkYear());
		ticketClaim.setDlAmt(  (new BigDecimal(bcrRequest.getDlAmt())).round(MathContext.DECIMAL32)  );
		ticketClaim.setDlExpenses(BigDecimal.ZERO);
		ticketClaim.setEmployeeName(bcrRequest.getEmployee());
		ticketClaim.setHours(BigDecimal.ZERO);
		ticketClaim.setNotes(bcrRequest.getNotes());
//		ticketClaim.setPassthruExpenseType(passthruExpenseType);
		ticketClaim.setServiceType(bcrRequest.getServiceTagId());
		ticketClaim.setTicketId(bcrRequest.getTicketId());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today.getTime());
		ticketClaim.setVolume(  (new BigDecimal(bcrRequest.getVolumeClaimed())).round(MathContext.DECIMAL32) );
		logger.log(Level.DEBUG, ticketClaim);
		Integer claimId = ticketClaim.insertWithKey(conn);
		
		if ( ! StringUtils.isBlank(bcrRequest.getClaimedEquipment()) ) {
			String[] claimedEquipmentList = StringUtils.split(bcrRequest.getClaimedEquipment(),",");
			for ( String tagId : claimedEquipmentList ) {
				ClaimEquipment claimEquipment = new ClaimEquipment();
				claimEquipment.setClaimId(claimId);
				claimEquipment.setEquipmentId(Integer.valueOf(tagId));
				claimEquipment.setAddedBy(sessionUser.getUserId());
				claimEquipment.setAddedDate(today.getTime());
				claimEquipment.setUpdatedBy(sessionUser.getUserId());
				claimEquipment.setUpdatedDate(today.getTime());
				claimEquipment.insertWithNoKey(conn);
			}
			
		}		
		return claimId;
	}
	
	
	
	public static void updateLaborClaim(Connection conn, Integer claimId, BcrTicketClaimRequest bcrRequest, SessionUser sessionUser) throws Exception {
		Logger logger = LogManager.getLogger(BcrUtils.class);
		String[] claimWeek = bcrRequest.getClaimWeek().split("-");

		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.selectOne(conn);
		
		TicketClaim key = new TicketClaim();
		key.setClaimId(claimId);
		
		Calendar today = Calendar.getInstance(new AnsiTime());
		ticketClaim.setClaimId(claimId);
		ticketClaim.setClaimWeek(Integer.valueOf(claimWeek[1]));
		ticketClaim.setClaimYear(bcrRequest.getWorkYear());
		ticketClaim.setDlAmt(  (new BigDecimal(bcrRequest.getDlAmt())).round(MathContext.DECIMAL32)  );
		ticketClaim.setDlExpenses(BigDecimal.ZERO);
		ticketClaim.setEmployeeName(bcrRequest.getEmployee());
		ticketClaim.setHours(BigDecimal.ZERO);
		ticketClaim.setNotes(bcrRequest.getNotes());
//		ticketClaim.setPassthruExpenseType(passthruExpenseType);
		ticketClaim.setServiceType(bcrRequest.getServiceTagId());
		ticketClaim.setTicketId(bcrRequest.getTicketId());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today.getTime());
		ticketClaim.setVolume(  (new BigDecimal(bcrRequest.getVolumeClaimed())).round(MathContext.DECIMAL32)  );
		logger.log(Level.DEBUG, ticketClaim);
		ticketClaim.update(conn, key);
		
		if ( StringUtils.isBlank(bcrRequest.getClaimedEquipment())) {
			try {
				ClaimEquipment ce = new ClaimEquipment();
				ce.setClaimId(claimId);
				ce.delete(conn);
			} catch ( RecordNotFoundException e) {
				// tried to delete something that wasn't there. We don't care
			}
		} else {
			final String equipmentSql = 
					"if exists (select * from claim_equipment where claim_id=? and equipment_id=?)\n" + 
					"	update claim_equipment set updated_by=?,updated_date=? where claim_id=? and equipment_id=?\n" + 
					"ELSE \n" + 
					"	insert into claim_equipment(claim_id,equipment_id,added_by,added_date,updated_by,updated_date)\n" + 
					"	values (?,?,?,?,?,?)";
			PreparedStatement psUpsert = conn.prepareStatement(equipmentSql);
			PreparedStatement psDelete = conn.prepareStatement("delete from claim_equipment where claim_id=? and equipment_id not in (" + bcrRequest.getClaimedEquipment() + ")");
			for ( String equipmentTag : bcrRequest.getClaimedEquipment().split(",") ) {
				Integer equipmentId = Integer.valueOf(equipmentTag);
				java.sql.Date sessionDate = new java.sql.Date(today.getTime().getTime());
				
				int n = 1;
				//updates
				psUpsert.setInt(n, claimId);
				n++;
				psUpsert.setInt(n, equipmentId);
				n++;
				psUpsert.setInt(n, sessionUser.getUserId());
				n++;
				psUpsert.setDate(n, sessionDate);
				n++;
				psUpsert.setInt(n, claimId);
				n++;
				psUpsert.setInt(n, equipmentId);
				n++;
				//inserts
				psUpsert.setInt(n, claimId);
				n++;
				psUpsert.setInt(n, equipmentId);
				n++;
				psUpsert.setInt(n, sessionUser.getUserId());
				n++;
				psUpsert.setDate(n, sessionDate);
				n++;
				psUpsert.setInt(n, sessionUser.getUserId());
				n++;
				psUpsert.setDate(n, sessionDate);
				n++;
				psUpsert.executeUpdate();
			}
			
			psDelete.setInt(1, claimId);
			psDelete.executeUpdate();
		}
	}
	
	
	
	
	
	public static Integer insertExpenseClaim(Connection conn, BcrExpenseRequest bcrExpenseRequest, SessionUser sessionUser) throws Exception {
		Logger logger = LogManager.getLogger(BcrUtils.class);
		logger.log(Level.DEBUG, "Inserting a Passthru Expense");
		TicketClaim ticketClaim = new TicketClaim();
		Calendar today = Calendar.getInstance(new AnsiTime());


		String[] claimWeek = bcrExpenseRequest.getClaimWeek().split("-");
		WorkYear workYear = new WorkYear(Integer.valueOf(claimWeek[0]));


		ticketClaim.setAddedBy(sessionUser.getUserId());
		ticketClaim.setAddedDate(today.getTime());
		ticketClaim.setClaimWeek(Integer.valueOf(claimWeek[1]));
		ticketClaim.setClaimYear(Integer.valueOf(claimWeek[0]));
		ticketClaim.setClaimMonth(workYear.getWorkMonth(Integer.valueOf(claimWeek[1])));
		ticketClaim.setDlAmt(BigDecimal.ZERO);
		ticketClaim.setDlExpenses(BigDecimal.ZERO);
		//		ticketClaim.setEmployeeName(employeeName);
		ticketClaim.setHours(BigDecimal.ZERO);
		ticketClaim.setNotes(bcrExpenseRequest.getNotes());
		ticketClaim.setPassthruExpenseType(bcrExpenseRequest.getExpenseType());
		ticketClaim.setPassthruExpenseVolume(  (new BigDecimal(bcrExpenseRequest.getVolume())).round(MathContext.DECIMAL32) );
		ticketClaim.setServiceType(bcrExpenseRequest.getServiceTagId());
		ticketClaim.setTicketId(bcrExpenseRequest.getTicketId());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today.getTime());
		ticketClaim.setVolume(BigDecimal.ZERO);

		Integer claimId = ticketClaim.insertWithKey(conn);
		conn.commit();
		return claimId;
	}




	public static void updateExpenseClaim(Connection conn, Integer claimId, BcrExpenseRequest bcrExpenseRequest, SessionUser sessionUser) throws Exception {
		Calendar today = Calendar.getInstance(new AnsiTime());
		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.selectOne(conn);

		ticketClaim.setPassthruExpenseType(bcrExpenseRequest.getExpenseType());
		ticketClaim.setPassthruExpenseVolume(  (new BigDecimal(bcrExpenseRequest.getVolume())).round(MathContext.DECIMAL32) );
		ticketClaim.setNotes(bcrExpenseRequest.getNotes());
		ticketClaim.setUpdatedBy(sessionUser.getUserId());
		ticketClaim.setUpdatedDate(today.getTime());


		TicketClaim key = new TicketClaim();
		key.setClaimId(claimId);
		ticketClaim.update(conn, key);
		conn.commit();
	}




	public static void deleteClaim(Connection conn, Integer claimId) throws Exception {
		try {
			ClaimEquipment claimEquipment = new ClaimEquipment();
			claimEquipment.setClaimId(claimId);
			claimEquipment.delete(conn);
		} catch ( RecordNotFoundException e) {
			// trying to delete something that isn't there; we don't care
		}

		TicketClaim ticketClaim = new TicketClaim();
		ticketClaim.setClaimId(claimId);
		ticketClaim.delete(conn);
		conn.commit();

	}
}
