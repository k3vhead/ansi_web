package com.ansi.scilla.web.bcr.request;

import java.util.HashMap;

import com.ansi.scilla.web.exceptions.ResourceNotFoundException;

public enum BcrTicketRequestType {
	CLAIM_WEEK,
	TOTAL_VOLUME,
	DL,
	EXPENSE;
	
	private static HashMap<String, BcrTicketRequestType> lookup;

	static {
		lookup = new HashMap<String, BcrTicketRequestType>();
		lookup.put("claimWeek", CLAIM_WEEK);
		lookup.put("totalVolume", TOTAL_VOLUME);
		lookup.put("dl",DL);
		lookup.put("expense",EXPENSE);
	}
	
	public static BcrTicketRequestType lookup(String key) throws ResourceNotFoundException {
		BcrTicketRequestType type = null;
		if ( lookup.containsKey(key) ) {
			type = lookup.get(key);
		}
		if ( type == null ) {
			throw new ResourceNotFoundException(key);
		}
		return type;
	}
}
