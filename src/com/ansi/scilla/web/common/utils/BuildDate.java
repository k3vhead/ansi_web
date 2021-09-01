package com.ansi.scilla.web.common.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.ApplicationObject;

public class BuildDate extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	private String webBuildDate;
	private String commonBuildDate;
	private String reportBuildDate;
	private String gitBranch;

	public BuildDate() {
		super();
		HashMap<String, String> parms = new HashMap<String, String>();
		try {
			String webResource="resources/build_web.properties";
			InputStream webStream = BuildDate.class.getClassLoader().getResourceAsStream(webResource);
			this.webBuildDate = makeBuildDate(parms, webStream);
			webStream.close();
			
			String commonResource="resources/build_common.properties";
			InputStream commonStream = BuildDate.class.getClassLoader().getResourceAsStream(commonResource);
			this.commonBuildDate = makeBuildDate(parms, commonStream);
			commonStream.close();

			String reportResource="resources/build_report.properties";
			InputStream reportStream = BuildDate.class.getClassLoader().getResourceAsStream(reportResource);
			this.reportBuildDate = makeBuildDate(parms, reportStream);
			reportStream.close();
			
			this.gitBranch = parms.get("gitbranch");
		} catch ( Exception e ) {
			AppUtils.logException(e);
			this.webBuildDate = "Error";
			this.commonBuildDate = "Error";
			this.reportBuildDate = "Error";
		}
	}

	private String makeBuildDate(HashMap<String,String> parms, InputStream is) throws IOException {
		String buildDate = "not available";
		String gitBranch = "not available";
		
		Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } finally {
            is.close();
        }
        String propertyString = writer.toString();
        String[] lines = propertyString.split("\n");
        for ( int i = 0; i < lines.length; i++ ) {
        	if ( lines[i].startsWith("builddate")) {
        		String[] values = lines[i].split("=");
        		buildDate = values[1];
        	}
        	if ( lines[i].startsWith("gitbranch")) {
        		String[] values = lines[i].split("=");
        		gitBranch = values[1];
        		parms.put("gitbranch",gitBranch);
        	}
        		
        }
        
        buildDate = StringUtils.replace(buildDate, "\\", "");
        return buildDate;
	}

	public String getWebBuildDate() {
		return webBuildDate;
	}

	public String getCommonBuildDate() {
		return commonBuildDate;
	}

	public String getReportBuildDate() {
		return reportBuildDate;
	}

	public String getGitBranch() {
		return gitBranch;
	}
	
	
}
