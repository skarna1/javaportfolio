package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.stt.portfolio.Portfolio;

public class TaxReportPane extends JPanel implements ActionListener {

	private static final long serialVersionUID = -4279071363677616980L;
	
	
	private JTable table;
	private JTextField winsField;
	private JTextField lossesField;
	private JTextField totalField;
	private JScrollPane scrollPane = null;
	private JPanel top = null;

	private Portfolio portfolio;

	public final String[] columnNames = { "Arvo-osuus", "Määrä", "Hankinta-aika",
			"Luovutusaika", "Hankintahinta", "Hankintakulut", "Luovutushinta", 
			"Myyntikulut",  "AO-tili", "Hank.olett.",
			 "Voitto" };

	public TaxReportPane(Portfolio portfolio) {
		super(new BorderLayout());

		this.portfolio = portfolio;
		
		init();
	}

	public void init() {
		if (scrollPane != null) {
			remove(scrollPane);
		}
		if (top != null) {
			remove(top);
		}
		Integer[] taxYears = portfolio.getTaxReportYears();
		if (taxYears.length > 0) {
			JComboBox<Integer> yearList = new JComboBox<>(taxYears);
			yearList.setSelectedIndex(taxYears.length - 1);
			yearList.addActionListener(this);
			table = new TaxReportTable(null);
			winsField = new JTextField();
			lossesField = new JTextField();
			totalField = new JTextField();
			
			winsField.setEditable(false);
			lossesField.setEditable(false);
			totalField.setEditable(false);
			winsField.setColumns(9);
			lossesField.setColumns(9);
			totalField.setColumns(9);
			int selectedYear = taxYears[taxYears.length - 1];
			setYearData(selectedYear);

			// Lay out

			top = new JPanel(new FlowLayout());
			top.add(new JLabel("Verovuosi: "));
			top.add(yearList);
			top.add(new JLabel("Voitot:"));
			top.add(winsField);
			top.add(new JLabel("Tappiot:"));
			top.add(lossesField);
			top.add(new JLabel("Yhteensä"));
			top.add(totalField);
			add(top, BorderLayout.PAGE_START);

			table.setPreferredScrollableViewportSize(new Dimension(1000, 500));
			table.setFillsViewportHeight(true);
			table.setAutoCreateRowSorter(true);

			// Create the scroll pane and add the table to it.
			scrollPane = new JScrollPane(table);

			// Add the scroll pane to this panel.
			add(scrollPane);
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JComboBox<Integer> cb = (JComboBox<Integer>) e.getSource();
		Integer selectedYear = (Integer) cb.getSelectedItem();
		setYearData(selectedYear);

	}

	private void setYearData(int selectedYear) {
		Object[][] taxEntries = portfolio
				.getCombinedTaxReportTable(selectedYear);
		table.setModel(new PortfolioTableModel(taxEntries, columnNames));

		double profit = portfolio.getTaxProfitOrLoss(selectedYear, true);
		double loss = portfolio.getTaxProfitOrLoss(selectedYear, false);

		lossesField.setText(String.format("%1$.2f", loss));
		winsField.setText(String.format("%1$.2f", profit));
		totalField.setText(String.format("%1$.2f", profit + loss));
	}
}
