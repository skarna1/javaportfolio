package com.stt.portfolio;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import com.stt.portfolio.gui.MenuCreator;
import com.stt.portfolio.gui.NewPortfolioDialog;
import com.stt.portfolio.gui.OpenPortfolioDialog;
import com.stt.portfolioupdater.HTTPQuoteFetcher;
import com.stt.portfolioupdater.Item;
import com.stt.portfolioupdater.Updater;

/**
 * The Class PortfolioGuiApp.
 */
public class PortfolioGuiApp {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 4144168906275338094L;

	/** The portfoliodocument. */
	PortfolioDocument portfolioDocument = null;

	/** The frame. */
	JFrame frame;

	/** The menu. */
	MenuCreator menu;

	List<HTTPQuoteFetcher> quoteUpdaters = new ArrayList<HTTPQuoteFetcher>();
	private boolean quoteUpdateRunning;

	/**
	 * Instantiates a new portfolio gui app.
	 */
	public PortfolioGuiApp() {

		frame = new JFrame();
		frame.setSize(1100, 750);

		menu = new MenuCreator(this);
		frame.setJMenuBar(menu.createMenu());

		try {
			Map<String, String> portfolios = PortfolioDocument
					.readPorfolioNames();
			if (portfolios.size() > 0) {
				menu.enableOpenPortfolio();
			}

			String lastPortfolio = readPortfolioName();
			if (lastPortfolio != null) {
				openPortfolio(portfolios, lastPortfolio);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Display the window.
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.pack();
		frame.setVisible(true);

		localiseFileChooser();
		createUpdaters();

	}

	private void openPortfolio(Map<String, String> portfolios,
			String portfolioName) {
		portfolioDocument = new PortfolioDocument(frame,
				portfolios.get(portfolioName));
		menu.showTransactionMenu();
		frame.setTitle(portfolioName);
	}

	private void localiseFileChooser() {
		UIManager.put("FileChooser.openDialogTitleText", "Avaa");
		UIManager.put("FileChooser.fileNameLabelText", "Tiedoston nimi");
		UIManager.put("FileChooser.filesOfTypeLabelText", "Tiedoston tyyppi");
		UIManager
				.put("FileChooser.acceptAllFileFilterText", "Kaikki tiedostot");
		UIManager.put("FileChooser.openButtonText", "Avaa");
		UIManager.put("FileChooser.cancelButtonText", "Peru");
		UIManager.put("FileChooser.lookInLabelText", "Sijainti");
		UIManager.put("FileChooser.fileSizeHeaderText", "Koko");
		UIManager.put("FileChooser.fileNameHeaderText", "Nimi");
		UIManager.put("FileChooser.fileDateHeaderText", "Muutettu");
		UIManager.put("FileChooser.detailsViewButtonToolTipText", "Tiedot");
		UIManager.put("FileChooser.listViewButtonToolTipText", "Lista");
		UIManager.put("FileChooser.newFolderToolTipText", "Tee uusi kansio");
		UIManager.put("FileChooser.homeFolderToolTipText", "Koti");
		UIManager.put("FileChooser.upFolderToolTipText", "Siirry ylös");
	}

	/**
	 * Handle import portfolio.
	 */
	public void handleImportPortfolio() {
		File file = PortfolioDocument.selectPortfolioDirectory(frame);
		if (file != null) {
			try {
				String name = PortfolioDocument.importPortfolio(file);
				if (name != null) {
					portfolioDocument = new PortfolioDocument(frame, name);
					menu.showTransactionMenu();
					menu.enableOpenPortfolio();
				} else {
					JOptionPane.showMessageDialog(frame, "Tuonti epäonnistui");
				}
			} catch (IOException e) {
				//
				e.printStackTrace();
				JOptionPane.showMessageDialog(frame, "Tuonti epäonnistui");
			}

		}

	}

	/**
	 * Handle open portfolio.
	 */
	public void handleOpenPortfolio() {

		try {
			Map<String, String> portfolios = PortfolioDocument
					.readPorfolioNames();
			OpenPortfolioDialog d = new OpenPortfolioDialog(frame, frame,
					"Avaa salkku", portfolios.keySet().toArray());

			if (d.isOk()) {
				String info = d.getInfo();

				openPortfolio(portfolios, info);
				savePortfolioName(info);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Handle new portfolio.
	 */
	public void handleNewPortfolio() {
		NewPortfolioDialog d = new NewPortfolioDialog(frame, frame,
				"Uusi salkku");
		if (d.isOk()) {
			portfolioDocument = new PortfolioDocument(frame);
			portfolioDocument.setInfo(d.getInfo());
			menu.showTransactionMenu();
			menu.enableOpenPortfolio();
		}

	}

	/**
	 * Handle menu command.
	 *
	 * @param item
	 *            the item
	 */
	public void handleMenuCommand(JMenuItem item) {
		if (item.getText().equals(MenuCreator.MENU_ITEM_NEW_PORTFOLIO)) {
			handleNewPortfolio();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_OPEN_PORTFOLIO)) {
			handleOpenPortfolio();
		} else if (item.getText()
				.equals(MenuCreator.MENU_ITEM_IMPORT_PORTFOLIO)) {
			handleImportPortfolio();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_QUIT)) {
			System.exit(0);
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_UPDATE_OMX_HEX)) {
			updateQuotes();
		} else if (item.getText().equals(MenuCreator.MENU_ITEM_ABOUT)) {
			handleAbout();
		} else {
			if (portfolioDocument != null) {
				portfolioDocument.handleMenuCommand(item);
			}
		}
	}

	private void createUpdaters() {
		try {
			Properties props = new Properties();
			props.load(new FileInputStream("etc/updaters.properties"));
			for (int i = 1; i < 100; ++i) {
				String uri = props.getProperty("uri_" + i);
				String xpath = props.getProperty("xpath_" + i);
				String className = props.getProperty("class_" + i);
				String ignoreVolumeStr = props.getProperty("ignorevolume_" + i);
				String name = props.getProperty("name_" + i);
				String ticker = props.getProperty("ticker_" + i);
				if (uri == null || xpath == null || className == null) {
					break;
				}

				boolean ignoreVolume = ignoreVolumeStr != null ? ignoreVolumeStr
						.equalsIgnoreCase("true") : false;

				try {
					Class cl = Class.forName(className);
					Constructor[] ctors = cl.getDeclaredConstructors();
					for (Constructor ctor : ctors) {
						if (ctor.getGenericParameterTypes().length == 0) {
							HTTPQuoteFetcher fetcher;
							try {
								fetcher = (HTTPQuoteFetcher) ctor.newInstance();
								fetcher.setUri(uri);
								fetcher.setXpath(xpath);
								fetcher.setIgnoreVolume(ignoreVolume);
								fetcher.init();
								if (name != null) {
									fetcher.setName(name);
								}
								if (ticker != null) {
									fetcher.setTicker(ticker);
								}
								quoteUpdaters.add(fetcher);
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

							break;
						}
					}


				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void updateQuotes() {
		if (quoteUpdateRunning) {
			return;
		}
		quoteUpdateRunning = true;

		JLabel status = new JLabel("Haetaan ja tallennetaan kurssitietoja...");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		JDialog progressDialog = new JDialog(frame, "Päivitetään kursseja", false);
		JPanel progressPanel = new JPanel(new BorderLayout(0, 8));
		progressPanel.add(status, BorderLayout.NORTH);
		progressPanel.add(new JLabel("Lähteiden tulokset näytetään päivityksen valmistuttua."),
				BorderLayout.CENTER);
		progressPanel.add(progressBar, BorderLayout.SOUTH);
		progressPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		progressDialog.setContentPane(progressPanel);
		progressDialog.setPreferredSize(new Dimension(500, 120));
		progressDialog.pack();
		progressDialog.setLocationRelativeTo(frame);

		SwingWorker<Map<String, Boolean>, Void> worker =
				new SwingWorker<Map<String, Boolean>, Void>() {
			@Override
			protected Map<String, Boolean> doInBackground() {
				String path = "etc/kurssidata/";
				Map<String, Boolean> fetchedUrls = new HashMap<String, Boolean>();
				Updater updater = new Updater();

				for (HTTPQuoteFetcher fetcher : quoteUpdaters) {
					String uri = fetcher.getUri();
					System.out.println("Fetching data from: " + uri);
					try {
						List<Item> items = fetcher.parseHtml();
						if (items == null || items.isEmpty()) {
							System.out.println("update failed: " + uri);
							fetchedUrls.put(uri, false);
							continue;
						}
						updater.writeToFiles(path, items, fetcher.isIgnoreVolume());
						fetchedUrls.put(uri, true);
					} catch (RuntimeException e) {
						System.err.println("Quote update failed for " + uri);
						e.printStackTrace();
						fetchedUrls.put(uri, false);
					}
				}
				return fetchedUrls;
			}

			@Override
			protected void done() {
				progressDialog.dispose();
				try {
					Map<String, Boolean> fetchedUrls = get();
					showQuoteUpdateResults(fetchedUrls);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					JOptionPane.showMessageDialog(frame,
							"Kurssitietojen päivitys keskeytettiin: " + e.getMessage(),
							"Päivitys epäonnistui", JOptionPane.ERROR_MESSAGE);
				} catch (ExecutionException e) {
					Throwable cause = e.getCause() != null ? e.getCause() : e;
					JOptionPane.showMessageDialog(frame,
							"Kurssitietojen päivitys epäonnistui: " + cause.getMessage(),
							"Päivitys epäonnistui", JOptionPane.ERROR_MESSAGE);
				} finally {
					quoteUpdateRunning = false;
					if (portfolioDocument != null) {
						portfolioDocument.redraw();
					}
				}
			}
		};
		progressDialog.setVisible(true);
		worker.execute();
	}

	private void showQuoteUpdateResults(Map<String, Boolean> fetchedUrls) {
		JPanel results = new JPanel();
		results.setLayout(new javax.swing.BoxLayout(results, javax.swing.BoxLayout.Y_AXIS));
		for (HTTPQuoteFetcher fetcher : quoteUpdaters) {
			String uri = fetcher.getUri();
			boolean succeeded = Boolean.TRUE.equals(fetchedUrls.get(uri));
			JLabel result = new JLabel((succeeded ? "✓ " : "✗ ") + uri);
			result.setForeground(succeeded ? new Color(0, 128, 0) : Color.RED);
			result.setToolTipText(uri);
			result.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
			results.add(result);
		}

		JScrollPane scrollPane = new JScrollPane(results);
		scrollPane.setPreferredSize(new Dimension(850, Math.min(400,
				Math.max(100, quoteUpdaters.size() * 32))));
		JOptionPane.showMessageDialog(frame, scrollPane,
				"Kurssitietojen päivityksen tulokset", JOptionPane.INFORMATION_MESSAGE);
	}

	/**
	 * Save portfolio name. Saves the name of the last portfolio into
	 * etc/salkku.ini file.
	 *
	 * @param info
	 *            the name of the portfolio
	 */
	private void savePortfolioName(String info) {

		OutputStream os;
		try {
			os = new FileOutputStream("etc/salkku.ini", false);
			OutputStreamWriter ow = new OutputStreamWriter(os, "ISO8859_1");

			String line = info + "\r\n";

			ow.write(line);
			ow.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private String readPortfolioName() {
		String filename = "etc/salkku.ini";
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					filename), "ISO8859_1"));
			String str = in.readLine();
			in.close();
			return str;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}

		return null;
	}

	private void handleAbout() {

	}

	/**
	 * The main method.
	 *
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		// Schedule a job for the event-dispatching thread:
		// creating and showing this application's GUI.
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new PortfolioGuiApp();
			}
		});

	}

}
