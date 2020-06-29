package com.stt.portfolio;

import java.util.List;
import java.util.Map;


/**
 * The Interface I_TickerManager.
 */
public interface I_TickerManager {

	/**
	 * Gets the stock.
	 * 
	 * @param ticker the ticker
	 * 
	 * @return the stock
	 */
	public abstract Stock getStock(String ticker);

	/**
	 * Gets the sectors.
	 * 
	 * @return the sectors
	 */
	public abstract String[] getSectors();

	/**
	 * Gets the stocks by sector.
	 * 
	 * @return the stocks by sector
	 */
	public abstract Map<String, List<String>> getStocksBySector();

	/**
	 * Gets the ticker.
	 * 
	 * @param name the name
	 * 
	 * @return the ticker
	 */
	public abstract String getTicker(String name);

	/**
	 * Gets the name.
	 * 
	 * @param ticker the ticker
	 * 
	 * @return the name
	 */
	public abstract String getName(String ticker);

	/**
	 * Inits the.
	 */
	public abstract void init();

	/**
	 * Gets the ccys.
	 * 
	 * @return the ccys
	 */
	public abstract Object[] getCcys();

	/**
	 * Gets the decimals. Mutual funds are handled with 5 decimals, stocks with 2.
	 * 
	 * @param ticker the ticker
	 * 
	 * @return the decimals
	 */
	public abstract int getDecimals(String ticker);

	/**
	 * Gets the country.
	 * 
	 * @param ticker the ticker
	 * 
	 * @return the country
	 */
	public abstract String getCountry(String ticker);

}