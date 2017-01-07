package com.stt.portfolio.gui;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

import javax.swing.table.DefaultTableCellRenderer;

public class CellRenderer extends DefaultTableCellRenderer {
	Calendar c = Calendar.getInstance();
	public CellRenderer(Color c, boolean isRightAlign, String tooltiptext) {
		super();
		if (tooltiptext != null){
		    this.setToolTipText(tooltiptext);
		}
		if (isRightAlign) {
			setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
		}
		if (c != null) {
			setBackground(c);
		}
	}

	public void setValue(Object value) {

		if (value != null && value.getClass().equals(Double.class)) {
			String text = String.format("%1$.2f", (Double) value);
			setText(text);
		} else if (value != null && value.getClass().equals(Date.class)) {
			
			c.setTime((Date) value);
			String text = String.format("%1$td.%1$tm.%1$tY", c);
			setText(text);
			
		} else {
			super.setValue(value);
		}

	}
}
