package com.stt.portfolio.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import com.stt.portfolio.I_TickerManager;

public class AddStockDialog extends BaseDialog {

    private JTextField tickerField;
    private JTextField nameField;
    private JComboBox<String> sectorList;
    private JComboBox<String> typeList;
    private JComboBox<String> currencyList;
    private JTextField priceDividerField;
    private JTextField countryField;

    public AddStockDialog(Component frameComp, Component locationComp, String title, 
                         I_TickerManager tickerManager, String[] sectors) {
        super(frameComp, locationComp, title, tickerManager);

        tickerField = new JTextField(FIELD_LEN);
        nameField = new JTextField(FIELD_LEN);
        sectorList = new JComboBox<>(sectors != null ? sectors : new String[0]);
        priceDividerField = new JTextField(FIELD_LEN);
        countryField = new JTextField(FIELD_LEN);
        countryField.setText("Suomi"); // Default country

        // Type combo box: Stock (S) or Option (O)
        typeList = new JComboBox<>(new String[]{"Osake (S)", "Optio (O)"});

        // Currency combo box - get from tickerManager
        Object[] ccys = tickerManager.getCcys();
        String[] currencies = new String[ccys.length + 1];
        currencies[0] = "EUR";
        for (int i = 0; i < ccys.length; i++) {
            currencies[i + 1] = (String) ccys[i];
        }
        currencyList = new JComboBox<>(currencies);
        currencyList.setSelectedItem("EUR");

        // Price divider default
        priceDividerField.setText("1");

        JLabel[] labels = new JLabel[]{
                new JLabel("Ticker: "),
                new JLabel("Nimi: "),
                new JLabel("Toimiala: "),
                new JLabel("Tyyppi: "),
                new JLabel("Valuutta: "),
                new JLabel("Hintajakaja: "),
                new JLabel("Maa: ")
        };

        Component[] components = new Component[]{
                tickerField,
                nameField,
                sectorList,
                typeList,
                currencyList,
                priceDividerField,
                countryField
        };

	okButton.setEnabled(true);

        init(labels, components);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (BUTTON_OK.equals(e.getActionCommand())) {
            if (validateInput()) {
                super.actionPerformed(e);
            }
        } else {
            super.actionPerformed(e);
        }
    }

    private boolean validateInput() {
        String ticker = tickerField.getText().trim();
        String name = nameField.getText().trim();
        Object selectedSector = sectorList.getSelectedItem();
        String sector = selectedSector == null ? "" : selectedSector.toString().trim();

        if (ticker.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ticker on pakollinen", "Virhe", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nimi on pakollinen", "Virhe", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (sector.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Toimiala on pakollinen", "Virhe", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(priceDividerField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Hintajakaja pitää olla numero", "Virhe", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    public String getTicker() {
        return tickerField.getText().trim();
    }

    public String getStockName() {
        return nameField.getText().trim();
    }

    public String getSector() {
        Object selected = sectorList.getSelectedItem();
        return selected == null ? "" : selected.toString().trim();
    }

    public String getStockType() {
        String selected = (String) typeList.getSelectedItem();
        return selected.contains("Optio") ? "O" : "S";
    }

    public String getCurrency() {
        return (String) currencyList.getSelectedItem();
    }

    public int getPriceDivider() {
        return Integer.parseInt(priceDividerField.getText().trim());
    }

    public String getCountry() {
        return countryField.getText().trim();
    }
}
