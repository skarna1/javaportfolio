package com.stt.portfolio.gui;

import java.awt.Dimension;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.stt.portfolio.Portfolio;

public class PortfolioTable extends JTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6188272625120545612L;

	public static final String[] columnNames = { "Arvo-osuus", "CCY",
			"Kurssi", "Noteerattu",
			"Määrä"
			// ,"Maksettu"
			, "Ostokurssi", "Ostettu", "Mark.arvo", "Mark.arvo EUR", "Hankintahinta", "Osingot",  "Tuotto",
			"Tuotto %", "P/A%", "%salkusta"
	// ,"Korkein"
	// ,"Alin"
	// ,"Vaihto"
	};

	TableCellRenderer cellRendererRedLeft = null;
	TableCellRenderer cellRendererGreenLeft = null;
	TableCellRenderer cellRendererRedRight = null;
	TableCellRenderer cellRendererGreenRight = null;
	TableCellRenderer cellRendererYellowLeft = null;
	TableCellRenderer cellRendererYellowRight = null;
	Portfolio portfolio;
	
	public PortfolioTable(Object[][] bookEntries, Portfolio portfolio) {
		super();

		cellRendererRedLeft = new CellRenderer(java.awt.Color.RED, false, null);
		cellRendererGreenLeft = new CellRenderer(java.awt.Color.GREEN, false, null);
		cellRendererRedRight = new CellRenderer(java.awt.Color.RED, true, null);
		cellRendererGreenRight = new CellRenderer(java.awt.Color.GREEN, true, null);
		cellRendererYellowLeft = new CellRenderer(java.awt.Color.YELLOW, false, null);
		cellRendererYellowRight = new CellRenderer(java.awt.Color.YELLOW, true, null);

		setPreferredScrollableViewportSize(new Dimension(1000, 500));
		setFillsViewportHeight(true);
		setAutoCreateRowSorter(true);

		changeContent(bookEntries);
		this.portfolio = portfolio;

	}

	public void changeContent(Object[][] bookEntries) {
		setModel(new PortfolioTableModel(bookEntries, columnNames));
		if (bookEntries.length > 0) {
			
			getRowSorter().toggleSortOrder(bookEntries[0].length - 3);
			getRowSorter().toggleSortOrder(bookEntries[0].length - 3);

			getColumnModel().getColumn(0).setPreferredWidth(100);
			getColumnModel().getColumn(1).setPreferredWidth(3);
			getColumnModel().getColumn(2).setPreferredWidth(50);
			
		}
		invalidate();
	}

	public TableCellRenderer getCellRenderer(int row, int column) {

		int modelRow = getRowSorter().convertRowIndexToModel(row);
		Double profitPercentage = (Double) getModel().getValueAt(modelRow, 12);
		String name = (String) getModel().getValueAt(modelRow, 0);
		name = name + " " + portfolio.getTicker(name);
		if (profitPercentage != null) {
			if (profitPercentage < -10.0) {
				return new CellRenderer(java.awt.Color.RED, (column != 0), name);
				
			} else if (profitPercentage < 10.0) {
				return new CellRenderer(java.awt.Color.YELLOW, (column != 0), name);
			}
		}
		// return super.getCellRenderer(row, column);
		return new CellRenderer(java.awt.Color.GREEN, (column != 0), (column == 0)?name:null);
	
	}

}
