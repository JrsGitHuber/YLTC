package com.uds.yl.ui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.mail.Message;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.tcutils.FormUtil;
import com.uds.yl.utils.StringsUtil;

public class OneScoreFrame extends JFrame {
	private JPanel contentPane;
	private JTextField strategicTextField;

	private TCComponentForm scoreForm;// 打分表

	private AbstractCallBack mCallBack;
	
	/**
	 * @param form	1、代表的是打分表		0、代表的是提案信息表
	 * @param code  1、代表有本用户的form   0、代表没有本用户的form
	 */
	public OneScoreFrame(final TCComponentForm form,final int code,AbstractCallBack callBack) {
		if(code==1){
			this.scoreForm = form;
		}
		this.mCallBack = callBack;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 652, 334);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton okBtn = new JButton("确定");
		okBtn.setBounds(104, 239, 93, 30);
		contentPane.add(okBtn);

		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(355, 239, 93, 30);
		contentPane.add(cancleBtn);

		JLabel lblNewLabel = new JLabel("战略价值：");
		lblNewLabel.setFont(new Font("宋体", Font.PLAIN, 14));
		lblNewLabel.setBounds(118, 62, 90, 20);
		contentPane.add(lblNewLabel);

		strategicTextField = new JTextField();
		strategicTextField.setBounds(248, 62, 120, 21);
		contentPane.add(strategicTextField);
		strategicTextField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("请输入0-9的数字");
		lblNewLabel_1.setBounds(219, 126, 143, 20);
		contentPane.add(lblNewLabel_1);
		
		JLabel label = new JLabel("战略价值：战略价值不高（0-3分）；有一定的战略价值（3-6分）；有很高的战略价值（6-9分）");
		label.setBounds(46, 154, 562, 20);
		contentPane.add(label);

		// 初始化
		if(code==1){
			try {
				String u8_strategic = scoreForm.getProperty("u8_strategic");
				strategicTextField.setText(u8_strategic);
			} catch (TCException e1) {
				e1.printStackTrace();
			}
		}

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				// 确定
				String strategic = strategicTextField.getText();
				System.out.println();
				if (!isTextFieldMathRul(strategic)) {// 如果为空或者包含字符
					MessageBox.post("请输入0-9的数字", "", MessageBox.INFORMATION);
					return;
				}
				
				if(code==0){
					try {
						TCComponent[] scoreRelList = form.getReferenceListProperty("U8_ScoreREL");
						String formScoreName = form.getProperty("object_name") + "_" + "评分表";
						TCComponentForm scoreRelForm = FormUtil.createtForm("U8_ReportScore", formScoreName, "");
						scoreForm = scoreRelForm;
						TCComponent[] allScoreFomrs = new TCComponentForm[scoreRelList.length + 1];
						for (int i = 0; i < scoreRelList.length; i++) {
							allScoreFomrs[i] = scoreRelList[i];
						}
						allScoreFomrs[scoreRelList.length] = scoreRelForm;
						form.setRelated("U8_ScoreREL", allScoreFomrs);
					} catch (TCException e1) {
						e1.printStackTrace();
					}
				}
				
				
				
				// 回写
				try {
					scoreForm.setProperty("u8_strategic", strategic);
					
					//在创意form的desc上追加上当前打分用户id
					String userId = UserInfoSingleFactory.getInstance().getUser().getUserId();
					mCallBack.setUserIdInProposalForm(userId);
					dispose();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		});

		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// 取消
				dispose();
				return;
			}
		});

	}
	
	
	/**
	 * 不为空，不包含字符，而且是整数 并且在0-9之间
	 * @param textField
	 * @return
	 */
	public boolean isTextFieldMathRul(String textField){
		if(!StringsUtil.isNumeric(textField)){
			return false;
		}
		if(StringsUtil.isEmpty(textField)){
			return false;
		}	
		if(textField.contains(".")){
			return false;
		}
		if(textField.length()>1||textField.length()==0){
			return false;
		}
		if(textField.charAt(0)<'0'||textField.charAt(0)>'9'){
			return false;
		}
		return true;
	}
}
