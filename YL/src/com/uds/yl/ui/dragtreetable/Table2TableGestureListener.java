package com.uds.yl.ui.dragtreetable;

import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.uds.yl.bean.NodeBean;

public class Table2TableGestureListener implements DragGestureListener {

	private TableToTableNodeDragListener mTable2TableNodeDragListener;
	
	public interface TableToTableNodeDragListener{
		NodeBean getTable2TableSelectedNodeBean(int position);
	}
	
	public void setNodeDragListener(TableToTableNodeDragListener table2TablenodeDragListener){
		this.mTable2TableNodeDragListener = table2TablenodeDragListener;
	}
	
	
	 // 将数据存储到Transferable中，然后通知组件开始调用startDrag()初始化
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Component sourceComponent = dge.getComponent();//选中的组件
		
		if(sourceComponent instanceof JTable){
			JTable table = (JTable) sourceComponent;
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			int selectedRow = table.getSelectedRow();
			NodeBean selectBean = mTable2TableNodeDragListener.getTable2TableSelectedNodeBean(selectedRow);
			
			MyTransferable<NodeBean> beanTransferable = new MyTransferable<NodeBean>(selectBean);
			dge.startDrag(DragSource.DefaultCopyDrop, beanTransferable,new MySourceListener());
		}
	}
	
	
}
