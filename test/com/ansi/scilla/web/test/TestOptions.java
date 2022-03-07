package com.ansi.scilla.web.test;

import java.util.Arrays;
import java.util.List;

import com.ansi.scilla.web.options.response.OptionsListResponse;
import com.ansi.scilla.web.options.response.ResponseOption;

public class TestOptions {

	public static void main(String[] args) {
//		TesterUtils.makeLoggers();
		try {
			for ( ResponseOption x : ResponseOption.values() ) {
				List<ResponseOption> o = Arrays.asList(new ResponseOption[] {x});
				OptionsListResponse r = new OptionsListResponse(o, null);
				String json = r.toJson();
//				String json = TesterUtils.getJson("http://127.0.0.1:8080/ansi_web/options?INVOICE_STYLE");
				System.out.println(x.name());
				System.out.println(json);
				System.out.println("*********************");
			}
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
