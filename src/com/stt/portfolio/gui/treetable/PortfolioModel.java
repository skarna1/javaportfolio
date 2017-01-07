package com.stt.portfolio.gui.treetable;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import com.stt.portfolio.BookEntry;
import com.stt.portfolio.BookEntryItem;

public class PortfolioModel extends AbstractTreeTableModel implements
		TreeTableModel {

	private boolean DEBUG = false;

	private Object[][] data;

	

	public static final String[] columnNames = { "Arvo-osuus", "CCY",
			"Kurssi",
			"Noteerattu",
			"Määrä"
			// ,"Maksettu"
			, "Ostokurssi", "Ostettu", "Mark.arvo", "Mark.arvo EUR",
			"Hankintahinta", "Osingot", "Tuotto", "Tuotto %", "P/A%",
			"%salkusta"
	// ,"Korkein"
	// ,"Alin"
	// ,"Vaihto"
	};
	
    
	public PortfolioModel(List<BookEntry> data) {
		super(new PortfolioNode(data));

		

	}

	public int getColumnCount() {
		return columnNames.length;
	}

	

	public String getColumnName(int col) {
		return columnNames[col];
	}

//	public Object getValueAt(int row, int col) {
//		return data[row][col];
//	}

	/*
	 * JTable uses this method to determine the default renderer/ editor for
	 * each cell. 
	 */
	public Class getColumnClass(int c) {

		if (c == 0) {
			return TreeTableModel.class;
		}
		PortfolioNode node = (PortfolioNode) getRoot();
		return node.getColumnClass(c);
		
		
	}

	/*
	 * Don't need to implement this method unless your table's editable.
	 */
	public boolean isCellEditable(int row, int col) {
		// Note that the data/cell address is constant,
		// no matter where the cell appears onscreen.
		return false;
	}

	/*
	 * Don't need to implement this method unless your table's data can change.
	 */
	public void setValueAt(Object value, int row, int col) {
		if (DEBUG) {
			System.out.println("Setting value at " + row + "," + col + " to "
					+ value + " (an instance of " + value.getClass() + ")");
		}

		data[row][col] = value;
	
	}

	
	//
	// The TreeModel interface
	//

	public int getChildCount(Object node) {
		Object[] children = getChildren(node);
		return (children == null) ? 0 : children.length;
	}

	public Object getChild(Object node, int i) {
		return getChildren(node)[i];
	}

	protected Object[] getChildren(Object node) {
		if (node.getClass() == BookEntryNode.class) {
			BookEntryNode bookEntryNode = ((BookEntryNode) node);
			return bookEntryNode.getChildren();
		} else if (node.getClass() == PortfolioNode.class) {
			PortfolioNode portfolioNode = ((PortfolioNode) node);
			return portfolioNode.getChildren();
		}
		else if (node.getClass() == BookEntryItemNode.class) {
			BookEntryItemNode bookEntryItemNode = ((BookEntryItemNode) node);
			return bookEntryItemNode.getChildren();
		} 
		
		return null;

	}

	@Override
	public Object getValueAt(Object node, int column) {

		if (node.getClass() == BookEntryNode.class) {
			BookEntryNode bookEntryNode = ((BookEntryNode) node);
			
			BookEntry be = bookEntryNode.getBookEntry();
			
			return bookEntryNode.getColumnValue(column);
			
			
		} else if (node.getClass() == PortfolioNode.class) {
			
			return null;
		}
		else if (node.getClass() == BookEntryItemNode.class) {
			BookEntryItemNode bookEntryItemNode = ((BookEntryItemNode) node);
			BookEntryItem item = bookEntryItemNode.getItem();
			BookEntryNode bookEntryNode = bookEntryItemNode.getParent();
			
			BookEntry be = bookEntryNode.getBookEntry();
			return bookEntryNode.getColumnValue(column);
			
			
		} 
		
		return null;
	}


	
	
}
