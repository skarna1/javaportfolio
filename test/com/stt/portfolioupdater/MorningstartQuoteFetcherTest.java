package com.stt.portfolioupdater;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;



public class MorningstartQuoteFetcherTest {
	MorningstarQuoteFetcher fetcher= null;

	@Before
	public void setUp() throws Exception {
		this.fetcher = new MorningstarQuoteFetcher();
	}

	@Test
	public void testSetPriceEURComma() {
		Item item = new Item();
		this.fetcher.setPrice(item, "EUR 152,12");

		assertEquals(152.12, item.getLast(), 0.0001);
	}

	@Test
	public void testSetPriceEURCommaNbsp() {
		Item item = new Item();
		this.fetcher.setPrice(item, "EUR\u00A0152,12");

		assertEquals(152.12, item.getLast(), 0.0001);
	}

}
