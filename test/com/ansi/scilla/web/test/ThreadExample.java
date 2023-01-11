package com.ansi.scilla.web.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ThreadExample {

	private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.S");
	
	private void go() {
		Random rand = new Random();
		
		Calendar start = Calendar.getInstance();
		System.out.println("Start test: "+ sdf.format(start.getTime()));
		
		
		List<Thread> threadList = new ArrayList<Thread>();
		for ( int i = 0; i<3; i++ ) {
			int n = rand.nextInt(10);
			MyRunnable myRunnable = new MyRunnable(i, n);
			Thread t = new Thread(myRunnable);
			threadList.add(t);
		}
		
		for ( Thread t : threadList ) {
			t.start();
		}
		
		while ( true ) {
			try {
				for ( Thread t : threadList ) {
					t.join();
				}
				break;
			} catch ( InterruptedException e) {
				System.err.println("Interrupted");
			}
		}
		
		Calendar end = Calendar.getInstance();
		System.out.println("End test: "+ sdf.format(end.getTime()));
	}

	
	public static void main(String[] args) {
		new ThreadExample().go();
	}

	
	public class MyRunnable implements Runnable {

		private final SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.S");
		public Integer threadNumber;
		public Integer waitTime;
		
		
		public MyRunnable(Integer threadNumber, Integer waitTime) {
			super();
			this.threadNumber = threadNumber;
			this.waitTime = waitTime;
		}


		@Override
		public void run() {
			Calendar start = Calendar.getInstance();
			System.out.println("Start " + this.threadNumber + ": "+ sdf.format(start.getTime()));
			
			try {
				TimeUnit.SECONDS.sleep(waitTime);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			Calendar end = Calendar.getInstance();
			System.out.println("End " + this.threadNumber + ": "+ sdf.format(end.getTime()));
		}
		
	}
}
