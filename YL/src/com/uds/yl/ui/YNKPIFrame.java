package com.uds.yl.ui;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import org.jacorb.trading.constraint.DoubleValue;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentDataset;
import com.teamcenter.rac.kernel.TCComponentForm;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.tcutils.FormUtil;
import com.uds.yl.utils.DateUtil;
import com.uds.yl.utils.StringsUtil;

import java.awt.Color;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.WindowConstants;


//Һ�̼�Ч��ֽ���
public class YNKPIFrame extends JFrame {

	private JPanel contentPane;
	
	private JTextField nameEdt;//����
	private JTextField ownerEdt;//������
	private JTextField selfScoreEdt;//�Լ���ķ���
	private JTextField dateEdt;//����
	private JTextField scoreEdt;//��ķ���
	private JTextField taskNameEdt;//����ڵ������
	private JComboBox comboBox;
	private String symbol="+";//���� Ĭ��������
	private JButton okBtn;//ȷ��
	private JButton cancleBtn;//ȡ��
	private String taskName="";//��ǰ��������ڵ������
	
	
	private TCComponentDataset mDataset;//ѡ�е����ݼ�
	private TCComponentForm mForm;//������form��

	private JTextArea commentEdt;
	
	/**
	 * Create the frame.
	 */
	public YNKPIFrame(TCComponentDataset dataset) {
		this.mDataset = dataset;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 681, 408);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("����:");
		label.setBounds(28, 47, 54, 15);
		contentPane.add(label);
		
		selfScoreEdt = new JTextField();
		selfScoreEdt.setColumns(10);
		selfScoreEdt.setBounds(104, 44, 114, 21);
		contentPane.add(selfScoreEdt);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(44, 97, 590, 190);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("���");
		lblNewLabel_2.setBounds(10, 10, 87, 21);
		panel.add(lblNewLabel_2);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"+", "-"}));
		comboBox.setBounds(147, 31, 51, 21);
		panel.add(comboBox);
		
		scoreEdt = new JTextField();
		scoreEdt.setBounds(231, 31, 102, 21);
		panel.add(scoreEdt);
		scoreEdt.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(129, 77, 418, 92);
		panel.add(scrollPane);
		
		commentEdt = new JTextArea();
		scrollPane.setViewportView(commentEdt);
		
		okBtn = new JButton("ȷ��");
		okBtn.setBounds(140, 313, 93, 23);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("ȡ��");
		cancleBtn.setBounds(392, 313, 93, 23);
		contentPane.add(cancleBtn);
		
		JLabel label_2 = new JLabel("����ڵ�:");
		label_2.setBounds(339, 50, 71, 15);
		contentPane.add(label_2);
		
		taskNameEdt = new JTextField();
		taskNameEdt.setColumns(10);
		taskNameEdt.setBounds(415, 47, 114, 21);
		contentPane.add(taskNameEdt);
		
		{//��ʼ��
			selfScoreEdt.setEditable(false);
			taskNameEdt.setEditable(false);
			try {
				String selfScore = mDataset.getProperty("u8_self");
				selfScoreEdt.setText(getCurrentSumScore(dataset,selfScore));
				TCComponent[] referenceListProperty = dataset.getReferenceListProperty("process_stage_list");
				
				TCComponent task = referenceListProperty[referenceListProperty.length-1];
				taskName = task.getProperty("object_name");
				taskNameEdt.setText(taskName);
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				symbol = comboBox.getSelectedItem().toString();
			}
		});
		
		
		okBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//�ж�������Ƿ�������
				String score = scoreEdt.getText().toString();
				if(StringsUtil.isNoNumeric(score)){//�����а����˲������ֵ�����
					MessageBox.post("����������","",MessageBox.INFORMATION);
					return ;
				}
				
				//����һ��Form
				String userName = UserInfoSingleFactory.getInstance().getTCSession().getUserName().toString();
				String dateStr = DateUtil.getDateStr(new Date(),"yyyy-MM-dd");
				String formName = userName +"-"+dateStr;
				String commnets = commentEdt.getText().toString();
				TCComponentForm kpiForm = FormUtil.createtForm("U8_YNKPI",formName,"");
				//��Form���ĸ�����дֵ
				score = symbol+scoreEdt.getText().toString();
				try {
					kpiForm.setProperty("u8_reviewdate", dateStr);//����
					kpiForm.setProperty("u8_reviewer", userName);//�����
					kpiForm.setProperty("u8_reviewtaskname", taskName);//�ڵ�����
					kpiForm.setProperty("u8_score", score);//����
					kpiForm.setProperty("u8_comments", commnets);//����
					mDataset.add("IMAN_external_object_link", kpiForm);//�ϴ�
					
					dispose();
					MessageBox.post("OK","",MessageBox.INFORMATION);
				} catch (TCException e1) {
					e1.printStackTrace();
				}
			}
		});
		
		
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
	}
	
	
	/**
	 * ��ȡ��ǰ�ķ���
	 * @param dataSet ѡ�е����ݼ�
	 * @param sumScore   �Լ���ķ�
	 * @return
	 */
	private String getCurrentSumScore(TCComponentDataset dataSet,String sumScore){
		Double currentSumScore = StringsUtil.convertStr2Double(sumScore);
		try {
			TCComponent[] components = dataSet.getRelatedComponents("IMAN_external_object_link");
			for(TCComponent component : components){
				TCComponentForm form = (TCComponentForm) component;
				String formScore = form.getProperty("u8_score");
				if('+'==formScore.charAt(0)){
					currentSumScore += StringsUtil.convertStr2Double(formScore.substring(1));
				}else if('-' == formScore.charAt(0)){
					currentSumScore -= StringsUtil.convertStr2Double(formScore.substring(1));
				}
			}
		} catch (TCException e) {
			e.printStackTrace();
		}
		return ""+currentSumScore;
	}
}