package com.uds.yl.ui;


import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import org.apache.axis.client.Call;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.interfaces.CallBack;
import com.uds.yl.utils.StringsUtil;

public class AddNewIndexFrame extends JFrame{

	private JPanel contentPane;
	private JTextField nameEdt;
	private JTextField unitEdt;
	
	private String indexName="";
	private String indexUnit="";
	
	private AbstractCallBack mCallBack;
	
	
	public AddNewIndexFrame() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 540, 301);
		contentPane = new JPanel();
		setAlwaysOnTop(true);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("名称：");
		lblNewLabel.setBounds(119, 61, 60, 30);
		contentPane.add(lblNewLabel);
		
		nameEdt = new JTextField();
		nameEdt.setBounds(204, 62, 189, 30);
		contentPane.add(nameEdt);
		nameEdt.setColumns(10);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(119, 198, 70, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(366, 198, 70, 30);
		contentPane.add(cancleBtn);
		
		JLabel label = new JLabel("单位：");
		label.setBounds(119, 126, 60, 30);
		contentPane.add(label);
		
		unitEdt = new JTextField();
		unitEdt.setColumns(10);
		unitEdt.setBounds(204, 127, 189, 30);
		contentPane.add(unitEdt);
		
		
		//确定
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				indexName = nameEdt.getText().toString();
				indexUnit = unitEdt.getText().toString();
				
				if(StringsUtil.isEmpty(indexName)||StringsUtil.isEmpty(indexUnit)){
					MessageBox.post("请完善信息！","提示",MessageBox.INFORMATION);
					return;
				}
				
				mCallBack.addNewIndexResult(indexName, indexUnit);
			}
		});
		
		//取消
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
	}
	
	
	public void setCallBack(AbstractCallBack callBack){
		this.mCallBack = callBack;
	}
	
}
