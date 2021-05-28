package com.stt.portfolio;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import com.stt.portfolio.gui.AccountTransferDialog;
import com.stt.portfolio.gui.BuyDialog;
import com.stt.portfolio.gui.CapitalRepaymentDialog;
import com.stt.portfolio.gui.DividendDialog;
import com.stt.portfolio.gui.MenuCreator;
import com.stt.portfolio.gui.PortfolioView;
import com.stt.portfolio.gui.SellDialog;
import com.stt.portfolio.gui.SubscribeOldOwnershipDialog;
import com.stt.portfolio.gui.SubscribeOptionDialog;
import com.stt.portfolio.gui.SubscriptionDialog;
import com.stt.portfolio.gui.TaxDialog;
import com.stt.portfolio.gui.TransferDialog;
import com.stt.portfolio.gui.UpdateQuoteManuallyDialog;
import com.stt.portfolio.transactions.Buy;
import com.stt.portfolio.transactions.CapitalRepayment;
import com.stt.portfolio.transactions.Dividend;
import com.stt.portfolio.transactions.DividendTax;
import com.stt.portfolio.transactions.OptionSubscription;
import com.stt.portfolio.transactions.RightsSubscription;
import com.stt.portfolio.transactions.Sell;
import com.stt.portfolio.transactions.Subscription;
import com.stt.portfolio.transactions.Tax;
import com.stt.portfolio.transactions.Transaction;
import com.stt.portfolio.transactions.Transfer;
import com.stt.portfolio.transactions.TransferAccount;
import com.stt.portfolioupdater.Item;
import com.stt.portfolioupdater.Updater;

public class PortfolioDocument {

	public static final String PORTFOLIO_PATH = "etc/salkut/";

	/** The name of the portfolio */
	private String name;

	/** The public name of the portfolio. Stored in info.txt */
	private String info;

	private Portfolio portfolio;

	/** The portfolio view. */
	private PortfolioView portfolioView;

	/** The ticker manager. */
	private I_TickerManager tickerManager = TickerManager.createTickerManager();

	private JFrame frame;

	public PortfolioDocument(JFrame frame) {

		String name = getNewPortfolioName();

		createPortfolioFiles(PORTFOLIO_PATH + name);
		init(frame, name);
	}

	public PortfolioDocument(JFrame frame, String name) {
		init(frame, name);
	}

	private void init(JFrame frame, String name) {
		this.frame = frame;
		this.name = name;
		Date date = Calendar.getInstance().getTime();
		portfolio = PortfolioFactory.createPortfolio(name, date, tickerManager);
		portfolioView = new PortfolioView(this, portfolio, frame);
	}

	public Portfolio getPortfolio() {
		return portfolio;
	}

	public void redraw() {
		portfolio.updateQuotes();
		portfolioView.redraw();
	}

	private void createPortfolioFiles(String path) {
		File folder = new File(path);
		if (folder.mkdir()) {
			File transactions = new File(path + "/tapahtumat.csv");
			File info = new File(path + "/info.txt");
			try {
				transactions.createNewFile();
				info.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Gets the new available portfolio name.
	 *
	 * @return the new portfolio name
	 */
	private static String getNewPortfolioName() {

		int nbr = -1;
		File file = null;

		do {
			nbr++;
			file = new File(PORTFOLIO_PATH + "salkku" + nbr);

		} while (file.exists());

		return "salkku" + nbr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Write transaction.
	 *
	 * @param t
	 *            the t
	 */
	public void writeTransaction(Transaction t) {
		portfolio.writeTransaction(t);
		portfolio.addTransaction(t);
		portfolio.process();
	}

	/**
	 * Read porfolio names.
	 *
	 * @return the map< string, string>
	 *
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public static Map<String, String> readPorfolioNames() throws IOException {
		Map<String, String> portfolios = new HashMap<String, String>();

		File folder = new File(PORTFOLIO_PATH);
		File[] files = folder.listFiles();
		for (File file : files) {
			File infoFile = new File(file, "info.txt");
			String filename = infoFile.getCanonicalPath();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					new FileInputStream(filename), "ISO8859_1"));


			String str = in.readLine();
			in.close();
			portfolios.put(str, file.getName());
		}

		return portfolios;
	}

	public static File selectPortfolioDirectory(JFrame frame) {

		// Create a file chooser
		JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.setLocale(new Locale("fi", "FI"));
		int returnVal = fc.showOpenDialog(frame);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			return file;
		}
		return null;
	}

	public static String importPortfolio(File folder) throws IOException {
		String name = getNewPortfolioName();

		File[] files = folder.listFiles(new PortfolioImportFilter());
		if (files.length < 2) {
			return null;
		}

		File newfolder = new File(PORTFOLIO_PATH + name);
		if (newfolder.mkdir()) {
			for (File fromFile : files) {

				File toFile = new File(PORTFOLIO_PATH + name + "/"
						+ fromFile.getName());

				if (!fromFile.exists()) {
					newfolder.delete();
					throw new IOException("FileCopy: "
							+ "no such source file: "
							+ fromFile.getCanonicalPath());
				}
				if (!fromFile.isFile()) {
					newfolder.delete();
					throw new IOException("FileCopy: "
							+ "can't copy directory: "
							+ fromFile.getCanonicalPath());
				}
				if (!fromFile.canRead()) {
					newfolder.delete();
					throw new IOException("FileCopy: "
							+ "source file is unreadable: "
							+ fromFile.getCanonicalPath());
				}
				FileInputStream from = null;
				FileOutputStream to = null;
				try {
					from = new FileInputStream(fromFile);
					to = new FileOutputStream(toFile);
					byte[] buffer = new byte[4096];
					int bytesRead;

					while ((bytesRead = from.read(buffer)) != -1)
						to.write(buffer, 0, bytesRead); // write
				} finally {
					if (from != null)
						try {
							from.close();
						} catch (IOException e) {
							;
						}
					if (to != null)
						try {
							to.close();
						} catch (IOException e) {
							;
						}
				}
			}
		} else {
			throw new IOException("FileCopy: " + "can't create directory: "
					+ name);
		}
		return name;

	}

	public void handleBuy(Transaction t) {
		Stock s = getSelectedStock();
		Object[] brokers = portfolio.getBrokers();
		BuyDialog d = new BuyDialog(frame, frame, "Osto", brokers,
				portfolio.getSectors(), portfolio.getStocksBySector(),
				tickerManager, s);
		if (d.isOk()) {

			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(d.getBrokerCost());
			t.setCost(-d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(tickerManager.getTicker(t.getName()));

			t.setRate(d.getRate());

			t.setDecimals(tickerManager.getDecimals(t.getTicker()));
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleSubscriptionOptions() {
		OptionSubscription t = new OptionSubscription();
		Object[] brokers = portfolio.getBrokers();

		SubscribeOptionDialog d = new SubscribeOptionDialog(frame, frame,
				"Merkintä", brokers, portfolio.getSectors(),
				portfolio.getStocksBySector(), tickerManager,
				portfolio.getBookEntries());

		if (d.isOk()) {

			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(d.getBrokerCost());
			t.setCost(-d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(tickerManager.getTicker(t.getName()));

			t.setRate(d.getRate());
			t.setSubscriptionRatio(d.getRatio());
			t.setOptionTicker(tickerManager.getTicker(d.getOptionName()));
			t.setDecimals(tickerManager.getDecimals(t.getTicker()));
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleSubscription() {
		Subscription t = new Subscription();
		Object[] brokers = portfolio.getBrokers();
		SubscriptionDialog d = new SubscriptionDialog(frame, frame,
				"Merkintä", brokers, portfolio.getSectors(),
				portfolio.getStocksBySector(), tickerManager);
		if (d.isOk()) {

			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(0.0);
			t.setCost(-d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(portfolio.getTicker(t.getName()));
			t.setTaxPurchaseDate(d.getTaxPurchaseDate());
			t.setRate(d.getRate());
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleSell() {

		SellDialog d = new SellDialog(frame, frame, "Myynti",
				portfolio.getBookEntries(), tickerManager);
		if (d.isOk()) {
			Transaction t = new Sell();
			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(d.getBrokerCost());
			t.setCost(d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(portfolio.getTicker(t.getName()));
			t.setRate(d.getRate());

			t.setDecimals(tickerManager.getDecimals(t.getTicker()));

			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	private void handleSubscriptionOldOwnership(RightsSubscription t) {

		SubscribeOldOwnershipDialog d = new SubscribeOldOwnershipDialog(frame,
				frame, "Merkintä annissa", portfolio.getBrokers(),
				portfolio.getBookEntries(), tickerManager);
		if (d.isOk()) {
			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(0.0);
			t.setCost(-d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(portfolio.getTicker(t.getName()));

			t.setAmountOfOldOwnership(Math.floor(d.getAmount() / d.getRatio()));
			t.setRatio(d.getRatio());
			t.setRate(d.getRate());

			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleDivident() {

		Stock s = getSelectedStock();

		DividendDialog d = new DividendDialog(frame, frame, "Osinko",
				portfolio.getSectors(), portfolio.getStocksBySector(),
				portfolio.getBookEntries(),
				tickerManager, s);
		if (d.isOk()) {
			Transaction t = new Dividend();
			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(0.0);
			t.setCost(d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(portfolio.getTicker(t.getName()));
			t.setRate(d.getRate());
			writeTransaction(t);

			if (d.getDividendTax() > 0.0) {
				Transaction t1 = new DividendTax();
				t1.setAmount(1);
				t1.setBroker(d.getBrokerName());
				t1.setBrokerCost(0.0);
				t1.setCost(-d.getDividendTax());
				t1.setDate(d.getTransactionDate());
				t1.setPrice(d.getDividendTax());
				t1.setName(d.getStockName());
				t1.setTicker(t.getTicker());
				t1.setRate(d.getRate());
				writeTransaction(t1);
			}
			portfolioView.redraw();
		}
	}

	private Stock getSelectedStock() {
		String selectedStock = portfolioView.getSelectedStock();
		Stock s = null;
		if (selectedStock != null) {
			s = portfolio.getStock(selectedStock);
		}
		return s;
	}

	public void handleTax() {

		TaxDialog d = new TaxDialog(frame, frame, "Vero", tickerManager);
		if (d.isOk()) {
			Transaction t = new Tax();
			t.setAmount(1);
			t.setBroker("VER");
			t.setBrokerCost(0.0);
			t.setCost(-d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getTotalCost());
			t.setName("Pääomatulovero");
			t.setTicker("POTVERO");
			t.setRate(d.getRate());
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleTransfer() {

		TransferDialog d = new TransferDialog(frame, frame, "Siirto",
				tickerManager);
		if (d.isOk()) {
			Transaction t = new Transfer();
			t.setAmount(1);
			t.setBroker("SRT");
			t.setBrokerCost(0.0);
			t.setCost(-d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getTotalCost());
			t.setName("Sij. pääoman siirto");
			t.setTicker("SIPOSIIRTO");
			t.setRate(d.getRate());
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleCapitalRepayment() {

		Object[] brokers = portfolio.getBrokers();
		CapitalRepaymentDialog d = new CapitalRepaymentDialog(frame, frame,
				"Pääoman palautus", brokers, portfolio.getSectors(),
				portfolio.getStocksBySector(), tickerManager);
		if (d.isOk()) {
			Transaction t = new CapitalRepayment();
			t.setAmount(d.getAmount());
			t.setBroker(d.getBrokerName());
			t.setBrokerCost(0.0);
			t.setCost(d.getTotalCost());
			t.setDate(d.getTransactionDate());
			t.setPrice(d.getCost());
			t.setName(d.getStockName());
			t.setTicker(portfolio.getTicker(t.getName()));
			t.setRate(d.getRate());
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void handleAccountTransfer() {

		Object[] brokers = portfolio.getBrokers();
		AccountTransferDialog d = new AccountTransferDialog(frame, frame,
				"Arvo-osuustilin siirto", brokers, tickerManager);
		if (d.isOk()) {
			TransferAccount t = new TransferAccount();
			t.setAmount(0.0);
			t.setOldBroker(d.getBroker());
			t.setBroker(d.getNewBroker());
			t.setBrokerCost(0.0);
			t.setCost(0.0);
			t.setDate(d.getTransactionDate());
			t.setPrice(0.0);
			t.setName("");
			t.setTicker("");
			t.setRate(1.0);
			writeTransaction(t);
			portfolioView.redraw();
		}
	}

	public void updateQuoteManually() {
		String selectedStock = portfolioView.getSelectedStock();

		String ticker = portfolio.getTicker(selectedStock);
		Stock s = portfolio.getStock(selectedStock);
		if (s == null) {
			System.out.println("ERROR: " + selectedStock + " " + ticker
					+ " not found!");
			return;
		}

		if (selectedStock != null) {

			UpdateQuoteManuallyDialog d = new UpdateQuoteManuallyDialog(frame,
					frame, "Päivitä noteeraus", selectedStock, ticker,
					tickerManager);
			if (d.isOk()) {

				double price = d.getPrice();

				Updater updater = new Updater();
				Item item = new Item();
				item.setHigh(price);
				item.setLow(price);
				item.setLast(price);
				item.setName(selectedStock);
				item.setTicker(ticker);
				item.setVolume(0);
				item.setGivenDate(d.getTransactionDate());
				item.setRate(d.getRate());
				item.setDecimals(tickerManager.getDecimals(ticker));
				String filename = "etc/kurssidata/" + item.getTicker() + ".csv";
				try {
					updater.writeToFile(filename, item);
					redraw();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					JOptionPane.showMessageDialog(frame,
							"Päivitys epäonnistui");
				}
			}
		} else {
			JOptionPane.showMessageDialog(frame,
					"Valitse ensin arvo-osuus taulukosta");
		}
	}

	public void handleMenuCommand(JMenuItem item) {
		if (item.getText().equals(MenuCreator.MENU_ITEM_BUY)) {
			handleBuy(new Buy());
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_SELL)) {
			handleSell();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_DIVIDENT)) {
			handleDivident();
			// } else if
			// (item.getText().equals(MenuCreator.MENU_ITEM_SUBSCRIPTION)) {
			// handleSubscription();
		} else if (item.getText().equals(
				MenuCreator.MENU_ITEM_SUBSCRIPTION_OLD_OWNERSHIP)) {
			handleSubscriptionOldOwnership(new RightsSubscription());
		} else if (item.getText().equals(
				MenuCreator.MENU_ITEM_SUBSCRIPTION_OPTIONS)) {
			handleSubscriptionOptions();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_TAX)) {
			handleTax();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_TRANSFER)) {
			handleTransfer();
		} else if (item.getText().equals(
				MenuCreator.MENU_ITEM_CAPITAL_REPAYMENT)) {
			handleCapitalRepayment();
		} else if (item.getText()
				.equals(MenuCreator.MENU_ITEM_ACCOUNT_TRANSFER)) {
			handleAccountTransfer();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_UPDATE_MANUAL)) {
			updateQuoteManually();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_SHOW_PARTIAL)) {

			handleToggleShowPartial(item.isSelected());
		}
	}

	private void handleToggleShowPartial(boolean isSelected) {
		portfolioView.toggleShowPartial(isSelected);
	}

	public void setInfo(String info) {
		this.info = info;

		String filename = PORTFOLIO_PATH + name + "/info.txt";
		OutputStream os;
		try {
			os = new FileOutputStream(filename, false);
			OutputStreamWriter ow = new OutputStreamWriter(os, "ISO8859_1");

			String line = info + "\r\n";

			ow.write(line);
			ow.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		frame.setTitle(info);
	}

	public void setPortfolioDate(Date date) {
		portfolio = PortfolioFactory.createPortfolio(name, date, tickerManager);
		portfolioView.setPortfolio(portfolio);
		redraw();
	}

}
