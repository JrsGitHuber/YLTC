package com.uds.yl.ui;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Color;

import javax.swing.JTable;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.NodeBean;
import com.uds.yl.common.Const;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.INewMilkFormulatorService;
import com.uds.yl.service.impl.NewMilkFormulatorServiceImpl;
import com.uds.yl.ui.dragtreetable.MyDefaultTreeCellRenderer;
import com.uds.yl.ui.dragtreetable.MyGestureListener;
import com.uds.yl.ui.dragtreetable.RemoveGestureListener;
import com.uds.yl.ui.dragtreetable.RemoveGestureListener.IndexTableRemoveListener;
import com.uds.yl.ui.dragtreetable.RemoveTargetListener;
import com.uds.yl.ui.dragtreetable.RemoveGestureListener.RemoveNodeDragListener;
import com.uds.yl.ui.dragtreetable.Table2TableGestureListener.TableToTableNodeDragListener;
import com.uds.yl.ui.dragtreetable.Table2TableTargetListener;
import com.uds.yl.ui.dragtreetable.MyGestureListener.NodeDragListener;
import com.uds.yl.ui.dragtreetable.MyTargetListener.FormulatorUpdateListener;
import com.uds.yl.ui.dragtreetable.MyTargetListener;
import com.uds.yl.ui.dragtreetable.Table2TableGestureListener;
import com.uds.yl.ui.dragtreetable.Table2TableTargetListener.IndexTableInsertListener;


public class NewMilkFormulatorFrame extends JFrame implements NodeDragListener, FormulatorUpdateListener,
				TableToTableNodeDragListener, IndexTableInsertListener,
				RemoveNodeDragListener, IndexTableRemoveListener{
	
	private INewMilkFormulatorService service = new NewMilkFormulatorServiceImpl();//�̷��䷽����ķ�����

	private JPanel contentPane;
	
	private JTree mFormulatorTree;//�ṹ��
	private JTable mFormulatorTable;//չʾ�����䷽��Ϣ�ı��
	private JTable mMaterialTable;//ԭ����Ϣ��
	private JTable mIndexTable;//ָ����Ϣ��
	private JTable mLableTable;//��ǩ��Ϣ��
	
	private DefaultTreeModel mFormulatorTreeModel;//�䷽��������ģ��
	private DefaultTableModel mMaterialTableModel;//ԭ����Ϣ�������ģ��
	private DefaultTableModel mIndexTableModel;//ָ����Ϣ�������ģ��
	private DefaultTableModel mLableTableModel;//��ǩ��Ϣ�������ģ��
	private DefaultTableModel mFormulatorModel;//�䷽��Ϣ���е�����ģ��
	
	private TreePath mRightPath;//�Ҽ��ڵ��path
	
	private JPopupMenu mMenu ;//�����һ��˵�
	private JMenuItem mDeleteMenuItem;//ɾ���ڵ�˵�
	private JMenuItem mInsertFormulatorMenuItem;//������۲˵�
	private JMenuItem mInsertNutritionMenuItem;//����Ӫ�����˵�
	
	private JComboBox<String> mMaterialTypeCombox;//ԭ������������
	private JComboBox<String> mDryLossCombox;//�ɷ����������
	private JComboBox<String> mWetLossCombox;//ʪ�����������
	private JComboBox<String> mDateLossCombox;//���������������
	private JComboBox<String> mLossTypeCombox;//������͵�������  
	
	private JButton mDryLossShowBtn;//�鿴�ɷ����
	private JButton mWetLossShowBtn;//�鿴ʪ�����
	private JButton mDateLossShowBtn;//�鿴���������
	
	private JTextField waterEdt;//������ʾѡ�л��۽ڵ�ĺ�ˮ��
	
	private JButton mOkBtn;//ȷ��
	private JButton mCancleBtn;//ȡ��
	
	
	private NodeBean mRootNodeBean;//���ڵ�
	private List<TCComponentItemRevision> mDryLossItemRevList;//���еĸɷ����
	private TCComponentItemRevision mSelectedDryLossItemRev;//ѡ�еĸɷ����
	private List<TCComponentItemRevision> mWetLossItemRevList;//���е�ʪ�����
	private TCComponentItemRevision mSelectedWetLossItemRev;//ѡ�е�ʪ�����
	private List<TCComponentItemRevision> mDateLossItemRevList;//���еı��������
	private TCComponentItemRevision mSelectedDateLossItemRev;//ѡ�еı��������
	
	
	private NodeBean mSelectedNodeBean;//���ṹ��ѡ�� ԭ����ѡ�� �� �����䷽��ѡ��
	
	private List<NodeBean> mMaterialTableNodeBeanList;//ԭ�� table �е����е�ԭ�ϵ�ʵ����
	private NodeBean mSelectedMaterialTableNodeBean;//ԭ�� tableѡ�е�ԭ��
	
	private List<NodeBean> mIndexTableNodeBeanList;//ָ��table �е�����ָ���ʵ����
	private NodeBean mSelectedIndexTableNodeBean;//ָ��table ��ѡ�е�ָ���ʵ����
	
	private List<NodeBean> mFormulatorTableNodeBeanList;//�䷽table �е����е�ʵ����
	private NodeBean mSelectedFormulatorTableNodeBean;//�䷽table ��ѡ�е�ʵ����
	
	private List<NodeBean> mLableTableNodeBeanList;//��ǩtable �е����е�ʵ����
	private NodeBean mSelectedLableTableNodeBean;//��ǩtable �е�ѡ�е�ʵ����
	
	private ProgressBarDialog mProgressBarDialog=null;//������
	private MyGestureListener mGestureListener;//�϶�������
	private MyTargetListener<NodeBean> mTargetListener;//�϶���Ŀ���ϵļ�����
	private Table2TableGestureListener mTable2TableGestureListener;
	private Table2TableTargetListener<NodeBean> mTable2TableTargetListener;
	private RemoveGestureListener mRemoveGestureListener;
	private RemoveTargetListener<NodeBean> mRemoveTargetListener;

	public NewMilkFormulatorFrame(NodeBean rootNodeBean) {
		
		this.mRootNodeBean = rootNodeBean;
		setResizable(false);
		setBounds(100, 10, 1421, 830);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(45, 20, 229, 274);
		contentPane.add(scrollPane);
		
		
		
		
		mFormulatorTree = new JTree(mRootNodeBean.node);
		scrollPane.setViewportView(mFormulatorTree);
		mFormulatorTree.setCellRenderer(new MyDefaultTreeCellRenderer());
		
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(20, 437, 282, 304);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(10, 54, 262, 235);
		panel.add(scrollPane_1);
		
		mFormulatorTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				if(ColIndex==0 || ColIndex==1){
					return false;
				}
				return true;
			}
		};
		mFormulatorTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID","ԭ������", "Ͷ����"
			}
		));
		scrollPane_1.setViewportView(mFormulatorTable);
		
		JLabel lblNewLabel = new JLabel("�����䷽��ϸ��Ϣ");
		lblNewLabel.setFont(new Font("����", Font.PLAIN, 15));
		lblNewLabel.setBounds(60, 10, 212, 34);
		panel.add(lblNewLabel);
		
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(737, 38, 642, 699);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel label = new JLabel("��ǩ��Ϣ");
		label.setFont(new Font("����", Font.PLAIN, 15));
		label.setBounds(309, 10, 97, 34);
		panel_1.add(label);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(10, 38, 622, 645);
		panel_1.add(scrollPane_2);
		
		mLableTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				if(ColIndex==4){//ֻ���޸ı�ǩֵ
					return true;
				}
				return false;
			}
		};
		mLableTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID","ָ������","����ֵ","�ڿ�ֵ", "��ǩֵ","��Сֵ","�м�ֵ","���ֵ"
			}
		));
		scrollPane_2.setViewportView(mLableTable);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_3.setBounds(315, 20, 393, 274);
		contentPane.add(panel_3);
		panel_3.setLayout(null);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setBounds(10, 62, 355, 201);
		panel_3.add(scrollPane_3);
		
		mMaterialTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				return false;
			}
		};
		mMaterialTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID","����","��Сֵ","�м�ֵ","���ֵ"
			}
		));
		scrollPane_3.setViewportView(mMaterialTable);
		
		JLabel lblNewLabel_1 = new JLabel("ԭ�����");
		lblNewLabel_1.setBounds(23, 24, 85, 15);
		panel_3.add(lblNewLabel_1);
		
		mMaterialTypeCombox = new JComboBox();
		mMaterialTypeCombox.setBounds(118, 21, 183, 21);
		panel_3.add(mMaterialTypeCombox);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_2.setBounds(326, 437, 379, 304);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		JLabel label_1 = new JLabel("ָ��");
		label_1.setBounds(146, 10, 98, 18);
		label_1.setFont(new Font("����", Font.PLAIN, 15));
		panel_2.add(label_1);
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setBounds(10, 38, 359, 249);
		panel_2.add(scrollPane_4);
		
		mIndexTable = new JTable(){
			public boolean isCellEditable(int rowIndex, int ColIndex) {
				if(ColIndex==0 || ColIndex==1){
					return false;
				}
				return true;
			}
			
		};
		mIndexTable.setModel(new DefaultTableModel(
			new Object[][] {
					{null, null},
			},
			new String[] {
				"ID","����", "��Сֵ","�м�ֵ","���ֵ"
			}
		));
		scrollPane_4.setViewportView(mIndexTable);
		
		JLabel label_2 = new JLabel("�ɷ���ģ�");
		label_2.setBounds(315, 316, 77, 15);
		contentPane.add(label_2);
		
		JLabel label_3 = new JLabel("ʪ����ģ�");
		label_3.setBounds(315, 350, 77, 15);
		contentPane.add(label_3);
		
		JLabel label_4 = new JLabel("��������ģ�");
		label_4.setBounds(315, 390, 83, 15);
		contentPane.add(label_4);
		
		mDryLossCombox = new JComboBox();
		mDryLossCombox.setBounds(420, 313, 144, 21);
		contentPane.add(mDryLossCombox);
		
		mWetLossCombox = new JComboBox();
		mWetLossCombox.setBounds(420, 347, 144, 21);
		contentPane.add(mWetLossCombox);
		
		mDateLossCombox = new JComboBox();
		mDateLossCombox.setBounds(420, 387, 144, 21);
		contentPane.add(mDateLossCombox);
		
		mDryLossShowBtn = new JButton("�鿴");
		mDryLossShowBtn.setBounds(598, 312, 93, 23);
		contentPane.add(mDryLossShowBtn);
		
		mWetLossShowBtn = new JButton("�鿴");
		mWetLossShowBtn.setBounds(598, 346, 93, 23);
		contentPane.add(mWetLossShowBtn);
		
		mDateLossShowBtn = new JButton("�鿴");
		mDateLossShowBtn.setBounds(598, 386, 93, 23);
		contentPane.add(mDateLossShowBtn);
		
		mOkBtn = new JButton("ȷ��");
		mOkBtn.setBounds(268, 764, 93, 23);
		contentPane.add(mOkBtn);
		
		mCancleBtn = new JButton("ȡ��");
		mCancleBtn.setBounds(788, 764, 93, 23);
		contentPane.add(mCancleBtn);
		
		JLabel label_5 = new JLabel("������ͣ�");
		label_5.setBounds(34, 347, 77, 15);
		contentPane.add(label_5);
		
		JComboBox lossTypeCombox = new JComboBox();
		lossTypeCombox.setBounds(130, 344, 144, 21);
		contentPane.add(lossTypeCombox);
		

		JLabel label_6 = new JLabel("���ۺ�ˮ����");
		label_6.setBounds(34, 390, 77, 15);
		contentPane.add(label_6);
		
		waterEdt = new JTextField();
		waterEdt.setBounds(133, 387, 115, 21);
		contentPane.add(waterEdt);
		waterEdt.setColumns(10);
		waterEdt.setEditable(false);
		
		
		
        //ԭ��Table���϶������ṹ��
		mGestureListener = new MyGestureListener();
		mGestureListener.setNodeDragListener(this);
		//�϶���Ŀ���ϼ�����
		mTargetListener = new MyTargetListener<NodeBean>();
		mTargetListener.setFormulatorUpdateListener(this);
		
    	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mMaterialTable, DnDConstants.ACTION_COPY_OR_MOVE, mGestureListener );
    	new DropTarget(mFormulatorTree,mTargetListener);//���� �϶�����Ŀ��
    	
    	
    	//��ǩ���е��϶�������Ϊ����Դ
    	mTable2TableGestureListener = new Table2TableGestureListener();
    	mTable2TableGestureListener.setNodeDragListener(this);
    	//ָ������Ϊ�϶�Ŀ�ĵ�
    	mTable2TableTargetListener = new Table2TableTargetListener<NodeBean>();
    	mTable2TableTargetListener.setIndexTableInsertListener(this);
    	
    	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mLableTable, DnDConstants.ACTION_COPY_OR_MOVE, mTable2TableGestureListener );
    	new DropTarget(mIndexTable,mTable2TableTargetListener);//���� �϶�����Ŀ��
    	
    	//��ǩ���е��϶�������Ϊ����Դ
    	mRemoveGestureListener = new RemoveGestureListener();
    	mRemoveGestureListener.setRemoveNodeDragListener(this);
    	mRemoveGestureListener.setIndexTableRemoveListener(this);
    	//ָ������Ϊ�϶�Ŀ�ĵ�
    	mRemoveTargetListener = new RemoveTargetListener<NodeBean>();
//    	mRemoveTargetListener.setIndexTableRemoveListener(this);
    	
    	DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(mIndexTable, DnDConstants.ACTION_COPY_OR_MOVE, mRemoveGestureListener );
//    	new DropTarget(contentPane,mRemoveTargetListener);//���� �϶�����Ŀ��
    	
        //��ʼ���Ҽ��������Ĳ˵�
    	initMenuAndTree();
        
        //��ʼ�� ԭ��Table  mMaterialTable
        initMaterialTable();
        
        //��ʼ��ָ��table
        initIndexTable();
        
        //��ʼ�������䷽table����Ϣ
        initFormulatorTable();
        
        //��ʼ��lableTable
        initLableTable();
        
        //��ʼ��������� �������Combox
        initLoss();
        
        //��ʼ���� table �ļ����¼�
        intiTableListener();
        
        //��ʼ�����ܰ�ť
        initBtnListener();
        
        
        
       
	}

	
	/**
	 * ��ʼ�������ϵĹ��ܰ�ť
	 */
	private void initBtnListener() {
		//TODO:ȷ��
		
		mCancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		//TODO: �鿴��ť
		
	}


	/**
	 * ��ʼ���ĸ� table �ļ����¼�
	 */
	private void intiTableListener() {
		// TODO ��ʼ�� ���� table�ļ����¼�
		
		//�䷽��Ϣ��
		mFormulatorModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				mSelectedFormulatorTableNodeBean = mFormulatorTableNodeBeanList.get(row);
				if(column==2){//Ͷ�����Ļ�
					mSelectedFormulatorTableNodeBean.U8_inventory = mFormulatorModel.getValueAt(row, column)==null ? "" :mFormulatorModel.getValueAt(row, column).toString();
				}
			}
		});
		
		//ָ����Ϣ��
		mIndexTableModel = (DefaultTableModel) mIndexTable.getModel(); 
		mIndexTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				if(mIndexTableNodeBeanList==null || mIndexTableNodeBeanList.size()<=0){//index�Ƚ����� ����ֻ��һ���յĶ���
					return;
				}
				
				mSelectedIndexTableNodeBean = mIndexTableNodeBeanList.get(row);
				if(column==2){//��Сֵ
					mSelectedIndexTableNodeBean.u8Down = mIndexTableModel.getValueAt(row, column)==null ? "" :mIndexTableModel.getValueAt(row, column).toString();
				}
				if(column==3){//�м�ֵ
					mSelectedIndexTableNodeBean.bl_quantity = mIndexTableModel.getValueAt(row, column)==null ? "" :mIndexTableModel.getValueAt(row, column).toString();
				}
				if(column==4){//���ֵ
					mSelectedIndexTableNodeBean.u8Up = mIndexTableModel.getValueAt(row, column)==null ? "" :mIndexTableModel.getValueAt(row, column).toString();
				}
			}
		});
		
		//��ǩ��Ϣ��
		mLableTableModel = (DefaultTableModel) mLableTable.getModel(); 
		mLableTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				int row = e.getFirstRow();
				int column = e.getColumn();
				mSelectedLableTableNodeBean = mLableTableNodeBeanList.get(row);
				if(column==4){//��ǩֵ
					mSelectedLableTableNodeBean.u8Up = mLableTableModel.getValueAt(row, column)==null ? "" :mLableTableModel.getValueAt(row, column).toString();
				}
			}
		});
		
	}


	/**
	 *��ʼ�����  �Լ��������Լ�����
	 */
	private void initLoss() {
		//TODO:
		
	}

	/**
	 * ��ʼ�� lableTable�е���Ϣ
	 */
	private void initLableTable() {
		mLableTableModel = (DefaultTableModel) mLableTable.getModel();
		
		mLableTableNodeBeanList = service.getLableTableNodeBeanListByFormulatorRev(mRootNodeBean.nodeItemRev);
		
		//��ʼ�����
		for(NodeBean lableNodeBean : mLableTableNodeBeanList){
			mLableTableModel.addRow(new String[]{
					lableNodeBean.itemID,
					lableNodeBean.objectName,
					lableNodeBean.lawValue,
					lableNodeBean.standardValue,
					lableNodeBean.bl_quantity,
					lableNodeBean.minValue,
					lableNodeBean.middleValue,
					lableNodeBean.maxValue
			});
		}
	}

	/**
	 * ��ʼ�������䷽ table
	 */
	private void initFormulatorTable() {
		mFormulatorModel = (DefaultTableModel) mFormulatorTable.getModel();
		
		mFormulatorTableNodeBeanList = service.getFormulatorTableNodeBeanListByRoot(mRootNodeBean);//��ȡ�䷽��Ļ�����Ϣ
		
		//��ʼ�����
		for(NodeBean materialNodeBean : mFormulatorTableNodeBeanList){
			mFormulatorModel.addRow(new String[]{
					materialNodeBean.itemID,
					materialNodeBean.objectName,
					materialNodeBean.U8_inventory
			});
		}
		
		//���� mFormulatorTable ��ѡ���м����¼�
		mFormulatorTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int selectRowIndex = mFormulatorTable.getSelectedRow();
				if(selectRowIndex == -1) return;
				
				NodeBean selectedMaterialNodeBean = mFormulatorTableNodeBeanList.get(selectRowIndex);
				mSelectedNodeBean = selectedMaterialNodeBean;
				
				updateIndexTable(selectedMaterialNodeBean);//����ָ����
				
			}
		});
		
		
		
	}

	/**
	 * ��ʼ�� ָ��table
	 * 
	 * Ĭ����ԭ��table�еĵ�һ��
	 */
	private void initIndexTable() {
		//TODO:
	}

	/**
	 * ��ʼ�� ԭ�ϱ��
	 */
	private void initMaterialTable() {
		//TODO:
		mMaterialTableModel = (DefaultTableModel) mMaterialTable.getModel();
		
		//��ʼ�����
		mMaterialTableNodeBeanList = service.getMaterialByType("type");
		for(NodeBean materialNodeBean : mMaterialTableNodeBeanList){
			mMaterialTableModel.addRow(new String[]{
					materialNodeBean.itemID,
					materialNodeBean.objectName,
					materialNodeBean.bl_quantity
			});
		}
		
		//���ü����¼�
		mMaterialTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent mouseevent) {
				int selectRowIndex = mMaterialTable.getSelectedRow();
				if(selectRowIndex == -1) return;
				
				NodeBean selectedMaterialNodeBean = mMaterialTableNodeBeanList.get(selectRowIndex);
				mSelectedNodeBean = selectedMaterialNodeBean;
				
				updateIndexTable(selectedMaterialNodeBean);//����ָ����
			}
			
		});
		
		
	}
	

	/**
	 * ��ʼ�˵��Ͳ˵��ļ����¼�
	 */
	private void initMenuAndTree() {
		
		mFormulatorTreeModel = (DefaultTreeModel) mFormulatorTree.getModel();
		
		//TODO: �˵������
		mMenu 						= new JPopupMenu();
		mDeleteMenuItem				= new JMenuItem("ɾ��");
		mInsertFormulatorMenuItem 	= new JMenuItem("�������");
		mInsertNutritionMenuItem	= new JMenuItem("����Ӫ����");
		
		
		//����Ӫ�����ĵ���¼�
		mInsertNutritionMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				mFormulatorTree.setSelectionPath(mRightPath);
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
				final NodeBean selectNodeBean = (NodeBean) selectNode.getUserObject();
				
				//ֻ���䷽���ܲ���Ӫ����
				if(!selectNodeBean.nodeType.equals(Const.NodeType.NODE_FORMULA)) return;
				
				InsertNutritionFrame insertNutritionFrame = new InsertNutritionFrame();
				insertNutritionFrame.setCallBack(new AbstractCallBack() {
					//���յ����ݹ�����Ӫ��������
					@Override
					public void setNutritionName(String nutritionName) {
						super.setNutritionName(nutritionName);
						//����һ��ԭ��ITEM ��ΪӪ����
						TCComponentItemRevision nutritionItemRev = service.createNutritionItemByName(nutritionName);
						
						NodeBean childNodeBean =service.initRootNode(nutritionItemRev); 	//nodeBean
						childNodeBean.nodeType = Const.NodeType.NODE_NUTRITION;//��ע����ΪӪ����
						
						insertNodeIntoNode(selectNodeBean,childNodeBean);//����
					}
				});
				insertNutritionFrame.setVisible(true);
			}
		});
		
		
		//������۵ĵ���¼�
		mInsertFormulatorMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				mFormulatorTree.setSelectionPath(mRightPath);
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
				final NodeBean selectNodeBean = (NodeBean) selectNode.getUserObject();
				
				//ֻ���䷽���ܲ������
				if(!selectNodeBean.nodeType.equals(Const.NodeType.NODE_FORMULA)) return;
				
				InsertFormulatorFrame insertFormulatorFrame = new InsertFormulatorFrame();
				insertFormulatorFrame.setCallBack(new AbstractCallBack() {
					@Override
					public void setFormulatorName(String formulatorName, String waterValue) {
						super.setFormulatorName(formulatorName, waterValue);
						
						//����һ��ԭ��ITEM ��ΪӪ����
						TCComponentItemRevision formulatroItemRev = service.createFormulatorItemByName(formulatorName);
						try {//��ˮ��д��������ȥ �������� bean�л�ȡ�õ���
							formulatroItemRev.setProperty("u8_water", waterValue);
						} catch (TCException e) {
							e.printStackTrace();
						}
						
						NodeBean childNodeBean =service.initRootNode(formulatroItemRev); 	//nodeBean
						insertNodeIntoNode(selectNodeBean,childNodeBean);//����
					}
				});
				insertFormulatorFrame.setVisible(true);
			}
		});
		
		//ɾ��ѡ�еĽڵ�
		mDeleteMenuItem.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				super.mousePressed(e);
				mFormulatorTree.setSelectionPath(mRightPath);
				DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
				NodeBean selectNodeBean = (NodeBean) selectNode.getUserObject();
				
				if(selectNodeBean.parentNode==null) return;//���ڵ㲻��ɾ��
				
				DefaultMutableTreeNode parentNode = selectNodeBean.parentNode;
				NodeBean parentNodeBean = (NodeBean) parentNode.getUserObject();
				
				
				service.removeChildFromParent(parentNodeBean, selectNodeBean);//�Ӹ��ڵ�ĺ�ֽ�������Ƴ�
				mFormulatorTreeModel.removeNodeFromParent(selectNode);//�Ƴ�tree�нڵ�
			}
		});

		
		//��Ӳ˵���
		mMenu.add(mDeleteMenuItem);
		mMenu.add(mInsertFormulatorMenuItem);
		mMenu.add(mInsertNutritionMenuItem);
		
		//�����Ҽ�����¼������˵�
		mFormulatorTree.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e) {
					if(e.getButton() == e.BUTTON3){//�һ�
						TreePath path = mFormulatorTree.getPathForLocation(e.getX(), e.getY());
						mRightPath = path;
						mFormulatorTree.setSelectionPath(path);
						System.out.println(path.toString());
						DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
						if(selectNode==null){
							return;
						}
						System.out.println(selectNode.toString());
						mMenu.show(mFormulatorTree, e.getX(), e.getY());
						
					}
					
					if(e.getButton() == e.BUTTON1){//�����ѡ
						//ѡ�е���ԭ�ϵĻ�Ҫչʾ ָ�� ��Ϣ�� ��mIndexTable��
						TreePath path = mFormulatorTree.getPathForLocation(e.getX(), e.getY());
						mFormulatorTree.setSelectionPath(path);
						DefaultMutableTreeNode selectNode = (DefaultMutableTreeNode) mFormulatorTree.getLastSelectedPathComponent();
						if(selectNode==null) return;
						
						NodeBean selectedNodeBean = (NodeBean) selectNode.getUserObject();
						mSelectedNodeBean = selectedNodeBean;//������treeѡ�нڵ���
						
						if(selectedNodeBean.nodeType.equals(Const.NodeType.NODE_MATERIAL) ||
								selectedNodeBean.nodeType.equals(Const.NodeType.NODE_NUTRITION)){//�ж��Ƿ���ԭ�ϻ���Ӫ����
							updateIndexTable(selectedNodeBean);
						}else if(selectedNodeBean.nodeType.equals(Const.NodeType.NODE_FORMULA) ||
								selectedNodeBean.nodeType.equals(Const.NodeType.NODE_BASE_FORMULATOR)){//ѡ������䷽���߻���
							updateFormulatorTable(selectedNodeBean);
						}
					}
				}
		});
		
		
		
	}


	/* (non-Javadoc)
	 * �϶��¼��ļ����¼�
	 */
	@Override
	public NodeBean getSelectedNodeBean(int position) {
		return mMaterialTableNodeBeanList.get(position);
	}


	/* (non-Javadoc)
	 * ���ԭ�ϳɹ���֪ͨ�������� �䷽���е���Ϣ
	 */
	@Override
	public void notifyFormulatorTableUpdate() {
		updateFormulatorTable(mRootNodeBean);
	}
	
	/**
	 * ����ѡ�еĽڵ���Ϣ �鿴ָ����Ŀ
	 */
	private void updateIndexTable(NodeBean selectedNodeBean){
		//��ձ������ 
		if(mIndexTableModel == null){
			mIndexTableModel = (DefaultTableModel) mIndexTable.getModel();
		}
		int rowCount = mIndexTableModel.getRowCount();
		for(int i=0; i<rowCount; i++){
			mIndexTableModel.removeRow(0);
		}
				
		//��ȡָ���Ļ�����Ϣ
		mIndexTableNodeBeanList = service.getIndexTableNodeBeanListBySelectedMaterialNode(selectedNodeBean);
		//��ʼ�����
		for(NodeBean indexNodeBean : mIndexTableNodeBeanList){
			mIndexTableModel.addRow(new String[]{
					indexNodeBean.itemID,
					indexNodeBean.objectName,
					indexNodeBean.u8Down,
					indexNodeBean.bl_quantity,
					indexNodeBean.u8Up
			});
		}
		
		//��������е�����Ϊ�յĻ�Ҫ���϶��¼�һ����·
		if(mIndexTableNodeBeanList.size() == 0){
			mIndexTableModel.addRow(new String[]{
					"",
			});
		}
	}
	
	
	/**
	 * @param selectedNodeBean
	 * ���� �䷽ table �е���Ϣ
	 */
	private void updateFormulatorTable(NodeBean selectedNodeBean){
		//���ú�ˮ��
		waterEdt.setText(selectedNodeBean.u8Water);
		
		//��ձ��
		if(mFormulatorModel == null){
			mFormulatorModel = (DefaultTableModel) mFormulatorTable.getModel();
		}
		int rowCount = mFormulatorModel.getRowCount();
		for(int i=0;i<rowCount;i++){
			mFormulatorModel.removeRow(0);
		}
		
		//��ȥ�䷽��Ļ�����Ϣ
		mFormulatorTableNodeBeanList = service.getFormulatorTableNodeBeanListByRoot(selectedNodeBean);
		//��ʼ�����
		for(NodeBean materialNodeBean : mFormulatorTableNodeBeanList){
			mFormulatorModel.addRow(new String[]{
					materialNodeBean.itemID,
					materialNodeBean.objectName,
					materialNodeBean.U8_inventory
			});
		}
	}
	
	/**
	 * ���������ݺ󴥷���ǩ���е����ݸ��� 
	 */
	private void updateLabelTable(){
		//TODO: �������Ҫ������������ж�Ӧ��Ŀ��ֵ
		//��ձ��
		if (mLableTableModel == null) mLableTableModel = (DefaultTableModel) mLableTable.getModel();
		
		int rowCount = mLableTableModel.getRowCount();
		for (int i = 0; i < rowCount; i++) {
			mLableTableModel.removeRow(0);
		}
		
		//��ֵ
		for (NodeBean lableNodeBean : mLableTableNodeBeanList) {
			mLableTableModel.addRow(new String[] { 
					lableNodeBean.itemID,
					lableNodeBean.objectName, 
					lableNodeBean.lawValue,
					lableNodeBean.standardValue, 
					lableNodeBean.bl_quantity,
					lableNodeBean.minValue, 
					lableNodeBean.middleValue,
					lableNodeBean.maxValue 
			});
		}
	}


	/* (non-Javadoc)
	 * �ӱ�ǩ��ı���л�ȡһ��������Ŀ
	 */
	@Override
	public NodeBean getTable2TableSelectedNodeBean(int position) {
		return mLableTableNodeBeanList.get(position);
	}


	/* (non-Javadoc)
	 * �϶���ǩ�е�ָ�� �� ָ������
	 * ����ָ��table�е����� �����ṹ
	 */
	@Override
	public void notifyIndexTableInsert(NodeBean insertNodeBean) {
		//TODO�����ָ��ĸ��׽ڵ������� Ӫ�������;�������� ��������
		if(!mSelectedNodeBean.nodeType.equals(Const.NodeType.NODE_NUTRITION)){//Ӫ�������͵ĲŲ���
			return;
		}
		
		if(mSelectedNodeBean == null)	return;
		
		if(mSelectedNodeBean.childNodeBeans == null) mSelectedNodeBean.childNodeBeans = new ArrayList<NodeBean>();
		
		if(mSelectedNodeBean.chidNodes == null) mSelectedNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
		
		mSelectedNodeBean.childNodeBeans.add(0, insertNodeBean);
		mSelectedNodeBean.chidNodes.add(0,insertNodeBean.node);
		
		insertNodeBean.parentNode = mSelectedNodeBean.node;
		
		mFormulatorTreeModel.insertNodeInto(insertNodeBean.node, mSelectedNodeBean.node,0);

		updateIndexTable(mSelectedNodeBean);
		
		
	}



	
	/* (non-Javadoc)
	 * ָ֪ͨ��table �Ƴ�һ����Ŀ
	 */
	@Override
	public void notifyIndexTableRemove(NodeBean removeNodeBean) {
		
		int position = -1;
		for(int i=0;i<mIndexTableNodeBeanList.size();i++){
			NodeBean indexNodeBean = mIndexTableNodeBeanList.get(i);
			if(removeNodeBean.itemID.equals(indexNodeBean.itemID)){
				position = i;
			}
		}
		
		mIndexTableNodeBeanList.remove(position);
		mSelectedNodeBean.chidNodes.remove(position);
		mSelectedNodeBean.childNodeBeans.remove(position);
		
		mFormulatorTreeModel.removeNodeFromParent(removeNodeBean.node);
		
		updateIndexTable(mSelectedNodeBean);
	}
	
	


	/* (non-Javadoc)
	 * �õ�Ҫ�Ƴ�ָ�� table �е��Ǹ���
	 */
	@Override
	public NodeBean getRemoveSelectedNodeBean(int position) {
		if(mIndexTableNodeBeanList==null || mIndexTableNodeBeanList.size()<=0){
			return null;
		}
		return mIndexTableNodeBeanList.get(position);
	}
	
	
	/**
	 * @param parentNodeBean
	 * @param childNodeBean
	 * �����ӽڵ���뵽���׽ڵ��µĵ�һ���ڵ���
	 */
	public void insertNodeIntoNode(NodeBean parentNodeBean, NodeBean childNodeBean){
		DefaultMutableTreeNode parentNode = parentNodeBean.node;
		DefaultMutableTreeNode childNode = childNodeBean.node;
		
		childNodeBean.parentNode = parentNode;
		
		if(parentNodeBean.chidNodes == null) parentNodeBean.chidNodes = new ArrayList<DefaultMutableTreeNode>();
		parentNodeBean.chidNodes.add(0,childNode);
		if(parentNodeBean.childNodeBeans == null) parentNodeBean.childNodeBeans = new ArrayList<NodeBean>();
		parentNodeBean.childNodeBeans.add(0,childNodeBean);
		
		mFormulatorTreeModel = (DefaultTreeModel) mFormulatorTree.getModel();
		mFormulatorTreeModel.insertNodeInto( childNode,parentNode, 0);//���뵽Ŀ��ڵ�ĵ�һ��
	}
	
	
	

}
