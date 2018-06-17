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
	
	
	
	//����ļ����¼�
	public interface IndexTableRemoveListener{
		void notifyIndexTableRemove(NodeBean removeNodeBean);
	}
	
	//���ü�����
	public void setIndexTableRemoveListener(
			IndexTableRemoveListener indexTableRemoveListener) {
		this.mIndexTableRemoveListener = indexTableRemoveListener;
	}
	
	
	 // �����ݴ洢��Transferable�У�Ȼ��֪ͨ�����ʼ����startDrag()��ʼ��
	@Override
	public void dragGestureRecognized(DragGestureEvent dge) {
		Component sourceComponent = dge.getComponent();//ѡ�е����
		
		if(sourceComponent instanceof JTable){
			JTable table = (JTable) sourceComponent;
			DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			int selectedRow = table.getSelectedRow();
			
			NodeBean selectBean = mRemoveNodeDragListener.getRemoveSelectedNodeBean(selectedRow);

			if(selectBean==null) return;//���ڱ�����һ��ռλ��һ�� �����ȡ�Ļ��ǿյľͲ���Ҫ�Ƴ�
			
			if(!selectBean.nodeType.equals(Const.NodeType.NODE_NUTRITION)) return;//����Ӫ��������ɾ��
			mIndexTableRemoveListener.notifyIndexTableRemove(selectBean);
			
			MyTransferable<NodeBean> beanTransferable = new MyTransferable<NodeBean>(selectBean);
//			dge.startDrag(DragSource.DefaultCopyDrop, beanTransferable,new MySourceListener());
		}
	}
	
	
}