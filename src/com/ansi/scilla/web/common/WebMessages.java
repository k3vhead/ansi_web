package com.ansi.scilla.web.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Used to pass error/informational messages from servlet to client
 * 
 * @author dclewis
 *
 */
public class WebMessages extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;
	public static String GLOBAL_MESSAGE = "GLOBAL_MESSAGE";
	
	
	private HashMap<String, List<String>> messages;
	
	public WebMessages() {
		super();
		this.messages = new HashMap<String, List<String>>(); 
	}

	public WebMessages(HashMap<String, List<String>> messages) {
		this();
		this.messages = messages;
	}

	public HashMap<String, List<String>> getMessages() {
		return messages;
	}

	public void setMessages(HashMap<String, List<String>> messages) {
		this.messages = messages;
	}

	public void addMessage(String key, String message) {
		if ( this.messages.containsKey(key)) {
			List<String> messageList = this.messages.get(key);
			messageList.add(message);
			this.messages.put(key, messageList);
		} else {
			this.messages.put(key, Arrays.asList(new String[] { message }));
		}
	}
	
	public boolean isEmpty() {
		return this.messages == null || this.messages.isEmpty();
	}
	
}
