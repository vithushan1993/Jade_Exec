package com.receiver.ui;

import javax.swing.JFrame;

import com.executingAgent.ChildAgent;

public class SwingFrame extends JFrame {
	
	private SwingPanel currentPanel;
	private SwingFrame main_f;
	//private Sender sender;
	
	public SwingFrame(ChildAgent receiver) {
		setTitle("Properties Gui");
		setResizable(false);
		currentPanel = new SwingPanel(receiver);
		setupFrame();
		//this.sender = sender;
	}
	
	private void setupFrame(){
		this.setContentPane(currentPanel);
	}
	
	public void start(ChildAgent receiver){
		this.setSize(330, 280);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
	
	public final JFrame getMainFrame(){
		return main_f;
	}
}
