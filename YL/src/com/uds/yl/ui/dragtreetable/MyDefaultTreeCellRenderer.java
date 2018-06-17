package com.uds.yl.ui.dragtreetable;

import java.awt.Component;
import java.net.URL;

import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;

public class MyDefaultTreeCellRenderer extends DefaultTreeCellRenderer    {    
    /**  
     * ID  
     */    
    private static final long   serialVersionUID    = 1L;    
    
    /**  
     * 重写父类DefaultTreeCellRenderer的方法  
     */    
    @Override    
    public Component getTreeCellRendererComponent(JTree tree, Object value,    
            boolean sel, boolean expanded, boolean leaf, int row,    
            boolean hasFocus)    
    {    
    
        //执行父类原型操作    
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,    
                row, hasFocus);    
    
        setText(value.toString());    
            
        if (sel)    
        {    
            setForeground(getTextSelectionColor());    
        }    
        else    
        {    
            setForeground(getTextNonSelectionColor());    
        }    
            
        //得到每个节点的TreeNode    
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;    
        
        //得到每个节点的text    
        NodeBean nodeBean = (NodeBean) node.getUserObject();           
            
        //判断是哪个文本的节点设置对应的值（这里如果节点传入的是一个实体,则可以根据实体里面的一个类型属性来显示对应的图标）    
        if (Const.NodeType.NODE_FORMULA.equals(nodeBean.nodeType))    
        {    
        	URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_FORMULA+".png");
            this.setIcon(new ImageIcon(imgUrl));    
        }    
        if (Const.NodeType.NODE_BASE_FORMULATOR.equals(nodeBean.nodeType)) //基粉用配方的图标   
        {    
        	URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_FORMULA+".png");
            this.setIcon(new ImageIcon(imgUrl));    
        }    
        if (Const.NodeType.NODE_MATERIAL.equals(nodeBean.nodeType))    
        {    
        	URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_MATERIAL+".png");
            this.setIcon(new ImageIcon(imgUrl));    
        }    
        if (Const.NodeType.NODE_INDEXITEM.equals(nodeBean.nodeType))    
        {    
        	URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_INDEXITEM+".png");
        	this.setIcon(new ImageIcon(imgUrl));    
        }    
        if(Const.NodeType.NODE_NUTRITION.equals(nodeBean.nodeType))//营养包 也是用原料的图标
        {
        	 URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_MATERIAL+".png");
        	 this.setIcon(new ImageIcon(imgUrl));    
        }
        
    
        return this;    
    }    
    
}    
