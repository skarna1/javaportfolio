package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;
import com.stt.portfolio.Stock;


public class BuyDialog extends BaseDialog {

	private String SECTOR_CHANGED = "sector";
	private String STOCK_SELECTED = "stock";

	private JPanel currencyButtonPanel;
	private JTextField totalCostField;
	private JTextField amountField;
	private JTextField costField;

	private JTextField brokerCostField;
	private JComboBox brokerList;
	private JComboBox sectorList;
	private JComboBox stocksList;

	private JLabel totalCostFieldLabel;
	private JLabel brokerCostFieldLabel;
	private JLabel brokerFieldLabel;
	private JLabel costFieldLabel;
	private JLabel amountFieldLabel;
	private JLabel dateFieldLabel;
	private JLabel stockFieldLabel;
	private JLabel sectorFieldLabel;
	private JLabel totalCostButtonLabel;
	private JLabel perShareCostButtonLabel;
	private JLabel localCurrencyButtonLabel;
	private static String totalCostString="Kokonaishinta";
	private static String perShareCostString="Hinta/kpl";
	private JRadioButton totalCostButton;
	private JRadioButton perShareCostButton;
	private ButtonGroup buttonGroup;
	
	Map<String, List<String>> stocks;

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

	public double getBrokerCost() {
		return convertToDouble(brokerCostField.getText());
	}

	public double getTotalCost() {
		return convertToDouble(totalCostField.getText());
	}

	protected void setSelection(Stock s) {
		if (s!= null) {
			sectorList.setSelectedItem(s.getSector());
			stocksList.setSelectedItem(s.getName());
		}
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
			updateForeignCurrency(stockName);
			updateRateField(stockName);
		} 
		else if (totalCostString.equals(e.getActionCommand())){
			totalCostField.setEditable(true);
			costField.setEditable(false);
		}
		else if (perShareCostString.equals(e.getActionCommand())){
			totalCostField.setEditable(false);
			costField.setEditable(true);
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

	public BuyDialog(Component frameComp, Component locationComp, String title,
			Object[] brokers, Object[] sectors, Map<String, List<String>> stocks,
			I_TickerManager tickerManager, Stock s) {
		super(frameComp, locationComp, title, tickerManager);

		this.stocks = stocks;

		totalCostField = new JTextField(FIELD_LEN);
		amountField = new JTextField(FIELD_LEN);
		costField = new JTextField(FIELD_LEN);
		brokerCostField = new JTextField(FIELD_LEN);
	    totalCostButton = new JRadioButton(totalCostString);
		perShareCostButton = new JRadioButton(perShareCostString);
		buttonGroup = new ButtonGroup();
		
	   
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
		brokerCostField.addKeyListener(this);
		totalCostField.addKeyListener(this);

		totalCostField.setEditable(false);
		totalCostField.setText("0.00");

		sectorFieldLabel = new JLabel("Toimiala: ");
		sectorFieldLabel.setLabelFor(sectorList);

		stockFieldLabel = new JLabel("Arvopaperi: ");
		stockFieldLabel.setLabelFor(stocksList);

		dateFieldLabel = new JLabel("Ostopäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);

		amountFieldLabel = new JLabel("Määrä: ");
		amountFieldLabel.setLabelFor(amountField);

		costFieldLabel = new JLabel("Ostohinta: ");
		costFieldLabel.setLabelFor(costField);

		brokerFieldLabel = new JLabel("Välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);

		brokerCostFieldLabel = new JLabel("Kulut: ");
		brokerCostFieldLabel.setLabelFor(brokerCostField);


		totalCostFieldLabel = new JLabel("yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		totalCostButtonLabel = new JLabel("");
		perShareCostButtonLabel = new JLabel("");
		localCurrencyButtonLabel = new JLabel("Valuutta: ");
		
		totalCostButton.setActionCommand(totalCostString);
		perShareCostButton.setActionCommand(perShareCostString);
		perShareCostButton.setSelected(true);
		buttonGroup.add(totalCostButton);
		buttonGroup.add(perShareCostButton);
		totalCostButton.addActionListener(this);
		perShareCostButton.addActionListener(this);
		
		
		
		currencyButtonPanel = new JPanel();
		currencyButtonPanel.setLayout(new BoxLayout(currencyButtonPanel,
	            BoxLayout.LINE_AXIS));

		localCurrencyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		currencyButtonPanel.add(localCurrencyButton);
		foreignCurrencyButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		currencyButtonPanel.add(foreignCurrencyButton);
		
		setSelection(s);

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
		if (costField.isEditable()){
			updateTotalCost();
		}
		else
		{
			updateCost();
		}
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

	private void updateCost() {
		double amount = convertToDouble(amountField.getText());
		double totalCost = convertToDouble(totalCostField.getText());
		double brokerCost = convertToDouble(brokerCostField.getText());

		double cost = (totalCost-brokerCost)/amount;

		costField.setText(String.format("%1$.2f", cost));
	}
	
	protected Component[] getDialogComponents() {
		Component[] components = { sectorList, stocksList, dateChooser,
				amountField, perShareCostButton,totalCostButton,costField, 
				currencyButtonPanel,rateField, brokerList, brokerCostField,
				totalCostField };
		return components;
	}


	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { sectorFieldLabel, stockFieldLabel, dateFieldLabel,
				amountFieldLabel, perShareCostButtonLabel,totalCostButtonLabel,costFieldLabel, 
				localCurrencyButtonLabel,rateFieldLabel,brokerFieldLabel,
				brokerCostFieldLabel, totalCostFieldLabel };
		return labels;
	}

}
