package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.BookEntry;
import com.stt.portfolio.I_TickerManager;


public class SellDialog extends BaseDialog {

	private String BROKER_CHANGED = "broker";
	private String STOCK_SELECTED = "stock";

	private JTextField totalCostField = new JTextField(FIELD_LEN);
	private JTextField amountField = new JTextField(FIELD_LEN);
	private JTextField costField = new JTextField(FIELD_LEN);
	private JTextField ownedAmountField = new JTextField(FIELD_LEN);
	private JTextField brokerCostField = new JTextField(FIELD_LEN);

	
	JComboBox brokerList;
	JComboBox stocksList;

	JLabel totalCostFieldLabel;
	JLabel brokerCostFieldLabel;
	JLabel brokerFieldLabel;
	JLabel costFieldLabel;
	JLabel ownedAmountFieldLabel;
	JLabel amountFieldLabel;
	JLabel dateFieldLabel;
	JLabel stockFieldLabel;
	
	
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

	public double getBrokerCost() {
		return convertToDouble(brokerCostField.getText());
	}

	public double getTotalCost() {
		return convertToDouble(totalCostField.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (BROKER_CHANGED.equals(e.getActionCommand())) {
			String broker = (String) brokerList.getSelectedItem();
			updateStockList(broker);
			updateOwnedAmount();
			String stockName = (String) stocksList.getSelectedItem();
			updateRateFieldForce(stockName);
		} else if (STOCK_SELECTED.equals(e.getActionCommand())) {
			updateOwnedAmount();
			String stockName = (String) stocksList.getSelectedItem();
			updateRateFieldForce(stockName);
		}
		else {
			super.actionPerformed(e);
		}

	}

	private void updateOwnedAmount() {
		String stockName = (String) stocksList.getSelectedItem();
		Collection<BookEntry> entries = bookEntries.get((String) brokerList.getSelectedItem());
		for (BookEntry entry : entries) {
			if (entry.getName().equals(stockName)) {
				ownedAmountField.setText(String.format("%1$.2f", entry.getAmount()));
				break;
			}
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

	public SellDialog(Component frameComp, Component locationComp, String title,
			Map<String, Collection<BookEntry>> bookEntries, I_TickerManager tickerManager) {
		super(frameComp, locationComp, title, tickerManager);

		this.bookEntries = bookEntries;
		
		
		// Brokers
		List<String> brokers = new ArrayList<String>();
		Iterator<String > iter = bookEntries.keySet().iterator();
		while (iter.hasNext()) {
			String b = iter.next();
			if (bookEntries.get(b).size() > 0) {
				brokers.add(b);
			}
		}
		
		
		brokerList = new JComboBox(brokers.toArray());
		brokerList.setActionCommand(BROKER_CHANGED);
		brokerList.addActionListener(this);
		//brokerList.setMinimumSize(new Dimension(200,20));
		// Stocks
		stocksList = new JComboBox();
		updateStockList((String) brokerList.getSelectedItem());
		stocksList.setActionCommand(STOCK_SELECTED);
		stocksList.addActionListener(this);
		
		amountField.addKeyListener(this);
		costField.addKeyListener(this);
		brokerCostField.addKeyListener(this);
		
		
		
		totalCostField.setEditable(false);
		totalCostField.setText("0.00");

		ownedAmountField.setEditable(false);
		
		brokerFieldLabel = new JLabel("Välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);
		
		stockFieldLabel = new JLabel("Arvopaperi: ");
		stockFieldLabel.setLabelFor(stocksList);

		dateFieldLabel = new JLabel("Myyntipäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);
		
		ownedAmountFieldLabel = new JLabel("Myytävissä: ");
		ownedAmountFieldLabel.setLabelFor(ownedAmountField);

		
		amountFieldLabel = new JLabel("Määrä: ");
		amountFieldLabel.setLabelFor(amountField);

		costFieldLabel = new JLabel("Myyntihinta: ");
		costFieldLabel.setLabelFor(costField);

		brokerCostFieldLabel = new JLabel("Kulut: ");
		brokerCostFieldLabel.setLabelFor(brokerCostField);

		totalCostFieldLabel = new JLabel("yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		updateOwnedAmount();
		
		init(getDialogLabels(), getDialogComponents());
	}

	private void updateStockList(String broker) {
		stocksList.removeAllItems();

		Collection<BookEntry> entries = bookEntries.get(broker);

		for (Object o : entries.toArray()) {
			BookEntry be = (BookEntry) o;
			stocksList.addItem(be.getName()  );
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

		double totalCost = amount * cost - brokerCost;

		totalCostField.setText(String.format("%1$.2f", totalCost));

	}
	
	
	protected Component[] getDialogComponents() {
		Component[] components = {  brokerList, stocksList, dateChooser,
				ownedAmountField, amountField, costField,  rateField, brokerCostField,
				totalCostField };
		return components;
	}

	
	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { brokerFieldLabel, stockFieldLabel, dateFieldLabel,
				ownedAmountFieldLabel, amountFieldLabel, costFieldLabel,rateFieldLabel,
				brokerCostFieldLabel, totalCostFieldLabel };
		return labels;
	}

}
