package com.ansi.scilla.web.common;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

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
			List<String> messageList = this.get(key);
			messageList.add(message);
			this.put(key, messageList);
		} else {
			this.put(key, Arrays.asList(new String[] { message }));
		}
	}

	@Override
	public String toString() {
		ReflectionToStringBuilder builder = new ReflectionToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE);
		return builder.toString();
	}

	 public String toJson() throws Exception {
         return AppUtils.object2json(this);
	 }

}
