package com.stt.portfolio.gui;

import java.awt.GridLayout;
import java.text.NumberFormat;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;

import com.stt.portfolio.Portfolio;

public class PieChartPane extends JPanel {
	 // Holds the data
	  private DefaultPieDataset dataset;

	  // Create  chart
	  private JFreeChart chart;
	
	  // Create a panel
	  private ChartPanel panel;
	  
	  private double belowPercentage = 1;
	  
	  private int maxEntries = 25;

	  public PieChartPane(Portfolio portfolio ) {
		  this.dataset = new DefaultPieDataset();
		  Object[][] bookEntries = portfolio.getCombinedBookEntryTable(false);
		  double totalValue = portfolio.getPortfolioValue();
		  double belowXPercent = 0.0;
		  double percentageLimit = bookEntries.length > maxEntries ? belowPercentage : 0.0;
		  for (int i = 0; i < bookEntries.length; ++i) {
			  double percentage = (Double) bookEntries[i][8]/totalValue * 100.0 ;
			  if (percentage > percentageLimit) {
				  dataset.setValue( (String) bookEntries[i][0], Double.valueOf(percentage) );
			  }
			  else {
				  belowXPercent += percentage;
			  }
		  }
		  if (belowXPercent > 0.0) {
			  dataset.setValue( "Alle " + belowPercentage + "%", Double.valueOf(belowXPercent) );
		  }
		 
		  
		    this.chart = ChartFactory.createPieChart(
		    	      "Arvopaperijakauma", // The chart title
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
