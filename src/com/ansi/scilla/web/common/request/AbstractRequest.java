package com.ansi.scilla.web.common.request;

import com.ansi.scilla.web.common.ApplicationWebObject;

public abstract class AbstractRequest extends ApplicationWebObject {

	private static final long serialVersionUID = 1L;

	public static final String EMAIL_FORMAT = "^(.+)@(.+)\\.(.+)";
	
	/**
	 * Phone format matching results:
	 * [java] true	(223) 456 7891 x 123
     [java] true	(223) 456 7891 ext 123
     [java] true	(223) 456 7891 ext. 123
     [java] true	(223) 456 7891 Ext 123
     [java] true	(223) 456 7891 Ext. 123
     [java] true	(910) 456-8970 x1
     [java] true	(910) 456-8970 x12
     [java] true	(910) 456-8970 x123
     [java] true	(910) 456-8970 x1234
     [java] true	(910) 456-8970 x12345
     [java] true	(910) 456-8970 x123456
     [java] true	(910) 456-8970 x1234567
     [java] true	(910) 456-8970 #12
     [java] true	(910) 456-8970 extension12456
     [java] true	(910) 456-8970 Extension 12456
     [java] true	234.445.6789
     [java] true	987 765-8765
     [java] true	1 (453) 876 9876
     [java] true	+1 (453) 876 9876
     [java] true	(800)ABCDEFG
     [java] true	(800) ABC-1234
     [java] false	2 (453) 876 9876
     [java] false	(4534) 876 9876
     [java] false	(453) 8764 9876
     [java] false	(453) 876 98769
     [java] true	123-123-1234
     [java] false	12-12-123
     [java] true	1231231234
     [java] true	(123) 123-1234
	 */
	public static final String PHONE_FORMAT = "^((1|\\+1)?( |-|\\.)?)?(\\(?[0-9]{3}\\)?|[0-9]{3})( |-|\\.)?([a-zA-Z0-9]{3}( |-|\\.)?[a-zA-Z0-9]{4})[ ]*(( |x|ext|ext\\.|extension|Ext|Ext\\.|Extension|#){1}[ ]?([0-9]){1,7}){0,1}$";
}
