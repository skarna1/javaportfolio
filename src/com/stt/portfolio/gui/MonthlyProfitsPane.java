package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.stt.portfolio.Portfolio;
import com.stt.portfolio.PortfolioFactory;
import com.stt.portfolio.TickerManager;

public class MonthlyProfitsPane extends JPanel  implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4279071363677616980L;

	private JTable table = new TaxReportTable(null);

	JScrollPane scrollPane = null;
	private JPanel top = null;
	
	private Portfolio portfolio = null;
	
	private String selectedYear;
	
	public final String[] columnNames = { "Kuukausi", "Arvo alussa",
			"Arvo lopussa", "Muutos", " Muutos %" };

	public MonthlyProfitsPane(Portfolio portfolio) {
		super(new BorderLayout());

		
		this.portfolio = portfolio;
	
		init();
	}

	public void init() {
		if (scrollPane != null) {
			remove(scrollPane);
		}

		// years
		String[] years = portfolio.getRawTransactionYears(null);
		JComboBox yearList = new JComboBox(years);
		yearList.setActionCommand("year");
				
		table.setPreferredScrollableViewportSize(new Dimension(1000, 500));
		table.setFillsViewportHeight(true);
		table.setAutoCreateRowSorter(true);

		FlowLayout flowLayout = new FlowLayout();

		top = new JPanel(flowLayout);
		top.add(new JLabel("Vuosi: "));
		top.add(yearList);
		add(top, BorderLayout.PAGE_START);


		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);

		if (years.length > 0) {

			selectedYear = years[years.length - 1];
			yearList.setSelectedIndex(years.length - 1);
			
			update(selectedYear);
		}
		yearList.addActionListener(this);
	}

	private void update(String selectedYear) {
		int year = Integer.parseInt(selectedYear);
		table.setModel(new PortfolioTableModel(getTable(portfolio.getName(), year),
				columnNames));

	}

	private Object[][] getTable(String portfolioName, int year) {

		Calendar current = Calendar.getInstance();
		Calendar c = Calendar.getInstance();
		
		
		int nbrofmonths;
		if (year == current.get(Calendar.YEAR)) {
			nbrofmonths = Math.min(12, current.get(Calendar.MONTH));
		}
		else {
			nbrofmonths = 12;
		}
		Object[][] table = new Object[nbrofmonths][columnNames.length];
		int row = 0;
		Portfolio portfolio = PortfolioFactory.createPortfolio(portfolioName,
				current.getTime(), TickerManager
						.createTickerManager());
		
		for (int month = 0; month < nbrofmonths; ++month) {
			try {

				c.set(Calendar.YEAR, year);
				c.set(Calendar.MONTH, month);
				c.set(Calendar.DATE, 1);
				Date startDate = c.getTime();
				
				portfolio.process(startDate);
				portfolio.getCombinedBookEntryTable(false);
				double value1 = portfolio.getPortfolioValue();

				
				c.set(Calendar.DAY_OF_MONTH,
		                c.getActualMaximum(Calendar.DAY_OF_MONTH));
				
				Date endDate = null;
								
				if (c.get(Calendar.YEAR) != Calendar.getInstance().get(
						Calendar.YEAR) || c.get(Calendar.MONTH) != Calendar.getInstance().get(
								Calendar.MONTH)) {
					endDate = c.getTime();
				} else {
					endDate = (Calendar.getInstance().getTime());
				}
				
				portfolio.process(endDate);
				portfolio.getCombinedBookEntryTable(false);
				double value2 = portfolio.getPortfolioValue();

				int col = 0;
				table[row][col++] = c.getDisplayName(Calendar.MONTH, Calendar.LONG, getLocale());
				table[row][col++] = value1;
				table[row][col++] = value2;
				table[row][col++] = value2 - value1;
			    table[row][col++] = (value2 - value1)/value1*100.0;
				//table[row][col++] = portfolio.getXirr(value1, startDate);
				row++;
			} catch (NumberFormatException e) {

			}
		}
		return table;

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();
		if (e.getActionCommand().equals("year")) {
			
			selectedYear = (String) cb.getSelectedItem();
			
		}
		
		update(selectedYear);
	}


}
