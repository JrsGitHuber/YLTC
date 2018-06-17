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
	
	//插入的监听事件
	public interface IndexTableInsertListener{
		void notifyIndexTableInsert(NodeBean insertNodeBean);
	}
	
	//设置监听器
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
		// 使用该函数从Transferable对象中获取有用的数据
		Point pt = dtde.getLocation();
		DropTargetContext context = dtde.getDropTargetContext();
		JTable table = (JTable) context.getComponent();//目标是 指标table
		
		try {
			Transferable tr = dtde.getTransferable(); 
			dtde.acceptDrop(dtde.getDropAction());//先允许改操作
			
			T selectObject =  (T) tr.getTransferData(MyTransferable.JTREE_FLAVOR);
		
			if(selectObject instanceof NodeBean){
				NodeBean selectBean = (NodeBean) selectObject;
				//通知在指标table 中插入一个数据
				mIndexTableInsertListener.notifyIndexTableInsert(selectBean);
				
				dtde.dropComplete(true);//操作完成
				
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

