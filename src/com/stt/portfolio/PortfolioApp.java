package com.stt.portfolio;



import java.util.Calendar;

import com.stt.portfolio.quotes.QuoteManagerFactory;
import com.stt.portfolio.quotes.portfoliofiles.FileQuoteManagerFactory;

public class PortfolioApp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		QuoteManagerFactory f = new FileQuoteManagerFactory();
		I_TickerManager tickerManager = new TickerManager("etc/tunnukset.csv");
		tickerManager.init();
		
		/** The changes parser. */
		ChangesParser changesParser = new ChangesParser("etc/muutokset.csv");
		

		Portfolio portfolio = new Portfolio("salkku0", f, tickerManager);
		
		
		portfolio.parse(changesParser, Calendar.getInstance().getTime());

		portfolio.print();
	}
	
}
