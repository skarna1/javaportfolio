package com.stt.portfolio.quotes.portfoliofiles;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;

import org.junit.Before;
import org.junit.Test;

import com.stt.portfolio.ParseException;
import com.stt.portfolio.quotes.Quote;

public class QuoteParserTest {
	QuoteParser p;
	@Before
	public void setUp() throws Exception {
		p = new QuoteParser();
	}

	@Test
	public void testParse() {
		try {
			Quote quote = p.parse("03072009,5.97,5.90,5.95,60");
			
			assertEquals(5.97, quote.getHigh(),0.0001);
			assertEquals(5.90, quote.getLow(),0.0001);
			assertEquals(5.95, quote.getLast(),0.0001);
			assertEquals(60, quote.getAmount());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void testParseLF2() {
		try {
			Quote quote = p.parse("03072009,5.97,5.90,5.95,60\r\n");
			
			assertEquals(5.97, quote.getHigh(),0.0001);
			assertEquals(5.90, quote.getLow(),0.0001);
			assertEquals(5.95, quote.getLast(),0.0001);
			assertEquals(60, quote.getAmount());
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void testParseLF1() {
		try {
			Quote quote = p.parse("03072009,5.97,5.90,5.95,60\n");
			
			assertEquals(5.97, quote.getHigh(),0.0001);
			assertEquals(5.90, quote.getLow(),0.0001);
			assertEquals(5.95, quote.getLast(),0.0001);
			assertEquals(60, quote.getAmount());
		
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
}
