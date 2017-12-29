package com.ansi.scilla.web.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.utils.PropertyNames;
import com.ansi.scilla.web.common.struts.SessionUser;
import com.ansi.scilla.web.common.utils.AppUtils;

public class TestLogger {

	public static void main(String[] args) {

		new TestLogger().go();
	}
	
	public void go() {
		SessionUser user = null;
        String userEmail = user == null ? "n/a" : user.getEmail();
        // Don't log passwords
        String logString = "log string goes here";
        Logger logger = LogManager.getLogger(PropertyNames.TRANSACTION_LOG.toString());
        System.out.println(logger.getName());
        logger.info("User Login: " + userEmail + "\tParameters: " + logString);
	}

}
