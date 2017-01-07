package com.stt.portfolio.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class OpenPortfolioDialog extends JDialog implements ActionListener {

	protected String BUTTON_OK = "Ok";
	protected String BUTTON_CANCEL = "Kumoa";

	protected JButton okButton;
	protected JButton cancelButton;
	
	private JComboBox portfolioList;
	JLabel portfolioFieldLabel;
	private boolean isOk = false;

	public OpenPortfolioDialog(Component frameComp, Component locationComp,
			String title, Object[] portfolios) {
		super(JOptionPane.getFrameForComponent(frameComp), title, true);

		// Create and initialize the buttons.
		cancelButton = new JButton(BUTTON_CANCEL);
		cancelButton.addActionListener(this);
		cancelButton.setActionCommand(BUTTON_CANCEL);

		okButton = new JButton(BUTTON_OK);
		okButton.setActionCommand(BUTTON_OK);
		okButton.addActionListener(this);
		getRootPane().setDefaultButton(okButton);
		
		portfolioList = new JComboBox(portfolios);
		portfolioFieldLabel = new JLabel("Salkun nimi: ");
		portfolioFieldLabel.setLabelFor(portfolioList);
		
		JPanel textControlsPane = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(10, 10, 10, 10); // this statement added.
		
		textControlsPane.add(portfolioFieldLabel,c);
		textControlsPane.add(portfolioList, c);
		
		// Lay out the buttons from left to right.
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(okButton);
		
		// Put everything together, using the content pane's BorderLayout.
		Container contentPane = getContentPane();
		contentPane.add(textControlsPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);

		pack();
		setLocationRelativeTo(locationComp);
		setVisible(true);
	}

	public boolean isOk() {
		return isOk;
	}
	
	public String getInfo() {
		return (String) portfolioList.getSelectedItem();
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (BUTTON_OK.equals(e.getActionCommand())) {
			setVisible(false);
			isOk = true;

		} else if (BUTTON_CANCEL.equals(e.getActionCommand())) {
			setVisible(false);

		}
	}

}
