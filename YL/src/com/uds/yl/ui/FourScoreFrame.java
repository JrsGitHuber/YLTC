package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import com.uds.yl.tcutils.PrivilegeUtil;
import com.uds.yl.utils.StringsUtil;

public class FourScoreFrame extends JFrame {

	private JPanel contentPane;
	private JTextField technicalfeaTextField;
	private JTextField statutefeaTextField;
	private JTextField marketTextField;
	private JTextField innovativenessTextField;
	private TCComponentForm scoreForm;

	private AbstractCallBack mCallBack;
	/**
	 * @param form	1、代表的是打分表		0、代表的是提案信息表
	 * @param code  1、代表有本用户的form   0、代表没有本用户的form
	 */
	public FourScoreFrame(final TCComponentForm form,final int code,AbstractCallBack callBack) {
		if(code==1){
			this.scoreForm = form;
		}
		
		this.mCallBack = callBack;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 703, 454);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(85, 342, 93, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(435, 342, 93, 30);
		contentPane.add(cancleBtn);
		
		JLabel lblNewLabel = new JLabel("技术可行性：");
		lblNewLabel.setBounds(31, 44, 112, 20);
		contentPane.add(lblNewLabel);
		
		technicalfeaTextField = new JTextField();
		technicalfeaTextField.setBounds(153, 44, 83, 21);
		contentPane.add(technicalfeaTextField);
		technicalfeaTextField.setColumns(10);
		
		JLabel label = new JLabel("法规可行性：");
		label.setBounds(382, 44, 93, 20);
		contentPane.add(label);
		
		statutefeaTextField = new JTextField();
		statutefeaTextField.setColumns(10);
		statutefeaTextField.setBounds(512, 44, 80, 21);
		contentPane.add(statutefeaTextField);
		
		JLabel label_1 = new JLabel("市场可行性：");
		label_1.setBounds(31, 119, 99, 20);
		contentPane.add(label_1);
		
		marketTextField = new JTextField();
		marketTextField.setColumns(10);
		marketTextField.setBounds(153, 119, 83, 21);
		contentPane.add(marketTextField);
		
		JLabel label_2 = new JLabel("创新性：");
		label_2.setBounds(382, 119, 93, 20);
		contentPane.add(label_2);
		
		innovativenessTextField = new JTextField();
		innovativenessTextField.setColumns(10);
		innovativenessTextField.setBounds(512, 119, 80, 21);
		contentPane.add(innovativenessTextField);
		
		JLabel lblNewLabel_1 = new JLabel("请输入0-9的数字");
		lblNewLabel_1.setBounds(233, 173, 146, 15);
		contentPane.add(lblNewLabel_1);
		
		JLabel label_3 = new JLabel("市场可行性：可行性较小（0-3分）；有一定的可行性（3-6分）；可行性很大（6-9分）");
		label_3.setBounds(64, 207, 528, 15);
		contentPane.add(label_3);
		
		JLabel label_4 = new JLabel("技术可行性：可行性较小（0-3分）；有一定的可行性（3-6分）；可行性很大（6-9分）");
		label_4.setBounds(64, 232, 528, 15);
		contentPane.add(label_4);
		
		JLabel label_5 = new JLabel("法规可行性：高风险（0-3分）；有一定的风险有风险（3-6分）；风险非常小（6-9分）");
		label_5.setBounds(64, 257, 528, 15);
		contentPane.add(label_5);
		
		JLabel label_6 = new JLabel("创新性：创新性不高（0-3分）；有一定的创新性（3-6分）；创新性很高（6-9分）");
		label_6.setBounds(64, 282, 528, 15);
		contentPane.add(label_6);
		
		//初始化
		if(code==1){
			try {
				String u8_technicalfea = scoreForm.getProperty("u8_technicalfea");
				String u8_statutefea = scoreForm.getProperty("u8_statutefea");
				String u8_marketfea = scoreForm.getProperty("u8_marketfea");
				String u8_innovativeness = scoreForm.getProperty("u8_innovativeness");
				technicalfeaTextField.setText(u8_technicalfea);
				statutefeaTextField.setText(u8_statutefea);
				marketTextField.setText(u8_marketfea);
				innovativenessTextField.setText(u8_innovativeness);
			} catch (TCException e1) {
				e1.printStackTrace();
			}
		}
		
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				//判断确定
				String technicalfea = technicalfeaTextField.getText();
				String statutefea = statutefeaTextField.getText();
				String market = marketTextField.getText();
				String innovativeness = innovativenessTextField.getText();
				  
				if(isTextFieldMathRul(technicalfea)&&isTextFieldMathRul(market)
						&&isTextFieldMathRul(statutefea)&&isTextFieldMathRul(innovativeness)){//如果为空或者包含字符
					
				}else{
					MessageBox.post("请输入0-9的数字","",MessageBox.INFORMATION);
					return;
				}
				
				if(code==0){
					try {
						//赋予当前用户对创意提报表的读权限
						PrivilegeUtil.grantUserPrivilege(form, PrivilegeUtil.PRIVILEGE_KEY.READ);
						
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
				
				//回写
				try {
					scoreForm.setProperty("u8_technicalfea", technicalfea);
					scoreForm.setProperty("u8_statutefea", statutefea);
					scoreForm.setProperty("u8_marketfea", market);
					scoreForm.setProperty("u8_innovativeness", innovativeness);
					
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