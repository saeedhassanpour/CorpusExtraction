package edu.stanford.rad.stride;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractReportsDates {

	public static Date findDateinString(String text) throws ParseException {
		DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
		Date date = formatter.parse("01/01/1900");

		Matcher m = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{4}").matcher(text);
		if (m.find()) {
			formatter = new SimpleDateFormat("MM/dd/yyyy");
			String dateString = m.group();
			date = formatter.parse(dateString);
		} else {
			m = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2}").matcher(text);
			if (m.find()) {
				formatter = new SimpleDateFormat("MM/dd/yy");
				String dateString = m.group();
				date = formatter.parse(dateString);
			}
		}
		return date;
	}

}
