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
		
		
		setTitle("����Ӫ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 207);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Ӫ�������ƣ�");
		lblNewLabel.setBounds(47, 40, 97, 15);
		contentPane.add(lblNewLabel);
		
		nutririonNameEdt = new JTextField();
		nutririonNameEdt.setBounds(185, 37, 175, 21);
		contentPane.add(nutririonNameEdt);
		nutririonNameEdt.setColumns(10);
		
		JButton okBtn = new JButton("ȷ��");
		okBtn.setBounds(47, 97, 93, 23);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(266, 97, 93, 23);
		contentPane.add(cancleBtn);
		
		
		//ȷ��
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String nutritionName = nutririonNameEdt.getText().toString();
				if(StringsUtil.isEmpty(nutritionName)){//�������Ϊ��
					MessageBox.post("����дӪ��������","",MessageBox.INFORMATION);
					return;
				}
				mCallBack.setNutritionName(nutritionName);
				dispose();
			}
		});
		
		//ȡ��
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
