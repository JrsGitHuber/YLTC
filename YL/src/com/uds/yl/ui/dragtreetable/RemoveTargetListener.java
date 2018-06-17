package com.uds.yl.ui.dragtreetable;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;


import com.uds.yl.bean.NodeBean;

public class RemoveTargetListener<T> implements DropTargetListener{
	

	
	
	
	
	
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
		DropTargetContext context = dtde.getDropTargetContext();
		try {
			Transferable tr = dtde.getTransferable(); 
			dtde.acceptDrop(dtde.getDropAction());//先允许改操作
			
			T selectObject =  (T) tr.getTransferData(MyTransferable.JTREE_FLAVOR);
		
			if(selectObject instanceof NodeBean){
				NodeBean selectBean = (NodeBean) selectObject;
				//通知在指标table 中插入一个数据
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
