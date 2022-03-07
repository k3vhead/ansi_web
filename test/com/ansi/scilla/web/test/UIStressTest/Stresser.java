package com.ansi.scilla.web.test.UIStressTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.ClientProtocolException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.web.test.TestServlet;

public class Stresser extends TestServlet implements Runnable {

	protected final Logger logger = LogManager.getLogger("ansi_stress_log");
	protected final Logger stopLogger = LogManager.getLogger("ansi_stress_stop_log");
	
	public String threadName;
	public Header sessionCookie;
	public List<StresserParam> params;
	
	public Stresser(String threadName, Header sessionCookie, String hostname, StresserParam[] params) {
		this(threadName, sessionCookie, hostname, Arrays.asList(params));
	}
	
	public Stresser(String threadName, Header sessionCookie, String hostname, List<StresserParam> params) {
		super();		
		this.threadName = threadName;
		this.sessionCookie = sessionCookie;
		this.hostname = hostname;
		this.params = params;
	}
	
	public void go() throws ClientProtocolException, IOException, URISyntaxException {
		Thread me = Thread.currentThread();
		logger.log(Level.DEBUG, "Starting " + me.getName() + " " + me.getId());
		stopLogger.log(Level.DEBUG, "Starting " + me.getName() + " " + me.getId());
		
		if ( params.size() == 1 ) {
			try {
				StresserParam param = params.get(0);			
				String result = param.doPost ? super.doPost(sessionCookie, param.url, param.paramString) : super.doGet(sessionCookie, param.url,param.parameterMap);
				logger.log(Level.DEBUG, result);
			} catch ( Exception e) {
				logException(me, e);
				throw e;
			}
		} else {
			List<Thread> threadList = new ArrayList<Thread>();

			for ( int i = 0; i < params.size(); i++ ) {
				StresserParam param = params.get(i);
				Stresser stresser = new Stresser(threadName + "." + i, sessionCookie, hostname, new StresserParam[] { param } );
				threadList.add(new Thread(stresser));
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
		}
		
		
		logger.log(Level.DEBUG, "Ending " + me.getName() + " " + me.getId());
		stopLogger.log(Level.DEBUG, "Ending " + me.getName() + " " + me.getId());
	}
	
	private void logException(Thread t, Exception e) {
		logger.log(Level.FATAL, "Thread Exception (" + t.getName() + "," + t.getId() + ")\t" + e.toString());
		for ( StackTraceElement msg : e.getStackTrace()) {
			logger.log(Level.FATAL, msg.toString());
		}
		
	}

	@Override
	public void run() {
		try {
			go();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}			
	}
	
}

