package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;


public class UpdateQuoteManuallyDialog extends BaseDialog {

	private JTextField priceField;
	private JTextField nameField;
	private JTextField tickerField;
	private JLabel priceFieldLabel;
	private JLabel nameFieldLabel;
	private JLabel tickerFieldLabel;
	private JLabel dateFieldLabel;

	/**
	 * Set up and show the dialog. The first Component argument determines which
	 * frame the dialog depends on; it should be a component in the dialog's
	 * controlling frame. The second Component argument should be null if you
	 * want the dialog to come up with its left corner in the center of the
	 * screen; otherwise, it should be the component on top of which the dialog
	 * should appear.
	 */

	public UpdateQuoteManuallyDialog(Component frameComp,
			Component locationComp, String title, String name, String ticker,
			I_TickerManager tickerManager) {
		super(frameComp, locationComp, title, tickerManager);
		priceField = new JTextField(11);
		nameField = new JTextField(11);
		tickerField = new JTextField(11);

		nameField.setEditable(false);
		nameField.setText(name);

		tickerField.setEditable(false);
		tickerField.setText(ticker);

		priceField.addKeyListener(this);

		nameFieldLabel = new JLabel("Nimi: ");
		nameFieldLabel.setLabelFor(nameField);

		tickerFieldLabel = new JLabel("Tunnus: ");
		tickerFieldLabel.setLabelFor(tickerField);

		dateFieldLabel = new JLabel("Noteerattu: ");
		dateFieldLabel.setLabelFor(dateChooser);

		priceFieldLabel = new JLabel("Noteeraus: ");
		priceFieldLabel.setLabelFor(priceField);

		updateRateField(name);

		init(getDialogLabels(), getDialogComponents());
	}

	protected Component[] getDialogComponents() {
		Component[] components = { nameField, tickerField, dateChooser,
				priceField, rateField};
		return components;
	}

	protected JLabel[] getDialogLabels() {
		JLabel[] labels = { nameFieldLabel, tickerFieldLabel, dateFieldLabel,
				priceFieldLabel, rateFieldLabel};
		return labels;
	}

	@Override
	public void keyReleased(KeyEvent e) {

		enableOK();
	}

	private void enableOK() {
		boolean isEnabled = false;

		String c = priceField.getText();

		isEnabled = !c.isEmpty();

		okButton.setEnabled(isEnabled);
	}

	public String getStockName() {
		return nameField.getText();
	}

	public String getTicker() {
		return tickerField.getText();
	}

	public double getPrice() {
		return convertToDouble(priceField.getText());
	}
}
