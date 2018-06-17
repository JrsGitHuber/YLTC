package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.management.Query;
import javax.print.CancelablePrintJob;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.eclipse.jface.viewers.AcceptAllFilter;

import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.tcutils.QueryUtil;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.ActionEvent;

public class AddWetFormulatorFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JTable table;
	private JButton okBtn;
	private JButton cancleBtn;
	private JButton searchBtn;

	
	private List<TCComponentItemRevision> mItemRevList; 
	
	private TCComponentItemRevision selectedItemRev;
	
	private DefaultTableModel tableModel;
	
	private AbstractCallBack mCallBack;

	public AddWetFormulatorFrame(AbstractCallBack callBack) {
		this.mCallBack = callBack;
		setTitle("添加基粉");
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 521, 364);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label = new JLabel("名称：");
		label.setBounds(40, 46, 54, 15);
		contentPane.add(label);
		
		textField = new JTextField();
		textField.setBounds(140, 43, 109, 21);
		contentPane.add(textField);
		textField.setColumns(10);
		
		searchBtn = new JButton("搜索");
		searchBtn.setBounds(365, 42, 93, 23);
		contentPane.add(searchBtn);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(46, 113, 390, 135);
		contentPane.add(scrollPane);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"名称"
			}
		));
		scrollPane.setViewportView(table);
		
		okBtn = new JButton("确定");
		okBtn.setBounds(67, 281, 93, 23);
		contentPane.add(okBtn);
		
		cancleBtn = new JButton("取消");
		cancleBtn.setBounds(289, 281, 93, 23);
		contentPane.add(cancleBtn);
		
		//初始化
		tableModel = (DefaultTableModel) table.getModel();
		
		
		//确定
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = table.getSelectedRow();
				if(selectedRow==-1){//没选择
					MessageBox.post("请选择基粉","",MessageBox.INFORMATION);
					return;
				}
				selectedItemRev =  mItemRevList.get(selectedRow);
				String name = tableModel.getValueAt(selectedRow, 0).toString();
				mCallBack.addWetFormulator(selectedItemRev,name);
				dispose();
			}
		});
		
		//取消
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		
		//搜索的监听事件
		searchBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String name = "*" + textField.getText().toString()+"*";
				//使用配方查询查询基粉的说
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_FormulatorRevision.getValue());
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称"}, new String[]{name});
				mItemRevList = new ArrayList<>();
				for(TCComponent component : searchResult){
					TCComponentItemRevision tempRev = (TCComponentItemRevision) component;
					mItemRevList.add(tempRev);
				}
				
				for(TCComponentItemRevision revision : mItemRevList){
					try {
						String revName = revision.getProperty("object_name");
						tableModel.addRow(new String[]{revName});
					} catch (TCException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
	}
}
