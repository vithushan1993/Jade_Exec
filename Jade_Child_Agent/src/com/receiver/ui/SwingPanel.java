package com.receiver.ui;

import javax.swing.JPanel;

import java.awt.Color;

import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.JComboBox;
import javax.swing.JButton;

import com.executingAgent.ChildAgent;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SwingPanel extends JPanel {
	private ChildAgent receiver;
	
	public SwingPanel(ChildAgent receiver) {	
		this.receiver=receiver;
		setupPanel();	
	}
	
	public void setupPanel(){
		setBackground(new Color(0, 102, 153));
		setLayout(null);
		
		JLabel lblChildAgent = new JLabel("Child Agent");
		lblChildAgent.setFont(new Font("Script MT Bold", Font.BOLD, 21));
		lblChildAgent.setBounds(92, 11, 115, 26);
		add(lblChildAgent);
		
		JLabel lblOS = new JLabel("OS: ");
		lblOS.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblOS.setBounds(39, 70, 46, 14);
		add(lblOS);
				
		JLabel lblBr = new JLabel("Browser:");
		lblBr.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblBr.setBounds(39, 112, 68, 14);
		add(lblBr);
		
		JLabel lblTool = new JLabel("Tool:");
		lblTool.setFont(new Font("Tahoma", Font.PLAIN, 15));
		lblTool.setBounds(39, 156, 68, 14);
		add(lblTool);
		
		String[] osCM = {"--Select--", "Windows", "Mac", "Unix", "Linux" };		
		final JComboBox cmbOS = new JComboBox(osCM);
		cmbOS.setBounds(116, 69, 149, 20);
		add(cmbOS);
		
		String[] brCM = {"--Select--", "Firefox", "Chrome", "IE"};
		final JComboBox cmbBr = new JComboBox(brCM);
		cmbBr.setBounds(117, 111, 148, 20);
		add(cmbBr);	
		
		String[] toolCM = {"--Select--", "Sellenium", "QTP"};
		final JComboBox cmbTool = new JComboBox(toolCM);
		cmbTool.setBounds(116, 155, 149, 20);
		add(cmbTool);
		
		JButton btnSubmit = new JButton("Submit");
		btnSubmit.setFont(new Font("Papyrus", Font.BOLD, 13));
		btnSubmit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(cmbOS.getSelectedIndex() == 0 || cmbBr.getSelectedIndex() == 0 || cmbTool.getSelectedIndex() == 0){
					JOptionPane.showMessageDialog(null, "Please select all option");
				}else{
					String os = (String)cmbOS.getSelectedItem();
					String browser = (String)cmbBr.getSelectedItem();
					String tool = (String)cmbTool.getSelectedItem();
					JOptionPane.showMessageDialog(null, os + " " + browser + " " + tool);
					
					// propertyFile = new CreateProperty();
					//propertyFile.createProperty(os, browser, tool);
					
					String sendMsg = browser + "_" + tool + "_" + os;
					receiver.sendConfig(sendMsg);

					getTopLevelAncestor().setVisible(false);
				}
			}
		});
		btnSubmit.setBounds(105, 199, 89, 26);
		add(btnSubmit);
	}
}
