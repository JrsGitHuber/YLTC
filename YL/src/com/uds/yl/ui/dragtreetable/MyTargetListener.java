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
import java.sql.Date;
import java.util.ArrayList;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import COM.inovie.services.integration.applicationRegistry.xml.ContactInfo;

import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;


public class MyTargetListener<T> implements DropTargetListener{
	
	private FormulatorUpdateListener mFormulatorUpdateListener;
	
	public interface FormulatorUpdateListener{
		void notifyFormulatorTableUpdate();
	}
	
	public void setFormulatorUpdateListener(FormulatorUpdateListener formulatorUpdateListener){
		this.mFormulatorUpdateListener = formulatorUpdateListener;
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
		JTree tree = (JTree) context.getComponent();
		TreePath targetPath = tree.getPathForLocation(pt.x,pt.y);
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();
//		if(targetNode.isLeaf()){//如果要移动到的节点是叶子节点 就拒绝拖动
//			dtde.rejectDrop();
//			return;
//		}
		
		try {
			Transferable tr = dtde.getTransferable(); 
			dtde.acceptDrop(dtde.getDropAction());//先允许改操作
			
			T selectObject =  (T) tr.getTransferData(MyTransferable.JTREE_FLAVOR);
			
			if(selectObject instanceof DefaultMutableTreeNode){
				DefaultMutableTreeNode  selectNode = (DefaultMutableTreeNode) selectObject;
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				treeModel.insertNodeInto(selectNode, targetNode, 0);//插入到目标节点的第一个
				dtde.dropComplete(true);//操作完成
				return;
			}else if(selectObject instanceof NodeBean){
				NodeBean selectBean = (NodeBean) selectObject;
				NodeBean parentNodeBean = (NodeBean) targetNode.getUserObject();
				
				
				if(parentNodeBean.childNodeBeans != null){//判断该节点下是否已经有了该原料
					for(NodeBean bean : parentNodeBean.childNodeBeans){
						if(bean.itemID.equals(selectBean.itemID)){
							MessageBox.post("已经存在","提示",MessageBox.INFORMATION);
							dtde.dropComplete(true);//操作完成
							return;
						}
					}
				}
				
				DefaultMutableTreeNode selectNode = selectBean.node;
				selectBean.parentNode = targetNode;
				
				if(parentNodeBean.chidNodes == null) parentNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
				parentNodeBean.chidNodes.add(0,selectNode);
				if(parentNodeBean.childNodeBeans == null) parentNodeBean.childNodeBeans = new ArrayList<NodeBean>();
				parentNodeBean.childNodeBeans.add(0,selectBean);
				
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				treeModel.insertNodeInto(selectNode, targetNode, 0);//插入到目标节点的第一个
				dtde.dropComplete(true);//操作完成
				
				//通知更新数据
				mFormulatorUpdateListener.notifyFormulatorTableUpdate();
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
