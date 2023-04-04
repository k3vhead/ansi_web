package com.ansi.scilla.web.bcr.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.bcr.servlet.AbstractBcrTicketLookupServlet;

public class BcrTicketLookupItemTransformer implements Transformer<HashMap<String, Object>, HashMap<String, Object>> {

	@Override
	public HashMap<String, Object> transform(HashMap<String, Object> arg0) {
		String notes = (String)arg0.get(AbstractBcrTicketLookupServlet.NOTES);
		if ( StringUtils.isBlank(notes) ) {
			arg0.put(AbstractBcrTicketLookupServlet.NOTES_DISPLAY, null);
		} else {
			arg0.put(AbstractBcrTicketLookupServlet.NOTES_DISPLAY, StringUtils.abbreviate(notes, 10));
		}
		
		if ( arg0.get(AbstractBcrTicketLookupServlet.CLAIM_WEEK).equals("-") ) {
			arg0.put(AbstractBcrTicketLookupServlet.CLAIM_WEEK, null);
		}
		
		
		String equipmentTags = (String)arg0.get(BcrTicketSql.EQUIPMENT_TAGS);
		String claimedEquipment = (String)arg0.get(BcrTicketSql.CLAIMED_EQUIPMENT);
		List<String> equipmentTagList = StringUtils.isBlank(equipmentTags) ? new ArrayList<String>() : Arrays.asList(equipmentTags.split(","));
		List<String> claimedEquipmentList = StringUtils.isBlank(claimedEquipment) ? new ArrayList<String>() : Arrays.asList(claimedEquipment.split(","));
		Collection<String> unclaimedEquipmentList = CollectionUtils.subtract(equipmentTagList, claimedEquipmentList);
		String unclaimedEquipment = StringUtils.join(unclaimedEquipmentList, ",");
		arg0.put(AbstractBcrTicketLookupServlet.UNCLAIMED_EQUIPMENT, unclaimedEquipment);
		
		return arg0;
	}

}
