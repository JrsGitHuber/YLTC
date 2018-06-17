package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;


public class InsertNutritionFrame extends JFrame{
	
	private JPanel contentPane;
	private JTextField nutririonNameEdt;
	private AbstractCallBack mCallBack;
	
	public InsertNutritionFrame() {
		
		
		setTitle("创建营养包");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 207);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("营养包名称：");
		lblNewLabel.setBounds(47, 40, 97, 15);
		contentPane.add(lblNewLabel);
		
		nutririonNameEdt = new JTextField();
		nutririonNameEdt.setBounds(185, 37, 175, 21);
		contentPane.add(nutririonNameEdt);
		nutririonNameEdt.setColumns(10);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(47, 97, 93, 23);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(266, 97, 93, 23);
		contentPane.add(cancleBtn);
		
		
		//确定
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nutritionName = nutririonNameEdt.getText().toString();
				if(StringsUtil.isEmpty(nutritionName)){//如果名称为空
					MessageBox.post("请填写营养包名称","",MessageBox.INFORMATION);
					return;
				}
				mCallBack.setNutritionName(nutritionName);
				dispose();
			}
		});
		
		//取消
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
	}
	
	public void setCallBack(AbstractCallBack callBack){
		this.mCallBack = callBack;
	}
}
