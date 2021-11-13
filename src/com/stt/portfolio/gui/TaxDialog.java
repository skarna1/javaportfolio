package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;

public class TaxDialog extends BaseDialog {

	private JTextField totalCostField;

	private JLabel totalCostFieldLabel;
	private JLabel dateFieldLabel;
	private JLabel currencyFieldLabel;
	public double getTotalCost() {
		return convertToDouble(totalCostField.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (CURRENCY_SELECTED.equals(e.getActionCommand())) {
			Object item = currencyList.getSelectedItem();
			if (item != null) {
				updateRateFieldCcy((String) item);
			}
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

	public TaxDialog(Component frameComp, Component locationComp,
			String title,  I_TickerManager tickerManager) {
		super(frameComp, locationComp, title, tickerManager);

		totalCostField = new JTextField(FIELD_LEN);
		totalCostField.setEditable(true);
		totalCostField.setText("");
		totalCostField.addKeyListener(this);

		dateFieldLabel = new JLabel("Maksupäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);


		totalCostFieldLabel = new JLabel("yhteensä: ");
		totalCostFieldLabel.setLabelFor(totalCostField);

		currencyFieldLabel = new JLabel("Valuutta: ");

		updateRateFieldCcy(localCurrencyString);
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
