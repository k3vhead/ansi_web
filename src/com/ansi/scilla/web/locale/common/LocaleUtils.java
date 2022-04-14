package com.ansi.scilla.web.locale.common;

import java.sql.Connection;

import org.apache.commons.lang3.StringUtils;

import com.ansi.scilla.common.db.Locale;
import com.ansi.scilla.common.utils.LocaleType;
import com.thewebthing.commons.db2.RecordNotFoundException;

public class LocaleUtils {

	/**
	 * Get a locale record that matches given state name and city name. 
	 * @param conn
	 * @param state State name
	 * @param city City name, or null if looking for a state record
	 * @return Locale for state, if city is null; Locale for non-state (ie City or Jurisdiction), if city is entered 
	 */
	public static Locale makeLocale(Connection conn, String state, String city) throws RecordNotFoundException, Exception {
		Locale stateLocale = new Locale();
		Locale cityLocale = new Locale();

		stateLocale.setStateName(state);
		stateLocale.setLocaleTypeId(LocaleType.STATE.name());
		stateLocale.selectOne(conn);
		
		if ( ! StringUtils.isBlank(city) ) {
			cityLocale.setName(city);
			cityLocale.setLocaleParentId(stateLocale.getLocaleId());
			cityLocale.selectOne(conn);
		}
		
		return StringUtils.isBlank(city) ? stateLocale : cityLocale;
	}

	
	/**
	 * Get a locale record that matches given state locale id and city name.
	 * @param conn
	 * @param state State locale id
	 * @param city City name, or null if looking for a state record (which is pretty redundant, since we already have the locale id)
	 * @return Locale for state, if city is null; Locale for non-state (ie City or Jurisdiction), if city is entered 
	 */
	public static Locale MakeLocale(Connection conn, Integer state, String city) throws RecordNotFoundException, Exception {
		Locale stateLocale = new Locale();
		Locale cityLocale = new Locale();

		stateLocale.setLocaleId(state);
		stateLocale.setLocaleTypeId(LocaleType.STATE.name());
		stateLocale.selectOne(conn);
		
		if ( ! StringUtils.isBlank(city) ) {
			cityLocale.setName(city);
			cityLocale.setLocaleParentId(stateLocale.getLocaleId());
			cityLocale.selectOne(conn);
		}
		
		return StringUtils.isBlank(city) ? stateLocale : cityLocale;
	}
}
