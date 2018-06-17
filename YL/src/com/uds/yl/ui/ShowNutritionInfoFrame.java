package com.uds.yl.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;


import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.StringsUtil;

import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Color;

public class ShowNutritionInfoFrame extends JFrame {

	private JPanel contentPane;
	
	private JTextField nutritionNameEdt;//ѡ�е�Ӫ����������
	private JTextField materialNameEdt;//��������ԭ�ϵ�����
	private JTable nutritionTable;//Ӫ�����еľ����ԭ��
	private JTable materialTable;//ԭ�ϵ��������
	
	private DefaultTableModel nutritionModel;//Ӫ������
	private DefaultTableModel materialModel;//����ԭ��

	
	private JButton searchMaterialBtn;//����ԭ�ϰ�ť
	private JButton addMaterialBtn;//��ԭ����ӵ�Ӫ������
	private JButton deleteMaterialBtn;//��һ��ԭ�ϴ�Ӫ������ɾ��
	
	
	
	private List<TCComponentItemRevision> materialRevList;//����ԭ���е�
	private List<MaterialBean> materialBeanList;//����ά���������bean
	
	private List<TCComponentItemRevision> nutritionRevList;//Ӫ�����е�
	private List<MaterialBean> beanList;//ֻ����Ӫ������table�п����޸�
	private TCComponentItemRevision selectMaterialRev;//ѡ�е�ԭ��
	
	private TCComponentItemRevision nutritionRev;//���ݹ�����Ӫ�����汾
	private AbstractCallBack mCallBack;//�ص�����
	
	/**
	 * Create the frame.
	 */
	public ShowNutritionInfoFrame(TCComponentItemRevision itemRev,AbstractCallBack callBack) {
		this.nutritionRev = itemRev;
		this.mCallBack = callBack;
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				mCallBack.modifyNutritionRev(nutritionRevList,beanList);//�رմ��ڵĻص�����
			}
		});
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 754, 562);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Ӫ�������ƣ�");
		lblNewLabel.setBounds(35, 22, 79, 20);
		contentPane.add(lblNewLabel);
		
		nutritionNameEdt = new JTextField();
		nutritionNameEdt.setBounds(137, 23, 137, 20);
		contentPane.add(nutritionNameEdt);
		nutritionNameEdt.setColumns(10);
		
		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel.setBounds(29, 67, 699, 207);
		contentPane.add(panel);
		panel.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 74, 537, 111);
		panel.add(scrollPane);
		
		materialTable = new JTable();
		materialTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"����"
			}
		));
		scrollPane.setViewportView(materialTable);
		
		JLabel lblNewLabel_1 = new JLabel("���ƣ�");
		lblNewLabel_1.setBounds(26, 34, 54, 15);
		panel.add(lblNewLabel_1);
		
		materialNameEdt = new JTextField();
		materialNameEdt.setBounds(79, 31, 83, 21);
		panel.add(materialNameEdt);
		materialNameEdt.setColumns(10);
		
		searchMaterialBtn = new JButton("����");
		searchMaterialBtn.setBounds(235, 30, 93, 23);
		panel.add(searchMaterialBtn);
		
		addMaterialBtn = new JButton("���");
		addMaterialBtn.setBounds(578, 142, 93, 23);
		panel.add(addMaterialBtn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(29, 296, 699, 207);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("Ӫ��������");
		lblNewLabel_2.setBounds(10, 20, 81, 15);
		panel_1.add(lblNewLabel_2);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(29, 45, 541, 152);
		panel_1.add(scrollPane_1);
		
		nutritionTable = new JTable();
		nutritionTable.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"����", "����","����","�ڿر�׼","���"
			}
		));
		scrollPane_1.setViewportView(nutritionTable);
		
		deleteMaterialBtn = new JButton("ɾ��");
		deleteMaterialBtn.setBounds(580, 159, 93, 23);
		panel_1.add(deleteMaterialBtn);
		
		{//��ʼ��
			try {
				String nutritionName = nutritionRev.getProperty("object_name");
				nutritionNameEdt.setText(nutritionName);
				
				nutritionModel = (DefaultTableModel) nutritionTable.getModel();
				materialModel = (DefaultTableModel) materialTable.getModel();
				nutritionNameEdt.setEditable(false);
				
				
				materialRevList = new ArrayList<>();
				nutritionRevList = new ArrayList<>();
				beanList = new ArrayList<>();
				materialBeanList = new ArrayList<>();
				
				
				
				//��ʼ��Ӫ����table
				initNutritionTable();
				
				
				nutritionModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						int column = e.getColumn();
						int row = e.getFirstRow();
						if(column==1){//����
							MaterialBean bean  = beanList.get(row);
							bean.up  = nutritionModel.getValueAt(row, column).toString();
						}else if(column==2){//����
							MaterialBean bean  = beanList.get(row);
							bean.down  = nutritionModel.getValueAt(row, column).toString();
						}
					}
				});
				
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		
		//ɾ��Ӫ�����е�һ����Ŀ
		deleteMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = nutritionTable.getSelectedRow();
				if(selectedRow==-1){//ѡ��������
					MessageBox.post("��ѡ��ԭ��","",MessageBox.INFORMATION);
					return;
				}
				
				nutritionRevList.remove(selectedRow);
				beanList.remove(selectedRow);
				
				//ɾ��������Ӫ������Table�е�����
				int tableSize = nutritionModel.getRowCount();
				for(int i=0;i<tableSize;i++){
					nutritionModel.removeRow(0);
				}
				for(int i=0;i<nutritionRevList.size();i++){
					MaterialBean bean = beanList.get(i);
					nutritionModel.addRow(new String[]{bean.objectName,bean.up,bean.down});
				}
				
			}
		});
		
		//����
		searchMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String materialName = materialNameEdt.getText().toString();
				if(StringsUtil.isEmpty(materialName)){//����Ϊ��
					MessageBox.post("�������ѯ����","",MessageBox.INFORMATION);
					return ;
				}
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8MATERIAL.getValue());
				if(query==null){
					MessageBox.post("����ԭ�ϲ�ѯ���Ƿ�����","",MessageBox.INFORMATION);
					return;
				}
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"����"}, new String[]{"*"+materialName+"*"});
				//���������������¸�ֵ
				materialRevList.clear();
				materialBeanList.clear();
				for(TCComponent component : searchResult){
					try {
						TCComponentItemRevision itemRevision = (TCComponentItemRevision) component;
						MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, itemRevision);
						materialRevList.add(itemRevision);
						materialBeanList.add(bean);
						materialModel.addRow(new String[]{bean.objectName});
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		
		
		//�������Ľ�������һ����Ӫ������table��
		addMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = materialTable.getSelectedRow();
				if(selectedRow==-1){
					MessageBox.post("��ѡ��ԭ��","",MessageBox.INFORMATION);
					return ;
				}
				
				nutritionRevList.add(materialRevList.get(selectedRow));
				beanList.add(materialBeanList.get(selectedRow));
				MaterialBean bean = materialBeanList.get(selectedRow);
				nutritionModel.addRow(new String[]{bean.objectName,bean.up,bean.down});
				
			}
		});
		
	}

	/**
	 * ��ʼ��Ӫ������table
	 */
	private void initNutritionTable() {
		//Ӫ��Table�ļ����¼�
		
		
		try {
			TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(nutritionRev, Const.CommonCosnt.BOM_VIEW_NAME);
			if(topBomLine==null){
				topBomLine = BomUtil.setBOMViewForItemRev(nutritionRev);
			}
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				TCComponentItemRevision itemRevision = bomLine.getItemRevision();
				String type = itemRevision.getType();
				if("U8_IndexItemRevision".equals(type)){//��ָ��ֱ������
					if(nutritionRevList.size()>0){
						nutritionRevList.clear();
						beanList.clear();
						MessageBox.post("��ѡ������Ӫ����","",MessageBox.INFORMATION);
						break;
					}
				}
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				nutritionRevList.add(itemRevision);
				beanList.add(bean);
			}
			
			//������ڸ�ֵ
			int tableSize = nutritionModel.getRowCount();
			for(int i=0;i<tableSize;i++){
				nutritionModel.removeRow(0);
			}
			for(int i=0;i<nutritionRevList.size();i++){
				MaterialBean bean = beanList.get(i);
				nutritionModel.addRow(new String[]{bean.objectName,bean.up,bean.down});
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		} 
		
	}
}
