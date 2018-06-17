package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.bean.UpAndDonwBean;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;
import com.uds.yl.utils.TechStandardUtil;

public class MaterialStandardAndWaringSelectedFrame extends JFrame {
	private JPanel contentPane;
	
	private AbstractCallBack callBack;
	
	private JTextField standardUpEdt;
	private JTextField standardDownEdt;
	private JComboBox standardUpComBox;
	private JComboBox standardDownComBox;
	private JTextField standardDescEdt;
	
	
	private JTextField testGistEdt;
	private JTextArea indexIntroductEdt;
	private JTextField remarkEdt;
	
	private String indexIntroduce;
	private String indexTestGist;
	private String indexRemark;

	private JButton okBtn;
	private JButton cancleBtn;

	private JLabel label_1;
	private JLabel standardLable;
	
	public MaterialStandardAndWaringSelectedFrame(String rawStandarStr,String introduceStr,String testGistStr,String remarkStr) {
		setTitle("�ڿر�׼ѡ��");
		setAlwaysOnTop(true);
		setBounds(100, 100, 844, 590);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		okBtn = new JButton("ȷ��");
		okBtn.setBounds(130, 502, 93, 30);
		panel.add(okBtn);
		
		cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(573, 502, 93, 30);
		panel.add(cancleBtn);
		
		JPanel standarPanel = new JPanel();
		standarPanel.setBounds(51, 34, 757, 198);
		panel.add(standarPanel);
		standarPanel.setLayout(null);
		
		standardUpComBox = new JComboBox();
		standardUpComBox.setBounds(424, 53, 79, 21);
		standarPanel.add(standardUpComBox);
		standardUpComBox.setModel(new DefaultComboBoxModel(new String[]{"","<=","<"}));
		
		standardDownComBox = new JComboBox();
		standardDownComBox.setBounds(51, 53, 79, 21);
		standarPanel.add(standardDownComBox);
		standardDownComBox.setModel(new DefaultComboBoxModel(new String[] {"", ">=", ">"}));
		
		standardDescEdt = new JTextField();
		standardDescEdt.setBounds(42, 130, 193, 21);
		standarPanel.add(standardDescEdt);
		standardDescEdt.setColumns(10);
		
		standardDownEdt = new JTextField();
		standardDownEdt.setBounds(164, 53, 95, 21);
		standarPanel.add(standardDownEdt);
		standardDownEdt.setColumns(10);
		
		standardUpEdt = new JTextField();
		standardUpEdt.setBounds(535, 53, 95, 21);
		standarPanel.add(standardUpEdt);
		standardUpEdt.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("����");
		lblNewLabel.setBounds(487, 10, 54, 30);
		standarPanel.add(lblNewLabel);
		lblNewLabel.setFont(new Font("����", Font.PLAIN, 13));
		
		JLabel label = new JLabel("����");
		label.setBounds(142, 10, 54, 30);
		standarPanel.add(label);
		label.setFont(new Font("����", Font.PLAIN, 13));
		
		label_1 = new JLabel("����");
		label_1.setBounds(298, 125, 54, 30);
		standarPanel.add(label_1);
		
		standardLable = new JLabel("�ڿأ�");
		standardLable.setBounds(21, 9, 54, 15);
		panel.add(standardLable);
		
		JLabel lblNewLabel_1 = new JLabel("��ⷽ�����ݣ�");
		lblNewLabel_1.setBounds(71, 268, 167, 15);
		panel.add(lblNewLabel_1);
		
		testGistEdt = new JTextField();
		testGistEdt.setBounds(232, 265, 107, 21);
		panel.add(testGistEdt);
		testGistEdt.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("ָ�����Ʊ�ע��");
		lblNewLabel_2.setBounds(71, 317, 119, 15);
		panel.add(lblNewLabel_2);
		
		indexIntroductEdt = new JTextArea();
		indexIntroductEdt.setBounds(233, 313, 492, 76);
		panel.add(indexIntroductEdt);
		
		JLabel label_2 = new JLabel("��ע��");
		label_2.setBounds(71, 430, 119, 15);
		panel.add(label_2);
		
		remarkEdt = new JTextField();
		remarkEdt.setColumns(10);
		remarkEdt.setBounds(232, 427, 107, 21);
		panel.add(remarkEdt);
		
		// ===============��ʼ�� �ڿ�ֵ
		UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(rawStandarStr);
		if(!StringsUtil.isEmpty(initUpAndDonwBean.detectValue)){
			standardDescEdt.setText(rawStandarStr);
		}else{
			//����
			standardDownEdt.setText(initUpAndDonwBean.down);
			if(">".equals(initUpAndDonwBean.downSymbol)){
				standardDownComBox.setSelectedIndex(2);
			}else if(">=".equals(initUpAndDonwBean.downSymbol)){
				standardDownComBox.setSelectedIndex(1);
			}else{
				standardDownComBox.setSelectedIndex(0);
			}
			
			
			//����
			standardUpEdt.setText(initUpAndDonwBean.up);
			if("<".equals(initUpAndDonwBean.upSymbol)){
				standardUpComBox.setSelectedIndex(2);
			}else if("<=".equals(initUpAndDonwBean.upSymbol)){
				standardUpComBox.setSelectedIndex(1);
			}else{
				standardUpComBox.setSelectedIndex(0);
			}
		}
		

		if (StringsUtil.isEmpty(rawStandarStr)) {
			standardDownComBox.setSelectedIndex(1);
			standardUpComBox.setSelectedIndex(1);
		}

		// ==��ʼ�� ��ⷽ����ָ��˵��
		indexIntroductEdt.setText(introduceStr);
		testGistEdt.setText(testGistStr);
		remarkEdt.setText(remarkStr);

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String standardResult = "";
				String waringResult = "";

				// ƴװ �ڿر�׼�Ľ��ֵ
				String standardUpEdtStr = standardUpEdt.getText().toString();
				String standardDownEdtStr = standardDownEdt.getText()
						.toString();

				String standardUpComStr = standardUpComBox.getSelectedItem()
						.toString();
				String standardDownComStr = standardDownComBox
						.getSelectedItem().toString();
				
				String standardDesc = standardDescEdt.getText().toString();

				if (!StringsUtil.isNumeric(standardUpEdtStr)
						&& !StringsUtil.isEmpty(standardUpEdtStr)) {
					MessageBox.post("��Χ����д����", "��ʾ", MessageBox.INFORMATION);
					return;
				}

				if (!StringsUtil.isNumeric(standardDownEdtStr)
						&& !StringsUtil.isEmpty(standardDownEdtStr)) {
					MessageBox.post("��Χ����д����", "��ʾ", MessageBox.INFORMATION);
					return;
				}
				
				if (!StringsUtil.isEmpty(standardDesc)) {
					if (!StringsUtil.isEmpty(standardUpEdtStr)
							|| !StringsUtil.isEmpty(standardDownEdtStr)) {
						MessageBox.post("����д���ֻ���ֻ��д��������", "��ʾ",
								MessageBox.INFORMATION);
						return;
					}
				}
				
				standardResult = TechStandardUtil.initResult(standardUpEdtStr, standardDownEdtStr,
						standardUpComStr, standardDownComStr, standardDesc).resultStr;
				

				// ��ⷽ�����ݺ�ָ��˵��
				indexTestGist = testGistEdt.getText().toString();
				indexIntroduce = indexIntroductEdt.getText().toString();
				indexRemark = remarkEdt.getText().toString();

				callBack.setStandardAndWaringResult(standardResult,
						"", indexTestGist, indexIntroduce,indexRemark);

				dispose();
			}
		});

		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
	}
	
	public void setCallBack(AbstractCallBack callBack) {
		this.callBack = callBack;
	}

}
