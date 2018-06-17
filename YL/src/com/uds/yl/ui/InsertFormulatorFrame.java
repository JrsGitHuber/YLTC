package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import com.aspose.words.WarningInfo;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;

public class InsertFormulatorFrame extends JFrame{
	
	private JPanel contentPane;
	private JTextField formulatorNameEdt;
	private JTextField waterEdt;//��ˮ��
	private AbstractCallBack mCallBack;
	
	public InsertFormulatorFrame() {
		setTitle("����Ӫ����");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 251);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("�������ƣ�");
		lblNewLabel.setBounds(47, 40, 97, 15);
		contentPane.add(lblNewLabel);
		
		formulatorNameEdt = new JTextField();
		formulatorNameEdt.setBounds(185, 37, 175, 21);
		contentPane.add(formulatorNameEdt);
		formulatorNameEdt.setColumns(10);
		
		JButton okBtn = new JButton("ȷ��");
		okBtn.setBounds(51, 153, 93, 23);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(267, 153, 93, 23);
		contentPane.add(cancleBtn);
		
		JLabel label = new JLabel("��ˮ����");
		label.setBounds(47, 100, 97, 15);
		contentPane.add(label);
		
		waterEdt = new JTextField();
		waterEdt.setColumns(10);
		waterEdt.setBounds(185, 97, 175, 21);
		contentPane.add(waterEdt);
		
		
		//ȷ��
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String formulatorName = formulatorNameEdt.getText().toString();
				String waterValue = waterEdt.getText().toString();
				
				
				if(StringsUtil.isEmpty(formulatorName)){//�������Ϊ��
					MessageBox.post("����д��������","",MessageBox.INFORMATION);
					return;
				}
				if(StringsUtil.isEmpty(waterValue)){//�������Ϊ��
					MessageBox.post("����д���ۺ�ˮ��","",MessageBox.INFORMATION);
					return;
				}
				if(!StringsUtil.isNumeric(waterValue)){//���Ǵ�����
					MessageBox.post("��ˮ������д����","",MessageBox.INFORMATION);
					return;
				}
				mCallBack.setFormulatorName(formulatorName,waterValue);
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
