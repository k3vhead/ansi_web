package com.ansi.scilla.web.test.UIStressTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ansi.scilla.common.ApplicationObject;

public class StresserParam extends ApplicationObject {
	private static final long serialVersionUID = 1L;
	
	protected final Logger logger = LogManager.getLogger("ansi_stress_log");
	
	public String url;
	public String paramString;
	public HashMap<String, String> parameterMap;
	public boolean doPost;
	public StresserParam(String url, String paramString, boolean doPost) {
		super();
		this.url = url;
		this.paramString = paramString;
		this.doPost = doPost;
		
		this.parameterMap = new HashMap<String, String>();
		String[] pairs = paramString.split("\\&");
		for ( String pair : pairs ) {
//			System.out.println(pair);
			String[] pieces = pair.split("=");
			if ( pieces.length == 1 ) {
				this.parameterMap.put(pieces[0],"");
			} else {
				this.parameterMap.put(pieces[0], pieces[1]);
			}
		}
	}
	
	public StresserParam(String url, HashMap<String, String> params, boolean doPost) {
		super();
		this.url = url;
		this.parameterMap = params;
		this.doPost = doPost;
		
		List<String> paramList = new ArrayList<String>();
		for ( Entry<String, String> entry : params.entrySet() ) {
			paramList.add(entry.getKey() + "=" + entry.getValue());
		}
		this.paramString = StringUtils.join(paramList, "&");
	}

	
}