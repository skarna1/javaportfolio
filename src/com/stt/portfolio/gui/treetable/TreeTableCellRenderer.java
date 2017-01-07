package com.stt.portfolio.gui.treetable;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;

public class TreeTableCellRenderer extends JTree implements TableCellRenderer {
	protected int visibleRow;
    protected JTreeTable table;
    
	public TreeTableCellRenderer(TreeModel model, JTreeTable table) { 
	    super(model);
	    this.table=table;
	}
	
	public void setBounds(int x, int y, int w, int h) {
	    super.setBounds(x, 0, w, this.table.getHeight());
	}
	
	public void paint(Graphics g) {
		g.translate(0, -visibleRow * getRowHeight());
		super.paint(g);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		visibleRow = row;
		return this;
	}
	 /**
     * updateUI is overridden to set the colors of the Tree's renderer
     * to match that of the table.
     */
//    public void updateUI() {
//        super.updateUI();
//        // Make the tree's cell renderer use the table's cell selection
//        // colors. 
//        TreeCellRenderer tcr = getCellRenderer();
//        if (tcr instanceof DefaultTreeCellRenderer) {
//            DefaultTreeCellRenderer dtcr = ((DefaultTreeCellRenderer)tcr); 
//            // For 1.1 uncomment this, 1.2 has a bug that will cause an
//            // exception to be thrown if the border selection color is
//            // null.
//            // dtcr.setBorderSelectionColor(null);
//            dtcr.setTextSelectionColor(UIManager.getColor
//                                       ("Table.selectionForeground"));
//            dtcr.setBackgroundSelectionColor(UIManager.getColor
//                                            ("Table.selectionBackground"));
//        }
//    }


}
