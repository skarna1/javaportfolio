package com.stt.portfolio.gui;

import java.awt.GridLayout;
import java.text.NumberFormat;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.stt.portfolio.Portfolio;

public class CountryAllocationPane extends JPanel {
	// Holds the data
	private DefaultPieDataset dataset;

	// Create  chart
	private JFreeChart chart;

	// Create a panel
	private ChartPanel panel;

	public CountryAllocationPane(Portfolio portfolio ) {
		this.dataset = new DefaultPieDataset();
		Set<Entry<String, Double>> bookEntries = portfolio.getCountryAllocations();
		double totalValue = portfolio.getPortfolioValue();
		
		for (Entry<String, Double> entry : bookEntries) {
			double percentage = (Double) entry.getValue()/totalValue * 100.0 ;
			dataset.setValue(entry.getKey(), new Double(percentage) );
		}

		this.chart = ChartFactory.createPieChart(
				"Maantieteellinen jakauma", // The chart title
				dataset,         // The dataset for the chart
				false,          // Is a legend required?
				true,          // Use tooltips
				false          // Configure chart to generate URLs?
				);
		/* Creating a pieplot so as to customize the chart  generated */
		final PiePlot plot = (PiePlot)chart.getPlot( ); 

		/* Customizing Label using an inner class */

		plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
				"{0} = {2}", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
				));
		// Create this panel
		this.setLayout( new GridLayout( 1, 1 ) );
		this.panel = new ChartPanel( chart );

		this.add( panel );
	}
}
