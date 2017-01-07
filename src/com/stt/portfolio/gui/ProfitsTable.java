package com.stt.portfolio.gui;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class ProfitsTable extends JTable {
	
	private CellRenderer cellRendererRight;
	private CellRenderer cellRendererLeft;

	public ProfitsTable(AbstractTableModel model) {
		super(model);
		cellRendererRight = new CellRenderer(null,true, null);
		cellRendererLeft = new CellRenderer(null,false, null);
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column == 0) {
			return cellRendererLeft;
		}
		return cellRendererRight;
	}
}
