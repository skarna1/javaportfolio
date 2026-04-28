package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.stt.portfolio.Portfolio;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TransactionPane extends JPanel implements ActionListener {

	public final String[] columnNames = { "Päiväys", "Tapahtumatyyppi",
			"Arvopaperi", "Nimi", "Määrä", "Kurssi", "Kulut", "Valuuttakurssi",
			"AO-tili", "yhteensä", "Yhteensä EUR" };

	private static final String[] transactionNames = {"Kaikki", "Osto", "merk.oikeus","Myynti", "Osinko","Vero", "Osinkovero","Siirto" , "Pääoman palautus"};
	private JPanel top = null;
	private JScrollPane scrollPane = null;
	private JTable table = new TransactionTable(null);
	private Portfolio portfolio = null;
	private java.util.List<com.stt.portfolio.transactions.Transaction> filteredTransactions = new java.util.ArrayList<>();
	private String selectedYear;
	private String selectedTransaction;
	private BrokerList brokerList = null;
	private String selectedName = "Kaikki";
	private Object[] names;
	private JComboBox transactionStocksList;
	
	public TransactionPane(com.stt.portfolio.PortfolioDocument portfolioDoc, Portfolio portfolio) {
		super(new BorderLayout());

		this.portfolio = portfolio;

		// Stocks
		names = portfolio.getTransactionTickers("kaikki","kaikki"); 
		
		transactionStocksList = new JComboBox(names);
		transactionStocksList.setActionCommand("name");
		
		
		// years
		String[] years = portfolio.getTransactionYears(null);
		JComboBox yearList = new JComboBox(years);
		yearList.setActionCommand("year");


		// Brokers
		Object[] brokerNames = portfolio.getBrokers();
		brokerList = new BrokerList(brokerNames, this);
		
		// Transaction types
		JComboBox transactionNameList = new JComboBox(transactionNames);
		transactionNameList.setActionCommand("transaction");
		transactionNameList.setSelectedIndex(0);
		selectedTransaction = transactionNames[0];
		

		
		// Lay out

		FlowLayout flowLayout = new FlowLayout();

		top = new JPanel(flowLayout);
		top.add(new JLabel("Vuosi: "));
		top.add(yearList);

		top.add(new JLabel("AO-tili: "));
		top.add(brokerList.getBrokerList());

		top.add(new JLabel("Tapahtuma: "));
		top.add(transactionNameList);
		
		top.add(new JLabel("Nimi: "));
		top.add(transactionStocksList);
		
		add(top, BorderLayout.PAGE_START);

		// Create the scroll pane and add the table to it.
		scrollPane = new JScrollPane(table);

		// Add the scroll pane to this panel.
		add(scrollPane);

		
		
		if (years.length > 0) {

			selectedYear = years[years.length - 1];
			yearList.setSelectedIndex(years.length - 1);
			
			update();
		}
		yearList.addActionListener(this);
		transactionNameList.addActionListener(this);
		transactionStocksList.addActionListener(this);

		// Add popup menu for editing a transaction on right-click
		JPopupMenu popup = new JPopupMenu();
		JMenuItem editItem = new JMenuItem(com.stt.portfolio.gui.MenuCreator.MENU_ITEM_EDIT_TRANSACTION);
		popup.add(editItem);

		editItem.addActionListener(ae -> {
			int viewRow = table.getSelectedRow();
			if (viewRow >= 0) {
				int modelRow = table.convertRowIndexToModel(viewRow);
				if (modelRow >= 0 && modelRow < filteredTransactions.size()) {
					com.stt.portfolio.transactions.Transaction t = filteredTransactions.get(modelRow);
					if (portfolioDoc != null) {
						portfolioDoc.handleEditTransaction(t);
					}
				}
			}
		});

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				maybeShowPopup(e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopup(e);
			}

			private void maybeShowPopup(MouseEvent e) {
				if (e.isPopupTrigger()) {
					int viewRow = table.rowAtPoint(e.getPoint());
					if (viewRow >= 0) {
						table.setRowSelectionInterval(viewRow, viewRow);
					}
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox cb = (JComboBox) e.getSource();
		if (e.getActionCommand().equals("year")) {
			
			selectedYear = (String) cb.getSelectedItem();
			
		}
		else if (e.getActionCommand().equals("broker")) {
		
			brokerList.updateSelectedBroker();
			updateStocks(brokerList.getSelectedBroker(), selectedTransaction);
			 
			
		}
		else if (e.getActionCommand().equals("transaction")) {
			selectedTransaction = (String) cb.getSelectedItem();
			updateStocks(brokerList.getSelectedBroker(), selectedTransaction);
		}
		else if (e.getActionCommand().equals("name")) {
			selectedName = (String) cb.getSelectedItem();
		}
		update();
	}

	
	void updateStocks(String broker, String transactionType) {
		transactionStocksList.removeActionListener(this);
		names = portfolio.getTransactionTickers(broker,transactionType);
		//System.out.println("size: " + names.length);
		transactionStocksList.removeAllItems();
		for (Object o : names) {
			transactionStocksList.addItem(o);
		}
		transactionStocksList.setSelectedIndex(0);
		selectedName = (String) transactionStocksList.getSelectedItem();
		
		//System.out.println("name: " + selectedName);
		transactionStocksList.addActionListener(this);
	}
	
	public void update() {
		//System.out.println("transactionpane::update name: " + selectedName);
		Object[][] transactions = portfolio.getTransactionTable(selectedYear,
			selectedTransaction,
			brokerList.getSelectedBroker(),
			selectedName);

		// also keep list of Transaction objects corresponding to table rows
		filteredTransactions = portfolio.getFilteredTransactions(selectedYear, selectedTransaction, brokerList.getSelectedBroker(), selectedName);

		table.setModel(new PortfolioTableModel(transactions, columnNames));

	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		update();
	}
}
