package com.uds.yl.ui.dragtreetable;
import java.awt.Component;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;

public class RemoveGestureListener  implements DragGestureListener {
	
	private IndexTableRemoveListener mIndexTableRemoveListener;
	private RemoveNodeDragListener mRemoveNodeDragListener;
	
	public interface RemoveNodeDragListener{
		NodeBean getRemoveSelectedNodeBean(int position);
	}
	
	public void setRemoveNodeDragListener(RemoveNodeDragListener removeNodeDragListener){
		this.mRemoveNodeDragListener = removeNodeDragListener;
	}
	
	
	
	//插入的监听事件
	public interface IndexTableRemoveListener{
		void notifyIndexTableRemove(NodeBean removeNodeBean);
	}
	
	//设置监听器
	public void setIndexTableRemoveListener(
			IndexTableRemoveListener indexTableRemoveListener) {
		this.mIndexTableRemoveListener = indexTableRemoveListener;
	}
	
	
	 // 将数据存储到Transferable中，然后通知组件开始调用startDrag()初始化
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Component sourceComponent = dge.getComponent();//选中的组件
		
		if(sourceComponent instanceof JTable){
			JTable table = (JTable) sourceComponent;
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			int selectedRow = table.getSelectedRow();
			
			NodeBean selectBean = mRemoveNodeDragListener.getRemoveSelectedNodeBean(selectedRow);

			if(selectBean==null) return;//由于表中有一个占位的一行 这里获取的会是空的就不需要移除
			
			if(!selectBean.nodeType.equals(Const.NodeType.NODE_NUTRITION)) return;//不是营养包不让删除
			mIndexTableRemoveListener.notifyIndexTableRemove(selectBean);
			
			MyTransferable<NodeBean> beanTransferable = new MyTransferable<NodeBean>(selectBean);
//			dge.startDrag(DragSource.DefaultCopyDrop, beanTransferable,new MySourceListener());
		}
	}
	
	
}