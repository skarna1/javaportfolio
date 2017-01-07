package com.stt.portfolio;

import java.util.Calendar;
import java.util.Date;

public class Util {
	
	public static final double EURO_TO_MARKKA_RATIO = 5.94573;
	private static Date dateBeforeEuro;
	
	static {
		Calendar c = Calendar.getInstance();
		c.set(1999, 0, 1);
		dateBeforeEuro = c.getTime();
	}
	
	public static double convertToDouble(String str) {
		return Double.parseDouble(str.replace(',', '.').trim());
	}
	
	public static boolean isDateBeforeEuro(Date date) {
	
		return date.before(dateBeforeEuro);
	}
	
	public static int compareDates(Date a, Date b) {
		Calendar ca = Calendar.getInstance();
		Calendar cb = Calendar.getInstance();
		
		ca.setTime(a);
		cb.setTime(b);
		
		if (ca.get(Calendar.YEAR) < cb.get(Calendar.YEAR)) {
			return -1;
		}
		else if (ca.get(Calendar.YEAR) > cb.get(Calendar.YEAR)) {
			return 1;
		}
		
		if (ca.get(Calendar.MONTH) < cb.get(Calendar.MONTH)) {
			return -1;
		}
		else if (ca.get(Calendar.MONTH) > cb.get(Calendar.MONTH)) {
			return 1;
		}
		
		if (ca.get(Calendar.DATE) < cb.get(Calendar.DATE)) {
			return -1;
		}
		else if (ca.get(Calendar.DATE) > cb.get(Calendar.DATE)) {
			return 1;
		}
		
		return 0;
	}
}
