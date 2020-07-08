package com.ansi.scilla.web.test.quote;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestJson {

	public void go() throws JsonProcessingException, IOException {
		String jobString1 = "{\"jobFrequency\":\"15/Y\",\"jobNbr\":\"1\",\"pricePerCleaning\":\"100\",\"serviceDescription\":\"as;ldkj asdf \",\"requestSpecialScheduling\":0,\"directLaborPct\":\".3\",\"budget\":\"100\",\"floors\":\"1\",\"equipment\":\"asdlfk;j\",\"washerNotes\":\"\",\"omNotes\":\"\",\"billingNotes\":\"\",\"poNumber\":\"\",\"ourVendorNbr\":\"\",\"expirationDate\":\"\",\"expirationReason\":\"\",\"repeatScheduleAnnually\":0,\"updateType\":\"add\",\"action\":\"job\"}";
		String jobString2 = "{\"invoiceBatch\":false,\"taxExempt\":false,\"taxExemptReason\":\"\",\"managerId\":\"32\",\"divisionId\":\"104\",\"accountType\":\"GOVNMT\",\"invoiceTerms\":\"DAY30\",\"leadType\":\"LBRARY\",\"invoiceStyle\":\"EMAIL\",\"buildingType\":\"CONDO\",\"invoiceGrouping\":\"BY_JOB_SITE\",\"action\":\"validate\"}";
		
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JsonNode jsonNode1 = mapper.readTree(jobString1);
		JsonNode jsonNode2 = mapper.readTree(jobString2);
		
		String action1 = jsonNode1.get("action").asText();
		String action2 = jsonNode2.get("action").asText();
		
		System.out.println(action1 + "\n" + action2);
	}
	
	
	public static void main(String[] args) {
		try {
			new TestJson().go();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
