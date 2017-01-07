package com.stt.portfolio.quotes;

import java.util.Date;

public interface QuoteManager {

	public void init();
	
	public Quote getQuote(String ticker);
	
	public Quote getQuote(String ticker, Date date);
}
