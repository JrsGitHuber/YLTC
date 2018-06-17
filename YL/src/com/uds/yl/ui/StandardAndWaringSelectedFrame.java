package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.bean.UpAndDonwBean;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;
import com.uds.yl.utils.TechStandardUtil;

public class StandardAndWaringSelectedFrame extends JFrame{

	private JPanel contentPane;

	private AbstractCallBack callBack;

	private JTextField standardUpEdt;
	private JTextField standardDownEdt;
	private JComboBox standardUpComBox;
	private JComboBox standardDownComBox;
	
	private JComboBox waringDownComBox;
	private JTextField waringDownEdt;
	private JTextField waringUpEdt;
	private JComboBox waringUpComBox;
	private JTextField standardDescEdt;
	private JTextField waringDescEdt;
	
	
	private JTextField testGistEdt;
	private JTextArea indexIntroductEdt;
	private JTextField remarkEdt;
	
	private String indexIntroduce;
	private String indexTestGist;
	private String indexRemark;

	private JButton okBtn;
	private JButton cancleBtn;

	private JLabel label_1;
	private JLabel label_2;
	private JLabel label_3;
	private JLabel label_4;
	private JLabel standardLable;
	private JLabel waringLable;

	/**
	 * Create the frame.
	 */
	public StandardAndWaringSelectedFrame(String rawStandarStr,
			String rawWaringStr,String introduceStr,String testGistStr,String remarkStr) {
		setTitle("内控标准选择");
		setAlwaysOnTop(true);
		setBounds(100, 100, 937, 576);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		okBtn = new JButton("确认");
		okBtn.setBounds(172, 488, 93, 30);
		panel.add(okBtn);
		
		cancleBtn = new JButton("取消");
		cancleBtn.setBounds(596, 488, 93, 30);
		panel.add(cancleBtn);
		
		JPanel waringPanel = new JPanel();
		waringPanel.setBounds(472, 34, 429, 198);
		panel.add(waringPanel);
		waringPanel.setLayout(null);
		
		label_2 = new JLabel("下限");
		label_2.setFont(new Font("宋体", Font.PLAIN, 13));
		label_2.setBounds(80, 10, 54, 30);
		waringPanel.add(label_2);
		
		waringDownComBox = new JComboBox();
		waringDownComBox.setBounds(37, 62, 54, 21);
		waringPanel.add(waringDownComBox);
		waringDownComBox.setModel(new DefaultComboBoxModel(new String[] { "",">=", ">" }));
		
		waringDownEdt = new JTextField();
		waringDownEdt.setColumns(10);
		waringDownEdt.setBounds(118, 62, 66, 21);
		waringPanel.add(waringDownEdt);
		
		waringUpEdt = new JTextField();
		waringUpEdt.setColumns(10);
		waringUpEdt.setBounds(340, 62, 66, 21);
		waringPanel.add(waringUpEdt);
		
		waringUpComBox = new JComboBox();
		waringUpComBox.setBounds(267, 62, 54, 21);
		waringPanel.add(waringUpComBox);
		waringUpComBox.setModel(new DefaultComboBoxModel(new String[] { "","<=", "<" }));
		
		label_3 = new JLabel("上限");
		label_3.setFont(new Font("宋体", Font.PLAIN, 13));
		label_3.setBounds(305, 10, 54, 30);
		waringPanel.add(label_3);
		
		waringDescEdt = new JTextField();
		waringDescEdt.setColumns(10);
		waringDescEdt.setBounds(37, 130, 129, 21);
		waringPanel.add(waringDescEdt);
		
		label_4 = new JLabel("描述");
		label_4.setBounds(210, 125, 54, 30);
		waringPanel.add(label_4);
		
		JPanel standarPanel = new JPanel();
		standarPanel.setBounds(10, 34, 429, 198);
		panel.add(standarPanel);
		standarPanel.setLayout(null);
		
		standardUpComBox = new JComboBox();
		standardUpComBox.setBounds(242, 63, 54, 21);
		standarPanel.add(standardUpComBox);
		standardUpComBox.setModel(new DefaultComboBoxModel(new String[]{"","<=","<"}));
		
		standardDownComBox = new JComboBox();
		standardDownComBox.setBounds(33, 63, 54, 21);
		standarPanel.add(standardDownComBox);
		standardDownComBox.setModel(new DefaultComboBoxModel(new String[]{"",">=", ">" }));
		
		standardDescEdt = new JTextField();
		standardDescEdt.setBounds(33, 130, 129, 21);
		standarPanel.add(standardDescEdt);
		standardDescEdt.setColumns(10);
		
		standardDownEdt = new JTextField();
		standardDownEdt.setBounds(96, 63, 66, 21);
		standarPanel.add(standardDownEdt);
		standardDownEdt.setColumns(10);
		
		standardUpEdt = new JTextField();
		standardUpEdt.setBounds(318, 63, 66, 21);
		standarPanel.add(standardUpEdt);
		standardUpEdt.setColumns(10);
		
		JLabel lblNewLabel = new JLabel("上限");
		lblNewLabel.setBounds(275, 10, 54, 30);
		standarPanel.add(lblNewLabel);
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 13));
		
		JLabel label = new JLabel("下限");
		label.setBounds(69, 10, 54, 30);
		standarPanel.add(label);
		label.setFont(new Font("宋体", Font.PLAIN, 13));
		
		label_1 = new JLabel("描述");
		label_1.setBounds(207, 125, 54, 30);
		standarPanel.add(label_1);
		
		standardLable = new JLabel("内控：");
		standardLable.setBounds(21, 9, 54, 15);
		panel.add(standardLable);
		
		waringLable = new JLabel("预警：");
		waringLable.setBounds(483, 9, 54, 15);
		panel.add(waringLable);
		
		JLabel lblNewLabel_1 = new JLabel("检测方法依据：");
		lblNewLabel_1.setBounds(122, 266, 167, 15);
		panel.add(lblNewLabel_1);
		
		testGistEdt = new JTextField();
		testGistEdt.setBounds(290, 263, 107, 21);
		panel.add(testGistEdt);
		testGistEdt.setColumns(10);
		
		JLabel lblNewLabel_2 = new JLabel("指标名称备注：");
		lblNewLabel_2.setBounds(122, 315, 130, 15);
		panel.add(lblNewLabel_2);
		
		indexIntroductEdt = new JTextArea();
		indexIntroductEdt.setBounds(290, 311, 492, 76);
		panel.add(indexIntroductEdt);
		
		JLabel label_5 = new JLabel("备注：");
		label_5.setBounds(122, 427, 130, 15);
		panel.add(label_5);
		
		remarkEdt = new JTextField();
		remarkEdt.setColumns(10);
		remarkEdt.setBounds(290, 424, 107, 21);
		panel.add(remarkEdt);
		

		// ===============初始化 内控值
		UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(rawStandarStr);
		if(!StringsUtil.isEmpty(initUpAndDonwBean.detectValue)){
			standardDescEdt.setText(rawStandarStr);
		}else{
			//下限
			standardDownEdt.setText(initUpAndDonwBean.down);
			if(">".equals(initUpAndDonwBean.downSymbol)){
				standardDownComBox.setSelectedIndex(2);
			}else if(">=".equals(initUpAndDonwBean.downSymbol)){
				standardDownComBox.setSelectedIndex(1);
			}else{
				standardDownComBox.setSelectedIndex(0);
			}
			
			
			//上限
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

		//===========初始化预警值

		initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(rawWaringStr);
		if (!StringsUtil.isEmpty(initUpAndDonwBean.detectValue)) {
			standardDescEdt.setText(rawStandarStr);
		} else {
			// 下限
			waringDownEdt.setText(initUpAndDonwBean.down);
			if (">".equals(initUpAndDonwBean.downSymbol)) {
				waringDownComBox.setSelectedIndex(2);
			} else if (">=".equals(initUpAndDonwBean.downSymbol)) {
				waringDownComBox.setSelectedIndex(1);
			} else {
				waringDownComBox.setSelectedIndex(0);
			}

			// 上限
			waringUpEdt.setText(initUpAndDonwBean.up);
			if ("<".equals(initUpAndDonwBean.upSymbol)) {
				waringUpComBox.setSelectedIndex(2);
			} else if ("<=".equals(initUpAndDonwBean.upSymbol)) {
				waringUpComBox.setSelectedIndex(1);
			} else {
				waringUpComBox.setSelectedIndex(0);
			}
		}
		

		if (StringsUtil.isEmpty(rawWaringStr)) {
			waringDownComBox.setSelectedIndex(1);
			waringUpComBox.setSelectedIndex(1);
		}
		
		//==初始化 检测方法和指标说明
		indexIntroductEdt.setText(introduceStr);
		testGistEdt.setText(testGistStr);
		remarkEdt.setText(remarkStr);
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String standardResult = "";
				String waringResult = "";
				
				//拼装 内控标准的结果值
				String standardUpEdtStr = standardUpEdt.getText().toString();
				String standardDownEdtStr = standardDownEdt.getText().toString();

				String standardUpComStr = standardUpComBox.getSelectedItem()
						.toString();
				String standardDownComStr = standardDownComBox
						.getSelectedItem().toString();

				String standardDesc = standardDescEdt.getText().toString();
				
				if (!StringsUtil.isNumeric(standardUpEdtStr)
						&& !StringsUtil.isEmpty(standardUpEdtStr)) {
					MessageBox.post("范围请填写数字", "提示", MessageBox.INFORMATION);
					return;
				}

				if (!StringsUtil.isNumeric(standardDownEdtStr)
						&& !StringsUtil.isEmpty(standardDownEdtStr)) {
					MessageBox.post("范围请填写数字", "提示", MessageBox.INFORMATION);
					return;
				}
				
				if (!StringsUtil.isEmpty(standardDesc)) {
					if (!StringsUtil.isEmpty(standardUpEdtStr)
							|| !StringsUtil.isEmpty(standardDownEdtStr)) {
						MessageBox.post("请填写数字或者只填写文字描述", "提示",
								MessageBox.INFORMATION);
						return;
					}
				}

				standardResult = TechStandardUtil.initResult(standardUpEdtStr, standardDownEdtStr,
						standardUpComStr, standardDownComStr, standardDesc).resultStr;
				

				
				
				
				//拼装预警值的结果值
				String waringUpEdtStr = waringUpEdt.getText().toString();
				String waringDownEdtStr = waringDownEdt.getText()
						.toString();

				String waringUpComStr = waringUpComBox.getSelectedItem()
						.toString();
				String waringDownComStr = waringDownComBox
						.getSelectedItem().toString();

				String waringDesc = waringDescEdt.getText().toString();
				
				if (!StringsUtil.isNumeric(waringUpEdtStr)
						&& !StringsUtil.isEmpty(waringUpEdtStr)) {
					MessageBox.post("范围请填写数字", "提示", MessageBox.INFORMATION);
					return;
				}

				if (!StringsUtil.isNumeric(waringDownEdtStr)
						&& !StringsUtil.isEmpty(waringDownEdtStr)) {
					MessageBox.post("范围请填写数字", "提示", MessageBox.INFORMATION);
					return;
				}

				if (!StringsUtil.isEmpty(waringDesc)) {
					if (!StringsUtil.isEmpty(waringUpEdtStr)
							|| !StringsUtil.isEmpty(waringDownEdtStr)) {
						MessageBox.post("请填写数字或者只填写文字描述", "提示",
								MessageBox.INFORMATION);
						return;
					}
				}
				
				waringResult = TechStandardUtil.initResult(waringUpEdtStr, waringDownEdtStr,
						waringUpComStr, waringDownComStr, waringDesc).resultStr;
				

				//检测方法依据和指标说明
				indexTestGist = testGistEdt.getText().toString();
				indexIntroduce = indexIntroductEdt.getText().toString();
				indexRemark = remarkEdt.getText().toString();
				
				callBack.setStandardAndWaringResult(standardResult, waringResult,indexTestGist,indexIntroduce,indexRemark);
				
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
