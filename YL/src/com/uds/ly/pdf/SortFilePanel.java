package com.uds.ly.pdf;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class SortFilePanel extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	List<String> mList = null;
	public boolean isOver = false;
	/////////
	public boolean isClickClose = false;
	// JFrame jFrame;
	JScrollPane listScrollPane;
	JPanel listPanel;
	JPanel buttonPanel;
	JButton upBtn;
	JButton downBtn;
	JButton okButton;
	JTable table;

	// 定义二维数组作为表格数据
	Object[][] tableData = null;
	// 定义一维数据作为列标题
	String[] columnTitle = { "文件名", "选中" };


	public SortFilePanel(final List<String> mList) {

		this.mList = mList;

		tableData = new Object[mList.size()][2];
		for (int i = 0; i < mList.size(); i++) {
			tableData[i][0] = mList.get(i);
			tableData[i][1] = false;
		}
		

		listPanel = new JPanel();
		buttonPanel = new JPanel();
		upBtn = new JButton("上移");
		downBtn = new JButton("下移");
		buttonPanel = new JPanel();
		okButton = new JButton("确定");
		table = new JTable();
		ExtendedTableModel model = new ExtendedTableModel(columnTitle, tableData);
		// 以ExtendedTableModel来创建JTable
		table = new JTable(model);
		table.setRowSelectionAllowed(false);
		table.setRowHeight(40);
		TableColumn checkBoxColumn = table.getColumnModel().getColumn(1);
		checkBoxColumn.setMaxWidth(60);
		checkBoxColumn.setPreferredWidth(60);

		listScrollPane = new JScrollPane(table);

		buttonPanel.setLayout(new FlowLayout());
		this.setLayout(new BorderLayout());

		this.add(listScrollPane, BorderLayout.CENTER);
		this.add(buttonPanel, BorderLayout.SOUTH);

		buttonPanel.add(upBtn);
		buttonPanel.add(downBtn);
		buttonPanel.add(okButton);

		this.setSize(500, 300);
		this.setVisible(false);

		//
		this.addWindowListener(new WindowAdapter() {
			public void windowClosed(WindowEvent e) {
				synchronized (SortFilePanel.this) {
					isClickClose = true;
					SortFilePanel.this.notify();
				}
			}
		});

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		
		
		upBtn.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent actionevent) {
    			int selectRows = table.getSelectedRows().length;
    			if(selectRows==1){
    				int selectIndexX = table.getSelectedRow();
    				int selectIndexY = table.getSelectedColumn();
    				if(selectIndexX==0||selectIndexY==1){
    					return;
    				}
    				String currentStr = table.getModel().getValueAt(selectIndexX, selectIndexY).toString();
    				String upStr = table.getModel().getValueAt(selectIndexX-1, selectIndexY).toString();
    				
    				boolean currentFlag = (boolean) table.getModel().getValueAt(selectIndexX, selectIndexY+1);
    				boolean upFlag = (boolean) table.getModel().getValueAt(selectIndexX-1, selectIndexY+1);
    				
    				//字段
    				table.getModel().setValueAt(upStr, selectIndexX, selectIndexY);//选中位置
    				table.getModel().setValueAt(currentStr, selectIndexX-1, selectIndexY);//选中之上位置
    				
    				//选中
    				table.getModel().setValueAt(upFlag, selectIndexX, selectIndexY+1);//选中位置
    				table.getModel().setValueAt(currentFlag, selectIndexX-1, selectIndexY+1);//选中之上位置
    				
    			}
    			
    		}
    	});
    	
    	downBtn.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent actionevent) {
    			int selectRows = table.getSelectedRows().length;
    			if(selectRows==1){
    				int selectIndexX = table.getSelectedRow();
    				int selectIndexY = table.getSelectedColumn();
    				if(selectIndexX==4||selectIndexY==1){
    					return;
    				}
    				String currentStr = table.getModel().getValueAt(selectIndexX, selectIndexY).toString();
    				String downStr = table.getModel().getValueAt(selectIndexX+1, selectIndexY).toString();
    				
    				boolean currentFlag = (boolean) table.getModel().getValueAt(selectIndexX, selectIndexY+1);
    				boolean downFlag = (boolean) table.getModel().getValueAt(selectIndexX+1, selectIndexY+1);
    				
    				//字段
    				table.getModel().setValueAt(downStr, selectIndexX, selectIndexY);//选中位置
    				table.getModel().setValueAt(currentStr, selectIndexX+1, selectIndexY);//选中之上位置
    				
    				//选中
    				table.getModel().setValueAt(downFlag, selectIndexX, selectIndexY+1);//选中位置
    				table.getModel().setValueAt(currentFlag, selectIndexX+1, selectIndexY+1);//选中之上位置
    				
    			}
    		}
    	});
    	
    	okButton.addActionListener(new ActionListener() {

			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//确认然后遍历
				mList.clear();
				for(int i=0;i<table.getRowCount();i++){
					if((boolean) table.getModel().getValueAt(i, 1)){
						System.out.println(table.getModel().getValueAt(i, 0).toString());
						mList.add(table.getModel().getValueAt(i, 0).toString());
					}
				}
				isOver = true;
				dispose();
				
			}
		});

	}

	public boolean showDialog() {
		setVisible(true);
		setTitle("选择文件顺序");
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return isClickClose;

	}



	class ExtendedTableModel extends DefaultTableModel {
		/**
		 * 
		 */
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
