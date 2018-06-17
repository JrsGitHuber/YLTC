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


//液奶绩效打分界面
public class YNKPIFrame extends JFrame {

	private JPanel contentPane;
	
	private JTextField nameEdt;//名称
	private JTextField ownerEdt;//所有者
	private JTextField selfScoreEdt;//自己打的分数
	private JTextField dateEdt;//日期
	private JTextField scoreEdt;//打的分数
	private JTextField taskNameEdt;//任务节点的名称
	private JComboBox comboBox;
	private String symbol="+";//符号 默认是正的
	private JButton okBtn;//确认
	private JButton cancleBtn;//取消
	private String taskName="";//当前流程任务节点的名称
	
	
	private TCComponentDataset mDataset;//选中的数据集
	private TCComponentForm mForm;//创建的form表单

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
		
		JLabel label = new JLabel("分数:");
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
		
		JLabel lblNewLabel_2 = new JLabel("打分");
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
		
		okBtn = new JButton("确定");
		okBtn.setBounds(140, 313, 93, 23);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("取消");
		cancleBtn.setBounds(392, 313, 93, 23);
		contentPane.add(cancleBtn);
		
		JLabel label_2 = new JLabel("任务节点:");
		label_2.setBounds(339, 50, 71, 15);
		contentPane.add(label_2);
		
		taskNameEdt = new JTextField();
		taskNameEdt.setColumns(10);
		taskNameEdt.setBounds(415, 47, 114, 21);
		contentPane.add(taskNameEdt);
		
		{//初始化
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
				//判定输入的是否是数字
				String score = scoreEdt.getText().toString();
				if(StringsUtil.isNoNumeric(score)){//分数中包含了不是数字的内容
					MessageBox.post("请输入数字","",MessageBox.INFORMATION);
					return ;
				}
				
				//创建一个Form
				String userName = UserInfoSingleFactory.getInstance().getTCSession().getUserName().toString();
				String dateStr = DateUtil.getDateStr(new Date(),"yyyy-MM-dd");
				String formName = userName +"-"+dateStr;
				String commnets = commentEdt.getText().toString();
				TCComponentForm kpiForm = FormUtil.createtForm("U8_YNKPI",formName,"");
				//对Form的四个属相写值
				score = symbol+scoreEdt.getText().toString();
				try {
					kpiForm.setProperty("u8_reviewdate", dateStr);//日期
					kpiForm.setProperty("u8_reviewer", userName);//审核者
					kpiForm.setProperty("u8_reviewtaskname", taskName);//节点名称
					kpiForm.setProperty("u8_score", score);//分数
					kpiForm.setProperty("u8_comments", commnets);//评论
					mDataset.add("IMAN_external_object_link", kpiForm);//上传
					
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
	 * 获取当前的分数
	 * @param dataSet 选中的数据集
	 * @param sumScore   自己打的分
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