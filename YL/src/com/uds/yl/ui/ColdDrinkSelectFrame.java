package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.uds.yl.interfaces.CallBack;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JScrollPane;
import javax.swing.JButton;

public class ColdDrinkSelectFrame extends JFrame {

	private JPanel contentPane;
	private JTable table;

	private CallBack callBack;

	private List<String> allWillSelectedNamesList;// 初始化Table的数据提供选择

	/**
	 * Create the frame.
	 */
	public ColdDrinkSelectFrame(List<String> nameList) {
		this.allWillSelectedNamesList = nameList;
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 10, 414, 155);
		contentPane.add(scrollPane);
		
		Object tableData[][] = new Object[allWillSelectedNamesList.size()][2];
		String[] columnTitle = { "原料", "选择" };
		for (int i = 0; i < allWillSelectedNamesList.size(); i++) {
			tableData[i][0] = allWillSelectedNamesList.get(i);
			tableData[i][1] = false;
		}
		ExtendedTableModel mode = new ExtendedTableModel(columnTitle, tableData);
		table = new JTable(mode){
			@Override
			public boolean isCellEditable(int row, int column) {
				if(column==0){
					return false;
				}
				return true;
			}
		};
		
		scrollPane.setViewportView(table);
		JButton okBtn = new JButton("确定");
		okBtn.setBounds(60, 211, 80, 30);
		contentPane.add(okBtn);

		JButton cancleBtn = new JButton("取消");
		cancleBtn.setBounds(230, 211, 80, 30);
		contentPane.add(cancleBtn);

		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dispose();
			}
		});

		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				DefaultTableModel model = (DefaultTableModel) table.getModel();
				StringBuilder strBuilder = new StringBuilder();
				int rowCount = table.getRowCount();
				for (int i = 0; i < rowCount; i++) {
					boolean isSelected = (boolean) model.getValueAt(i, 1);
					String name = (String) model.getValueAt(i, 0);
					if (isSelected) {
						strBuilder.append(name + "#");
					}
				}
				callBack.setResult(strBuilder.toString());
			}
		});
	}

	public void setCallBack(CallBack callBack) {
		this.callBack = callBack;
	}

	class ExtendedTableModel extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		// 重新提供一个构造器，该构造器的实现委托给DefaultTableModel父类
		public ExtendedTableModel(String[] columnNames, Object[][] cells) {
			super(cells, columnNames);
		}

		// 重写getColumnClass方法，根据每列的第一个值来返回其真实的数据类型
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}
}
