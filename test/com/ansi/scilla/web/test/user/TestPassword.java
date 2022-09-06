package com.ansi.scilla.web.test.user;

import com.ansi.scilla.web.common.utils.AppUtils;
import com.ansi.scilla.web.test.TesterUtils;

public class TestPassword extends TesterUtils {

	public static void main(String[] args) {
		System.out.println(AppUtils.encryptPassword("password1", 1));
	}
}
