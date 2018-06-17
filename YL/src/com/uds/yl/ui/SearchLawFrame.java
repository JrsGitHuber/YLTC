package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.StringsUtil;

import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class SearchLawFrame extends JFrame {

	private JPanel contentPane;
	private JTextField lawNameText;
	private JTable lawTable;
	

	private List<TCComponentItemRevision> searchLawRevList;

	
	/**
	 * Create the frame.
	 */
	public SearchLawFrame(final AbstractCallBack callBack) {
		setTitle("法规查询");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 527, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("名称：");
		lblNewLabel.setBounds(56, 27, 54, 15);
		contentPane.add(lblNewLabel);
		
		lawNameText = new JTextField();
		lawNameText.setBounds(133, 24, 138, 21);
		contentPane.add(lawNameText);
		lawNameText.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(56, 79, 392, 99);
		contentPane.add(scrollPane);
		
		lawTable = new JTable();
		lawTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"名称"
			}
		));
		scrollPane.setViewportView(lawTable);
		
		JButton searchBtn = new JButton("查找");
		searchBtn.setBounds(334, 23, 93, 23);
		contentPane.add(searchBtn);
		
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(71, 211, 93, 23);
		contentPane.add(okBtn);
		
		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(261, 211, 93, 23);
		contentPane.add(cancleBtn);
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//选中的选项
				DefaultTableModel model = (DefaultTableModel) lawTable.getModel();
				int selectedRow = lawTable.getSelectedRow();
				if(selectedRow==-1){//没有选中
					MessageBox.post("请选择法规","",MessageBox.INFORMATION);
					return;
				}
				TCComponentItemRevision selectRev = searchLawRevList.get(selectedRow);
				
				//调用回调接口
				callBack.setLawRev(selectRev);
				dispose();
			}
		});
		
		
		//查询法规版本
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String lawName = lawNameText.getText();
				if(StringsUtil.isEmpty(lawName)){//名字为空就提示并跳出
					MessageBox.post("请输入查询法规的名称","",MessageBox.INFORMATION);
					return;
				}
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LawItem.getValue());
				if(query==null){
					MessageBox.post("请配置法规版本查询器","",MessageBox.ERROR);
					return;
				}
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称"}, new String[]{"*"+lawName+"*"});
				searchLawRevList = new ArrayList<>();
				for(TCComponent component:searchResult){
					TCComponentItem item = (TCComponentItem) component;
					TCComponentItemRevision revision = null;
					try {
						revision = item.getLatestItemRevision();
					} catch (TCException e1) {
						e1.printStackTrace();
					}
					if(revision==null){
						continue;
					}
					searchLawRevList.add(revision);
				}
				
				//更新Table
				DefaultTableModel model = (DefaultTableModel) lawTable.getModel();
				int rowCount = model.getRowCount();
				for(int i=0;i<rowCount;i++){
					model.removeRow(0);
				}
				
				for(int i=0;i<searchLawRevList.size();i++){
					String name = "";//法规的名称
					try {
						name = searchLawRevList.get(i).getProperty("object_name");
					} catch (TCException e) {
						e.printStackTrace();
					}
					model.addRow(new String[]{name});
				}
			}
		});
	
	
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});
		
	}
}
