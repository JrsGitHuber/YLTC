package com.uds.yl.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;
import com.uds.yl.ui.dragtreetable.MyDefaultTreeCellRenderer;
import com.uds.yl.ui.dragtreetable.MyGestureListener;
import com.uds.yl.ui.dragtreetable.MyTargetListener;

public class DragTableTreeTest extends JFrame{
	private JPanel contentPane;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DragTableTreeTest frame = new DragTableTreeTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public DragTableTreeTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 745, 377);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(30, 10, 218, 233);
		contentPane.add(scrollPane);
		
		
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		NodeBean rootNodeBean = new NodeBean(); 
		rootNodeBean.nodeType = Const.NodeType.NODE_FORMULA;
		rootNodeBean.objectName = "配方二分局领导数据分类看电视剧了看见了看见的酸辣粉束带结发";
		rootNode.setUserObject(rootNodeBean);
		
		DefaultMutableTreeNode secondNode = new DefaultMutableTreeNode();
		NodeBean secondNodeBean = new NodeBean(); 
		secondNodeBean.nodeType = Const.NodeType.NODE_MATERIAL;
		secondNodeBean.objectName = "原料";
		secondNode.setUserObject(secondNodeBean);
		
		rootNode.add(secondNode);
		
		JTree tree = new JTree(rootNode);
		scrollPane.setViewportView(tree);
		tree.setCellRenderer(new MyDefaultTreeCellRenderer());
		
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(290, 70, 397, 186);
		contentPane.add(scrollPane_1);
		
		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"XI", "1"},
				{"ZH", null},
			},
			new String[] {
				"Name", "age"
			}
		));
		scrollPane_1.setViewportView(table);
		
		 //设置拖动源
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(table, DnDConstants.ACTION_COPY_OR_MOVE, new MyGestureListener());
        new DropTarget(tree,new MyTargetListener());//监听 拖动到的目标
        
        DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, new MyGestureListener());
        new DropTarget(tree,new MyTargetListener());//监听 拖动到的目标
		
		
	}
}
