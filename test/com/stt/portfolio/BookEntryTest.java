package com.stt.portfolio;


import static org.junit.Assert.*;

import java.util.Calendar;


import org.junit.Before;
import org.junit.Test;

import com.stt.portfolio.transactions.Buy;
import com.stt.portfolio.transactions.Sell;
import com.stt.portfolio.transactions.Transaction;

public class BookEntryTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testAddTransactionBuy() {

		Transaction t = createBuy("KO", 100, 42.51);
		CashManager c = new CashManager();
		BookEntry e = new BookEntry(t.getTicker(), t.getName(), "EUR", "NON", c, null, null);
		e.buy(t);
	
		
		double epsilon = 0.00001;
		assertEquals(100.0, e.getAmount(), epsilon);
		assertEquals(4251.0 + 13.0, e.getCost(), epsilon);
		assertEquals("KO", e.getSymbol());
		assertEquals(-t.getCost()/t.getAmount(), e.getPrice(), epsilon);
		assertEquals(t.getDate(), e.getPurchaseDate());
	
	}
	
	@Test
	public void testAddTransactionBuySell() {

		Transaction t1 = createBuy("KO", 100, 42.51);
		Transaction t2 = createSell("KO", 100, 45.51);
		CashManager c = new CashManager();
		BookEntry e = new BookEntry(t1.getTicker(), t1.getName(), "USD","NON", c, null, new TaxManager());
		e.buy(t1);
		e.sell(t2);
		
		double epsilon = 0.00001;
		assertEquals(0.0, e.getAmount(), epsilon);
		assertEquals(0.0, e.getCost(), epsilon);
		assertEquals("KO", e.getSymbol());
		assertEquals(0.0, e.getPrice(), epsilon);
		assertEquals(null, e.getPurchaseDate());
	
	}
	
	@Test
	public void testAddTransactionBuySellHalf() {

		Transaction t1 = createBuy("KO", 100, 42.51);
		Transaction t2 = createSell("KO", 50, 45.51);
		CashManager c = new CashManager();
		BookEntry e = new BookEntry(t1.getTicker(), t1.getName(), "USD", "Nordnet", c, null, new TaxManager());
		e.buy(t1);
		e.sell(t2);
		
		
		double epsilon = 0.00001;
		assertEquals(50.0, e.getAmount(), epsilon);
		assertEquals((4251+13)/2.0, e.getCost(), epsilon);
		assertEquals("KO", e.getSymbol());
		assertEquals((4251+13)/100.0, e.getPrice(), epsilon);
		assertEquals(t1.getDate(), e.getPurchaseDate());
	
	}

	private Transaction createBuy(String symbol, double amount, double price) {
		Transaction t = new Buy();
		t.setTicker(symbol);
		t.setPrice(price);
		t.setAmount(amount);
		t.setCost(-amount * t.getPrice() - 13);
		t.setDate(Calendar.getInstance().getTime());
		t.setRate(1.0);
		return t;
	}
	
	private Transaction createSell(String symbol, double amount, double price) {
		Transaction t = new Sell();
		t.setTicker(symbol);
		t.setPrice(price);
		t.setAmount(amount);
		t.setCost(amount * t.getPrice() + 13);
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, 20);		
		t.setDate(c.getTime());
		t.setRate(1.0);
		return t;
	}
}
