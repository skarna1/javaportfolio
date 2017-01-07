package com.stt.portfolio.gui;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

public class TransactionTable extends JTable {
	
	CellRenderer cellRendererRight = new CellRenderer(null,true, null);
	CellRenderer cellRendererRightSum = new CellRenderer(Color.LIGHT_GRAY,true, null);
	CellRenderer cellRendererLeft = new CellRenderer(null,false, null);

	public TransactionTable(AbstractTableModel model) {
		super(model);

		
	}

	public TableCellRenderer getCellRenderer(int row, int column) {

		
		if (row == getModel().getRowCount() -1 ){
			return cellRendererRightSum;
		}
		if (column >= 1 && column <= 3) {
			return cellRendererLeft;
		}
		return cellRendererRight;
	}

}
