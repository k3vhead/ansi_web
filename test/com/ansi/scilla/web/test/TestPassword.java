package com.ansi.scilla.web.test;

import com.ansi.scilla.web.common.AppUtils;

public class TestPassword extends TesterUtils {

	public static void main(String[] args) {
		System.out.println(AppUtils.encryptPassword("password1", 1));
	}
}
