package com.ansi.scilla.web.common.utils;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.common.ApplicationObject;
import com.ansi.scilla.common.db.Division;
import com.ansi.scilla.web.common.struts.SessionDivision;

public class Division2SessionDivisionTransformer extends ApplicationObject implements Transformer<Division, SessionDivision> {

	private static final long serialVersionUID = 1L;

	@Override
	public SessionDivision transform(Division arg0) {
		return new SessionDivision(arg0);
	}

}
