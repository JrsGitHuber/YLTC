package com.uds.yl.ui;

//冷饮配方搭建器

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.JScrollPane;
import javax.swing.JTable;


import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;










import com.teamcenter.rac.aif.kernel.AIFComponentContext;
import com.teamcenter.rac.kernel.TCComponentBOMLine;
import com.teamcenter.rac.kernel.TCComponentBOMViewRevision;
import com.teamcenter.rac.kernel.TCComponentBOMWindow;
import com.teamcenter.rac.kernel.TCComponentItem;
import com.teamcenter.rac.kernel.TCComponentItemRevision;
import com.teamcenter.rac.kernel.TCException;
import com.teamcenter.rac.util.MessageBox;
import com.teamcenter.soaictstubs.booleanSeq_tHolder;
import com.uds.yl.annotation.AnnotationFactory;
import com.uds.yl.bean.FormulatorCheckedBean;
import com.uds.yl.bean.IndexItemBean;
import com.uds.yl.bean.MaterialBean;
import com.uds.yl.bean.MinMaterialBean;
import com.uds.yl.common.Const;
import com.uds.yl.interfaces.AbstractCallBack;
import com.uds.yl.service.IColdFormulatorService;
import com.uds.yl.service.IFormulatorLegalCheckService;
import com.uds.yl.service.impl.ColdFormulatorServiceImpl;
import com.uds.yl.service.impl.FormulatorLegalCheckServiceImpl;
import com.uds.yl.tcutils.BomUtil;
import com.uds.yl.tcutils.ItemUtil;
import com.uds.yl.utils.DoubleUtil;
import com.uds.yl.utils.StringsUtil;



























import javax.swing.JButton;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;







import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author infodba
 * 
 * bl_quantity 数量  指标存在的值得说 比如蛋白质的量是10mg/100g
 *
 */
public class ColdFormulatorFrame extends JFrame {

	
	public static boolean isShow = false;
	
	private IColdFormulatorService iColdFormulatorService = new ColdFormulatorServiceImpl();
	
	private JPanel contentPane;
	private JTextField formulatorNameText;
	private JTextField formulatorRevText;
	
	private JTable componentTable;
	private JTextField componentNameText;//组分的名字
	private JTextField materialSum0;//投料量合计
	private JTextField materialSum1;//配比合计
	private JTextField materialSum2;//单位成本合计
	
	private JTable formulatorTable;
	private JTextField componentSumText0;//投料量合计
	private JTextField componentSumText1;//配比合计
	private JTextField componentSumText2;//单位成本合计
	private JTextField componentSumText3;//能量合计
	private JTextField componentSumText4;//蛋白质合计
	private JTextField componentSumText5;//脂肪合计
	private JTextField componentSumText6;//碳水化合物合计
	private JTextField componentSumText7;//钠合计
	private JTextField componentSumText8;//反式脂肪酸合计
	private JTextField componentSumText9;
	private JTextField componentSumText10;
	private JTextField componentSumText11;
	private JTextField componentSumText12;
	
	private JTextField supplementEdt;//用于显示当前组分的补足的值是多少
	
	private TCComponentItemRevision formulatorRev;//选中的配方版本
	
	private List<ComponenetBean> mComponenetBeansList;//配方中的组分实体列集合
	private List<ComponentBom> mComponentBomsList;//配方中的组分BOM集合
	
	private ComponenetBean currentComponentBean;//当前选中的组分Bean对象
	private ComponentBom currentComponentBom;//当前选中的组分的BOM实体类

	private JButton showBtn;//查看一个组分
	private JButton newComponentBtn;//新增一个组分 在组分的table中
	private JButton deleteComponentBtn;//删除一个组分

	private JButton addMaterialBtn;//在组分中添加一个原料
	private JButton deleteMaterialBtn;//在组分中删除一个原料
	private JButton addComponentBtn;//将调整过结果的组分对象添加到配方中  要过滤那些是否是新建的或者是已经存在的
	private JButton supplementBtn;//触发在搭建组分的时候进行补水的功能  水中的Na含量为20mg/100g
	
	private JButton createBtn;//创建
	private JButton labelComputeBtn;//标签计算
	private JButton physicalAndChemicalBtn;//理化标签
	private JButton clearBtn;//暂时认为清除配方的结构  就是list集合清空，界面表格清空
	private JButton cancleBtn;//配方搭建器取消
	
	private JButton searchLawBtn;//搜索法规
	private JTextField lawRevIDEdt;//法规的名字
	private TCComponentItemRevision lawRevsion = null;//作合规性检查的法规
	
	private JButton lawCheckBtn;//法规对比
	
	private List<TCComponentItemRevision> checkLawRevList;//作为存储等待检查的法规数组
	private IFormulatorLegalCheckService iFormulatorLegalCheckService = new  FormulatorLegalCheckServiceImpl();
	private List<TCComponentBOMLine> waitMaterialBomList = null;//等待检查的添加剂Bom
	private List<TCComponentBOMLine> waitIndexBomList = null;//等待检查的指标Bom
	
	private List<MaterialBean> waitMaterialBeanList = null;//等待检查的添加剂Bom对应的pojo
	private List<IndexItemBean> waitIndexBeanList = null;//等待检查的指标Bom对应的pojo
	
	private List<MaterialBean> checkMaterialBeanList = null;//添加剂法规Bom对应的pojo
	private List<IndexItemBean> checkIndexBeanList = null;//指标法规Bom对应的pojo
	
	private List<FormulatorCheckedBean> allCheckedBeanList = new ArrayList<>();//检查过的要写到excel的数据
	
	private ProgressBarDialog progressBarDialog;//进度条
	
	private JLabel componentSumLabel0;
	private JLabel componentSumLabel1;
	private JLabel componentSumLabel2;
	private JLabel componentSumLabel3;
	private JLabel componentSumLabel4;
	private JLabel componentSumLabel5;
	private JLabel componentSumLabel6;
	private JLabel componentSumLabel7;
	private JLabel componentSumLabel8;
	private JLabel componentSumLabel9;
	private JLabel componentSumLabel10;
	private JLabel componentSumLabel11;
	private JLabel componentSumLabel12;
	
	private DefaultTableModel formulatorTableModel;//配方表
	private DefaultTableModel componentTableModel;//组分表
	
	private String[] indexNameArray = new String[]{"能量","脂肪","碳水化合物","钠","反式脂肪酸","干物质"};

	private boolean isNutritionFlag = true;//默认是计算的营养标签指标



	/**
	 * Create the frame.
	 */
	public ColdFormulatorFrame(TCComponentItemRevision rev) {
		
		super("冷饮配方管理器");
		
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				isShow = false;
				if(progressBarDialog.isLive()){
					progressBarDialog.stop();
				}
			}
		});
		
		
		{
			isShow = true;
			setResizable(false);
			this.formulatorRev = rev;
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setBounds(100, 100, 1405, 738);
			contentPane = new JPanel();
			contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
			setContentPane(contentPane);
			contentPane.setLayout(null);

			JLabel lblNewLabel = new JLabel("配方：");
			lblNewLabel.setBounds(47, 10, 54, 15);
			contentPane.add(lblNewLabel);

			formulatorNameText = new JTextField();
			formulatorNameText.setBounds(117, 7, 109, 21);
			contentPane.add(formulatorNameText);
			formulatorNameText.setColumns(10);

			JLabel label = new JLabel("版本：");
			label.setBounds(312, 13, 54, 15);
			contentPane.add(label);

			formulatorRevText = new JTextField();
			formulatorRevText.setColumns(10);
			formulatorRevText.setBounds(376, 7, 109, 21);
			contentPane.add(formulatorRevText);

			JPanel panel = new JPanel();
			panel.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.setBounds(36, 56, 1238, 212);
			contentPane.add(panel);
			panel.setLayout(null);

			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setBounds(26, 38, 754, 128);
			panel.add(scrollPane);

			componentTable = new JTable();
			componentTable.setModel(
					new DefaultTableModel(new Object[][] {}, new String[] { "原料名", "投料量(kg)", "配比%", "单位成本(元/kg)","小料名称" }));
			componentTable.getColumnModel().getColumn(1).setPreferredWidth(93);
			componentTable.getColumnModel().getColumn(2).setPreferredWidth(56);
			componentTable.getColumnModel().getColumn(3).setPreferredWidth(116);
			scrollPane.setViewportView(componentTable);

			JLabel label_1 = new JLabel("组分：");
			label_1.setBounds(133, 13, 54, 15);
			panel.add(label_1);

			componentNameText = new JTextField();
			componentNameText.setColumns(10);
			componentNameText.setBounds(203, 10, 109, 21);
			panel.add(componentNameText);

			addMaterialBtn = new JButton("添加原料");
			addMaterialBtn.setBounds(929, 38, 132, 23);
			panel.add(addMaterialBtn);

			deleteMaterialBtn = new JButton("删除原料");
			deleteMaterialBtn.setBounds(929, 96, 132, 23);
			panel.add(deleteMaterialBtn);

			addComponentBtn = new JButton("确定");
			addComponentBtn.setBounds(929, 140, 132, 23);
			panel.add(addComponentBtn);

			supplementBtn = new JButton("补足");
			supplementBtn.setBounds(929, 179, 100, 23);
			panel.add(supplementBtn);

			supplementEdt = new JTextField();
			supplementEdt.setColumns(10);
			supplementEdt.setBounds(1062, 181, 100, 21);
			panel.add(supplementEdt);
			supplementEdt.setEditable(false);

			JLabel label_2 = new JLabel("合计：");
			label_2.setBounds(87, 176, 54, 15);
			panel.add(label_2);

			materialSum0 = new JTextField();
			materialSum0.setColumns(10);
			materialSum0.setBounds(180, 176, 132, 21);
			panel.add(materialSum0);

			materialSum1 = new JTextField();
			materialSum1.setColumns(10);
			materialSum1.setBounds(416, 176, 115, 21);
			panel.add(materialSum1);

			materialSum2 = new JTextField();
			materialSum2.setColumns(10);
			materialSum2.setBounds(585, 180, 122, 21);
			panel.add(materialSum2);

			JPanel componentSumLabelTwo = new JPanel();
			componentSumLabelTwo.setBorder(new LineBorder(new Color(0, 0, 0)));
			componentSumLabelTwo.setBounds(47, 316, 1328, 329);
			contentPane.add(componentSumLabelTwo);
			componentSumLabelTwo.setLayout(null);

			JScrollPane scrollPane_1 = new JScrollPane();
			scrollPane_1.setBounds(10, 29, 1205, 204);
			componentSumLabelTwo.add(scrollPane_1);

			formulatorTable = new JTable() {
				@Override
				public boolean isCellEditable(int row, int column) {
					if (column == 1) {// 只有第一列可以编辑
						return true;
					}
					return false;
				}
			};
			formulatorTable.setModel(
					new DefaultTableModel(new Object[][] {}, new String[] { "组分名", "投料量(克)", "配比%", "单位成本(元/千克)"
					// "能量/KJ", "蛋白质%", "脂肪%", "碳水化合物%", "钠/mg",
					// "反式脂肪酸/g","干物质/g"
			}));
			formulatorTable.getColumnModel().getColumn(3).setPreferredWidth(118);
			// formulatorTable.getColumnModel().getColumn(7).setPreferredWidth(93);
			// formulatorTable.getColumnModel().getColumn(8).setPreferredWidth(63);
			// formulatorTable.getColumnModel().getColumn(9).setPreferredWidth(92);
			scrollPane_1.setViewportView(formulatorTable);

			JLabel label_4 = new JLabel("合计：");
			label_4.setBounds(20, 301, 54, 15);
			componentSumLabelTwo.add(label_4);

			componentSumText0 = new JTextField();
			componentSumText0.setColumns(10);
			componentSumText0.setBounds(85, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText0);

			componentSumText1 = new JTextField();
			componentSumText1.setColumns(10);
			componentSumText1.setBounds(160, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText1);

			componentSumText2 = new JTextField();
			componentSumText2.setColumns(10);
			componentSumText2.setBounds(248, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText2);

			componentSumText3 = new JTextField();
			componentSumText3.setColumns(10);
			componentSumText3.setBounds(347, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText3);

			componentSumText4 = new JTextField();
			componentSumText4.setColumns(10);
			componentSumText4.setBounds(436, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText4);

			componentSumText5 = new JTextField();
			componentSumText5.setColumns(10);
			componentSumText5.setBounds(535, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText5);

			componentSumText6 = new JTextField();
			componentSumText6.setColumns(10);
			componentSumText6.setBounds(628, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText6);

			componentSumText7 = new JTextField();
			componentSumText7.setColumns(10);
			componentSumText7.setBounds(722, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText7);

			componentSumText8 = new JTextField();
			componentSumText8.setColumns(10);
			componentSumText8.setBounds(814, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText8);

			showBtn = new JButton("查看");
			showBtn.setBounds(1225, 73, 93, 23);
			componentSumLabelTwo.add(showBtn);

			newComponentBtn = new JButton("新增");
			newComponentBtn.setBounds(1225, 115, 93, 23);
			componentSumLabelTwo.add(newComponentBtn);

			deleteComponentBtn = new JButton("删除");
			deleteComponentBtn.setBounds(1225, 148, 93, 23);
			componentSumLabelTwo.add(deleteComponentBtn);

			componentSumText9 = new JTextField();
			componentSumText9.setColumns(10);
			componentSumText9.setBounds(896, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText9);

			componentSumText10 = new JTextField();
			componentSumText10.setColumns(10);
			componentSumText10.setBounds(984, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText10);

			componentSumText11 = new JTextField();
			componentSumText11.setColumns(10);
			componentSumText11.setBounds(1066, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText11);

			componentSumText12 = new JTextField();
			componentSumText12.setColumns(10);
			componentSumText12.setBounds(1151, 298, 66, 21);
			componentSumLabelTwo.add(componentSumText12);

			componentSumLabel0 = new JLabel("投料量");
			componentSumLabel0.setBounds(85, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel0);

			componentSumLabel1 = new JLabel("配比");
			componentSumLabel1.setBounds(160, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel1);

			componentSumLabel2 = new JLabel("单位成本");
			componentSumLabel2.setBounds(248, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel2);

			componentSumLabel3 = new JLabel("");
			componentSumLabel3.setBounds(347, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel3);

			componentSumLabel4 = new JLabel("");
			componentSumLabel4.setBounds(436, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel4);

			componentSumLabel5 = new JLabel("");
			componentSumLabel5.setBounds(535, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel5);

			componentSumLabel6 = new JLabel("");
			componentSumLabel6.setBounds(628, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel6);

			componentSumLabel7 = new JLabel("");
			componentSumLabel7.setBounds(722, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel7);

			componentSumLabel8 = new JLabel("");
			componentSumLabel8.setBounds(814, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel8);

			componentSumLabel9 = new JLabel("");
			componentSumLabel9.setBounds(896, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel9);

			componentSumLabel10 = new JLabel("");
			componentSumLabel10.setBounds(984, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel10);

			componentSumLabel11 = new JLabel("");
			componentSumLabel11.setBounds(1066, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel11);

			componentSumLabel12 = new JLabel("");
			componentSumLabel12.setBounds(1151, 268, 54, 15);
			componentSumLabelTwo.add(componentSumLabel12);

			JLabel label_3 = new JLabel("冷饮配方");
			label_3.setBounds(60, 291, 83, 15);
			contentPane.add(label_3);

			createBtn = new JButton("创建");
			createBtn.setBounds(171, 667, 93, 23);
			contentPane.add(createBtn);

			labelComputeBtn = new JButton("标签计算");
			labelComputeBtn.setBounds(311, 667, 93, 23);
			contentPane.add(labelComputeBtn);

			physicalAndChemicalBtn = new JButton("理化指标");
			physicalAndChemicalBtn.setBounds(445, 667, 93, 23);
			contentPane.add(physicalAndChemicalBtn);

			clearBtn = new JButton("清除");
			clearBtn.setBounds(741, 667, 93, 23);
			contentPane.add(clearBtn);
			
			cancleBtn = new JButton("取消");
			cancleBtn.setBounds(880, 667, 93, 23);
			contentPane.add(cancleBtn);
			
			JLabel label_5 = new JLabel("法规");
			label_5.setBounds(629, 10, 54, 15);
			contentPane.add(label_5);
			
			lawRevIDEdt = new JTextField();
			lawRevIDEdt.setColumns(10);
			lawRevIDEdt.setBounds(674, 7, 160, 21);
			contentPane.add(lawRevIDEdt);
			lawRevIDEdt.setEditable(false);
			
			searchLawBtn = new JButton("添加法规");
			searchLawBtn.setBounds(892, 6, 132, 23);
			contentPane.add(searchLawBtn);
			
			lawCheckBtn = new JButton("法规对比");
			lawCheckBtn.setBounds(577, 667, 93, 23);
			contentPane.add(lawCheckBtn);
			
		}
		
		
		
		
		{//初始化Name和版本
			try {
				
				formulatorTableModel = (DefaultTableModel) formulatorTable.getModel();
				componentTableModel = (DefaultTableModel) componentTable.getModel();
				
				String name = formulatorRev.getProperty("object_name");
				String revId = formulatorRev.getProperty("item_revision_id");
				formulatorNameText.setText(name);formulatorNameText.setEditable(false);
				formulatorRevText.setText(revId);formulatorRevText.setEditable(false);
				
				//组分名称以及一些其他的合计编辑框设置为不可编辑
				componentNameText.setEditable(false);
				componentSumText0.setEditable(false);componentSumText1.setEditable(false);
				componentSumText2.setEditable(false);componentSumText3.setEditable(false);
				componentSumText4.setEditable(false);componentSumText5.setEditable(false);
				componentSumText6.setEditable(false);componentSumText7.setEditable(false);
				componentSumText8.setEditable(false);componentSumText9.setEditable(false);
				componentSumText10.setEditable(false);componentSumText11.setEditable(false);
				componentSumText12.setEditable(false);
				materialSum0.setEditable(false);materialSum1.setEditable(false);materialSum2.setEditable(false);
				
				
				//配方表的监听事件  对于配方中的组分来说而言
				formulatorTableModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						int row = e.getFirstRow();
						int column = e.getColumn();
						if(column !=1){
							return ;
						}
						if(column==1){//投料量的话
							refreshFormualtorTable();
						}
					}
				});
				
				//对组分表中的数据行行更新
				componentTableModel.addTableModelListener(new TableModelListener() {
					@Override
					public void tableChanged(TableModelEvent e) {
						//除了第一个列的值以外进行恢复
						int row = e.getFirstRow();
						int column = e.getColumn();
						if(column==1 || column==4){
							refreshCurrentComponentTable();
						}
					}
				});
				
				
			} catch (TCException e1) {
				e1.printStackTrace();
			}
			
			
			progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
				@Override
				public void run() {
					
					//获取组分BOM的信息
					mComponentBomsList = iColdFormulatorService.getComponentBomLineList(formulatorRev);
					mComponenetBeansList = iColdFormulatorService.getComponentBeanList(formulatorRev);
					
					//初始化配方表的信息   --先初始化配方表然后获取当前选中的组分对象 默认是第一个的
					initFormulaTable();
					
					//初始化只有默认选中的是第一个组分
					currentComponentBean = mComponenetBeansList.size()==0 ? null : mComponenetBeansList.get(0);
					currentComponentBom = mComponentBomsList.size()==0 ? null : mComponentBomsList.get(0);
					//组分表默认是第一个组分的信息
					initComponentTable();
					
					//将营养指标项目的label和text隐藏起来
					setComponentSumTextAndLabelInvisiable();
					
					//组分中原料的投料量的综合计算
					Double currentComponentSum = computeSumCurrentComponent();
					
					//刷新组分中原料的比例
					int rowCount = componentTable.getRowCount();
					DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
					for(int i=0;i<rowCount;i++){
						String materialName = model.getValueAt(i,0).toString();
						for(MaterialBean materialBean : currentComponentBean.childBeanList){
							if(materialName.equals(materialBean.objectName)){
								Double blQuantity = StringsUtil.convertStr2Double(materialBean.U8_inventory) / currentComponentSum * 100;
								materialBean.bl_quantity = DoubleUtil.formatNumber(blQuantity);
								model.setValueAt(materialBean.bl_quantity,i, 2);
							}
						}
					}
					progressBarDialog.stop();
				}
			}));
			progressBarDialog.start();
			
			
			
			
		}
		
		//在组分中添加原料
		addMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//查询并选择原料后的回调函数 
				AbstractCallBack callBack = new AbstractCallBack() {
					@Override
					public void setMaterialItem(TCComponentItemRevision materialItemRev) {
						super.setMaterialItem(materialItemRev);
						
						//TODO:确定添加之前需要判断是否已经存在该个原料
						
						try {
							//将Bean添加到组分实体类中
							MaterialBean materialBean = AnnotationFactory.getInstcnce(MaterialBean.class, materialItemRev);
							currentComponentBean.childBeanList.add(materialBean);
							
							DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
							model.addRow(new String[]{materialBean.objectName,materialBean.U8_inventory,materialBean.bl_quantity,materialBean.u8Uom,materialBean.minMaterialType});
							
							//将Bom添加到组分Bom实体类中
							TCComponentBOMLine bomLine  = null;
							if(isNutritionFlag){
								 bomLine = BomUtil.getTopBomLine(materialItemRev, "冷饮营养标签");
							}else {
								 bomLine = BomUtil.getTopBomLine(materialItemRev, "视图");
							}
							
							if(bomLine==null){
								bomLine = BomUtil.setBOMViewForItemRev(materialItemRev);
							}
							currentComponentBom.childBomList.add(bomLine);
							
							
						} catch (InstantiationException | IllegalAccessException e) {
							e.printStackTrace();
						}
						
						
					}
				};
				
				SearchMaterialFrame searchMaterialFrame = new SearchMaterialFrame(callBack);
				searchMaterialFrame.setVisible(true);
			}
		});
	
	
		//添加法规
		searchLawBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SearchLawFrame searchLawFrame = new SearchLawFrame(new AbstractCallBack() {
					@Override
					public void setLawRev(TCComponentItemRevision lawItemRev) {
						super.setLawRev(lawItemRev);
						lawRevsion = lawItemRev;
						try {
							String lawName = lawItemRev.getProperty("object_name");
							lawRevIDEdt.setText(lawName);
						} catch (TCException e) {
							e.printStackTrace();
						}
					}
				});
				searchLawFrame.setVisible(true);
			}
		});
		
		//法规对比
		lawCheckBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						
						if(lawRevsion==null){//如果法规没选择的话就提示
							MessageBox.post("请选择配方对应的产品法规","",MessageBox.INFORMATION);
							progressBarDialog.stop();
							return;
						}
						
						//先替换为理化指标
						resetPhysicalAndChemicalIndexBom();
						
						
						//先生成作为临时的配方对象的说  需要根据不同的结构 将多层的原料投料量写进去
						TCComponentBOMLine cacheTopBomLine = iColdFormulatorService.getCacheTopBomLine(mComponentBomsList, mComponenetBeansList);
						
						checkLawRevList = iFormulatorLegalCheckService.getRelatedIDLaws(lawRevsion);
						
						
						//根据配方获取原料的BOM
						waitMaterialBomList = iFormulatorLegalCheckService.getWaitMaterialBomList(cacheTopBomLine);
						//根据原料的BOM获取对应的Bean
						waitMaterialBeanList = iFormulatorLegalCheckService.getWaitMaterialBeanList(waitMaterialBomList);	
						
						//根据配方获取指标的BOM
//						waitIndexBomList = iFormulatorLegalCheckService.getWaitIndexBomList(cacheTopBomLine);
						//根据对应的原料的BOM获取对应的指标的Bean
						waitIndexBeanList = iFormulatorLegalCheckService.getWaitIndexBeanList(cacheTopBomLine);
						
						//所有的指标中的量要除以配方的总的含量 
						Double sumInventory = 0d;
						for(int i=0;i<mComponenetBeansList.size();i++){
							Double inventory = StringsUtil.convertStr2Double(mComponenetBeansList.get(i).component.U8_inventory);
							sumInventory += inventory;
						}
						for(int i=0;i<waitIndexBeanList.size();i++){
							Double indexInventory = StringsUtil.convertStr2Double(waitIndexBeanList.get(i).U8_inventory);
							indexInventory = indexInventory/sumInventory;
							waitIndexBeanList.get(i).U8_inventory = indexInventory+"";
						}
						
						//获取法规中的添加剂Bean   要检测的添加剂都来自于选中的法规 
						checkMaterialBeanList = iFormulatorLegalCheckService.getCheckMaterialBeanList(checkLawRevList);
						//获取法规中的指标Bean是从法规中来的说  都来自于选中的产品标准
						checkIndexBeanList = iFormulatorLegalCheckService.getCheckIndexBeanList(checkLawRevList);
						
						
						//检查添加剂  	添加剂的法规比较特殊 专门来自一个法规  待定
						List<FormulatorCheckedBean> materialCheckedBean = iFormulatorLegalCheckService.getMaterialCheckedBean(waitMaterialBeanList, checkMaterialBeanList);
						//检查指标
						List<FormulatorCheckedBean> indexCheckedBean = iFormulatorLegalCheckService.getIndexCheckedBean(waitIndexBeanList, checkIndexBeanList);
						
						//写到excel中
						allCheckedBeanList.addAll(materialCheckedBean);
						allCheckedBeanList.addAll(indexCheckedBean);
						iFormulatorLegalCheckService.write2Excel(allCheckedBeanList);
						
						//将组分中的数据换回去
						if(isNutritionFlag){
							resetNutririonIndexBom();
						}else{
							resetPhysicalAndChemicalIndexBom();
						}
						
						progressBarDialog.stop();
						
					}
				}));
				progressBarDialog.start();
				
				
				
			}
		});
		
		//删除原料
		deleteMaterialBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				DefaultTableModel modle = (DefaultTableModel) componentTable.getModel();
				int selectIndex = componentTable.getSelectedRow();
				if(selectIndex==-1){//请选中原料
					MessageBox.post("请选中要删除的原料","",MessageBox.INFORMATION);
					return;
				}
				
				//在组分界面表格中删除这条要删除的原料
				modle.removeRow(selectIndex);
			
				//删除选中的原料对象
				currentComponentBean.childBeanList.remove(selectIndex);
				currentComponentBom.childBomList.remove(selectIndex);
				
			}
		});
		
		
		
		//将当先在组分表中的组分对象添加到配方中去 和 配方的数据对象集合中去
		addComponentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				//如果当前组分为空   也就是通过componentNameText判断
				if("".equals(componentNameText.getText())){
					MessageBox.post("请检查组分信息","",MessageBox.INFORMATION);
					return;
				}
				boolean exitComponent = false;//默认不存在
				for(ComponenetBean componenetBean : mComponenetBeansList){
					String componentName = componenetBean.component.objectName;
					String currentComponentName = componentNameText.getText();
					if(componentName.equals(currentComponentName)){//当前的组分在配方中有了
						exitComponent = true;
						break;
					}
				}
				if(exitComponent){//组分存在
					//TODO:更新组分在配方表中的信息    该组分对象信息是在list集合中的不用修改
				}else{
					//将该组分添加到配方表中的一行 然后将该组分对象存放到对应的list集合中去
					mComponenetBeansList.add(currentComponentBean);
					mComponentBomsList.add(currentComponentBom);
					
					DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
					//TODO:更新配方表中的代表组分信息的一行
					model.addRow(new String[]{currentComponentBean.component.objectName});
					
				}
			}
		});
	
		
		//展示选中的这个组分
		showBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				
				boolean componentExit = false;
				// 首先判断单当前组分是否已经保存的说
				for (ComponenetBean componenetBean : mComponenetBeansList) {
					String componentName = componenetBean.component.objectName;
					String currentComponentName = componentNameText.getText();
					if (currentComponentName.equals(componentName)) {// 找到了重名组分
						componentExit = true;
						break;
					}
				}
				
				
				//要最先判断是否在删除的后当前组分为空了  通过判断componentNameText这个输入框是否为空
				if("".equals(componentNameText.getText())){//为空就不用处理的
					componentExit=true;//既按照存在处理
				}
				
				if (!componentExit) {// 如果不存在就提示要保存
					MessageBox.post("请保存当前组分信息", "", MessageBox.INFORMATION);
					return;
				}
				//当前组分信息已经在集合中也在配方表格中了 可以让用户选择并展示了
				int selectComponentIndex = formulatorTable.getSelectedRow();//配方表中选中的组分的下标
				if(selectComponentIndex==-1){//没选
					MessageBox.post("请选择展示的组分","",MessageBox.INFORMATION);
					return;
				}
				
				currentComponentBean = mComponenetBeansList.get(selectComponentIndex);
				currentComponentBom = mComponentBomsList.get(selectComponentIndex);
				
				componentNameText.setText(currentComponentBean.component.objectName);//初始化组分表 componentTable
				supplementEdt.setText(currentComponentBean.complementContent);//设置组分补足的编辑框
				
				DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
				int rowCount = model.getRowCount();
				for(int i=0;i<rowCount;i++){
					model.removeRow(0);//清空组分表
				}
				for(int i=0;i<currentComponentBean.childBeanList.size();i++){
					MaterialBean materialBean = currentComponentBean.childBeanList.get(i);
					model.addRow(new String[]{materialBean.objectName,materialBean.U8_inventory,materialBean.bl_quantity,materialBean.u8Uom,materialBean.minMaterialType});//初始化选中的组分
				}
				
				refreshCurrentComponentTable();
			}
		});
		
		
		//新建一个组分对象
		newComponentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean componentExit = false;
				// 首先判断单当前组分是否已经保存的说
				for (ComponenetBean componenetBean : mComponenetBeansList) {
					String componentName = componenetBean.component.objectName;
					String currentComponentName = componentNameText.getText();
					if (currentComponentName.equals(componentName)) {// 找到了重名组分
						componentExit = true;
						break;
					}
				}
				
				//要最先判断是否在删除的后当前组分为空了  通过判断componentNameText这个输入框是否为空
				if("".equals(componentNameText.getText())){//为空就不用处理的
					componentExit=true;//既按照存在处理
				}
				
				if (!componentExit) {// 如果不存在就提示要保存
					MessageBox.post("请保存当前组分信息", "", MessageBox.INFORMATION);
					return;
				}

				String result = JOptionPane.showInputDialog("请输入组分名称：");
				if (result == null) {// 说明是取消
					return;
				}
				if ("".equals(result)) {// 为空说明是没有输入字符点击确定
					MessageBox.post("请输入组分的名称", "", MessageBox.INFORMATION);
					return;
				}
				// 组分是存在了 还有判断输入的组分的名称是或否已经存在
				componentExit = false;
				for (ComponenetBean componenetBean : mComponenetBeansList) {
					String componentName = componenetBean.component.objectName;
					if (result.equals(componentName)) {// 找到了重名组分
						componentExit = true;
						break;
					}
				}
				if (componentExit) {// 组分已经存在
					MessageBox.post("组分已经存在请重新输入！", "", MessageBox.INFORMATION);
					return;
				} else {

					// 初始化一个新的组分
					currentComponentBean = new ComponenetBean();
					currentComponentBean.component = new MaterialBean();
					currentComponentBean.component.objectName = result;
					currentComponentBean.childBeanList = new ArrayList<>();

					// 创建一个原料对象作为组分 并创建BOM
					TCComponentItem materialItem = ItemUtil.createtItem("U8_Material", result, "");
					TCComponentItemRevision materialRevision = null;
					try {
						AnnotationFactory.setObjectInTC(new MaterialBean(), materialItem);//添加一个属性
						materialRevision = materialItem.getLatestItemRevision();
					} catch (TCException | InstantiationException | IllegalAccessException e1) {
						e1.printStackTrace();
					}
					TCComponentBOMLine topBomLine = BomUtil.setBOMViewForItemRev(materialRevision);// 新创建的对象肯定是空的

					currentComponentBom = new ComponentBom();
					currentComponentBom.componentBOMLine = topBomLine;
					currentComponentBom.childBomList = new ArrayList<>();

					componentNameText.setText(result);// 界面上组分名字的显示
					supplementEdt.setText("");//补足编辑框置为空
					// 清空当前组分表中的数据
					DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
					int rowCount = model.getRowCount();
					for (int i = 0; i < rowCount; i++) {
						model.removeRow(0);//
					}

				}
			}
		});
				
		
		//从配方中删除一个组分对象
		deleteComponentBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				//选中的组分是否已经在展示了
				DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
				int selectedRow = formulatorTable.getSelectedRow();
				if(selectedRow==-1){//没选
					MessageBox.post("请选择要删除的组分","",MessageBox.INFORMATION);
					return;
				}
				
				//如果正在展示则 直接将组分表格置为空 还要从配方表格和list中删除
				ComponenetBean selectComponent = mComponenetBeansList.get(selectedRow);//选中的组分
				if(selectComponent.component.objectName.equals(componentNameText.getText())){
					//正在展示
					componentNameText.setText("");
					model = (DefaultTableModel) componentTable.getModel();
					int rowCount = model.getRowCount();
					for(int i=0;i<rowCount;i++){
						model.removeRow(0);//清空组分表格信息
					}
					
				}
				//从配方表格和list中删除
				model = (DefaultTableModel) formulatorTable.getModel();
				model.removeRow(selectedRow);
				mComponenetBeansList.remove(selectedRow);
				mComponentBomsList.remove(selectedRow);
				
			}
		});
	
		//补足按钮
		supplementBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String type = currentComponentBean.complementType;
				String content = currentComponentBean.complementContent;
				ComplementFrame complementFrame = new ComplementFrame(type, content, new AbstractCallBack() {
					@Override//设置补足的信息的
					public void setCompelemnet(String complementType, String complementContent) {
						super.setCompelemnet(complementType, complementContent);
						if(StringsUtil.isEmpty(complementType)&&StringsUtil.isEmpty(complementContent)){//类型不能为空
							MessageBox.post("补足问题数据有问题请重新填写","",MessageBox.INFORMATION);
							return;
						}
						currentComponentBean.complementContent = complementContent;
						currentComponentBean.complementType = complementType;
						supplementEdt.setText(complementContent);
						currentComponentBean.component.componentType = complementType;
						currentComponentBean.component.componentValue = complementContent;
						//根据补足信息计算新的数据
						refreshCurrentComponentTable();
					
					}
				});
				
				complementFrame.setVisible(true);
			}
		});
	
		
		//取消按钮
		cancleBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				if(progressBarDialog.isLive()){
					progressBarDialog.stop();
				}
				isShow = false;
				dispose();
				return;
			}
			
		});
	
	
		//标签计算  计算前先判断currentComponent是否已经保存在集合中了
		labelComputeBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						if(!currentComponentExitInTable()){//如果不存在的话就要进行弹框提示
							MessageBox.post("请保存当前展示的组分信息","",MessageBox.INFORMATION);
							return;
						}
						
						
						//替换BOM
						resetNutririonIndexBom();
						
						isNutritionFlag = true;
						clearComponentColunms();
						//计算配方中的所有组分的营养指标
						for(int i=0;i<mComponenetBeansList.size();i++){
							ComponenetBean componenetBean = mComponenetBeansList.get(i);
							ComponentBom componentBom = mComponentBomsList.get(i);
							try {
								lableCompute(componenetBean, componentBom);//计算一个组分的信息
								
								updateFormulatorTable(componenetBean,componentBom);//更新一个组分的信息 
								
							} catch (TCException e) {
								e.printStackTrace();
							}
						}
						
						
						//对投料量的总和进行计算
						Double sumInventory = 0d;
						DefaultTableModel formulatorModel = (DefaultTableModel) formulatorTable.getModel();
						int rowCount = formulatorTable.getRowCount();
						for(int i=0;i<rowCount;i++){
							String componentName = formulatorModel.getValueAt(i, 0).toString();
							for(ComponenetBean componenetBean : mComponenetBeansList){
								if(componentName.equals(componenetBean.component.objectName)){
									//找到对应的组分
									String inventoty = formulatorModel.getValueAt(i, 1).toString()==null ? "":formulatorModel.getValueAt(i, 1).toString();
									componenetBean.component.U8_inventory = inventoty;
									sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
								}
							}
						}
						//根据新的表格中的值进行计算  ---组分中的值都是已经更新过的
						componentSumText0.setText(sumInventory+"");
						
						HashMap<String, String> indexSumMap = computeSumFormulatorTable();//将配方中指标项目和计算出来
						
						setComponentSumText(indexSumMap);//将计算出来的指标和显示在界面上
						progressBarDialog.stop();
					}
				}));
				progressBarDialog.start();
				
				
			}
		});
	
		//创建配方监听事件
		createBtn.addActionListener(new ActionListener() {
//			topBomLine.add(materialItemRev.getItem(), materialItemRev, null, false);
			@Override
			public void actionPerformed(ActionEvent actionevent) {
				
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						
						//判断当前的组分是否已经保存
						if(!currentComponentExitInTable()){
							MessageBox.post("请保存正在编辑的组分","",MessageBox.INFORMATION);
							return;
						}
						
						//将配方版本下面的bom清空
						TCComponentBOMLine topBomLine = BomUtil.getTopBomLine(formulatorRev, "视图");
						if(topBomLine==null){
							topBomLine = BomUtil.setBOMViewForItemRev(formulatorRev);
						}
						try {
							AIFComponentContext[] children = topBomLine.getChildren();
							for(AIFComponentContext context : children){
								TCComponentBOMLine childBom = (TCComponentBOMLine) context.getComponent();
								childBom.cut();
							}
						} catch (TCException e) {
							e.printStackTrace();
						}
						
						//先创建组分的bom视图
						for(int i=0;i<mComponenetBeansList.size();i++){
							try {
								ComponenetBean componenetBean = mComponenetBeansList.get(i);
								ComponentBom componentBom = mComponentBomsList.get(i);
								TCComponentItemRevision componentRev = componentBom.componentBOMLine.getItemRevision();
								
								//添加成功后 将在视图展示的BOMLine赋值给组分的对象的说
								TCComponentBOMLine componentBOMLine = topBomLine.add(componentRev.getItem(), componentRev, null, false);
								AnnotationFactory.setObjectInTC(componenetBean.component, componentBOMLine);//对组分BOM进行赋值
								componentBom.componentBOMLine = componentBOMLine;
								
								//清空这个组分BOMLine下的所有原料然后在再将组分实体类中孩子原料都给赋值上去
								AIFComponentContext[] children = componentBOMLine.getChildren();
								for(AIFComponentContext context : children){
									TCComponentBOMLine bomLine = (TCComponentBOMLine) context.getComponent();
									bomLine.cut();
								}
								int size = componentBom.childBomList.size();
								for(int j=0;j<size;j++){
									//个数加倍了
									TCComponentItemRevision materialRev = componentBom.childBomList.get(j).getItemRevision();
									TCComponentBOMLine materialBom = componentBOMLine.add(materialRev.getItem(), materialRev, null, false);
									AnnotationFactory.setObjectInTC(componenetBean.childBeanList.get(j), materialBom);//对原料BOM赋值
									componentBom.childBomList.add(materialBom);
								}
								size = componentBom.childBomList.size();
								for(int j=0;j<size/2;j++){//上面翻倍所以在这里要删除多余的部分
									componentBom.childBomList.remove(0);//顺序的删除一半的个数
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						//保存bom
						try {
							topBomLine.refresh();
							TCComponentBOMWindow bomWindow = topBomLine.getCachedWindow();
							bomWindow.refresh();
							bomWindow.save();
							bomWindow.close();
							
							formulatorRev.setProperty("object_desc", "PF");
							
							//在配方的版本下的BOM版本下的描述属性中写一个属性（法规）
							TCComponentBOMViewRevision bomRevByItemRev = BomUtil.getBOMRevByItemRev(formulatorRev);
							bomRevByItemRev.setProperty("object_desc", Const.BomViewType.FORMULATOR);
						} catch (TCException e) {
							e.printStackTrace();
						}
						MessageBox.post("OK","",MessageBox.INFORMATION);
						
						progressBarDialog.stop();
					}
				}));
				progressBarDialog.start();
				
			}
			
		});
	
		//理化指标  组分中的原料BOM需要改为
		//所以 需要组分列表中的原料BOM 而Bean不需要改变
		physicalAndChemicalBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				progressBarDialog = new ProgressBarDialog(new Thread(new Runnable() {
					@Override
					public void run() {
						
						if(currentComponentBom==null){
							return ;
						}
						
						//替换BOM
						resetPhysicalAndChemicalIndexBom();
					
						
						isNutritionFlag = false;//转换为理化指标
						
						clearComponentColunms();//清除指标行
						
						//计算
						try {
							for(int i=0;i<mComponenetBeansList.size();i++){
								ComponenetBean componenetBean = mComponenetBeansList.get(i);
								ComponentBom componentBom = mComponentBomsList.get(i);
								lableCompute(componenetBean, componentBom);
								updateFormulatorTable(componenetBean,componentBom);
							}
						} catch (TCException e1) {
							e1.printStackTrace();
						}
						
						setComponentSumTextAndLabelInvisiable();//理化指标不需要和的结果
						progressBarDialog.stop();
						
					}
				}));
				progressBarDialog.start();
				
				
			}
			
		});
		
		
	}
	
	
		
		
	
		/**
		 * 初始化 组分的表格信息
		 */
		private void initComponentTable() {
			if(currentComponentBean==null){
				return;//没有结构 不做处理
			}
			
			//组分的名字
			String componentName = currentComponentBean.component.objectName;
			componentNameText.setText(componentName);
			
			//组分的详细信息
			DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
			for(MaterialBean materialBean : currentComponentBean.childBeanList){
				model.addRow(new String[]{materialBean.objectName,materialBean.U8_inventory,materialBean.bl_quantity,materialBean.u8Uom,materialBean.minMaterialType});
			}
			 
			//设置补足的数字
			supplementEdt.setText(currentComponentBean.complementContent==null?"":currentComponentBean.complementContent);
			
		}
		
		
		/**
		 * 初始化  配方表的信息
		 */
		private void initFormulaTable(){
			if(mComponenetBeansList==null||mComponenetBeansList.size()==0){
				return;//没有结构 不做处理
			}
			
			
			for(ComponenetBean bean:mComponenetBeansList){
				formulatorTableModel.addRow(new String[]{bean.component.objectName,bean.component.U8_inventory,bean.component.bl_quantity});
				bean.complementType = bean.component.componentType;
				bean.complementContent = bean.component.componentValue;
			}
			
			
			//计算总的投料量 然后计算配比
			Double sumInventory = 0d;
			int rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//找到对应的组分
						String inventoty = formulatorTableModel.getValueAt(i, 1).toString()==null ? "":formulatorTableModel.getValueAt(i, 1).toString();
						componenetBeanTemp.component.U8_inventory = inventoty;
						sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
					}
				}
			}
			
			//计算更新配比
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//找到对应的组分
						if(sumInventory==0){
							componenetBeanTemp.component.bl_quantity = "";
						}else {
							componenetBeanTemp.component.bl_quantity =  StringsUtil.convertStr2Double(componenetBeanTemp.component.U8_inventory)/sumInventory*100 +"";
						}
						
						formulatorTableModel.setValueAt(DoubleUtil.formatNumber(componenetBeanTemp.component.bl_quantity),i , 2);
						
					}
				}
			}
			
		}

		//存儲配方结构的实体类的信息 ----一个组分的 实体Bean信息 
		public static class ComponenetBean {
			public MaterialBean component;//组分自己
			public List<MaterialBean> childBeanList;//所有的原料 不区分小料和单层原料的区别的说
 			public List<MinMaterialBean> childMinBeanList;//小料数组  暂时没有使用到 作为备用
 			public String complementType;//补足的类型
 			public String complementContent;//补足是多少 
 			public HashMap<String, String> indexValueMap = new HashMap<String, String>();//组分的原料中的指标的并集  /100g为单位的
			
		}

		// 存放配方中的BOM结构=====一个组分的BOm的结构信息  直接把小料和原料当做同种结构处理了
		public static class ComponentBom {
			public TCComponentBOMLine componentBOMLine;
			public List<TCComponentBOMLine> childBomList;
		}
	
		
		
		
		/**
		 * 计算了一个分组
		 * @param componenetBean  一个组分
		 * @param componentBom	一个组分
		 * @return  直接将指标的map放到组分的实体类里面去
		 * @throws TCException
		 */
		public void lableCompute(ComponenetBean componenetBean, ComponentBom componentBom) throws TCException{
			//计算当前组分在其类型的补足之下的各种营养素的含量/100g
			
			//获取当前组分中的所有原料中的指标项的合计
			HashSet<String> indexNameSet = new HashSet<>(); 
			for(TCComponentBOMLine bomLine : componentBom.childBomList){
				if(bomLine==null) continue;
				AIFComponentContext[] children = bomLine.getChildren();
				for(AIFComponentContext context : children){
					TCComponentBOMLine indexBomLine = (TCComponentBOMLine) context.getComponent();
					String indexName = indexBomLine.getItem().getProperty("object_name");//指标的名称
					indexNameSet.add(indexName);
				} 
			}
			
			
			//对于一个指标项目   要根据所有的原料中的相同名字的指标项目根据投料量的累加
			if(StringsUtil.isEmpty(componenetBean.complementType)
					||StringsUtil.isEmpty(componenetBean.complementContent)){//如果组分的类型或者补足字段为空则跳出提示
				MessageBox.post("请完善-"+componenetBean.component.objectName+"-的补足类型和数量的信息","",MessageBox.INFORMATION);
				return ;
			}
			
			
			HashMap<String,String> indexMap = new HashMap<>();
			//先计算出来这个组分中的投的原料的总的值
			Double materialSum = 0d;
			for(int i=0;i<componentBom.childBomList.size();i++){
				TCComponentBOMLine materialBomLine = componentBom.childBomList.get(i);
				MaterialBean materialBean = componenetBean.childBeanList.get(i);
				
				//如果是蛋卷之类的要计算上干物质的消耗的说
				Double inventoryDb = StringsUtil.convertStr2Double(materialBean.U8_inventory);
				Double percentDb = 0d;//找到干物质的含量
				if(componenetBean.complementType.equals("二投蛋卷")
						||componenetBean.complementType.equals("二投碎料")){//需要计算干物质的说
					AIFComponentContext[] children = materialBomLine.getChildren();
					for(AIFComponentContext context : children){
						TCComponentBOMLine indexBomLine = (TCComponentBOMLine) context.getComponent();
						String name = indexBomLine.getProperty("object_name");
						if("干物质".equals(name)){
							percentDb =  StringsUtil.convertStr2Double(indexBomLine.getProperty("bl_quantity"))/100;
							break;
						}
					}
					
					if(percentDb==0){//如果没有干物质的比例的话就按1处理了
						percentDb = 1d;
					}
					materialSum = materialSum +inventoryDb*percentDb;
					
				}else{
					materialSum = materialSum + inventoryDb;
				}
				
			}
			//计算各个指标的值
			Iterator<String> indexNameIterator = indexNameSet.iterator();
			while(indexNameIterator.hasNext()){
				String indexName = indexNameIterator.next();
				Double indexSum = 0d;
				for(int i=0;i<componentBom.childBomList.size();i++){
					TCComponentBOMLine materialBom = componentBom.childBomList.get(i);
					MaterialBean materialBean = componenetBean.childBeanList.get(i);
					
					String materialInventory = materialBean.U8_inventory;//组分下的原料的投料量
					if(materialBom==null) continue;
					AIFComponentContext[] children = materialBom.getChildren();
					for(AIFComponentContext context : children){
						TCComponentBOMLine indexBom = (TCComponentBOMLine) context.getComponent();
						String name = indexBom.getItem().getProperty("object_name");
						String value = indexBom.getProperty("bl_quantity");//当前指标的每百克的值
						if(name.equals(indexName)){//找到了对应的营养指标的Bom
							//根据当前组分的补足类型以及补足的量计算出来该指标的值
							Double valueDb = StringsUtil.convertStr2Double(value);
							Double inventoryDb = StringsUtil.convertStr2Double(materialInventory);
							if("*".equals(componenetBean.complementContent)){//是*表示投多少是多少
								indexSum = indexSum + (valueDb*inventoryDb/100)/materialSum;
							}else{
								Double contentDb = StringsUtil.convertStr2Double(componenetBean.complementContent);//补足的量
								indexSum = indexSum + (valueDb*inventoryDb/100)/contentDb;
							}
						}
					}
				}
			
				
				//最后计算是否是补水或者是干物质    
				boolean flag1 = false;
				boolean flag2 = false;
				boolean flag3 = false;
				if(componenetBean.complementType.equals("料液")|componenetBean.complementType.equals("果酱"))  flag1 = true;
				if(!componenetBean.complementType.equals("*")) flag2 = true;
				if(indexName.equals("钠")) flag3 = true;
				
				if(flag1&&flag2&&flag3){//果酱或者料液类型的组分，所以补水的话要加钠
					Double valueDb = 20d;
					Double contentDb = StringsUtil.convertStr2Double(componenetBean.complementContent);//补足的量
					Double inventoryDb = contentDb - materialSum;//总的量-原料的添加量=补水的量
					Double sumInventory = getComponentInventory(componenetBean, componentBom);//该组分的补足或者所投的原料的总值
					indexSum = indexSum + (valueDb*inventoryDb/100)/sumInventory;
				}//对于其他投多少就是多少的组分而言的话不需要单独计算钠
				
				//将在这个组分中 累计计算出来的indexName的指标的含量放到map里面
				indexMap.put(indexName, indexSum+"");//营养指标的名称     营养指标在这个组分中的比例
			}
			
			componenetBean.indexValueMap = indexMap;
		}
		
		
		/**
		 * 计算不同的补足类型的组分中原料
		 * @param componenetBean  一个组分
		 * @param componentBom	一个组分
		 * @return  直接将指标的map放到组分的实体类里面去
		 * @throws TCException
		 */
		public Double getComponentInventory(ComponenetBean componenetBean, ComponentBom componentBom) throws TCException{
			//计算这个组分下面的原料的投料量
			
			//先计算出来这个组分中的投的原料的总的值
			Double materialSum = 0d;
			for(int i=0;i<componentBom.childBomList.size();i++){
				TCComponentBOMLine materialBomLine = componentBom.childBomList.get(i);
				MaterialBean materialBean = componenetBean.childBeanList.get(i);
				
				//如果是蛋卷之类的要计算上干物质的消耗的说
				Double inventoryDb = StringsUtil.convertStr2Double(materialBean.U8_inventory);
				Double percentDb = 0d;//找到干物质的含量
				if(componenetBean.complementType.equals("二投蛋卷")
						||componenetBean.complementType.equals("二投碎料")){//需要计算干物质的说
					AIFComponentContext[] children = materialBomLine.getChildren();
					for(AIFComponentContext context : children){
						TCComponentBOMLine indexBomLine = (TCComponentBOMLine) context.getComponent();
						String name = indexBomLine.getProperty("object_name");
						if("干物质".equals(name)){
							percentDb =  StringsUtil.convertStr2Double(indexBomLine.getProperty("bl_quantity"))/100;
							break;
						}
					}
					
					if(percentDb==0){//如果没有干物质的比例的话就按1处理了
						percentDb = 1d;
					}
					materialSum = materialSum +inventoryDb*percentDb;
					
				}else{
					materialSum = materialSum + inventoryDb;
				}
				
			}
			
			if("*".equals(componenetBean.complementContent)){//是*表示投多少是多少
				return materialSum;
			}else{
				Double contentDb = StringsUtil.convertStr2Double(componenetBean.complementContent);//补足的量
				return contentDb;
			}
		}
		
		/**
		 * 
		 * 计算当前配方中的所有组分的总的投料量
		 * @return
		 */
		public Double getComponentSumInventory(){
			Double sumInventory = 0d;
			for(int i=0;i<mComponenetBeansList.size();i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				Double inventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
				sumInventory += inventory;
			}
			
			return sumInventory;
			
		}
		
		
		/**
		 * 判断当前的在展示的组分是否已经保存在了集合中了
		 * @return
		 */
		public boolean currentComponentExitInTable(){
			boolean flag = false;
			if(StringsUtil.isEmpty(componentNameText.getText().toString())){//组分的名字为空的话直接认为为真然后跳过
				return true;
			}
			for(ComponenetBean componenetBean: mComponenetBeansList){
				if(componenetBean.component.objectName.equals(currentComponentBean.component.objectName)){
					flag = true;
				}
				
			}
			return flag;
			
		}


		/**
		 * 这个里面只是更新营养指标的项目的说啊
		 * @param componenetBean
		 * 更新一个组分  在tabel中代表的是一行信息进行更新
		 */
		public void updateFormulatorTable(ComponenetBean componenetBean,ComponentBom componentBom){
			//这里要更新的组分中已经计算出了 指标的map 每一个指标的值是 改指标在该组分中 的值  每g
			
			if(StringsUtil.isEmpty(componenetBean.component.U8_inventory)){//如果这个组分的投料量为0就不更新table表
				return;
			}
			
			if(isNutritionFlag){//如果要计算的是营养成分  标签中的值在该组分中的含量 以 每100g 
				//由于目前组分的指标Map中存储的是该指标在组分中的含量 需要转化为重量
				HashMap<String, String> indexValueMap = componenetBean.indexValueMap;
				Set<Entry<String, String>> indexEntrySet = indexValueMap.entrySet();
				for(Entry<String, String> entry : indexEntrySet){
					String indexName = entry.getKey();
					String indexValue = entry.getValue();//由于这个Value只是指标在组分中的比例 乘以投料量才是含量
					indexValue = StringsUtil.convertStr2Double(indexValue)*100+"";
					
					indexValueMap.put(indexName, DoubleUtil.formatNumber(indexValue));//更新
				} 
			}else{//计算的是理化指标  根据组分的投料量来计算该指标在组分中的总的值
				Double componentInventory = 0d;
				try {
					componentInventory= getComponentInventory(componenetBean,componentBom);
				} catch (TCException e) {
					e.printStackTrace();
				}
				
				HashMap<String, String> indexValueMap = componenetBean.indexValueMap;
				Set<Entry<String, String>> indexEntrySet = indexValueMap.entrySet();
				for(Entry<String, String> entry : indexEntrySet){
					String indexName = entry.getKey();
					String indexValue = entry.getValue();//由于这个Value只是指标在组分中的比例 乘以投料量才是含量
					indexValue = StringsUtil.convertStr2Double(indexValue)*StringsUtil.convertStr2Double(componenetBean.component.U8_inventory)+"";
					
					indexValueMap.put(indexName, DoubleUtil.formatNumber(indexValue));//更新
				} 
			}
			
			
			
			DefaultTableModel modle = (DefaultTableModel) formulatorTable.getModel();
			int rowCount = modle.getRowCount();
			int indexRow = -1;//用来标记要更新的组分在配方表中的行下标
			for(int i=0;i<rowCount;i++){
				String componentName  = modle.getValueAt(i, 0).toString();//
				if(componentName.equals(componenetBean.component.objectName)){//找到对应的组分
					indexRow = i;
					break;
				}
			}
			if(indexRow==-1){
				MessageBox.post("","",MessageBox.INFORMATION);//基本不存在这种情况的说
			}
			if(componenetBean.indexValueMap==null){
				return ;
			}
			HashMap<String, String> hashMap = componenetBean.indexValueMap;
			Set<Entry<String, String>> entrySet = hashMap.entrySet();
			for(Entry<String, String> entry : entrySet){
				String indexName = entry.getKey();
				String indexValue = entry.getValue();//这个Value经过上面的转换已经变换为含量了
				//查找这个作为列名的项是否在table中
				int indexColumn = getnIdexNameExitInTableColumns(indexName);
				
				modle.setValueAt(indexValue, indexRow, indexColumn);//更新值
			}
			
			
			
		}
		
		/**
		 * @param indexName 指标的名称
		 * @return  如果指标的名称存在的话就 就返回这个列的下标记,如果不存在的话就返回-1
		 */
		public int getnIdexNameExitInTableColumns(String indexName){
			DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
			int columnCount = model.getColumnCount();
			int k = -1;
			for(int i=0;i<columnCount;i++){
				String columnName = model.getColumnName(i);
				if(columnName.equals(indexName)){
					k = i;
					return k;
				}
			}
			if(k==-1){
				model.addColumn(indexName);
				k = columnCount;//添加上一个之后刚好是在最末尾
			}
			return k;
		}
		
		
		/**
		 * 对当前显示的组分进行计算求和
		 */
		public Double computeSumCurrentComponent(){
			Double sumInventory = 0d;
			if(currentComponentBean==null){
				return sumInventory;
			}
			for(MaterialBean materialBean : currentComponentBean.childBeanList){
				sumInventory = sumInventory + StringsUtil.convertStr2Double(materialBean.U8_inventory);
			}
			if("1000".equals(currentComponentBean.complementContent)){
				materialSum0.setText("1000");
			}else if("100".equals(currentComponentBean.complementContent)){
				materialSum0.setText("100");
			}else if("*".equals(currentComponentBean.complementContent)){
				materialSum0.setText(""+sumInventory);
			}
			
			return sumInventory;
			
		}
		
		
		/**
		 * 对配方表中的各个组分进行求和计算
		 * 返回值用来进行写到excel中来进行使用的说
		 */
		public HashMap<String, String> computeSumFormulatorTable(){
			HashMap<String, String> sumIndexValueMap = new HashMap<>();
			HashSet<String> indexNameSet = new HashSet<>();//存储所有的营养指标项目的名称的并集
			for(ComponenetBean bean : mComponenetBeansList){
				if(bean.indexValueMap==null){
					continue;
				}
				Iterator<String> iterator = bean.indexValueMap.keySet().iterator();
				while(iterator.hasNext()){
					String name = iterator.next();
					indexNameSet.add(name);
				}
				break;
			}
			
			if(isNutritionFlag){//要计算的是营养成分
				Double sumInventory = getComponentSumInventory();//求出来投料量的总的和
				Iterator<String> iterator = indexNameSet.iterator();
				while(iterator.hasNext()){
					String indexName = iterator.next();
					Double sumIndexValue = 0d;
					for(int i=0;i<mComponenetBeansList.size();i++){
						ComponenetBean componenetBean = mComponenetBeansList.get(i);
						Double inventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
						
						HashMap<String, String> indexMap = componenetBean.indexValueMap;
						Set<Entry<String, String>> entrySet = indexMap.entrySet();
						for(Entry<String,String> entry : entrySet){
							String name = entry.getKey();
							String value = entry.getValue();
							if(indexName.equals(name)){//找到了对应的指标项目
								sumIndexValue = sumIndexValue + StringsUtil.convertStr2Double(value)*inventory/100;
							}
						}
					}
					
					sumIndexValue = sumIndexValue/sumInventory*100; //sumIndex遍历之后就累加为 这种组分的配置下该指标的含量   最后要转换为每100g
					sumIndexValueMap.put(indexName, DoubleUtil.formatNumber(sumIndexValue));//一个指标计算完成
				}
				
			}else{//要计算的是理化指标   同一种的指标项的值可以直接累加
				Double sumInventory = 0d;//求出来投料量的总的和
				Iterator<String> iterator = indexNameSet.iterator();
				while(iterator.hasNext()){
					String indexName = iterator.next();
					Double sumIndexValue = 0d;
					for(int i=0;i<mComponenetBeansList.size();i++){
						ComponenetBean componenetBean = mComponenetBeansList.get(i);
						Double inventory = StringsUtil.convertStr2Double(componenetBean.component.U8_inventory);
						
						HashMap<String, String> indexMap = componenetBean.indexValueMap;
						Set<Entry<String, String>> entrySet = indexMap.entrySet();
						for(Entry<String,String> entry : entrySet){
							String name = entry.getKey();
							String value = entry.getValue();
							if(indexName.equals(name)){//找到了对应的指标项目
								sumIndexValue = sumIndexValue + StringsUtil.convertStr2Double(value);
							}
						}
					}
					sumIndexValueMap.put(indexName, DoubleUtil.formatNumber(sumIndexValue));//一个指标计算完成
				}
			}
			
			
			return sumIndexValueMap;
		}
		
		/**
		 * 将配方table中的营养指标的和填写到text中去
		 * @param indexMap	对配方表中得到的营养指标总和的map
		 */
		public void setComponentSumText(HashMap<String, String> sumIndexMap){
			setComponentSumTextAndLabelInvisiable();//在最开始的时候要把所有的标签项都给隐藏起来 因为不知道标签的顺序或者个数有么有增加的说
			//先将配方table中的列名字获取到 从 3开始就是营养指标
			List<String> indexNameList = new ArrayList<>();
			DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
			int columnCount = model.getColumnCount();
			for(int i=4;i<columnCount;i++){
				String indexName = model.getColumnName(i);
				indexNameList.add(indexName);
			}
			
			
			
			//按照这个有顺序的集合去从map找匹配的值
			for(int i=0;i<indexNameList.size();i++){
				String indexName = indexNameList.get(i);
				Set<Entry<String, String>> entrySet = sumIndexMap.entrySet();
				for(Entry<String, String> entry : entrySet){
					String key = entry.getKey();
					String value = entry.getValue();
					if(indexName.equals(key)){//找到了指标对应的名字
						setComponentSumKeyAndValue(key,value,i+3);//将这个值写到对应的text中 并让text和label显示
					}
				}
			}
		}
		
		
		/**
		 * 将配方中的营养标签对应的text和label都隐藏起来 
		 * 从 3开始-12
		 */
		public void setComponentSumTextAndLabelInvisiable(){
			componentSumLabel3.setVisible(false);
			componentSumLabel4.setVisible(false);
			componentSumLabel5.setVisible(false);
			componentSumLabel6.setVisible(false);
			componentSumLabel7.setVisible(false);
			componentSumLabel8.setVisible(false);
			componentSumLabel9.setVisible(false);
			componentSumLabel10.setVisible(false);
			componentSumLabel11.setVisible(false);
			componentSumLabel12.setVisible(false);
			
			componentSumText3.setVisible(false);
			componentSumText4.setVisible(false);
			componentSumText5.setVisible(false);
			componentSumText6.setVisible(false);
			componentSumText7.setVisible(false);
			componentSumText8.setVisible(false);
			componentSumText9.setVisible(false);
			componentSumText10.setVisible(false);
			componentSumText11.setVisible(false);
			componentSumText12.setVisible(false);
		}


		/**
		 * @param indexName 营养标签的名称
		 * @param indexValue	营养标签的值
		 * @param number	所对应的label和edittext的标记号码
		 */
		public void setComponentSumKeyAndValue(String indexName,String indexValue,int number){
			switch (number) {
			case 3:
				componentSumLabel3.setVisible(true);
				componentSumText3.setVisible(true);
				componentSumLabel3.setText(indexName);
				componentSumText3.setText(indexValue);
				break;
			case 4:
				componentSumLabel4.setVisible(true);
				componentSumText4.setVisible(true);
				componentSumLabel4.setText(indexName);
				componentSumText4.setText(indexValue);
				break;
			case 5:
				componentSumLabel5.setVisible(true);
				componentSumText5.setVisible(true);
				componentSumLabel5.setText(indexName);
				componentSumText5.setText(indexValue);
				break;
			case 6:
				componentSumLabel6.setVisible(true);
				componentSumText6.setVisible(true);
				componentSumLabel6.setText(indexName);
				componentSumText6.setText(indexValue);
				break;
			case 7:
				componentSumLabel7.setVisible(true);
				componentSumText7.setVisible(true);
				componentSumLabel7.setText(indexName);
				componentSumText7.setText(indexValue);
				break;
			case 8:
				componentSumLabel8.setVisible(true);
				componentSumText8.setVisible(true);
				componentSumLabel8.setText(indexName);
				componentSumText8.setText(indexValue);
				break;
			case 9:
				componentSumLabel9.setVisible(true);
				componentSumText9.setVisible(true);
				componentSumLabel9.setText(indexName);
				componentSumText9.setText(indexValue);
				break;
			case 10:
				componentSumLabel10.setVisible(true);
				componentSumText10.setVisible(true);
				componentSumLabel10.setText(indexName);
				componentSumText10.setText(indexValue);
				break;
			case 11:
				componentSumLabel11.setVisible(true);
				componentSumText11.setVisible(true);
				componentSumLabel11.setText(indexName);
				componentSumText11.setText(indexValue);
				break;
			case 12:
				componentSumLabel12.setVisible(true);
				componentSumText12.setVisible(true);
				componentSumLabel12.setText(indexName);
				componentSumText12.setText(indexValue);
				break;
			default:
				break;
			}
		}
		
		
		/**
		 * 清空下配方中组分的指标列
		 */
		public void clearComponentColunms(){
			DefaultTableModel model = (DefaultTableModel) formulatorTable.getModel();
			model.setColumnCount(4);
		
		}
		
		
		/**
		 * 更新当前组分的代码
		 */
		public void refreshCurrentComponentTable(){
			
			int rowCount = componentTable.getRowCount();
			DefaultTableModel model = (DefaultTableModel) componentTable.getModel();
			
			//将第一列的值和第四列的值回写进原料的bean中 
			for(int i=0;i<rowCount;i++){
				MaterialBean materialBean  = currentComponentBean.childBeanList.get(i);
				String inventory =model.getValueAt(i, 1)==null ? "" :model.getValueAt(i, 1).toString();
				String minMaterialType = model.getValueAt(i, 4) == null ? "":model.getValueAt(i, 4).toString();
				materialBean.U8_inventory = inventory;
				materialBean.minMaterialType = minMaterialType;
			}
			//组分中原料的投料量的综合计算
			computeSumCurrentComponent();
			Double currentComponentSum = Double.valueOf(StringsUtil.convertStr2Double(materialSum0.getText().toString()));
			
			//刷新组分中原料的比例
			for(int i=0;i<rowCount;i++){
				MaterialBean materialBean  = currentComponentBean.childBeanList.get(i);
				Double blQuantity = StringsUtil.convertStr2Double(materialBean.U8_inventory) / currentComponentSum * 100;
				materialBean.bl_quantity = DoubleUtil.formatNumber(blQuantity);
				model.setValueAt(materialBean.objectName, i, 0);//名称
				model.setValueAt(materialBean.bl_quantity,i, 2);//比例
				model.setValueAt(materialBean.u8Uom,i, 3);//比例
			}
			
			
			//刷新一下配方表中的信息
			//计算配方中的所有组分的营养指标
			for(int i=0;i<mComponenetBeansList.size();i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				ComponentBom componentBom = mComponentBomsList.get(i);
				try {
					lableCompute(componenetBean, componentBom);//计算一个组分的信息
					updateFormulatorTable(componenetBean,componentBom);//更新一个组分的信息 
				} catch (TCException e1) {
					e1.printStackTrace();
				}
			}
			
			//对投料量的总和进行计算
			Double sumInventory = 0d;
			DefaultTableModel formulatorModel = (DefaultTableModel) formulatorTable.getModel();
			rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				//找到对应的组分
				String inventoty = formulatorModel.getValueAt(i, 1).toString()==null ? "":formulatorModel.getValueAt(i, 1).toString();
				componenetBean.component.U8_inventory = inventoty;
				sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
			}
			//根据新的表格中的值进行计算  ---组分中的值都是已经更新过的
			componentSumText0.setText(sumInventory+"");
			
			//组分变化 更新配方表
			HashMap<String, String> indexSumMap = computeSumFormulatorTable();//将配方中指标项目和计算出来
			setComponentSumText(indexSumMap);//将计算出来的指标和显示在界面上
			
		}
		
		
		/**
		 * 刷新整个配方表
		 */
		public void refreshFormualtorTable(){
			//将第一列的值更新进bean中
			Double sumInventory = 0d;
			int rowCount = formulatorTable.getRowCount();
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//找到对应的组分
						String inventoty = formulatorTableModel.getValueAt(i, 1).toString()==null ? "":formulatorTableModel.getValueAt(i, 1).toString();
						componenetBeanTemp.component.U8_inventory = inventoty;
						sumInventory = sumInventory + StringsUtil.convertStr2Double(inventoty);
					}
				}
			}
			
			
			
			////计算组分的比例
			for(int i=0;i<rowCount;i++){
				String componentName = formulatorTableModel.getValueAt(i, 0).toString();
				for(ComponenetBean componenetBeanTemp : mComponenetBeansList){
					if(componentName.equals(componenetBeanTemp.component.objectName)){
						//找到对应的组分
						if(sumInventory==0){
							componenetBeanTemp.component.bl_quantity = "";
						}else {
							componenetBeanTemp.component.bl_quantity =  StringsUtil.convertStr2Double(componenetBeanTemp.component.U8_inventory)/sumInventory*100 +"";
						}
						
						formulatorTableModel.setValueAt(DoubleUtil.formatNumber(componenetBeanTemp.component.bl_quantity),i , 2);
						
					}
				}
			}
		
			
			
			//然后计算指标的值
			for(int i=0;i<mComponenetBeansList.size();i++){
				ComponenetBean componenetBean = mComponenetBeansList.get(i);
				ComponentBom componentBom = mComponentBomsList.get(i);
				try {
					lableCompute(componenetBean, componentBom);
					updateFormulatorTable(componenetBean,componentBom);
				} catch (TCException e1) {
					e1.printStackTrace();
				}
			}
			
			
			
			//根据新的表格中的值进行计算  ---组分中的值都是已经更新过的
			componentSumText0.setText(sumInventory+"");
			
			HashMap<String, String> indexSumMap = computeSumFormulatorTable();//将配方中指标项目和计算出来
			setComponentSumText(indexSumMap);//将计算出来的指标和显示在界面上
			
		}
		
	
		/**
		 * 将BOM设置为理化指标中的BOM
		 */
		public void resetPhysicalAndChemicalIndexBom(){
			for(int i=0;i<mComponentBomsList.size();i++){//理化指标
				ComponentBom componentBom = mComponentBomsList.get(i);
				
				List<TCComponentBOMLine> childBomList = new ArrayList<>();
				
				for(TCComponentBOMLine materialBomLine : componentBom.childBomList){
					try {
						TCComponentItemRevision materialRev = materialBomLine.getItemRevision();
						TCComponentBOMLine physicalBom = BomUtil.getTopBomLine(materialRev, "视图");
						childBomList.add(physicalBom);
						
						
					} catch (TCException e1) {
						if(progressBarDialog.isLive()){
							progressBarDialog.stop();
						}
						e1.printStackTrace();
					}
				}
				componentBom.childBomList = childBomList;
			}
		}
		
		
		/**
		 * 将BOM设置为营养成分的BOM
		 */
		public void resetNutririonIndexBom(){
			for(int i=0;i<mComponentBomsList.size();i++){//理化指标
				ComponentBom componentBom = mComponentBomsList.get(i);
				
				List<TCComponentBOMLine> childBomList = new ArrayList<>();
				
				for(TCComponentBOMLine materialBomLine : componentBom.childBomList){
					try {
						TCComponentItemRevision materialRev = materialBomLine.getItemRevision();
						TCComponentBOMLine physicalBom = BomUtil.getTopBomLine(materialRev, "冷饮营养标签");
						childBomList.add(physicalBom);
						
					} catch (TCException e1) {
						if(progressBarDialog.isLive()){
							progressBarDialog.stop();
						}
						e1.printStackTrace();
					}
				}
				componentBom.childBomList = childBomList;
			}
		}
		
		
		
		
		
}

