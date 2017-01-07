package com.stt.portfolio;

import java.util.List;
import java.util.Map;

public class TickerManagerMock implements I_TickerManager {

	@Override
	public Object[] getCcys() {
		Object[] ccys = {"EUR", "USD" };
		return ccys;
	}

	@Override
	public String getCountry(String ticker) {
		
		return "EUR";
	}

	@Override
	public int getDecimals(String ticker) {
		
		return 2;
	}

	@Override
	public String getName(String ticker) {
		
		return "foo";
	}

	@Override
	public Object[] getSectors() {
		Object[] sectors = {"kulutustavarat", "metsä", "rahoitus"};
		return sectors;
	}

	@Override
	public Stock getStock(String ticker) {
		Stock s = new Stock();
		s.setName("foo");
		s.setTicker(ticker);
		s.setCcy("EUR");
		s.setType("S");
		return s;
	}

	@Override
	public Map<String, List<String>> getStocksBySector() {
		
		return null;
	}

	@Override
	public String getTicker(String name) {
		
		return "SON1V";
	}

	@Override
	public void init() {
		

	}

}
