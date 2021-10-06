package com.ansi.scilla.web.test.payroll;

import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.payroll.request.TimesheetRequest;

public class TestTimesheetRequest {

	public static void main(String[] args) {
		try {
			TimesheetRequest request = new TimesheetRequest();
			request.setDirectLabor(-1.0D);
			WebMessages webMessages = request.validate(null);
			System.out.println(webMessages);
//			Field[] fields = TimesheetRequest.class.getDeclaredFields();
//			for ( Field field: fields ) {
//				System.out.println(field.getName() + " " + field.getType().getName());
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
				
		

	}

}
