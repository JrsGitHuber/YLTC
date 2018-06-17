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
     * ��д����DefaultTreeCellRenderer�ķ���  
     */    
    @Override    
    public Component getTreeCellRendererComponent(JTree tree, Object value,    
            boolean sel, boolean expanded, boolean leaf, int row,    
            boolean hasFocus)    
    {    
    
        //ִ�и���ԭ�Ͳ���    
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
            
        //�õ�ÿ���ڵ��TreeNode    
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;    
        
        //�õ�ÿ���ڵ��text    
        NodeBean nodeBean = (NodeBean) node.getUserObject();           
            
        //�ж����ĸ��ı��Ľڵ����ö�Ӧ��ֵ����������ڵ㴫�����һ��ʵ��,����Ը���ʵ�������һ��������������ʾ��Ӧ��ͼ�꣩    
        if (Const.NodeType.NODE_FORMULA.equals(nodeBean.nodeType))    
        {    
        	URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_FORMULA+".png");
            this.setIcon(new ImageIcon(imgUrl));    
        }    
        if (Const.NodeType.NODE_BASE_FORMULATOR.equals(nodeBean.nodeType)) //�������䷽��ͼ��   
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
        if(Const.NodeType.NODE_NUTRITION.equals(nodeBean.nodeType))//Ӫ���� Ҳ����ԭ�ϵ�ͼ��
        {
        	 URL imgUrl = getClass().getResource("//"+Const.NodeType.NODE_MATERIAL+".png");
        	 this.setIcon(new ImageIcon(imgUrl));    
        }
        
    
        return this;    
    }    
    
}    
