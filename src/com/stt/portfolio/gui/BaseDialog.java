package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;
import com.stt.portfolio.Stock;
import com.stt.portfolioupdater.CcyFactory;
import com.stt.portfolioupdater.CcyFetcher;
import com.toedter.calendar.JDateChooser;

public abstract class BaseDialog extends JDialog implements ActionListener,
KeyListener {

	protected String BUTTON_OK = "Ok";
	protected String BUTTON_CANCEL = "Peru";

	protected JDateChooser dateChooser;
	protected JButton okButton;
	protected JButton cancelButton;
	private boolean isOk = false;

	protected int FIELD_LEN = 20;
	protected static String CURRENCY_SELECTED = "ccy";

	protected JTextField rateField;
	protected JLabel rateFieldLabel;

	I_TickerManager tickerManager = null;

	// protected JRadioButton localCurrencyButton;
	// protected JRadioButton foreignCurrencyButton;
	// protected ButtonGroup currencyGroup;

	protected JComboBox<String> currencyList;

	protected static String localCurrencyString="EUR";

	private Component frameComp;

	public boolean isOk() {
		return isOk;
	}

	public Date getTransactionDate() {
		return dateChooser.getDate();
	}

	public double getRate() {
		return convertToDouble(rateField.getText());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (BUTTON_OK.equals(e.getActionCommand())) {
			setVisible(false);
			isOk = true;

		} else if (BUTTON_CANCEL.equals(e.getActionCommand())) {
			setVisible(false);
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

	public BaseDialog(Component frameComp, Component locationComp,
			String title, I_TickerManager tickerManager) {
		super(JOptionPane.getFrameForComponent(frameComp), title, true);
		this.frameComp = frameComp;
		dateChooser = new JDateChooser(Calendar.getInstance().getTime());
		dateChooser.setLocale(new Locale("fi", "FI"));

		this.tickerManager = tickerManager;

		// Create and initialize the buttons.
		cancelButton = new JButton(BUTTON_CANCEL);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(BUTTON_CANCEL);

		okButton = new JButton(BUTTON_OK);
		okButton.setActionCommand(BUTTON_OK);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		okButton.setEnabled(false);

		rateField = new JTextField(FIELD_LEN);
		rateField.setVisible(true);
		rateField.setText("1.0000");

		rateFieldLabel = new JLabel("Valuuttakurssi: ");
		rateFieldLabel.setLabelFor(rateField);
		rateFieldLabel.setVisible(true);

		// Currencies
		currencyList = new JComboBox<String>();
		currencyList.addItem(localCurrencyString);
		currencyList.setActionCommand(CURRENCY_SELECTED);
		currencyList.addActionListener(this);

		updateRateFieldCcy(localCurrencyString);
	}

	protected void init(JLabel[] labels, Component[] components) {

		// Lay out the buttons from left to right.

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(okButton);

		// Put everything together, using the content pane's BorderLayout.
		Container contentPane = getContentPane();
		contentPane.add(setupTextFields(labels, components),
				BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);

		pack();

		setLocationRelativeTo(frameComp);
		setVisible(true);
	}

	private JPanel setupTextFields(JLabel[] labels, Component[] components) {

		// Lay out the text controls and the labels.
		JPanel textControlsPane = new JPanel();
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();

		textControlsPane.setLayout(gridbag);

		addLabelTextRows(labels, components, gridbag, textControlsPane);

		c.gridwidth = GridBagConstraints.REMAINDER; // last
		c.anchor = GridBagConstraints.WEST;
		c.weightx = 1.0;

		textControlsPane.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Tiedot"), BorderFactory
				.createEmptyBorder(5, 5, 5, 5)));
		return textControlsPane;

	}

	private void addLabelTextRows(JLabel[] labels, Component[] textFields,
			GridBagLayout gridbag, Container container) {
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5, 5, 5, 5); // this statement added.
		int numLabels = labels.length;

		for (int i = 0; i < numLabels; i++) {
			c.gridwidth = GridBagConstraints.RELATIVE; // next-to-last
			c.fill = GridBagConstraints.NONE; // reset to default
			c.weightx = 0.0; // reset to default
			container.add(labels[i], c);

			c.gridwidth = GridBagConstraints.REMAINDER; // end row
			c.fill = GridBagConstraints.HORIZONTAL;
			c.weightx = 1.0;
			container.add(textFields[i], c);
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {

	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	protected double convertToDouble(String d) {
		double value = 0.0;

		try {
			value = Double.parseDouble(d.replace(',', '.').trim());
		} catch (Exception e) {

		}
		return value;
	}

	protected void updateRateField(String stockName) {
		if (stockName != null) {
			String ticker = tickerManager.getTicker(stockName);

			Stock s = tickerManager.getStock(ticker);
			if (s != null) {
				updateRateFieldCcy(s.getCcy());
			}
		}
	}

	protected void updateRateFieldCcy(String ccy) {
		double rate = 1.000;

		rateField.setVisible(true);
		rateFieldLabel.setText("Kurssi EUR/" + ccy + " : ");
		rateFieldLabel.setVisible(true);

		if (!ccy.equals(localCurrencyString)) {

				CcyFetcher f = CcyFactory.createCcyFetcher();
				try {
					rate = f.getExchangeRate(ccy);
				}
				catch (Exception e) {

				}
		}
		rateField.setText(String.format("%1$.5f", rate));
	}

	protected void updateForeignCurrency(String stockName) {
		if (stockName != null) {
			String ticker = tickerManager.getTicker(stockName);

			Stock s = tickerManager.getStock(ticker);
			if (s != null) {
				currencyList.removeAllItems();
				currencyList.addItem(s.getCcy());
				if (!s.getCcy().equals(localCurrencyString)) {
					currencyList.addItem(localCurrencyString);
				}

				String[] currencies = { "SEK", "USD", "CAD", "NOK", "DKK" };


				for (String ccy : currencies) {
					if (((DefaultComboBoxModel) currencyList.getModel()).getIndexOf(ccy) == -1) {
						currencyList.addItem(ccy);
					}
				}
				currencyList.setSelectedIndex(0);
			}
		}
	}
}
