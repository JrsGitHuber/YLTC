package com.uds.yl.ui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.KeyStore.PrivateKeyEntry;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.commands.voidDS.VoidDigitalSignatureDataBean;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.utils.StringsUtil;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

//����ѡ��
/**
 * @author infodba
 * "��Һ", "����", "��Ͷ����", "��Ͷ����", "��Ͷ�ɿ���", "��Ͷ����"
 *	"1000", "100", "*"
 */
public class ComplementFrame extends JFrame {

	private JPanel contentPane;
	private JButton cancleBtn;
	private JButton okBtn;
	private JComboBox typeCombox;//���������
	private JComboBox complementCombox;//��������

	private AbstractCallBack mCallBack;

	/**
	 * Create the frame.
	 */
	public ComplementFrame(String type,String content,AbstractCallBack callBack) {
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		this.mCallBack = callBack;
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel lblNewLabel_1 = new JLabel("������*��ʾ��Ӷ��پ��Ƕ��ٲ�����");
		lblNewLabel_1.setBounds(111, 21, 255, 15);
		contentPane.add(lblNewLabel_1);
		
		
		typeCombox = new JComboBox();
		typeCombox.setModel(new DefaultComboBoxModel(new String[] {"��Һ", "����", "��Ͷ����", "��Ͷ����", "��Ͷ�ɿ���", "��Ͷ����"}));
		typeCombox.setBounds(202, 53, 106, 30);
		contentPane.add(typeCombox);
		
		JLabel lblNewLabel = new JLabel("���ͣ�");
		lblNewLabel.setBounds(74, 53, 54, 30);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("���㣺");
		label.setBounds(74, 129, 54, 30);
		contentPane.add(label);
		
		complementCombox = new JComboBox();
		complementCombox.setModel(new DefaultComboBoxModel(new String[] {"1000", "100", "*"}));
		complementCombox.setBounds(202, 129, 106, 30);
		contentPane.add(complementCombox);
		
		
		okBtn = new JButton("ȷ��");
		okBtn.setBounds(72, 199, 93, 30);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(273, 199, 93, 30);
		contentPane.add(cancleBtn);
		
		//��ʼ����������ͺ���ֵ
		initCombox(type,content);
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
				return ;
			}
		});
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String type =  typeCombox.getSelectedItem().toString();
				String complementText = complementCombox.getSelectedItem().toString();
				boolean passFlag = false;
				//��Ҫ�����������ж�
				if (!StringsUtil.isEmpty(type)) {// ���type��Ϊ����һ��Ҫ��֤�������ݵ���ȷ����
					if ("��Һ".equals(type)) {// ��ˮ1000kg ˮ�к���
						if("1000".equals(complementText)){//1000����
							passFlag = true;
						}
					}
					if ("����".equals(type)) {// ��ˮ��100kg  ˮ�к���
						if("100".equals(complementText)){
							passFlag = true;
						}
					}
					if ("��Ͷ����".equals(type)) {//Ͷ�����Ƕ���  Ҫ���������
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					if ("��Ͷ����".equals(type)) {//Ͷ�����Ƕ���  Ҫ���������
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					if ("��Ͷ�ɿ���".equals(type)) {//Ͷ�����Ƕ���"*"  ��*�Ŵ���Ͷ���پ��Ƕ���
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					if ("��Ͷ����".equals(type)) {// 1�̶�
						if("*".equals(complementText)){
							passFlag = true;
						}
					}
					
				}
				
				if(!passFlag){//�����Ϲ涨
					MessageBox.post("���鲹������ͺͶ�Ӧ�Ĳ����ֵ��","",MessageBox.INFORMATION);
					return;
				}
				mCallBack.setCompelemnet(type, complementText);
				dispose();
			}
		});
	}
	
	
	/**
	 * @param type		��ֵ�����
	 * @param complementContent  ��ֵĲ���ĵ�λֵ
	 */
	private void initCombox(String type,String complementContent){
		if ("��Һ".equals(type)) {// 0
			typeCombox.setSelectedIndex(0);
		}
		if ("����".equals(type)) {// 1
			typeCombox.setSelectedIndex(1);
		}
		if ("��Ͷ����".equals(type)) {//2
			typeCombox.setSelectedIndex(2);
		}
		if ("��Ͷ����".equals(type)) {//3
			typeCombox.setSelectedIndex(3);
		}
		if ("��Ͷ�ɿ���".equals(type)) {//4
			typeCombox.setSelectedIndex(4);
		}
		if ("��Ͷ����".equals(type)) {// 5
			typeCombox.setSelectedIndex(5);
		}
		
		if(StringsUtil.isEmpty(complementContent)){
			complementCombox.setSelectedIndex(2);//���Ϊ�յĻ�Ĭ����ѡ��*
		}else if("1000".equals(complementContent)){
			complementCombox.setSelectedIndex(0);
		}else if("100".equals(complementContent)){
			complementCombox.setSelectedIndex(1);
		}else if("*".equals(complementContent)){
			complementCombox.setSelectedIndex(2);
		}
	}
}
