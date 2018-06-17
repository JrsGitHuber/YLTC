package com.uds.yl.herb;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import com.teamcenter.rac.util.PropertyLayout;
/**
 * 
 * @author zhaoyao
 *
 */
public class CompareFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int m_result = 0;
	public CompareFrame(List<String> uids, List<String> names){
		String[] namess = new String[names.size()];
		names.toArray(namess);
		final JComboBox<String> box = new JComboBox<>(namess);
		JButton button = new JButton("开始比较");
		button.setSize(new Dimension(50,50));
		JPanel upPanel =new JPanel(new PropertyLayout(15,15,15,15,15,15));
		JPanel panelleft =new JPanel(new PropertyLayout());
		panelleft.setLayout(new GridLayout(1,1));
		panelleft.add("1.1.left.center",box);
		panelleft.add("1.2.left.center",new JLabel("    "));
		panelleft.add("1.3.right.center",button);
		upPanel.add("1.1.center",panelleft);
		upPanel.setLayout(new GridLayout(2,1));
		Container contentPane = this.getContentPane();
		contentPane.setLayout(new GridLayout(1,1));
		contentPane.add(upPanel);
		((JComponent) contentPane).setBorder(BorderFactory.createTitledBorder(""));
		box.addItemListener(new ItemListener(){

			@Override
			public void itemStateChanged(ItemEvent itemevent) {
				// TODO Auto-generated method stub
				m_result = box.getSelectedIndex();
			}
		});
		button.addActionListener(new ActionListener(){
	
			@Override
			public void actionPerformed(ActionEvent paramActionEvent) {
				dispose();
			}
		});
		this.add(upPanel);
		this.pack();
		this.setBounds(200, 200, 400, 100);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setResizable(false);
		this.addWindowListener(new WindowAdapter(){
			public void windowClosed(WindowEvent e){
				synchronized (CompareFrame.this) {
					CompareFrame.this.notify();
				}
			}
		});
	}
	
	public int ShowDialog()
	{
		this.setVisible(true);
		this.setTitle("配方对比");
		this.setResizable(true);
		synchronized(this){
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return m_result;
	}

}
