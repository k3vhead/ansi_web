package com.ansi.scilla.web.test;

import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.web.response.options.OptionsListResponse;
import com.ansi.scilla.web.response.options.ResponseOption;

public class TestOptions {

	public static void main(String[] args) {
		TesterUtils.makeLoggers();
		try {
//			List<ResponseOption> o = Arrays.asList(new ResponseOption[] {ResponseOption.JOB_STATUS, ResponseOption.JOB_FREQUENCY});
//			OptionsListResponse r = new OptionsListResponse(o);
//			String json = r.toJson();
			String json = TesterUtils.getJson("http://127.0.0.1:8080/ansi_web/options?JOB_STATUS,JOB_FREQUENCY,TICKET_STATUS");
			System.out.println(json);
		} catch ( Exception e) {
			e.printStackTrace();
		}
	}

	/*
	ObjectMapper mapper = new ObjectMapper();
	
	mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	return mapper.writeValueAsString(this);
	*/
}
