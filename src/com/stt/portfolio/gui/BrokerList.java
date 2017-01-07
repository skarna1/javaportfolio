package com.stt.portfolio.gui;

import java.awt.event.ActionListener;

import javax.swing.JComboBox;

public class BrokerList {

	private String selectedBroker = null;
	private JComboBox<String> brokerList = null;

	public BrokerList(Object[] brokerNames, ActionListener listener) {

		String[] brokers = new String[brokerNames.length + 1];
		brokers[0] = "Kaikki";
		selectedBroker = brokers[0];
		int i = 1;
		for (Object s : brokerNames) {
			brokers[i] = (String) s;
			i++;
		}

		brokerList = new JComboBox<>(brokers);
		brokerList.setSelectedIndex(0);
		brokerList.addActionListener(listener);
		brokerList.setActionCommand("broker");
	}

	public JComboBox<String> getBrokerList() {
		return brokerList;
	}

	public String getSelectedBroker() {
		return selectedBroker;
	}

	public void setSelectedBroker(String selectedBroker) {
		this.selectedBroker = selectedBroker;
	}

	public void updateSelectedBroker() {
		selectedBroker = (String) brokerList.getSelectedItem();

	}
}
