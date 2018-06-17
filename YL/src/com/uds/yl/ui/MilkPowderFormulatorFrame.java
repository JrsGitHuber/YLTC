package com.uds.yl.ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JComboBox;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.xssf.eventusermodel.examples.FromHowTo;
import org.apache.soap.util.Bean;
import org.apache.xalan.xsltc.dom.SAXImpl.NamespaceWildcardIterator;
import org.jacorb.trading.constraint.DoubleValue;
import org.jacorb.transaction.Sleeper;

import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.common.actions.NewECMAction;
import com.teamcenter.rac.kernel.TCComponent;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCComponentQuery;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.kernel.TCSession;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.common.LogLevel;
import com.uds.yl.common.QueryClassConst;
import com.uds.yl.common.UserInfoSingleFactory;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.IFormulatorModifyService;
import com.uds.yl.service.IMilkPowderFormulatorService;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.service.impl.FormulatorModifyServiceImpl;
import com.uds.yl.service.impl.MilkPowderFormulatorServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.LOVUtil;
import com.uds.yl.tcutils.QueryUtil;
import com.uds.yl.utils.LogFactory;
import com.uds.yl.utils.StringsUtil;

public class MilkPowderFormulatorFrame extends JFrame {

	private Logger logger = LogFactory.initLog("MilkPowderFormulatorFrame", LogLevel.INFO.getValue());
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private JPanel contentPane;
	private JTextField codeEdt;
	private JTextField revisionEdt;
	private JTextField nutritionEdt;//其实就是配方的名称编辑框
	
	private JTable materialTable;
	private JTable formulatorTable;
	
	private JTextField inventorySumEdt;
	private JTextField ratioSumEdt;
	private JTextField unitCostSumEdt;

	private JComboBox wetLossCombox;//用来显示湿法损耗的combox
	private JComboBox dryLossCombox;//用来显示干法损耗的combox
	private JComboBox dateLossCombox;//用来显示保质期损耗的combox
	private JComboBox typeCombox;//用来显示配方类别的combox
	
	private JButton showWetLossBtn;//湿法损耗模式的查看
	private JButton showDryLossBtn;//干法损耗模式的查看
	private JButton showDateLossBtn;//保质期损耗模式的查看
	private JButton showMaterialBtn;//原料的查看
	private JButton wetAddMaterialBtn;//湿法添加
	private JButton dryAddMaterialBtn;//干法添加
	private JButton nutritionBtn;
	private JButton cancleBtn;
	private JButton createBtn;
	private JButton searchIndexBtn;
	private JButton searchLawBtn;
	private JButton searchMaterialBtn;
	private JButton lawCompareBtn;
	private JButton indexCompareBtn;
	private JButton deleteBtn;
	private JButton creatDryFormulatorBtn;//创建干法配方
	private JButton addWetFormulatorBtn;//添加基粉
	private JButton productExcelBtn;//生产配方表
	private JButton nutritionExcelBtn;//营养包报表
	
	private JTextField materialNameEdt;//查询原料名称
	private JTextField supplierEdt;//查询原料供应商
	private JTextField materialTypeEdt;//查询原料类别
	
	private JTextField formulatorIndexEdt;//法规
	private JTextField formulatorLawEdt;//执行标准
	
	private DefaultTableModel materialTableModel;//原料表中model
	private DefaultTableModel formulatorTableModel;//配方表中的model
	
	private IMilkPowderFormulatorService milkPowderFormulatorService = new MilkPowderFormulatorServiceImpl();
	private IFormulatorModifyService iFormulatorModifyService = new FormulatorModifyServiceImpl();
	private TCComponentItemRevision itemRevision;//配方版本对象
	
	private List<TCComponentItemRevision> materialList = new ArrayList<>();//搜索原料列表
	private List<String> materialNameList = new ArrayList<>();//原料表中的名称
	private List<MaterialBean> materialBeansList = new ArrayList<>();//原料表中的实体类集合
	
	private List<TCComponentItemRevision> formulatorList = new ArrayList<>();//配方中的原料列表
	private List<String> formulatorNameList = new ArrayList<>();//配方表中的名称
	private List<MaterialBean> formulatorMaterialBeanList = new ArrayList<>();//配方中原料对应的实体类
	
	
	private List<String> lossNameList;//损耗模式中显示在combox中的值
	private List<TCComponentItemRevision> lossItemRevList;//模式损耗的所有的集合
	private TCComponentItemRevision wetLossItemRevsion;//选中的湿法损耗模式
	private TCComponentItemRevision dryLossItemRevsion;//选中的干法损耗模式
	private TCComponentItemRevision dateLossItemRevsion;//选中的保质期损耗模式
	
	private List<String> formulatorTypeNameList;//配方类别的名称显示在combonx中的值  因为这里只有一种类型 string所以不用来回转化
	private String formulatorType;//配方类别 选中的
	
	private TCComponentItemRevision formulatorLawIRev;//配方类别对应的法规版本
	private TCComponentItemRevision formulatorIndexRev;//配方类别对应的技术标准版本

	
	//奶粉配方搭建器的两种搭建方法
	private String Wet_Method = "湿法";
	private String Dry_Method = "干法";
	
	//作为法规对比所使用的部分
	private List<TCComponentItemRevision> checkLawRevList = null;//搜索到的匹配的所有法规
	private List<TCComponentBOMLine> waitMaterialBomList = null;//等待检查的添加剂Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//等待检查的指标Bom
	private List<MaterialBean> waitMaterialBeanList = null;//等待检查的添加剂Bom对应的pojo
	private List<IndexItemBean> waitIndexBeanList = null;//等待检查的指标Bom对应的pojo
	private List<MaterialBean> checkMaterialBeanList = null;//添加剂法规Bom对应的pojo
	private List<IndexItemBean> checkIndexBeanList = null;//指标法规Bom对应的pojo
	private List<FormulatorCheckedBean> allCheckedBeanList = null;//检查过的要写到excel的数据

	
	/**
	 * Create the frame.
	 */
	public MilkPowderFormulatorFrame(final TCComponentItemRevision itemRevision) {
		super("奶粉配方搭建器");
		{
			setResizable(false);
			this.itemRevision = itemRevision;
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setBounds(70, 70, 1071, 775);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);
			
			JLabel lblNewLabel = new JLabel("编码：");
			lblNewLabel.setBounds(34, 13, 79, 15);
			contentPane.add(lblNewLabel);
			
			codeEdt = new JTextField();
			codeEdt.setBounds(167, 10, 100, 21);
			contentPane.add(codeEdt);
			codeEdt.setColumns(10);
			
			JLabel lblNewLabel_1 = new JLabel("版本：");
			lblNewLabel_1.setBounds(409, 13, 54, 15);
			contentPane.add(lblNewLabel_1);
			
			revisionEdt = new JTextField();
			revisionEdt.setColumns(10);
			revisionEdt.setBounds(548, 10, 100, 21);
			contentPane.add(revisionEdt);
			
			JLabel label = new JLabel("配方名称：");
			label.setBounds(34, 41, 111, 15);
			contentPane.add(label);
			
			nutritionEdt = new JTextField();
			nutritionEdt.setColumns(10);
			nutritionEdt.setBounds(167, 38, 100, 21);
			contentPane.add(nutritionEdt);
			
			JLabel label_1 = new JLabel("配方类别：");
			label_1.setBounds(409, 41, 79, 15);
			contentPane.add(label_1);
			
			JLabel label_2 = new JLabel("湿法损耗:");
			label_2.setBounds(34, 70, 111, 15);
			contentPane.add(label_2);
			
			
			wetLossCombox = new JComboBox<String>();
			wetLossCombox.setBounds(167, 67, 111, 21);
			contentPane.add(wetLossCombox);
			
			showWetLossBtn = new JButton("查看");
			showWetLossBtn.setBounds(298, 66, 77, 23);
			contentPane.add(showWetLossBtn);
			
			
			JLabel label_8 = new JLabel("干法损耗：");
			label_8.setBounds(34, 103, 111, 15);
			contentPane.add(label_8);
			
			JLabel label_9 = new JLabel("保质期损耗：");
			label_9.setBounds(34, 133, 111, 15);
			contentPane.add(label_9);
			
			dryLossCombox = new JComboBox();
			dryLossCombox.setBounds(167, 100, 111, 21);
			contentPane.add(dryLossCombox);
			
			dateLossCombox = new JComboBox();
			dateLossCombox.setBounds(167, 130, 111, 21);
			contentPane.add(dateLossCombox);
			
			showDryLossBtn = new JButton("查看");
			showDryLossBtn.setBounds(298, 99, 77, 23);
			contentPane.add(showDryLossBtn);
			
			showDateLossBtn = new JButton("查看");
			showDateLossBtn.setBounds(298, 129, 77, 23);
			contentPane.add(showDateLossBtn);
			
			JLabel lblNewLabel_2 = new JLabel("执行标准：");
			lblNewLabel_2.setBounds(409, 98, 85, 18);
			contentPane.add(lblNewLabel_2);
			
			typeCombox = new JComboBox();
			typeCombox.setBounds(548, 38, 111, 21);
			contentPane.add(typeCombox);
			
			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(10, 172, 987, 286);
			contentPane.add(panel);
			panel.setLayout(null);
			
			JLabel label_3 = new JLabel("原料类别：");
			label_3.setBounds(10, 14, 111, 15);
			panel.add(label_3);
			
			JLabel label_4 = new JLabel("原料名：");
			label_4.setBounds(321, 14, 85, 15);
			panel.add(label_4);
			
			materialNameEdt = new JTextField();
			materialNameEdt.setColumns(10);
			materialNameEdt.setBounds(414, 11, 100, 21);
			panel.add(materialNameEdt);
			
			searchMaterialBtn = new JButton("搜索");
			searchMaterialBtn.setBounds(598, 10, 77, 23);
			panel.add(searchMaterialBtn);
			
			JLabel label_5 = new JLabel("供应商：");
			label_5.setBounds(10, 50, 111, 15);
			panel.add(label_5);
			
			supplierEdt = new JTextField();
			supplierEdt.setColumns(10);
			supplierEdt.setBounds(111, 47, 100, 21);
			panel.add(supplierEdt);
			
			JLabel lblNewLabel_3 = new JLabel("可选原料名表：");
			lblNewLabel_3.setBounds(21, 88, 201, 15);
			panel.add(lblNewLabel_3);
			
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(31, 114, 725, 140);
			panel.add(scrollPane);
			
			materialTable = new JTable(){
				public boolean isCellEditable(int row, int column) {
					return false;
				};
			};
			materialTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"原料名", "供应商", "单位成本(元/千克)", "基准单位", "状态"
				}
			));
			scrollPane.setViewportView(materialTable);
			
			showMaterialBtn = new JButton("查看");
			showMaterialBtn.setBounds(826, 162, 115, 23);
			panel.add(showMaterialBtn);
			
			wetAddMaterialBtn = new JButton("湿法添加");
			wetAddMaterialBtn.setBounds(826, 195, 115, 23);
			panel.add(wetAddMaterialBtn);
			
			materialTypeEdt = new JTextField();
			materialTypeEdt.setColumns(10);
			materialTypeEdt.setBounds(111, 11, 100, 21);
			panel.add(materialTypeEdt);
			
			dryAddMaterialBtn = new JButton("干法添加");
			dryAddMaterialBtn.setBounds(826, 228, 115, 23);
			panel.add(dryAddMaterialBtn);
			
			JPanel panel_1 = new JPanel();
			panel_1.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel_1.setBounds(34, 493, 992, 185);
			contentPane.add(panel_1);
			panel_1.setLayout(null);
			
			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(26, 10, 738, 121);
			panel_1.add(scrollPane_1);
			
			formulatorTable = new JTable(){
				public boolean isCellEditable(int row, int column) {
					if(column==1){
						return true;
					}
					return false;
				};
			};
			formulatorTable.setModel(new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"原料名", "投料量(千克)", "配比%", "单位成本(元/千克)","方式"
				}
			));
			scrollPane_1.setViewportView(formulatorTable);
			
			JLabel label_6 = new JLabel("合计：");
			label_6.setBounds(126, 151, 91, 15);
			panel_1.add(label_6);
			
			inventorySumEdt = new JTextField();
			inventorySumEdt.setBounds(207, 148, 180, 21);
			panel_1.add(inventorySumEdt);
			inventorySumEdt.setColumns(10);
			
			ratioSumEdt = new JTextField();
			ratioSumEdt.setColumns(10);
			ratioSumEdt.setBounds(387, 148, 180, 21);
			panel_1.add(ratioSumEdt);
			
			unitCostSumEdt = new JTextField();
			unitCostSumEdt.setColumns(10);
			unitCostSumEdt.setBounds(566, 148, 180, 21);
			panel_1.add(unitCostSumEdt);
			
			deleteBtn = new JButton("删除");
			deleteBtn.setBounds(808, 92, 123, 23);
			panel_1.add(deleteBtn);
			
			addWetFormulatorBtn = new JButton("添加基粉");
			addWetFormulatorBtn.setBounds(808, 56, 123, 23);
			panel_1.add(addWetFormulatorBtn);
			
			JLabel lblNewLabel_4 = new JLabel("配方表：");
			lblNewLabel_4.setBounds(34, 468, 258, 15);
			contentPane.add(lblNewLabel_4);
			
			createBtn = new JButton("创建基粉");
			createBtn.setBounds(34, 704, 111, 23);
			contentPane.add(createBtn);
			
			nutritionBtn = new JButton("营养成分");
			nutritionBtn.setBounds(576, 704, 93, 23);
			contentPane.add(nutritionBtn);
			
			cancleBtn = new JButton("取消");
			cancleBtn.setBounds(952, 704, 93, 23);
			contentPane.add(cancleBtn);
			
			formulatorIndexEdt = new JTextField();
			formulatorIndexEdt.setColumns(10);
			formulatorIndexEdt.setBounds(548, 97, 100, 21);
			contentPane.add(formulatorIndexEdt);
			
			JLabel label_7 = new JLabel("执行法规：");
			label_7.setBounds(409, 68, 85, 18);
			contentPane.add(label_7);
			
			formulatorLawEdt = new JTextField();
			formulatorLawEdt.setColumns(10);
			formulatorLawEdt.setBounds(548, 67, 100, 21);
			contentPane.add(formulatorLawEdt);
			
			searchIndexBtn = new JButton("获取标准");
			searchIndexBtn.setBounds(681, 96, 100, 23);
			contentPane.add(searchIndexBtn);
			
			searchLawBtn = new JButton("获取法规");
			searchLawBtn.setBounds(681, 66, 100, 23);
			contentPane.add(searchLawBtn);
			
			lawCompareBtn = new JButton("法规对比");
			lawCompareBtn.setBounds(321, 704, 93, 23);
			contentPane.add(lawCompareBtn);
			
			indexCompareBtn = new JButton("标准对比");
			indexCompareBtn.setBounds(447, 704, 93, 23);
			contentPane.add(indexCompareBtn);
			
			creatDryFormulatorBtn = new JButton("创建干法");
			creatDryFormulatorBtn.setBounds(186, 704, 100, 23);
			contentPane.add(creatDryFormulatorBtn);
			
			productExcelBtn = new JButton("生产配方");
			productExcelBtn.setBounds(706, 704, 93, 23);
			contentPane.add(productExcelBtn);
			
			nutritionExcelBtn = new JButton("营养包");
			nutritionExcelBtn.setBounds(824, 704, 93, 23);
			contentPane.add(nutritionExcelBtn);
			
			materialTableModel = (DefaultTableModel) materialTable.getModel();
			formulatorTableModel = (DefaultTableModel) formulatorTable.getModel();
			
		}
	
		
		{
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					
					ProgressBarDialog progressBarDialog = new ProgressBarDialog();
					progressBarDialog.start();
					//初始化
					try {
						String formulatorName = itemRevision.getProperty("object_name");
						String revisionId = itemRevision.getProperty("item_revision_id");
						
						nutritionEdt.setText(formulatorName);
						revisionEdt.setText(revisionId);
						
						
						nutritionEdt.setEditable(false);
						revisionEdt.setEditable(false);
						codeEdt.setEditable(false);
						
						inventorySumEdt.setEditable(false);
						ratioSumEdt.setEditable(false);
						unitCostSumEdt.setEditable(false);
						
						
						
						formulatorIndexEdt.setEditable(false);
						formulatorLawEdt.setEditable(false);
						
						initFrame();//初始化界面的方法
						
						progressBarDialog.stop();
					} catch (TCException e) {
						e.printStackTrace();
						progressBarDialog.stop();
					}
				}
			}).start();
			
			
			//设置湿法损耗模式的combox的监听事件
			wetLossCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = wetLossCombox.getSelectedIndex();
					if(selectIndex!=-1){//不是-1就表示选中了一个
						wetLossItemRevsion = lossItemRevList.get(selectIndex);
					}
				}
			});
			
			//设置干法损耗模式的combox的监听事件
			dryLossCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = dryLossCombox.getSelectedIndex();
					if(selectIndex!=-1){//不是-1就表示选中了一个
						dryLossItemRevsion = lossItemRevList.get(selectIndex);
					}
				}
			});
			
			//设置保质期损耗模式的combox的监听事件
			dateLossCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = dateLossCombox.getSelectedIndex();
					if(selectIndex!=-1){//不是-1就表示选中了一个
						dateLossItemRevsion = lossItemRevList.get(selectIndex);
					}
				}
			});
			
			//设置配方类别的combox的监听事件
			typeCombox.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectIndex = typeCombox.getSelectedIndex();
					if(selectIndex!=-1){
//						formulatorType = formulatorTypeNameList.get(selectIndex);
					}
				}
			});
			
			
			//设置配方表中投料量的变换
			formulatorTableModel.addTableModelListener(new TableModelListener() {
				@Override
				public void tableChanged(TableModelEvent e) {
					int column = e.getColumn();
					int row = e.getFirstRow();
					if(column==1){//是投料量的说
						MaterialBean materialBean = formulatorMaterialBeanList.get(row);
						materialBean.U8_inventory = formulatorTableModel.getValueAt(row, column).toString();//更新bean的值
					}
					
					//合计值需要重新计算 可以封装一个方法
					computeSumInventory();
				}
			});
			
			//搜索原料
			searchMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							String name = materialNameEdt.getText().toString();
							String type = materialTypeEdt.getText().toString();
							String supplier = supplierEdt.getText().toString();
							
							if(StringsUtil.isEmpty(name)&&StringsUtil.isEmpty(type)&&StringsUtil.isEmpty(supplier)){
								MessageBox.post("请填写查询信息！","提示",MessageBox.INFORMATION);
								return;
							}
							
							List<TCComponentItemRevision> searchMaterialResult = milkPowderFormulatorService.searchMaterialResult(name,type,supplier);
							//清空原料table中的数据
							int size = materialList.size();
							for(int i=0;i<size;i++){
								materialList.remove(0);
								materialNameList.remove(0);
								materialTableModel.removeRow(0);
								materialBeansList.remove(0);
							}
							//重新赋值
							for(TCComponentItemRevision rev : searchMaterialResult){
								try {
									String materiaName = rev.getProperty("object_name");
									materialList.add(rev);
									materialNameList.add(materiaName);
									materialTableModel.addRow(new String[]{materiaName});
									
									MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, rev);
									materialBeansList.add(materialBean);
								} catch (TCException | InstantiationException | IllegalAccessException e1) {
									e1.printStackTrace();
								}
							}
							progressBarDialog.stop();
						}
					}).start();
					
					
				}
			});
			
			//湿法添加一个原料
			wetAddMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedIndex = materialTable.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("请选择原料","",MessageBox.INFORMATION);
						return;
					}
					TCComponentItemRevision selectRev = materialList.get(selectedIndex);
					String revName = materialNameList.get(selectedIndex);
					MaterialBean materialBean = materialBeansList.get(selectedIndex);
					materialBean.productMethod = Wet_Method;//标记为湿法搭建的原料的说
					
					formulatorList.add(selectRev);
					formulatorNameList.add(revName);
					formulatorMaterialBeanList.add(materialBean);
					
					formulatorTableModel.addRow(new String[]{revName,"","","",Wet_Method});//添加到配方表中
					
					computeSumInventory();
					
				}
			});
			
			
			//干法添加一个原料
			dryAddMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedIndex = materialTable.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("请选择原料","",MessageBox.INFORMATION);
						return;
					}
					TCComponentItemRevision selectRev = materialList.get(selectedIndex);
					String revName = materialNameList.get(selectedIndex);
					MaterialBean materialBean = materialBeansList.get(selectedIndex);
					materialBean.productMethod = Dry_Method;//标记为干法搭建的原料
					
					formulatorList.add(selectRev);
					formulatorNameList.add(revName);
					formulatorMaterialBeanList.add(materialBean);
					
					formulatorTableModel.addRow(new String[]{revName,"","","",Dry_Method});//添加到配方表中
					
					computeSumInventory();
					
				}
			});
		
			
			//删除一个原料从配方的table中
			deleteBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedIndex = formulatorTable.getSelectedRow();
					if(selectedIndex==-1){
						MessageBox.post("请选中一行","",MessageBox.INFORMATION);
						return;
					}
					formulatorList.remove(selectedIndex);
					formulatorNameList.remove(selectedIndex);
					formulatorMaterialBeanList.remove(selectedIndex);
					formulatorTableModel.removeRow(selectedIndex);
					
					computeSumInventory();
				}
			});
		
		
			
			//创建基粉
			createBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
//					milkPowderFormulatorService.createFormulatorBOM(itemRevision, formulatorList, formulatorMaterialBeanList);
//					MessageBox.post("OK","",MessageBox.INFORMATION);
					
					
					//弹出界面 选择这个基粉是当前选中的配方对象还是另外要创建放到Home之下
					CreateWetFormulatorFrame createWetFormulatorFrame = new CreateWetFormulatorFrame(new AbstractCallBack() {
						@Override
						public void createWetFormulator(final boolean selected, final String name) {
							super.createWetFormulator(selected, name);
							
							new Thread(new Runnable() {
								@Override
								public void run() {
									ProgressBarDialog progressBarDialog = new ProgressBarDialog();
									progressBarDialog.start();
									//判断所添加的内容是否都是湿法的
									boolean canCreate = true;
									for(MaterialBean bean : formulatorMaterialBeanList){
										if("干法".equals(bean.productMethod)){//如果有干法就不让创建
											MessageBox.post("请核对配方原料都是湿法","",MessageBox.INFORMATION);
											return;
										}
									}
									if(wetLossItemRevsion==null){
										MessageBox.post("请选择损耗的版本","",MessageBox.INFORMATION);
										return;
									}
									if(selected){
										//select为true就表示在当前的配方中创建结构
										milkPowderFormulatorService.createFormulatorBOM(itemRevision, formulatorList, formulatorMaterialBeanList,wetLossItemRevsion);
										MessageBox.post("OK","",MessageBox.INFORMATION);
									}else {
										//select为false就表示需要在home创建一个基粉的配方
										milkPowderFormulatorService.createWetFormulatorInHome(formulatorList, formulatorMaterialBeanList, name,wetLossItemRevsion);
										MessageBox.post("OK","",MessageBox.INFORMATION);
									}
									
									progressBarDialog.stop();
								}
							}).start();
							
						}
					});
					createWetFormulatorFrame.setVisible(true);
					//如果列表中含有干法的条目的话就提示有问题                                   
				}
				
			});
			
			
			
			//取消
			cancleBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent actionevent) {
					dispose();
				}
			});
			
			//搜索法规e
			searchLawBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SearchLawFrame searchLawFrame = new SearchLawFrame(new AbstractCallBack() {
						@Override
						public void setLawRev(TCComponentItemRevision lawItemRev) {
							super.setLawRev(lawItemRev);
							formulatorLawIRev = lawItemRev;
							try {
								String lawName = lawItemRev.getProperty("object_name");
								formulatorLawEdt.setText(lawName);
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
					});
					searchLawFrame.setVisible(true);
				}
			});
			
			//搜索执行标准
			searchIndexBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					SearchIndexFrame searchIndexFrame = new SearchIndexFrame(new AbstractCallBack() {
						@Override
						public void setIndexRev(TCComponentItemRevision indexItemRev) {
							super.setIndexRev(indexItemRev);
							formulatorIndexRev = indexItemRev;
							try {
								String indexName = indexItemRev.getProperty("object_name");
								formulatorIndexEdt.setText(indexName);
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
					});
					searchIndexFrame.setVisible(true);
				}
			});
			
			
			//查看湿法损耗模式的
			showWetLossBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if(wetLossItemRevsion==null){//没有损耗对象
								MessageBox.post("请选择要查看的损耗对象","",MessageBox.INFORMATION);
								return;
							}
							ShowLossFrame showLossFrame = new ShowLossFrame(wetLossItemRevsion);
							showLossFrame.setVisible(true);
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
				}
			});
			
			//查看干法损耗模式
			showDryLossBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if(dryLossItemRevsion==null){//没有损耗对象
								MessageBox.post("请选择要查看的损耗对象","",MessageBox.INFORMATION);
								return;
							}
							ShowLossFrame showLossFrame = new ShowLossFrame(dryLossItemRevsion);
							showLossFrame.setVisible(true);
							
							progressBarDialog.stop();
						}
					}).start();
				}
			});
			
			
			//查看保质期的损耗模式
			showDateLossBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							if(dateLossItemRevsion==null){//没有损耗对象
								MessageBox.post("请选择要查看的损耗对象","",MessageBox.INFORMATION);
								return;
							}
							ShowLossFrame showLossFrame = new ShowLossFrame(dateLossItemRevsion);
							showLossFrame.setVisible(true);
							
							progressBarDialog.stop();
						}
					}).start();
					
				}
			});
			
			
			//查看原料(既专门用来查看原料包的说)
			showMaterialBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					int selectedRow = materialTable.getSelectedRow();
					if(selectedRow<0){//没有选到
						MessageBox.post("请选择原料","",MessageBox.INFORMATION);
						return;
					}
					
					final TCComponentItemRevision selectMaterialRev = materialList.get(selectedRow);
					ShowNutritionInfoFrame showNutritionInfoFrame = new ShowNutritionInfoFrame(selectMaterialRev, new AbstractCallBack() {
						@Override
						public void modifyNutritionRev(List<TCComponentItemRevision> materialList,
								List<MaterialBean> materialBeanList) {
							super.modifyNutritionRev(materialList, materialBeanList);
							//对选中的营养包进行处理  先清空 然后将版本放进bom中，然后将bean写进tc
							
							milkPowderFormulatorService.updateNutritionStruct(selectMaterialRev, materialList, materialBeanList);
						}
					});
					showNutritionInfoFrame.setVisible(true);
					
				}
			});
		
		
		
			//法规对比
			lawCompareBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							
							//如果法规和技术标准都没有的话就跳出
							if(formulatorLawIRev==null){
								MessageBox.post("请选择法规","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;

							}
							
							//获取法规
							checkLawRevList = iFormulatorLegalCheckService.getRelatedIDLaws(formulatorLawIRev);
							
							
							if(checkLawRevList.size()==0||checkLawRevList==null){
								MessageBox.post("没有匹配的法规","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;
							}
							//根据表格中的数据生成一个临时的配方对象
							TCComponentBOMLine cacheTopBomLine = milkPowderFormulatorService.getCacheTopBomLine(formulatorList, formulatorMaterialBeanList);
							//根据配方获取原料的BOM
							waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
							//根据原料的BOM获取对应的Bean
							waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
							
							//根据配方获取指标的BOM
//							waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
							//根据对应的原料的BOM获取对应的指标的Bean
							waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
							
							//获取法规中的添加剂Bean   要检测的添加剂都来自于选中的法规 
							checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
							//获取法规中的指标Bean是从法规中来的说  都来自于选中的产品标准
							checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
//							milkPowderFormulatorService.getCheckIndexBeanListByIndexStandard(checkIndexBeanList, formulatorIndexRev);
							
							
							
							//检查添加剂  	添加剂的法规比较特殊 专门来自一个法规  待定
							List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
							//检查指标
							List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
							
							//写到excel中
							allCheckedBeanList = new ArrayList<>();
							allCheckedBeanList.addAll(materialCheckedBean);
							allCheckedBeanList.addAll(indexCheckedBean);
							
							iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
							
							
							progressBarDialog.stop();
						}
					}).start();
					
					
				}
			});
			
			
			//执行标准对比
			indexCompareBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							//如果技术标准都没有的话就跳出
							if(formulatorIndexRev==null){
								MessageBox.post("请选择执行标准","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;

							}
							
							checkLawRevList = new ArrayList<>();
							checkLawRevList.add(formulatorIndexRev);
							
							if(checkLawRevList.size()==0||checkLawRevList==null){
								MessageBox.post("没有匹配的法规","",MessageBox.INFORMATION);
								progressBarDialog.stop();
								return;
							}
							TCComponentBOMLine cacheTopBomLine = milkPowderFormulatorService.getCacheTopBomLine(formulatorList, formulatorMaterialBeanList);
							
							//根据配方获取原料的BOM
							waitMaterialBomList = milkPowderFormulatorService.getWaitMaterialBomList(cacheTopBomLine);
							//根据原料的BOM获取对应的Bean
							waitMaterialBeanList = milkPowderFormulatorService.getWaitMaterialBeanList(waitMaterialBomList);	
							
							//根据配方获取指标的BOM
							waitIndexBomList = milkPowderFormulatorService.getWaitIndexBomList(cacheTopBomLine);
							//根据对应的原料的BOM获取对应的指标的Bean
							waitIndexBeanList = milkPowderFormulatorService.getWaitIndexBeanList(waitIndexBomList);
							
							//获取法规中的添加剂Bean   要检测的添加剂都来自于选中的法规 
							checkMaterialBeanList = milkPowderFormulatorService.getCheckMaterialBeanList(checkLawRevList);
							//获取法规中的指标Bean是从法规中来的说  都来自于选中的产品标准
							checkIndexBeanList = milkPowderFormulatorService.getCheckIndexBeanList(checkLawRevList);
//							milkPowderFormulatorService.getCheckIndexBeanListByIndexStandard(checkIndexBeanList, formulatorIndexRev);
							 
							
							
							//检查添加剂  	添加剂的法规比较特殊 专门来自一个法规  待定
							List<FormulatorCheckedBean> materialCheckedBean = milkPowderFormulatorService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
							//检查指标
							List<FormulatorCheckedBean> indexCheckedBean = milkPowderFormulatorService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
							
							//写到excel中
							allCheckedBeanList = new ArrayList<>();
							allCheckedBeanList.addAll(materialCheckedBean);
							allCheckedBeanList.addAll(indexCheckedBean);
							
							milkPowderFormulatorService.write2Excel(allCheckedBeanList);
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
				}
			});
		
		
			//添加基粉
			addWetFormulatorBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					AddWetFormulatorFrame addWetFormulatorFrame = new AddWetFormulatorFrame(new AbstractCallBack() {
						@Override
						public void addWetFormulator(TCComponentItemRevision wetFormulator,String name) {
							super.addWetFormulator(wetFormulator,name);
							
							try {
								TCComponentItemRevision selectRev = wetFormulator;
								MaterialBean  materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, selectRev);
								materialBean.productMethod = Wet_Method;//标记为湿法 因为是基粉
								
								formulatorList.add(selectRev);
								formulatorNameList.add(name);
								formulatorMaterialBeanList.add(materialBean);
								
								formulatorTableModel.addRow(new String[]{name,"","","",Wet_Method});//添加到配方表中 基粉默认是湿法
								computeSumInventory();
							} catch (InstantiationException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
					addWetFormulatorFrame.setVisible(true);
				}
			});
		
		
			//干法创建配方的说
			creatDryFormulatorBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							//这里不用判断直接创建即可  但是湿法的当做配方的说啊
							milkPowderFormulatorService.createDryFormulatorBOM(itemRevision, formulatorList, formulatorMaterialBeanList);
							MessageBox.post("OK","",MessageBox.INFORMATION);
							
							progressBarDialog.stop();
							
						}
					}).start();
					
					
				}
			});
			
			
			//生成生产配方表
			productExcelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							milkPowderFormulatorService.createFormulatorExcel(itemRevision);
							
							progressBarDialog.stop();
							
						}
					}).start();
				}
			});
			
			
			//生成营养包信息表
			nutritionExcelBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progressBarDialog = new ProgressBarDialog();
							progressBarDialog.start();
							
							milkPowderFormulatorService.createNutritionExcel(itemRevision);
							progressBarDialog.stop();
							
						}
					}).start();	
				}
			});
			
			
			//生成营养成分表
			nutritionBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					new Thread(new Runnable() {
						@Override
						public void run() {
							ProgressBarDialog progerBarDialog = new ProgressBarDialog();
							progerBarDialog.start();
							
							
							//配方中所有的指标项目
							TCComponentBOMLine cacheTopBomLine = milkPowderFormulatorService.getCacheTopBomLine(formulatorList, formulatorMaterialBeanList);
							
							//获取经过三个损耗计算好的最终的指标的集合
							List<IndexItemBean> finallIndexBeanList = milkPowderFormulatorService.getFinallIndexBeanList(cacheTopBomLine,
									wetLossItemRevsion, dryLossItemRevsion, dateLossItemRevsion);
							
							
							
							
							//将所有的指标写进营养成分表格  
							milkPowderFormulatorService.createNutritionIndexExcel(finallIndexBeanList,itemRevision);
							
							
							cacheTopBomLine.clearCache();
							TCComponentBOMWindow bomWindow = cacheTopBomLine.getCachedWindow();
							try {
								bomWindow.save();
								bomWindow.close();
							} catch (TCException e) {
								progerBarDialog.stop();
								e.printStackTrace();
							}
							
							progerBarDialog.stop();
						}
					}).start();
					
					
				}
				
			});
	
		}
		
	}



	/**
	 * 计算合计值
	 */
	protected void computeSumInventory() {
		Double sumInventory = 0d;
		int rowCount = formulatorTable.getRowCount();
		for(int i=0;i<rowCount;i++){
			Double inventory = StringsUtil.convertStr2Double(formulatorTableModel.getValueAt(i, 1).toString());
			sumInventory += inventory;
		}
		inventorySumEdt.setText(sumInventory+"");
	}



	/**
	 * @param finalIndexBeanList
	 * 根据损耗值去计算指标项目中的上线和下限
	 */
	private void computeIndexBeanByLoss(List<IndexItemBean> finalIndexBeanList) {
		for(IndexItemBean indexItemBean : finalIndexBeanList){
			Double u8Loss = StringsUtil.convertStr2Double(indexItemBean.u8Loss);
			Double quantity = StringsUtil.convertStr2Double(indexItemBean.bl_quantity);
			Double inventory = StringsUtil.convertStr2Double(indexItemBean.U8_inventory);
			quantity = quantity * (1-u8Loss);
			inventory = inventory * (1-u8Loss);
			indexItemBean.bl_quantity = quantity+"";
			indexItemBean.U8_inventory = inventory+"";
		}
	}

	/**
	 * 初始化界面
	 * 1、损耗模式的初始化
	 * 2、配方类别的初始化
	 */
	private void initFrame() {
		//初始化损耗的说啊
		TCComponentQuery query = QueryUtil.getTCComponentQuery(QueryClassConst.U8_LOSSITEM.getValue());
		TCComponent[] searchResult = QueryUtil.getSearchResult(query, new String[]{"名称"}, new String[]{"*"});
		lossNameList = new ArrayList<String>();
		lossItemRevList = new ArrayList<TCComponentItemRevision>();
		for(TCComponent component : searchResult){
			if(component instanceof TCComponentItem){
				TCComponentItem lossItem  = (TCComponentItem) component;
				try {
					String lossItemName = lossItem.getProperty("object_name");
					TCComponentItemRevision lossItemRev = lossItem.getLatestItemRevision();
					if(lossItemRev == null){
						continue;
					}
					
					lossNameList.add(lossItemName);
					lossItemRevList.add(lossItemRev);
					wetLossCombox.addItem(lossItemName);
					dryLossCombox.addItem(lossItemName);
					dateLossCombox.addItem(lossItemName);
					
				} catch (TCException e) {
					e.printStackTrace();
				}
			}
		}
		//添加一个空的选项
		lossNameList.add(" ");
		wetLossCombox.addItem(" ");
		dryLossCombox.addItem(" ");
		dateLossCombox.addItem(" ");
		lossItemRevList.add(null);
		
		if(lossItemRevList.size()>0){//如果有损耗对象的话 默认选中第一个
			wetLossItemRevsion = lossItemRevList.get(0);
			dryLossItemRevsion = lossItemRevList.get(0);
			dateLossItemRevsion = lossItemRevList.get(0);
		}
		
		
		//配方类别Combox的初始化
		TCSession session = UserInfoSingleFactory.getInstance().getTCSession();
		List<String> formulatorTypeList = LOVUtil.getLovValues(session, Const.MilkPowderFormulator.FORMULATOR_TYPE_LOV);
		for(String type : formulatorTypeList){
			typeCombox.addItem(type);
		}
		
		if(formulatorTypeList.size()>0){//初始化默认值
			formulatorType = formulatorTypeList.get(0);
		}
		
		//初始化配方table界面
		TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(itemRevision, Const.CommonCosnt.BOM_VIEW_NAME);
		if(topBomLine==null){
			topBomLine = BomUtil.setBOMViewForItemRev(itemRevision);
		}
		
		try {
			AIFComponentContext[] children = topBomLine.getChildren();
			for(AIFComponentContext context : children){
				TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
				MaterialBean bean = AnnotationFactory.getInstcnce(MaterialBean.class, bomLine);
				TCComponentItemRevision materailItemRev = bomLine.getItemRevision();
				formulatorList.add(materailItemRev); 
				formulatorNameList.add(bean.objectName);
				formulatorMaterialBeanList.add(bean);
			}
			
			
			int rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				formulatorTableModel.removeRow(0);
			}
			
			for(MaterialBean bean : formulatorMaterialBeanList){
				formulatorTableModel.addRow(new String[]{bean.objectName,bean.U8_inventory,"","",bean.productMethod});
			}
		} catch (TCException | InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		
		
		
	}
}
