package com.stt.portfolio;


import java.util.Collection;
import java.util.Date;


import com.stt.portfolio.quotes.QuoteManager;
import com.stt.portfolio.transactions.ChangeTransaction;
import com.stt.portfolio.transactions.Transaction;

public class Account {

	private String broker;
	private String accountNumber;
	private BookEntryManager bookEntryManager;

	
	public Account(String broker, BookEntryManager bookEntryManager) {
		this.broker = broker;	
		this.bookEntryManager = bookEntryManager;
		this.bookEntryManager.setBroker(broker);
	}
	
	public void add(Account a) {
		a.addBookEntries(bookEntryManager);
	}

	public BookEntryManager getBookEntryManager() {
		return bookEntryManager;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public void generateBookEntries() {
		bookEntryManager.removeEmptyBookEntries();
	}
	
	public void clear() {
		bookEntryManager.clear();
	}

	public void print() {
		System.out.println("\nBroker: " + getBroker());
		bookEntryManager.printEntries();
	}

	public void addBookEntries(BookEntryManager b) {
		Collection<BookEntry> entries = bookEntryManager.getBookEntryCollection();
		for (BookEntry e : entries) {
			b.addEntry(e);
		}
	}
	
	public Collection<BookEntry> getBookEntries() {
		return bookEntryManager.getBookEntryCollection();
	}

	public Object[][] getBookEntryTable(QuoteManager quoteManager, Date date, boolean showPartial) {
		return bookEntryManager.getBookEntryTable(quoteManager, date, showPartial);
	}
	
	public void process(Transaction t) {
		BookEntry entry = bookEntryManager.createBookEntry(t.getTicker());

		t.process(entry);
	}
	
	public void process(ChangeTransaction ct) {
		ct.process(bookEntryManager);
	}
}
