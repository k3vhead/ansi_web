package com.ansi.scilla.web.test.permission;

import java.util.List;

import com.ansi.scilla.web.common.response.WebMessages;
import com.ansi.scilla.web.permission.request.PermissionRequest;

public class TestPermissionRequest {

	
	private void go() {
		doTest(1, "ADDRESS","ADDRESS_READ");
		doTest(2, "ADDRESS","CAN_COMPLETE_TICKETS");
		doTest(3, "XXX", "ADDRESS_READ");
		doTest(4, "ADDRESS", "XXXX");
		doTest(5, "ADDRESS_READ", "ADDRESS");
		doTest(6, "ADDRESS_READ", "ADDRESS_READ");
		doTest(7, null, "ADDRESS_READ");
		doTest(8, "ADDRESS", null);
	}

	
	
	private void doTest(int testNum, String functionalArea, String permission) {
		System.out.println("=== test " + testNum + " ===");
		PermissionRequest request = new PermissionRequest();
		request.setFunctionalArea(functionalArea);
		request.setPermission(permission);
		WebMessages webMessages = request.validate();		
		for ( String key : webMessages.keySet() ) {
			List<String> values = webMessages.get(key);
			System.out.println(key);
			for ( String value : values ) {
				System.out.println("\t" + value);
			}
		}

		
	}



	public static void main(String[] args) {
		new TestPermissionRequest().go();

	}

}
