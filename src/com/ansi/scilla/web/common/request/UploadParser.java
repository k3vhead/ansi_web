package com.ansi.scilla.web.common.request;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

public interface UploadParser {

	default String makeString(FileItem item) {
		return item.getString();
	}
	
	default Integer makeInteger(FileItem item) {
		return StringUtils.isBlank(item.getString()) ? null : Integer.valueOf(item.getString());
	}
	
	default Calendar makeCalendar(FileItem item, SimpleDateFormat dateFormat) throws ParseException {
		Calendar calendar = null;
		if ( ! StringUtils.isBlank(item.getString())) {
			Date date = dateFormat.parse(item.getString());
			calendar = DateUtils.toCalendar(date);
		}
		return calendar;
	}
}
