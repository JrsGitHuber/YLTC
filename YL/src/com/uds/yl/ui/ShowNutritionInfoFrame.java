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
	
	private JTextField nutritionNameEdt;//选中的营养包的名称
	private JTextField materialNameEdt;//用来搜索原料的名称
	private JTable nutritionTable;//营养包中的具体的原料
	private JTable materialTable;//原料的搜索结果
	
	private DefaultTableModel nutritionModel;//营养包的
	private DefaultTableModel materialModel;//搜素原料

	
	private JButton searchMaterialBtn;//搜索原料按钮
	private JButton addMaterialBtn;//将原料添加到营养包中
	private JButton deleteMaterialBtn;//将一个原料从营养包中删除
	
	
	
	private List<TCComponentItemRevision> materialRevList;//搜索原料中的
	private List<MaterialBean> materialBeanList;//用来维持搜索结果bean
	
	private List<TCComponentItemRevision> nutritionRevList;//营养包中的
	private List<MaterialBean> beanList;//只能在营养包的table中可以修改
	private TCComponentItemRevision selectMaterialRev;//选中的原料
	
	private TCComponentItemRevision nutritionRev;//传递过来的营养包版本
	private AbstractCallBack mCallBack;//回调函数
	
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
				mCallBack.modifyNutritionRev(nutritionRevList,beanList);//关闭窗口的回调函数
			}
		});
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 754, 562);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("营养包名称：");
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
				"名称"
			}
		));
		scrollPane.setViewportView(materialTable);
		
		JLabel lblNewLabel_1 = new JLabel("名称：");
		lblNewLabel_1.setBounds(26, 34, 54, 15);
		panel.add(lblNewLabel_1);
		
		materialNameEdt = new JTextField();
		materialNameEdt.setBounds(79, 31, 83, 21);
		panel.add(materialNameEdt);
		materialNameEdt.setColumns(10);
		
		searchMaterialBtn = new JButton("搜索");
		searchMaterialBtn.setBounds(235, 30, 93, 23);
		panel.add(searchMaterialBtn);
		
		addMaterialBtn = new JButton("添加");
		addMaterialBtn.setBounds(578, 142, 93, 23);
		panel.add(addMaterialBtn);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
		panel_1.setBounds(29, 296, 699, 207);
		contentPane.add(panel_1);
		panel_1.setLayout(null);
		
		JLabel lblNewLabel_2 = new JLabel("营养包名称");
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
				"名称", "上线","下限","内控标准","企标"
			}
		));
		scrollPane_1.setViewportView(nutritionTable);
		
		deleteMaterialBtn = new JButton("删除");
		deleteMaterialBtn.setBounds(580, 159, 93, 23);
		panel_1.add(deleteMaterialBtn);
		
		{//初始化
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
				
				
				
				//初始化营养包table
				initNutritionTable();
				
				
				nutritionModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						int column = e.getColumn();
						int row = e.getFirstRow();
						if(column==1){//上限
							MaterialBean bean  = beanList.get(row);
							bean.up  = nutritionModel.getValueAt(row, column).toString();
						}else if(column==2){//下限
							MaterialBean bean  = beanList.get(row);
							bean.down  = nutritionModel.getValueAt(row, column).toString();
						}
					}
				});
				
			} catch (TCException e) {
				e.printStackTrace();
			}
		}
		
		
		//删除营养包中的一个条目
		deleteMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = nutritionTable.getSelectedRow();
				if(selectedRow==-1){//选中有问题
					MessageBox.post("请选择原料","",MessageBox.INFORMATION);
					return;
				}
				
				nutritionRevList.remove(selectedRow);
				beanList.remove(selectedRow);
				
				//删除并更新营养包的Table中的数据
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
		
		//搜索
		searchMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String materialName = materialNameEdt.getText().toString();
				if(StringsUtil.isEmpty(materialName)){//名称为空
					MessageBox.post("请输入查询条件","",MessageBox.INFORMATION);
					return ;
				}
				TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8MATERIAL.getValue());
				if(query==null){
					MessageBox.post("请检查原料查询器是否配置","",MessageBox.INFORMATION);
					return;
				}
				TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称"}, new String[]{"*"+materialName+"*"});
				//清空搜索结果并重新赋值
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
		
		
		//从搜索的结果中添加一个到营养包的table中
		addMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int selectedRow = materialTable.getSelectedRow();
				if(selectedRow==-1){
					MessageBox.post("请选择原料","",MessageBox.INFORMATION);
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
	 * 初始化营养包的table
	 */
	private void initNutritionTable() {
		//营养Table的监听事件
		
		
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
				if("U8_IndexItemRevision".equals(type)){//是指标直接跳出
					if(nutritionRevList.size()>0){
						nutritionRevList.clear();
						beanList.clear();
						MessageBox.post("所选对象不是营养包","",MessageBox.INFORMATION);
						break;
					}
				}
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				nutritionRevList.add(itemRevision);
				beanList.add(bean);
			}
			
			//先清空在赋值
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
