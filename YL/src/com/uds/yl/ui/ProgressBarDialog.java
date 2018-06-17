package com.uds.yl.ui;


import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JProgressBar;

import com.teamcenter.soaictstubs.booleanSeq_tHolder;

public class ProgressBarDialog {

	JDialog dialog ;
	
	private boolean liveFlag =true;
	private Thread taskThread;
	
	public ProgressBarDialog(){
		this.taskThread = new Thread(new Runnable() {
			@Override
			public void run() {
				
			}
		});
	}
	
	public ProgressBarDialog(Thread taskThread){
		
		this.taskThread = taskThread;
		if(taskThread == null){
			this.taskThread = new Thread(new Runnable() {
				@Override
				public void run() {
					
				}
			});
		}
		
	}
	
	public void start(){
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(20, 24, 396, 23);
		progressBar.setIndeterminate(true);
		
		dialog = new JDialog();
		dialog.setBounds(100, 100, 458, 102);
		dialog.getContentPane().setLayout(null);
		dialog.getContentPane().add(progressBar);
		dialog.setResizable(false);
		dialog.setTitle("¥¶¿Ì÷–...");
//		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		taskThread.start();
		
		dialog.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				taskThread.interrupt();
			}
		});
	}
	
	public boolean isLive(){
		return liveFlag;
	}
	
	
	
	public void stop(){
		liveFlag = false;
		dialog.setVisible(false);
		taskThread.interrupt();;
		
	}
	
	
}

