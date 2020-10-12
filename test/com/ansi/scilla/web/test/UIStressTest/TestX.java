package com.ansi.scilla.web.test.UIStressTest;

public class TestX {

	
	public void go() {
		String address = "1280 New Scotland Road";
		for ( int i = 0; i <= address.length(); i++ ) {
			System.out.println(address.substring(0, i));
		}
	}
	
	private void goException() throws Exception {
		
		try {
			for ( int i = 0; i < 10; i++ ) {
				System.out.println(i);
				if ( i > 4 ) {
					throw new Exception("Bigger than 4");
				}
			}
		} catch ( Exception e ) {
			Thread t = Thread.currentThread();
			System.out.println( t.getName() + " " + t.getId() + " " + e.toString());
			for ( StackTraceElement msg : e.getStackTrace()) {
				System.out.println( msg.toString());
			}
			throw e;
		}
		
	}

	public static void main(String[] args) {
		try {
			new TestX().goException();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
