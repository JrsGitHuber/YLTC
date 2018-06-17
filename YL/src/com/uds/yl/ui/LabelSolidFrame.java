package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.controler.LabelGeneratorSolidExcelControler;
import com.uds.yl.tcutils.QueryUtil;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

//固态标签生成器  要计算损耗
public class LabelSolidFrame extends JFrame {

	private JPanel contentPane;
	
	private JComboBox wetLossCombox;//用来显示湿法损耗的combox
	private JComboBox dryLossCombox;//用来显示干法损耗的combox
	private JComboBox dateLossCombox;//用来显示保质期损耗的combox
	private JComboBox typeCombox;//用来显示配方类别的combox
	private JButton showWetLossBtn;//湿法损耗模式的查看
	private JButton showDryLossBtn;//干法损耗模式的查看
	private JButton showDateLossBtn;//保质期损耗模式的查看
	private JButton okBtn;
	private JButton cancleBtn;
	
	
	private List<String> lossNameList;//损耗模式中显示在combox中的值
	private List<TCComponentItemRevision> lossItemRevList;//模式损耗的所有的集合
	private TCComponentItemRevision wetLossItemRevsion;//选中的湿法损耗模式
	private TCComponentItemRevision dryLossItemRevsion;//选中的干法损耗模式
	private TCComponentItemRevision dateLossItemRevsion;//选中的保质期损耗模式
	
	
	private TCComponentItemRevision itemRevision ;

	/**
	 * Create the frame.
	 */
	public LabelSolidFrame(TCComponentItemRevision itemRv ) {
		this.itemRevision = itemRv;
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		
		JLabel label_2 = new JLabel("湿法损耗:");
		label_2.setBounds(18, 51, 111, 15);
		contentPane.add(label_2);
		
		
		wetLossCombox = new JComboBox();
		wetLossCombox.setBounds(167, 48, 111, 21);
		contentPane.add(wetLossCombox);
		
		showWetLossBtn = new JButton("查看");
		showWetLossBtn.setBounds(315, 47, 77, 23);
		contentPane.add(showWetLossBtn);
		
		
		JLabel label_8 = new JLabel("干法损耗：");
		label_8.setBounds(18, 99, 125, 15);
		contentPane.add(label_8);
		
		JLabel label_9 = new JLabel("保质期损耗：");
		label_9.setBounds(18, 155, 111, 15);
		contentPane.add(label_9);
		
		dryLossCombox = new JComboBox();
		dryLossCombox.setBounds(167, 96, 111, 21);
		contentPane.add(dryLossCombox);
		
		dateLossCombox = new JComboBox();
		dateLossCombox.setBounds(167, 152, 111, 21);
		contentPane.add(dateLossCombox);
		
		showDryLossBtn = new JButton("查看");
		showDryLossBtn.setBounds(315, 95, 77, 23);
		contentPane.add(showDryLossBtn);
		
		showDateLossBtn = new JButton("查看");
		showDateLossBtn.setBounds(315, 151, 77, 23);
		contentPane.add(showDateLossBtn);
		
		okBtn = new JButton("确定");
		okBtn.setBounds(75, 213, 93, 23);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("取消");
		cancleBtn.setBounds(260, 213, 93, 23);
		contentPane.add(cancleBtn);
		
		
		//初始化损耗的说啊
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LOSSITEM.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[] { "名称" }, new String[] { "*" });
		lossNameList = new ArrayList<>();
		lossItemRevList = new ArrayList<>();
		for (TCComponent component : searchResult) {
			if (component instanceof TCComponentItem) {
				TCComponentItem lossItem = (TCComponentItem) component;
				try {
					String lossItemName = lossItem.getProperty("object_name");
					TCComponentItemRevision lossItemRev = lossItem.getLatestItemRevision();
					if (lossItemRev == null) {
						continue;
					}
					lossNameList.add(lossItemName);
					wetLossCombox.addItem(lossItemName);
					dryLossCombox.addItem(lossItemName);
					dateLossCombox.addItem(lossItemName);
					lossItemRevList.add(lossItemRev);
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		// 添加一个空的选项
		lossNameList.add(" ");
		wetLossCombox.addItem(" ");
		dryLossCombox.addItem(" ");
		dateLossCombox.addItem(" ");
		lossItemRevList.add(null);

		if (lossItemRevList.size() > 0) {// 如果有损耗对象的话 默认选中第一个
			wetLossItemRevsion = lossItemRevList.get(0);
			dryLossItemRevsion = lossItemRevList.get(0);
			dateLossItemRevsion = lossItemRevList.get(0);
		}
		
		
		//设置湿法损耗模式的combox的监听事件
		wetLossCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = wetLossCombox.getSelectedIndex();
				if(selectIndex!=-1){//不是-1就表示选中了一个
					wetLossItemRevsion = lossItemRevList.get(selectIndex);
				}
			}
		});
		
		//设置干法损耗模式的combox的监听事件
		dryLossCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = dryLossCombox.getSelectedIndex();
				if(selectIndex!=-1){//不是-1就表示选中了一个
					dryLossItemRevsion = lossItemRevList.get(selectIndex);
				}
			}
		});
		
		//设置保质期损耗模式的combox的监听事件
		dateLossCombox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectIndex = dateLossCombox.getSelectedIndex();
				if(selectIndex!=-1){//不是-1就表示选中了一个
					dateLossItemRevsion = lossItemRevList.get(selectIndex);
				}
			}
		});
		
		
		//查看湿法损耗模式的
		showWetLossBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(wetLossItemRevsion==null){//没有损耗对象
					MessageBox.post("请选择要查看的损耗对象","",MessageBox.INFORMATION);
					return;
				}
				ShowLossFrame showLossFrame = new ShowLossFrame(wetLossItemRevsion);
				showLossFrame.setVisible(true);
				
			}
		});
		
		//查看干法损耗模式
		showDryLossBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dryLossItemRevsion==null){//没有损耗对象
					MessageBox.post("请选择要查看的损耗对象","",MessageBox.INFORMATION);
					return;
				}
				ShowLossFrame showLossFrame = new ShowLossFrame(dryLossItemRevsion);
				showLossFrame.setVisible(true);
			}
		});
		
		
		//查看保质期的损耗模式
		showDateLossBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(dateLossItemRevsion==null){//没有损耗对象
					MessageBox.post("请选择要查看的损耗对象","",MessageBox.INFORMATION);
					return;
				}
				ShowLossFrame showLossFrame = new ShowLossFrame(dateLossItemRevsion);
				showLossFrame.setVisible(true);
			}
		});
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				LabelGeneratorSolidExcelControler labelGeneratorSolidExcelControler = new LabelGeneratorSolidExcelControler();
				labelGeneratorSolidExcelControler.userTask(itemRevision,
						wetLossItemRevsion,dryLossItemRevsion,dateLossItemRevsion);
			}
		});
		
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		
		
	}
}
