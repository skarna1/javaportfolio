package com.stt.portfolio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.stt.portfolio.transactions.I_BookEntryModifier;
import com.stt.portfolio.transactions.OptionSubscription;
import com.stt.portfolio.transactions.RightsSubscription;
import com.stt.portfolio.transactions.TaxEntry;
import com.stt.portfolio.transactions.Transaction;


/**
 * The Class BookEntry.
 */
public class BookEntry implements I_BookEntryModifier {

	/** The Constant DELTA. */
	private static final double DELTA = 0.00001;

	/** The symbol. */
	String symbol;

	/** The name. */
	String name;

	String ccy;

	/** The broker. */
	String broker;

	/** The market price. */
	double marketPrice;

	/** The current currency rate. */
	double rate;

	/** The quote date. */
	Date quoteDate;

	/** The manager. */
	CashManager cashManager = null;

	/** The book entry manager. */
	BookEntryManager bookEntryManager = null;

	/** The sub entries. */
	List<BookEntryItem> subEntries = new ArrayList<BookEntryItem>();

	TaxManager taxManager = null;

	boolean isOption = false;

	double lastDividendAmount = 0.0;

	/**
	 * Instantiates a new book entry.
	 *
	 * @param symbol the symbol
	 * @param name the name
	 * @param broker the broker
	 * @param manager the manager
	 * @param bookEntryManager the book entry manager
	 * @param taxManager the tax manager
	 */
	public BookEntry(String symbol, String name, String ccy, String broker,
			CashManager manager, BookEntryManager bookEntryManager,
			TaxManager taxManager) {
		this.symbol = symbol;
		this.name = name;
		this.ccy = ccy;
		this.broker = broker;
		this.bookEntryManager = bookEntryManager;
		this.cashManager = manager;
		this.taxManager = taxManager;
	}

	/**
	 * Instantiates a new book entry.
	 *
	 * @param e the e
	 */
	public BookEntry(BookEntry e) {
		this.symbol = new String(e.symbol);
		this.name = new String(e.name);
		this.ccy = new String(e.ccy);
		this.broker = new String(e.broker);
		this.bookEntryManager = e.bookEntryManager;
		this.cashManager = e.cashManager;
		this.taxManager = e.taxManager;
		add(e);
	}



	/**
	 * Gets the dividends.
	 *
	 * @return the dividends
	 */
	public double getDividents() {
		double dividents = 0.0;
		for (BookEntryItem i : subEntries) {
			dividents += i.getDividents();

		}
		return dividents;
	}

	/**
	 * Adds the dividends.
	 *
	 * @param amount the amount
	 * @param dividentsPerShare the dividends per share
	 * @param date the date
	 */
	public void addDividents(double amount, double dividentsPerShare, Date date, String itemtype) {
		double amountToBeHandled = amount;

		Iterator<BookEntryItem> iter = subEntries.iterator();
		while (amountToBeHandled > 0.000001 && iter.hasNext()) {
			BookEntryItem i = iter.next();
			double amountOfItem = Math.min(amountToBeHandled, i.amount);
			i.addDivident(new CashItem(dividentsPerShare * amountOfItem, dividentsPerShare, date, itemtype));
			amountToBeHandled -= amountOfItem;

		}

	}

	/**
	 * Adds the.
	 *
	 * @param e the e
	 */
	public void add(BookEntry e) {
        if (this == e) return;
		for (BookEntryItem i : e.subEntries) {
			subEntries.add(new BookEntryItem(i));
		}
		Collections.sort(subEntries);

	}

	/**
	 * Adds the.
	 *
	 * @param e the e
	 */
	public List<BookEntryItem> add(BookEntry e, boolean storeParent) {
        if (this == e) {
        	//System.out.println(e.symbol + " is same");
        	return null;
        }
        List<BookEntryItem> newitems = new ArrayList<BookEntryItem>();
		for (BookEntryItem i : e.subEntries) {
			BookEntryItem bei = new BookEntryItem(i);
			if (storeParent) {
				bei.setParent(i);
			}
			subEntries.add(bei);
			newitems.add(bei);
		}
		Collections.sort(subEntries);
		return newitems;

	}


	/**
	 * Change symbol.
	 *
	 * @param newSymbol the new symbol
	 */
	public void changeSymbol(String newSymbol) {
		this.symbol = newSymbol;

	}

	/**
	 * Gets the purchase date.
	 *
	 * @return the purchase date
	 */
	public Date getPurchaseDate() {
		if (subEntries.size() == 0) {
			return null;
		}
		return subEntries.get(0).getPurchaseDate();
	}

	/**
	 * Sets the symbol.
	 *
	 * @param symbol the new symbol
	 */
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	/**
	 * Gets the symbol.
	 *
	 * @return the symbol
	 */
	public String getSymbol() {
		return symbol;
	}

	/**
	 * Gets the amount.
	 *
	 * @return the amount
	 */
	public double getAmount() {
		double amount = 0.0;
		for (BookEntryItem i : subEntries) {
			amount += i.getAmount();
		}
		return amount;
	}

	/**
	 * Gets the purchase price per share
	 *
	 * @return the price
	 */
	public double getPrice() {
		double amount = getAmount();
		if (amount == 0.0) {
			return 0.0;
		}
		return getCost() / amount;
	}

	/**
	 * Gets the price in original currency.
	 *
	 * @return the price
	 */
	public double getPriceOriginalCurrency() {
		double amount = getAmount();
		if (amount == 0.0) {
			return 0.0;
		}
		return getCostOriginalCurrency() / amount;
	}

	/**
	 * Gets the cost.
	 *
	 * @return the cost
	 */
	public double getCost() {
		double cost = 0.0;
		for (BookEntryItem i : subEntries) {
			cost += i.getCost();
		}
		return -cost;
	}

	/**
	 * Gets the cost in original currency.
	 *
	 * @return the cost
	 */
	public double getCostOriginalCurrency() {
		double cost = 0.0;
		for (BookEntryItem i : subEntries) {
			cost += i.getCostInOriginalCurrency();
		}
		return -cost;
	}

	/**
	 * Gets the tax cost.
	 *
	 * @return the taxable cost
	 */
	public double getTaxCost() {
		double cost = 0.0;
		for (BookEntryItem i : subEntries) {
			cost += i.getTaxCost();
		}
		return cost;
	}

	/**
	 * Prints the.
	 */
	public void print() {
		System.out.println(getSymbol() + " " + getAmount() + " " + getPrice()
				+ " " + " " + getCost());

		for (BookEntryItem i : subEntries) {
			i.print();
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.stt.portfolio.transactions.I_BookEntryModifier#buy(com.stt.portfolio
	 * .transactions.Transaction)
	 */
	@Override
	public void buy(Transaction t) {
		if (t.getTicker().equals(getSymbol())) {
			addCash(t);
			// System.out.println("BUY: " + t.getBroker() + " " + getSymbol()
			// + " " + t.getAmount() + " " + t.getCost() + " " + t.getRate() +
			// " " + t.getDate());

			BookEntryItem item = new BookEntryItem();
			item.setAmount(t.getAmount());
			item.setCost(t.getCost() / t.getRate());
			item.setPurchaseDate((t.getDate()));
			item.setTaxPurchaseDate((t.getDate()));
			item.setTaxCost(-t.getCost() / t.getRate());
			item.setRateOnPurchaseDate(t.getRate());
			item.setCostInOriginalCurrency(t.getCost());

			subEntries.add(item);

		} else {
			System.out.println("ERROR BUY: symbol mismatch: " + getSymbol()
					+ " " + t.getTicker());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.stt.portfolio.transactions.I_BookEntryModifier#sell(com.stt.portfolio
	 * .transactions.Transaction)
	 */
	@Override
	public void sell(Transaction t) {
		if (t.getTicker().equals(getSymbol())) {
			addCash(t);
			// System.out.println("SELL: " + t.getBroker() + " " + getSymbol()
			// + " " + t.getAmount() + " " + t.getDate());

			double amountToBeHandled = t.getAmount();
			Iterator<BookEntryItem> iter = subEntries.iterator();
			while (amountToBeHandled > DELTA && iter.hasNext()) {

				BookEntryItem i = iter.next();
				if (i.getAmount() <= amountToBeHandled + DELTA) {
					// All sold from this item

					TaxEntry taxEntry = generateTaxEntry(i, t);
					taxManager.add(taxEntry);

					amountToBeHandled -= i.getAmount();
					iter.remove();

				} else {
					// Partial sell

					double remainingRatio = (i.getAmount() - amountToBeHandled)
							/ i.getAmount();

					TaxEntry taxEntry = generateTaxEntry(i, t);
					taxManager.add(taxEntry);

					i.sellPartial(remainingRatio);

					amountToBeHandled = 0.0;

					// System.out.println("SELL: " + t.getBroker() + " " +
					// getSymbol()
					// + " " + i.getAmount() + " " + t.getRate() + " " +
					// t.getDate());
				}
			}

			if (amountToBeHandled > 0.0001) {
				System.out.println("ERROR SELL: " + getSymbol()
						+ " amount not matched: " + amountToBeHandled);
			}

		} else {
			System.out.println("ERROR SELL: symbol mismatch: " + getSymbol()
					+ " " + t.getTicker());
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.stt.portfolio.transactions.I_BookEntryModifier#spinoff(double)
	 */
	@Override
	public void spinoff(double ratio, double stockratio, List<BookEntryItem> items) {
		if (items == null)
			return;
		double fractions = 0.0;
		for (BookEntryItem i : items) {
			i.spinoff(ratio);
			double newAmount = (i.getAmount() * stockratio);
			int newIntAmount = (int) newAmount;
			fractions += newAmount - newIntAmount;
			i.setAmount(newIntAmount);

		    //System.out.println("SPINOFF: " + getSymbol() + " " + ratio + " " + stockratio + " fractions: " + fractions);
		}
		double roundedFractions = Math.round(fractions * 1000000.0) / 1000000.0;
		//System.out.println("Rounded fractions: " + roundedFractions);
		if (roundedFractions >= 1.0 ) {
			items.get(0).addAmount((int)roundedFractions);
		}
	}

	/**
	 * Split.
	 *
	 * @param ratio the ratio
	 */
	public void split(double ratio) {
		double fractions = 0.0;
		for (BookEntryItem i : subEntries) {
			double newAmount = (i.getAmount() * ratio);
			int newIntAmount = (int) (newAmount + 0.00000000001);
			fractions += newAmount - newIntAmount;
			i.setAmount(newIntAmount);
			// System.out.println("SPLIT: " + getSymbol() + " " + ratio);
		}
		if (fractions >= 1.0 ) {
			subEntries.get(0).addAmount((int)fractions);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.stt.portfolio.transactions.I_BookEntryModifier#addDividend(com.stt
	 * .portfolio.transactions.Transaction)
	 */
	@Override
	public void addDividend(Transaction t) {
		lastDividendAmount = t.getAmount();
		addDividents(t.getAmount(), t.getCost() / t.getAmount() / t.getRate(),
				t.getDate(), "DIVIDEND");

		String country = bookEntryManager.getTickerManager().getCountry(t.getTicker());
		taxManager.addDivident(country, t.getDate(), t.getCost()/ t.getRate());

		addCash(t);

	}

	@Override
	public void addDividendTax(Transaction t) {
		double amount = 0.0;
		if (lastDividendAmount != 0.0) {
			amount = lastDividendAmount;

		}
		else {
			System.out.println("ERROR: Dividend tax could not be associated with dividend");
		}
		addDividents(amount, t.getCost() / amount / t.getRate(),
				t.getDate(), "DIVIDEND TAX");
		String country = bookEntryManager.getTickerManager().getCountry(t.getTicker());
		taxManager.addDividentTax(country, t.getDate(), t.getCost()/ t.getRate());

		addCash(t);
	}

	/*
	 * Subscribe share based on existing ownership
	 *
	 * Osakeanti viimeistään 31.12.2004 ----------------------------------
	 *
	 * jos osakkeet on saatu osakeannissa, jota vastaava osakepääoman korotus on
	 * merkitty kaupparekisteriin viimeistään 31.12.2004, hankintameno määräytyy
	 * seuraavasti:
	 *
	 * Osakemerkinnän perusteena olleen osakkeen hankintamenoa ei oteta huomioon
	 * merkityn osakkeen hankintamenoa laskettaessa eli ei jaeta osaksikaan
	 * merkityille uusille osakkeille.
	 *
	 * Osakemerkinnän perusteena olevan osakkeen hankintameno säilyy
	 * osakeannissa entisellään.
	 *
	 * Maksullisessa uusmerkinnässä merkityn osakkeen hankintameno on osakkeesta
	 * maksettu merkintähinta lisättynä merkintää varten mahdollisesti
	 * hankittujen lisämerkintäoikeuksien hankintamenolla.
	 *
	 * Rahastoannissa ilmaiseksi saadun osakkeen hankintameno on 0.
	 *
	 * Osakeanti 1.1.2005 tai sen jälkeen ----------------------------------
	 *
	 * Jos osakkeen perusteella merkitään tai saadaan uusia osakkeita 1.1.2005
	 * tai sen jälkeen toimeenpannussa osakeannissa, merkinnän perusteena olevan
	 * osakkeen hankintameno ja merkityn osakkeen mahdollinen hankintameno
	 * jaetaan merkinnän perusteena olevalle osakkeelle ja merkitylle
	 * osakkeelle. Tämä koskee sekä ennen 1.1.2005 että 1.1.2005 tai sen jälkeen
	 * hankittuja osakkeita.
	 */
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.stt.portfolio.transactions.I_BookEntryModifier#subscribe(com.stt.
	 * portfolio.transactions.RightsSubscription)
	 */
	@Override
	public void subscribe(RightsSubscription t) {
		if (t.getTicker().equals(getSymbol())) {
			addCash(t);
//			System.out.println("SUBSCRIBE_WITH_RIGHTS: " + t.getBroker() + " "
//					+ getSymbol() + " " + t.getAmount() + " " + t.getCost()
//					+ " " + t.getRate() + " " + t.getDate() + " based on: "
//					+ t.getAmountOfOldOwnership());

			List<BookEntryItem> newEntries = new ArrayList<BookEntryItem>();

			double amountToBeHandled = t.getAmountOfOldOwnership();
			Iterator<BookEntryItem> iter = subEntries.iterator();
			double remainder = 0.0;
			while (amountToBeHandled > DELTA && iter.hasNext()) {

				BookEntryItem i = iter.next();

				if (t.isCostShared()) {
					//System.out.println("cost shared");
					if (i.getAmount() <= amountToBeHandled + DELTA) {

						subscribeBasedOnOwnership(t, newEntries, i);
					} else {

						// Partial usage, split entry to two

						BookEntryItem splitted = splitBookEntry(i,
								amountToBeHandled);
						subscribeBasedOnOwnership(t, newEntries, splitted);
						newEntries.add(splitted);

					}

				} else {
					//System.out.println("cost not shared");
					BookEntryItem item = new BookEntryItem();
					double amount=Math.floor(i.getAmount() * t.getRatio());
					remainder += i.getAmount() * t.getRatio() - amount;
					if (remainder > 1.0) {
						remainder = remainder - 1.0;
						amount = amount + 1.0;
					}
					item.setAmount(amount );
					item.setCostInOriginalCurrency(t.getCost()
							* item.getAmount() / t.getAmount());
					item
							.setCost(item.getCostInOriginalCurrency()
									/ t.getRate());

					item.setPurchaseDate(t.getDate());
					item.setTaxPurchaseDate(i.getTaxPurchaseDate());
					item.setTaxCost(-item.getCost());
					item.setRateOnPurchaseDate(t.getRate());

					newEntries.add(item);
				}
				amountToBeHandled -= i.getAmount();
			}

			subEntries.addAll(newEntries);

//			for (BookEntryItem se : subEntries) {
//				se.print();
//			}

		} else {
			System.out.println("ERROR SUBSCRIBE_WITH_RIGHTS: symbol mismatch: "
					+ getSymbol() + " " + t.getTicker());
		}

	}

	/**
	 * Subscribe based on ownership.
	 *
	 * @param t the t
	 * @param newEntries the new entries
	 * @param i the i
	 */
	private void subscribeBasedOnOwnership(RightsSubscription t,
			List<BookEntryItem> newEntries, BookEntryItem i) {


		double amount = Math.floor(i.getAmount() * t.getRatio());
		double ratio = (amount / t.getAmount());
		BookEntryItem item = new BookEntryItem();
		item.setAmount(amount);
		item.setCostInOriginalCurrency(-t.getCost() * ratio);
		item.setCost(item.getCostInOriginalCurrency() / t.getRate());
		item.setPurchaseDate(t.getDate());
		item.setTaxPurchaseDate(i.getTaxPurchaseDate());
		item.setTaxCost(item.getAmount() * (-item.getCost() - i.getCost())
				/ (item.getAmount() + i.getAmount()));
		item.setRateOnPurchaseDate(t.getRate());

		newEntries.add(item);

		i.setTaxCost(i.getAmount() * (-item.getCost() - i.getCost())
				/ (item.getAmount() + i.getAmount()));
	}

	/**
	 * Split book entry.
	 *
	 * @param i the i
	 * @param newamount the newamount
	 *
	 * @return the book entry item
	 */
	private BookEntryItem splitBookEntry(BookEntryItem i, double newamount) {

		double amount = i.getAmount();
		double cost = i.getCost();
		double taxCost = i.getTaxCost();

		double ratio = newamount / amount;

		BookEntryItem item = new BookEntryItem();
		item.setAmount(newamount);
		item.setCost(ratio * cost);
		item.setCostInOriginalCurrency(i.getCostInOriginalCurrency() * ratio);
		item.adjustCashItems(ratio);
		item.setPurchaseDate(i.getPurchaseDate());
		item.setTaxPurchaseDate(i.getTaxPurchaseDate());
		item.setTaxCost(taxCost * ratio);
		item.setRateOnPurchaseDate(i.getRateOnPurchaseDate());

		ratio = (amount - newamount) / amount;
		i.setAmount(amount - newamount);
		i.setCost(ratio * cost);
		i.setCostInOriginalCurrency(i.getCostInOriginalCurrency() * ratio);
		i.adjustCashItems(ratio);
		i.setTaxCost(taxCost * ratio);

		return item;
	}

	/**
	 * Gets the profit.
	 *
	 * @param currentPrice the current price
	 *
	 * @return the profit
	 */
	public double getProfit(double currentPrice) {
		double profit = 0.0;
		for (BookEntryItem i : subEntries) {
			profit += i.getProfit(currentPrice);
		}
		return profit;
	}

	/**
	 * Gets the profit percent.
	 *
	 * @param currentPrice the current price
	 *
	 * @return the profit percent
	 */
	public double getProfitPercent(double currentPrice) {
		return 100.0 * (getProfit(currentPrice) / getCost());
	}

	/**
	 * Generate tax entry.
	 *
	 * @param i the i
	 * @param t the t
	 *
	 * @return the tax entry
	 */
	public TaxEntry generateTaxEntry(BookEntryItem i, Transaction t) {

		double soldAmount = Math.min(t.getAmount(), i.getAmount());

		TaxEntry e = new TaxEntry();
		e.setPurchaseDate(i.getTaxPurchaseDate());
		e.setSellDate(t.getDate());

		e.setPurchasePrice(i.getTaxCost() * soldAmount / i.getAmount());
		e.setSellPrice(((t.getCost() + t.getBrokerCost()) / t.getRate())
				* soldAmount / t.getAmount());
		e.setAmount(soldAmount);
		e.setSellCosts(t.getBrokerCost() / t.getRate()*(soldAmount/t.getAmount()));

		e.setSymbol(getSymbol());
		e.setProfit(e.getSellPrice() - e.getPurchasePrice() - e.getSellCosts());
		e.setBroker(getBroker());

		return e;
	}



	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Gets the broker.
	 *
	 * @return the broker
	 */
	public String getBroker() {
		return broker;
	}

	/**
	 * Sets the broker.
	 *
	 * @param broker the new broker
	 */
	public void setBroker(String broker) {
		this.broker = broker;
	}

	/**
	 * Gets the market price.
	 *
	 * @return the market price
	 */
	public double getMarketPrice() {
		return marketPrice;
	}

	/**
	 * Sets the market price.
	 *
	 * @param marketPrice the new market price
	 */
	public void setMarketPrice(double marketPrice) {
		this.marketPrice = marketPrice;
	}

	/**
	 * Gets the rate.
	 *
	 * @return the rate
	 */
	public double getRate() {
		return rate;
	}

	/**
	 * Sets the rate.
	 *
	 * @param rate the new rate
	 */
	public void setRate(double rate) {
		this.rate = rate;
	}

	/**
	 * Gets the quote date.
	 *
	 * @return the quote date
	 */
	public Date getQuoteDate() {
		return quoteDate;
	}

	/**
	 * Sets the quote date.
	 *
	 * @param quoteDate the new quote date
	 */
	public void setQuoteDate(Date quoteDate) {
		this.quoteDate = quoteDate;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.stt.portfolio.transactions.I_BookEntryModifier#addCash(double)
	 */
	@Override
	public void addCash(Transaction t) {
		cashManager.addCash(t.getCost() / t.getRate(), t.getDate());

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.stt.portfolio.transactions.I_BookEntryModifier#removeCapital(com.
	 * stt.portfolio.transactions.Transaction)
	 */
	@Override
	public void removeCapital(Transaction t) {
		cashManager.changeInvestedCapital(t.getCost() / t.getRate(), t
				.getDate());

	}







	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * com.stt.portfolio.transactions.I_BookEntryModifier#addCapitalRepayment
	 * (com.stt.portfolio.transactions.Transaction)
	 */
	@Override
	public void addCapitalRepayment(Transaction t) {

		double amountToBeHandled = t.getAmount();
		//while (amountToBeHandled > 0.00001) {

			for (BookEntryItem e : subEntries) {

				double a = Math.min(e.getAmount(), amountToBeHandled);

				// System.out.println("Reducing tax price: " + e.getTaxCost()
				// + " " + t.getPrice() * a);
				e.setTaxCost(e.getTaxCost() - t.getPrice() * a);
				// System.out.println("Reduced tax price: " + e.getTaxCost());

				amountToBeHandled -= a;
			}

		//}
		addCash(t);
		addDividents(t.getAmount(), t.getCost() / t.getAmount() / t.getRate(),
				t.getDate(), "CAPITAL REPAYMENT");
	}

	/* (non-Javadoc)
	 * @see com.stt.portfolio.transactions.I_BookEntryModifier#optionSubscribe(com.stt.portfolio.transactions.OptionSubscription)
	 */
	@Override
	public void optionSubscribe(OptionSubscription t) {

		BookEntry options = bookEntryManager.getBookEntry(t.getOptionTicker());
		if (options != null) {
			addCash(t);

			double amountToBeHandled = t.getAmount() * t.getSubscriptionRatio();
			double oddlots = 0.0;
			Iterator<BookEntryItem> iter = options.subEntries.iterator();
			while (amountToBeHandled > DELTA && iter.hasNext()) {

				BookEntryItem i = iter.next();
				double itemAmount = i.getAmount() + oddlots;
				if (itemAmount  <= amountToBeHandled + DELTA) {
					double amount = Math.floor(itemAmount / t.getSubscriptionRatio());
					if (i.amount - amount > DELTA) {
						oddlots += i.amount - amount* t.getSubscriptionRatio();

					}

					subscribeOption(t, i, amount);
					amountToBeHandled -= amount;
					iter.remove();

				} else {
					// Partial subscription

					double remainingRatio = (i.getAmount() - amountToBeHandled)
							/ i.getAmount();

					subscribeOption(t, i, amountToBeHandled/t.getSubscriptionRatio());
					i.sellPartial(remainingRatio);

					amountToBeHandled = 0.0;

				}
			}
		}
	//printItems();
	}



	/**
	 * Subscribe option.
	 *
	 * @param t the subscription transaction
	 * @param i the bookentryitem of the options
	 * @param amount the amount of new shares
	 */
	private void subscribeOption(OptionSubscription t, BookEntryItem i, double amount) {

		//i.print();

		BookEntryItem item = new BookEntryItem();
		item.setAmount(amount);
		item.setCost(amount/t.getAmount()*t.getCost() / t.getRate() + i.getCost());
		item.setPurchaseDate(t.getDate());
		item.setTaxPurchaseDate(i.getTaxPurchaseDate());
		item.setTaxCost(amount/t.getAmount()*-t.getCost() / t.getRate() + i.getTaxCost());
		item.setRateOnPurchaseDate(t.getRate());
		item.setCostInOriginalCurrency(amount/t.getAmount()*t.getCost() + i.getCostInOriginalCurrency());

		//System.out.println("Adding " + getSymbol() + " " + item.getAmount());
		subEntries.add(item);

	}

	@SuppressWarnings("unused")
	private void printItems() {
		//System.out.println(getSymbol() + " items");
		final Iterator<BookEntryItem> iter = subEntries.iterator();
		while (iter.hasNext()) {

			final BookEntryItem i = iter.next();
			i.print();
		}

	}


	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public boolean isOption() {
		return isOption;
	}

	public void setOption(boolean isOption) {
		this.isOption = isOption;
	}

	public Object[] getChildren() {
		return subEntries.toArray();
	}

	public int getChildrenCount() {
		return subEntries.size();
	}

	public Iterator<BookEntryItem> getChildrenIterator() {
		return subEntries.iterator();
	}

	public BookEntryManager getBookEntryManager() {
		return bookEntryManager;
	}

	public void setBookEntryManager(BookEntryManager bookEntryManager) {
		this.bookEntryManager = bookEntryManager;
	}

}
