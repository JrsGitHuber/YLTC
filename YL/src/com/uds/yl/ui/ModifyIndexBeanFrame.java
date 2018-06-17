package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;

import com.uds.yl.bean.TechStandarTableBean;
import com.uds.yl.interfaces.AbstractCallBack;

public class ModifyIndexBeanFrame extends JFrame{

	private JPanel contentPane;
	private JTextField testGistEdt;
	private JTextArea indexIntroductEdt;
	
	private String indexIntroduce;
	private String indexTestGist;
	
	private AbstractCallBack mCallBack;
	
	
	public ModifyIndexBeanFrame() {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 668, 316);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("指标说明：");
		lblNewLabel.setBounds(38, 91, 81, 22);
		contentPane.add(lblNewLabel);
		
		indexIntroductEdt = new JTextArea();
		indexIntroductEdt.setBounds(158, 91, 429, 42);
		contentPane.add(indexIntroductEdt);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(91, 212, 93, 29);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(423, 212, 93, 29);
		contentPane.add(cancleBtn);
		
		JLabel label_2 = new JLabel("检测方法依据");
		label_2.setBounds(38, 33, 111, 22);
		contentPane.add(label_2);
		
		testGistEdt = new JTextField();
		testGistEdt.setColumns(10);
		testGistEdt.setBounds(159, 34, 165, 21);
		contentPane.add(testGistEdt);
		
		
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				indexIntroduce = indexIntroductEdt.getText().toString();
				indexTestGist = testGistEdt.getText().toString();
				
				mCallBack.modifyIndexBeanResult(indexIntroduce,indexTestGist);
				
				dispose();
			}
		});
		
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
	
	public void initFrame(String testGist ,String introduce){
		indexIntroductEdt.setText(introduce);
		testGistEdt.setText(testGist);
	}
}
