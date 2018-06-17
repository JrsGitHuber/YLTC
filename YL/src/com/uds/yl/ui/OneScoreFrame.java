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

	private TCComponentForm scoreForm;// ��ֱ�

	private AbstractCallBack mCallBack;
	
	/**
	 * @param form	1��������Ǵ�ֱ�		0����������᰸��Ϣ��
	 * @param code  1�������б��û���form   0������û�б��û���form
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

		JButton okBtn = new JButton("ȷ��");
		okBtn.setBounds(104, 239, 93, 30);
		contentPane.add(okBtn);

		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(355, 239, 93, 30);
		contentPane.add(cancleBtn);

		JLabel lblNewLabel = new JLabel("ս�Լ�ֵ��");
		lblNewLabel.setFont(new Font("����", Font.PLAIN, 14));
		lblNewLabel.setBounds(118, 62, 90, 20);
		contentPane.add(lblNewLabel);

		strategicTextField = new JTextField();
		strategicTextField.setBounds(248, 62, 120, 21);
		contentPane.add(strategicTextField);
		strategicTextField.setColumns(10);
		
		JLabel lblNewLabel_1 = new JLabel("������0-9������");
		lblNewLabel_1.setBounds(219, 126, 143, 20);
		contentPane.add(lblNewLabel_1);
		
		JLabel label = new JLabel("ս�Լ�ֵ��ս�Լ�ֵ���ߣ�0-3�֣�����һ����ս�Լ�ֵ��3-6�֣����кܸߵ�ս�Լ�ֵ��6-9�֣�");
		label.setBounds(46, 154, 562, 20);
		contentPane.add(label);

		// ��ʼ��
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
				
				// ȷ��
				String strategic = strategicTextField.getText();
				System.out.println();
				if (!isTextFieldMathRul(strategic)) {// ���Ϊ�ջ��߰����ַ�
					MessageBox.post("������0-9������", "", MessageBox.INFORMATION);
					return;
				}
				
				if(code==0){
					try {
						TCComponent[] scoreRelList = form.getReferenceListProperty("U8_ScoreREL");
						String formScoreName = form.getProperty("object_name") + "_" + "���ֱ�";
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
				
				
				
				// ��д
				try {
					scoreForm.setProperty("u8_strategic", strategic);
					
					//�ڴ���form��desc��׷���ϵ�ǰ����û�id
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
				// ȡ��
				dispose();
				return;
			}
		});

	}
	
	
	/**
	 * ��Ϊ�գ��������ַ������������� ������0-9֮��
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
