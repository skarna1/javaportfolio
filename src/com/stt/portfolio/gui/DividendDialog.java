package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;
import com.stt.portfolio.Stock;

public class DividendDialog extends BaseDialog {

	private static String SECTOR_CHANGED = "sector";
	private static String STOCK_SELECTED = "stock";

	private JTextField totalCostField = new JTextField(FIELD_LEN);
	private JTextField netCostField = new JTextField(FIELD_LEN);
	private JTextField amountField = new JTextField(FIELD_LEN);
	private JTextField costField = new JTextField(FIELD_LEN);
	private JTextField dividendTaxField = new JTextField(FIELD_LEN);

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

	private JPanel currencyButtonPanel;
	
	Map<String, List<String>> stocks = null;
	I_TickerManager tickerManager = null;

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

		if (SECTOR_CHANGED.equals(e.getActionCommand())) {
			String sector = (String) sectorList.getSelectedItem();
			updateStockList(sector);
			String stockName = (String) stocksList.getSelectedItem();
			updateRateFieldForce(stockName);
		} else if (STOCK_SELECTED.equals(e.getActionCommand())) {
			String stockName = (String) stocksList.getSelectedItem();
			updateForeignCurrency(stockName);
			updateRateFieldForce(stockName);

		} 
		else if (foreignCurrencyString.equals(e.getActionCommand()) || localCurrencyString.equals(e.getActionCommand())) {
			String stockName = (String) stocksList.getSelectedItem();
			updateRateField(stockName);
		}
		else {
			super.actionPerformed(e);
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
			String title, String[] brokers, String[] sectors,
			Map<String, List<String>> stocks, I_TickerManager tickerManager, Stock s) {
		super(frameComp, locationComp, title, tickerManager);

		this.stocks = stocks;
	

		// Brokers

		brokerList = new JComboBox<String>(brokers);
		brokerList.setEditable(true);

		// Sectors
		sectorList = new JComboBox<String>(sectors);
		sectorList.setActionCommand(SECTOR_CHANGED);
		sectorList.addActionListener(this);

		// Stocks
		stocksList = new JComboBox<String>();
		updateStockList((String) sectorList.getSelectedItem());
		stocksList.setActionCommand(STOCK_SELECTED);
		stocksList.addActionListener(this);

		amountField.addKeyListener(this);
		costField.addKeyListener(this);
		dividendTaxField.addKeyListener(this);
		
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
		
		currencyButtonPanel = new JPanel();
		currencyButtonPanel.setLayout(new BoxLayout(currencyButtonPanel,
	            BoxLayout.LINE_AXIS));

		localCurrencyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		currencyButtonPanel.add(localCurrencyButton);
		foreignCurrencyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		currencyButtonPanel.add(foreignCurrencyButton);
		
		amountFieldLabel = new JLabel("Määrä: ");
		amountFieldLabel.setLabelFor(amountField);

		costFieldLabel = new JLabel("Osinko/osake: ");
		costFieldLabel.setLabelFor(costField);

		brokerFieldLabel = new JLabel("Välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);

		dividendTaxLabel = new JLabel("Ennakonpidätys: ");
		dividendTaxLabel.setLabelFor(dividendTaxField);
		dividendTaxField.setText("0.00");		
		
		totalCostFieldLabel = new JLabel("Yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);
		
		netCostFieldLabel = new JLabel("Netto: ");
		netCostFieldLabel.setLabelFor(totalCostField);
		
		setSelection(s);
		String stockName = String.valueOf(stocksList.getSelectedItem());
		updateRateFieldForce(stockName);
		updateForeignCurrency(stockName);
		init(getDialogLabels(), getDialogComponents());
	}

	protected void setSelection(Stock s) {
		if (s!= null) {
			sectorList.setSelectedItem(s.getSector());
			stocksList.setSelectedItem(s.getName());
		}
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

	protected Component[] getDialogComponents() {
		Component[] components = { sectorList, stocksList, dateChooser, currencyButtonPanel,
				amountField, costField, rateField, brokerList, dividendTaxField, totalCostField, netCostField };
		return components;
	}

	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { sectorFieldLabel, stockFieldLabel, dateFieldLabel, localCurrencyButtonLabel,
				amountFieldLabel, costFieldLabel, rateFieldLabel,
				brokerFieldLabel, dividendTaxLabel, totalCostFieldLabel, netCostFieldLabel };
		return labels;
	}

}
