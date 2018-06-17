package com.uds.yl.ui.dragtreetable;

import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.NodeBean;


public class MyGestureListener implements DragGestureListener {

	private NodeDragListener mNodeDragListener;
	
	public interface NodeDragListener{
		NodeBean getSelectedNodeBean(int position);
	}
	
	public void setNodeDragListener(NodeDragListener nodeDragListener){
		this.mNodeDragListener = nodeDragListener;
	}
	
	
	 // 将数据存储到Transferable中，然后通知组件开始调用startDrag()初始化
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Component sourceComponent = dge.getComponent();//选中的组件
		if(sourceComponent instanceof JTree){
//			TreePath path = tree.getPathForLocation(e.getX(), e.getY()); 根据选中的坐标位置找到树种的选中节点路径
			JTree tree = (JTree) sourceComponent;
			TreePath selectPath = tree.getSelectionPath();//获取树种选中的结构
			DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) selectPath.getLastPathComponent();
			
			MyTransferable<DefaultMutableTreeNode> treeTransferable = new MyTransferable<DefaultMutableTreeNode>(selectNode);
			
			dge.startDrag(DragSource.DefaultCopyDrop, treeTransferable,new MySourceListener());
			
		}else if(sourceComponent instanceof JTable){
			JTable table = (JTable) sourceComponent;
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			int selectedRow = table.getSelectedRow();
			NodeBean selectBean = mNodeDragListener.getSelectedNodeBean(selectedRow);
			
			MyTransferable<NodeBean> beanTransferable = new MyTransferable<NodeBean>(selectBean);
			dge.startDrag(DragSource.DefaultCopyDrop, beanTransferable,new MySourceListener());
		}
	}
	
	
}
