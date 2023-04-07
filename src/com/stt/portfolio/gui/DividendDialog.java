package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.stt.portfolio.BookEntry;
import com.stt.portfolio.I_TickerManager;
import com.stt.portfolio.Stock;

public class DividendDialog extends BaseDialog {

	private static String SECTOR_SELECTED = "sector";
	private static String STOCK_SELECTED = "stock";
	private static String BROKER_SELECTED = "broker";
	private static String TAX_SELECTED = "tax";

	private JTextField totalCostField = new JTextField(FIELD_LEN);
	private JTextField netCostField = new JTextField(FIELD_LEN);
	private JTextField amountField = new JTextField(FIELD_LEN);
	private JTextField costField = new JTextField(FIELD_LEN);
	private JTextField dividendTaxField = new JTextField(FIELD_LEN);

	private ButtonGroup taxButtons = new ButtonGroup();
	Box buttonBox;
	JRadioButton tax0 = new JRadioButton("0 %");
	JRadioButton tax15 = new JRadioButton("15 %");
	JRadioButton tax255 = new JRadioButton("25,5%");

	JComboBox<String> brokerList;
	JComboBox<String> sectorList;
	JComboBox<String> stocksList;

	JLabel totalCostFieldLabel;
	JLabel netCostFieldLabel;
	JLabel brokerFieldLabel;
	JLabel costFieldLabel;
	JLabel amountFieldLabel;
	JLabel dateFieldLabel;
	JLabel stockFieldLabel;
	JLabel sectorFieldLabel;
	JLabel dividendTaxLabel;
	private JLabel localCurrencyButtonLabel;

	private Map<String, List<String>> stocks = null;
	private I_TickerManager tickerManager = null;
	private Map<String, Collection<BookEntry>> bookEntries;

	@FunctionalInterface
	public interface SimpleDocumentListener extends DocumentListener {
		void update(@SuppressWarnings("exports") DocumentEvent e);

		@Override
		default void insertUpdate(@SuppressWarnings("exports") DocumentEvent e) {
			update(e);
		}

		@Override
		default void removeUpdate(@SuppressWarnings("exports") DocumentEvent e) {
			update(e);
		}

		@Override
		default void changedUpdate(@SuppressWarnings("exports") DocumentEvent e) {
			update(e);
		}
	}

	public String getStockName() {
		return String.valueOf(stocksList.getSelectedItem());
	}

	public double getAmount() {
		return convertToDouble(amountField.getText());
	}

	public double getCost() {
		return convertToDouble(costField.getText());
	}

	public String getBrokerName() {
		return (String) brokerList.getSelectedItem();
	}

	public double getTotalCost() {
		return convertToDouble(totalCostField.getText());
	}

	public double getDividendTax() {
		return convertToDouble(dividendTaxField.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (SECTOR_SELECTED.equals(e.getActionCommand())) {
			String sector = (String) sectorList.getSelectedItem();
			updateStockList(sector);
			String stockName = (String) stocksList.getSelectedItem();
			updateForeignCurrency(stockName);
		} else if (STOCK_SELECTED.equals(e.getActionCommand())) {
			String stockName = (String) stocksList.getSelectedItem();
			updateBrokers(stockName);
			updateForeignCurrency(stockName);
		}
		else if (BROKER_SELECTED.equals(e.getActionCommand())) {
			String broker = getBrokerName();
			String stockName = (String) stocksList.getSelectedItem();
			updateForeignCurrency(stockName);
			updateOwnedAmount(broker, stockName);
		}
		else if (TAX_SELECTED.equals(e.getActionCommand())) {
			updateTax();
		}
		else {
			super.actionPerformed(e);
		}

	}

	private void updateOwnedAmount(String broker, String stockName) {

		Collection<BookEntry> entries = bookEntries.getOrDefault(broker, List.of());

		Optional<BookEntry> entry = entries.stream().filter(e -> e.getName().equals(stockName)).findFirst();
		if (entry.isPresent()) {
			amountField.setText(String.format("%1$.2f", entry.get().getAmount()));
		}
	}
	/**
	 * Set up and show the dialog. The first Component argument determines which
	 * frame the dialog depends on; it should be a component in the dialog's
	 * controlling frame. The second Component argument should be null if you
	 * want the dialog to come up with its left corner in the center of the
	 * screen; otherwise, it should be the component on top of which the dialog
	 * should appear.
	 */

	public DividendDialog(Component frameComp, Component locationComp,
			String title, String[] sectors,
			Map<String, List<String>> stocks, Map<String, Collection<BookEntry>> bookEntries,
			I_TickerManager tickerManager, Stock s) {
		super(frameComp, locationComp, title, tickerManager);

		this.stocks = stocks;
		this.bookEntries = bookEntries;

		// Brokers

		brokerList = new JComboBox<String>();
		brokerList.setEditable(true);
		brokerList.setActionCommand(BROKER_SELECTED);
		brokerList.addActionListener(this);

		// Sectors
		sectorList = new JComboBox<String>(sectors);
		sectorList.setActionCommand(SECTOR_SELECTED);
		sectorList.addActionListener(this);

		// Stocks
		stocksList = new JComboBox<String>();
		updateStockList((String) sectorList.getSelectedItem());
		stocksList.setActionCommand(STOCK_SELECTED);
		stocksList.addActionListener(this);

		// tax percents
		taxButtons.add(tax0);
		taxButtons.add(tax15);
		taxButtons.add(tax255);
		buttonBox = Box.createHorizontalBox();
		buttonBox.add(tax0);
		buttonBox.add(tax15);
		buttonBox.add(tax255);
		tax255.setSelected(true);
		tax0.setActionCommand(TAX_SELECTED);
		tax15.setActionCommand(TAX_SELECTED);
		tax255.setActionCommand(TAX_SELECTED);
		tax0.addActionListener(this);
		tax15.addActionListener(this);
		tax255.addActionListener(this);

		amountField.addKeyListener(this);
		costField.addKeyListener(this);
		dividendTaxField.addKeyListener(this);

		amountField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			updateTax();
		});

		costField.getDocument().addDocumentListener((SimpleDocumentListener) e -> {
			updateTax();
		});

		totalCostField.setEditable(false);
		totalCostField.setText("0.00");

		netCostField.setEditable(false);
		netCostField.setText("0.00");

		sectorFieldLabel = new JLabel("Toimiala: ");
		sectorFieldLabel.setLabelFor(sectorList);

		stockFieldLabel = new JLabel("Arvopaperi: ");
		stockFieldLabel.setLabelFor(stocksList);

		dateFieldLabel = new JLabel("Maksupäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);

		localCurrencyButtonLabel = new JLabel("Valuutta: ");
		localCurrencyButtonLabel.setLabelFor(currencyList);

		brokerFieldLabel = new JLabel("Välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);

		amountFieldLabel = new JLabel("Määrä: ");
		amountFieldLabel.setLabelFor(amountField);

		costFieldLabel = new JLabel("Osinko/osake: ");
		costFieldLabel.setLabelFor(costField);

		dividendTaxLabel = new JLabel("Ennakonpidätys: ");
		dividendTaxLabel.setLabelFor(dividendTaxField);
		dividendTaxField.setText("0.00");

		totalCostFieldLabel = new JLabel("Yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		netCostFieldLabel = new JLabel("Netto: ");
		netCostFieldLabel.setLabelFor(totalCostField);

		setSelection(s);
		String stockName = String.valueOf(stocksList.getSelectedItem());

		updateForeignCurrency(stockName);
		init(getDialogLabels(), getDialogComponents());
	}

	protected void setSelection(Stock s) {

		if (s!= null) {
			sectorList.setSelectedItem(s.getSector());
			stocksList.setSelectedItem(s.getName());
			// Brokers
			updateBrokers(s.getName());
		}
	}

	private void updateBrokers(String stockName) {
		List<String> brokers = bookEntries.keySet().stream().filter(broker -> hasBrokerStock(broker, stockName))
				.collect(Collectors.toList());
		brokerList.removeAllItems();
		for (String broker : brokers) {
			brokerList.addItem(broker);
		}
	}

	private boolean hasBrokerStock(String broker, String stock) {
		if (bookEntries.get(broker).size() > 0) {
			return bookEntries.get(broker).stream().anyMatch(e->e.getName().equals(stock));
		}
		return false;
	}

	private void updateStockList(String sector) {
		stocksList.removeAllItems();

		List<String> stockNames = stocks.get(sector);

		for (String o : stockNames) {
			stocksList.addItem(o);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		updateTotalCost();
		enableOK();
	}

	private void enableOK() {
		boolean isEnabled = false;

		String c = costField.getText();
		String a = amountField.getText();

		isEnabled = !c.isEmpty() && !a.isEmpty();

		okButton.setEnabled(isEnabled);
	}

	private void updateTotalCost() {

		double amount = convertToDouble(amountField.getText());
		double cost = convertToDouble(costField.getText());
		double tax = convertToDouble(dividendTaxField.getText());

		double totalCost = amount * cost;
		double netCost = totalCost - tax;
		totalCostField.setText(String.format("%1$.2f", totalCost));
		netCostField.setText(String.format("%1$.2f", netCost));
	}

	private void updateTax() {
		double taxPercent = 25.5;
		if (tax0.isSelected()) {
			taxPercent = 0.0;
		} else if (tax15.isSelected()) {
			taxPercent = 15.0;
		} else if (tax255.isSelected()) {
			taxPercent = 25.5;
		}
		double amount = convertToDouble(amountField.getText());
		double cost = convertToDouble(costField.getText());
		double totalCost = amount * cost;
		double tax = totalCost * taxPercent / 100.0;
		dividendTaxField.setText(String.format("%1$.2f", tax));
	}

	protected Component[] getDialogComponents() {
		Component[] components = { sectorList, stocksList, dateChooser, currencyList, amountField, costField, rateField,
				brokerList, buttonBox, dividendTaxField, totalCostField, netCostField };
		return components;
	}

	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { sectorFieldLabel, stockFieldLabel, dateFieldLabel, localCurrencyButtonLabel,
				amountFieldLabel, costFieldLabel, rateFieldLabel,
				brokerFieldLabel, new JLabel("tax percents"),
				totalCostFieldLabel, netCostFieldLabel };
		return labels;
	}

}
