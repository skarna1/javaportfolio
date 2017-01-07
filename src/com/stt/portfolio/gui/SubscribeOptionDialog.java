package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.BookEntry;
import com.stt.portfolio.I_TickerManager;


public class SubscribeOptionDialog extends BaseDialog {

	private String SECTOR_CHANGED = "sector";
	private String STOCK_SELECTED = "stock";
	private String BROKER_CHANGED = "broker";
	
	private JTextField totalCostField = new JTextField(FIELD_LEN);
	private JTextField amountField = new JTextField(FIELD_LEN);
	private JTextField costField = new JTextField(FIELD_LEN);
	private JTextField ratioField = new JTextField(FIELD_LEN);
	
	private JTextField brokerCostField = new JTextField(FIELD_LEN);
	JComboBox brokerList;
	JComboBox sectorList;
	JComboBox stocksList;
	JComboBox optionList;

	JLabel totalCostFieldLabel;
	JLabel brokerCostFieldLabel;
	JLabel brokerFieldLabel;
	JLabel costFieldLabel;
	JLabel amountFieldLabel;
	JLabel dateFieldLabel;
	JLabel stockFieldLabel;
	JLabel sectorFieldLabel;
	JLabel ratioFieldLabel;
	JLabel optionFieldLabel;
	
	Map<String, List<String>> stocks;
	Map<String, Collection<BookEntry>> bookEntries;
	
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
	
	public String getOptionName() {
		return (String) optionList.getSelectedItem();
	}

	public double getBrokerCost() {
		return convertToDouble(brokerCostField.getText());
	}

	public double getTotalCost() {
		return convertToDouble(totalCostField.getText());
	}
	
	public double getRatio() {
		return convertToDouble(ratioField.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (BROKER_CHANGED.equals(e.getActionCommand())) {
			String broker = (String) brokerList.getSelectedItem();
			updateOptionsList(broker);
			
			String stockName = (String) stocksList.getSelectedItem();
			updateRateField(stockName);
		} if (SECTOR_CHANGED.equals(e.getActionCommand())) {
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

	public SubscribeOptionDialog(Component frameComp, Component locationComp, String title,
			Object[] brokers, Object[] sectors, Map<String, List<String>> stocks,
			I_TickerManager tickerManager, Map<String, Collection<BookEntry>> bookEntries) {
		super(frameComp, locationComp, title, tickerManager);

		this.stocks = stocks;
		this.bookEntries = bookEntries;
		
		// Brokers

		brokerList = new JComboBox(brokers);
		brokerList.setActionCommand(BROKER_CHANGED);
		brokerList.addActionListener(this);

		//brokerList.setEditable(true);

		// Sectors
		sectorList = new JComboBox(sectors);
		sectorList.setActionCommand(SECTOR_CHANGED);
		sectorList.addActionListener(this);

		// Stocks
		stocksList = new JComboBox();
		updateStockList((String) sectorList.getSelectedItem());
		stocksList.setActionCommand(STOCK_SELECTED);
		stocksList.addActionListener(this);
		
		// Options
		optionList = new JComboBox();
		updateOptionsList((String)brokerList.getSelectedItem());
		
		amountField.addKeyListener(this);
		costField.addKeyListener(this);
		brokerCostField.addKeyListener(this);

		brokerCostField.setText("0.00");
		ratioField.setText("1");
		
		totalCostField.setEditable(false);
		totalCostField.setText("0.00");

		sectorFieldLabel = new JLabel("Toimiala: ");
		sectorFieldLabel.setLabelFor(sectorList);

		stockFieldLabel = new JLabel("Arvopaperi: ");
		stockFieldLabel.setLabelFor(stocksList);

		dateFieldLabel = new JLabel("Merkintäpäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);

		amountFieldLabel = new JLabel("Osakkeiden lkm: ");
		amountFieldLabel.setLabelFor(amountField);

		costFieldLabel = new JLabel("Merkintähinta/osake: ");
		costFieldLabel.setLabelFor(costField);

		brokerFieldLabel = new JLabel("Välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);

		brokerCostFieldLabel = new JLabel("Kulut: ");
		brokerCostFieldLabel.setLabelFor(brokerCostField);
		
		
		totalCostFieldLabel = new JLabel("yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		ratioFieldLabel = new JLabel("Optioita / osake: ");
		ratioFieldLabel.setLabelFor(ratioField);
		
		optionFieldLabel = new JLabel("Optio tai MO: ");
		optionFieldLabel.setLabelFor(optionList);
		
		init(getDialogLabels(), getDialogComponents());
	}

	private void updateStockList(String sector) {
		stocksList.removeAllItems();

		List<String> stockNames = stocks.get(sector);

		for (Object o : stockNames.toArray()) {
			stocksList.addItem(o);
		}
	}

	private void updateOptionsList(String broker) {
		optionList.removeAllItems();

		Collection<BookEntry> entries = bookEntries.get(broker);

		for (Object o : entries.toArray()) {
			BookEntry be = (BookEntry) o;
			if (be.isOption()) {
				optionList.addItem(be.getName());
			}
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		updateTotalCost();
		enableOK();
	}

	private void enableOK() {
		boolean isEnabled = false;
		String bc = brokerCostField.getText();
		String c = costField.getText();
		String a = amountField.getText();

		isEnabled = !bc.isEmpty() && !c.isEmpty() && !a.isEmpty();

		okButton.setEnabled(isEnabled);
	}

	private void updateTotalCost() {

		double amount = convertToDouble(amountField.getText());
		double cost = convertToDouble(costField.getText());
		double brokerCost = convertToDouble(brokerCostField.getText());

		double totalCost = amount * cost + brokerCost;

		totalCostField.setText(String.format("%1$.2f", totalCost));

	}

	
	protected Component[] getDialogComponents() {
		Component[] components = { sectorList, stocksList, dateChooser, brokerList, optionList, ratioField,
				amountField, costField, rateField,  brokerCostField,
				totalCostField };
		return components;
	}

	
	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { sectorFieldLabel, stockFieldLabel, dateFieldLabel,
				brokerFieldLabel,optionFieldLabel, ratioFieldLabel, 
				amountFieldLabel, costFieldLabel, rateFieldLabel,
				brokerCostFieldLabel, totalCostFieldLabel };
		return labels;
	}

}
