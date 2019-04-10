package com.ansi.scilla.web.common.utils;

import org.apache.commons.collections4.Transformer;

import com.ansi.scilla.web.common.struts.SessionDivision;


/**
 * Transforms SessionDivision to (integer) divisionId
 * @author dclewis
 *
 */
public class SessionDivisionTransformer implements Transformer<SessionDivision, Integer> {
	@Override
	public Integer transform(SessionDivision arg0) {
		return arg0.getDivisionId();
	}
}


