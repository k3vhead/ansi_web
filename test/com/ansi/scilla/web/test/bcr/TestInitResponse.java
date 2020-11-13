package com.ansi.scilla.web.test.bcr;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.ansi.scilla.common.utils.AppUtils;
import com.ansi.scilla.web.bcr.response.BcrInitResponse;
import com.fasterxml.jackson.core.JsonProcessingException;

public class TestInitResponse {

	public void go() throws JsonProcessingException {
		GregorianCalendar workDate = new GregorianCalendar(2020, Calendar.NOVEMBER, 12);
		BcrInitResponse r = new BcrInitResponse(null, workDate);
//		System.out.println(r);
		String json = AppUtils.object2json(r);
		System.out.println(json);
	}
	
	
	public static void main(String[] args) {
		try {
			new TestInitResponse().go();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}

	}

}
