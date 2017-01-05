package com.ansi.scilla.web.test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import com.ansi.scilla.common.address.Country;
import com.ansi.scilla.common.address.State;

public class TestStates {

	public static void main(String[] args) {
		try {
//			new TestStates().go();
			new TestStates().goPost();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void go() throws IOException {
		List<State> stateList = Country.USA.getStates();
		Collections.sort(stateList);
		for ( State state : stateList ) {
			System.out.println(state.getDisplay());
		}
	}
	
	public void goPost() throws Exception {
		String json = TesterUtils.getJson("http://127.0.0.1:8080/ansi_web/options/?COUNTRY");
		System.out.println(json);
	}

}
