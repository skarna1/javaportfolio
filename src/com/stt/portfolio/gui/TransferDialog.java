package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;

public class TransferDialog extends BaseDialog {

	private static String CURRENCY_CHANGED = "currency";
	
	private JTextField totalCostField;
	private JComboBox currencyList;
	
	private JLabel totalCostFieldLabel;
	private JLabel dateFieldLabel;
	private JLabel currencyFieldLabel;

	

	public double getTotalCost() {
		return convertToDouble(totalCostField.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (CURRENCY_CHANGED.equals(e.getActionCommand())) {
		String ccy = (String) currencyList.getSelectedItem();
		updateRateFieldCcy(ccy, true);
		} else
	{
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

	public TransferDialog(Component frameComp, Component locationComp,
			String title,  I_TickerManager tickerManager) {
		super(frameComp, locationComp, title, tickerManager);

		String[] currencies = {"EUR", "USD", "SEK" , "NOK"};
		currencyList = new JComboBox(currencies);
		currencyList.setEditable(true);
		currencyList.addActionListener(this);
		currencyList.setSelectedIndex(0);
		currencyList.setActionCommand(CURRENCY_CHANGED);
		
		totalCostField = new JTextField(FIELD_LEN);
		totalCostField.setEditable(true);
		totalCostField.setText("");		
		totalCostField.addKeyListener(this);
		
		dateFieldLabel = new JLabel("Maksupäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);


		totalCostFieldLabel = new JLabel("yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		currencyFieldLabel = new JLabel("Valuutta: ");
		
		updateRateFieldCcy((String) currencyList.getSelectedItem(), true);
		init(getDialogLabels(), getDialogComponents());
	}

	

	

	@Override
	public void keyReleased(KeyEvent e) {
		
		enableOK();
	}

	private void enableOK() {
		boolean isEnabled = false;

		String c = totalCostField.getText();
		

		isEnabled = !c.isEmpty();

		okButton.setEnabled(isEnabled);
	}

	


	protected Component[] getDialogComponents() {
		Component[] components = {dateChooser, currencyList,
				rateField,  totalCostField };
		return components;
	}

	protected JLabel[] getDialogLabels() {
		JLabel[] labels = {  dateFieldLabel, currencyFieldLabel,
				 rateFieldLabel,
				totalCostFieldLabel };
		return labels;
	}

}
