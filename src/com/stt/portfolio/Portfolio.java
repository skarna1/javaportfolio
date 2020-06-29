package com.stt.portfolio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.stt.portfolio.quotes.QuoteManager;
import com.stt.portfolio.quotes.QuoteManagerFactory;
import com.stt.portfolio.transactions.ChangeTransaction;
import com.stt.portfolio.transactions.TaxEntry;
import com.stt.portfolio.transactions.Transaction;
import com.stt.portfolio.transactions.TransferAccount;

/**
 * The Class Portfolio.
 */
public class Portfolio {

	private static final String KAIKKI = "Kaikki";

	/*
	 * List of all accounts belonging to portfolio
	 */
	/** The accounts. */
	private List<Account> accounts = new ArrayList<Account>();

	private List<Transaction> transactions = new ArrayList<Transaction>();

	private List<ChangeTransaction> changeTransactions = new ArrayList<ChangeTransaction>();

	/*
	 * Collection of all book entries combined from all accounts
	 */
	/** The book entry manager. */
	private BookEntryManager bookEntryManager = null;

	/** The tax manager. */
	private TaxManager taxManager = new TaxManager();

	/** The quote manager. */
	private QuoteManager quoteManager = null;

	/** The ticker manager. */
	private I_TickerManager tickerManager = null;

	/** The quote manager factory. */
	private QuoteManagerFactory quoteManagerFactory = null;

	/** The cash manager. */
	private CashManager cashManager = new CashManager();

	private String name;

	private TransactionParser transactionParser = null;
	
	/**
	 * Instantiates a new portfolio.
	 * 
	 * @param f
	 *            the quoteManagerFactory
	 * @param tickerManager
	 *            the ticker manager
	 */
	public Portfolio(String name, QuoteManagerFactory f,
			I_TickerManager tickerManager) {
		this.quoteManagerFactory = f;
		this.tickerManager = tickerManager;
		this.name = name;
		quoteManager = f.create();
		quoteManager.init();
		
		bookEntryManager = new BookEntryManager(tickerManager, cashManager,
				taxManager);
		
		transactionParser = new TransactionParser(
				PortfolioFactory.PORTFOLIO_PATH + name + "/tapahtumat.csv");

	}

	/*
	 * Owner of the portfolio
	 */
	/** The owner. */
	String owner = "";

	private Date portfolioDate;

	/**
	 * Gets the owner.
	 * 
	 * @return the owner
	 */
	public String getOwner() {
		return owner;
	}

	/**
	 * Sets the owner.
	 * 
	 * @param owner
	 *            the new owner
	 */
	public void setOwner(String owner) {
		this.owner = owner;
	}

	/**
	 * Adds the account.
	 * 
	 * @param account
	 *            the account
	 */
	public void addAccount(Account account) {
		accounts.add(account);
	}

	/**
	 * Creates the account.
	 * 
	 * @param broker
	 *            the broker
	 * 
	 * @return the account
	 */
	public Account createAccount(String broker) {
		Account account = new Account(broker,
				new BookEntryManager(tickerManager,cashManager, taxManager));

		accounts.add(account);
		return account;
	}

	/**
	 * Gets the accounts.
	 * 
	 * @return the accounts
	 */
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * Gets the account.
	 * 
	 * @param broker
	 *            the broker
	 * 
	 * @return the account
	 */
	public Account getAccount(String broker) {
		Iterator<Account> iter = accounts.iterator();
		while (iter.hasNext()) {

			Account a = iter.next();
			if (a.getBroker().equalsIgnoreCase(broker)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Adds the transaction.
	 * 
	 * @param t
	 *            the transaction
	 */
	public void addTransaction(Transaction t) {

		String broker = t.getBroker();

		if (broker.equals("SRT")) {
			broker = accounts.get(0).getBroker();
			t.setBroker(broker);
		}
		Account account = getAccount(broker);
		if (account == null) {
			account = createAccount(broker);
		}

		// account.addTransaction(t);
		transactions.add(t);

	}

	/**
	 * Adds the change transaction.
	 * 
	 * @param t
	 *            the t
	 */
	public void addChangeTransaction(ChangeTransaction t) {
		

		changeTransactions.add(t);

	}

	/**
	 * Parses the.
	 * 
	 * @param changesParser
	 *            the changes parser
	 */
	public void parse(ChangesParser changesParser,
			 Date date) {

		portfolioDate = date;
		transactionParser.parse(this);
		changesParser.parse(this);

		process();
	}

	/**
	 * Clear.
	 */
	private void clear() {
		bookEntryManager.clear();
		taxManager.clear();
		cashManager.clear();
		for (Account a : accounts) {
			a.clear();
		}
	}

	/**
	 * Processes transactions to portfolioDate.
	 */
	public void process() {
		clear();
		populateBookEntries();
		for (Account a : accounts) {

			a.generateBookEntries();
			a.addBookEntries(bookEntryManager);
		}
		cashManager.process();
	}

	public void process(Date date) {
		portfolioDate = date;
		process();
	}
	
	private void processChangeTransactions(
			ListIterator<ChangeTransaction> chIter, Date date) {
		while (chIter.hasNext()) {
			ChangeTransaction ct = chIter.next();

			if (ct.getDate().before(date) && ct.getDate().before(portfolioDate)) {
				// System.out.println("process date: " + ct.getDate() + " " +
				// date);
				for (Account a : accounts) {
					a.process(ct);
				}

			} else {
				// System.out.println("date: " + ct.getDate() + " " + date);
				chIter.previous();
				break;
			}
		}
	}

	

	private void populateBookEntries() {

		ListIterator<Transaction> trIter = transactions.listIterator();
		ListIterator<ChangeTransaction> chIter = changeTransactions
				.listIterator();

		while (trIter.hasNext()) {

			Transaction t = trIter.next();
			if (Util.compareDates(t.getDate(),portfolioDate) <= 0) {
				processChangeTransactions(chIter, t.getDate());

				if (t.getOp().equalsIgnoreCase(Transaction.TR_ACCOUNT_TRANSFER)) {
					Account a1 = getAccount(t.getBroker());
					Account a2 = getAccount(((TransferAccount) t)
							.getOldBroker());

					a1.add(a2);
					a2.clear();
				} else {
					Account a = getAccount(t.getBroker());
					a.process(t);
				}
			}
			else  {
				trIter.previous();
				break;
			}
		}
		processChangeTransactions(chIter, Calendar.getInstance().getTime());
	}

	/**
	 * Prints the.
	 */
	public void print() {
		System.out.println("Number of accounts: " + accounts.size());
		for (Account a : accounts) {
			a.print();
		}
	}

	/**
	 * Gets the combined book entry table.
	 * 
	 * @return the combined book entry table
	 */
	public Object[][] getCombinedBookEntryTable(boolean showItems) {

		return bookEntryManager.getBookEntryTable(quoteManager, portfolioDate,
				showItems);
	}

	public List<BookEntry> getCombinedBookEntryList() {

		return bookEntryManager.getBookEntryList(quoteManager, portfolioDate);
	}

	/**
	 * Gets the book entry table of the broker.
	 * 
	 * @param broker
	 *            the broker
	 * 
	 * @return the book entry table
	 */
	public Object[][] getBookEntryTable(String broker, boolean showItems) {

		Account a = getAccount(broker);

		return a.getBookEntryTable(quoteManager, portfolioDate, showItems);
	}

	/**
	 * Gets the combined tax report table.
	 * 
	 * @param year
	 *            the year
	 * 
	 * @return the combined tax report table
	 */
	public Object[][] getCombinedTaxReportTable(int year) {

		return taxManager.getCombinedTaxReportTable(tickerManager, year);
	}

	/**
	 * Gets the tax entries.
	 * 
	 * @param year
	 *            the year
	 * 
	 * @return the tax entries
	 */
	private List<TaxEntry> getTaxEntries(int year) {
		return taxManager.getTaxEntries(year);
	}

	/**
	 * Gets the tax report years.
	 * 
	 * @return the tax report years
	 */
	public Integer[] getTaxReportYears() {

		return taxManager.getTaxReportYears();
	}

	public Object[] getTransactionTickers(String broker,
			String selectedTransaction) {

		Set<String> tickerSet = new HashSet<String>();
		// Calendar c = Calendar.getInstance();
		for (Account a : accounts) {
			if (broker == null || broker.equalsIgnoreCase(KAIKKI)
					|| a.getBroker().equalsIgnoreCase(broker)) {

				for (Transaction t : getAccountTransactions(a.getBroker())) {
					// c.setTime(t.getDate());
					// int year = c.get(Calendar.YEAR);

					if (selectedTransaction.equalsIgnoreCase(KAIKKI)
							|| selectedTransaction.equalsIgnoreCase(t.getOp())) {
						if (!t.getTicker().equalsIgnoreCase("siposiirto")
								&& !t.getTicker().equalsIgnoreCase("potvero")) {
							String name = tickerManager.getName(t.getTicker());
							if (name != null) {
								tickerSet.add(name);
							}
						}

					}
				}
			}
		}
		String[] names = tickerSet.toArray(new String[tickerSet.size()]);
		Arrays.sort(names);

		return addAllItem(names);
	}

	String[] addAllItem(String[] items) {
		String[] names = new String[items.length + 1];
		names[0] = KAIKKI;
		int i = 1;
		for (String s : items) {
			names[i] = s;
			// System.out.println(s);
			i++;
		}
		return names;
	}

	/**
	 * Gets the transaction years.
	 * 
	 * @param broker
	 *            the broker
	 * 
	 * @return the transaction years
	 */
	public String[] getRawTransactionYears(String broker) {

		Set<String> yearSet = new HashSet<String>();
		Calendar c = Calendar.getInstance();
		for (Account a : accounts) {
			if (broker == null || a.getBroker().equalsIgnoreCase(broker)) {

				for (Transaction t : getAccountTransactions(a.getBroker())) {
					c.setTime(t.getDate());
					int year = c.get(Calendar.YEAR);

					yearSet.add(Integer.toString(year));
				}
			}
		}

		String[] years = yearSet.toArray(new String[yearSet.size()]);
		Arrays.sort(years);

		return (years);
	}
	
	public String[] getTransactionYears(String broker) {
		String[] years =  getRawTransactionYears(broker);
		return addAllItem(years);
	}

	/**
	 * Gets the transaction table.
	 * 
	 * @param year
	 *            the year
	 * @param transaction
	 *            the transaction name
	 * @param broker
	 *            the broker
	 * 
	 * @return the transaction table
	 */
	public Object[][] getTransactionTable(String year, String transaction,
			String broker, String stockName) {

		List<Transaction> transactions = getTransactions(year, broker);

		List<Object[]> trs = new ArrayList<Object[]>();
		double sum = 0.0;

		for (Transaction e : transactions) {
			if (transaction.equalsIgnoreCase(KAIKKI)
					|| transaction.equalsIgnoreCase(e.getOp())) {
				Stock stock = tickerManager.getStock(e.getTicker());

				if (stockName.equalsIgnoreCase(KAIKKI)
						|| (stock != null && stock.getName().equals(stockName))) {
					Object[] row = new Object[11];

					int j = 0;

					row[j++] = e.getDate();
					row[j++] = e.getOp();
					if (e.getOp().equals(Transaction.TR_ACCOUNT_TRANSFER)) {
						row[j++] = ((TransferAccount) e).getOldBroker();
					} else {
						row[j++] = e.getTicker();
					}
					row[j++] = (stock != null) ? stock.getName() : e
							.getTicker();

					row[j++] = Double.valueOf(e.getAmount());
					row[j++] = Double.valueOf(e.getPrice());
					row[j++] = Double.valueOf(e.getBrokerCost());
					row[j++] = Double.valueOf(e.getRate());
					row[j++] = e.getBroker();
					row[j++] = Double.valueOf(e.getCost());
					row[j++] = Double.valueOf(e.getCost() / e.getRate());

					sum += e.getCost() / e.getRate();
					trs.add(row);
				}

			}
		}
		Object[] row = new Object[11];
		int j = 0;
		row[j++] = "";
		row[j++] = "";
		row[j++] = "";
		row[j++] = "Summa";

		row[j++] = "";
		row[j++] = "";
		row[j++] = "";
		row[j++] = "";
		row[j++] = "";
		row[j++] = "";
		row[j++] = Double.valueOf(sum);
		trs.add(row);

		// Build table
		Object[][] table = new Object[trs.size()][11];
		int i = 0;
		for (Object[] o : trs) {

			table[i++] = o;
		}

		return table;
	}

	/**
	 * Gets the transactions.
	 * 
	 * @param year
	 *            the year
	 * @param broker
	 *            the broker
	 * 
	 * @return the transactions
	 */
	List<Transaction> getTransactions(String year, String broker) {
		// Find entries of the year
		List<Transaction> ts = new ArrayList<Transaction>();
		Calendar c = Calendar.getInstance();
		for (Account a : accounts) {

			for (Transaction t : getAccountTransactions(a.getBroker())) {
				if (broker.equalsIgnoreCase(KAIKKI)
						|| broker.equals(t.getBroker())) {
					c.setTime(t.getDate());
					if (year.equalsIgnoreCase(KAIKKI)
							|| c.get(Calendar.YEAR) == Integer.valueOf(year)) {
						ts.add(t);
					}
				}
			}
		}

		Collections.sort(ts);

		return ts;
	}

	/**
	 * Gets the tax profit or loss.
	 * 
	 * @param year
	 *            the year
	 * @param isProfit
	 *            the is profit
	 * 
	 * @return the tax profit or loss
	 */
	public double getTaxProfitOrLoss(int year, boolean isProfit) {
		List<TaxEntry> taxEntries = getTaxEntries(year);
		double sum = 0.0;
		for (TaxEntry e : taxEntries) {
			if (isProfit && e.getProfit() > 0.0) {
				sum += e.getProfit();
			} else if (!isProfit && e.getProfit() < 0.0) {
				sum += e.getProfit();
			}

		}
		return sum;
	}

	/**
	 * Gets the brokers.
	 * 
	 * @return the brokers
	 */
	public String[] getBrokers() {
		List<String> brokers = new ArrayList<String>();
		for (Account a : accounts) {
			if (!a.getBroker().equalsIgnoreCase("ver")) {
				brokers.add(a.getBroker());
			}
		}
		String[] brokerarray = new String[brokers.size()];
		brokerarray = brokers.toArray(brokerarray);
		return brokerarray;
	}

	/**
	 * Gets the brokers having book entry.
	 * 
	 * @return the brokers
	 */
	public Object[] getBookEntryBrokers() {
		List<String> brokers = new ArrayList<String>();
		for (Account a : accounts) {

			if (a.getBookEntries().size() > 0) {
				brokers.add(a.getBroker());
			}
		}
		return brokers.toArray();
	}

	/**
	 * Gets the book entries.
	 * 
	 * @return the book entries
	 */
	public Map<String, Collection<BookEntry>> getBookEntries() {
		Map<String, Collection<BookEntry>> entries = new HashMap<String, Collection<BookEntry>>();

		for (Account a : accounts) {
			entries.put(a.getBroker(), a.getBookEntries());
		}
		return entries;
	}

	/**
	 * Gets the sectors.
	 * 
	 * @return the sectors
	 */
	public String[] getSectors() {
		return tickerManager.getSectors();
	}

	/**
	 * Gets the stocks by sector.
	 * 
	 * @return the stocks by sector
	 */
	public Map<String, List<String>> getStocksBySector() {
		return tickerManager.getStocksBySector();
	}

	/**
	 * Gets the ticker.
	 * 
	 * @param name
	 *            the name of the stock
	 * 
	 * @return the ticker
	 */
	public String getTicker(String name) {
		return tickerManager.getTicker(name);
	}

	/**
	 * Gets the stock.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the stock
	 */
	public Stock getStock(String name) {

		return tickerManager.getStock(getTicker(name));
	}

	/**
	 * Sets the ticker manager.
	 * 
	 * @param tickerManager
	 *            the new ticker manager
	 */
	public void setTickerManager(I_TickerManager tickerManager) {
		this.tickerManager = tickerManager;
	}

	/**
	 * Update quotes.
	 */
	public void updateQuotes() {
		quoteManager = quoteManagerFactory.create();
		quoteManager.init();

	}

	/**
	 * Gets the portfolio value.
	 * 
	 * @return the portfolio value
	 */
	public double getStocksValue() {

		return bookEntryManager.getPortfolioValue();
	}

	public double getStocksValue(String broker) {
		Account account = getAccount(broker);
		if (account != null) {
			return account.getBookEntryManager().getPortfolioValue();
		}
		return 0.0;
	}
	/**
	 * Gets the portfolio value.
	 * 
	 * @return the portfolio value
	 */
	public double getPortfolioValue() {

		return getStocksValue() + getCash();
	}

	/**
	 * Gets the cash.
	 * 
	 * @return the cash
	 */
	public double getCash() {
		return cashManager.getCash();
	}

	/**
	 * Gets the invested.
	 * 
	 * @return the invested
	 */
	public double getInvested() {
		return cashManager.getInvestedCapital();
	}

	public double getPortfolioValue(Date endDate) {
		process(endDate);
		getCombinedBookEntryTable(false);
		return getPortfolioValue();
	}
	
	/**
	 * Gets the xirr.
	 * 
	 * @return the xirr
	 */
	public double getXirr() {

		return cashManager.getXirr(getPortfolioValue(), portfolioDate) * 100.0;
	}
	
	public double getXirr(double startValue, Date startDate) {

		return cashManager.getXirrBetweenDates(startValue, startDate, 
				getPortfolioValue(), portfolioDate) * 100.0;
	}
	
	private List<Transaction> getAccountTransactions(String broker) {
		List<Transaction> trs = new ArrayList<Transaction>();

		for (Transaction t : transactions) {
			if (t.getBroker().equalsIgnoreCase(broker)) {
				trs.add(t);
			}
		}

		return trs;
	}

	public String getName() {
		return this.name;
	}
	
	public Date getPortfolioDate() {
		return portfolioDate;
	}

	public void setPortfolioDate(Date portfolioDate) {
		this.portfolioDate = portfolioDate;
	}

	public void writeTransaction(Transaction t) {
		transactionParser.write(t);
		
	}

	public Set<Entry<String, Double>> getCountryAllocations() {
		return bookEntryManager.getCountryAllocationTable(quoteManager, portfolioDate);
		
	}
}
