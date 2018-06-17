package com.uds.yl.ui.dragtreetable;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import javax.swing.JTable;

import com.uds.yl.bean.NodeBean;

public class Table2TableTargetListener<T> implements DropTargetListener{
	

	private IndexTableInsertListener mIndexTableInsertListener;
	
	//����ļ����¼�
	public interface IndexTableInsertListener{
		void notifyIndexTableInsert(NodeBean insertNodeBean);
	}
	
	//���ü�����
	public void setIndexTableInsertListener(
			IndexTableInsertListener indexTableInsertListener) {
		this.mIndexTableInsertListener = indexTableInsertListener;
	}
	
	
	
	
	
	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void dragOver(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
		
	}

	@Override
	public void dragExit(DropTargetEvent dte) {
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent dtde) {
		// ʹ�øú�����Transferable�����л�ȡ���õ�����
		Point pt = dtde.getLocation();
		DropTargetContext context = dtde.getDropTargetContext();
		JTable table = (JTable) context.getComponent();//Ŀ���� ָ��table
		
		try {
			Transferable tr = dtde.getTransferable(); 
			dtde.acceptDrop(dtde.getDropAction());//������Ĳ���
			
			T selectObject =  (T) tr.getTransferData(MyTransferable.JTREE_FLAVOR);
		
			if(selectObject instanceof NodeBean){
				NodeBean selectBean = (NodeBean) selectObject;
				//֪ͨ��ָ��table �в���һ������
				mIndexTableInsertListener.notifyIndexTableInsert(selectBean);
				
				dtde.dropComplete(true);//�������
				
				return;
			}
			
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
		
	}

}

