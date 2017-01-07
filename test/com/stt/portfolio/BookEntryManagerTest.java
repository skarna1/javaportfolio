package com.stt.portfolio;

import static org.junit.Assert.*;

import java.util.Calendar;

import org.junit.Before;
import org.junit.Test;

import com.stt.portfolio.transactions.Buy;
import com.stt.portfolio.transactions.ChangeTransaction;
import com.stt.portfolio.transactions.Demerge;
import com.stt.portfolio.transactions.Demergee;
import com.stt.portfolio.transactions.Transaction;

public class BookEntryManagerTest {
	BookEntryManager m = null;
	Calendar c = Calendar.getInstance();
	

	@Before
	public void setUp() throws Exception {
		I_TickerManager tm = new TickerManagerMock();
		CashManager c = new CashManager();
		TaxManager t = new TaxManager();
		m = new BookEntryManager(tm, c, t);
		
	}

	@Test
	public void testSplit() {
		m.createBookEntry("SON1V");
		BookEntry be = m.getBookEntry("SON1V");
		Transaction t = createBuy("SON1V", 50.0, 50.0);
		be.buy(t);
		t = createBuy("SON1V", 25.0, 50.0);
		be.buy(t);
		m.split("SON1V", 1.5144, c.getTime());
		
		double amount = be.getAmount();
		//System.out.println(amount);
		assertEquals(113.0, amount, 0.01);
	}
	
	@Test
	public void testSpinoff() {
		m.createBookEntry("MEO1V");
		BookEntry be = m.getBookEntry("MEO1V");
		Transaction buy = createBuy("MEO1V", 100.0, 30.0);
		be.buy(buy);
		Demerge t = new Demerge();
		Demergee a = new Demergee();
		a.setRatio(0.3);
		a.setSymbol("VALMT");
		t.addDemergee(a);
		Demergee b = new Demergee();
		b.setRatio(0.7);
		b.setSymbol("MEO1V");
		t.addDemergee(b);
	    t.setSymbol("MEO1V");
		t.process(m);
		
		BookEntry bv = m.getBookEntry("VALMT");
		be = m.getBookEntry("MEO1V");
	
		assertNotEquals(null, bv);
		bv.print();
		assertEquals(100.0, bv.getAmount(),  0.01);
		assertEquals(30*0.3, bv.getCost()/be.getAmount(),  0.01);
		be.print();
		
		assertEquals(100.0, be.getAmount(),  0.01);
		assertEquals(30*0.7, be.getCost()/be.getAmount(),  0.01);
	}
	
	private Transaction createBuy(String symbol, double amount, double price) {
		Transaction t = new Buy();
		t.setTicker(symbol);
		t.setPrice(price);
		t.setAmount(amount);
		t.setCost(-amount * t.getPrice());
		t.setDate(Calendar.getInstance().getTime());
		t.setRate(1.0);
		return t;
	}
}
