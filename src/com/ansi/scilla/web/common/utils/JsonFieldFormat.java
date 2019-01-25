package com.ansi.scilla.web.common.utils;

import java.sql.Types;

/**
 * Formats that the abstract CRUD servlet understands
 * @author dclewis
 *
 */
public enum JsonFieldFormat {
	STRING(Types.VARCHAR),
	INTEGER(Types.INTEGER),
	DECIMAL(Types.DECIMAL),
	DATE(Types.DATE);
	
	private final Integer types;
	
	private JsonFieldFormat(Integer types) { this.types = types; }
	
	public Integer getTypes() { return this.types; }
}
