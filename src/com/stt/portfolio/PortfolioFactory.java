package com.stt.portfolio;

import java.util.Calendar;
import java.util.Date;

import com.stt.portfolio.quotes.portfoliofiles.FileQuoteManagerFactory;

public class PortfolioFactory {
	public static final String PORTFOLIO_PATH = "etc/salkut/";
	
	public static Portfolio createPortfolio(String name, Date date, I_TickerManager tickerManager
			) {

		System.out.println("Creating portfolio with name: " + name + " and date: " + date);
		
		Portfolio portfolio = new Portfolio(name, new FileQuoteManagerFactory(), tickerManager);
		portfolio.parse(new ChangesParser("etc/muutokset.csv"), date);
		portfolio.clear();
		portfolio.process();
		portfolio.getCombinedBookEntryTable(false);
		return portfolio;
	}

}
