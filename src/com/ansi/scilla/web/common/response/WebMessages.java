package com.ansi.scilla.web.common.response;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.web.common.utils.AppUtils;

/**
 * Used to pass error/informational messages from servlet to client
 * 
 * @author dclewis
 *
 */
public class WebMessages extends HashMap<String, List<String>> {

	private static final long serialVersionUID = 1L;
	public static String GLOBAL_MESSAGE = "GLOBAL_MESSAGE";
	
		
	public WebMessages() {
		super();
	}

	public void addMessage(String key, String message) {
		if ( this.containsKey(key)) {
			List<String> messageList = new ArrayList<String>();
			for ( String msg : this.get(key) ) {
				messageList.add(msg);
			}
			messageList.add(message);
			this.put(key, messageList);
		} else {
			this.put(key, Arrays.asList(new String[] { message }));
		}
	}
	
	/**
	 * Merge webMessages
	 * @param webMessages
	 */
	public void addAllMessages(WebMessages webMessages) {
		for ( String key : webMessages.keySet() ) {
			for ( String message : webMessages.get(key)) {
				addMessage(key, message);
			}
		}
	}

	
	@Override
	public String toString() {
		List<String> outbound = new ArrayList<String>();
		List<String> keyList = IterableUtils.toList(this.keySet());
		Collections.sort(keyList);
		for ( String key : keyList ) {
			outbound.add(key + ":");
			List<String> valueList = this.get(key);
			for ( String value : valueList ) {
				outbound.add("\t" + value);
			}
		}
		return StringUtils.join(outbound, "\n");
	}

	 public String toJson() throws Exception {
         return AppUtils.object2json(this);
	 }

	 public void addRequiredFieldMessages(Connection conn, List<String> missingFields) throws Exception {
		 if ( missingFields != null ) {
			 String messageText = AppUtils.getMessageText(conn, MessageKey.MISSING_DATA, "Required Entry");
			 for ( String field : missingFields ) {
				 addMessage(field, messageText);
			 }
		 }
	 }

	
	 
	 /**
	  * The most common return message -- "everything worked"
	  * 
	  * @return
	  */
	 public static WebMessages makeGlobalSuccessMessage() {
		 WebMessages webMessages = new WebMessages();
		 webMessages.addMessage(GLOBAL_MESSAGE, "Success");
		 return webMessages;
	 }
}
