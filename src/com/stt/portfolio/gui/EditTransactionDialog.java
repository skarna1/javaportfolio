package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;
import com.stt.portfolio.Stock;
import com.stt.portfolio.transactions.Transaction;

public class EditTransactionDialog extends BaseDialog implements ActionListener {

    JComboBox<String> transactionList;
    List<Transaction> transactions;
    Transaction selectedTransaction;

    JTextField opField;
    JTextField tickerField;
    JTextField nameField;
    JTextField amountField;
    JTextField priceField;
    JTextField brokerField;
    JTextField brokerCostField;
    JTextField costField;

    
    /**
     * Constructor for editing a single transaction (from context menu).
     */
    public EditTransactionDialog(Component frameComp, Component locationComp, String title, Transaction t, I_TickerManager tickerManager) {
        super(frameComp, locationComp, title, tickerManager);

        this.selectedTransaction = t;
        
        opField = new JTextField(FIELD_LEN);
        opField.setEditable(false);
        tickerField = new JTextField(FIELD_LEN);
        nameField = new JTextField(FIELD_LEN);
        amountField = new JTextField(FIELD_LEN);
        priceField = new JTextField(FIELD_LEN);
        brokerField = new JTextField(FIELD_LEN);
        brokerCostField = new JTextField(FIELD_LEN);
        costField = new JTextField(FIELD_LEN);

        JLabel[] labels = new JLabel[] { new JLabel("Päiväys: "), new JLabel("Tyyppi: "), new JLabel("Ticker: "), new JLabel("Nimi: "), new JLabel("Määrä: "), new JLabel("Kurssi: "), new JLabel("Välittäjä: "), new JLabel("Välityskulut: "), new JLabel("Yhteensä: ") };

        Component[] components = new Component[] { dateChooser, opField, tickerField, nameField, amountField, priceField, brokerField, brokerCostField, costField };
       
        System.out.println("Selected transaction: " + selectedTransaction);
        if (this.selectedTransaction != null) {
            populateFieldsFromSelected();
            okButton.setEnabled(true);
        }
        init(labels, components);
    }

    private void populateFieldsFromSelected() {
        if (selectedTransaction == null) return;
        dateChooser.setDate(selectedTransaction.getDate());
        opField.setText(selectedTransaction.getOp());
        tickerField.setText(selectedTransaction.getTicker());
        Stock s = tickerManager.getStock(selectedTransaction.getTicker());
        if (s != null) {
            nameField.setText(s.getName());
        }
        
        amountField.setText(String.valueOf(selectedTransaction.getAmount()));
        priceField.setText(String.valueOf(selectedTransaction.getPrice()));
        brokerField.setText(selectedTransaction.getBroker());
        brokerCostField.setText(String.valueOf(selectedTransaction.getBrokerCost()));
        costField.setText(String.valueOf(selectedTransaction.getCost()));
        rateField.setText(String.format("%1$.5f", selectedTransaction.getRate()));
    }

    private void setStockNameFromTicker() {
        String ticker = tickerField.getText();
        Stock s = tickerManager.getStock(ticker);
        if (s != null) {
            nameField.setText(s.getName());
        } else {
            nameField.setText("");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      if (BUTTON_OK.equals(e.getActionCommand())) {
            applyChanges();
            super.actionPerformed(e);
        } else {
            super.actionPerformed(e);
        }
    }

    private void applyChanges() {
        if (selectedTransaction == null)
            return;

        selectedTransaction.setDate(dateChooser.getDate());
        selectedTransaction.setTicker(tickerField.getText());
        selectedTransaction.setName(nameField.getText());
        selectedTransaction.setAmount(convertToDouble(amountField.getText()));
        selectedTransaction.setPrice(convertToDouble(priceField.getText()));
        selectedTransaction.setBroker(brokerField.getText());
        selectedTransaction.setBrokerCost(convertToDouble(brokerCostField.getText()));
        selectedTransaction.setCost(convertToDouble(costField.getText()));
        selectedTransaction.setRate(convertToDouble(rateField.getText()));
    }

}
