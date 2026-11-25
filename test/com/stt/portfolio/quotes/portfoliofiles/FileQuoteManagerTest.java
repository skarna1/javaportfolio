package com.stt.portfolio.quotes.portfoliofiles;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.nio.file.Files;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.stt.portfolio.DateParser;
import com.stt.portfolio.quotes.Quote;

public class FileQuoteManagerTest {

	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	@Test
	public void loadsLatestAndHistoricalQuotesInSinglePass() throws Exception {
		File quotesDirectory = temporaryFolder.newFolder();
		File quoteFile = new File(quotesDirectory, "TEST.csv");
		Files.writeString(quoteFile.toPath(),
				"date,high,low,last,amount\n"
				+ "03072009,6.00,5.90,5.95,60\n"
				+ "05072009,6.30,6.10,6.25,80\n");

		FileQuoteManager quoteManager = new FileQuoteManager(quotesDirectory);
		quoteManager.init();
		Files.delete(quoteFile.toPath());

		Quote latestQuote = quoteManager.getQuote("TEST");
		assertNotNull(latestQuote);
		assertEquals(6.25, latestQuote.getLast(), 0.0001);
		assertEquals("TEST", latestQuote.getTicker());

		Date historicalDate = new DateParser().parseDateDDMMYYYY("04072009");
		Quote historicalQuote = quoteManager.getQuote("TEST", historicalDate);
		assertNotNull(historicalQuote);
		assertEquals(5.95, historicalQuote.getLast(), 0.0001);
	}
}
