package com.stt.portfolio;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.stt.portfolio.quotes.QuoteManagerFactory;
import com.stt.portfolio.quotes.portfoliofiles.FileQuoteManagerFactory;

public class PortfolioTest {

	Portfolio portfolio;

	@Before
	public void setUp() throws Exception {
		QuoteManagerFactory f = new FileQuoteManagerFactory();
		portfolio = new Portfolio("", f, null);
	}

	@Test
	public void testAddAccount() {
		Account account = new Account("NON", new BookEntryManager(null, null, null));
		List<Account> accounts = portfolio.getAccounts();
		assertEquals(0, accounts.size());

		portfolio.addAccount(account);

		assertEquals(1, accounts.size());
	}

	@Test
	public void testCreateAccount() {
		List<Account> accounts = portfolio.getAccounts();
		assertEquals(0, accounts.size());

		portfolio.createAccount("nordnet");

		assertEquals(1, accounts.size());
	}

	@Test
	public void testGetAccount() {
		portfolio.createAccount("nordnet");
		Account a = portfolio.getAccount("nordnet");
		assertNotNull(a);
		assertEquals("nordnet", a.getBroker());
	}

	@Test
	public void testGetAccountNull() {
		portfolio.createAccount("nordnet");
		Account a = portfolio.getAccount("eq");
		assertNull(a);

	}
}
