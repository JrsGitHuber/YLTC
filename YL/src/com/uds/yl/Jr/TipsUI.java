package com.uds.yl.Jr;

import java.awt.BorderLayout;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.util.MessageBox;

public class TipsUI {
	UI ui;
	Thread thread;
	
	public static void ShowUI(String title, Thread thread, Object parent){
		new TipsUI(title, thread, parent);
	}
	
//	public static void CloseUI(String title, Thread thread, Object parent){
//		if (ui == null) {
//			return;
//		} else {
//			ui.dispose();
//			ui = null;
//		}
//	}
	
	
	private TipsUI(String title, Thread thread, Object parent){
		this.thread = thread;
		StartThread();
		
		if(parent == null){
			ui = new UI(title);
		}else if(parent instanceof JDialog){
			ui = new UI(title, (JDialog)parent);
		}else if(parent instanceof JFrame){
			ui = new UI(title, (JFrame)parent);
		}else if(parent instanceof Rectangle){
			ui = new UI(title, (Rectangle)parent);
		}else{
			ui = new UI(title);
		}
		
		ui.setVisible(true);
	}
	
	private void StartThread() {
		new Thread() {
			public void run() {
				try {
					thread.start(); // �����ʱ����
					thread.join(); // �ȴ��������߳̽���
				} catch (Exception e) {
					MessageBox.post("����\n\n" + e.getMessage() + "\n��鿴����̨����Ĵ�����Ϣ", "��ʾ", MessageBox.INFORMATION);
					e.printStackTrace();					
				} finally {
					if (ui == null) {
						return;
					} else {
						ui.dispose();
						ui = null;
					}
				}
			}
		}.start();
	}
}

class UI extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	
	private int width = 450;
	private int height = 100;
	
	public UI(String title){
		setTitle(title);
		OtherInit();
		setLocationRelativeTo(null);
	}
	
	public UI(String title, JDialog parent) {
		super(parent, title, true);
		OtherInit();
		SetBounds(parent.getBounds());
	}
	
	public UI(String title, JFrame parent) {
		super(parent, title, true);
		OtherInit();
		SetBounds(parent.getBounds());
	}
	
	public UI(String title, Rectangle parent) {
		setTitle(title);
		OtherInit();
		SetBounds(parent.getBounds());
	}
	
	private void OtherInit(){
		setAlwaysOnTop(true);
		setType(Type.UTILITY);
		setBounds(100, 100, width, height);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPanel.setLayout(null);
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		
		JProgressBar progressBar = new JProgressBar();
		progressBar.setBounds(80, 30, 280, 15);
		progressBar.setIndeterminate(true);
		contentPanel.add(progressBar);
	}
	
	private void SetBounds(Rectangle rectangle){
		int centerX = rectangle.x + rectangle.width / 2;
		int centerY = rectangle.y + rectangle.height / 2;
		setBounds(centerX - width/2, centerY - height/2, width, height);
	}
	
	public void dispose(){
		// �����ǲ���
		super.dispose();
	}
}