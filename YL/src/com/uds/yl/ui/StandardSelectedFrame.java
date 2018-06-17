package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.mail.Message;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.bean.UpAndDonwBean;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.interfaces.CallBack;
import com.uds.yl.utils.StringsUtil;
import com.uds.yl.utils.TechStandardUtil;

public class StandardSelectedFrame extends JFrame {


	
	
	private AbstractCallBack callBack;
	
	private JPanel contentPane;
	
	private JTextField upEdt;
	private JTextField downEdt;
	private JComboBox upComBox;
	private JComboBox downComBox;
	
	private JButton okBtn;
	private JButton cancleBtn;
	
	private JLabel label_1;
	private JTextField descEdt;

	/**
	 * Create the frame.
	 */
	public StandardSelectedFrame(String rawStr) {
		setTitle("内控标准选择");
//		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setAlwaysOnTop(true);
		setBounds(100, 100, 559, 298);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(null);
		
		upEdt = new JTextField();
		upEdt.setBounds(395, 55, 93, 21);
		panel.add(upEdt);
		upEdt.setColumns(10);
		
		upComBox = new JComboBox();
		upComBox.setModel(new DefaultComboBoxModel(new String[] {"", "<=", "<"}));
		upComBox.setBounds(293, 55, 73, 21);
		panel.add(upComBox);
		
		downEdt = new JTextField();
		downEdt.setColumns(10);
		downEdt.setBounds(143, 55, 83, 21);
		panel.add(downEdt);
		
		downComBox = new JComboBox();
		downComBox.setModel(new DefaultComboBoxModel(new String[] {"", ">=", ">"}));
		downComBox.setBounds(38, 55, 73, 21);
		panel.add(downComBox);
		
		JLabel lblNewLabel = new JLabel("上限");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 13));
		lblNewLabel.setBounds(385, 10, 54, 30);
		panel.add(lblNewLabel);
		
		JLabel label = new JLabel("下限");
		label.setFont(new Font("宋体", Font.PLAIN, 13));
		label.setBounds(109, 10, 54, 30);
		panel.add(label);
		
		okBtn = new JButton("确认");
		okBtn.setBounds(68, 188, 93, 30);
		panel.add(okBtn);
		
		cancleBtn = new JButton("取消");
		cancleBtn.setBounds(312, 188, 93, 30);
		panel.add(cancleBtn);
		
		label_1 = new JLabel("描述");
		label_1.setBounds(271, 112, 54, 30);
		panel.add(label_1);
		
		descEdt = new JTextField();
		descEdt.setColumns(10);
		descEdt.setBounds(49, 117, 162, 21);
		panel.add(descEdt);
		
		
		//初始化
		UpAndDonwBean initUpAndDonwBean = TechStandardUtil.initUpAndDonwBean(rawStr);
		if(!StringsUtil.isEmpty(initUpAndDonwBean.detectValue)){
			descEdt.setText(rawStr);
		}else{
			//下限
			downEdt.setText(initUpAndDonwBean.down);
			if(">".equals(initUpAndDonwBean.downSymbol)){
				downComBox.setSelectedIndex(2);
			}else if(">=".equals(initUpAndDonwBean.downSymbol)){
				downComBox.setSelectedIndex(1);
			}else{
				downComBox.setSelectedIndex(0);
			}
			
			
			//上限
			upEdt.setText(initUpAndDonwBean.up);
			if("<".equals(initUpAndDonwBean.upSymbol)){
				upComBox.setSelectedIndex(2);
			}else if("<=".equals(initUpAndDonwBean.upSymbol)){
				upComBox.setSelectedIndex(1);
			}else{
				upComBox.setSelectedIndex(0);
			}
		}
		
		
		if(StringsUtil.isEmpty(rawStr)){
			downComBox.setSelectedIndex(1);
			upComBox.setSelectedIndex(1);
		}
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String result = "";
				String upEdtStr = upEdt.getText().toString();
				String downEdtStr = downEdt.getText().toString();
				
				String upComStr = upComBox.getSelectedItem().toString();
				String downComStr = downComBox.getSelectedItem().toString();
				
				String desc = descEdt.getText().toString();
				
				if(!StringsUtil.isNumeric(upEdtStr)&&!StringsUtil.isEmpty(upEdtStr)){
					MessageBox.post("范围请填写数字","提示",MessageBox.INFORMATION);
					return;
				}
				
				if(!StringsUtil.isNumeric(downEdtStr)&&!StringsUtil.isEmpty(downEdtStr)){
					MessageBox.post("范围请填写数字","提示",MessageBox.INFORMATION);
					return;
				}
				
				result = TechStandardUtil.initResult(upEdtStr, downEdtStr,
						upComStr, downComStr, desc).resultStr;
				
				
				if(!StringsUtil.isEmpty(desc)){
					if(!StringsUtil.isEmpty(upEdtStr)||!StringsUtil.isEmpty(downEdtStr)){
						MessageBox.post("请填写数字或者只填写文字描述","提示",MessageBox.INFORMATION);
						return;
					}
				}
				callBack.setUpAndDownResult(result);
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

	public void setCallBack(AbstractCallBack callBack){
		this.callBack = callBack;
	}
}
