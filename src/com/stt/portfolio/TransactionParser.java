package com.stt.portfolio;

import java.io.*;
import java.util.Date;

import com.stt.portfolio.transactions.Buy;
import com.stt.portfolio.transactions.CapitalRepayment;
import com.stt.portfolio.transactions.Dividend;
import com.stt.portfolio.transactions.DividendTax;
import com.stt.portfolio.transactions.OptionSubscription;
import com.stt.portfolio.transactions.RightsSubscription;
import com.stt.portfolio.transactions.Sell;
import com.stt.portfolio.transactions.Subscription;
import com.stt.portfolio.transactions.Tax;
import com.stt.portfolio.transactions.Transaction;
import com.stt.portfolio.transactions.Transfer;
import com.stt.portfolio.transactions.TransferAccount;

/**
 * The Class TransactionParser.
 */
public class TransactionParser implements I_Parser {

	/** The date parser. */
	DateParser dateParser = new DateParser();

	/** The filename. */
	String filename;

	/**
	 * Instantiates a new transaction parser.
	 * 
	 * @param filename
	 *            the filename
	 */
	public TransactionParser(String filename) {
		this.filename = filename;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.stt.portfolio.I_Parser#parse(com.stt.portfolio.Portfolio)
	 */
	public void parse(Portfolio portfolio) {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "ISO8859_1"));

			String str;
			while ((str = in.readLine()) != null) {
				if (str.length() > 0 && !str.startsWith("#")) {
					Transaction t;
					try {
						t = process(str.trim());
						if (t != null) {
							portfolio.addTransaction(t);
						}
					} catch (ParseException e) {

						e.printStackTrace();
					}
				}
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Write. Appends transaction to the transaction file
	 * 
	 * @param t
	 *            the transaction
	 */
	public void write(Transaction t) {

		try {

			OutputStream os = new FileOutputStream(filename, true);

			OutputStreamWriter ow = new OutputStreamWriter(os, "ISO8859_1");

			String line = t.getLine();

			ow.write(line);
			ow.close();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * Process.
	 * 
	 * @param line
	 *            the line
	 * 
	 * @return the transaction
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	private Transaction process(String line) throws ParseException {
		Transaction t = null;

		if (line.length() > 0) {

			String[] items = line.split(";");
			if (items.length < 9) {
				throw new ParseException("Illegal line: " + line);
			}
			String datestr = items[0].trim();
			String eventtype = items[1].trim();
			String symbol = items[2].trim();
			// String name = items[3].trim();
			String amountstr = items[4];
			String pricestr = items[5];
			String broker = items[6].trim();
			String coststr = items[7];
			String totalcoststr = items[8];
			double rate = 1.0000; // currency rate
			if (items.length > 9) {
				rate = Util.convertToDouble(items[9]);
			}

			 if (eventtype.equalsIgnoreCase("MERK.OIKEUS")) {
				t = new OptionSubscription();

				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);

				if (items.length > 11) {

					String optionTicker = items[10].trim();
					double ratio = Util.convertToDouble(items[11]);
					((OptionSubscription) t).setOptionTicker(optionTicker);
					((OptionSubscription) t).setSubscriptionRatio(ratio);
				}
			} else if (eventtype.equalsIgnoreCase("myynti")) {
				t = new Sell();

				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);

			} else if (eventtype.equalsIgnoreCase("osinko")) {
				t = new Dividend();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);

			} else if (eventtype.equalsIgnoreCase("merkintä_oikeuksilla")) {
				t = new RightsSubscription();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);
				double oldOwnership = Util.convertToDouble(items[10]);
				double ratio = Util.convertToDouble(items[11]);

				((RightsSubscription) t).setAmountOfOldOwnership(oldOwnership);
				((RightsSubscription) t).setRatio(ratio);

			} else if (eventtype.equalsIgnoreCase("osto")
					|| eventtype.equalsIgnoreCase("merkintä")) {
				t = new Buy();

				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);
			} else if (eventtype.equalsIgnoreCase(Transaction.TR_DIVIDEND_TAX)) {
				t = new DividendTax();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);
			} 
			 
			else if (eventtype.equalsIgnoreCase(Transaction.TR_TAX)) {
				t = new Tax();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);

			} else if (eventtype.equalsIgnoreCase("siirto")) {
				t = new Transfer();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);

			} else if (eventtype.equalsIgnoreCase("PÄÄOMAN PALAUTUS")) {
				t = new CapitalRepayment();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);
			} else if (eventtype.equalsIgnoreCase(Transaction.TR_ACCOUNT_TRANSFER)) {
				t = new TransferAccount();
				parseBuySell(datestr, broker, symbol, pricestr, totalcoststr,
						amountstr, coststr, t);
				if (items.length > 10) {

					String newBroker = items[10].trim();
					((TransferAccount) t).setOldBroker(newBroker);
				}
			}else {
				System.out.println(eventtype);
			}
			if (t != null) {
				t.setRate(rate);
			}
		}

		return t;
	}

	/**
	 * Parses the buy sell.
	 * 
	 * @param datestr
	 *            the datestr
	 * @param broker
	 *            the broker
	 * @param symbol
	 *            the symbol
	 * @param pricestr
	 *            the pricestr
	 * @param totalcoststr
	 *            the totalcoststr
	 * @param amountstr
	 *            the amountstr
	 * @param t
	 *            the t
	 * @param brokerCostStr
	 *            the broker cost str
	 * 
	 * @throws ParseException
	 *             the parse exception
	 */
	private void parseBuySell(String datestr, String broker, String symbol,
			String pricestr, String totalcoststr, String amountstr,
			String brokerCostStr, Transaction t) throws ParseException {

		Date date = dateParser.parseDate(datestr);
		t.setDate(date);

		Double totalCost = Util.convertToDouble(totalcoststr);
		Double price = Util.convertToDouble(pricestr);
		if (Util.isDateBeforeEuro(date)) {
			totalCost /= Util.EURO_TO_MARKKA_RATIO;
			price /= Util.EURO_TO_MARKKA_RATIO;

		}
		t.setAmount(Util.convertToDouble(amountstr));
		t.setCost(totalCost);
		t.setPrice(price);
		t.setBrokerCost(Util.convertToDouble(brokerCostStr));
		t.setTicker(symbol);
		t.setBroker(broker);

	}

}
