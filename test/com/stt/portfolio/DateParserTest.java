package com.stt.portfolio;


import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class DateParserTest {

	DateParser p;

	@Before
	public void setUp() throws Exception {
		p = new DateParser();
	}

	@Test
	public void testParseDate() {

		Date date;
		try {
			date = p.parseDate("22.12.2008 ");
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			assertEquals(22, c.get(Calendar.DATE));
			assertEquals(12 - 1, c.get(Calendar.MONTH));
			assertEquals(2008, c.get(Calendar.YEAR));
		} catch (ParseException e) {
			fail();
		}
	
	}
}
