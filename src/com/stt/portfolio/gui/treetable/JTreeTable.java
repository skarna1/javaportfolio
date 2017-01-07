package com.stt.portfolio.gui.treetable;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeSelectionModel;

import com.stt.portfolio.gui.CellRenderer;

public class JTreeTable extends JTable {
	protected TreeTableCellRenderer tree;
	TableCellRenderer cellRendererRedLeft = null;
	TableCellRenderer cellRendererGreenLeft = null;
	TableCellRenderer cellRendererRedRight = null;
	TableCellRenderer cellRendererGreenRight = null;
	TableCellRenderer cellRendererYellowLeft = null;
	TableCellRenderer cellRendererYellowRight = null;

	public JTreeTable(TreeTableModel treeTableModel) {
		super();

		// Create the tree. It will be used as a renderer and editor.
		tree = new TreeTableCellRenderer(treeTableModel, this);

		// Install a tableModel representing the visible rows in the tree.
		super.setModel(new TreeTableModelAdapter(treeTableModel, tree));

		// Force the JTable and JTree to share their row selection models.
		tree.setSelectionModel(new DefaultTreeSelectionModel() {
			// Extend the implementation of the constructor, as if:
			/* public this() */{
				setSelectionModel(listSelectionModel);
			}
		});
		// Make the tree and table row heights the same.
		tree.setRowHeight(getRowHeight());

		// Install the tree editor renderer and editor.
		setDefaultRenderer(TreeTableModel.class, tree);
		setDefaultEditor(TreeTableModel.class, new TreeTableCellEditor());

		setShowGrid(false);
		setIntercellSpacing(new Dimension(0, 0));

		cellRendererRedLeft = new CellRenderer(java.awt.Color.RED, false, null);
		cellRendererGreenLeft = new CellRenderer(java.awt.Color.GREEN, false, null);
		cellRendererRedRight = new CellRenderer(java.awt.Color.RED, true, null);
		cellRendererGreenRight = new CellRenderer(java.awt.Color.GREEN, true, null);
		cellRendererYellowLeft = new CellRenderer(java.awt.Color.YELLOW, false, null);
		cellRendererYellowRight = new CellRenderer(java.awt.Color.YELLOW, true, null);

		setPreferredScrollableViewportSize(new Dimension(1000, 500));
		setFillsViewportHeight(true);
		setAutoCreateRowSorter(true);
	}

	/*
	 * Workaround for BasicTableUI anomaly. Make sure the UI never tries to
	 * paint the editor. The UI currently uses different techniques to paint the
	 * renderers and editors and overriding setBounds() below is not the right
	 * thing to do for an editor. Returning -1 for the editing row in this case,
	 * ensures the editor is never painted.
	 */
	public int getEditingRow() {
		return (getColumnClass(editingColumn) == TreeTableModel.class) ? -1
				: editingRow;
	}

	public void changeContent(Object[][] bookEntries) {

	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		if (row == 0)
			return super.getCellRenderer(row, column);
		if (getModel().getColumnCount() < 13) {
			return cellRendererGreenLeft;
		}

		int modelRow = getRowSorter().convertRowIndexToModel(row);
		Double profitPercentage = (Double) getModel().getValueAt(modelRow, 12);
		if (profitPercentage != null) {
			if (profitPercentage < -10.0) {
				return (column == 0) ? cellRendererRedLeft
						: cellRendererRedRight;
			} else if (profitPercentage < 10.0) {
				return (column == 0) ? cellRendererYellowLeft
						: cellRendererYellowRight;
			}
		}
		// return super.getCellRenderer(row, column);
		return (column == 0) ? cellRendererGreenLeft : cellRendererGreenRight;
	}

	// 
	// The editor used to interact with tree nodes, a JTree.
	//

	public class TreeTableCellEditor extends AbstractCellEditor implements
			TableCellEditor {
		public Component getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int r, int c) {
			return tree;
		}

		@Override
		public Object getCellEditorValue() {
			// TODO Auto-generated method stub
			return null;
		}
				
	}
	
	
	
}
