package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import com.stt.portfolio.Portfolio;
import com.stt.portfolio.PortfolioDocument;
import com.toedter.calendar.JDateChooser;

public class PortfolioView extends JPanel implements ActionListener, PropertyChangeListener {
	private PortfolioDocument portfolioDoc;
	private PortfolioTable bookEntryPane;
	private Portfolio portfolio;
	private TaxReportPane taxReportPane;
	private TransactionPane transactionPane;
	private ProfitsPane profitsPane;
	private MonthlyProfitsPane monthlyProfitsPane;

	private JFrame frame;
	private JTextField portfolioValueField;
	private JTextField portfolioCashField;
	private JTextField portfolioInvestedField;
	private JTextField portfolioXirrField;
	private BrokerList brokerList = null;
	private JDateChooser dateChooser;
	private boolean showItems = false;


	public PortfolioView(PortfolioDocument portfolioDoc, Portfolio portfolio, JFrame frame) {
		super(new BorderLayout());
		this.frame = frame;
		this.portfolioDoc = portfolioDoc;
		this.portfolio = portfolio;
		portfolioValueField = new JTextField();
		portfolioCashField = new JTextField();
		portfolioInvestedField = new JTextField();
		portfolioXirrField = new JTextField();

		frame.getContentPane().removeAll();

		FlowLayout flowLayout = new FlowLayout();

		Object[][] bookEntries = portfolio.getCombinedBookEntryTable(showItems);

		Object[] brokerNames = portfolio.getBookEntryBrokers();
		brokerList = new BrokerList(brokerNames, this);

		dateChooser = new JDateChooser(Calendar.getInstance().getTime());
		dateChooser.setLocale(new Locale("fi", "FI"));
		dateChooser.addPropertyChangeListener(this);

		JPanel top = new JPanel(flowLayout);

		top.add(dateChooser);

		top.add(new JLabel("AO-tili: "));
		top.add(brokerList.getBrokerList());

		top.add(new JLabel("Osakkeet: "));
		top.add(portfolioValueField);

		top.add(new JLabel("Käteinen: "));
		top.add(portfolioCashField);

		top.add(new JLabel("Sijoitettu: "));
		top.add(portfolioInvestedField);

		top.add(new JLabel("P/A%: "));
		top.add(portfolioXirrField);

		portfolioValueField.setEditable(false);
		portfolioCashField.setEditable(false);
		portfolioInvestedField.setEditable(false);
		portfolioXirrField.setEditable(false);

		updateTextFields();

		add(top, BorderLayout.PAGE_START);

		bookEntryPane = new PortfolioTable(bookEntries, portfolio);
		// PortfolioModel model = new
		// PortfolioModel(portfolio.getCombinedBookEntryList());
		// bookEntryPane = new JTreeTable((model));
		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(bookEntryPane);

		// Add the scroll pane to this panel.
		add(scrollPane);

		taxReportPane = new TaxReportPane(portfolio);
		transactionPane = new TransactionPane(portfolio);
		profitsPane = new ProfitsPane(portfolio);
		monthlyProfitsPane = new MonthlyProfitsPane(portfolio);

		JTabbedPane jtp = new JTabbedPane();
		frame.getContentPane().add(jtp);
		jtp.addTab("Yhteenveto", this);
		jtp.addTab("Arvopaperijakauma", new PieChartPane(portfolio));
		jtp.addTab("Maajakauma", new CountryAllocationPane(portfolio));
		jtp.addTab("Tapahtumat", transactionPane);
		jtp.addTab("Luovutusvoitot", taxReportPane);
		jtp.addTab("Vuosituotot", profitsPane);
		jtp.addTab("Kuukausimuutokset", monthlyProfitsPane);
		setOpaque(true); // content panes must be opaque
	}

	private void updateTextFields() {

		if (brokerList.getBrokerList().getSelectedIndex() == 0) {
			portfolioValueField.setText(String.format("%1$.2f", portfolio.getStocksValue()));

			portfolioCashField.setText(String.format("%1$.2f", portfolio.getCash()));
			portfolioInvestedField.setText(String.format("%1$.2f", portfolio.getInvested()));
			portfolioXirrField.setText(String.format("%1$.2f", portfolio.getXirr()));
		} else {
			String broker = brokerList.getSelectedBroker();
			portfolioValueField.setText(String.format("%1$.2f", portfolio.getStocksValue(broker)));
			portfolioCashField.setText("N/A");
			portfolioInvestedField.setText("N/A");
			portfolioXirrField.setText("N/A");
		}
	}

	public void redraw() {
		Object[][] bookEntries = getBookEntries(showItems);
		bookEntryPane.changeContent(bookEntries);

		updateTextFields();

		taxReportPane.init();
		taxReportPane.invalidate();

		transactionPane.update();
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setFrame(JFrame frame) {
		this.frame = frame;
	}

	public String getSelectedStock() {

		int row = bookEntryPane.getSelectedRow();
		if (row != -1) {
			int modelRow = bookEntryPane.getRowSorter().convertRowIndexToModel(row);
			String stockName = (String) bookEntryPane.getModel().getValueAt(modelRow, 0);
			return stockName;
		}
		return null;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("broker")) {
			brokerList.updateSelectedBroker();
			Object[][] bookEntries = getBookEntries(showItems);
			bookEntryPane.changeContent(bookEntries);
			updateTextFields();
			validate();
		}
	}

	private Object[][] getBookEntries(boolean showItems) {
		Object[][] bookEntries = null;

		if (brokerList.getBrokerList().getSelectedIndex() == 0) {
			bookEntries = portfolio.getCombinedBookEntryTable(showItems);
		} else {
			bookEntries = portfolio.getBookEntryTable(brokerList.getSelectedBroker(), showItems);
		}
		return bookEntries;
	}

	public void toggleShowPartial(boolean isSelected) {
		if (isSelected != showItems) {
			showItems = isSelected;
			Object[][] bookEntries = getBookEntries(isSelected);
			bookEntryPane.changeContent(bookEntries);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {

		if (evt.getPropertyName().equals("date")) {
			// System.out.println((Date)evt.getNewValue());
			portfolioDoc.setPortfolioDate((Date) evt.getNewValue());
		}
	}

	public void setPortfolio(Portfolio portfolio) {
		this.portfolio = portfolio;
		
	}
}
