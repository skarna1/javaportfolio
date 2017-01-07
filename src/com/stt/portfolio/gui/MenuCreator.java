package com.stt.portfolio.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import com.stt.portfolio.PortfolioGuiApp;

public class MenuCreator implements ActionListener {

	// Transaction menu
	public final static String MENU_ITEM_BUY = "OSTO...";
	public final static String MENU_ITEM_SELL = "MYYNTI...";
	public final static String MENU_ITEM_DIVIDENT = "OSINKO...";
//	public final static String MENU_ITEM_SUBSCRIPTION = "MERKINTÄ...";
	public final static String MENU_ITEM_SUBSCRIPTION_OLD_OWNERSHIP = "MERKINTÄ...";
	public final static String MENU_ITEM_SUBSCRIPTION_OPTIONS = "MERKINTÄ MO/OPTIO...";
	public final static String MENU_ITEM_TAX = "PÄÄOMATULOVERO...";
	public final static String MENU_ITEM_TRANSFER = "SIJOITUSPÄÄOMAN SIIRTO...";
	public final static String MENU_ITEM_CAPITAL_REPAYMENT = "PÄÄOMAN PALAUTUS...";
	public final static String MENU_ITEM_ACCOUNT_TRANSFER = "ARVO-OSUUSTILIN SIIRTO...";
	
	// File menu
	public final static String MENU_ITEM_NEW_PORTFOLIO = "Uusi salkku...";
	public final static String MENU_ITEM_OPEN_PORTFOLIO = "Avaa salkku...";
	public final static String MENU_ITEM_IMPORT_PORTFOLIO = "Tuo salkku...";
	public final static String MENU_ITEM_QUIT = "Lopeta";
	
	
	// View menu
	public final static String MENU_ITEM_SHOW_PARTIAL = "Näytä hankintaerät";

	// Quotes menu

	public final static String MENU_ITEM_UPDATE_OMX_HEX = "Päivitä kurssit";
	public final static String MENU_ITEM_UPDATE_MANUAL = "Syötä noteeraus...";

	
	// Help menu
	public final static String MENU_ITEM_ABOUT = "Tietoja Portfolio ohjelmasta";

		
	

	
	PortfolioGuiApp app;
	JMenuBar menuBar;
	JMenu transactionMenu;
	JMenuItem openPortfolio;

	public MenuCreator(PortfolioGuiApp app) {

		this.app = app;
		menuBar = new JMenuBar();
	}

	public JMenuBar createMenu() {

		menuBar.add(createFileMenu());
		menuBar.add(createTransactionMenu());
		menuBar.add(createViewMenu());
		menuBar.add(createQuotesMenu());
		menuBar.add(createHelpMenu());

		return menuBar;
	}

	
	
	public JMenu createFileMenu() {

		// Build the file menu.
		JMenu fileMenu = new JMenu("Tiedosto");

		createMenuItem(fileMenu, MENU_ITEM_NEW_PORTFOLIO, "Luo uusi salkku");
		openPortfolio = createMenuItem(fileMenu, MENU_ITEM_OPEN_PORTFOLIO,
				"Avaa salkku");
		openPortfolio.setEnabled(false);

		createMenuItem(fileMenu, MENU_ITEM_IMPORT_PORTFOLIO,
				"Luo uusi salkku Portfolio-ohjelman tiedostosta");
		fileMenu.add(new JSeparator()); // SEPARATOR
		createMenuItem(fileMenu, MENU_ITEM_QUIT, "Lopeta ohjelma");

		return fileMenu;
	}

	public void showTransactionMenu() {
		transactionMenu.setVisible(true);
	}

	public void hideTransactionMenu() {
		transactionMenu.setVisible(false);
	}

	public JMenu createTransactionMenu() {
		// Build the transaction menu.
		transactionMenu = new JMenu("Tapahtumat");

		createMenuItem(transactionMenu, MENU_ITEM_BUY, "Kirjaa ostotapahtuma");
		createMenuItem(transactionMenu, MENU_ITEM_SELL,
				"Kirjaa myyntitapahtuma");
		createMenuItem(transactionMenu, MENU_ITEM_DIVIDENT, "Kirjaa osinko");
//		createMenuItem(transactionMenu, MENU_ITEM_SUBSCRIPTION,
//				"Kirjaa merkintä");
		createMenuItem(transactionMenu, MENU_ITEM_SUBSCRIPTION_OLD_OWNERSHIP,
		"Kirjaa merkintä osakeannissa");
		
		createMenuItem(transactionMenu, MENU_ITEM_SUBSCRIPTION_OPTIONS,
		"Kirjaa merkintä optioilla");
		
		createMenuItem(transactionMenu, MENU_ITEM_TAX,
		"Kirjaa pääomatulovero");
		
		createMenuItem(transactionMenu, MENU_ITEM_TRANSFER,
		"Kirjaa sijoituspääoman siirto");
		
		createMenuItem(transactionMenu, MENU_ITEM_CAPITAL_REPAYMENT,
		"Kirjaa pääoman palautus");
		
		createMenuItem(transactionMenu, MENU_ITEM_ACCOUNT_TRANSFER,
		"Arvo-osuustilin siirto");
		
		transactionMenu.setVisible(false);
		return transactionMenu;
	}

	public JMenu createQuotesMenu() {
		JMenu quotesMenu = new JMenu("Kurssit");

		createMenuItem(quotesMenu, MENU_ITEM_UPDATE_OMX_HEX, "Lue kurssit");
		createMenuItem(quotesMenu, MENU_ITEM_UPDATE_MANUAL, "Syötä noteeraus");
		return quotesMenu;
	}

	public JMenu createViewMenu() {
		JMenu viewMenu = new JMenu("Näkymä");
		JMenuItem item;
		viewMenu.add(item = new JCheckBoxMenuItem(MENU_ITEM_SHOW_PARTIAL));
	    item.addActionListener(this);
	    
	    return viewMenu;
	}
	
	public JMenu createHelpMenu() {
		JMenu helpMenu = new JMenu("Ohje");

		createMenuItem(helpMenu, MENU_ITEM_ABOUT, "Tietoja ohjelmasta");
		
		return helpMenu;
	}

	public JMenuItem createMenuItem(JMenu menu, String name, String description) {
		// a group of JMenuItems
		JMenuItem menuItem = new JMenuItem(name);

		menuItem.getAccessibleContext().setAccessibleDescription(description);
		menuItem.addActionListener(this);

		menu.add(menuItem);
		return menuItem;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JMenuItem item = (JMenuItem) (e.getSource());

		app.handleMenuCommand(item);

	}

	public void enableOpenPortfolio() {
		openPortfolio.setEnabled(true);

	}

}
