package com.stt.portfolio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.stt.portfolio.quotes.Quote;
import com.stt.portfolio.quotes.QuoteManager;
import com.stt.portfolio.transactions.I_BookEntryManagement;



public class BookEntryManager implements I_BookEntryManagement {

	private Map<String, BookEntry> bookEntries;

	private I_TickerManager tickerManager = null;

	private double portfolioValue = 0.0;

	private String broker;

	private CashManager cashManager;

	private TaxManager taxManager = null;

	public BookEntryManager(I_TickerManager tickerManager,
			CashManager cashManager, TaxManager taxManager) {
		this.tickerManager = tickerManager;
		this.cashManager = cashManager;
		this.taxManager = taxManager;
		bookEntries = new HashMap<String, BookEntry>();
	}

	public void clear() {
		bookEntries.clear();
	}

	public void renameBookEntry(String symbol, String newSymbol, Date date) {
		if (bookEntries.containsKey(symbol)) {
			BookEntry entry = bookEntries.get(symbol);
			// System.out.println("Renaming " + symbol + " to " + newSymbol +
			// " " + date);

			if (bookEntries.containsKey(newSymbol)) {
				BookEntry entryNew = bookEntries.get(newSymbol);
				entryNew.add(entry);

			} else {
				entry.changeSymbol(newSymbol);

				entry.setName(tickerManager.getName(newSymbol));
				bookEntries.put(newSymbol, entry);

			}
			bookEntries.remove(symbol);
		}
	}

	public void spinoffBookEntry(String symbol, String newSymbol, double ratio, double stockratio,
			boolean storeParent) {

		if (bookEntries.containsKey(symbol)) {
			BookEntry entry = bookEntries.get(symbol);

			BookEntry entryNew = createBookEntry(newSymbol);
			List<BookEntryItem> items = entryNew.add(entry, storeParent);

			//			if (symbol.equals("MEO1V")){
			//				System.out.println("spinoff " + symbol + " " + newSymbol + " " + ratio + " " + stockratio);
			//			}
			if (items == null) {
				entryNew.spinoff(ratio, stockratio, entryNew.subEntries);
			}
			else {
				entryNew.spinoff(ratio, stockratio, items);
			}
		}
	}

	public void removeBookEntry(String symbol) {
		if (bookEntries.containsKey(symbol)) {
			bookEntries.remove(symbol);
		}
	}

	public void split(String symbol, double ratio, Date date) {
		if (bookEntries.containsKey(symbol)) {
			// System.out.println("Split: " + symbol + " " + ratio + " " +
			// date);
			BookEntry entry = bookEntries.get(symbol);
			entry.split(ratio);
		}
	}

	public BookEntry createBookEntry(String symbol) {
		BookEntry entry;
		if (bookEntries.containsKey(symbol)) {
			entry = bookEntries.get(symbol);
		} else {
			String name = symbol;
			String ccy = "";
			Stock s = tickerManager.getStock(symbol);
			if (s != null) {
				name = s.getName();
				ccy = s.getCcy();
			}
			entry = new BookEntry(symbol, name, ccy, broker, cashManager, this,
					taxManager);
			if (s != null) {
				entry.setOption(s.isOption());
			} else {
				// System.out.println("null " + name);
			}

			bookEntries.put(symbol, entry);
		}
		return entry;
	}

	public void removeEmptyBookEntries() {

		for (Iterator<Map.Entry<String, BookEntry>> i = bookEntries.entrySet()
				.iterator(); i.hasNext();) {
			Map.Entry<String, BookEntry> entry = i.next();

			if (entry.getValue().getAmount() < 0.0001) {
				i.remove();
			}
		}

	}

	public void printEntries() {

		Collection<BookEntry> entries = bookEntries.values();
		for (BookEntry e : entries) {
			if (e.getAmount() > 0.000001) {
				e.print();
			}
		}
	}

	public Collection<BookEntry> getBookEntryCollection() {
		return bookEntries.values();
	}

	public Map<String, BookEntry> getBookEntries() {
		return bookEntries;
	}

	public void addEntry(BookEntry e) {
		if (bookEntries.containsKey(e.getSymbol())) {
			BookEntry old = bookEntries.get(e.getSymbol());
			old.add(e);
		} else {
			BookEntry newEntry = new BookEntry(e);
			newEntry.setBookEntryManager(this);
			bookEntries.put(e.getSymbol(), newEntry);
		}
	}

	private void updateMarketPrices(QuoteManager quoteManager,
			I_TickerManager tickerManager, Date date) {
		Collection<BookEntry> entries = getBookEntryCollection();
		for (BookEntry e : entries) {
			if (e.getAmount() > 0.0) {
				double rate = 1.0000;
				// table[i][j++] = String.format("%1$.2f",
				// quoteManager.getQuote(e.getSymbol()).getLast());
				double marketPrice = e.getPrice();

				Quote quote = quoteManager.getQuote(e.getSymbol(), date);
				if (quote != null) {
					marketPrice = quote.getLast();

					rate = quote.getRate();

					e.setQuoteDate(quote.getDate());

				}
				else {
					System.out.println("null quote: " + date + " " + e.getSymbol());
				}
				Stock stock = tickerManager.getStock(e.getSymbol());
				if (stock != null) {
					marketPrice = marketPrice / stock.getPriceDivider();
				}
				e.setMarketPrice(marketPrice / rate);
				e.setRate(rate);
			}
		}
	}

	private int getItemCount() {
		int count = 0;

		Collection<BookEntry> entries = getBookEntryCollection();
		for (BookEntry e : entries) {

			count += e.getChildrenCount();
		}
		return count;
	}


	public Object[][] getBookEntryTable(QuoteManager quoteManager, Date date,
			boolean showItems) {

		int COLUMNS = 15;



		XIRRAdapter xirrAdapter = new XIRRAdapter();

		updateMarketPrices(quoteManager, tickerManager, date);

		List<BookEntry> entries = new ArrayList<BookEntry>();

		entries.addAll(getBookEntryCollection());

		sortBookEntries(entries);
		Object[][] table;
		if (showItems) {
			table = new Object[getItemCount()][COLUMNS];
		} else {
			table = new Object[entries.size()][COLUMNS];
		}

		portfolioValue = 0.0;
		int i = 0;

		for (BookEntry e : entries) {
			if (e.getAmount() > 0.0) {
				int j = 0;
				Stock stock = tickerManager.getStock(e.getSymbol());


				if (!showItems) {

					table[i][j++] = (stock != null) ? stock.getName() : e.getSymbol();
					table[i][j++] = e.getCcy();

					table[i][j++] = Double.valueOf(e.getMarketPrice() * e.getRate());
					table[i][j++] = (e.getQuoteDate() != null) ? e
							.getQuoteDate() : "N/A";

							table[i][j++] = Integer.valueOf((int) e.getAmount());
							// table[i][j++] = new Double(e.getCost());
							// table[i][j++] = String.format("%1$.2f", e.getPrice());
							table[i][j++] = Double.valueOf(e.getPriceOriginalCurrency()); // Show
							// buy
							// price
							// in
							// original
							// currency
							table[i][j++] = e.getPurchaseDate();

							table[i][j++] = Double.valueOf(e.getMarketPrice() * e.getRate()
									* e.getAmount());
							table[i][j++] = Double.valueOf(e.getMarketPrice()
									* e.getAmount());
							portfolioValue += e.getMarketPrice() * e.getAmount();
							table[i][j++] = Double.valueOf(e.getTaxCost());
							table[i][j++] = Double.valueOf(e.getDividents());

							table[i][j++] = Double.valueOf(e.getProfit(e.getMarketPrice()));
							table[i][j++] = Double.valueOf(e.getProfitPercent(e
									.getMarketPrice()));
							table[i][j++] = Double.valueOf(xirrAdapter.getXirr(e.getQuoteDate(), e));

							++i;
				} else {
					Iterator<BookEntryItem> iter = e.getChildrenIterator();
					while (iter.hasNext()) {
						j = 0;
						BookEntryItem item = iter.next();

						table[i][j++] = (stock != null) ? stock.getName() : e.getSymbol();
						table[i][j++] = e.getCcy();

						table[i][j++] = Double.valueOf(e.getMarketPrice()
								* e.getRate());
						table[i][j++] = (e.getQuoteDate() != null) ? e
								.getQuoteDate() : "N/A";

								table[i][j++] = Integer.valueOf((int) item.getAmount());
								// table[i][j++] = new Double(e.getCost());
								// table[i][j++] = String.format("%1$.2f",
								// e.getPrice());
								table[i][j++] = Double.valueOf(-item
										.getCostInOriginalCurrency()
										/ item.getAmount()); // Show buy price in
								// original currency
								table[i][j++] = item.getPurchaseDate();

								table[i][j++] = Double.valueOf(e.getMarketPrice() *
										e.getRate() * item.getAmount());
								table[i][j++] = Double.valueOf(e.getMarketPrice() *
										item.getAmount());

								table[i][j++] = Double.valueOf(item.getTaxCost() );
								table[i][j++] = Double.valueOf(item.getDividents());

								table[i][j++] = Double.valueOf(item.getProfit(e.getMarketPrice()));

								table[i][j++] = Double.valueOf(item.getProfitPercent(e.getMarketPrice()));

								table[i][j++] = Double.valueOf(xirrAdapter.getXirr(e.getQuoteDate(), e.getMarketPrice(), item));

								portfolioValue += e.getMarketPrice() * item.getAmount();

								i++;
					}
				}

			}
		}
		// add weight % from portfolio
		for (int k = 0; k < i; ++k) {
			if (table[k][8] != null) {
				table[k][14] = Double.valueOf(100.0 * (Double) (table[k][8])
						/ portfolioValue);
			}
		}
		return table;
	}

	public List<BookEntry> getBookEntryList(QuoteManager quoteManager, Date date) {

		updateMarketPrices(quoteManager, tickerManager, date);

		List<BookEntry> entries = new ArrayList<BookEntry>();

		entries.addAll(getBookEntryCollection());

		sortBookEntries(entries);
		return entries;
	}

	private void sortBookEntries(List<BookEntry> entries) {
		Collections.sort(entries, new Comparator<BookEntry>() {

			@Override
			public int compare(BookEntry o1, BookEntry o2) {
				Double v1 = Double.valueOf(o1.getMarketPrice() * o1.getAmount());
				Double v2 = Double.valueOf(o2.getMarketPrice() * o2.getAmount());
				return v1.compareTo(v2);
			}

		});
	}

	public double getPortfolioValue() {
		return portfolioValue;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public BookEntry getBookEntry(String ticker) {

		return bookEntries.get(ticker);
	}

	public I_TickerManager getTickerManager() {
		return tickerManager;
	}

	public TaxManager getTaxManager() {
		return taxManager;
	}

	public Set<Entry<String, Double>> getCountryAllocationTable(QuoteManager quoteManager, Date date) {

		updateMarketPrices(quoteManager, tickerManager, date);

		List<BookEntry> entries = new ArrayList<BookEntry>();

		entries.addAll(getBookEntryCollection());

		sortBookEntries(entries);
		
		Map<String, Double> allocations = new HashMap<>();
		
		
		for (BookEntry e : entries) {
			if (e.getAmount() > 0.0) {
				Stock stock = tickerManager.getStock(e.getSymbol());
				if (stock != null) {
					String country = stock.getCountry();
					if (allocations.containsKey(country)) {
						double newAmount = allocations.get(country) + (e.getMarketPrice() * e.getAmount());
						allocations.put(country, newAmount);
					} else {
						allocations.put(country, (e.getMarketPrice() * e.getAmount()));
					}
				}
				
			}
		}
		
		Set<java.util.Map.Entry<String, Double>> allocationEntries = allocations.entrySet();
		
		return allocationEntries;
	}
}
