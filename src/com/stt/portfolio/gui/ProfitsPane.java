package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.stt.portfolio.Portfolio;
import com.stt.portfolio.PortfolioFactory;
import com.stt.portfolio.TickerManager;

public class ProfitsPane extends JPanel {

	private static final long serialVersionUID = -4279071363677616980L;
	private JTable table;
	private JScrollPane scrollPane;
	private Portfolio portfolio;

	public final String[] columnNames = { "Vuosi", "Arvo alussa",
			"Arvo lopussa", "Muutos", "P/A%" };

	public ProfitsPane(Portfolio portfolio) {
		super(new BorderLayout());

		this.portfolio = portfolio;
		
		init();
	}

	public void init() {
		if (scrollPane != null) {
			remove(scrollPane);
		}
		table = new TaxReportTable(null);
		table.setPreferredScrollableViewportSize(new Dimension(1000, 500));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);
		table.setModel(new PortfolioTableModel(getTable(portfolio.getName()),
				columnNames));

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);

	}

	private Object[][] getTable(String portfolioName) {

		Portfolio portfolioCurrent = PortfolioFactory.createPortfolio(
				portfolioName, Calendar.getInstance().getTime(), TickerManager
						.createTickerManager());

		String[] years = portfolioCurrent.getTransactionYears(null);
		Object[][] table = new Object[years.length - 1][columnNames.length];
		int row = 0;
		Portfolio portfolio = PortfolioFactory.createPortfolio(portfolioName,
				Calendar.getInstance().getTime(), TickerManager
						.createTickerManager());
		

		boolean first = true;
		for (String year : years) {
			try {

				Date startDate = getYearStartDate(year, first);
				
				double value1 = portfolio.getPortfolioValue(startDate);

				Date endDate = getYearEndDate(year);
				
				double value2 = portfolio.getPortfolioValue(endDate);

				int col = 0;
				table[row][col++] = year;
				table[row][col++] = value1;
				table[row][col++] = value2;
				table[row][col++] = value2 - value1;
				// table[row][col++] = (value2 - value1)/value1*100.0;
				table[row][col++] = portfolio.getXirr(value1, startDate);
				row++;
				first = false;
			} catch (NumberFormatException e) {

			}
		}
		return table;

	}


	
	public Date getYearStartDate(String year, boolean first)
	{
		Calendar c = Calendar.getInstance();
		if (first) {
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, 0);
		c.set(Calendar.DATE, 1);
		}
		else {
			c.set(Calendar.YEAR, Integer.parseInt(year)-1);
			c.set(Calendar.MONTH, 11);
			c.set(Calendar.DAY_OF_MONTH,
	                c.getActualMaximum(Calendar.DAY_OF_MONTH));
			
		}
		
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY){
			 c.add(Calendar.DATE, 2);
		}
		else if (dayOfWeek == Calendar.SUNDAY){
			c.add(Calendar.DATE, 1);
		}
		
		Date date = c.getTime();
		
		
		return date;
	}
	
	public Date getYearEndDate(String year)
	{
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, Integer.parseInt(year));
		c.set(Calendar.MONTH, 11);
		c.set(Calendar.DAY_OF_MONTH,
                c.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = null;
						
		if (c.get(Calendar.YEAR) != Calendar.getInstance().get(
				Calendar.YEAR)) {
			endDate = c.getTime();
		} else {
			endDate = (Calendar.getInstance().getTime());
		}
		
		int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
		if (dayOfWeek == Calendar.SATURDAY){
			 c.add(Calendar.DATE, -1);
		}
		else if (dayOfWeek == Calendar.SUNDAY){
			c.add(Calendar.DATE, -2);
		}
		
		return endDate;
	}
}
