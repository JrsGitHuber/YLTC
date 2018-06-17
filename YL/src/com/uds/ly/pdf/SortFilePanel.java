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

	// �����ά������Ϊ�������
	Object[][] tableData = null;
	// ����һά������Ϊ�б���
	String[] columnTitle = { "�ļ���", "ѡ��" };


	public SortFilePanel(final List<String> mList) {

		this.mList = mList;

		tableData = new Object[mList.size()][2];
		for (int i = 0; i < mList.size(); i++) {
			tableData[i][0] = mList.get(i);
			tableData[i][1] = false;
		}
		

		listPanel = new JPanel();
		buttonPanel = new JPanel();
		upBtn = new JButton("����");
		downBtn = new JButton("����");
		buttonPanel = new JPanel();
		okButton = new JButton("ȷ��");
		table = new JTable();
		ExtendedTableModel model = new ExtendedTableModel(columnTitle, tableData);
		// ��ExtendedTableModel������JTable
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
    				
    				//�ֶ�
    				table.getModel().setValueAt(upStr, selectIndexX, selectIndexY);//ѡ��λ��
    				table.getModel().setValueAt(currentStr, selectIndexX-1, selectIndexY);//ѡ��֮��λ��
    				
    				//ѡ��
    				table.getModel().setValueAt(upFlag, selectIndexX, selectIndexY+1);//ѡ��λ��
    				table.getModel().setValueAt(currentFlag, selectIndexX-1, selectIndexY+1);//ѡ��֮��λ��
    				
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
    				
    				//�ֶ�
    				table.getModel().setValueAt(downStr, selectIndexX, selectIndexY);//ѡ��λ��
    				table.getModel().setValueAt(currentStr, selectIndexX+1, selectIndexY);//ѡ��֮��λ��
    				
    				//ѡ��
    				table.getModel().setValueAt(downFlag, selectIndexX, selectIndexY+1);//ѡ��λ��
    				table.getModel().setValueAt(currentFlag, selectIndexX+1, selectIndexY+1);//ѡ��֮��λ��
    				
    			}
    		}
    	});
    	
    	okButton.addActionListener(new ActionListener() {

			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				//ȷ��Ȼ�����
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
		setTitle("ѡ���ļ�˳��");
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

		// �����ṩһ�����������ù�������ʵ��ί�и�DefaultTableModel����
		public ExtendedTableModel(String[] columnNames, Object[][] cells) {
			super(cells, columnNames);
		}

		// ��дgetColumnClass����������ÿ�еĵ�һ��ֵ����������ʵ����������
		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}
	}

}
