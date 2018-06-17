package com.uds.yl.ui.dragtreetable;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

public class MySourceListener implements DragSourceListener{

	@Override
	public void dragEnter(DragSourceDragEvent dsde) {
		DragSourceContext context = dsde.getDragSourceContext();
		int dropAction = dsde.getDropAction();
		if((dropAction & DnDConstants.ACTION_COPY) != 0){
			context.setCursor(DragSource.DefaultCopyDrop);
		}else if((dropAction & DnDConstants.ACTION_MOVE) != 0){
			context.setCursor(DragSource.DefaultMoveDrop);
		}else {
			context.setCursor(DragSource.DefaultCopyNoDrop);
		}
	}

	@Override
	public void dragOver(DragSourceDragEvent dsde) {
		
	}

	@Override
	public void dropActionChanged(DragSourceDragEvent dsde) {
		
	}

	@Override
	public void dragExit(DragSourceEvent dse) {
		
	}

	@Override
	public void dragDropEnd(DragSourceDropEvent dsde) {
		//拖拽动作结束的时候调用
		int drapAction = dsde.getDropAction();
		if(drapAction == DnDConstants.ACTION_MOVE && dsde.getDropSuccess()){
			System.out.println("MOVE: remove node");
		}
	}

}
