package com.ansi.scilla.web.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.ansi.scilla.common.ApplicationObject;

public class BuildDate extends ApplicationObject {

	private static final long serialVersionUID = 1L;
	private String webBuildDate;
	private String commonBuildDate;
	private String reportBuildDate;

	public BuildDate() {
		super();
		try {
			String webResource="resources/build_web.properties";
//			System.out.println(webResource);
			InputStream webStream = BuildDate.class.getClassLoader().getResourceAsStream(webResource);
			this.webBuildDate = makeBuildDate(webStream);
			webStream.close();
			
			String commonResource="resources/build_common.properties";
//			System.out.println(commonResource);
			InputStream commonStream = BuildDate.class.getClassLoader().getResourceAsStream(commonResource);
			this.commonBuildDate = makeBuildDate(commonStream);
			commonStream.close();

			String reportResource="resources/build_report.properties";
//			System.out.println(reportResource);
			InputStream reportStream = BuildDate.class.getClassLoader().getResourceAsStream(reportResource);
			this.reportBuildDate = makeBuildDate(reportStream);
			reportStream.close();
		} catch ( Exception e ) {
			AppUtils.logException(e);
			this.webBuildDate = "Error";
			this.commonBuildDate = "Error";
			this.reportBuildDate = "Error";
		}
	}

	private String makeBuildDate(InputStream is) throws IOException {
		String buildDate = "not available";
		
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
//        	System.out.println(i + "\t" + lines[i]);
        	if ( lines[i].startsWith("builddate")) {
        		String[] values = lines[i].split("=");
        		buildDate = values[1];
        	}
        		
        }
        
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
}
