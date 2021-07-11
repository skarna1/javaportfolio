package com.stt.portfolio;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class AccountTest {

	Account account;

	@Before
	public void setUp() throws Exception {
		account = new Account("NON", new BookEntryManager(null, null, null));
	}

	@Test
	public void testSetBroker() {
		account.setBroker("nordnet");
		assertEquals("nordnet", account.getBroker());
	}

	@Test
	public void testSetAccountNumber() {
		account.setAccountNumber("1234567890");
		assertEquals("1234567890", account.getAccountNumber());
	}

}
