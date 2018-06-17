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
	 * @param form	1��������Ǵ�ֱ�		0����������᰸��Ϣ��
	 * @param code  1�������б��û���form   0������û�б��û���form
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
		
		JButton okBtn = new JButton("ȷ��");
		okBtn.setBounds(85, 342, 93, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(435, 342, 93, 30);
		contentPane.add(cancleBtn);
		
		JLabel lblNewLabel = new JLabel("���������ԣ�");
		lblNewLabel.setBounds(31, 44, 112, 20);
		contentPane.add(lblNewLabel);
		
		technicalfeaTextField = new JTextField();
		technicalfeaTextField.setBounds(153, 44, 83, 21);
		contentPane.add(technicalfeaTextField);
		technicalfeaTextField.setColumns(10);
		
		JLabel label = new JLabel("��������ԣ�");
		label.setBounds(382, 44, 93, 20);
		contentPane.add(label);
		
		statutefeaTextField = new JTextField();
		statutefeaTextField.setColumns(10);
		statutefeaTextField.setBounds(512, 44, 80, 21);
		contentPane.add(statutefeaTextField);
		
		JLabel label_1 = new JLabel("�г������ԣ�");
		label_1.setBounds(31, 119, 99, 20);
		contentPane.add(label_1);
		
		marketTextField = new JTextField();
		marketTextField.setColumns(10);
		marketTextField.setBounds(153, 119, 83, 21);
		contentPane.add(marketTextField);
		
		JLabel label_2 = new JLabel("�����ԣ�");
		label_2.setBounds(382, 119, 93, 20);
		contentPane.add(label_2);
		
		innovativenessTextField = new JTextField();
		innovativenessTextField.setColumns(10);
		innovativenessTextField.setBounds(512, 119, 80, 21);
		contentPane.add(innovativenessTextField);
		
		JLabel lblNewLabel_1 = new JLabel("������0-9������");
		lblNewLabel_1.setBounds(233, 173, 146, 15);
		contentPane.add(lblNewLabel_1);
		
		JLabel label_3 = new JLabel("�г������ԣ������Խ�С��0-3�֣�����һ���Ŀ����ԣ�3-6�֣��������Ժܴ�6-9�֣�");
		label_3.setBounds(64, 207, 528, 15);
		contentPane.add(label_3);
		
		JLabel label_4 = new JLabel("���������ԣ������Խ�С��0-3�֣�����һ���Ŀ����ԣ�3-6�֣��������Ժܴ�6-9�֣�");
		label_4.setBounds(64, 232, 528, 15);
		contentPane.add(label_4);
		
		JLabel label_5 = new JLabel("��������ԣ��߷��գ�0-3�֣�����һ���ķ����з��գ�3-6�֣������շǳ�С��6-9�֣�");
		label_5.setBounds(64, 257, 528, 15);
		contentPane.add(label_5);
		
		JLabel label_6 = new JLabel("�����ԣ������Բ��ߣ�0-3�֣�����һ���Ĵ����ԣ�3-6�֣��������Ժܸߣ�6-9�֣�");
		label_6.setBounds(64, 282, 528, 15);
		contentPane.add(label_6);
		
		//��ʼ��
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
				
				//�ж�ȷ��
				String technicalfea = technicalfeaTextField.getText();
				String statutefea = statutefeaTextField.getText();
				String market = marketTextField.getText();
				String innovativeness = innovativenessTextField.getText();
				  
				if(isTextFieldMathRul(technicalfea)&&isTextFieldMathRul(market)
						&&isTextFieldMathRul(statutefea)&&isTextFieldMathRul(innovativeness)){//���Ϊ�ջ��߰����ַ�
					
				}else{
					MessageBox.post("������0-9������","",MessageBox.INFORMATION);
					return;
				}
				
				if(code==0){
					try {
						//���赱ǰ�û��Դ����ᱨ��Ķ�Ȩ��
						PrivilegeUtil.grantUserPrivilege(form, PrivilegeUtil.PRIVILEGE_KEY.READ);
						
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
				
				//��д
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