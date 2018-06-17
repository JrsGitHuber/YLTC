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
		// ʹ�øú�����Transferable�����л�ȡ���õ�����
		Point pt = dtde.getLocation();
		DropTargetContext context = dtde.getDropTargetContext();
		JTree tree = (JTree) context.getComponent();
		TreePath targetPath = tree.getPathForLocation(pt.x,pt.y);
		DefaultMutableTreeNode targetNode = (DefaultMutableTreeNode) targetPath.getLastPathComponent();
//		if(targetNode.isLeaf()){//���Ҫ�ƶ����Ľڵ���Ҷ�ӽڵ� �;ܾ��϶�
//			dtde.rejectDrop();
//			return;
//		}
		
		try {
			Transferable tr = dtde.getTransferable(); 
			dtde.acceptDrop(dtde.getDropAction());//������Ĳ���
			
			T selectObject =  (T) tr.getTransferData(MyTransferable.JTREE_FLAVOR);
			
			if(selectObject instanceof DefaultMutableTreeNode){
				DefaultMutableTreeNode  selectNode = (DefaultMutableTreeNode) selectObject;
				DefaultTreeModel treeModel = (DefaultTreeModel) tree.getModel();
				treeModel.insertNodeInto(selectNode, targetNode, 0);//���뵽Ŀ��ڵ�ĵ�һ��
				dtde.dropComplete(true);//�������
				return;
			}else if(selectObject instanceof NodeBean){
				NodeBean selectBean = (NodeBean) selectObject;
				NodeBean parentNodeBean = (NodeBean) targetNode.getUserObject();
				
				
				if(parentNodeBean.childNodeBeans != null){//�жϸýڵ����Ƿ��Ѿ����˸�ԭ��
					for(NodeBean bean : parentNodeBean.childNodeBeans){
						if(bean.itemID.equals(selectBean.itemID)){
							MessageBox.post("�Ѿ�����","��ʾ",MessageBox.INFORMATION);
							dtde.dropComplete(true);//�������
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
				treeModel.insertNodeInto(selectNode, targetNode, 0);//���뵽Ŀ��ڵ�ĵ�һ��
				dtde.dropComplete(true);//�������
				
				//֪ͨ��������
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
