package com.uds.yl.herb;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import com.teamcenter.rac.util.PropertyLayout;
/**
 * 
 * @author zhaoyao
 *
 */
public class RawQueryFrame extends JFrame {
	
	private static final long serialVersionUID = 1L;
	public int m_comboboxIndex = 0; 
	public String m_code = "";
	public String m_name = "";
	public boolean m_dlgResult = false;
	
	JLabel label1 = new JLabel("原料编码：");
	JLabel label2 = new JLabel("原料/成分INCI名：");
	JLabel label3 = new JLabel("原料类别");
	JTextField field1 = null;
	JTextField field2 = null;
	JComboBox<String> combo = null;
	JButton countButton = null;
	JButton cleanButton = null;
	JButton cacelButton = null;
	JPanel mainPanel = null;
	JPanel upPanel = null;
	JPanel downPanel = null;

	public RawQueryFrame(String[] valueLovs){
		
		combo = new JComboBox<>(valueLovs);
		combo.setEditable(true);
		field1 = new JTextField();
		field2 = new JTextField();
		
		m_code = field1.getText();
		m_name = field2.getText();
		
		field1.setColumns(15);
		field2.setColumns(15);
		
		countButton = new JButton("统计");
		cleanButton = new JButton("消除");
		cacelButton = new JButton("取消");
		upPanel = new JPanel(new PropertyLayout(15, 15, 15, 15, 15, 15));
		JPanel panelleft = new JPanel(new PropertyLayout());
		JPanel panelright = new JPanel(new PropertyLayout());
		JPanel panelend = new JPanel(new PropertyLayout());
		panelleft.add("1.1", label1);
		panelleft.add("1.2", field1);
		panelright.add("1.1", label2);
		panelright.add("1.2", field2);
		panelend.add("1.1", label3);
		panelend.add("1.2", combo);
		panelend.add("1.3", new JLabel("   "));
		panelend.add("1.4", countButton);
		panelend.add("1.5", cleanButton);
		panelend.add("1.6",cacelButton);
		upPanel.add("1.1.left.center", panelleft);
		upPanel.add("1.2.right.center", panelright);
		upPanel.setLayout(new GridLayout(1,1));
		upPanel.setBorder(BorderFactory.createEmptyBorder());
		downPanel = new JPanel(new PropertyLayout(15, 15, 15, 15, 15, 15));
		downPanel.setLayout(new GridLayout(1,1));
		//downPanel.setLayout(new BorderLayout());
		downPanel.add("1.1.right.center",panelend);
		downPanel.setBorder(BorderFactory.createTitledBorder(""));
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.add(upPanel, BorderLayout.NORTH);
		mainPanel.add(downPanel, BorderLayout.SOUTH);
		mainPanel.setPreferredSize(new Dimension(480, 170));
		mainPanel.setLayout(new GridLayout(2,1));
		mainPanel.setBorder(BorderFactory.createTitledBorder(""));
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(1,1));
		contentPane.add(mainPanel);
		((JComponent) contentPane).setBorder(BorderFactory.createTitledBorder(""));
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(true);
		this.pack();
		Toolkit kit = Toolkit.getDefaultToolkit();
		Dimension screenSize = kit.getScreenSize();
		int screenWidth = screenSize.width / 2;
		int screenHeight = screenSize.height / 2;
		int height = this.getHeight();
		int width = this.getWidth();
		this.setLocation(screenWidth - (width / 2), screenHeight - (height / 2));
		
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				synchronized (RawQueryFrame.this) {
					RawQueryFrame.this.notify();
				}
			}
		});
		
		
		combo.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent itemevent) {
				// TODO Auto-generated method stub
				m_comboboxIndex = combo.getSelectedIndex();
			}
		});
		
		countButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				m_code = field1.getText();
				m_name = field2.getText();
				m_dlgResult = true;
				dispose();
			}
			
		});
		
		cleanButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				combo.setSelectedItem("");
				field1.setText("");
				field2.setText("");
			}
		});
		
		cacelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				m_dlgResult = false;
				dispose();
			}
			
		});
	}

	public boolean ShowDialog()
	{
		this.setVisible(true);
		this.setTitle("成分查询");
		synchronized(this){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return m_dlgResult;
	}
}
