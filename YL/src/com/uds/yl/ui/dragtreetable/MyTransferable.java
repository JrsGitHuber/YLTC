package com.uds.yl.ui.dragtreetable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import com.uds.yl.bean.MaterialBean;


public class MyTransferable<T> implements Transferable{

	private T t;
	
	static DataFlavor TABLE_FLAVOR = new DataFlavor(MaterialBean.class, "ROWBEAN");//传递过来的是 bean类型
	static DataFlavor JTREE_FLAVOR = new DataFlavor(DefaultMutableTreeNode.class, "JTREE");//传递过来是Tree 中的节点类型
	
	static DataFlavor flavor[] = {TABLE_FLAVOR,JTREE_FLAVOR};

	
	public MyTransferable(T t){
		this.t = t;
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return flavor;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		Class<?> representationClass = flavor.getRepresentationClass();
		if(representationClass == TABLE_FLAVOR.getRepresentationClass()){
			return true;
		}
		if(representationClass == JTREE_FLAVOR.getRepresentationClass()){
			return true;
		}
		return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		
		if(isDataFlavorSupported(flavor)){//如果数据类型支持
			return  t;
		}else {
			 throw new UnsupportedFlavorException(flavor);  
		}
	}

}
