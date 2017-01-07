package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;

public class CapitalRepaymentDialog extends BaseDialog {

	private static String SECTOR_CHANGED = "sector";
	private static String STOCK_SELECTED = "stock";

	private JTextField totalCostField = new JTextField(FIELD_LEN);

	private JTextField amountField = new JTextField(FIELD_LEN);
	private JTextField costField = new JTextField(FIELD_LEN);


	JComboBox brokerList;
	JComboBox sectorList;
	JComboBox stocksList;

	JLabel totalCostFieldLabel;
	JLabel brokerFieldLabel;
	JLabel costFieldLabel;
	JLabel amountFieldLabel;
	JLabel dateFieldLabel;
	JLabel stockFieldLabel;
	JLabel sectorFieldLabel;
	

	Map<String, List<String>> stocks = null;
	I_TickerManager tickerManager = null;

	public String getStockName() {
		return (String) stocksList.getSelectedItem();
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

	@Override
	public void actionPerformed(ActionEvent e) {

		if (SECTOR_CHANGED.equals(e.getActionCommand())) {
			String sector = (String) sectorList.getSelectedItem();
			updateStockList(sector);
			String stockName = (String) stocksList.getSelectedItem();
			updateRateField(stockName);
		} else if (STOCK_SELECTED.equals(e.getActionCommand())) {
			String stockName = (String) stocksList.getSelectedItem();
			updateRateField(stockName);

		} else {
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

	public CapitalRepaymentDialog(Component frameComp, Component locationComp,
			String title, Object[] brokers, Object[] sectors,
			Map<String, List<String>> stocks, I_TickerManager tickerManager) {
		super(frameComp, locationComp, title, tickerManager);

		this.stocks = stocks;
	

		// Brokers

		brokerList = new JComboBox(brokers);
		brokerList.setEditable(true);

		// Sectors
		sectorList = new JComboBox(sectors);
		sectorList.setActionCommand(SECTOR_CHANGED);
		sectorList.addActionListener(this);

		// Stocks
		stocksList = new JComboBox();
		updateStockList((String) sectorList.getSelectedItem());
		stocksList.setActionCommand(STOCK_SELECTED);
		stocksList.addActionListener(this);

		amountField.addKeyListener(this);
		costField.addKeyListener(this);

		totalCostField.setEditable(false);
		totalCostField.setText("0.00");		

		sectorFieldLabel = new JLabel("Toimiala: ");
		sectorFieldLabel.setLabelFor(sectorList);

		stockFieldLabel = new JLabel("Arvopaperi: ");
		stockFieldLabel.setLabelFor(stocksList);

		dateFieldLabel = new JLabel("Maksupäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);

		amountFieldLabel = new JLabel("Määrä: ");
		amountFieldLabel.setLabelFor(amountField);

		costFieldLabel = new JLabel("Palautus/osake: ");
		costFieldLabel.setLabelFor(costField);

		brokerFieldLabel = new JLabel("Välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);

		totalCostFieldLabel = new JLabel("yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		String stockName = (String) stocksList.getSelectedItem();
		updateRateField(stockName);
		init(getDialogLabels(), getDialogComponents());
	}

	private void updateStockList(String sector) {
		stocksList.removeAllItems();

		List<String> stockNames = stocks.get(sector);

		for (Object o : stockNames.toArray()) {
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

		double totalCost = amount * cost;

		totalCostField.setText(String.format("%1$.2f", totalCost));

	}

	protected Component[] getDialogComponents() {
		Component[] components = { sectorList, stocksList, dateChooser,
				amountField, costField, rateField, brokerList, totalCostField };
		return components;
	}

	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { sectorFieldLabel, stockFieldLabel, dateFieldLabel,
				amountFieldLabel, costFieldLabel, rateFieldLabel,
				brokerFieldLabel, totalCostFieldLabel };
		return labels;
	}

}
