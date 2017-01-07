package com.stt.portfolio.quotes.portfoliofiles;

import com.stt.portfolio.quotes.QuoteManager;
import com.stt.portfolio.quotes.QuoteManagerFactory;

public class FileQuoteManagerFactory implements QuoteManagerFactory {

	@Override
	public QuoteManager create() {
		
		return new FileQuoteManager();
	}

}
