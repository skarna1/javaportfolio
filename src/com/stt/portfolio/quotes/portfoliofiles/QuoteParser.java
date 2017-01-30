package com.stt.portfolio.quotes.portfoliofiles;

import java.util.Date;

import com.stt.portfolio.DateParser;
import com.stt.portfolio.ParseException;
import com.stt.portfolio.Util;
import com.stt.portfolio.quotes.Quote;

public class QuoteParser {

	private DateParser dateParser = new DateParser();

	public Quote parse(String line) throws ParseException {

		String[] items = line.split(",");

		if (items.length < 5) {
			throw new ParseException("Parse error: " + line);
		}

		String high = items[1].trim();
		String low = items[2].trim();
		String last = items[3].trim();
		String amount = items[4].trim();

		double rate = 1.00000;
		
		Quote quote = new Quote();

		try {

			quote.setAmount(Long.parseLong(amount));
			quote.setLast(Double.parseDouble(last));
			
			quote.setHighIfAvailable(high);
			quote.setLowIfAvailable(low);

			Date date = dateParser.parseDateDDMMYYYY(items[0].trim());
			quote.setDate(date);
			if (items.length > 5) {
				rate = Util.convertToDouble(items[5]);
			}
			quote.setRate(rate);

		} catch (NumberFormatException e) {
			e.printStackTrace();
			System.out.println("high: " + high);
			System.out.println("Low: " + low);
			System.out.println("Last: " + last);
			System.out.println("Amount: " + amount);
			System.out.println("Rate: " + rate);
		}
		return quote;
	}

}
