package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.mail.Message;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.interfaces.AbstractCallBack;

public class PassOrNotFrame extends JFrame {

	private JPanel contentPane;
	
	private AbstractCallBack callBack;
	private JCheckBox checkBox1;//���д���
	private JCheckBox checkBox2;//�޴�����
	private JCheckBox checkBox3;//�޼���������ֵ
	private JCheckBox checkBox4;//��ս�Է����Զ
	private JCheckBox checkBox5;//����

	/**
	 * Create the frame.
	 */
	public PassOrNotFrame(final AbstractCallBack callBack) {
		this.callBack = callBack;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 787, 443);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("�ж���");
		lblNewLabel.setBounds(55, 31, 54, 30);
		contentPane.add(lblNewLabel);
		
		final JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"pass", "deny","modify"}));
		comboBox.setBounds(144, 31, 105, 30);
		contentPane.add(comboBox);
		
		JButton okBtn = new JButton("ȷ��");
		okBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		okBtn.setBounds(137, 351, 93, 30);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("ȡ��");
		cancleBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		cancleBtn.setBounds(481, 351, 93, 30);
		contentPane.add(cancleBtn);
		
		JLabel label = new JLabel("�����");
		label.setBounds(55, 104, 54, 30);
		contentPane.add(label);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(144, 91, 539, 127);
		contentPane.add(scrollPane);
		
		final JTextArea textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		textArea.setLineWrap(true);
		
		JLabel label_1 = new JLabel("��ͨ������:");
		label_1.setBounds(55, 256, 112, 30);
		contentPane.add(label_1);
		
		checkBox1 = new JCheckBox("���д���");
		checkBox1.setBounds(146, 260, 103, 23);
		contentPane.add(checkBox1);
		
		checkBox2 = new JCheckBox("�޴�����");
		checkBox2.setBounds(262, 260, 127, 23);
		contentPane.add(checkBox2);
		
		checkBox3 = new JCheckBox("�޼���������ֵ");
		checkBox3.setBounds(405, 260, 145, 23);
		contentPane.add(checkBox3);
		
		checkBox4 = new JCheckBox("��ս�Է����Զ");
		checkBox4.setBounds(580, 260, 154, 23);
		contentPane.add(checkBox4);
		
		checkBox5 = new JCheckBox("����");
		checkBox5.setBounds(146, 306, 103, 23);
		contentPane.add(checkBox5);
		
		
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				//ȷ��
				int selectedIndex = comboBox.getSelectedIndex();
				if(selectedIndex==-1){
					MessageBox.post("��ѡ��","",MessageBox.INFORMATION);
					return;
				}
				String passOrNot = comboBox.getSelectedItem().toString();
				String comment = textArea.getText();
				
				if(passOrNot.equals("deny")){//����ǲ�ͨ�����͵Ļ� ����Ҫѡ��һ�ֲ�ͨ������
					if(!checkBox1.isSelected()&&!checkBox2.isSelected()
							&&!checkBox3.isSelected()&&!checkBox4.isSelected()
							&&!checkBox5.isSelected()){
						MessageBox.post("��ѡ��ͨ������","",MessageBox.INFORMATION);
						return ;
					}
				}
				String box1 = checkBox1.isSelected() ? "��" : "";
				String box2 = checkBox2.isSelected() ? "��" : "";
				String box3 = checkBox3.isSelected() ? "��" : "";
				String box4 = checkBox4.isSelected() ? "��" : "";
				String box5 = checkBox5.isSelected() ? "��" : "";
				try {
					callBack.setComment(passOrNot, comment,
					box1,box2,box3,box4,box5);
					dispose();
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		});
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				dispose();
				return;
			}
		});
	}
}