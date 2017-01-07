package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;

public class AccountTransferDialog extends BaseDialog {

	
	private JTextField newBrokerField;
	private JComboBox brokerList;
	private JLabel dateFieldLabel;
	private JLabel brokerFieldLabel;
	private JLabel newBrokerFieldLabel;

	public String getNewBroker() {
		return newBrokerField.getText();
	}

	public String getBroker() {
		return (String) brokerList.getSelectedItem();
	}

	/**
	 * Set up and show the dialog. The first Component argument determines which
	 * frame the dialog depends on; it should be a component in the dialog's
	 * controlling frame. The second Component argument should be null if you
	 * want the dialog to come up with its left corner in the center of the
	 * screen; otherwise, it should be the component on top of which the dialog
	 * should appear.
	 */

	public AccountTransferDialog(Component frameComp, Component locationComp,
			String title,  Object[] brokers,I_TickerManager tickerManager) {
		super(frameComp, locationComp, title, tickerManager);

		
		brokerList = new JComboBox(brokers);
		brokerList.setEditable(true);
		brokerList.setSelectedIndex(0);
		
		newBrokerField = new JTextField(FIELD_LEN);
		newBrokerField.setEditable(true);
		newBrokerField.setText("");		
		newBrokerField.addKeyListener(this);
		
		dateFieldLabel = new JLabel("Siirtopäivä: ");
		dateFieldLabel.setLabelFor(dateChooser);
		
		brokerFieldLabel = new JLabel("Vanha välittäjä: ");
		brokerFieldLabel.setLabelFor(brokerList);
		
		newBrokerFieldLabel = new JLabel("Uusi välittäjä: ");
		newBrokerFieldLabel.setLabelFor(newBrokerField);
		
		init(getDialogLabels(), getDialogComponents());
	}

	

	

	@Override
	public void keyReleased(KeyEvent e) {
		
		enableOK();
	}

	private void enableOK() {
		boolean isEnabled = false;

		String c = newBrokerField.getText();
		
		isEnabled = !c.isEmpty();

		okButton.setEnabled(isEnabled);
	}


	protected Component[] getDialogComponents() {
		Component[] components = {dateChooser, brokerList,
				  newBrokerField };
		return components;
	}

	protected JLabel[] getDialogLabels() {
		JLabel[] labels = {  dateFieldLabel, 
				 brokerFieldLabel, newBrokerFieldLabel
				 };
		return labels;
	}

}
